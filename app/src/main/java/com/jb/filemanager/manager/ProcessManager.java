package com.jb.filemanager.manager;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.appBean.RunningAppBean;
import com.jb.filemanager.manager.processmanager.FetchMemoryTask;
import com.jb.filemanager.manager.processmanager.ProcessHelperUtil;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.device.Machine;
import com.jb.filemanager.util.log.TimeRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 进程管理器
 * <p>
 * 功能:
 * </p>
 * <ol>
 * <li>获取正在运行的进程列表；
 * <li>普通杀进程；
 * <li>强杀进程；
 * </ol>
 */
public class ProcessManager {

    public static final String TAG = "PCMgr";

    private static final int PER_USER_RANGE = 100000;

    private static ProcessManager sProcMgr;

    private Context mContext = null;
    private ActivityManager mActMgr;
    private HashSet<String> mLauncherAbleApps = null;
    private ExecutorService mExec = Executors.newCachedThreadPool();

    /**
     * 记录可使用的内存的百分比
     */
    private float mAvaliableMemoryPercentage = 1.0f;

    private ProcessManager(Context ctx) {
        mContext = ctx.getApplicationContext();
        mActMgr = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        mLauncherAbleApps = new HashSet<>(getLauncherableApps().keySet());
        EssentialProcessFilter.loadCoreList(mContext);
    }

    public static ProcessManager getInstance(Context context) {
        if (sProcMgr == null) {
            sProcMgr = new ProcessManager(context);
        }
        return sProcMgr;
    }

    /**
     * 获取在功能菜单出现的程序列表（能打开的应用程序）
     * intent.addCategory("android.intent.category.LAUNCHER");
     *
     * @return 程序列表，类型是 HashMap<String, ResolveInfo>
     */
    public HashMap<String, ResolveInfo> getLauncherableApps() {
        List<ResolveInfo> infos = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        try {
            infos = TheApplication.getAppContext().getPackageManager().queryIntentActivities(intent, 0);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, ResolveInfo> infosMap = new HashMap<>();
        if (infos != null) {
            for (ResolveInfo ri : infos) {
                infosMap.put(ri.activityInfo.processName, ri);
            }
        }
        return infosMap;
    }

    /**
     * 获取当前运行的程序列表
     *
     * @return 程序列表
     */
    public List<RunningAppBean> getRunningAppList() {
        return getRunningAppList(true);
    }

    /**
     * 获取当前运行的程序列表
     *
     * @param filterRecentKilled 是否过滤最近查杀的应用, true则过滤
     * @return 程序列表
     */
    public List<RunningAppBean> getRunningAppList(boolean filterRecentKilled) {
        TimeRecord recorder = new TimeRecord(TAG);
        recorder.begin();
        List<RunningAppBean> appList = new ArrayList<RunningAppBean>();
        getRunningAppListInternal(appList, filterRecentKilled);
        // 遍历出内存值
        recorder.mark("--- 开始获取内存 ---" + appList.size());
        CountDownLatch latch = new CountDownLatch(appList.size());
        for (RunningAppBean app : appList) {
            mExec.execute(new FetchMemoryTask(mContext, latch, app));
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recorder.mark("--- 获取内存结束 ---" + appList.size());
        recorder.end();
        // 去除无效数据
        Iterator<RunningAppBean> iterator = appList.iterator();
        while (iterator.hasNext()) {
            RunningAppBean next = iterator.next();
            if (next.mMemory <= 0 || next.mPackageName.equals(Const.PACKAGE_NAME)) {
                iterator.remove();
            }
        }
        return appList;
    }

    /**
     * 获取当前运行的程序列表(不包含进程占用的内存值大小)
     *
     * @return 程序列表
     */
    public List<RunningAppBean> getRunningAppListNoMemory() {
        TimeRecord recorder = new TimeRecord(TAG);
        recorder.begin();
        ArrayList<RunningAppBean> appList = new ArrayList<>();
        getRunningAppListInternal(appList, true);
        recorder.end();
        return appList;
    }

    /**
     * 获取当前运行的程序列表
     *
     * @param apps
     * @param filterRecentKilled 是否过滤最近查杀的应用, true则过滤
     */
    private void getRunningAppListInternal(List<RunningAppBean> apps,
                                           boolean filterRecentKilled) {
        List<ActivityManager.RunningAppProcessInfo> rApps = mActMgr.getRunningAppProcesses();
        Logger.e("ProcessManager", "aApps.size = " + rApps.size());
        if (Build.VERSION.SDK_INT >= 24 && (rApps == null || rApps.size() < 5)) {
            getRunningAppListInternalForAndroidApi25(apps, filterRecentKilled);
            return;
        }
        if (Machine.HAS_SDK_LOLLIPOP && (rApps == null || rApps.size() < 5)) {
            // 5.0以上的系统并且无法通过api getRunningAppProcesses()获取正在运行的应用，则用以下特殊方法
            getRunningAppListInternalForAndroidApi22(apps, filterRecentKilled);
            return;
        }
        final IgnoreListManager ignoreMgr = IgnoreListManager.getInstance();
        try {
            rApps = mActMgr.getRunningAppProcesses();
            long now = SystemClock.elapsedRealtime();
            final List<ActivityManager.RunningServiceInfo> runningServices = getRunningServices(100);
            for (ActivityManager.RunningAppProcessInfo ri : rApps) {
                String[] pkgList = ri.pkgList;
                for (String pkgName : pkgList) {
                    // 过滤掉系统关键进程及用户主要的进程
                    if (EssentialProcessFilter.isEssentialProcess(pkgName)) {
                        Logger.d(TAG, "系统关键进程 - [" + pkgName + "]过滤");
                        continue;
                    }
                    // 过滤最近杀死的进程（给用户查杀效果的印象）
                    if (filterRecentKilled
                            && EssentialProcessFilter.isRecentKilled(pkgName,
                            now)) {
                        Logger.d(TAG, "最近查杀过的进程 - [" + pkgName + "]过滤");
                        continue;
                    }
                    ApplicationInfo appInfo = TheApplication.getAppContext().getPackageManager().getApplicationInfo(pkgName,
                            PackageManager.GET_META_DATA);
                    RunningAppBean appRunning = getRunningAppModleInList(
                            pkgName, apps);
                    if (appRunning != null) { // 该进程已经在我们的列表中了
                        appRunning.mPids.add(ri.pid);
                        Logger.d(TAG, "进程[" + pkgName + "]已存在，添加子进程pid-"
                                + ri.pid);
                    } else { // 不在列表中，添加
                        boolean isSystemApp = AppUtils.isSystemApp(appInfo);
                        // 再按照系统重要进程关键字模糊匹配
                        if (EssentialProcessFilter.isEssentialProcessMock(
                                pkgName, isSystemApp)) {
                            Logger.d(TAG, "系统关键进程（模糊匹配） - [" + pkgName + "]过滤");
                            continue;
                        }
                        // 封装信息到AppRunning类
                        appRunning = new RunningAppBean();
                        appRunning.mAppName = TheApplication.getAppContext().getPackageManager().getApplicationLabel(appInfo).toString();
                        appRunning.mPids.add(ri.pid);
                        appRunning.mProcessName = ri.processName;
                        appRunning.mPackageName = appInfo.packageName;
                        appRunning.mIsSysApp = isSystemApp;
                        appRunning.mIsForeground = isServiceRunningForeground(
                                runningServices, appInfo.packageName);
                        appRunning.mIsLaunchableApp = isLauncherableApp(appInfo.packageName);
                        appRunning.mIsIgnore = ignoreMgr.isInIgnoreList(appRunning.mPackageName);
                        apps.add(appRunning);
                        Logger.d(TAG, "添加到正在运行程序列表 - [" + appRunning.mAppName
                                + "|" + pkgName + "]");
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
            if (Logger.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前运行的程序列表(适配5.1.1系统，因为getRunningAppProcesses这个接口已经无效，替代方式是获取全部程序，
     * 排除停止运行的应用)
     *
     * @param apps
     * @param filterRecentKilled 是否过滤最近查杀的应用, true则过滤
     */
    @TargetApi(Machine.SDK_VERSION_CODE_5_1_1)
    private void getRunningAppListInternalForAndroidApi22(
            List<RunningAppBean> apps, boolean filterRecentKilled) {
        //final IgnoreListManager ignoreMgr = LauncherModel.getInstance().getIgnoreListManager();
        try {
            List<PackageInfo> rApps = AppUtils.getInstalledPackages(mContext);
            Map<String, List<Integer>> runningAppProcessesMap = ProcessHelperUtil
                    .getRunningAppProcesses(mContext);
            long now = SystemClock.elapsedRealtime();
            final List<ActivityManager.RunningServiceInfo> runningServices = getRunningServices(100);
            for (PackageInfo ri : rApps) {
                String pkgName = ri.packageName;
                // 过滤没有运行的进程
                if (!runningAppProcessesMap.containsKey(pkgName)) {
                    Logger.d(TAG, "没有运行的进程 - [" + pkgName + "]过滤");
                    continue;
                }
                // 过滤已经停止运行的进程
                if (AppUtils.isAppStop(mContext, pkgName)) {
                    Logger.d(TAG, "已经停止运行的进程 - [" + pkgName + "]过滤");
                    continue;
                }
                // 过滤掉系统关键进程及用户主要的进程
                if (EssentialProcessFilter.isEssentialProcess(pkgName)) {
                    Logger.d(TAG, "系统关键进程 - [" + pkgName + "]过滤");
                    continue;
                }
                // 过滤最近杀死的进程（给用户查杀效果的印象）
                if (filterRecentKilled
                        && EssentialProcessFilter.isRecentKilled(pkgName, now)) {
                    Logger.d(TAG, "最近查杀过的进程 - [" + pkgName + "]过滤");
                    continue;
                }
                ApplicationInfo appInfo = ri.applicationInfo;
                RunningAppBean appruning = getRunningAppModleInList(pkgName,
                        apps);
                if (appruning != null) { // 该进程已经在我们的列表中了
                    if (runningAppProcessesMap.containsKey(pkgName)) {
                        appruning.mPids.clear();
                        appruning.mPids.addAll(runningAppProcessesMap
                                .get(pkgName));
                    }
                    // Logger.d(TAG, "进程[" + pkgName + "]已存在，添加子进程pid-"
                    // + ri.pid);
                } else { // 不在列表中，添加
                    boolean isSystemApp = AppUtils.isSystemApp(appInfo);
                    // 再按照系统重要进程关键字模糊匹配
                    if (EssentialProcessFilter.isEssentialProcessMock(pkgName,
                            isSystemApp)) {
                        Logger.d(TAG, "系统关键进程（模糊匹配） - [" + pkgName + "]过滤");
                        continue;
                    }
                    // 封装信息到AppRunning类
                    appruning = new RunningAppBean();
                    appruning.mAppName = TheApplication.getAppContext().getPackageManager().getApplicationLabel(appInfo)
                            .toString();
                    if (runningAppProcessesMap.containsKey(pkgName)) {
                        appruning.mPids.addAll(runningAppProcessesMap
                                .get(pkgName));
                        // appruning.mProcessName = ri.processName; // 进程名暂时不要
                    }
                    appruning.mPackageName = appInfo.packageName;
                    appruning.mIsSysApp = isSystemApp;
                    appruning.mIsForeground = isServiceRunningForeground(
                            runningServices, appInfo.packageName);
                    appruning.mIsLaunchableApp = isLauncherableApp(appInfo.packageName);
                    appruning.mIsIgnore = IgnoreListManager.getInstance().isInIgnoreList(appruning.mPackageName);
                    apps.add(appruning);
                    Logger.d(TAG, "添加到正在运行程序列表 - [" + appruning.mAppName + "|"
                            + pkgName + "]");
                }
            }
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
            if (Logger.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private void getRunningAppListInternalForAndroidApi25(
            List<RunningAppBean> apps, boolean filterRecentKilled) {
        try {
            Map<String, List<Integer>> runningAppProcessesMap = ProcessHelperUtil
                    .getRunningAppProcesses(mContext);
            long now = SystemClock.elapsedRealtime();
            List<ActivityManager.RunningServiceInfo> runningServices = mActMgr.getRunningServices(100);
            Logger.e("ProcessManager", "25 size=" + runningServices.size());
            for (ActivityManager.RunningServiceInfo ri : runningServices) {
                String pkgName = ri.service.getPackageName();
                // 过滤没有运行的进程
//                if (!runningAppProcessesMap.containsKey(pkgName)) {
//                    Logger.d(TAG, "没有运行的进程 - [" + pkgName + "]过滤");
//                    continue;
//                }
                // 过滤已经停止运行的进程
//                if (AppUtils.isAppStop(mContext, pkgName)) {
//                    Logger.d(TAG, "已经停止运行的进程 - [" + pkgName + "]过滤");
//                    continue;
//                }
                // 过滤掉系统关键进程及用户主要的进程
                if (EssentialProcessFilter.isEssentialProcess(pkgName)) {
                    Logger.d(TAG, "系统关键进程 - [" + pkgName + "]过滤");
                    continue;
                }
                // 过滤最近杀死的进程（给用户查杀效果的印象）
                if (filterRecentKilled
                        && EssentialProcessFilter.isRecentKilled(pkgName, now)) {
                    Logger.d(TAG, "最近查杀过的进程 - [" + pkgName + "]过滤");
                    continue;
                }
                ApplicationInfo appInfo;
                try {
                    appInfo = mContext.getPackageManager().getApplicationInfo(pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
                RunningAppBean appruning = getRunningAppModleInList(pkgName,
                        apps);
                if (appruning != null) { // 该进程已经在我们的列表中了
                    if (runningAppProcessesMap.containsKey(pkgName)) {
                        appruning.mPids.clear();
                        appruning.mPids.addAll(runningAppProcessesMap
                                .get(pkgName));
                    }
                    // Logger.d(TAG, "进程[" + pkgName + "]已存在，添加子进程pid-"
                    // + ri.pid);
                } else { // 不在列表中，添加
                    boolean isSystemApp = AppUtils.isSystemApp(appInfo);
                    // 再按照系统重要进程关键字模糊匹配
                    if (EssentialProcessFilter.isEssentialProcessMock(pkgName,
                            isSystemApp)) {
                        Logger.d(TAG, "系统关键进程（模糊匹配） - [" + pkgName + "]过滤");
                        continue;
                    }
                    // 封装信息到AppRunning类
                    appruning = new RunningAppBean();
                    appruning.mAppName = TheApplication.getAppContext().getPackageManager().getApplicationLabel(appInfo)
                            .toString();
                    if (runningAppProcessesMap.containsKey(pkgName)) {
                        appruning.mPids.addAll(runningAppProcessesMap
                                .get(pkgName));
                        // appruning.mProcessName = ri.processName; // 进程名暂时不要
                    }
                    appruning.mPackageName = appInfo.packageName;
                    appruning.mIsSysApp = isSystemApp;
                    appruning.mIsForeground = isServiceRunningForeground(
                            runningServices, appInfo.packageName);
                    appruning.mIsLaunchableApp = isLauncherableApp(appInfo.packageName);
                    appruning.mIsIgnore = IgnoreListManager.getInstance().isInIgnoreList(appruning.mPackageName);
                    apps.add(appruning);
                    Logger.d(TAG, "添加到正在运行程序列表 - [" + appruning.mAppName + "|"
                            + pkgName + "]");
                }
            }
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
            if (Logger.DEBUG) {
                e.printStackTrace();
            }
        }
        Logger.e("ProcessManager", "25结束 列表长度 = " + apps.size());
    }

    /**
     * 是否为可打开的app进程
     *
     * @param packageName
     * @return
     */
    public boolean isLauncherableApp(String packageName) {
        return mLauncherAbleApps.contains(packageName);
    }

    /**
     * 在列表中查找是否有跟所提供包名一致的app进程
     *
     * @param pkgName
     * @param list
     * @return
     */
    private RunningAppBean getRunningAppModleInList(String pkgName,
                                                    List<RunningAppBean> list) {
        RunningAppBean appModel = null;
        for (RunningAppBean app : list) {
            if (app.mPackageName.equals(pkgName)) {
                appModel = app;
                break;
            }
        }
        return appModel;
    }

    /**
     * <br>
     * 功能简述:获取正在运行的服务列表 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @return
     */
    /*public List<RunningServiceBean> getRunningServiceList() throws PackageManager.NameNotFoundException {
        List<RunningServiceBean> serviceList = new ArrayList<RunningServiceBean>();
        List<ActivityManager.RunningServiceInfo> runningService = getRunningServices(100);
        for (ActivityManager.RunningServiceInfo service : runningService) {
            if (service.service == null) {
                continue;
            }
            final String pkgName = service.service.getPackageName();

            // 过滤掉系统关键进程及用户主要的进程
            if (EssentialProcessFilter.isEssentialProcess(pkgName)) {
                continue;
            }

            ApplicationInfo appInfo = TheApplication.getAppContext().getPackageManager().getApplicationInfo(pkgName,
                    PackageManager.GET_META_DATA);
            ;
            if (appInfo != null) {
                // 获得该进程占用的内存
                int[] myMempid = new int[]{service.pid};
                // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
                Debug.MemoryInfo[] memoryInfo = mActMgr
                        .getProcessMemoryInfo(myMempid);
                // 获取进程占内存用信息kb单位
                int memSize = memoryInfo[0].getTotalPrivateDirty();
                boolean isSystemApp = AppUtils.isSystemApp(appInfo);
                // 封装信息到serviceRunning类
                RunningServiceBean serviceRunning = new RunningServiceBean();
                serviceRunning.mPids.add(service.pid);
                serviceRunning.mPackageName = appInfo.packageName;
                serviceRunning.mMemory = memSize;
                serviceRunning.mIsSysApp = isSystemApp;
                serviceRunning.mComponent = service.service;
                serviceList.add(serviceRunning);
            }
        }
        return serviceList;
    }*/

    /**
     * 获取手机当前可用内存,默认取保护后的数据
     *
     * @return 可用内存(KB)
     */
    public long getAvaliableMemory() {
        return getAvaliableMemory(true);
    }

    /**
     * 获取手机当前可用内存
     *
     * @param isProtect 是否保护数据,90s内显示比较好看的数据
     * @return 可用内存(KB)
     */
    public long getAvaliableMemory(boolean isProtect) {
//        if (!isProtect) {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        mActMgr.getMemoryInfo(memInfo);
        return memInfo.availMem >> 10; // div 1024
//        }
//        else {
        // 保护数据
        //return BoostProtectManger.getInstance().getAvaliableMemory();
//        }
    }

    /**
     * 获取手机总内存
     *
     * @return 手机总内存(KB)
     */
    public long getTotalMemory() {
        long totalMemroy = 0;
        BufferedReader bufReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("/proc/meminfo");
            bufReader = new BufferedReader(fileReader, 4096);
            String cat = bufReader.readLine(); // 读取第一行“MemTotal: 415268 kB”
            if (cat != null) {
                String[] array = cat.split("\\s+");
                if (array != null && array.length > 1) {
                    // 数组第2个为内存大小
                    totalMemroy = Long.parseLong(array[1]);
                }
                array = null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return totalMemroy;
    }

    /**
     * 普通终止程序
     *
     * @param packageName 包名
     */
    public void killAppByPackageName(final String packageName) {
        TheApplication.postRunOnShortTaskThread(new Runnable() {

            @Override
            public void run() {
                if (mActMgr != null && !TextUtils.isEmpty(packageName)) {
                    mActMgr.killBackgroundProcesses(packageName);
                }
            }
        });
    }

    /**
     * 普通终止程序
     *
     * @param pid 程序的pid
     */
    @SuppressWarnings("deprecation")
    public void killAppByPid(int pid) {
        List<ActivityManager.RunningAppProcessInfo> listApps = mActMgr.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appInfo : listApps) {
            if (appInfo.pid == pid) {
                String[] pkList = appInfo.pkgList;
                if (Machine.HAS_SDK_FROYO) {
                    mActMgr.killBackgroundProcesses(pkList[0]);
                } else {
                    mActMgr.restartPackage(pkList[0]);
                }
            }
        }
    }

    /**
     * root模式下强力查杀进程
     *
     * @param pkgName
     */
    public void forceKillAppByPackageName(String pkgName) {
        // 以下强制关闭所有的activity
        ArrayList<String> list = new ArrayList<String>();
        // 加载系统核心进程
        list.add(searchFilePath("app_process"));
        // 加载的jar包中的类
        list.add("com.gau.go.taskmanager.CoreFS");
        if (Machine.HAS_SDK_JELLY_BEAN_MR1) {
            list.add("-fs17");
            list.add(pkgName);
            list.add(String.valueOf(getUserId(mContext, pkgName)));
        } else {
            list.add("-fs");
            list.add(pkgName);
        }
        String libPath = getCoreLibAbsPath(mContext);
        execvpFS(mContext, "app_process", list, libPath);
    }

    /**
     * 从系统Path变量的路径下获取指定文件路径
     *
     * @param fileName 文件名
     * @return
     */
    private String searchFilePath(String fileName) {
        String path = System.getenv("PATH");
        if (TextUtils.isEmpty(path)) {
            path = null;
        } else if (path.contains(":")) {
            String[] paths = path.split(":");
            for (String p : paths) {
                if (new File(p, fileName).isFile()) {
                    path = p;
                    break;
                }
            }
        }
        return path;
    }

    /**
     * Returns the user id for a given uid.
     */
    private final int getUserId(Context context, String pkgName) {
        ApplicationInfo ai = null;
        try {
            ai = TheApplication.getAppContext().getPackageManager().getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int uid = ai.uid;
        return uid / PER_USER_RANGE;
    }

    /**
     * 获取核心jar包的文件路径，如果没有在内部存储则进行拷贝
     *
     * @param context
     * @return
     */
    private static final String getCoreLibAbsPath(Context context) {
        final String coreLib;
        if (Machine.HAS_SDK_JELLY_BEAN_MR1) {
            coreLib = "core_fs.jar";
        } else {
            coreLib = "tkcore.jar";
        }
        File file = context.getFileStreamPath(coreLib);
        String path = null;
        if (file != null) {
            if (!file.exists() || !file.isFile()) {
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = context.getAssets().open(coreLib);
                    fos = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int i = 0;
                    while ((i = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, i);
                    }
                    fos.flush();
                    path = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                path = file.getAbsolutePath();
            }
        }
        return path;
    }

    private boolean execvpFS(Context context, String fileName,
                             ArrayList<String> list1, String libPath) {
        String filePath = searchFilePath(fileName);
        boolean result = false;
        if (filePath != null) {
            result = fsCloseProcess(context, pathAppend(filePath, fileName),
                    list1, libPath);
        }
        return result;
    }

    private static String pathAppend(String string1, String string2) {
        StringBuffer sb = new StringBuffer(string1);
        if (!string1.endsWith("/")) {
            sb.append("/");
        }
        sb.append(string2);
        return sb.toString();
    }

    private boolean fsCloseProcess(Context context, String appCoreFile,
                                   ArrayList<String> cmdList, String jarFilePath) {
        StringBuilder sb = new StringBuilder();
        if (Machine.SDK_UNDER_JELLY_BEAN) {
            sb.append("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
        }
        if (!TextUtils.isEmpty(jarFilePath)) {
            sb.append("CLASSPATH=").append(jarFilePath);
        }
        sb.append(" ").append(appCoreFile).append(" ");
        for (Iterator<String> iterator = cmdList.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();
            sb.append(string).append(" ");
        }
        sb.append("\n");
//        RootManager rtMgr = LauncherModel.getInstance().getRootManager();
//        final String cmd = sb.toString();
//        Logger.d(TAG, "执行命令：" + cmd);
//        return rtMgr.execu(cmd);
        return false;
    }

    /**
     * 检查正在运行的应用是否有services在运行
     *
     * @param context
     * @param allRunningApps 通过方法 {@link #getRunningAppList()} 获取的数据.<br>
     */
    public static List<RunningAppBean> checkHasRunningServices(
            Context context, List<RunningAppBean> allRunningApps) {
        if (null == allRunningApps || allRunningApps.size() == 0) {
            return allRunningApps;
        }
        final ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> runningServices = am
                .getRunningServices(Integer.MAX_VALUE);
        for (RunningAppBean runningAppModle : allRunningApps) {
//			Logger.d("LJL", "packageName: " +  runningAppModle.mPackageName);
            runningAppModle.mRunningServices.clear();
            if (runningServices != null) {
                for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
                    if (runningAppModle.mPackageName
                            .equals(runningServiceInfo.service.getPackageName())) {
                        runningAppModle.mRunningServices
                                .add(runningServiceInfo.service);
//						Logger.d("LJL", "service: " +  runningServiceInfo.service.getShortClassName());
                    }
                }
            }
        }
        return allRunningApps;
    }

    /**
     * 获取正在运行的服务列表<br>
     *
     * @param maxNum 获取列表的最大数目，实际个数会小于或等于这个值<br>
     * @return
     */
    public List<ActivityManager.RunningServiceInfo> getRunningServices(int maxNum) {
        List<ActivityManager.RunningServiceInfo> list = null;
        try {
            list = mActMgr.getRunningServices(maxNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == list) {
            list = new ArrayList<ActivityManager.RunningServiceInfo>();
        }
        return list;
    }

    /**
     * 应用的服务是否在前台运行<br>
     *
     * @param runningServices {@link #getRunningServices()}
     * @param packageName
     * @return
     */
    public boolean isServiceRunningForeground(
            List<ActivityManager.RunningServiceInfo> runningServices, String packageName) {
        if (null == runningServices || runningServices.size() == 0
                || TextUtils.isEmpty(packageName)) {
            return false;
        }
        boolean serviceRunningforeground = false;
        Iterator<ActivityManager.RunningServiceInfo> iterator = runningServices
                .iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = iterator.next();
            if (runningServiceInfo.service.getPackageName().equals(packageName)
                    && runningServiceInfo.foreground) {
                serviceRunningforeground = true;
                break;
            }
        }
        return serviceRunningforeground;
    }

    /**
     * 是否禁止内存查杀的应用(如重要系统进程就不能查杀，否则可能引起严重问题)<br>
     *
     * @param packageName
     * @return
     */
    public boolean isForbidBoostApp(String packageName) {
        // 过滤掉系统关键进程及用户主要的进程
        if (EssentialProcessFilter.isEssentialProcess(packageName)) {
            return true;
        }
        ApplicationInfo appInfo = null;
        try {
            appInfo = TheApplication.getAppContext().getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo != null) {
            boolean isSystemApp = AppUtils.isSystemApp(appInfo);
            // 再按照系统重要进程关键字模糊匹配
            if (EssentialProcessFilter.isEssentialProcessMock(packageName,
                    isSystemApp)) {
                return true;
            }
        }
        return false;
    }

    public float updateAvaliableMemoryPercentage(long avaliableMemory,
                                                 long totalMemory) {
        mAvaliableMemoryPercentage = (float) avaliableMemory / totalMemory;
        return mAvaliableMemoryPercentage;
    }

    public float getAvaliableMemoryPercentage() {
        return mAvaliableMemoryPercentage;
    }

}

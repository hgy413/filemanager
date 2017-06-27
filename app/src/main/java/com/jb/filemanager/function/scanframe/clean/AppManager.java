package com.jb.filemanager.function.scanframe.clean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.function.scanframe.clean.event.AppInstallEvent;
import com.jb.filemanager.function.scanframe.clean.event.AppManagerDataCompleteEvent;
import com.jb.filemanager.function.scanframe.clean.event.AppManagerHalfCompleteEvent;
import com.jb.filemanager.function.scanframe.clean.event.AppUninstallEvent;
import com.jb.filemanager.function.scanframe.clean.event.AppUpdateEvent;
import com.jb.filemanager.function.scanframe.clean.event.GlobalDataLoadingDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageAddedEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageRemovedEvent;
import com.jb.filemanager.function.scanframe.clean.event.PackageReplacedEvent;
import com.jb.filemanager.function.scanframe.manager.SysCacheManager;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.util.device.Machine;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * com.gto.zero.zboost.util.AppManager
 *
 * @author wangying <br/>
 *         应用信息获取类
 */

public class AppManager {

    private Context mContext;

    private String mCalculatorPackageName = null;

    private static final String CALCULATOR = "calculator";

    /**
     * 所有程序的缓存大小
     */
    private long mAllAppCacheSize;

    /**
     * 单例
     */
    public static AppManager sIntance = null;

    /**
     * 所有安装的应用
     */
    private ArrayList<AppItemInfo> mCompleteApp;

    /**
     * 除了DataSize，CodeSize，CacheSize之外的数据是否已经完成
     */
    private boolean mHalfComplete = false;

    /**
     * 数据是否已经完整
     */
    private boolean mIsComplete = false;

    /**
     * 上次扫描完成的时间
     */
    private long mCompleteTime = 0;

    /**
     * 初始化AppManager单例
     *
     * @param context
     */
    public static void initSingleton(Context context) {
        if (sIntance == null) {
            sIntance = new AppManager(context);
        }
    }

    private AppManager(Context context) {

        mContext = context.getApplicationContext();
        if (mCompleteApp == null) {
            mCompleteApp = new ArrayList<>();
        }
        TheApplication.getGlobalEventBus().register(this);

    }

    /**
     * 获取AppManager单例
     */
    public static AppManager getIntance() {
        if (sIntance == null) {
            // 初始化异常时会触发该条件, 可能会无数据
            // 2017年2月13日17:33:30 xiaoyu
            sIntance = new AppManager(TheApplication.getAppContext());
        }
        return sIntance;
    }

    /**
     * 应用数据除了缓存数据是否已经完毕
     */
    public synchronized boolean getHalfComplete() {
        return mHalfComplete;
    }

    /**
     * 应用缓存数据是否已经完毕
     */
    public synchronized boolean isComplete() {
        return mIsComplete;
    }

    /**
     * 上次完成的时间
     */
    public synchronized long getCompleteTime() {
        return mCompleteTime;
    }

    public void setAllAppCacheSize(long size) {
        mAllAppCacheSize = size;
    }

    public long getAllAppCacheSize() {
        return mAllAppCacheSize;
    }

    /**
     * 3月20日修改
     */
    public void addOneAppCache(long oneAppCache) {
        mAllAppCacheSize = mAllAppCacheSize + oneAppCache;
    }

    /**
     * 获取所有的应用
     */
    public ArrayList<AppItemInfo> getAllApps() {
        return (ArrayList<AppItemInfo>) mCompleteApp.clone();
    }

    /**
     * 扫描出的所有安装的应用程序的信息
     *
     * @throws InterruptedException
     */
    public void scanAllAppItems() {
        mIsComplete = false;
        mHalfComplete = false;
        mCompleteApp = (ArrayList<AppItemInfo>) scanAllInstallApps(mContext);
        mHalfComplete = true;
        TheApplication.getGlobalEventBus().postSticky(new AppManagerHalfCompleteEvent());
        SysCacheManager appCaCheManager = new SysCacheManager(mContext);
        appCaCheManager.setOnAppSizeCompleteListener(new AllAppSizeCompleteListener());
        appCaCheManager.querySizes(mCompleteApp);
    }

    /**
     * 获取所有安装应用
     */
    private List<AppItemInfo> scanAllInstallApps(Context context) {

        List<AppItemInfo> appInfos = new ArrayList<>();
        // 获得所有安装的应用
        List<PackageInfo> packageInfos = PackageManagerLocker.getInstance().getInstalledPackages(0);

        int size = packageInfos.size();
        // 将PackageInfo中的信息填入AppItemInfo中
        for (int i = 0; i < size; i++) {
            PackageInfo resolveInfo = packageInfos.get(i);
            appInfos.add(goIntoOne(resolveInfo));
        }
        return appInfos;
    }

    /**
     * 将单个的PackageInfo包装成AppItemInfo，初始化AppItemInfo
     *
     * @param packageInfo
     * @return AppItemInfo
     */
    private AppItemInfo goIntoOne(PackageInfo packageInfo) {
        if (packageInfo == null || packageInfo.packageName == null) {
            return null;
        }

        boolean isSystemApp = ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                || ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
        AppItemInfo appItemInfo = new AppItemInfo();
        CharSequence charSequence = PackageManagerLocker.getInstance().getApplicationLabel(packageInfo.applicationInfo);
        if (charSequence != null) {
            appItemInfo.setAppName(charSequence.toString());
        }

        appItemInfo.setAppUriString("package:" + packageInfo.packageName);
        appItemInfo.setAppPackageName(packageInfo.packageName.trim());
        containCalculator(packageInfo.packageName);

        if (Machine.HAS_SDK_GINGERBREAD) {
            appItemInfo.setVersionName(packageInfo.versionName);
            appItemInfo.setVersionCode(packageInfo.versionCode);
        }
        appItemInfo.setFirstInstallTime(packageInfo.firstInstallTime);
        appItemInfo.setLastUpdateTime(packageInfo.lastUpdateTime);
        appItemInfo.setSysApp(isSystemApp);

        return appItemInfo;
    }

    /**
     * 取得所有的系统应用
     *
     * @return ArrayList<AppItemInfo>
     */
    public ArrayList<AppItemInfo> getSystemApps() {

        ArrayList<AppItemInfo> syetemApps = new ArrayList<AppItemInfo>();
        for (AppItemInfo appItemInfo : mCompleteApp) {
            if (appItemInfo.getIsSysApp()) {
                syetemApps.add(appItemInfo);
            }
        }
        return syetemApps;
    }

    /**
     * 获取系统预装应用个数，注意判是否为-1
     *
     * @return
     */
    public int getSystemAppCounts() {
        int count = -1;
        if (mHalfComplete) {
            count = 0;
            for (AppItemInfo appItemInfo : mCompleteApp) {
                if (appItemInfo.getIsSysApp()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取用户安装应用数量,注意判是否为-1
     *
     * @return
     */
    public int getInstallAppCounts() {
        if (mHalfComplete) {
            return mCompleteApp.size() - getSystemAppCounts();
        } else {
            return -1;
        }
    }

    /**
     * 取得所有的非系统应用
     *
     * @return ArrayList<AppItemInfo>
     */
    public ArrayList<AppItemInfo> getNotSystemApps() {
        ArrayList<AppItemInfo> notSystemApps = new ArrayList<>();

        for (AppItemInfo appItemInfo : mCompleteApp) {
            if (!appItemInfo.getIsSysApp()
                    && !appItemInfo.getAppPackageName().equals(
                    Const.PACKAGE_NAME)) {
                notSystemApps.add(appItemInfo);
            }
        }

        return notSystemApps;
    }

    /**
     * 获取所有有缓存的应用(已经安装),按缓存的从大到小排序
     *
     * @return
     */
    public ArrayList<AppItemInfo> getCacheApps() {

        ArrayList<AppItemInfo> cacheApps = new ArrayList<>();

        for (AppItemInfo appItemInfo : mCompleteApp) {
            if (appItemInfo.getAppCacheSize() > 0) {
                cacheApps.add(appItemInfo);
            }
        }
        ScoreComparator sc = new ScoreComparator();
        Collections.sort(cacheApps, sc);
        return cacheApps;
    }

    /**
     * 对list排序的类
     *
     * @author zoupinyuan
     */
    class ScoreComparator implements Comparator {
        @Override
        public int compare(Object arg0, Object arg1) {
            AppItemInfo s1 = (AppItemInfo) arg0;
            AppItemInfo s2 = (AppItemInfo) arg1;
            // 第一个比第二个大，返回-1
            if (s1.getAppCacheSize() > s2.getAppCacheSize()) {
                return -1;
                // 第一个和第二个相等，返回0
            } else if (s1.getAppCacheSize() == s2.getAppCacheSize()) {
                return 0;
                // 第一个比第二个小，返回1
            } else {
                return 1;
            }
        }
    }

    /**
     * 清除数据
     */
    public void clearData() {
        this.mCompleteApp.clear();
    }

    /**
     * 数据准备好时的回调接口 com.gto.zero.zboost.function.clean.manager.ScanOkListener
     *
     * @author wangying <br/>
     *         create at 2015-1-22 下午8:18:57
     */
    class AllAppSizeCompleteListener implements SysCacheManager.AppSizeCompleteListener {

        @Override
        public void onGetCacheListComplete() {
            mIsComplete = true;
            mCompleteTime = System.currentTimeMillis();
            TheApplication.getGlobalEventBus().post(new AppManagerDataCompleteEvent());
        }
    }

    /**
     * app安装时的数据同步回调接口
     * com.gto.zero.zboost.function.clean.manager.AppInstallDataOkListener
     *
     * @author wangying <br/>
     *         create at 2015-1-26 下午5:44:34
     */
    class AppInstallDataOkListener implements SysCacheManager.AppSizeCompleteListener {

        private AppItemInfo mAppItemInfoInstall;

        public AppInstallDataOkListener(AppItemInfo appItemInfo) {
            mAppItemInfoInstall = appItemInfo;
        }

        @Override
        public void onGetCacheListComplete() {
            TheApplication.getGlobalEventBus().post(
                    new AppInstallEvent(mAppItemInfoInstall));
        }
    }

    /**
     * app更新时的数据同步回调借口
     * com.gto.zero.zboost.function.clean.manager.AppUpdateDataOkListener
     *
     * @author wangying <br/>
     *         create at 2015-1-26 下午5:43:05
     */
    class AppUpdateDataOkListener implements SysCacheManager.AppSizeCompleteListener {

        private AppItemInfo mAppItemInfoInstall;

        public AppUpdateDataOkListener(AppItemInfo appItemInfo) {
            mAppItemInfoInstall = appItemInfo;
        }

        @Override
        public void onGetCacheListComplete() {
            TheApplication.getGlobalEventBus().post(
                    new AppUpdateEvent(mAppItemInfoInstall));
        }
    }

    /**
     * 接收处理安装应用,维护数据
     *
     * @param packageAddedEvent e
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(PackageAddedEvent packageAddedEvent) {
        String packageName = packageAddedEvent.getPackageName();
        try {
            PackageInfo packageInfo = PackageManagerLocker.getInstance().getPackageInfo(packageName, 0);
            AppItemInfo appItemInfo = goIntoOne(packageInfo);
            SysCacheManager appCaCheManager = new SysCacheManager(mContext, true);
            appCaCheManager
                    .setOnAppSizeCompleteListener(new AppInstallDataOkListener(
                            appItemInfo));
            mCompleteApp.add(appItemInfo);
            appCaCheManager.queryPackageSize(appItemInfo, 1, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理app卸载,内存app信息删除
     *
     * @param packageRemovedEvent e
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(PackageRemovedEvent packageRemovedEvent) {
        int num = 0;
        for (int i = 0; i < mCompleteApp.size(); i++) {
            if (mCompleteApp.get(i).getAppPackageName()
                    .equals(packageRemovedEvent.getPackageName())) {
                num = i;
                break;
            }
        }
        if (mCompleteApp == null || mCompleteApp.size() == 0) {
            return;
        }
        String pack = mCompleteApp.get(num).getAppPackageName();

        /** 3月20修改 */
        mAllAppCacheSize = mAllAppCacheSize - mCompleteApp.get(num).getAppCacheSize();

        mCompleteApp.remove(num);
        TheApplication.getGlobalEventBus().post(new AppUninstallEvent(pack));
    }

    /**
     * 处理app更新,内存信息更新
     *
     * @param packageReplacedEvent e
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackgroundThread(PackageReplacedEvent packageReplacedEvent) {
        int num = 0;
        String packageName = packageReplacedEvent.getPackageName();
        for (int i = 0; i < mCompleteApp.size(); i++) {
            if (mCompleteApp.get(i).getAppPackageName().equals(packageName)) {
                num = i;
                break;
            }
        }
        // 3月20日修改
        mAllAppCacheSize = mAllAppCacheSize - mCompleteApp.get(num).getAppCacheSize();

        mCompleteApp.remove(num);
        try {
            PackageInfo packageInfo = PackageManagerLocker.getInstance().getPackageInfo(packageName, 0);
            AppItemInfo appItemInfo = goIntoOne(packageInfo);
            mCompleteApp.add(appItemInfo);
            SysCacheManager appCaCheManager = new SysCacheManager(mContext,
                    true);
            appCaCheManager
                    .setOnAppSizeCompleteListener(new AppUpdateDataOkListener(
                            appItemInfo));
            appCaCheManager.queryPackageSize(appItemInfo, 1, false);
        } catch (Exception e1) {

        }
    }

    /**
     * 在启动阶段必要的数据加载完成后,进行第一次的app数据扫描,并进行相应标识位的标识
     *
     * @param event e
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(GlobalDataLoadingDoneEvent event) {
        try {
            AppManager.getIntance().scanAllAppItems();
        } catch (Exception e) {
        }

    }

    private void containCalculator(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            if (packageName.indexOf(CALCULATOR) != -1) {
                mCalculatorPackageName = packageName;
            }
        }
    }

    public String getCalculatorPackageName() {
        return mCalculatorPackageName;
    }

}

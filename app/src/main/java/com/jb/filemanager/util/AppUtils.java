package com.jb.filemanager.util;

/**
 * Created by bill wang on 2017/6/21.
 */


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.appBean.RunningAppBean;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.ProcessManager;
import com.jb.filemanager.util.device.Machine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.jb.ga0.commerce.util.topApp.TopHelper.sNotALauncher;
import static com.jiubang.commerce.buychannel.buyChannel.utils.AppInfoUtils.GOOGLE_ADVERTING_DEFAULT_ID;
import static com.jiubang.commerce.utils.AppUtils.getDefaultLauncher;

/**
 * @version 1.0.0
 */
public class AppUtils {

    private static final String LOG_TAG = "AppUtils";

    /**
     * first app user
     */
    private static final int AID_APP = 10000;

    /**
     * offset for uid ranges for each user
     */
    private static final int AID_USER = 100000;

    private static int sChannel;
    private static String sGoogleAdvertingId;

    private final static FilenameFilter FILENAME_FILTER_NUMS = new FilenameFilter() {

        /**
         * 匹配模式，只要数字
         */
        private Pattern mPattern = Pattern.compile("^[0-9]+$");

        @Override
        public boolean accept(File dir, String filename) {
            return mPattern.matcher(filename).matches();
        }
    };


    /**
     * 获取栈顶应用程序
     *
     * @param context
     * @return
     */
    public static ComponentName getTopPackageName(Context context) {
        ComponentName topActivity = null;
        // 5.1或以上
        if (Machine.HAS_SDK_5_1_1) {
            if (AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(context)) {
                topActivity = AppUtils.getFrontActivityLollipopMr1(context);
            }
        }
        // 5.0
        else if (Machine.HAS_SDK_LOLLIPOP) {
            if (AppUtils.isPermissionPackageUsageStatsGrandedOnLollipop(context)) {
                topActivity = AppUtils.getFrontActivityOnLollipop(context);
            }
        }
        // 5.0以下
        else {
            topActivity = AppUtils
                    .getTopActivity(context);
        }
        if (topActivity == null) {
            topActivity = new ComponentName("invalid_package_name", "invalid_activity_name");
        }
        return topActivity;
    }

    /**
     * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
     * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
     * 用于5.1版本或以上版本<br>
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean isPermissionPackageUsageStatsGrandedLollipopMr1(Context context) {
        if (Machine.HAS_SDK_5_1_1) {
            return isPermissionPackageUsageStatsGranded(getSystemServiceUsageStatsManager(context));
        }
        return false;
    }

    /**
     * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
     * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
     *
     * @param usageStatsManager
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isPermissionPackageUsageStatsGranded(UsageStatsManager usageStatsManager) {
        if (usageStatsManager != null && Machine.HAS_SDK_LOLLIPOP) {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - AlarmManager.INTERVAL_DAY;
            List<UsageStats> usageStatses = null;
            try {
                usageStatses = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return usageStatses != null && !usageStatses.isEmpty();
        }
        return false;
    }

    /**
     * 获取系统应用信息管理实例UsageStatsManager<br>
     * 只建议于5.0或以上使用<br>
     *
     * @param context
     * @return UsageStatsManager or null when error occur
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static UsageStatsManager getSystemServiceUsageStatsManager(Context context) {
        UsageStatsManager usageStatsManager = null;
        if (Machine.HAS_SDK_5_1_1) {
            usageStatsManager = (UsageStatsManager) context
                    .getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            try {
                usageStatsManager = (UsageStatsManager) context
                        .getSystemService("usagestats");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return usageStatsManager;
    }


    /**
     * 获取栈顶的应用的ComponentName<br>
     * 注意只适用于5.1或以上<br>
     *
     * @param context
     * @return ComponentName or null when error
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static ComponentName getFrontActivityLollipopMr1(Context context) {
        return getFrontActivityLollipop(getSystemServiceUsageStatsManager(context));
    }

    /**
     * 获取栈顶的应用的ComponentName<br>
     * 注意只适用于5.0或以上<br>
     *
     * @param usageStatsManager
     * @return ComponentName or null when error
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ComponentName getFrontActivityLollipop(UsageStatsManager usageStatsManager) {
        if (usageStatsManager == null) {
            return null;
        }
        if (Machine.HAS_SDK_LOLLIPOP) {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000; // 获取10秒内的事件
            UsageEvents.Event event = new UsageEvents.Event();
            long lastEvent = 0;
            String packageName = null;
            String className = null;
            UsageEvents usageEvents = null;
            // XXX 按照usageStatsManager.queryEvents的方法说明，最近几分钟内的事件应该是获取不到的；
            // 然而APP LOCK经实际使用来看是可以获取到最近的事件的，或许以后这个API会做到如其方法所说，
            // 这样可能这个方法就不适用我们所期望的用途了
            try {
                usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (usageEvents != null) {
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                    if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        final long timeStamp = event.getTimeStamp();
                        // XXX 按照applocker的经验，以这个时间判断是否最新事件并不合适，只取最后一个事件就可以了
                        // if (timeStamp > lastEvent) {
                        lastEvent = timeStamp;
                        packageName = event.getPackageName();
                        className = event.getClassName();
                        // }
                    }
                }
            }
            // XXX 事件太久远了就不好使了吧...
//			if (System.currentTimeMillis() - lastEvent > 10 * 1000) {
//				// 用这个方法我们就没有className了
//				packageName = getFrontActivityPackageNameByQueryAndAggregateUsageStats(usageStatsManager);
//				className = "";
//			}

            if (TextUtils.isEmpty(className)) {
                className = "";
            }
            if (!TextUtils.isEmpty(packageName)) {
                return new ComponentName(packageName, className);
            }
        }
        return null;
    }

    /**
     * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
     * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
     * 用于5.0版本<br>
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isPermissionPackageUsageStatsGrandedOnLollipop(Context context) {
        if (Machine.HAS_SDK_LOLLIPOP) {
            return isPermissionPackageUsageStatsGranded(getSystemServiceUsageStatsManager(context));
        }
        return false;
    }

    /**
     * 获取栈顶的应用的ComponentName<br>
     * 注意只适用于5.0<br>
     *
     * @param context
     * @return ComponentName or null when error
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ComponentName getFrontActivityOnLollipop(Context context) {
        ComponentName frontActivity = getFrontActivityLollipop(getSystemServiceUsageStatsManager(context));
        if (frontActivity == null) {
            frontActivity = getFrontActivityOnLollipopByTrick(context);
        }
        return frontActivity;
    }

    /**
     * 在5.0系统使用的获取栈顶应用的补充方法<br>
     *
     * @param context
     * @return
     */
    private static ComponentName getFrontActivityOnLollipopByTrick(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> pis = activityManager.getRunningAppProcesses();
            if (pis != null) {
                for (ActivityManager.RunningAppProcessInfo pi : pis) {
                    if (pi.pkgList.length == 1) {
                        return new ComponentName(pi.pkgList[0], "");
                    }
                }
            }
        }
        return new ComponentName(getForegroundAppByProcFiles(context), "");
    }

    /**
     * 获取正在前台运行的应用的包名<br>
     * 通过分析进程文件估算，可靠性有待考量, 可作为补充使用<br>
     *
     * @param context
     * @return
     */
    public static String getForegroundAppByProcFiles(Context context) {
        File[] files = new File("/proc").listFiles(FILENAME_FILTER_NUMS);
        if (files == null || files.length == 0) {
            return "";
        }
        int lowestOomScore = Integer.MAX_VALUE;
//        String foregroundProcess = null;

        int foregroundProcessUid = -1;

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }

            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = readFile(String.format("/proc/%d/cgroup", pid));

                String[] lines = cgroup.split("\n");

                if (lines.length != 2) {
                    continue;
                }

                String cpuSubsystem = lines[0];
                String cpuAcctSubsystem = lines[1];

                if (!cpuAcctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }

                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }

//                String cmdline = read(String.format("/proc/%d/cmdline", pid));
//
//                if (cmdline.contains("com.android.systemui")) {
//                    continue;
//                }

                int uid = Integer.parseInt(cpuAcctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }

                int appId = uid - AID_APP;
//                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
//                    userId++;
                }

                if (appId < 0) {
                    continue;
                }

                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);

                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(readFile(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }

                int oomScore = Integer.parseInt(readFile(String.format("/proc/%d/oom_score", pid)));
                if (oomScore < lowestOomScore) {
                    lowestOomScore = oomScore;
//                    foregroundProcess = cmdline;
                    foregroundProcessUid = uid;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (foregroundProcessUid != -1) {
            return context.getPackageManager().getPackagesForUid(foregroundProcessUid)[0];
        }
        return "";
    }

    private static String readFile(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }


    /**
     * 获得当前所在进程的进程名<br>
     *
     * @param cxt context
     * @return current process name
     */
    public static String getCurrentProcessName(Context cxt) {
        ActivityManager actMgr = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        if (actMgr == null) {
            return null;
        }
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = actMgr
                .getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return null;
        }
        final int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
            if (appProcess != null && appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 查询已安装的应用
     *
     * @param context context
     * @return package info list
     */
    public static List<PackageInfo> getInstalledPackages(Context context) {
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> pakList = null;
        try {
            pakList = pManager.getInstalledPackages(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (pakList == null) {
            pakList = new ArrayList<>();
        }
        return pakList;
    }

    /**
     * 查询已安装的应用
     *
     * @param context context
     * @return application info list
     */
    public static List<ApplicationInfo> getInstalledApplications(Context context) {
        PackageManager pManager = context.getPackageManager();
        List<ApplicationInfo> appInfoList = null;
        try {
            appInfoList = pManager.getInstalledApplications(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (appInfoList == null) {
            appInfoList = new ArrayList<>();
        }
        return appInfoList;
    }

    @NonNull
    public static String getAppName(final Context context,
                                    final String packageName) {
        PackageInfo info = getAppPackageInfo(context, packageName);
        return getAppName(context, info);
    }

    @NonNull
    public static String getAppName(final Context context, PackageInfo info) {
        if (info != null) {
            return info.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }
        return "";
    }

    /**
     * 获取app包信息
     *
     * @param context     context
     * @param packageName 包名
     * @return package info
     */
    public static PackageInfo getAppPackageInfo(final Context context,
                                                final String packageName) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            info = null;
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 跳转到浏览器
     *
     * @param context   context
     * @param urlString url
     * @return result
     */
    public static boolean openBrowser(Context context, String urlString) {
        if (!(urlString.startsWith("http://") || urlString.startsWith("https://"))) {
            urlString = "http://" + urlString;
        }
        return !TextUtils.isEmpty(urlString) && openActivitySafely(context, Intent.ACTION_VIEW, urlString, null);
    }

    /**
     * 安全地打开外部的activity
     *
     * @param action      如Intent.ACTION_VIEW
     * @param uri         uri
     * @param packageName 可选，明确要跳转的程序的包名
     * @return 是否成功
     */
    public static boolean openActivitySafely(Context context, String action, String uri, String packageName) {
        boolean isOk = true;
        try {
            Uri uriData = Uri.parse(uri);
            final Intent intent = new Intent(action, uriData);
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName);
            }
            if (!Activity.class.isInstance(context)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
            isOk = false;
        }
        return isOk;
    }

    /**
     * 获取所有的应用中的有联网权限的应用
     *
     * @param context
     * @return 返回所有的应用中的有联网权限的应用
     * @author xiaoyu
     */
    public static List<PackageInfo> getInstalledNetPackages(Context context) {
        List<PackageInfo> result = new ArrayList<>();

        PackageManager packageManager = null;
        List<PackageInfo> installedPackages = null;
        try {
            packageManager = context.getPackageManager();
            installedPackages = packageManager.getInstalledPackages(0);
            // 安装应用过多, 内存超过1M时, PackageManager就会死掉TransactionTooLargeException
            if (packageManager == null) {
                packageManager = context.getPackageManager();
            }
            for (PackageInfo installedPackage : installedPackages) {
                String packageName = installedPackage.packageName;
                // 检查是否有联网权限
                boolean isPermission = (PackageManager.PERMISSION_GRANTED ==
                        packageManager.checkPermission("android.permission.INTERNET", packageName));
                if (isPermission) {
                    // 有联网权限, 则将其添加到结果集合中
                    result.add(installedPackage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 安装应用过多, 内存超过1M时, PackageManager就会死掉
            packageManager = context.getPackageManager();
            for (PackageInfo installedPackage : installedPackages) {
                String packageName = installedPackage.packageName;
                // 检查是否有联网权限
                boolean isPermission = (PackageManager.PERMISSION_GRANTED ==
                        packageManager.checkPermission("android.permission.INTERNET", packageName));
                if (isPermission) {
                    // 有联网权限, 则将其添加到结果集合中
                    //Log.e("获取所有应用 xiaoyu", packageName + ",有联网权限");
                    result.add(installedPackage);
                }
            }
        }

        return result;
    }

    /**
     * 检查是安装某包
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppExist(final Context context,
                                     final String packageName) {
        if (context == null || packageName == null) {
            return false;
        }
        boolean result = false;
        try {
            context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SHARED_LIBRARY_FILES);
            result = true;
        } catch (PackageManager.NameNotFoundException e) {
            result = false;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static long getAppFirstInstallTime(final Context context,
                                              final String packageName) {
        PackageInfo info = getAppPackageInfo(context, packageName);
        return getAppFirstInstallTime(context, info);
    }

    public static long getAppFirstInstallTime(final Context context,
                                              PackageInfo info) {
        if (null != info) {
            return info.firstInstallTime;
        }
        return 0;
    }

    /**
     * url跳转
     *
     * @param url url
     */
    public static void openLinkSafe(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (isIntentSafe(context, intent)) {
            context.startActivity(intent);
        }
    }


    /**
     * 在使用Intent试图打开其它软件(尤其是第三方)前, 应该先进行判断是否有支持该打开该Intent的Activity
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentSafe(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 跳转到指定应用的google play的详情页<br>
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean openGooglePlay(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        return openGooglePlay(context, "market://details?id=" + packageName, "https://play.google.com/store/apps/details?id=" + packageName);
    }

    /**
     * 跳转到google play, 优先跳转到客户端，若失败尝试跳转到网页
     *
     * @param context
     * @param uriString 用于跳转客户端的uri字符串， 如 market://details?id=com.jiubang.alock&referrer=utm_source%3Dcom.com.gto.zero.zboost_icon_%26utm_medium%3Dhyperlink%26utm_campaign%3DZboosticon
     * @param urlString 用于跳转到网页版的url字符串， 如 https://play.google.com/store/apps/details?id=com.gto.zero.zboost
     * @return
     */
    public static boolean openGooglePlay(Context context, String uriString, String urlString) {
        boolean isOk = false;
        if (!TextUtils.isEmpty(uriString)) {
            // 先尝试打开客户端
            isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, "com.android.vending");
            //不跳 应用商店
            //  if (!isOk) {
            //     isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, null);
            //  }
        }
        if (!isOk) {
            if (!TextUtils.isEmpty(urlString)) {
                // 试试打开浏览器
                isOk = openActivitySafely(context, Intent.ACTION_VIEW, urlString, null);
            }
        }
        return isOk;
    }

    /**
     * 打开"有权查看使用情况的应用"的窗口 该权限的授权窗口在 设置 -> 安全 -> 高级 -> 有权查看使用情况的应用(Apps with
     * usage access)<br>
     * 先只判断最终界面 而不处理中间界面 之后版本处理
     * */
    public static boolean openUsageAccess(Context context , int requestCode) {
        Activity mStart = null;
        final String[] pages = new String[]{Settings.ACTION_USAGE_ACCESS_SETTINGS/*, Settings.ACTION_SECURITY_SETTINGS,
                Settings.ACTION_SETTINGS*/};
        for (int i = 0; i < pages.length; i++) {
            Intent intent = new Intent(pages[i]);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                mStart = (Activity) context;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            if (isIntentSafe(context, intent)) {
                if (mStart != null) {
                    mStart.startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定的应用的Activity是否位于前台.<br>
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isFrontActivity(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        boolean isFront = false;
        if (Machine.HAS_SDK_LOLLIPOP) {
            try {
                ActivityManager activityManager = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                        .getRunningAppProcesses();
                if (appProcesses == null) {
                    return false;
                }
                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    // 通过进程名及进程所用到的包名来进行查找
                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        if (appProcess.processName.equals(packageName)
                                || Arrays.asList(appProcess.pkgList).contains(
                                packageName)) {
                            isFront = true;
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(LOG_TAG, e.toString());
            }
        } else {
            isFront = getFrontActivityAppPackageName(context).equals(
                    packageName);
        }
        return isFront;
    }

    /**
     * 获取当前前台Activity的应用的包名.<br>
     * 注意:只适用于5.0以下使用.<br>
     *
     * @param context
     * @return 对应的包名或当出错时为""
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getFrontActivityAppPackageName(Context context) {
        final ComponentName componentInfo = getTopActivity(context);
        if (componentInfo == null) {
            return "";
        }
        return componentInfo.getPackageName();
    }

    /**
     * 获取当前前台Activity的应用的ComponentName.<br>
     * 注意:只适用于5.0以下使用.<br>
     *
     * @param context
     * @return 对应的包名或当出错时为null
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ComponentName getTopActivity(Context context) {
        if (Machine.HAS_SDK_LOLLIPOP) {
            throw new IllegalStateException(
                    "getTopActivity() has no mean for above LOLLIPOP!");
        }
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (taskInfo == null || taskInfo.isEmpty()) {
            return null;
        }
        return taskInfo.get(0).topActivity;
    }

    public static String getCurrentLanguageWithLocale() {
        StringBuilder builder = new StringBuilder();
        builder.append(Locale.getDefault().getLanguage());
        builder.append("_");
        builder.append(Locale.getDefault().getCountry());
        return builder.toString();
    }

    /**
     * 现在是否在GP上
     */
    public static boolean isAtGP(Context context) {
        return isFrontActivity(context, Const.GP_PACKAGE);
    }

    /**
     * 打开google play客户端跳转到极加速的详情页<br>
     *
     * @param context
     */
    public static boolean openGP(Context context) {
        return openGooglePlay(context, Const.PACKAGE_NAME);
    }

    public static boolean likeUsOnFacebook(Context context) {
        String uri = "fb://page/987392581335091";
        String url = "https://www.facebook.com/GO-Security-987392581335091/";
        return AppUtils.openFacebook(context, uri, url);
    }

    public static boolean openFacebook(Context context, String uriString, String urlString) {
        boolean isOk = false;
        if (!TextUtils.isEmpty(uriString)) {
            // 先尝试打开客户端
            isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, "com.facebook.katana");
            if (!isOk) {
                isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, null);
            }
        }
        if (!isOk) {
            if (!TextUtils.isEmpty(uriString)) {
                // 试试打开浏览器
                isOk = openActivitySafely(context, Intent.ACTION_VIEW, urlString, null);
            }
        }
        return isOk;
    }

    /**
     * 跳转GooglePlay
     */
    public static void goToGooglePlay() {
        Context context = TheApplication.getAppContext();
        Uri uri = Uri.parse(Const.ABOUT_GOOGLE_PLAY_M);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
        marketIntent.setPackage(Const.GP_PACKAGE);
        marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            Uri uriHttp = Uri.parse(Const.ABOUT_GOOGLE_PLAY);
            Intent i = new Intent(Intent.ACTION_VIEW, uriHttp);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(i);
            } catch (ActivityNotFoundException e1) {
                Toast.makeText(context, context.getResources().getString(R.string.gp_not_connect), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 跳转url
     */
    public static void jumpUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            TheApplication.getAppContext().startActivity(intent);
        } catch (Exception e) {
            // ignore
            e.printStackTrace();
        }
    }

    /**
     * 简单的单例吐司  可以实现快速连续的弹出吐司
     *
     * @param message
     */
    public static void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(TheApplication.getAppContext(), message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 之前显示的内容
     */
    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    /**
     * 单例toast 避免快速点击的时候弹出很多toast
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showToast(Context context, int resId) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            String message = context.getApplicationContext().getResources().getString(resId);
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    /**
     * 获取版本名
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 修复输入框管理器内存泄漏
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * 根据包名卸载某个应用
     *
     * @param pkgName
     * @author xiaoyu
     */
    public static void uninstallAppWithPackageName(Context context, String pkgName) {
        Uri packageURI = Uri.parse("package:" + pkgName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 根据包名跳转到应用详情页面, 进行停止该应用
     *
     * @param context
     * @param pkgName
     */
    public static void stopApp(Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("package", pkgName, null));
        context.startActivity(intent);
    }

    /**
     * 检测是否升级用户
     *
     * @return result
     */
    public static boolean isInstallFromUpdate() {
        boolean result = false;
        PackageManager pm = TheApplication.getInstance().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(Const.PACKAGE_NAME, 0);
            long firstInstallTime = packageInfo.firstInstallTime;
            long lastUpdateTime = packageInfo.lastUpdateTime;
            result = firstInstallTime != lastUpdateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否拥有用户数据查看的权限
     * */
    public static boolean isHaveUsageDataPermission() {
        boolean isPermission = true;
        if (Machine.HAS_SDK_5_1_1) {
            isPermission = AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(TheApplication.getAppContext());
        } else if (Machine.HAS_SDK_LOLLIPOP) {
            isPermission = AppUtils.isPermissionPackageUsageStatsGrandedOnLollipop(TheApplication.getAppContext());
        }
        return isPermission;
    }

    /**
     * 获取桌面类应用的包名.<br>
     *
     * @param context context
     * @return result
     */
    public static List<String> getLauncherPackageNames(Context context) {
        List<String> packages = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            resolveInfo = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resolveInfo != null && !resolveInfo.isEmpty()) {
            for (ResolveInfo info : resolveInfo) {
                // 过滤掉一些名不符实的桌面
                if (!TextUtils.isEmpty(info.activityInfo.packageName)
                        && !sNotALauncher
                        .contains(info.activityInfo.packageName)) {
                    packages.add(info.activityInfo.packageName);
                }
            }
        }
        return packages;
    }
    /**
     * 判断应用是否是系统应用
     *
     * @param context context
     * @param intent intent
     * @return result
     */
    public static boolean isSystemApp(Context context, Intent intent) {
        boolean isSystemApp = false;
        if (context != null) {
            ApplicationInfo applicationInfo = PackageManagerLocker.getInstance().getActivityInfo(
                    intent.getComponent(), 0).applicationInfo;
            if (applicationInfo != null) {
                isSystemApp = ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                        || ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
            }
        }
        return isSystemApp;
    }

    /**
     * 判断应用是否是系统应用
     *
     * @return result
     */
    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        boolean isSystemApp = false;
        if (applicationInfo != null) {
            int i = applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM;
            int j = applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            isSystemApp = i != 0
                    || j != 0;
        }
        return isSystemApp;
    }

    /**
     * 是否是系统应用
     * */
    public static boolean isSystemApp(String pkgName) {
        ApplicationInfo app = PackageManagerLocker.getInstance().getApplicationInfo(pkgName, 0);
        return isSystemApp(app);
    }

    /**
     * 判断一个应用是否已经停止运行.<br>
     *
     * @param context context
     * @param packageName package name
     * @return result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static boolean isAppStop(Context context, String packageName) {
        boolean isStop;
        try {
            ApplicationInfo app = PackageManagerLocker.getInstance().getApplicationInfo(packageName, 0);
            isStop = (app.flags & ApplicationInfo.FLAG_STOPPED) != 0;
        } catch (Exception e) {
            isStop = true; // 通常是程序不存在了吧...
            e.printStackTrace();
        }
        return isStop;
    }

    /**
     * 是否安装有FB客户端
     *
     * @param context context
     * @return result
     */
    public static boolean isFacebookInstalled(Context context) {
        return isAppExist(context, Const.PACKAGE_FB) || isAppExist(context, Const.PACKAGE_FB_LITE);
    }

    public static String getChannel(Context context) {
        String channel;
        try {
            channel = String.valueOf(getChannelN(context));
        } catch (Exception e) {
            channel = "200";
        }
        return channel;
    }

    /**
     * 获取桌面渠道号的方法
     *
     * @param context context
     * @return result
     */
    private static int getChannelN(Context context) {
        if (sChannel == 0) {
            try {
                ApplicationInfo appInfo = PackageManagerLocker.getInstance().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                sChannel = appInfo.metaData.getInt("com.jb.filemanager.channel");
            } catch (Exception e) {
                sChannel = 200;
            }
        }
        return sChannel;
    }

    /**
     * 查询手机内所有应用
     * 获取设置的包名
     *
     * @param context
     * @return
     */
    public static List<String> getInstalledPackagesPackageNameOnly(
            Context context) {
        List<PackageInfo> paklist = getInstalledPackages(context);
        List<String> packNames = new ArrayList<String>();
        for (PackageInfo packageInfo : paklist) {
            packNames.add(packageInfo.packageName);
        }
        return packNames;
    }

    /**
     * 获取正在运行的应用, 未过滤
     *
     * @param context
     * @return
     * @author xiaoyu
     */
    public static List<RunningAppBean> getRunningApps(Context context) {
        List<RunningAppBean> result = ProcessManager.getInstance(context)
                .getRunningAppList();
        return result;
    }

    public static List<String> getSettingPackageName(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        return queryIntentActivities(context, intent);
    }

    /**
     * 获取卸载应用程序的包名
     *
     * @param context
     * @return
     */
    public static List<String> getUnInstallPackageName(Context context) {
        return queryIntentActivities(context, new Intent(Intent.ACTION_DELETE, Uri.parse("package:")));
    }

    /**
     * 获取安装应用程序的包
     *
     * @param context
     * @return
     */
    public static List<String> getInstallPackageName(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(FileUtil.SDCARD)), "application/vnd.android.package-archive");
        return queryIntentActivities(context, intent);
    }

    public static List<String> queryIntentActivities(Context context, Intent intent) {
        List<String> list = new ArrayList<String>();
        List<ResolveInfo> resolveInfos = PackageManagerLocker.getInstance().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            list.add(resolveInfo.activityInfo.packageName);
        }
        return list.isEmpty() ? null : list;
    }

    /**
     * 根据包名获取应用Icon
     */
    public static Bitmap loadAppIcon(Context context, String appPackageName) {
        Bitmap bitmap = null;
        BitmapDrawable drawable = (BitmapDrawable) getApplicationDrawable(
                context, appPackageName);
        if (drawable != null) {
            bitmap = drawable.getBitmap();
        } else if (drawable == null) {
            // 兼容未安装应用
            BitmapDrawable drawable2 = (BitmapDrawable) getApplicationDrawableIfNotInstalled(
                    context, appPackageName);
            if (drawable2 != null) {
                bitmap = drawable2.getBitmap();
            } else {
                BitmapDrawable drawable3 = (BitmapDrawable) context
                        .getResources().getDrawable(
                                R.drawable.common_default_app_icon);
                bitmap = drawable3.getBitmap();
            }
        }
        return bitmap;
    }


    private static Drawable getApplicationDrawable(Context context,
                                                   String pkgName) {
        Drawable drawable = null;
        drawable = PackageManagerLocker.getInstance().getApplicationIcon(pkgName);
        if (!(drawable instanceof BitmapDrawable)) {
            drawable = null;
        }
        return drawable;
    }

    private static Drawable getApplicationDrawableIfNotInstalled(
            Context context, String path) {
        try {
            PackageInfo packageInfo = PackageManagerLocker.getInstance().getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES);

            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                appInfo.sourceDir = path;
                appInfo.publicSourceDir = path;
                try {
                    return PackageManagerLocker.getInstance().getApplicationIcon(appInfo);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取应用ICON图标
     *
     * @param context
     * @param pkgInfo
     * @return
     */
    public static Drawable getIconByPkgInfo(Context context, PackageInfo pkgInfo) {
        if (pkgInfo != null) {
            return PackageManagerLocker.getInstance().getApplicationIcon(pkgInfo.applicationInfo);
        }
        return null;
    }

    /**
     * 打开软键盘<br>
     *
     * @param context
     * @param editText
     */
    public static void showSoftInputFromWindow(Context context, EditText editText) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 关闭软键盘<br>
     *
     * @param context
     * @param editText
     */
    public static void hideSoftInputFromWindow(Context context, EditText editText) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 获取在功能菜单出现的程序列表
     *
     * @param context 上下文
     * @return 程序列表，类型是 List<ResolveInfo>
     */
    public static List<ResolveInfo> getLauncherApps(Context context) {
        List<ResolveInfo> infos = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        try {
            infos = PackageManagerLocker.getInstance().queryIntentActivities(intent, 0);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    public static void gotoLauncherWithoutChoice(Context context, String usePkgname) {
        try {
            Intent intent = null;
            String launcher = getDefaultLauncher(context);
            if (null == launcher && !android.text.TextUtils.isEmpty(usePkgname)) {
                launcher = usePkgname;
            }
            if (null != launcher) {
                Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
                intentToResolve.addCategory(Intent.CATEGORY_HOME);
                intentToResolve.setPackage(launcher);
                ResolveInfo ri = PackageManagerLocker.getInstance().resolveActivity(intentToResolve, 0);
                if (ri != null) {
                    intent = new Intent(intentToResolve);
                    intent.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                } else {
                    intent = PackageManagerLocker.getInstance().getLaunchIntentForPackage(launcher);
                    if (null == intent) {
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.setPackage(launcher);
                    }
                }
            } else {
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前是否亮屏(跟ACTION_SCREEN_ON的意义一样，when the device wakes up and becomes interactive)
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static boolean isScreenOn(Context context) {
        if (Machine.HAS_SDK_KITKAT_WATCH) {
            // If you use API20 or more:
            DisplayManager dm = (DisplayManager) context
                    .getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    return true;
                }
            }
            return false;
        }
        // If you use less than API20:
        PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * 获取Strings资源文件
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return TheApplication.getAppContext().getResources().getString(resId);
    }

    /**
     * 获取Strings资源文件
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        return TheApplication.getAppContext().getResources().getColor(resId);
    }
}

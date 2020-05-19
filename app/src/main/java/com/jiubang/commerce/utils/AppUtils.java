package com.jiubang.commerce.utils;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppUtils {
    public static final int MAX_INSTALL_APP = 50;
    public static final long OBTAIN_TIME = 14400000;
    private static String sInstallInfoStr = "";
    private static long sObtainTime = 0;

    public static boolean isAppExist(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(packageName, 1024);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e2) {
            return false;
        }
    }

    public static int getAppVersionCode(Context context, String packageName) {
        int versionCode = -1;
        if (context == null || packageName == null) {
            return -1;
        }
        try {
            versionCode = context.getPackageManager().getPackageInfo(packageName, 1024).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("Ad_SDK", "Error :" + packageName + " is not exist.");
        } catch (Exception e2) {
        }
        int i = versionCode;
        return versionCode;
    }

    public static String getAppVersionName(Context context, String packageName) {
        String versionName = "";
        if (context == null || packageName == null) {
            String str = versionName;
            return versionName;
        }
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 1024).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("Ad_SDK", "Error :" + packageName + " is not exist.");
        } catch (Exception e2) {
        }
        String str2 = versionName;
        return versionName;
    }

    public static String getAppLabel(Context context, String packageName) {
        if (packageName == null) {
            return null;
        }
        try {
            PackageManager pkManager = context.getPackageManager();
            ApplicationInfo info = pkManager.getApplicationInfo(packageName, 0);
            if (info != null) {
                return pkManager.getApplicationLabel(info).toString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPackageName(Intent intent) {
        ComponentName cn;
        if (intent == null || (cn = intent.getComponent()) == null) {
            return null;
        }
        return cn.getPackageName();
    }

    public static List<ResolveInfo> getLauncherApps(Context context) {
        PackageManager packageMgr = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        return packageMgr.queryIntentActivities(intent, 0);
    }

    public static List<PackageInfo> getLauncherableApps(Context context) {
        PackageManager pm = context.getPackageManager();
        String ourPackageName = context.getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<>();
        for (PackageInfo info : packages) {
            if (pm.getLaunchIntentForPackage(info.applicationInfo.packageName) != null && !ourPackageName.equals(info.packageName)) {
                apps.add(info);
            }
        }
        return apps;
    }

    public static boolean isAppRunningInForground(Context context, String pkgName) {
        if (SystemUtils.IS_SDK_ABOVE_L) {
            return isForgroundApp(context, pkgName);
        }
        return isTopActivity(context, pkgName);
    }

    public static String getTopAppPackageName(Context context) {
        try {
            List<ActivityManager.RunningTaskInfo> tasksInfo = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                return tasksInfo.get(0).topActivity.getPackageName();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static boolean isTopActivity(Context context, String packageName) {
        try {
            List<ActivityManager.RunningTaskInfo> tasksInfo = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            if (tasksInfo.size() > 0 && packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isForgroundApp(Context context, String packageName) {
        try {
            List<ActivityManager.RunningAppProcessInfo> appProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == 100 && (appProcess.processName.equals(packageName) || Arrays.asList(appProcess.pkgList).contains(packageName))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getPidByProcessName(Context context, String processName) {
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
            if (runningAppProcessInfo.processName.equals(processName)) {
                return runningAppProcessInfo.pid;
            }
        }
        return 0;
    }

    public static void killProcess() {
        killProcess(Process.myPid());
    }

    public static void killProcess(int pid) {
        new Exception().printStackTrace();
        Process.killProcess(pid);
    }

    public static String getSelfTopActivityName(Context context) {
        try {
            List<ActivityManager.RunningTaskInfo> tasksInfo = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                return tasksInfo.get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getDefaultLauncher(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 65536);
        if (res.activityInfo != null && !res.activityInfo.packageName.equals("android")) {
            return res.activityInfo.packageName;
        }
        return null;
    }

    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 1);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
            }
        }
        return null;
    }

    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setFlags(268435456);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
            return;
        }
        ToastUtils.makeEventToast(context, "Can not find APK!", false);
    }

    public static void uninstallApk(Context context, String pkgName) {
        Intent uninstallIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + pkgName));
        if (!"Xiaomi".equals(Build.BRAND)) {
            uninstallIntent.setFlags(1073741824);
        }
        context.startActivity(uninstallIntent);
    }

    public static List<PackageInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        String ourPackageName = context.getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<>();
        for (PackageInfo info : packages) {
            if ((info.applicationInfo.flags & 1) == 0 && !ourPackageName.equals(info.packageName)) {
                apps.add(info);
            }
        }
        return apps;
    }

    public static List<PackageInfo> getAllInstalledApps(Context context) {
        return context.getPackageManager().getInstalledPackages(0);
    }

    public static PackageInfo getAppPackageInfo(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean safeStartActivity(Context context, String packageName) {
        PackageInfo packageInfo;
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(packageName)) {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (intent == null && (packageInfo = getAppPackageInfo(context, packageName)) != null && !TextUtils.isEmpty(packageInfo.applicationInfo.className)) {
                        intent = new Intent();
                        intent.addFlags(268435456);
                        intent.setClassName(packageName, packageInfo.applicationInfo.className);
                    }
                    if (intent != null) {
                        context.startActivity(intent);
                        return true;
                    }
                }
            } catch (ActivityNotFoundException | Exception e) {
            }
        }
        return false;
    }

    public static synchronized String getInstallAppInfoWithoutSys(Context context) {
        String str;
        synchronized (AppUtils.class) {
            if (context == null) {
                str = null;
            } else {
                long now = System.currentTimeMillis();
                if (sInstallInfoStr.equals("") || sObtainTime == 0 || now - sObtainTime > 14400000) {
                    String str2 = "";
                    try {
                        List<PackageInfo> appInfoList = context.getPackageManager().getInstalledPackages(0);
                        if (appInfoList != null) {
                            int count = 0;
                            for (int i = 0; i < appInfoList.size(); i++) {
                                PackageInfo packageInfo = appInfoList.get(i);
                                if ((packageInfo.applicationInfo.flags & 1) == 0) {
                                    if (LogUtils.isShowLog()) {
                                        LogUtils.d("", i + "   :   " + packageInfo.packageName);
                                    }
                                    str2 = str2 + packageInfo.packageName + ",";
                                    count++;
                                    if (count >= 50) {
                                        break;
                                    }
                                }
                            }
                        }
                        sInstallInfoStr = str2;
                    } catch (Throwable thr) {
                        LogUtils.w("Ad_SDK", "AppUtils.getInstallAppInfoWithoutSys error", thr);
                    }
                    str = sInstallInfoStr;
                } else {
                    str = sInstallInfoStr;
                }
            }
        }
        return str;
    }

    public static String getCurrProcessName(Context context) {
        if (context != null) {
            int pid = Process.myPid();
            try {
                for (ActivityManager.RunningAppProcessInfo appProcess : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
                    if (appProcess.pid == pid) {
                        return appProcess.processName;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

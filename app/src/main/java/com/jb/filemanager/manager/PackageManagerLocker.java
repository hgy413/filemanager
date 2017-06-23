package com.jb.filemanager.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;

import com.jb.filemanager.TheApplication;

import java.io.File;
import java.util.List;

/**
 * Created by bill wang on 17/2/4.
 *
 */

public class PackageManagerLocker {

    private static PackageManagerLocker sInstance;
    private PackageManager mPM;

    private volatile PackageInfo mPackageInfo = null;
    private volatile PackageInfo mPackageArchiveInfo = null;
    private volatile ApplicationInfo mApplicationInfo = null;
    private volatile Drawable mApplicationIcon = null;
    private volatile List<PackageInfo> mInstalledPackages = null;
    private volatile List<ApplicationInfo> mInstalledApplications = null;
    private volatile List<ResolveInfo> mIntentActivities = null;
    private volatile CharSequence mApplicationLabel = null;
    private volatile ActivityInfo mActivityInfo = null;
    private volatile String[] mPackagesForUid = null;
    private volatile ResolveInfo mResolveInfo = null;
    private volatile Intent mLaunchForPackage = null;

    public static PackageManagerLocker getInstance() {
        synchronized (PackageManagerLocker.class) {
            if (sInstance == null) {
                sInstance = new PackageManagerLocker();
            }
            return sInstance;
        }
    }

    private PackageManagerLocker() {
        mPM = TheApplication.getInstance().getPackageManager();
    }

    public PackageManager getPackageManager() {
        return mPM;
    }

    public PackageInfo getPackageInfo(final String packageName, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mPackageInfo = doGetPackageInfo(packageName, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            PackageInfo result = mPackageInfo;
            mPackageInfo = null;
            return result;
        }

//        synchronized (this) {
//            PackageInfo result = null;
//            for (int i = 0; i < 3; i++) {
//                try {
//                    result = doGetPackageInfo(packageName, flag);
//                    break;
//                } catch (Exception nameNotFoundException) {
//                    nameNotFoundException.printStackTrace();
//                }
//            }
//            return result;
//        }
    }

    private PackageInfo doGetPackageInfo(String packageName, int flag) throws PackageManager.NameNotFoundException {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getPackageInfo(packageName, flag);
    }

    public List<PackageInfo> getInstalledPackages(final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mInstalledPackages = doGetInstalledPackages(flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<PackageInfo> result = mInstalledPackages;
            mInstalledPackages = null;
            return result;
        }

//        long threadId = Thread.currentThread().getId();
//        Logger.e("wangzq", "getInstalledPackages in" + String.valueOf(threadId));
//        synchronized (this) {
//            Logger.e("wangzq", "getInstalledPackages sync in" + String.valueOf(threadId));
//            List<PackageInfo> result = null;
//            for (int i = 0; i < 3; i++) {
//                try {
//                    result = doGetInstalledPackages(flag);
//                    break;
//                } catch (Exception nameNotFoundException) {
//                    nameNotFoundException.printStackTrace();
//                }
//            }
//            Logger.e("wangzq", "getInstalledPackages sync out" + String.valueOf(threadId));
//            return result;
//        }
    }

    private List<PackageInfo> doGetInstalledPackages(int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getInstalledPackages(flag);
    }

    public List<ApplicationInfo> getInstalledApplications(final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mInstalledApplications = doGetInstalledApplications(flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ApplicationInfo> result = mInstalledApplications;
            mInstalledApplications = null;
            return result;
        }

//        long threadId = Thread.currentThread().getId();
//        Logger.e("wangzq", "getInstalledPackages in" + String.valueOf(threadId));
//        synchronized (this) {
//            Logger.e("wangzq", "getInstalledPackages sync in" + String.valueOf(threadId));
//            List<PackageInfo> result = null;
//            for (int i = 0; i < 3; i++) {
//                try {
//                    result = doGetInstalledPackages(flag);
//                    break;
//                } catch (Exception nameNotFoundException) {
//                    nameNotFoundException.printStackTrace();
//                }
//            }
//            Logger.e("wangzq", "getInstalledPackages sync out" + String.valueOf(threadId));
//            return result;
//        }
    }

    private List<ApplicationInfo> doGetInstalledApplications(int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getInstalledApplications(flag);
    }

    public List<ResolveInfo> queryIntentActivities(final Intent intent, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mIntentActivities = doQueryIntentActivities(intent, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<ResolveInfo> result = mIntentActivities;
            mIntentActivities = null;
            return result;
        }
    }

    private List<ResolveInfo> doQueryIntentActivities(Intent intent, int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.queryIntentActivities(intent, flag);
    }

    public ApplicationInfo getApplicationInfo(final String packageName, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mApplicationInfo = doGetApplicationInfo(packageName, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ApplicationInfo result = mApplicationInfo;
            mApplicationInfo = null;
            return result;
        }
    }

    private ApplicationInfo doGetApplicationInfo(String packageName, int flag) throws PackageManager.NameNotFoundException {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getApplicationInfo(packageName, flag);
    }

    public PackageInfo getPackageArchiveInfo(final String filePath, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mPackageArchiveInfo = doGetPackageArchiveInfo(filePath, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            PackageInfo result = mPackageArchiveInfo;
            mPackageArchiveInfo = null;
            return result;
        }
    }

    private PackageInfo doGetPackageArchiveInfo(String filePath, int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getPackageArchiveInfo(filePath, flag);
    }

    public CharSequence getApplicationLabel(final ApplicationInfo applicationInfo) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mApplicationLabel = doGetApplicationLabel(applicationInfo);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CharSequence result = mApplicationLabel;
            mApplicationLabel = null;
            return result;
        }
    }

    private CharSequence doGetApplicationLabel(ApplicationInfo applicationInfo) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getApplicationLabel(applicationInfo);
    }

    public Drawable getApplicationIcon(final String packageName) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mApplicationIcon = doGetApplicationIcon(packageName);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Drawable result = mApplicationIcon;
            mApplicationIcon = null;
            return result;
        }
    }

    public Drawable getApplicationIconByPath(final String apkFilePath, final int width, final int height) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    if (!TextUtils.isEmpty(apkFilePath) && new File(apkFilePath).exists()) {
                        Context context = TheApplication.getInstance();
                        if (mPM == null) {
                            mPM = context.getPackageManager();
                        }
                        PackageInfo packageInfo = mPM.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
                        if (packageInfo != null) {
                            ApplicationInfo appInfo = packageInfo.applicationInfo;

                            for (int i = 0; i < 3; i++) {
                                try {
                                    Drawable icon = doGetApplicationIcon(appInfo);
                                    Bitmap b = ((BitmapDrawable) icon).getBitmap();
                                    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
                                    mApplicationIcon = new BitmapDrawable(context.getResources(), bitmapResized);
                                    break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            appInfo.sourceDir = apkFilePath;
                            appInfo.publicSourceDir = apkFilePath;
                        }
                    }

                }
            });
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Drawable result = mApplicationIcon;
            mApplicationIcon = null;
            return result;
        }
    }

    private Drawable doGetApplicationIcon(String packageName) throws PackageManager.NameNotFoundException {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getApplicationIcon(packageName);
    }

    public ActivityInfo getActivityInfo(final ComponentName componentName, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mActivityInfo = doGetActivityInfo(componentName, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ActivityInfo result = mActivityInfo;
            mActivityInfo = null;
            return result;
        }
    }

    private ActivityInfo doGetActivityInfo(ComponentName componentName, int flag) throws PackageManager.NameNotFoundException {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getActivityInfo(componentName, flag);
    }

    public String[] getPackagesForUid(final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mPackagesForUid = doGetPackagesForUid(flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] result = mPackagesForUid;
            mPackagesForUid = null;
            return result;
        }
    }

    private String[] doGetPackagesForUid(int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getPackagesForUid(flag);
    }

    public Drawable getApplicationIcon(final ApplicationInfo applicationInfo) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mApplicationIcon = doGetApplicationIcon(applicationInfo);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Drawable result = mApplicationIcon;
            mApplicationIcon = null;
            return result;
        }
    }

    private Drawable doGetApplicationIcon(ApplicationInfo applicationInfo) throws PackageManager.NameNotFoundException {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getApplicationIcon(applicationInfo);
    }

    public ResolveInfo resolveActivity(final Intent intent, final int flag) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mResolveInfo = doResolveActivity(intent, flag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResolveInfo result = mResolveInfo;
            mResolveInfo = null;
            return result;
        }
    }

    private ResolveInfo doResolveActivity(Intent intent, int flag) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.resolveActivity(intent, flag);
    }

    public Intent getLaunchIntentForPackage(final String launch) {
        synchronized (this) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mLaunchForPackage = doGetLaunchIntentForPackage(launch);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent result = mLaunchForPackage;
            mLaunchForPackage = null;
            return result;
        }
    }

    private Intent doGetLaunchIntentForPackage(String launch) {
        if (mPM == null) {
            mPM = TheApplication.getInstance().getPackageManager();
        }
        return mPM.getLaunchIntentForPackage(launch);
    }

    public void run(Runnable runnable) {
        synchronized (this) {
            Thread thread = new Thread(runnable);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
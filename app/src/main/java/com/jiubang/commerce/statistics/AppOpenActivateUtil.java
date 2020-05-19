package com.jiubang.commerce.statistics;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.jiubang.commerce.ad.notification.ActivationGuideNotification;
import com.jiubang.commerce.preferences.PreferencesManager;
import com.jiubang.commerce.receiver.AppBroadcastReceiver;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.SystemUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AppOpenActivateUtil {
    private static final long CHECK_FREQUENCY = 5000;
    private static final long CHECK_VALID_DURATION = 10000;
    private static final long INSTALL_VALID_DURATION = 86400000;
    public static final boolean IS_OPEN_ACTIVATE_FUNC = false;
    private static final String PACKAGE_NAME_VALUE = "packagename";
    private static final String SHARE_PREFERENCE_APP_OPEN_ACTIVATE = "share_preference_app_open_activate";
    private static final long START_CHECK_DELAY_TIME = 0;
    private static AppOpenActivateUtil sInstance;
    private ActivateAppCheckTimerTask mAACTimerTask;
    /* access modifiers changed from: private */
    public ActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private BroadcastReceiver mInstallReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getData().getSchemeSpecificPart();
            if (!"android.intent.action.PACKAGE_ADDED".equals(action)) {
                if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                }
            } else if (AppOpenActivateUtil.this.isOpenValid(packageName)) {
                boolean needToStartServer = false;
                synchronized (AppOpenActivateUtil.this.mNeedCheckAppsLock) {
                    if (AppOpenActivateUtil.this.mNeedCheckApps.isEmpty()) {
                        needToStartServer = true;
                    }
                    AppOpenActivateUtil.this.mNeedCheckApps.add(packageName);
                }
                if (needToStartServer) {
                    AppOpenActivateUtil.this.startMonitor();
                }
                AppOpenActivateUtil.this.setInstallAppTime(packageName);
            }
        }
    };
    /* access modifiers changed from: private */
    public String mLastOpenPackageName;
    /* access modifiers changed from: private */
    public List<String> mNeedCheckApps;
    /* access modifiers changed from: private */
    public byte[] mNeedCheckAppsLock = new byte[0];
    private PreferencesManager mPreferenceManager;
    private ScreenBrocastReceiver mScreenReceiver;
    private TimeSetReceiver mTimeSetReceiver;
    private Timer mTimer;
    /* access modifiers changed from: private */
    public int mUsedTotalTime;

    static /* synthetic */ int access$1014(AppOpenActivateUtil x0, long x1) {
        int i = (int) (((long) x0.mUsedTotalTime) + x1);
        x0.mUsedTotalTime = i;
        return i;
    }

    private AppOpenActivateUtil(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mTimer = new Timer();
        this.mAACTimerTask = new ActivateAppCheckTimerTask();
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mPreferenceManager = new PreferencesManager(this.mContext, SHARE_PREFERENCE_APP_OPEN_ACTIVATE, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme(AppBroadcastReceiver.DATA_SCHEME);
        this.mContext.registerReceiver(this.mInstallReceiver, filter);
        initData();
    }

    private void initData() {
        this.mNeedCheckApps = new ArrayList();
        Map<String, ?> allDatas = this.mPreferenceManager.getAll();
        for (String key : allDatas.keySet()) {
            if (PACKAGE_NAME_VALUE.equals(allDatas.get(key)) && isOpenValid(key)) {
                this.mNeedCheckApps.add(key);
            }
        }
        this.mLastOpenPackageName = "";
        synchronized (this.mNeedCheckAppsLock) {
            if (!this.mNeedCheckApps.isEmpty()) {
                startMonitor();
            }
        }
    }

    public static synchronized AppOpenActivateUtil getInstance(Context context) {
        AppOpenActivateUtil appOpenActivateUtil;
        synchronized (AppOpenActivateUtil.class) {
            if (sInstance == null) {
                sInstance = new AppOpenActivateUtil(context);
            }
            appOpenActivateUtil = sInstance;
        }
        return appOpenActivateUtil;
    }

    /* access modifiers changed from: private */
    public void startMonitor() {
        startScreenBroadcastReceiver();
        registerTimeSetReceiver();
        startTimer();
    }

    public void stopMonitor() {
        stopScreenBroadcastReceiver();
        unregisterTimeSetReceiver();
        stopTimer();
    }

    private void registerTimeSetReceiver() {
        if (this.mTimeSetReceiver == null) {
            this.mTimeSetReceiver = new TimeSetReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        this.mContext.registerReceiver(this.mTimeSetReceiver, filter);
    }

    private void unregisterTimeSetReceiver() {
        if (this.mTimeSetReceiver != null) {
            this.mContext.unregisterReceiver(this.mTimeSetReceiver);
            this.mTimeSetReceiver = null;
        }
    }

    private void startScreenBroadcastReceiver() {
        if (this.mScreenReceiver == null) {
            this.mScreenReceiver = new ScreenBrocastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction(ActivationGuideNotification.CLICK_ACTIVATION_GUIDE_NOTIFICATION_ACTION);
        this.mContext.registerReceiver(this.mScreenReceiver, filter);
    }

    private void stopScreenBroadcastReceiver() {
        try {
            this.mContext.unregisterReceiver(this.mScreenReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.mScreenReceiver = null;
        }
    }

    /* access modifiers changed from: private */
    public void startTimer() {
        stopTimer();
        if (this.mAACTimerTask == null) {
            this.mAACTimerTask = new ActivateAppCheckTimerTask();
        }
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        if (this.mTimer != null && this.mAACTimerTask != null) {
            this.mTimer.schedule(this.mAACTimerTask, 0, CHECK_FREQUENCY);
        }
    }

    /* access modifiers changed from: private */
    public void stopTimer() {
        if (this.mAACTimerTask != null) {
            this.mAACTimerTask.cancel();
            this.mAACTimerTask = null;
        }
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }

    /* access modifiers changed from: private */
    public void removeCheckedApp(String packageName) {
        synchronized (this.mNeedCheckAppsLock) {
            if (this.mNeedCheckApps.contains(packageName)) {
                this.mNeedCheckApps.remove(packageName);
                removeInvalidData(packageName);
            }
            if (this.mNeedCheckApps.isEmpty()) {
                stopMonitor();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isAboveAndroidL() {
        return SystemUtils.IS_SDK_ABOVE_L;
    }

    class ActivateAppCheckTimerTask extends TimerTask {
        ActivateAppCheckTimerTask() {
        }

        public void run() {
            if (AppOpenActivateUtil.this.mActivityManager != null) {
                String pkgName = "";
                if (AppOpenActivateUtil.this.isAboveAndroidL()) {
                    synchronized (AppOpenActivateUtil.this.mNeedCheckAppsLock) {
                        Iterator i$ = AppOpenActivateUtil.this.mNeedCheckApps.iterator();
                        while (true) {
                            if (!i$.hasNext()) {
                                break;
                            }
                            String packageName = (String) i$.next();
                            if (AppUtils.isAppRunningInForground(AppOpenActivateUtil.this.mContext, packageName)) {
                                pkgName = packageName;
                                break;
                            }
                        }
                    }
                } else if (!AppOpenActivateUtil.this.mActivityManager.getRunningTasks(1).isEmpty()) {
                    pkgName = AppOpenActivateUtil.this.mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                }
                Log.e("sunxiaodong", "AppOpenActivateUtil--ActivateAppCheckTimerTask--each++pkgName：" + pkgName);
                if (!AppOpenActivateUtil.this.isOpenValid(pkgName) || !AppOpenActivateUtil.this.isActivateValid(pkgName)) {
                    AppOpenActivateUtil.this.removeCheckedApp(pkgName);
                    int unused = AppOpenActivateUtil.this.mUsedTotalTime = 0;
                    String unused2 = AppOpenActivateUtil.this.mLastOpenPackageName = "";
                } else if (pkgName.equals(AppOpenActivateUtil.this.mLastOpenPackageName)) {
                    AppOpenActivateUtil.access$1014(AppOpenActivateUtil.this, AppOpenActivateUtil.CHECK_FREQUENCY);
                    if (((long) AppOpenActivateUtil.this.mUsedTotalTime) >= 10000) {
                        synchronized (AppOpenActivateUtil.this.mNeedCheckAppsLock) {
                            if (AppOpenActivateUtil.this.mNeedCheckApps.contains(pkgName)) {
                                AdSdkOperationStatistic.uploadAdActiveStaticstic(AppOpenActivateUtil.this.mContext, pkgName);
                                AppOpenActivateUtil.this.removeCheckedApp(pkgName);
                            }
                        }
                        int unused3 = AppOpenActivateUtil.this.mUsedTotalTime = 0;
                        String unused4 = AppOpenActivateUtil.this.mLastOpenPackageName = "";
                    }
                } else {
                    int unused5 = AppOpenActivateUtil.this.mUsedTotalTime = 0;
                    String unused6 = AppOpenActivateUtil.this.mLastOpenPackageName = pkgName;
                }
            }
        }
    }

    private String getDownloadPkgKey(String packageName) {
        return packageName + "_download";
    }

    private String getInstallPkgKey(String packageName) {
        return packageName + "_install";
    }

    /* access modifiers changed from: private */
    public boolean isActivateValid(String packageName) {
        long now = System.currentTimeMillis();
        if (now - this.mPreferenceManager.getLong(getDownloadPkgKey(packageName), 0) > now - this.mPreferenceManager.getLong(getInstallPkgKey(packageName), 0)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isOpenValid(String packageName) {
        if (System.currentTimeMillis() - this.mPreferenceManager.getLong(getDownloadPkgKey(packageName), 0) > 86400000) {
            return false;
        }
        return true;
    }

    private void removeInvalidData(String packageName) {
        this.mPreferenceManager.remove(packageName);
        this.mPreferenceManager.remove(getDownloadPkgKey(packageName));
        this.mPreferenceManager.remove(getInstallPkgKey(packageName));
    }

    public void setDownloadAppTime(String packageName) {
        this.mPreferenceManager.putLong(getDownloadPkgKey(packageName), System.currentTimeMillis());
        this.mPreferenceManager.commit();
    }

    /* access modifiers changed from: private */
    public void setInstallAppTime(String packageName) {
        this.mPreferenceManager.putString(packageName, PACKAGE_NAME_VALUE);
        this.mPreferenceManager.putLong(getInstallPkgKey(packageName), System.currentTimeMillis());
        this.mPreferenceManager.commit();
    }

    private class ScreenBrocastReceiver extends BroadcastReceiver {
        private String mAction;

        private ScreenBrocastReceiver() {
            this.mAction = null;
        }

        public void onReceive(Context context, Intent intent) {
            this.mAction = intent.getAction();
            if ("android.intent.action.SCREEN_ON".equals(this.mAction)) {
                new AdSdkThread(new Runnable() {
                    public void run() {
                        AppOpenActivateUtil.this.startTimer();
                    }
                }).start();
            } else if ("android.intent.action.SCREEN_OFF".equals(this.mAction)) {
                new AdSdkThread(new Runnable() {
                    public void run() {
                        AppOpenActivateUtil.this.stopTimer();
                    }
                }).start();
            } else if (ActivationGuideNotification.CLICK_ACTIVATION_GUIDE_NOTIFICATION_ACTION.equals(this.mAction)) {
                ActivationGuideNotification.clickNotification(AppOpenActivateUtil.this.mContext, intent);
            }
        }
    }

    private class TimeSetReceiver extends BroadcastReceiver {
        private TimeSetReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
                AppOpenActivateUtil.this.stopTimer();
                AppOpenActivateUtil.this.startTimer();
            }
        }
    }

    public void onDestroy() {
        stopMonitor();
    }
}

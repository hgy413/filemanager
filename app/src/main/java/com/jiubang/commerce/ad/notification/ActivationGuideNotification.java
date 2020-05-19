package com.jiubang.commerce.ad.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.database.model.WaitActivationAppInfoBean;
import com.jiubang.commerce.database.table.WaitActivationAppTable;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivationGuideNotification {
    public static final String APP_PACKAGENAME_KEY = "app_packageName_key";
    private static final long CHECK_TIME_INTERVAL = 3600000;
    public static final String CLICK_ACTIVATION_GUIDE_NOTIFICATION_ACTION = "com.jiubang.adsdk.click.activation.guide.notification.action";
    private static final long SHOW_ACTIVATION_GUIDE_NOTIFICATION_DURATION = 86400000;
    private static long sLastCheckTime;

    public static void checkNotification(Context context) {
        if (System.currentTimeMillis() - sLastCheckTime >= 3600000) {
            if (!isShowNotification()) {
                WaitActivationAppTable.getInstance(context).deleteInvalidWaitActivationAppData((String) null);
                sLastCheckTime = System.currentTimeMillis();
                return;
            }
            WaitActivationAppInfoBean whereInfoBean = new WaitActivationAppInfoBean();
            whereInfoBean.setInstallTime(Long.valueOf(System.currentTimeMillis() - 86400000));
            List<WaitActivationAppInfoBean> appInfoList = WaitActivationAppTable.getInstance(context).getWaitActivationAppData(whereInfoBean);
            if (appInfoList == null || appInfoList.isEmpty()) {
                sLastCheckTime = System.currentTimeMillis();
                return;
            }
            for (WaitActivationAppInfoBean appInfoBean : appInfoList) {
                if (appInfoBean != null) {
                    WaitActivationAppTable.getInstance(context).deleteInvalidWaitActivationAppData(appInfoBean.getPackageName());
                    Map<Integer, String> preActivateDataMap = AdSdkOperationStatistic.getPreActivateData(context, appInfoBean.getPackageName());
                    if (preActivateDataMap != null && !preActivateDataMap.isEmpty()) {
                        showOpenAppNotification(context, appInfoBean.getPackageName());
                    }
                }
            }
            sLastCheckTime = System.currentTimeMillis();
        }
    }

    private static boolean isShowNotification() {
        return true;
    }

    public static void showOpenAppNotification(Context context, String packageName) {
        PackageInfo packageInfo;
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "ActivationGuideNotification.showOpenAppNotification(" + packageName + ")");
        }
        if (!TextUtils.isEmpty(packageName) && context != null) {
            try {
                Map<Integer, String> preActivateDataMap = AdSdkOperationStatistic.getPreActivateData(context, packageName);
                if (preActivateDataMap != null && preActivateDataMap.size() > 0 && (packageInfo = AppUtils.getAppPackageInfo(context, packageName)) != null && packageInfo.applicationInfo != null) {
                    PackageManager packageManager = context.getPackageManager();
                    String appName = StringUtils.toString(packageInfo.applicationInfo.loadLabel(packageManager));
                    Drawable appIconDrawable = packageInfo.applicationInfo.loadIcon(packageManager);
                    ResourcesProvider resourcesProvider = ResourcesProvider.getInstance(context);
                    Notification notification = new Notification(resourcesProvider.getDrawableId("default_icon"), appName, System.currentTimeMillis());
                    RemoteViews contentView = new RemoteViews(context.getPackageName(), resourcesProvider.getLayoutId("ad_notification_open_app_layout"));
                    if (appIconDrawable != null) {
                        contentView.setImageViewBitmap(resourcesProvider.getId("image"), ((BitmapDrawable) appIconDrawable).getBitmap());
                    } else {
                        contentView.setImageViewResource(resourcesProvider.getId("image"), resourcesProvider.getDrawableId("default_icon"));
                    }
                    contentView.setTextViewText(resourcesProvider.getId("title"), appName);
                    contentView.setTextViewText(resourcesProvider.getId("text"), resourcesProvider.getString("ad_notification_message_open_app"));
                    notification.contentView = contentView;
                    notification.tickerText = appName;
                    Intent intent = new Intent(CLICK_ACTIVATION_GUIDE_NOTIFICATION_ACTION);
                    intent.putExtra(APP_PACKAGENAME_KEY, packageName);
                    notification.contentIntent = PendingIntent.getBroadcast(context, 0, intent, 134217728);
                    notification.flags = 16;
                    ((NotificationManager) context.getSystemService("notification")).notify(packageName.hashCode(), notification);
                    AdSdkOperationStatistic.uploadAdShowActivationGuideStaticstic(context, AdSdkOperationStatistic.ACTIVATION_GUIDE_NOTIFICATION_NT_F000, preActivateDataMap.get(1), preActivateDataMap.get(6), preActivateDataMap.get(8), packageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clickNotification(Context context, Intent intent) {
        String packageName = intent.getStringExtra(APP_PACKAGENAME_KEY);
        if (!TextUtils.isEmpty(packageName)) {
            AppUtils.safeStartActivity(context, packageName);
            Map<Integer, String> preActivateDataMap = AdSdkOperationStatistic.getPreActivateData(context, packageName);
            if (preActivateDataMap != null && preActivateDataMap.size() > 0) {
                AdSdkOperationStatistic.uploadAdActivationGuideBtnClickStaticstic(context, AdSdkOperationStatistic.ACTIVATION_GUIDE_NOTIFICATION_NT_A000, preActivateDataMap.get(1), preActivateDataMap.get(6), preActivateDataMap.get(8), packageName);
            } else {
                return;
            }
        }
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "ActivationGuideNotification.clickNotification(" + packageName + ")");
        }
    }

    public static void saveToWaitActivationList(Context context, String packageName) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            List<WaitActivationAppInfoBean> waitActivationAppInfoList = new ArrayList<>();
            waitActivationAppInfoList.add(new WaitActivationAppInfoBean(packageName, System.currentTimeMillis()));
            boolean flag = WaitActivationAppTable.getInstance(context).insertWaitActivationAppData(waitActivationAppInfoList);
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "ActivationGuideNotification.saveToWaitActivationList(" + flag + ", " + packageName + ")");
            }
        }
    }
}

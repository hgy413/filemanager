package com.jiubang.commerce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.window.ActivationGuideWindowManager;
import com.jiubang.commerce.database.table.PkgRecordTable;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.statistics.adinfo.AdInfoStatistic;
import com.jiubang.commerce.statistics.adinfo.AppInstallMonitorTable;
import com.jiubang.commerce.statistics.adinfo.bean.AppInstallStatisInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class AppBroadcastReceiver extends BroadcastReceiver {
    public static final String DATA_SCHEME = "package";
    private static final String TAG = "hzw";
    private AppInstallMonitorTable mInstallMonitor;

    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "app changed onReceive");
        if (!AdSdkManager.isShieldAdSdk() && intent != null) {
            String packageName = intent.getDataString();
            int index = packageName.indexOf("package:");
            if (index >= 0) {
                packageName = packageName.substring("package:".length() + index);
            }
            if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                return;
            }
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
                if (AdSdkOperationStatistic.uploadAdInstallAppStatistic(context, packageName) && AdSdkManager.getInstance().isShowActivationGuideWindow()) {
                    ActivationGuideWindowManager.getInstance(context).showActivationGuideWindow(context, packageName);
                }
                PkgRecordTable.getInstance(context).insertData(packageName);
                statisticAdInstallInfo(context, packageName);
                return;
            }
            if ("android.intent.action.PACKAGE_REPLACED".equals(intent.getAction())) {
            }
        }
    }

    private void statisticAdInstallInfo(Context context, String s) {
        if (this.mInstallMonitor == null) {
            this.mInstallMonitor = new AppInstallMonitorTable(context);
        }
        LogUtils.i(TAG, "安装应用onAppInstalled：" + s);
        String jsonObjectString = this.mInstallMonitor.get(s);
        if (jsonObjectString != null) {
            try {
                AppInstallStatisInfo info = new AppInstallStatisInfo(new JSONObject(jsonObjectString));
                if (info.isValid()) {
                    LogUtils.i(TAG, "需要统计第一阶段安装");
                    AdInfoStatistic.uploadPhase1Install(context, info.mTitle, info.mBannerUrl, info.mDesc, info.mPkgName, info.mType);
                    if (info.mNeedPhase2Install) {
                        LogUtils.i(TAG, "需要统计第二阶段安装");
                        AdInfoStatistic.uploadPhase2Install(context, info.mTitle, info.mBannerUrl, info.mDesc, info.mPkgName, info.mType);
                    }
                } else {
                    LogUtils.i(TAG, "应用超时");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtils.i(TAG, "onAppInstalled:statistic>error");
            }
            this.mInstallMonitor.remove(s);
        }
    }
}

package com.jiubang.commerce.buychannel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.manager.ReferrerManager;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import java.net.URLDecoder;

public class GaTrackerReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER";
    private static final String EXTRA_REFERRER = "referrer";
    private static String lastReferrer;

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] intent = " + intent.toString());
            String referrer = intent.getStringExtra("referrer");
            LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] referrer = " + referrer);
            if (referrer == null || !referrer.equals(lastReferrer)) {
                lastReferrer = referrer;
                ReferrerManager refererManager = ReferrerManager.getInstance(context);
                if (refererManager.checkFirstReceiver()) {
                    StatisticsDebug.Statistic103Params statistic103Params = new StatisticsDebug.Statistic103Params();
                    statistic103Params.code(BuySdkConstants.DEBUG_CODE3).referrer(referrer);
                    StatisticsDebug.upload(context, statistic103Params);
                }
                if (!refererManager.checkFirstReceiver()) {
                    StatisticsDebug.Statistic103Params statistic103ParamsP1 = new StatisticsDebug.Statistic103Params();
                    statistic103ParamsP1.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(1);
                    StatisticsDebug.upload(context, statistic103ParamsP1);
                    LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] 不是第一次GA广播");
                } else if (BuyChannelUtils.isOldUser(context)) {
                    StatisticsDebug.Statistic103Params statistic103ParamsP2 = new StatisticsDebug.Statistic103Params();
                    statistic103ParamsP2.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(2);
                    StatisticsDebug.upload(context, statistic103ParamsP2);
                    LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] 已明确老用户");
                } else if (BuyChannelUtils.isOldApkBuy(context)) {
                    StatisticsDebug.Statistic103Params statistic103ParamsP3 = new StatisticsDebug.Statistic103Params();
                    statistic103ParamsP3.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(3);
                    StatisticsDebug.upload(context, statistic103ParamsP3);
                    LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] 已明确APK买量");
                } else {
                    try {
                        AppsFlyerProxy.getInstance().cancelupload15S(referrer, (String) null);
                        AppsFlyerProxy.getInstance().cancelUploadFirst45(referrer);
                        String refer = URLDecoder.decode(referrer, "utf-8");
                        String utmSource = refererManager.getUtmSource(refer);
                        LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] 接收第一次GA广播，referrer = " + referrer + ",utmSource=" + utmSource);
                        refererManager.analyseUtmSource(utmSource, refer);
                        refererManager.saveFirstReceiver();
                        refererManager.saveReferrer(referrer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("buychannelsdk", e.toString());
                        String utmSource2 = refererManager.getUtmSource(referrer);
                        LogUtils.i("buychannelsdk", "[GaTrackerReceiver::onReceive] 接收第一次GA广播，referrer = " + referrer + ",utmSource=" + utmSource2);
                        refererManager.analyseUtmSource(utmSource2, referrer);
                        refererManager.saveFirstReceiver();
                        refererManager.saveReferrer(referrer);
                    }
                }
            }
        }
    }
}

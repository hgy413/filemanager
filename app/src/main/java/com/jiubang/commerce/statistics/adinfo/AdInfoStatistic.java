package com.jiubang.commerce.statistics.adinfo;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.statistics.BaseSeq102OperationStatistic;
import java.util.HashMap;

public class AdInfoStatistic extends BaseSeq102OperationStatistic {
    private static final long AD_SHOW_STATISTIC_TIMEOUT = 900000;
    private static final String SEPARATOR_BATCH = "!!";
    private static final String SEPARATOR_CONTENT = ";;";
    /* access modifiers changed from: private */
    public static final HashMap<String, Runnable> mAdShowStatisticMap = new HashMap<>();

    public static void uploadAdInfo(Context context, String layoutInfo, int type) {
        uploadSqe102StatisticData(context, 646, layoutInfo, type, getProductId(), "adv_info");
    }

    public static void uploadAdUrl(Context context, String url, int type) {
        uploadSqe102StatisticData(context, 646, url, type, getProductId(), "adv_url");
    }

    public static void uploadPhase1Click(Context context, String title, String url, String desc, String pkgname, int type) {
        uploadSqe102StatisticData(context, 646, replaceAdInfo(title) + SEPARATOR_CONTENT + replaceAdInfo(url) + SEPARATOR_CONTENT + replaceAdInfo(desc) + SEPARATOR_CONTENT + replaceAdInfo(pkgname) + SEPARATOR_CONTENT + replaceAdInfo(GoHttpHeadUtil.getLanguage(context)), type, getProductId(), "phase1_click");
    }

    public static void uploadPhase1Install(Context context, String title, String url, String desc, String pkgname, int type) {
        uploadSqe102StatisticData(context, 646, replaceAdInfo(title) + SEPARATOR_CONTENT + replaceAdInfo(url) + SEPARATOR_CONTENT + replaceAdInfo(desc) + SEPARATOR_CONTENT + replaceAdInfo(pkgname) + SEPARATOR_CONTENT + replaceAdInfo(GoHttpHeadUtil.getLanguage(context)), type, getProductId(), "phase1_install");
    }

    public static void uploadPhase2ImpressionNow(Context context, String info) {
        uploadSqe102StatisticData(context, 646, info, 511, getProductId(), "phase2_impression");
    }

    public static void uploadPhase2Impression(final String adId, final Context context, String title, String url, String desc, int type) {
        AdInfoStatisticManager.getInstance(context).addAdShowStatistic(adId, replaceAdInfo(title) + SEPARATOR_CONTENT + replaceAdInfo(url) + SEPARATOR_CONTENT + replaceAdInfo(desc) + SEPARATOR_CONTENT + replaceAdInfo(GoHttpHeadUtil.getLanguage(context)));
        Runnable r = new Runnable() {
            public void run() {
                AdInfoStatistic.mAdShowStatisticMap.remove(adId);
                LogUtils.i("hzw", "展示统计有效期(900000)已到，尝试上传");
                String showInfo = AdInfoStatisticManager.getInstance(context).getAdShowStatistic(adId);
                if (showInfo == null) {
                    LogUtils.e("hzw", "uploadPhase2Impression:showInfo is null");
                } else {
                    AdInfoStatistic.uploadPhase2ImpressionNow(context, showInfo);
                }
            }
        };
        mAdShowStatisticMap.put(adId, r);
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(r, AD_SHOW_STATISTIC_TIMEOUT);
    }

    public static void uploadPhase2Click(String adId, Context context, String title, String url, String desc, String pkgname, int type) {
        String showInfo = AdInfoStatisticManager.getInstance(context).getAdShowStatistic(adId);
        if (showInfo == null) {
            LogUtils.e("hzw", "uploadPhase2Click:showInfo is null");
            return;
        }
        CustomThreadExecutorProxy.getInstance().cancel(mAdShowStatisticMap.remove(adId));
        uploadPhase2ImpressionNow(context, showInfo);
        uploadSqe102StatisticData(context, 646, replaceAdInfo(title) + SEPARATOR_CONTENT + replaceAdInfo(url) + SEPARATOR_CONTENT + replaceAdInfo(desc) + SEPARATOR_CONTENT + replaceAdInfo(pkgname) + SEPARATOR_CONTENT + replaceAdInfo(GoHttpHeadUtil.getLanguage(context)), type, getProductId(), "phase2_click");
    }

    public static void uploadPhase2Install(Context context, String title, String url, String desc, String pkgname, int type) {
        uploadSqe102StatisticData(context, 646, replaceAdInfo(title) + SEPARATOR_CONTENT + replaceAdInfo(url) + SEPARATOR_CONTENT + replaceAdInfo(desc) + SEPARATOR_CONTENT + replaceAdInfo(pkgname) + SEPARATOR_CONTENT + replaceAdInfo(GoHttpHeadUtil.getLanguage(context)), type, getProductId(), "phase2_install");
    }

    public static String replaceAdInfo(String info) {
        return info == null ? info : info.replaceAll(SEPARATOR_CONTENT, "; ;").replaceAll(SEPARATOR_BATCH, "! !");
    }
}

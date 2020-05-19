package com.jiubang.commerce.buychannel.buyChannel.Statistics;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.statistics.BaseStatistic;
import com.jiubang.commerce.buychannel.BuildConfig;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.Statistics45;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.manager.GpVersionHelper;
import com.jiubang.commerce.buychannel.buyChannel.utils.AppInfoUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class UserStatistics extends BaseStatistic {
    public static void upload45(Context context, boolean isGoKeyboard, int funId, String sender, String afDetail, String referrer, String userType, String tag, String afAgency, boolean isAfter19) {
        Statistics45.Statistic45Params params = new Statistics45.Statistic45Params();
        String oldSender = BuyChannelUtils.getOldSender(context);
        String oldUserType = BuyChannelUtils.getOldUserType(context);
        if (oldSender == null || sender == null || !sender.equals(oldSender) || oldUserType == null || userType == null || !userType.equals(oldUserType)) {
            params.sender(sender).referrer(referrer).afDetail(afDetail).associatedObj(context.getPackageName()).funId(funId).operationCode("k001").gpVersionName(GpVersionHelper.getInstance(context).getGpVersionName()).userType(userType).tag(tag).sdkVersionCode("1.4.2").afAgency(afAgency).advertId(AppInfoUtils.getAdvertisingId(context));
            Statistics45.upload(context, isGoKeyboard, params, isAfter19);
            BuyChannelUtils.setOldSender(context, params.getSender());
            BuyChannelUtils.setOldUserType(context, params.getUserType());
            return;
        }
        LogUtils.i("buychannelsdk", "[UserStatistics::upload45] sender数据或者用户类型和上一次45上传的相同，不在上传45协议,oldParams:sender=" + oldSender + ",oldParams:userType=" + oldUserType);
        StatisticsDebug.Statistic103Params statistic103ParamsP10 = new StatisticsDebug.Statistic103Params();
        statistic103ParamsP10.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(4);
        StatisticsDebug.upload(context, statistic103ParamsP10);
    }

    public static String get45AssociatedObjFB(String campaign, String adset, String adgroup) {
        return "utm_source=fb&utm_medium=banner" + "&utm_campaign=" + campaign + "&gokey_channel=" + adset + "&gokey_click_id=" + adgroup;
    }

    public static String get45AssociatedObjOther(String mediaSource, String campaign, String adset, String adgroup) {
        return "utm_source=" + mediaSource + "&utm_medium=banner" + "&utm_campaign=" + campaign + "&gokey_channel=" + adset + "&gokey_click_id=" + adgroup;
    }

    public static String get45AssociatedEmptyOther(String mediaSource) {
        return "utm_source=" + mediaSource;
    }

    public static String get45AssociatedObjAdwords(String campaign, boolean isGdnCampaignids) {
        String channel;
        StringBuilder sb = new StringBuilder("utm_source=");
        if (isGdnCampaignids) {
            channel = "adwords_gdn";
        } else {
            channel = "adwords";
        }
        sb.append(channel);
        sb.append("&utm_medium=banner");
        sb.append("&utm_campaign=");
        sb.append(campaign);
        sb.append("&gokey_channel=&gokey_click_id=");
        return sb.toString();
    }

    public static String get45AssociatedObjTwitter(String campaign) {
        return "utm_source=twitter&utm_medium=banner" + "&utm_campaign=" + campaign + "&gokey_channel=&gokey_click_id=";
    }

    public static void logInstallReferrer(String referrer) {
        uploadLog(BuySdkConstants.REFERRER, referrer);
    }

    public static void logAppsflyerInfo(String installReferrer, String conversionDataJsonStr, String analysistResult, String afStatus) {
        String tag;
        if ("Organic".equalsIgnoreCase(afStatus)) {
            tag = "report_appsflyer_organic";
        } else {
            tag = "report_appsflyer_not_organic";
        }
        Map<String, String> extraInfos = new HashMap<>();
        if (installReferrer == null) {
            installReferrer = BuildConfig.FLAVOR;
        }
        extraInfos.put("install_referrer", installReferrer);
        if (!TextUtils.isEmpty(conversionDataJsonStr)) {
            extraInfos.put("appsflyer_conversionData", conversionDataJsonStr);
        }
        if (!TextUtils.isEmpty(analysistResult)) {
            extraInfos.put("analysist_result", analysistResult);
        }
        uploadLog(tag, extraInfos);
    }

    public static String getAppsflyerAnalysistResult(boolean isFb, String campaign, String adSet, String adGroup, String mediaSource, String agency, String afStatus, String buyChannel) {
        JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("is_fb", String.valueOf(isFb));
            if (!TextUtils.isEmpty(campaign)) {
                infoJson.put(BuySdkConstants.CAMPAIGN, campaign);
            }
            if (!TextUtils.isEmpty(adSet)) {
                infoJson.put("adset", adSet);
            }
            if (!TextUtils.isEmpty(adGroup)) {
                infoJson.put("adgroup", adGroup);
            }
            if (!TextUtils.isEmpty(mediaSource)) {
                infoJson.put("media_source", mediaSource);
            }
            if (!TextUtils.isEmpty(agency)) {
                infoJson.put("agency", agency);
            }
            if (!TextUtils.isEmpty(agency)) {
                infoJson.put("agency", agency);
            }
            if (!TextUtils.isEmpty(afStatus)) {
                infoJson.put("af_status", afStatus);
            }
            if (!TextUtils.isEmpty(buyChannel)) {
                infoJson.put("buyChannel", buyChannel);
            }
            String info = infoJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable e2) {
            e2.printStackTrace();
        }
        return infoJson.toString();
    }

    private static void uploadLog(String tag, String content) {
        LogUtils.i("buychannelsdk", "[UserStatistics::uploadLog] tag:" + tag + ", content:" + content);
    }

    private static void uploadLog(String tag, Map<String, String> contents) {
        String content = BuildConfig.FLAVOR;
        JSONObject jsonObj = new JSONObject();
        try {
            for (String key : contents.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    jsonObj.put(key, contents.get(key));
                }
            }
            content = jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable e2) {
            e2.printStackTrace();
        }
        uploadLog(tag, content);
    }
}

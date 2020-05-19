package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.BuildConfig;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.UserStatistics;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;

public class ReferrerManager {
    private static ReferrerManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public SharedPreferences mSp;

    public static ReferrerManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ReferrerManager.class) {
                if (sInstance == null) {
                    sInstance = new ReferrerManager(context);
                }
            }
        }
        return sInstance;
    }

    private ReferrerManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mSp = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
    }

    public boolean checkFirstReceiver() {
        return this.mSp.getBoolean(BuySdkConstants.IS_FIRST_RECEIVER, true);
    }

    public void saveFirstReceiver() {
        this.mSp.edit().putBoolean(BuySdkConstants.IS_FIRST_RECEIVER, false).commit();
    }

    public void saveReferrer(String referrer) {
        if (!TextUtils.isEmpty(referrer)) {
            referrer = referrer.replaceAll("\\n", BuildConfig.FLAVOR).replaceAll("\\u007C", "#");
        }
        this.mSp.edit().putString(BuySdkConstants.REFERRER, referrer).commit();
    }

    public String getReferrer() {
        return this.mSp.getString(BuySdkConstants.REFERRER, (String) null);
    }

    public String getCampaign(String referrer) {
        String[] splits;
        String[] utmSources;
        if (!TextUtils.isEmpty(referrer) && (splits = referrer.split("&")) != null && splits.length >= 0) {
            for (String string : splits) {
                if (string != null && string.toLowerCase().contains(BuySdkConstants.CAMPAIGN) && (utmSources = string.split("=")) != null && utmSources.length > 1) {
                    return utmSources[1];
                }
            }
        }
        return null;
    }

    public String getCampaignId(String referrer) {
        String[] splits;
        String[] utmSources;
        if (!TextUtils.isEmpty(referrer) && (splits = referrer.split("&")) != null && splits.length >= 0) {
            for (String string : splits) {
                if (string != null && string.toLowerCase().contains("campaignid") && (utmSources = string.split("=")) != null && utmSources.length > 1) {
                    return utmSources[1];
                }
            }
        }
        return null;
    }

    public String getUtmSource(String referrer) {
        String[] splits;
        String[] utmSources;
        if (!TextUtils.isEmpty(referrer) && (splits = referrer.split("&")) != null && splits.length >= 0) {
            for (String string : splits) {
                if (string != null && string.contains("utm_source") && (utmSources = string.split("=")) != null && utmSources.length > 1) {
                    return utmSources[1];
                }
            }
        }
        return null;
    }

    public String getFromUserType(String referrer) {
        String[] splits;
        String[] fromUserType;
        if (!TextUtils.isEmpty(referrer) && (splits = referrer.split("&")) != null && splits.length >= 0) {
            for (String string : splits) {
                if (string != null && string.contains(BuySdkConstants.FROM_3G_CHANNEL) && (fromUserType = string.split("=")) != null && fromUserType.length > 1) {
                    return fromUserType[1];
                }
            }
        }
        return null;
    }

    public void analyseUtmSource(String utmSource, String referrer) {
        UserTypeInfo.FirstUserType userType;
        UserTypeInfo.SecondUserType sUserType;
        boolean isGpOrgnic = false;
        boolean isNotGpOrgnic = false;
        GpVersionHelper gpVersionHelper = GpVersionHelper.getInstance(this.mContext);
        if (referrer == null) {
            StatisticsDebug.Statistic103Params statistic103ParamsP4 = new StatisticsDebug.Statistic103Params();
            statistic103ParamsP4.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(4);
            StatisticsDebug.upload(this.mContext, statistic103ParamsP4);
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource] refferer为空");
            return;
        }
        if (TextUtils.isEmpty(utmSource) && !gpVersionHelper.campareWhitGpVersion6_8_24()) {
            isGpOrgnic = true;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]解析出UtmSource为空，且gp版本小于6.8.24或没有gp版本,判定为gp自然，isGpOrgnic=" + true);
        }
        if (TextUtils.isEmpty(utmSource) && gpVersionHelper.campareWhitGpVersion6_8_24()) {
            isNotGpOrgnic = true;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]解析出UtmSource为空，且gp版本大于6.8.24,判定为非gp自然，isNotGpOrgnic=" + true);
        }
        if (!TextUtils.isEmpty(referrer)) {
            referrer = referrer.replaceAll("\\n", BuildConfig.FLAVOR).replaceAll("\\u007C", "#");
        }
        if (!TextUtils.isEmpty(utmSource) && utmSource.contains("not set")) {
            UserTypeInfo.FirstUserType userType2 = UserTypeInfo.FirstUserType.withCount;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]utmSource为not_set,认定为非自然带量，userType=" + userType2 + "，二级类型为：" + UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC);
        }
        if (!TextUtils.isEmpty(utmSource) && utmSource.toLowerCase().contains("google-play")) {
            userType = UserTypeInfo.FirstUserType.organic;
            sUserType = UserTypeInfo.SecondUserType.GP_ORGNIC;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]解析GA中的UtmSource，包含google-play关键字，判定为gp自然，isGpOrgnic=" + isGpOrgnic);
        } else if ((!TextUtils.isEmpty(utmSource) && referrer.contains("gokey_channel") && referrer.contains("gokey_click_id")) || (!TextUtils.isEmpty(utmSource) && referrer.contains("zerokey_channel") && referrer.contains("zerokey_click_id"))) {
            userType = UserTypeInfo.FirstUserType.userbuy;
            sUserType = UserTypeInfo.SecondUserType.GA_USERBUY;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]解析GA中的UtmSource，解析出用户类型为买量用户，userType=" + userType + "，二级类型为：" + sUserType);
        } else if (referrer.contains(BuySdkConstants.FROM_3G_CHANNEL)) {
            userType = UserTypeInfo.FirstUserType.withCount;
            sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC;
            String fromUserType = getFromUserType(referrer);
            if (!TextUtils.isEmpty(fromUserType)) {
                if (fromUserType.equals(UserTypeInfo.FirstUserType.apkbuy.toString())) {
                    sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC;
                    LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]带量规则解析GA中的UtmSource，解析出用户类型为非自然的带量，from_3g_channel=" + fromUserType + "，二级类型为：" + sUserType);
                }
                if (fromUserType.equals(UserTypeInfo.FirstUserType.withCount.toString())) {
                    sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC;
                    LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]带量规则解析GA中的UtmSource，解析出用户类型为非自然的带量，from_3g_channel=" + fromUserType + "，二级类型为：" + sUserType);
                }
                if (fromUserType.equals(UserTypeInfo.FirstUserType.userbuy.toString())) {
                    sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC;
                    LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]带量规则解析GA中的UtmSource，解析出用户类型为非自然的带量，from_3g_channel=" + fromUserType + "，二级类型为：" + sUserType);
                }
                if (fromUserType.equals(UserTypeInfo.FirstUserType.organic.toString())) {
                    sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_ORGNIC;
                    LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]带量规则解析GA中的UtmSource，解析出用户类型为自然的带量，from_3g_channel=" + fromUserType + "，二级类型为：" + sUserType);
                }
            }
        } else if (isGpOrgnic) {
            userType = UserTypeInfo.FirstUserType.organic;
            sUserType = UserTypeInfo.SecondUserType.GP_ORGNIC;
        } else if (isNotGpOrgnic) {
            userType = UserTypeInfo.FirstUserType.organic;
            sUserType = UserTypeInfo.SecondUserType.NOT_GP_ORGNIC;
        } else {
            userType = UserTypeInfo.FirstUserType.withCount;
            sUserType = UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC;
            LogUtils.i("buychannelsdk", "[ReferrerManager::analyseUtmSource]没有from_3g_channel标识,又不是买量，判断为认定为非自然带量，userType=" + userType + "，二级类型为：" + sUserType);
        }
        final String finalReferrer = referrer;
        final UserTypeInfo.SecondUserType secondUserType = sUserType;
        BuyChannelSetting.getInstance(this.mContext).setBuyChannel(utmSource, BuyChannelSetting.ChannelFrom.from_ga, userType, sUserType, (String) null, (String) null, referrer, (String) null, (String) null, referrer, (String) null, new SetBuyChannelListener() {
            public void setBuyChannelSuccess() {
            }

            public void setBuyChannelFailure(final String position) {
                CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
                    public void run() {
                        if (ReferrerManager.getInstance(ReferrerManager.this.mContext).checkFirstReceiver() && !BuyChannelUtils.isOldApkBuy(ReferrerManager.this.mContext)) {
                            if (ReferrerManager.this.mSp == null) {
                                SharedPreferences unused = ReferrerManager.this.mSp = BuyChannelDataMgr.getInstance(ReferrerManager.this.mContext).getSharedPreferences(ReferrerManager.this.mContext);
                            }
                            UserStatistics.upload45(ReferrerManager.this.mContext, ReferrerManager.this.mSp.getBoolean(BuySdkConstants.IS_GOKEYBORAD, false), ReferrerManager.this.mSp.getInt(BuySdkConstants.FUN_ID_45, 0), finalReferrer, (String) null, finalReferrer, String.valueOf(secondUserType.getValue()), "-1", (String) null, false);
                        } else if (BuySdkConstants.Position_103.POSITION_7.getValue().equals(position)) {
                            StatisticsDebug.Statistic103Params statistic103ParamsP7 = new StatisticsDebug.Statistic103Params();
                            statistic103ParamsP7.code(BuySdkConstants.DEBUG_CODE2).referrer(finalReferrer).position(7);
                            StatisticsDebug.upload(ReferrerManager.this.mContext, statistic103ParamsP7);
                        } else if (BuySdkConstants.Position_103.POSITION_8.getValue().equals(position)) {
                            StatisticsDebug.Statistic103Params statistic103ParamsP8 = new StatisticsDebug.Statistic103Params();
                            statistic103ParamsP8.code(BuySdkConstants.DEBUG_CODE2).referrer(finalReferrer).position(8);
                            StatisticsDebug.upload(ReferrerManager.this.mContext, statistic103ParamsP8);
                        } else if (BuySdkConstants.Position_103.POSITION_9.getValue().equals(position)) {
                            StatisticsDebug.Statistic103Params statistic103ParamsP9 = new StatisticsDebug.Statistic103Params();
                            statistic103ParamsP9.code(BuySdkConstants.DEBUG_CODE2).referrer(finalReferrer).position(9);
                            StatisticsDebug.upload(ReferrerManager.this.mContext, statistic103ParamsP9);
                        }
                    }
                });
            }
        });
    }
}

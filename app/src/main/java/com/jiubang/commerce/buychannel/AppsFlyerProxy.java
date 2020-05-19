package com.jiubang.commerce.buychannel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.UserStatistics;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTagParam;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.manager.GpVersionHelper;
import com.jiubang.commerce.buychannel.buyChannel.manager.ReferrerManager;
import com.jiubang.commerce.buychannel.buyChannel.manager.ServerCheckHelper;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class AppsFlyerProxy {
    public static final String FLYER_KEY = "o6XxR94NFNcyL6NTzsUrRG";
    private static AppsFlyerProxy sInstance;
    /* access modifiers changed from: private */
    public List<String> mAdwordsGdnCampaignids;
    private AppsFlyerConversionListener mAppsFlyerConversionListener = new AppsFlyerConversionListener() {
        public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
            String associatedObj;
            if (conversionData != null && !conversionData.isEmpty()) {
                if (BuyChannelUtils.isOldApkBuy(AppsFlyerProxy.this.mContext)) {
                    LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onInstallConversionDataLoaded] 缓存中已是APK买量，不再处理af的回调");
                } else if (BuyChannelUtils.isOldUser(AppsFlyerProxy.this.mContext)) {
                    LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onInstallConversionDataLoaded] 已经是老用户，不再处理af的回调");
                } else {
                    boolean isFb = false;
                    boolean isAdwords = false;
                    String campaign = BuildConfig.FLAVOR;
                    String adSet = BuildConfig.FLAVOR;
                    String adGroup = BuildConfig.FLAVOR;
                    String mediaSource = BuildConfig.FLAVOR;
                    String agency = BuildConfig.FLAVOR;
                    String campaignid = BuildConfig.FLAVOR;
                    String campaign_id = BuildConfig.FLAVOR;
                    String campaignId = BuildConfig.FLAVOR;
                    String conversionDataJsonStr = BuildConfig.FLAVOR;
                    for (String attrName : conversionData.keySet()) {
                        if (!TextUtils.isEmpty(attrName)) {
                            String attrValue = conversionData.get(attrName);
                            if (TextUtils.isEmpty(attrValue)) {
                                attrValue = BuildConfig.FLAVOR;
                            }
                            if (attrName.equals("is_fb")) {
                                isFb = Boolean.TRUE.toString().equalsIgnoreCase(attrValue);
                            } else if (attrName.equals(BuySdkConstants.CAMPAIGN)) {
                                campaign = attrValue;
                            } else if (attrName.equals("adset")) {
                                adSet = attrValue;
                            } else if (attrName.equals("adgroup")) {
                                adGroup = attrValue;
                            } else if (attrName.equals("media_source")) {
                                mediaSource = attrValue;
                            } else if (attrName.equals("agency")) {
                                agency = attrValue;
                                if ("null".equalsIgnoreCase(agency)) {
                                    agency = BuildConfig.FLAVOR;
                                }
                            } else if (attrName.equals("af_status")) {
                                String afStatus = attrValue;
                            } else if (attrName.equals("campaignid")) {
                                campaignid = attrValue;
                            } else if (attrName.equals("campaign_id")) {
                                campaign_id = attrValue;
                            }
                        }
                    }
                    String afCampaignId = "null";
                    if (!TextUtils.isEmpty(campaignid)) {
                        afCampaignId = campaignid;
                    } else if (TextUtils.isEmpty(campaign_id)) {
                        afCampaignId = campaign_id;
                    }
                    if (!TextUtils.isEmpty(campaignid)) {
                        campaignId = campaignid;
                    } else if (!TextUtils.isEmpty(campaign)) {
                        campaignId = campaign;
                    } else if (!TextUtils.isEmpty(campaign_id)) {
                        campaignId = campaign_id;
                    }
                    try {
                        conversionDataJsonStr = new JSONObject(conversionData).toString();
                    } catch (Throwable e) {
                        LogUtils.w("buychannelsdk", "warning-->", e);
                    }
                    LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onInstallConversionDataLoaded] AppsFlyer原始数据，conversionDataJsonStr: " + conversionDataJsonStr);
                    AppsFlyerProxy.this.cancelupload15S((String) null, conversionDataJsonStr);
                    String buyChannel = mediaSource;
                    if (isFb) {
                        buyChannel = "fb";
                    }
                    if (!isFb && "Facebook Ads".equalsIgnoreCase(mediaSource)) {
                        isFb = true;
                        buyChannel = "fb";
                    }
                    if (!isFb && ("adwords".equalsIgnoreCase(mediaSource) || "googleadwords_int".equalsIgnoreCase(mediaSource) || (!AppsFlyerProxy.this.isAgencyEmpty(agency) && TextUtils.isEmpty(mediaSource)))) {
                        isAdwords = true;
                        buyChannel = "adwords";
                    }
                    if (TextUtils.isEmpty(adGroup)) {
                        adGroup = conversionData.get("adgroup_name");
                    }
                    if (TextUtils.isEmpty(adSet)) {
                        adSet = conversionData.get("adset_name");
                    }
                    String referrer = ReferrerManager.getInstance(AppsFlyerProxy.this.mContext).getReferrer();
                    UserTypeInfo.FirstUserType userType = null;
                    UserTypeInfo.SecondUserType secondUserType = null;
                    if (isFb) {
                        associatedObj = UserStatistics.get45AssociatedObjFB(campaign, adSet, adGroup);
                        userType = UserTypeInfo.FirstUserType.userbuy;
                        secondUserType = UserTypeInfo.SecondUserType.FB_AUTO;
                        if ((!"Facebook Ads".equalsIgnoreCase(mediaSource) || !campaign.toLowerCase().startsWith("gomo")) && "Facebook Ads".equalsIgnoreCase(mediaSource) && !campaign.toLowerCase().startsWith("gomo")) {
                            userType = UserTypeInfo.FirstUserType.userbuy;
                            secondUserType = UserTypeInfo.SecondUserType.FB_NOTAUTO;
                        }
                    } else if (isAdwords) {
                        if ("googleadwords_int".equalsIgnoreCase(mediaSource)) {
                            userType = UserTypeInfo.FirstUserType.userbuy;
                            secondUserType = UserTypeInfo.SecondUserType.ADWORDS_AUTO;
                        } else if (!AppsFlyerProxy.this.isAgencyEmpty(agency) && TextUtils.isEmpty(mediaSource)) {
                            userType = UserTypeInfo.FirstUserType.userbuy;
                            secondUserType = UserTypeInfo.SecondUserType.ADWORDS_NOTAUTO;
                        }
                        if (AppsFlyerProxy.this.mAdwordsGdnCampaignids == null || !AppsFlyerProxy.this.mAdwordsGdnCampaignids.contains(campaignId)) {
                            buyChannel = "adwords";
                            associatedObj = UserStatistics.get45AssociatedObjAdwords(campaign, false);
                        } else {
                            buyChannel = "adwords_gdn";
                            associatedObj = UserStatistics.get45AssociatedObjAdwords(campaign, true);
                            userType = UserTypeInfo.FirstUserType.userbuy;
                            secondUserType = UserTypeInfo.SecondUserType.ADWORDS_AUTO;
                        }
                    } else if (TextUtils.isEmpty(mediaSource) || !AppsFlyerProxy.this.isAgencyEmpty(agency) || 0 != 0) {
                        UserTypeInfo.FirstUserType userType2 = UserTypeInfo.FirstUserType.organic;
                        UserTypeInfo.SecondUserType secondUserType2 = UserTypeInfo.SecondUserType.GP_ORGNIC;
                        AppsFlyerProxy.this.uploadFirst45(referrer, conversionDataJsonStr, BuySdkConstants.UNKNOWN_BUYCHANNEL, UserStatistics.get45AssociatedEmptyOther(mediaSource), agency, userType2, secondUserType2, campaign, afCampaignId);
                        return;
                    } else {
                        associatedObj = UserStatistics.get45AssociatedObjOther(mediaSource, campaign, adSet, adGroup);
                        userType = UserTypeInfo.FirstUserType.userbuy;
                        secondUserType = UserTypeInfo.SecondUserType.GA_USERBUY;
                        LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onInstallConversionDataLoaded]mediaSource不为空,判断为GA买量;Appfly原始数据:" + conversionDataJsonStr + ";GA原始数据:" + referrer);
                    }
                    final String finalConversionDataJsonStr = conversionDataJsonStr;
                    final String finalAssociatedObj = associatedObj;
                    final String finalAgency = agency;
                    final UserTypeInfo.SecondUserType finalSecondUserType = secondUserType;
                    final String finalBuyChannel = buyChannel;
                    final String finalConversionDataJsonStr1 = conversionDataJsonStr;
                    final String finalAssociatedObj1 = associatedObj;
                    final String str = referrer;
                    BuyChannelSetting.getInstance(AppsFlyerProxy.this.mContext).setBuyChannel(buyChannel, BuyChannelSetting.ChannelFrom.from_appsflyer, userType, secondUserType, campaign, afCampaignId, associatedObj, agency, conversionDataJsonStr, referrer, (String) null, new SetBuyChannelListener() {
                        public void setBuyChannelSuccess() {
                            AppsFlyerProxy.this.mSp.edit().putBoolean(BuySdkConstants.APPFLY_UPLOARD, false).commit();
                            StatisticsDebug.Statistic103Params statistic103Params = new StatisticsDebug.Statistic103Params();
                            statistic103Params.code(BuySdkConstants.DEBUG_CODE4).referrer(str).buychannel(finalBuyChannel).position(1).appflyer(finalConversionDataJsonStr1);
                            StatisticsDebug.upload(AppsFlyerProxy.this.mContext, statistic103Params);
                            AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.ASSOCIATE_103, finalAssociatedObj1).commit();
                        }

                        public void setBuyChannelFailure(String position) {
                            if (AppsFlyerProxy.this.mSp.getBoolean(BuySdkConstants.APPFLY_UPLOARD, true)) {
                                AppsFlyerProxy.this.uploard45(finalAssociatedObj, finalConversionDataJsonStr, str, finalSecondUserType.getValue() + BuildConfig.FLAVOR, "-1", finalAgency, AppsFlyerProxy.this.mInitParams);
                                BuyChannelDataMgr.getInstance(AppsFlyerProxy.this.mContext).setConversionStr(finalConversionDataJsonStr);
                                AppsFlyerProxy.this.mSp.edit().putBoolean(BuySdkConstants.APPFLY_UPLOARD, false).commit();
                                LogUtils.i("buychannelsdk", "[BuyChannelSetting::uploard45] 上传debug 45协议，保存第一次af的原始数据");
                            }
                            String oldAssociateObj = AppsFlyerProxy.this.mSp.getString(BuySdkConstants.ASSOCIATE_103, (String) null);
                            if (oldAssociateObj == null || !oldAssociateObj.equals(finalAssociatedObj)) {
                                StatisticsDebug.Statistic103Params statistic103Params = new StatisticsDebug.Statistic103Params();
                                statistic103Params.code(BuySdkConstants.DEBUG_CODE4).referrer(str).buychannel(finalBuyChannel).position(0).appflyer(finalConversionDataJsonStr1);
                                StatisticsDebug.upload(AppsFlyerProxy.this.mContext, statistic103Params);
                                AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.ASSOCIATE_103, finalAssociatedObj1).commit();
                            }
                        }
                    });
                }
            }
        }

        public void onInstallConversionFailure(String errorMessage) {
            LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onInstallConversionFailure] errorMessage: " + errorMessage);
        }

        public void onAppOpenAttribution(Map<String, String> attributionData) {
            if (LogUtils.isShowLog()) {
                String jsonStr = BuildConfig.FLAVOR;
                try {
                    jsonStr = new JSONObject(attributionData).toString();
                } catch (Throwable e) {
                    LogUtils.w("buychannelsdk", "warning-->", e);
                }
                LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onAppOpenAttribution] attributionData: " + jsonStr);
            }
        }

        public void onAttributionFailure(String errorMessage) {
            LogUtils.i("buychannelsdk", "[AppsFlyerProxy::onAttributionFailure] errorMessage: " + errorMessage);
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public BuySdkInitParams mInitParams;
    /* access modifiers changed from: private */
    public SharedPreferences mSp;
    /* access modifiers changed from: private */
    public Runnable mUpload15sTask;
    /* access modifiers changed from: private */
    public Runnable mUploard45Task;

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
    }

    private AppsFlyerProxy() {
    }

    public BuySdkInitParams getInitParams() {
        return this.mInitParams;
    }

    public static AppsFlyerProxy getInstance() {
        if (sInstance == null) {
            synchronized (AppsFlyerProxy.class) {
                sInstance = new AppsFlyerProxy();
            }
        }
        return sInstance;
    }

    public void init(Application application, final BuySdkInitParams params) {
        this.mContext = application.getApplicationContext();
        this.mInitParams = params;
        this.mAdwordsGdnCampaignids = params.mAdwordsGdnCampaignids;
        this.mSp = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
            public void run() {
                long triggerTime;
                if (params != null) {
                    AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.USERTAG_PARAMS, new UserTagParam(StatisticsManager.getGOID(AppsFlyerProxy.this.mContext), (String) null, params.mChannel + BuildConfig.FLAVOR, params.mProductKey, params.mAccessKey).toJsonStr()).commit();
                    AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.CID_45, params.mUsertypeProtocalCId).commit();
                    AppsFlyerProxy.this.mSp.edit().putBoolean(BuySdkConstants.IS_GOKEYBORAD, params.mIsGoKeyboard).commit();
                    AppsFlyerProxy.this.mSp.edit().putInt(BuySdkConstants.FUN_ID_45, params.mP45FunId).commit();
                }
                long triggerTime2 = System.currentTimeMillis() - BuyChannelDataMgr.getInstance(AppsFlyerProxy.this.mContext).getLastCheckTime();
                if (triggerTime2 > BuySdkConstants.INTERVAL_SERVER_CHECKTIME) {
                    triggerTime = BuySdkConstants.INTERVAL_SERVER_CHECKTIME;
                } else {
                    triggerTime = BuySdkConstants.INTERVAL_SERVER_CHECKTIME - triggerTime2;
                }
                ServerCheckHelper.getInstance(AppsFlyerProxy.this.mContext).cancelCheckServer();
                ServerCheckHelper.getInstance(AppsFlyerProxy.this.mContext).startCheckServer(triggerTime);
                LogUtils.i("buychannelsdk", "check定时器");
                AppsFlyerProxy.this.upload15Second();
            }
        });
        initAf(application);
    }

    private void initAf(Application application) {
        if (this.mInitParams.isSetImei) {
            AppsFlyerLib.getInstance().setImeiData(BuyChannelUtils.getIMEI(application));
        }
        try {
            AppsFlyerLib.getInstance().setAndroidIdData(Machine.getAndroidId(application.getApplicationContext()));
        } catch (Exception e) {
        }
        AppsFlyerLib.getInstance().startTracking(application, FLYER_KEY);
        AppsFlyerLib.getInstance().registerConversionListener(this.mContext, this.mAppsFlyerConversionListener);
    }

    /* access modifiers changed from: private */
    public void uploadFirst45(String referrer, String conversionDataJsonStr, String buyChannel, String associatedObj, String agency, UserTypeInfo.FirstUserType userType, UserTypeInfo.SecondUserType secondUserType, String campaign, String campaignId) {
        if (!BuyChannelUtils.isOldApkBuy(this.mContext) && !BuyChannelUtils.isOldUser(this.mContext) && this.mSp.getBoolean(BuySdkConstants.APPFLY_UPLOARD, true)) {
            if (this.mUploard45Task != null) {
                CustomThreadExecutorProxy.getInstance().cancel(this.mUploard45Task);
                this.mUploard45Task = null;
            }
            final String str = buyChannel;
            final UserTypeInfo.FirstUserType firstUserType = userType;
            final UserTypeInfo.SecondUserType secondUserType2 = secondUserType;
            final String str2 = campaign;
            final String str3 = campaignId;
            final String str4 = associatedObj;
            final String str5 = agency;
            final String str6 = conversionDataJsonStr;
            final String str7 = referrer;
            this.mUploard45Task = new Runnable() {
                public void run() {
                    BuyChannelSetting.getInstance(AppsFlyerProxy.this.mContext).setBuyChannel(str, BuyChannelSetting.ChannelFrom.from_appsflyer, firstUserType, secondUserType2, str2, str3, str4, str5, str6, str7, (String) null, new SetBuyChannelListener() {
                        public void setBuyChannelSuccess() {
                            AppsFlyerProxy.this.mSp.edit().putBoolean(BuySdkConstants.APPFLY_UPLOARD, false).commit();
                            StatisticsDebug.Statistic103Params statistic103Params = new StatisticsDebug.Statistic103Params();
                            statistic103Params.code(BuySdkConstants.DEBUG_CODE4).referrer(str7).buychannel(str).position(1).appflyer(str6);
                            StatisticsDebug.upload(AppsFlyerProxy.this.mContext, statistic103Params);
                            AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.ASSOCIATE_103, str4).commit();
                        }

                        public void setBuyChannelFailure(String position) {
                            if (AppsFlyerProxy.this.mSp.getBoolean(BuySdkConstants.APPFLY_UPLOARD, true)) {
                                AppsFlyerProxy.this.uploard45(str4, str6, str7, secondUserType2.getValue() + BuildConfig.FLAVOR, "-1", str5, AppsFlyerProxy.this.mInitParams);
                                BuyChannelDataMgr.getInstance(AppsFlyerProxy.this.mContext).setConversionStr(str6);
                                AppsFlyerProxy.this.mSp.edit().putBoolean(BuySdkConstants.APPFLY_UPLOARD, false).commit();
                                LogUtils.i("buychannelsdk", "[BuyChannelSetting::uploard45] 上传debug 45协议，保存第一次af的原始数据");
                            }
                            String oldAssociateObj = AppsFlyerProxy.this.mSp.getString(BuySdkConstants.ASSOCIATE_103, (String) null);
                            if (oldAssociateObj == null || !oldAssociateObj.equals(str4)) {
                                StatisticsDebug.Statistic103Params statistic103Params = new StatisticsDebug.Statistic103Params();
                                statistic103Params.code(BuySdkConstants.DEBUG_CODE4).referrer(str7).buychannel(str).position(0).appflyer(str6);
                                StatisticsDebug.upload(AppsFlyerProxy.this.mContext, statistic103Params);
                                AppsFlyerProxy.this.mSp.edit().putString(BuySdkConstants.ASSOCIATE_103, str4).commit();
                            }
                        }
                    });
                    Runnable unused = AppsFlyerProxy.this.mUploard45Task = null;
                }
            };
            CustomThreadExecutorProxy.getInstance().runOnAsyncThread(this.mUploard45Task, 15000);
        }
    }

    public void cancelUploadFirst45(String referrer) {
        if (this.mUploard45Task != null) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mUploard45Task);
            this.mUploard45Task = null;
            LogUtils.i("buychannelsdk", "[AppsFlyerProxy::cancelUploadFirst45]");
        }
    }

    /* access modifiers changed from: private */
    public void uploard45(String associatedObj, String conversionDataJsonStr, String referrer, String secondUserType, String tag, String afAgency, BuySdkInitParams mInitParams2) {
        UserStatistics.upload45(this.mContext, mInitParams2.mIsGoKeyboard, mInitParams2.mP45FunId, associatedObj, conversionDataJsonStr, referrer, secondUserType, tag, afAgency, false);
    }

    /* access modifiers changed from: private */
    public boolean isAgencyEmpty(String agency) {
        if (!TextUtils.isEmpty(agency) && !agency.equalsIgnoreCase("null")) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void upload15Second() {
        if (this.mUpload15sTask != null) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mUpload15sTask);
            this.mUpload15sTask = null;
        }
        this.mUpload15sTask = new Runnable() {
            public void run() {
                UserTypeInfo.FirstUserType userType;
                UserTypeInfo.SecondUserType sUserType;
                if (!BuyChannelUtils.isOldUser(AppsFlyerProxy.this.mContext) && !BuyChannelUtils.isOldApkBuy(AppsFlyerProxy.this.mContext)) {
                    if (GpVersionHelper.getInstance(AppsFlyerProxy.this.mContext).campareWhitGpVersion6_8_24()) {
                        userType = UserTypeInfo.FirstUserType.organic;
                        sUserType = UserTypeInfo.SecondUserType.NOT_GP_ORGNIC;
                        LogUtils.i("buychannelsdk", "[AppsFlyerProxy::upload15Second] 15内，未返回appflyer和GA数据, 没有GA广播和af信息且gp版本≥6.8.24,判定为非GP自然");
                    } else {
                        userType = UserTypeInfo.FirstUserType.organic;
                        sUserType = UserTypeInfo.SecondUserType.GP_ORGNIC;
                        LogUtils.i("buychannelsdk", "[AppsFlyerProxy::upload15Second] 15内，未返回appflyer和GA数据, 没有Ga广播和af信息且gp版本<6.8.24或为gp版本空，判定为GP自然,判定为GP自然");
                    }
                    BuyChannelSetting.getInstance(AppsFlyerProxy.this.mContext).setBuyChannel(BuySdkConstants.UNKNOWN_BUYCHANNEL, BuyChannelSetting.ChannelFrom.un_known, userType, sUserType, (String) null, (String) null, UserStatistics.get45AssociatedEmptyOther(BuySdkConstants.UNKNOWN_BUYCHANNEL), (String) null, (String) null, (String) null, (String) null, (SetBuyChannelListener) null);
                    Runnable unused = AppsFlyerProxy.this.mUpload15sTask = null;
                }
            }
        };
        LogUtils.i("buychannelsdk", "[AppsFlyerProxy::upload15Second] 15内，未返回appflyer和GA数据, 则判断为自然用户");
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(this.mUpload15sTask, 15000);
    }

    public void cancelupload15S(String referrer, String conversionDataJsonStr) {
        if (this.mUpload15sTask != null) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mUpload15sTask);
            this.mUpload15sTask = null;
            LogUtils.i("buychannelsdk", "[AppsFlyerProxy::cancelupload15S] ");
        }
    }
}

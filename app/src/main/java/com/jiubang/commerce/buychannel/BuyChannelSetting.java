package com.jiubang.commerce.buychannel;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.CustomAlarmManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.UserStatistics;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.database.BuychannelDbHelpler;
import com.jiubang.commerce.buychannel.buyChannel.manager.ServerCheckHelper;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;

public class BuyChannelSetting {
    private static BuyChannelSetting sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Runnable mRefreshUserTagTask;
    private SharedPreferences mSp;

    public enum ChannelFrom {
        from_ga,
        from_appsflyer,
        from_usertag,
        from_client,
        un_known,
        from_oldUser,
        from_oldUser_usertag
    }

    public static BuyChannelSetting getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BuyChannelSetting.class) {
                if (sInstance == null) {
                    sInstance = new BuyChannelSetting(context);
                }
            }
        }
        return sInstance;
    }

    private BuyChannelSetting(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mSp = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
    }

    public void setBuyChannel(String buyChannel, ChannelFrom channel, UserTypeInfo.FirstUserType userType, UserTypeInfo.SecondUserType juserType, String campaign, String campaignId, String associatedObj, String agency, String conversionDataJsonStr, String referrer, String tag, SetBuyChannelListener listener) {
        BuyChannelDataMgr buyChannelDataMgr = BuyChannelDataMgr.getInstance(this.mContext);
        if (userType == null || juserType == null) {
            if (channel.equals(ChannelFrom.from_ga.toString())) {
                StatisticsDebug.Statistic103Params statistic103ParamsP5 = new StatisticsDebug.Statistic103Params();
                statistic103ParamsP5.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(5).buychannel(buyChannel);
                StatisticsDebug.upload(this.mContext, statistic103ParamsP5);
                return;
            }
            return;
        }
        switch (AnonymousClass2.$SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType[userType.ordinal()]) {
            case BuychannelDbHelpler.DB_VERSION_MAX /*1*/:
                buyChannelDataMgr.setBuyChannelBean(buyChannel, channel, userType, juserType, conversionDataJsonStr, listener, campaign, campaignId);
                LogUtils.i("buychannelsdk", "[BuyChannelSetting::setBuyChannel] setBuyChannel　success,userType:" + userType.toString());
                if (!TextUtils.isEmpty(associatedObj)) {
                    uploard45(associatedObj, agency, conversionDataJsonStr, referrer, juserType.getValue(), tag, false);
                    return;
                }
                StatisticsDebug.Statistic103Params statistic103ParamsP6 = new StatisticsDebug.Statistic103Params();
                statistic103ParamsP6.code(BuySdkConstants.DEBUG_CODE2).referrer(referrer).position(6).buychannel(buyChannel);
                StatisticsDebug.upload(this.mContext, statistic103ParamsP6);
                return;
            case 2:
                if (BuyChannelUtils.isOldApkBuy(this.mContext) || BuyChannelUtils.isOldFbAdTw(this.mContext)) {
                    if (listener != null) {
                        listener.setBuyChannelFailure("7");
                        return;
                    }
                    return;
                } else if (isFbAdTw(juserType) || BuyChannelUtils.isOldWithCount(this.mContext) || BuyChannelUtils.isOldOrgnic(this.mContext) || BuyChannelUtils.isOldEmpty(this.mContext)) {
                    buyChannelDataMgr.setBuyChannelBean(buyChannel, channel, userType, juserType, conversionDataJsonStr, listener, campaign, campaignId);
                    uploard45(associatedObj, agency, conversionDataJsonStr, referrer, juserType.getValue(), tag, true);
                    LogUtils.i("buychannelsdk", "[BuyChannelSetting::setBuyChannel] setBuyChannel　success,userType:" + userType.toString());
                    return;
                } else if (listener != null) {
                    listener.setBuyChannelFailure("7");
                    return;
                } else {
                    return;
                }
            case 3:
                if (BuyChannelUtils.isOldOrgnic(this.mContext) || BuyChannelUtils.isOldEmpty(this.mContext)) {
                    buyChannelDataMgr.setBuyChannelBean(buyChannel, channel, userType, juserType, conversionDataJsonStr, listener, campaign, campaignId);
                    uploard45(associatedObj, agency, conversionDataJsonStr, referrer, juserType.getValue(), tag, false);
                    LogUtils.i("buychannelsdk", "[BuyChannelSetting::setBuyChannel] setBuyChannel　success,userType:" + userType.toString());
                    if (juserType.toString().equals(UserTypeInfo.SecondUserType.WITHCOUNT_NOT_ORGNIC.toString())) {
                        checkOrgnic(userType, campaign, campaignId);
                        return;
                    }
                    return;
                } else if (listener != null) {
                    listener.setBuyChannelFailure("8");
                    return;
                } else {
                    return;
                }
            case 4:
                if (BuyChannelUtils.isOldEmpty(this.mContext)) {
                    buyChannelDataMgr.setBuyChannelBean(buyChannel, channel, userType, juserType, conversionDataJsonStr, listener, campaign, campaignId);
                    uploard45(associatedObj, agency, conversionDataJsonStr, referrer, juserType.getValue(), tag, false);
                    checkOrgnic(userType, campaign, campaignId);
                    LogUtils.i("buychannelsdk", "[BuyChannelSetting::setBuyChannel] setBuyChannel　success,userType:" + userType.toString());
                    return;
                } else if (listener != null) {
                    listener.setBuyChannelFailure("9");
                    return;
                } else {
                    return;
                }
            default:
                if (listener != null) {
                    listener.setBuyChannelFailure((String) null);
                    return;
                }
                return;
        }
    }

    /* renamed from: com.jiubang.commerce.buychannel.BuyChannelSetting$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType = new int[UserTypeInfo.FirstUserType.values().length];

        static {
            try {
                $SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType[UserTypeInfo.FirstUserType.apkbuy.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType[UserTypeInfo.FirstUserType.userbuy.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType[UserTypeInfo.FirstUserType.withCount.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$jiubang$commerce$buychannel$buyChannel$bean$UserTypeInfo$FirstUserType[UserTypeInfo.FirstUserType.organic.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private boolean isFbAdTw(UserTypeInfo.SecondUserType juserType) {
        if (BuyChannelUtils.isOldFbAdTw(this.mContext)) {
            return false;
        }
        if (juserType.equals(UserTypeInfo.SecondUserType.FB_AUTO)) {
            return true;
        }
        if (juserType.equals(UserTypeInfo.SecondUserType.FB_NOTAUTO)) {
            return true;
        }
        if (juserType.equals(UserTypeInfo.SecondUserType.ADWORDS_AUTO)) {
            return true;
        }
        if (juserType.equals(UserTypeInfo.SecondUserType.ADWORDS_NOTAUTO)) {
            return true;
        }
        return false;
    }

    private void uploard45(String associatedObj, String agency, String conversionDataJsonStr, String referrer, int secondUserType, String tag, boolean isAfter19) {
        BuySdkInitParams mInitParams = AppsFlyerProxy.getInstance().getInitParams();
        boolean isGoKeyboard = this.mSp.getBoolean(BuySdkConstants.IS_GOKEYBORAD, false);
        int fun45id = this.mSp.getInt(BuySdkConstants.FUN_ID_45, 0);
        if (mInitParams != null && fun45id == 0) {
            fun45id = mInitParams.mP45FunId;
        }
        UserStatistics.upload45(this.mContext, isGoKeyboard, fun45id, associatedObj, conversionDataJsonStr, referrer, String.valueOf(secondUserType), tag, agency, isAfter19);
    }

    private void checkOrgnic(UserTypeInfo.FirstUserType userType, String campaign, String campaignId) {
        BuyChannelDataMgr.getInstance(this.mContext).setFirstCheckTime(0);
        BuyChannelDataMgr.getInstance(this.mContext).setLastCheckTime(0);
        CustomAlarmManager.getInstance(this.mContext).getAlarm("buychannelsdk").cancelAarm(BuySdkConstants.ALARM_ID);
        LogUtils.i("buychannelsdk", "[BuyChannelSetting::setBuyChannel] 新数据为自然或非自然带量，去服务器进行检查:" + userType.toString());
        SharedPreferences mSp2 = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
        mSp2.edit().putString(BuySdkConstants.NEW_USER_BEFORE, userType.toString()).commit();
        mSp2.edit().putString(BuySdkConstants.CAMPAIGN, campaign).commit();
        mSp2.edit().putString(BuySdkConstants.CAMPAIGN_ID, campaignId).commit();
        startUserTagTask();
    }

    private void startUserTagTask() {
        if (this.mRefreshUserTagTask != null) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mRefreshUserTagTask);
            this.mRefreshUserTagTask = null;
        }
        this.mRefreshUserTagTask = new Runnable() {
            public void run() {
                ServerCheckHelper.getInstance(BuyChannelSetting.this.mContext).startCheckServer(0);
                Runnable unused = BuyChannelSetting.this.mRefreshUserTagTask = null;
            }
        };
        CustomThreadExecutorProxy.getInstance().runOnMainThread(this.mRefreshUserTagTask, 15000);
        if (LogUtils.isShowLog()) {
            LogUtils.i("buychannelsdk", "[BuyChannelSetting::startUserTagTask] 延迟15s启动server-chcek");
        }
    }
}

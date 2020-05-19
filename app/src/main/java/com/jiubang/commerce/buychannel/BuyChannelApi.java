package com.jiubang.commerce.buychannel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.jb.ga0.commerce.util.AppUtils;
import com.jb.ga0.commerce.util.DevHelper;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.UserStatistics;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.manager.InitManager;
import com.jiubang.commerce.buychannel.buyChannel.utils.AppInfoUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;

public class BuyChannelApi {
    /* access modifiers changed from: private */
    public static Context sContext = null;
    /* access modifiers changed from: private */
    public static InitManager sManager = null;
    public static final int sVersionCode = 18;
    public static final String sVersionName = "1.4.2";

    public static void setDebugMode() {
        LogUtils.setShowLog(true);
    }

    public static void init(Application application, final BuySdkInitParams params) {
        LogUtils.d("buychannelsdk", "[BuyChannelApi::init]mChannel:" + params.mChannel + ",mP45FunId:" + params.mP45FunId + ",mUsertypeProtocalCId:" + params.mUsertypeProtocalCId + ",mIsOldUserWithoutSdk:" + params.mIsOldUserWithoutSdk + ",mOldBuyChannel:" + params.mOldBuyChannel + ",mIsGoKeyboard:" + params.mIsGoKeyboard);
        DevHelper.setRegister(application.getPackageName());
        sContext = application.getApplicationContext();
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
            public void run() {
                InitManager unused = BuyChannelApi.sManager = InitManager.getInstance(BuyChannelApi.sContext);
                BuyChannelApi.sManager.setStatisticStateListener();
                BuyChannelApi.sManager.check45(2);
                AppInfoUtils.getAdvertisingId(BuyChannelApi.sContext);
                SharedPreferences mSp = BuyChannelDataMgr.getInstance(BuyChannelApi.sContext).getSharedPreferences(BuyChannelApi.sContext);
                mSp.edit().putBoolean(BuySdkConstants.IS_GOKEYBORAD, params.mIsGoKeyboard).commit();
                mSp.edit().putInt(BuySdkConstants.FUN_ID_45, params.mP45FunId).commit();
                int channel = params.mChannel;
                if (channel > 9999 && channel < 20000) {
                    String associatedObj = null;
                    String apkBuyChannel = "buychannel_apk_" + channel;
                    if (params.mIsApkUpLoad45) {
                        associatedObj = UserStatistics.get45AssociatedObjOther(apkBuyChannel, (String) null, channel + BuildConfig.FLAVOR, (String) null);
                    }
                    BuyChannelSetting.getInstance(BuyChannelApi.sContext).setBuyChannel(apkBuyChannel, BuyChannelSetting.ChannelFrom.from_client, UserTypeInfo.FirstUserType.apkbuy, UserTypeInfo.SecondUserType.APK_USERBUY, (String) null, (String) null, associatedObj, (String) null, (String) null, (String) null, (String) null, (SetBuyChannelListener) null);
                }
                if (params.mIsOldUserWithoutSdk && !BuyChannelApi.sManager.isUpdateBuyChannelSdk()) {
                    BuyChannelApi.sManager.updateOldUser(BuyChannelApi.sContext, params.mOldBuyChannel, params.mIsOldUserWithoutSdk, params.mUsertypeProtocalCId);
                }
                BuyChannelApi.sManager.saveSdkVersion();
            }
        });
        String pkgName = params.mProcessName != null ? params.mProcessName : application.getPackageName();
        String currentProcess = AppUtils.getCurrProcessName(application);
        if (LogUtils.isShowLog()) {
            LogUtils.i("buychannelsdk", "[BuyChannelApi::init] pkgName:" + pkgName + ", currentProcess:" + currentProcess);
        }
        if (params.mIsOldUserWithoutSdk && TextUtils.isEmpty(params.mOldBuyChannel)) {
            LogUtils.w("buychannelsdk", "[BuyChannelApi::init] 老用户的买量渠道不应该空！");
        }
        if (!params.mIsOldUserWithoutSdk && !TextUtils.isEmpty(params.mOldBuyChannel)) {
            LogUtils.w("buychannelsdk", "[BuyChannelApi::init] 新用户不应该有买量渠道！");
        }
        if (pkgName != null && pkgName.equals(currentProcess)) {
            LogUtils.d("buychannelsdk", "[BuyChannelApi::init]: currentProcess:" + currentProcess.toString());
            AppsFlyerProxy.getInstance().init(application, params);
        }
    }

    public static void registerBuyChannelListener(Context context, IBuyChannelUpdateListener listener) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("buychannelsdk", "[BuyChannelApi::registerBuyChannelListener] listener:" + listener.getClass().getName());
        }
        BuyChannelDataMgr.getInstance(context).registerBuyChannelListener(listener);
    }

    public static void unregisterBuyChannelListener(Context context, IBuyChannelUpdateListener listener) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("buychannelsdk", "[BuyChannelApi::unregisterBuyChannelListener] listener:" + listener.getClass().getName());
        }
        BuyChannelDataMgr.getInstance(context).unregisterBuyChannelListener(listener);
    }

    public static BuyChannelBean getBuyChannelBean(Context context) {
        BuyChannelBean buyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (LogUtils.isShowLog() && buyChannelBean != null) {
            LogUtils.i("buychannelsdk", "获取buyChannel,[BuyChannelApi::getBuyChannelBean] BuyChannelBean:" + buyChannelBean.toString());
        }
        return buyChannelBean != null ? buyChannelBean : new BuyChannelBean();
    }

    public static String transformUrl(Context context, String url) {
        String userType = null;
        BuyChannelBean buyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (buyChannelBean != null) {
            userType = buyChannelBean.getFirstUserType();
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        buffer.append(BuySdkConstants.SEPARATOR);
        buffer.append("from_3g_channel%3d");
        buffer.append(userType);
        LogUtils.d("buychannelsdk", "[BuyChannelApi::transformUrl]带量拼接链接：" + buffer.toString());
        return buffer.toString();
    }

    public static void setOldUser(String oldBuyChannel, boolean isOldUserWithoutSdk) {
        LogUtils.d("buychannelsdk", "[BuyChannelApi::setOldUser]调用设置老用户接口：oldBuyChannel：" + oldBuyChannel + ",isOldUserWithoutSdk:" + isOldUserWithoutSdk);
        InitManager manager = InitManager.getInstance(sContext);
        if (isOldUserWithoutSdk && !manager.isUpdateBuyChannelSdk()) {
            manager.updateOldUser(sContext, oldBuyChannel, isOldUserWithoutSdk, AppsFlyerProxy.getInstance().getInitParams().mUsertypeProtocalCId);
        }
    }

    public static String getReferrer(Context context) {
        String referrer = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getString(BuySdkConstants.REFERRER, (String) null);
        LogUtils.d("buychannelsdk", "[BuyChannelApi::getReferrer]：Referrer：" + referrer);
        return referrer;
    }
}

package com.jiubang.commerce.ad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.avoid.AdAvoider;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.cache.AdCachePool;
import com.jiubang.commerce.ad.cache.config.CacheAdConfig;
import com.jiubang.commerce.ad.h5.H5AdActivity;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.ad.intelligent.api.IntelligentApi;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.ad.manager.AdImageManager;
import com.jiubang.commerce.ad.manager.AdModuleShowCountManager;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.params.AdSet;
import com.jiubang.commerce.ad.params.ClientParams;
import com.jiubang.commerce.ad.params.PresolveParams;
import com.jiubang.commerce.ad.params.UserTagParams;
import com.jiubang.commerce.ad.sdk.AdmobAdConfig;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;
import com.jiubang.commerce.ad.url.AdUrlPreParseLoadingActivity;
import com.jiubang.commerce.ad.url.AdUrlPreParseTask;
import com.jiubang.commerce.ad.url.AppDetailsJumpUtil;
import com.jiubang.commerce.ad.window.ExitGoogleWindowManager;
import com.jiubang.commerce.database.model.AdvertFilterBean;
import com.jiubang.commerce.database.table.AdvertFilterTable;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.statistics.adinfo.AdInfoStatisticManager;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.ImageUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.List;

public class AdSdkApi {
    public static final String DATA_CHANNEL_ACE_CLEANER = "39";
    public static final String DATA_CHANNEL_ACE_SECURITY = "41";
    public static final String DATA_CHANNEL_ACE_SECURITY_PLUS = "56";
    public static final String DATA_CHANNEL_AD_SDK = "7";
    public static final String DATA_CHANNEL_ALPHA_SECURITY = "54";
    public static final String DATA_CHANNEL_APP_LOCKER = "18";
    public static final String DATA_CHANNEL_BLUE_BATTERY = "58";
    public static final String DATA_CHANNEL_BUBBLE_FISH = "59";
    public static final String DATA_CHANNEL_COMMON = "1";
    public static final String DATA_CHANNEL_COOL_SMS = "51";
    public static final String DATA_CHANNEL_CUCKOO_NEWS = "27";
    public static final String DATA_CHANNEL_DAILY_REC = "16";
    public static final String DATA_CHANNEL_DOOM_RACING = "57";
    public static final String DATA_CHANNEL_DOUBLE_OPEN = "26";
    public static final String DATA_CHANNEL_GAME_CENTER = "3";
    public static final String DATA_CHANNEL_GOMO_GAME = "31";
    public static final String DATA_CHANNEL_GO_BFLASHLIGHT = "38";
    public static final String DATA_CHANNEL_GO_CALLER = "34";
    public static final String DATA_CHANNEL_GO_DARLING = "36";
    public static final String DATA_CHANNEL_GO_DIAL = "25";
    public static final String DATA_CHANNEL_GO_DOUBLE_OPEN = "42";
    public static final String DATA_CHANNEL_GO_KEYBOARD = "2";
    public static final String DATA_CHANNEL_GO_KEYBOARD_EMBED = "22";
    public static final String DATA_CHANNEL_GO_KEYBOARD_OLD = "8";
    public static final String DATA_CHANNEL_GO_KEYBOARD_PRO = "30";
    public static final String DATA_CHANNEL_GO_LAUNCHER = "9";
    public static final String DATA_CHANNEL_GO_LOCKER = "5";
    public static final String DATA_CHANNEL_GO_LOCKER_CN = "13";
    public static final String DATA_CHANNEL_GO_MUSIC_PLAYER = "29";
    public static final String DATA_CHANNEL_GO_NETWORK_SECURITY = "49";
    public static final String DATA_CHANNEL_GO_POWER_MASTER = "15";
    public static final String DATA_CHANNEL_GO_POWER_MASTER_PRO = "35";
    public static final String DATA_CHANNEL_GO_SECURITY = "28";
    public static final String DATA_CHANNEL_GO_SMS = "4";
    public static final String DATA_CHANNEL_GO_TOUCHER = "60";
    public static final String DATA_CHANNEL_GO_TRANSFER = "37";
    public static final String DATA_CHANNEL_GO_WEATHER = "11";
    public static final String DATA_CHANNEL_HI_KEYBOARD = "45";
    public static final String DATA_CHANNEL_KITTY_PLAY = "21";
    public static final String DATA_CHANNEL_LETS_CLEAN = "55";
    public static final String DATA_CHANNEL_MUSIC_PLAYER_MASTER = "46";
    public static final String DATA_CHANNEL_MY_WEATHER_REPORTER = "50";
    public static final String DATA_CHANNEL_NEXT_BROWSER = "23";
    public static final String DATA_CHANNEL_NEXT_LAUNCHER = "10";
    public static final String DATA_CHANNEL_NEXT_LAUNCHER_PAY = "40";
    public static final String DATA_CHANNEL_ONE_KEY_LOCKER = "24";
    public static final String DATA_CHANNEL_POWER_MASTER_PLUS = "43";
    public static final String DATA_CHANNEL_PRIVACY_BUTLER = "48";
    public static final String DATA_CHANNEL_SIMPLE_CLOCK = "47";
    public static final String DATA_CHANNEL_STIKER_PHOTO_EDITOR = "53";
    public static final String DATA_CHANNEL_SUPER_SECURITY = "61";
    public static final String DATA_CHANNEL_SUPER_WALLPAPER = "17";
    public static final String DATA_CHANNEL_S_PHOTO_EDITOR = "44";
    public static final String DATA_CHANNEL_V_LAUNCHER = "52";
    public static final String DATA_CHANNEL_ZERO_CAMERA = "19";
    public static final String DATA_CHANNEL_ZERO_FLASHLIGHT = "33";
    public static final String DATA_CHANNEL_ZERO_LAUNCHER = "6";
    public static final String DATA_CHANNEL_ZERO_LAUNCHER_FOR_APK = "20";
    public static final String DATA_CHANNEL_ZERO_SMS = "12";
    public static final String DATA_CHANNEL_ZERO_SPEED = "14";
    public static final String ENTRANCE_ID_MAIN = "1";
    public static final String ENTRANCE_ID_THEME = "2";
    public static final String LOG_TAG = "Ad_SDK";
    public static final String PRODUCT_ID_ACE_CLEANER = "47";
    public static final String PRODUCT_ID_ACE_SECURITY = "49";
    public static final String PRODUCT_ID_ACE_SECURITY_PLUS = "64";
    public static final String PRODUCT_ID_ALPHA_SECURITY = "62";
    public static final String PRODUCT_ID_APPCENTER = "3";
    public static final String PRODUCT_ID_APP_LOCKER = "20";
    public static final String PRODUCT_ID_BLUE_BATTERY = "66";
    public static final String PRODUCT_ID_BUBBLE_FISH = "67";
    public static final String PRODUCT_ID_COOL_SMS = "59";
    public static final String PRODUCT_ID_CUCKOO_NEWS = "36";
    public static final String PRODUCT_ID_DAILY_REC = "18";
    public static final String PRODUCT_ID_DOOM_RACING = "65";
    public static final String PRODUCT_ID_DOUBLE_OPEN = "35";
    public static final String PRODUCT_ID_GAME_CENTER = "17";
    public static final String PRODUCT_ID_GOMO_GAME = "40";
    public static final String PRODUCT_ID_GO_BFLASHLIGHT = "46";
    public static final String PRODUCT_ID_GO_CALLER = "42";
    public static final String PRODUCT_ID_GO_DARLING = "44";
    public static final String PRODUCT_ID_GO_DIAL = "34";
    public static final String PRODUCT_ID_GO_DOUBLE_OPEN = "50";
    public static final String PRODUCT_ID_GO_KEYBOARD = "4";
    public static final String PRODUCT_ID_GO_KEYBOARD_EMBED = "31";
    public static final String PRODUCT_ID_GO_KEYBOARD_OLD = "9";
    public static final String PRODUCT_ID_GO_KEYBOARD_PRO = "39";
    public static final String PRODUCT_ID_GO_LAUNCHER = "5";
    public static final String PRODUCT_ID_GO_LAUNCHER_APPCENTER = "2";
    public static final String PRODUCT_ID_GO_LOCKER = "7";
    public static final String PRODUCT_ID_GO_LOCKER_CN = "14";
    public static final String PRODUCT_ID_GO_MUSIC_PLAYER = "38";
    public static final String PRODUCT_ID_GO_NETWORK_SECURITY = "57";
    public static final String PRODUCT_ID_GO_POWER_MASTER = "16";
    public static final String PRODUCT_ID_GO_POWER_MASTER_PRO = "43";
    public static final String PRODUCT_ID_GO_SECURITY = "37";
    public static final String PRODUCT_ID_GO_SMS = "6";
    public static final String PRODUCT_ID_GO_TOUCHER = "68";
    public static final String PRODUCT_ID_GO_TRANSFER = "45";
    public static final String PRODUCT_ID_GO_WEATHER = "12";
    public static final String PRODUCT_ID_HI_KEYBOARD = "53";
    public static final String PRODUCT_ID_KEYBOARD_LAB = "90";
    public static final String PRODUCT_ID_KITTY_PLAY = "10";
    public static final String PRODUCT_ID_LETS_CLEAN = "63";
    public static final String PRODUCT_ID_MUSIC_PLAYER_MASTER = "54";
    public static final String PRODUCT_ID_MY_WEATHER_REPORTER = "58";
    public static final String PRODUCT_ID_NEXT_BROWSER = "32";
    public static final String PRODUCT_ID_NEXT_LAUNCHER = "11";
    public static final String PRODUCT_ID_NEXT_LAUNCHER_PAY = "48";
    public static final String PRODUCT_ID_ONE_KEY_LOCKER = "33";
    public static final String PRODUCT_ID_POWER_MASTER_PLUS = "51";
    public static final String PRODUCT_ID_PRIVACY_BUTLER = "56";
    public static final String PRODUCT_ID_SIMPLE_CLOCK = "55";
    public static final String PRODUCT_ID_STIKER_PHOTO_EDITOR = "61";
    public static final String PRODUCT_ID_SUPER_SECURITY = "69";
    public static final String PRODUCT_ID_SUPER_WALLPAPER = "19";
    public static final String PRODUCT_ID_S_PHOTO_EDITOR = "52";
    public static final String PRODUCT_ID_T_ME_THEME = "23";
    public static final String PRODUCT_ID_V_LAUNCHER = "60";
    public static final String PRODUCT_ID_ZERO_CAMERA = "21";
    public static final String PRODUCT_ID_ZERO_FLASHLIGHT = "41";
    public static final String PRODUCT_ID_ZERO_LAUNCHER = "8";
    public static final String PRODUCT_ID_ZERO_LAUNCHER_APPCENTER = "1";
    public static final String PRODUCT_ID_ZERO_LAUNCHER_FOR_APK = "22";
    public static final String PRODUCT_ID_ZERO_SMS = "13";
    public static final String PRODUCT_ID_ZERO_SPEED = "15";
    public static final String UNABLE_TO_RETRIEVE = "UNABLE-TO-RETRIEVE";

    @Deprecated
    public static void initSDK(Context context, String processName, String goId, String cId, String googleId, String channel, String dataChannel, String entranceId) {
        if (!AdSdkManager.isShieldAdSdk()) {
            if (StringUtils.isEmpty(entranceId)) {
                entranceId = "1";
            }
            int c = StringUtils.toInteger(channel, 0).intValue();
            AdSdkManager.initSDK(context, processName, goId, cId, googleId, c <= 0 ? "200" : String.valueOf(c), dataChannel, entranceId, (ClientParams) null);
        }
    }

    public static void initSDK(Context context, String processName, String goId, String googleId, String channel, ClientParams params) {
        if (!AdSdkManager.isShieldAdSdk()) {
            int c = StringUtils.toInteger(channel, 0).intValue();
            AdSdkManager.initSDK(context, processName, goId, (String) null, googleId, c <= 0 ? "200" : String.valueOf(c), (String) null, (String) null, params);
        }
    }

    public static void setClientParams(Context context, ClientParams params) {
        ClientParams.save2Local(context, params);
    }

    public static void setGoogleAdvertisingId(Context context, String googleAdvertisingId) {
        AdSdkManager.setGoogleAdvertisingId(context, googleAdvertisingId);
        IntelligentApi.startServiceWithCommand(context, IntelligentApi.COMMAND_SET_GAID, new String[]{googleAdvertisingId});
    }

    public static void setTestServer(boolean isTestServer) {
        if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "[AdSdkApi::setTestServer] isTestServer:" + isTestServer);
        }
        AdSdkRequestHeader.setTestServer(isTestServer);
    }

    public static void setEnableLog(boolean onOff) {
        LogUtils.setShowLog(onOff);
    }

    public static void setShieldAdSdk() {
        if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "AdSdkApi::setShieldAdSdk()");
        }
        AdSdkManager.setShieldAdSdk(true);
    }

    public static void setShowActivationGuideWindow() {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "AdSdkApi::setShowActivationGuideWindow()");
        }
        if (!AdSdkManager.isShieldAdSdk()) {
            AdSdkManager.getInstance().setIsShowActivationGuideWindow(true);
        }
    }

    public static void loadAdBean(AdSdkParamsBuilder adSdkParams) {
        AdCachePool pool;
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + adSdkParams.mVirtualModuleId + "]AdSdkApi::loadAdBean(virtualModuleId:" + adSdkParams.mVirtualModuleId + ", returnAdCount:" + adSdkParams.mReturnAdCount + ", isNeedDownloadIcon:" + adSdkParams.mIsNeedDownloadIcon + ", isNeedDownloadBanner:" + adSdkParams.mIsNeedDownloadBanner + ", isNeedPreResolve:" + adSdkParams.mIsNeedPreResolve + ", isRequestData:" + adSdkParams.mIsRequestData + ", isPreResolveBeforeShow:" + adSdkParams.mIsPreResolveBeforeShow + ", buyuserchannel:" + adSdkParams.mBuyuserchannel + ", cdays:" + (adSdkParams.mCdays != null ? Integer.valueOf(adSdkParams.mCdays.intValue()) : "null") + ")");
        }
        if (!AdSdkManager.isShieldAdSdk()) {
            if (adSdkParams != null && adSdkParams.mIsUploadClientAdRequest) {
                AdSdkOperationStatistic.uploadClientAdRequest(adSdkParams.mContext, adSdkParams.mTabCategory, String.valueOf(adSdkParams.mVirtualModuleId));
            }
            if (adSdkParams.mApplyAdCache && (pool = AdCachePool.getInstance(adSdkParams.mContext)) != null) {
                pool.informClientLoadAdBean();
            }
            AdSdkManager.getInstance().loadAdBean(adSdkParams);
        }
    }

    @Deprecated
    public static void loadAdBean(Context context, int virtualModuleId, int returnAdCount, String packageNames, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, boolean isRequestData, AdSet filterAdSourceArray, String tabCategory, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
        loadAdBean(context, virtualModuleId, returnAdCount, packageNames, isNeedDownloadIcon, isNeedDownloadBanner, isNeedPreResolve, isPreResolveBeforeShow, isRequestData, true, filterAdSourceArray, (String) null, (Integer) null, -1, tabCategory, (AdmobAdConfig) null, loadAdvertDataListener);
    }

    @Deprecated
    public static void loadAdBean(Context context, int virtualModuleId, int returnAdCount, String packageNames, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, boolean isRequestData, AdSet filterAdSourceArray, String buyuserchannel, Integer cdays, String tabCategory, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
        loadAdBean(context, virtualModuleId, returnAdCount, packageNames, isNeedDownloadIcon, isNeedDownloadBanner, isNeedPreResolve, isPreResolveBeforeShow, isRequestData, true, filterAdSourceArray, buyuserchannel, cdays, -1, tabCategory, (AdmobAdConfig) null, loadAdvertDataListener);
    }

    @Deprecated
    public static void loadAdBean(Context context, int virtualModuleId, int returnAdCount, String packageNames, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, boolean isRequestData, AdSet filterAdSourceArray, String buyuserchannel, Integer cdays, int shownCount, String tabCategory, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
        loadAdBean(context, virtualModuleId, returnAdCount, packageNames, isNeedDownloadIcon, isNeedDownloadBanner, isNeedPreResolve, isPreResolveBeforeShow, isRequestData, true, filterAdSourceArray, buyuserchannel, cdays, shownCount, tabCategory, (AdmobAdConfig) null, loadAdvertDataListener);
    }

    @Deprecated
    public static void loadAdBean(Context context, int virtualModuleId, int returnAdCount, String packageNames, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, boolean isRequestData, boolean isAddFilterPackageNames, AdSet filterAdSourceArray, String buyuserchannel, Integer cdays, int shownCount, String tabCategory, AdmobAdConfig admobAdConfig, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
        loadAdBean(new AdSdkParamsBuilder.Builder(context, virtualModuleId, tabCategory, loadAdvertDataListener).returnAdCount(returnAdCount).isNeedDownloadIcon(isNeedDownloadIcon).isNeedDownloadBanner(isNeedDownloadBanner).isNeedPreResolve(isNeedPreResolve).isPreResolveBeforeShow(isPreResolveBeforeShow).isRequestData(isRequestData).isAddFilterPackageNames(isAddFilterPackageNames).filterAdSourceArray(filterAdSourceArray).buyuserchannel(buyuserchannel).cdays(cdays).shownCount(shownCount).admobAdConfig(admobAdConfig).build());
    }

    public static AdModuleInfoBean getAPIAdSync(Context context, int adCount, int requestAdCount, int onlineAdPosId, boolean isRequestData, List<String> installFilterException) {
        return AdControlManager.getInstance(context).loadOnlineAdInfo(context, (BaseModuleDataItemBean) null, -1, adCount, requestAdCount, onlineAdPosId, isRequestData, true, installFilterException, (AdSdkRequestHeader.S2SParams) null);
    }

    public static AdModuleInfoBean getAPIAdSync(Context context, int adCount, int requestAdCount, int onlineAdPosId, boolean isRequestData, boolean needShownFilter, List<String> installFilterException) {
        return AdControlManager.getInstance(context).loadOnlineAdInfo(context, (BaseModuleDataItemBean) null, -1, adCount, requestAdCount, onlineAdPosId, isRequestData, needShownFilter, installFilterException, (AdSdkRequestHeader.S2SParams) null);
    }

    public static void requestAdData(Context context, List<Integer> virtualModuleIds, AdSdkParamsBuilder apb) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleIds + "]AdSdkApi::requestAdData(virtualModuleId:" + virtualModuleIds + ", buyuserchannel" + apb.mBuyuserchannel + ", cdays:" + (apb.mCdays != null ? Integer.valueOf(apb.mCdays.intValue()) : "null") + ")");
        }
        if (!AdSdkManager.isShieldAdSdk()) {
            AdSdkManager.getInstance().requestAdData(context, virtualModuleIds, apb);
        }
    }

    public static void requestBatchModuleControlInfo(Context context, List<Integer> virtualModuleIds, AdSdkParamsBuilder apb, AdControlManager.IBacthControlListener listener) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleIds + "]AdSdkApi::requestBatchModuleControlInfo(virtualModuleId:" + virtualModuleIds + ", buyuserchannel" + apb.mBuyuserchannel + ", cdays:" + (apb.mCdays != null ? Integer.valueOf(apb.mCdays.intValue()) : "null") + ")");
        }
        if (!AdSdkManager.isShieldAdSdk()) {
            AdSdkManager.getInstance().requestBatchControlInfo(context, virtualModuleIds, apb, true, listener);
        }
    }

    public static void getBatchValidCacheModuleControlInfo(Context context, List<Integer> virtualModuleIds, AdSdkParamsBuilder apb, AdControlManager.IBacthControlListener listener) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleIds + "]AdSdkApi::getBatchValidCacheModuleControlInfo(virtualModuleId:" + virtualModuleIds + ", buyuserchannel" + apb.mBuyuserchannel + ", cdays:" + (apb.mCdays != null ? Integer.valueOf(apb.mCdays.intValue()) : "null") + ")");
        }
        if (!AdSdkManager.isShieldAdSdk()) {
            AdSdkManager.getInstance().requestBatchControlInfo(context, virtualModuleIds, apb, false, listener);
        }
    }

    public static boolean hasAvailableAd(Context context, int virtualModuleId) {
        return AdSdkManager.getInstance().hasAvailableAd(context, virtualModuleId);
    }

    public static void hasAvailableAd(Context context, int virtualModuleId, AdSdkManager.IAdCheckListener adCheckListener) {
        AdSdkManager.getInstance().hasAvailableAd(context, virtualModuleId, adCheckListener);
    }

    public static void preResolveAdvertUrlForIntelligent(Context context, List<AdInfoBean> adInfoList, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        PresolveUtils.preResolveAdvertUrl(context, adInfoList, new PresolveParams.Builder().repeatClickEnable(true).isControlled(true).useCache(true).build(), listener);
    }

    public static void preResolveAdvertUrl(Context context, List<AdInfoBean> adInfoList, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        PresolveUtils.preResolveAdvertUrl(context, adInfoList, new PresolveParams.Builder().repeatClickEnable(false).isControlled(true).useCache(true).uaType(2).build(), listener);
    }

    public static void preResolveAdvertUrlForRealClick(Context context, List<AdInfoBean> adInfoList, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        PresolveUtils.preResolveAdvertUrl(context, adInfoList, new PresolveParams.Builder().repeatClickEnable(false).isControlled(false).useCache(false).uaType(2).build(), listener);
    }

    public static void showAdvert(Context context, AdInfoBean adInfoBean, String tabCategory, String remark) {
        if (adInfoBean != null) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]AdSdkApi::showAdvert(" + "adInfoBean:" + AdSdkLogUtils.getLogString(adInfoBean) + ", tabCategory:" + tabCategory + ", remark:" + remark + ")");
            }
            showAdvertHandle(context, adInfoBean, tabCategory, remark);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "AdSdkApi::showAdvert(error, " + adInfoBean + ", " + tabCategory + ", " + remark + ")", new Throwable());
        }
    }

    public static void clickAdvertWithToast(Context context, AdInfoBean adInfoBean, String tabCategory, String remark, boolean isShowToast) {
        clickAdvertWithToast(context, adInfoBean, tabCategory, remark, isShowToast, true);
    }

    public static void clickAdvertWithToast(Context context, AdInfoBean adInfoBean, String tabCategory, String remark, boolean isShowToast, boolean isShowFloatWindow) {
        if (adInfoBean != null) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]AdSdkApi::clickAdvertWithToast(" + "adInfoBean:" + AdSdkLogUtils.getLogString(adInfoBean) + ", tabCategory:" + tabCategory + ", remark:" + remark + ", isShowToast:" + isShowToast + ", isShowFloatWindow:" + isShowFloatWindow + ")");
            }
            clickAdvertHandle(context, adInfoBean, tabCategory, remark, isShowToast, false, isShowFloatWindow, true);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "AdSdkApi::clickAdvertWithToast(error, " + adInfoBean + ", " + tabCategory + ", " + remark + ", " + isShowToast + ")", new Throwable());
        }
    }

    public static void clickAdvertWithDialog(Context context, AdInfoBean adInfoBean, String tabCategory, String remark, boolean isShowDialog) {
        clickAdvertWithDialog(context, adInfoBean, tabCategory, remark, isShowDialog, false);
    }

    public static void clickAdvertForIntegrawall(Context context, int mapId, int adId, String pkgName, String adUrl, String googlePlayUrl, boolean isShowFloatWindow) {
        AdInfoBean adInfoBean = new AdInfoBean();
        adInfoBean.setPackageName(pkgName);
        adInfoBean.setModuleId(-1);
        adInfoBean.setMapId(mapId);
        adInfoBean.setAdId(adId);
        adInfoBean.setAdUrl(adUrl);
        adInfoBean.setDownUrl(googlePlayUrl);
        adInfoBean.setIsAd(1);
        adInfoBean.setUASwitcher(1);
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]AdSdkApi::clickAdvertForIntegrawall(" + "adInfoBean:" + AdSdkLogUtils.getLogString(adInfoBean) + ", isShowDialog:" + true + ", isShowFloatWindow:" + isShowFloatWindow + ")");
        }
        String packageName = adInfoBean.getPackageName();
        int moduleId = adInfoBean.getModuleId();
        int aId = adInfoBean.getAdId();
        String downloadUrl = adInfoBean.getDownUrl();
        int isAd = adInfoBean.getIsAd();
        ParamsBean paramsBean = new ParamsBean();
        paramsBean.setUASwitcher(adInfoBean.getUASwitcher());
        AppDetailsJumpUtil.gotoAppDetailsDialog(context, paramsBean, packageName, moduleId, mapId, aId, adUrl, downloadUrl, isAd, true, isShowFloatWindow);
    }

    public static void clickAdvertWithDialog(Context context, AdInfoBean adInfoBean, String tabCategory, String remark, boolean isShowDialog, boolean isShowFloatWindow) {
        if (adInfoBean != null) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]AdSdkApi::clickAdvertWithDialog(" + "adInfoBean:" + AdSdkLogUtils.getLogString(adInfoBean) + ", tabCategory:" + tabCategory + ", remark:" + remark + ", isShowDialog:" + isShowDialog + ", isShowFloatWindow:" + isShowFloatWindow + ")");
            }
            clickAdvertHandle(context, adInfoBean, tabCategory, remark, false, isShowDialog, isShowFloatWindow, true);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "clickAdvertWithDialog(error, " + adInfoBean + ", " + tabCategory + ", " + remark + ")", new Throwable());
        }
    }

    public static void clickFtpAdvertHandle(Context context, AdInfoBean adInfoBean, String tabCategory, String remark) {
        if (adInfoBean != null) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]AdSdkApi::clickFtpAdvertHandle(" + "adInfoBean:" + AdSdkLogUtils.getLogString(adInfoBean) + ", tabCategory:" + tabCategory + ", remark:" + remark + ", isShowDialog:" + false + ", isShowFloatWindow:" + false + ")");
            }
            clickAdvertHandle(context, adInfoBean, tabCategory, remark, false, false, false, false);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "clickFtpAdvertHandle(error, " + adInfoBean + ", " + tabCategory + ", " + remark + ")", new Throwable());
        }
    }

    private static void showAdvertHandle(Context context, AdInfoBean adInfoBean, String tabCategory, String remark) {
        if (adInfoBean != null) {
            AdModuleShowCountManager.getInstance(context).recordShow(adInfoBean.getVirtualModuleId());
            final AdvertFilterTable advertFilterTable = AdvertFilterTable.getInstance(context);
            final String str = tabCategory;
            final AdInfoBean adInfoBean2 = adInfoBean;
            final Context context2 = context;
            final String str2 = remark;
            new AdSdkThread(new Runnable() {
                public void run() {
                    String tab;
                    if (TextUtils.isEmpty(str)) {
                        tab = String.valueOf(adInfoBean2.getVirtualModuleId());
                    } else {
                        tab = str;
                    }
                    int virtualModuleId = adInfoBean2.getVirtualModuleId();
                    int moduleId = adInfoBean2.getModuleId();
                    int mapId = adInfoBean2.getMapId();
                    int aId = adInfoBean2.getAdId();
                    String packageName = adInfoBean2.getPackageName();
                    String showCallUrl = adInfoBean2.getShowCallUrl();
                    String advDataSource = AdSdkOperationStatistic.getAdvDataSource(adInfoBean2.getAdvDataSource(), adInfoBean2.getAdId());
                    String entrance = String.valueOf(adInfoBean2.getOnlineAdvType());
                    long bannerID = adInfoBean2.getGomoAdBannerId();
                    AdSdkOperationStatistic.uploadAdShowStaticstic(context2, String.valueOf(mapId), entrance, tab, String.valueOf(moduleId), advDataSource, String.valueOf(aId), str2, showCallUrl);
                    if (bannerID != -1) {
                        AdSdkOperationStatistic.uploadMaterialAdF00(context2, String.valueOf(mapId), String.valueOf(aId), String.valueOf(bannerID), String.valueOf(moduleId));
                    }
                    AdSdkManager.recordShowAdToCacheAdData(adInfoBean2);
                    AdvertFilterBean tempFilterBean = advertFilterTable.isDataExist(packageName, String.valueOf(virtualModuleId));
                    if (tempFilterBean != null) {
                        tempFilterBean.setmShowCount(tempFilterBean.getmShowCount() + 1);
                        advertFilterTable.update(tempFilterBean);
                        return;
                    }
                    AdvertFilterBean advertFilterBean = new AdvertFilterBean();
                    advertFilterBean.setmPackageName(packageName);
                    advertFilterBean.setmMoudleId(String.valueOf(virtualModuleId));
                    advertFilterBean.setmAdvertPos(String.valueOf(aId));
                    advertFilterBean.setmShowCount(1);
                    advertFilterBean.setmSaveTime(System.currentTimeMillis());
                    advertFilterTable.insert(advertFilterBean);
                }
            }).start();
        }
    }

    private static void clickAdvertHandle(Context context, AdInfoBean adInfoBean, String tabCategory, String remark, boolean isShowToast, boolean isShowDialog, boolean isShowFloatWindow, boolean isOpen) {
        if (adInfoBean != null) {
            if (TextUtils.isEmpty(tabCategory)) {
                tabCategory = String.valueOf(adInfoBean.getVirtualModuleId());
            }
            String packageName = adInfoBean.getPackageName();
            int moduleId = adInfoBean.getModuleId();
            int mapId = adInfoBean.getMapId();
            int aId = adInfoBean.getAdId();
            String downloadUrl = adInfoBean.getDownUrl();
            String adUrl = adInfoBean.getAdUrl();
            int isAd = adInfoBean.getIsAd();
            String clickCallUrl = adInfoBean.getClickCallUrl();
            String installCallUrl = adInfoBean.getInstallCallUrl();
            ParamsBean paramsBean = new ParamsBean();
            paramsBean.setUASwitcher(adInfoBean.getUASwitcher());
            paramsBean.setUAType(2);
            String advDataSource = AdSdkOperationStatistic.getAdvDataSource(adInfoBean.getAdvDataSource(), adInfoBean.getAdId());
            String entrance = String.valueOf(adInfoBean.getOnlineAdvType());
            AdModuleShowCountManager.getInstance(context).recordClick(adInfoBean.getVirtualModuleId());
            if (isOpen) {
                if (adInfoBean.getIsH5Adv()) {
                    H5AdActivity.openLink(context, adUrl);
                } else if (!isShowToast && !isShowDialog) {
                    AppDetailsJumpUtil.gotoAppDetails(context, paramsBean, packageName, moduleId, mapId, aId, adUrl, downloadUrl, isAd, true, false, "", isShowFloatWindow);
                } else if (isShowToast) {
                    AppDetailsJumpUtil.gotoAppDetails(context, paramsBean, packageName, moduleId, mapId, aId, adUrl, downloadUrl, isAd, true, true, ResourcesProvider.getInstance(context).getString("ad_click_tip"), isShowFloatWindow);
                } else if (isShowDialog) {
                    AppDetailsJumpUtil.gotoAppDetailsDialog(context, paramsBean, packageName, moduleId, mapId, aId, adUrl, downloadUrl, isAd, true, isShowFloatWindow);
                }
            }
            AdSdkOperationStatistic.uploadAdDownloadClickStaticstic(context, String.valueOf(mapId), entrance, packageName, tabCategory, String.valueOf(moduleId), advDataSource, String.valueOf(aId), remark, clickCallUrl, installCallUrl);
            long bannerID = adInfoBean.getGomoAdBannerId();
            if (bannerID != -1) {
                AdSdkOperationStatistic.uploadMaterialAdA00(context, String.valueOf(mapId), String.valueOf(aId), String.valueOf(bannerID), String.valueOf(moduleId));
            }
            IntelligentApi.informGomoAdClick(context, adInfoBean);
        }
    }

    public static void sdkAdClickStatistic(Context context, BaseModuleDataItemBean baseModuleDataItemBean, SdkAdSourceAdWrapper adWrapper, String tabCategory) {
        if (baseModuleDataItemBean != null && adWrapper != null) {
            IntelligentApi.startServiceWithCommand(context, IntelligentApi.COMMAND_SELF_OPEN_GP, new String[]{"delay"});
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]AdSdkApi::sdkAdClickStatistic(" + "baseModuleDataItemBean:" + AdSdkLogUtils.getSimpleLogString(baseModuleDataItemBean) + ", adWrapper->AppKey:" + adWrapper.getAppKey() + ")");
            }
            AdModuleShowCountManager.getInstance(context).recordClick(baseModuleDataItemBean.getVirtualModuleId());
            if (TextUtils.isEmpty(tabCategory)) {
                tabCategory = String.valueOf(baseModuleDataItemBean.getVirtualModuleId());
            }
            String remark = baseModuleDataItemBean.getStatistics105Remark();
            int moduleId = baseModuleDataItemBean.getModuleId();
            int aId = baseModuleDataItemBean.getAdvPositionId();
            Context context2 = context;
            String str = tabCategory;
            AdSdkOperationStatistic.uploadAdDownloadClickStaticstic(context2, adWrapper.getAppKey(), String.valueOf(baseModuleDataItemBean.getOnlineAdvType()), (String) null, str, String.valueOf(moduleId), AdSdkOperationStatistic.getAdvDataSource(baseModuleDataItemBean.getAdvDataSource(), baseModuleDataItemBean.getOnlineAdvPositionId()), String.valueOf(aId), remark, (String) null, (String) null);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "AdSdkApi::sdkAdClickStatistic(baseModuleDataItemBean:" + baseModuleDataItemBean + ", adWrapper:" + adWrapper + ")", new Throwable());
        }
    }

    public static void sdkAdShowStatistic(Context context, BaseModuleDataItemBean baseModuleDataItemBean, SdkAdSourceAdWrapper adWrapper, String tabCategory) {
        if (baseModuleDataItemBean != null && adWrapper != null) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]AdSdkApi::sdkAdShowStatistic(" + "baseModuleDataItemBean:" + AdSdkLogUtils.getSimpleLogString(baseModuleDataItemBean) + ", adWrapper->AppKey:" + adWrapper.getAppKey() + ")");
            }
            AdModuleShowCountManager.getInstance(context).recordShow(baseModuleDataItemBean.getVirtualModuleId());
            if (TextUtils.isEmpty(tabCategory)) {
                tabCategory = String.valueOf(baseModuleDataItemBean.getVirtualModuleId());
            }
            String remark = baseModuleDataItemBean.getStatistics105Remark();
            int moduleId = baseModuleDataItemBean.getModuleId();
            int aId = baseModuleDataItemBean.getAdvPositionId();
            Context context2 = context;
            String str = tabCategory;
            AdSdkOperationStatistic.uploadAdShowStaticstic(context2, adWrapper.getAppKey(), String.valueOf(baseModuleDataItemBean.getOnlineAdvType()), str, String.valueOf(moduleId), AdSdkOperationStatistic.getAdvDataSource(baseModuleDataItemBean.getAdvDataSource(), baseModuleDataItemBean.getOnlineAdvPositionId()), String.valueOf(aId), remark, (String) null);
            statisticAdShowInfo(context, adWrapper);
        } else if (LogUtils.isShowLog()) {
            LogUtils.w("Ad_SDK", "AdSdkApi::sdkAdShowStatistic(baseModuleDataItemBean:" + baseModuleDataItemBean + ", adWrapper:" + adWrapper + ")", new Throwable());
        }
    }

    private static void statisticAdShowInfo(Context context, SdkAdSourceAdWrapper adWrapper) {
        if (adWrapper != null) {
            AdInfoStatisticManager.getInstance(context).statisticImpressionIfCan(context, adWrapper.getAdObject());
        }
    }

    public static void showIAPStatistic(Context context, String sender, String tabCategory) {
        AdSdkOperationStatistic.uploadAdShowStaticstic(context, sender, (String) null, tabCategory, (String) null, (String) null, (String) null, (String) null, (String) null);
    }

    public static void clickIAPStatistic(Context context, String sender, String tabCategory) {
        AdSdkOperationStatistic.uploadAdDownloadClickStaticstic(context, sender, (String) null, (String) null, tabCategory, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null);
    }

    public static void advertDownloadedHandler(Context context, AdInfoBean adInfoBean, String tabCategory, String remark) {
        if (TextUtils.isEmpty(tabCategory)) {
            tabCategory = String.valueOf(adInfoBean.getVirtualModuleId());
        }
        int moduleId = adInfoBean.getModuleId();
        int mapId = adInfoBean.getMapId();
        int aId = adInfoBean.getAdId();
        String advDataSource = AdSdkOperationStatistic.getAdvDataSource(adInfoBean.getAdvDataSource(), adInfoBean.getAdId());
        Context context2 = context;
        String str = tabCategory;
        AdSdkOperationStatistic.uploadAdDownloadedStatistic(context2, String.valueOf(mapId), String.valueOf(aId), str, String.valueOf(moduleId), String.valueOf(adInfoBean.getOnlineAdvType()), advDataSource, remark);
    }

    public static Bitmap getAdImageForSDCard(String imageUrl) {
        return ImageUtils.getBitmapFromSDCard(AdImageManager.getAdImagePath(imageUrl));
    }

    public static void loadAdImage(Context context, String imageUrl, AdImageManager.ILoadSingleAdImageListener adImageListener) {
        AdImageManager.getInstance(context).asynLoadAdImage(imageUrl, adImageListener);
    }

    public static void showParseAdUrlPromptDialog(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, AdUrlPreParseLoadingActivity.class);
            intent.addFlags(268435456);
            intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_IS_PARSE_URL, false);
            context.startActivity(intent);
        }
    }

    public static void hideParseAdUrlPromptDialog() {
        AdUrlPreParseLoadingActivity.finishActivity();
    }

    public static void showGooglePlayFloatWindow(Context context) {
        GoogleMarketUtils.showFloatWindow(context);
    }

    public static void hideGooglePlayFloatWindow(Context context) {
        ExitGoogleWindowManager.getInstance().removeSmallWindow(context);
    }

    public static void informAppOpen(Context context, String packageName) {
        IntelligentApi.startServiceWithCommand(context, IntelligentApi.COMMAND_ON_GP_OPEN, new String[]{packageName});
    }

    public static void informAppClose(Context context, String packageName) {
        IntelligentApi.startServiceWithCommand(context, IntelligentApi.COMMAND_ON_GP_CLOSE, new String[]{packageName});
    }

    public static void configIntelligentPreload(Context context, boolean enable) {
        IntelligentApi.configIntelligentPreload(context, enable);
    }

    public static void setSdkThreadPool(int poolSize) {
        AdSdkThreadExecutorProxy.setThreadPoolSize(poolSize);
    }

    public static void requestUserTags(Context context, AdSdkManager.IAdvertUserTagResultListener listener) {
        AdSdkManager.getInstance().requestUserTags(context, listener);
    }

    public static void requestUserTags(Context context, AdSdkManager.IAdvertUserTagResultListener listener, UserTagParams params) {
        AdSdkManager.getInstance().requestUserTags(context, listener, params);
    }

    public static void requestUserTags(Context context, AdSdkManager.IAdvertUserTagResultListener listener, UserTagParams params, boolean isNew) {
        AdSdkManager.getInstance().requestUserTags(context, listener, params, isNew);
    }

    public static void requestUserTags(Context context, boolean isAsyncTask, int productId, AdSdkManager.IAdvertUserTagResultListener listener) {
        AdSdkManager.getInstance().requestUserTags(context, isAsyncTask, productId, listener);
    }

    public static void enableAdCache(Context context, boolean isConcurrentLoadAd, CacheAdConfig adConfig) {
        AdCachePool pool = AdCachePool.getInstance(context);
        if (pool != null) {
            pool.setStatus(true, false);
            pool.setConcurrentLoadAd(isConcurrentLoadAd);
            pool.setCacheAdConfig(adConfig);
        }
    }

    public static void disableAdCache(Context context, boolean cleanCache) {
        AdCachePool pool = AdCachePool.getInstance(context);
        if (pool != null) {
            pool.setStatus(false, cleanCache);
        }
    }

    public static void destory(Context context) {
        if (!AdSdkManager.isShieldAdSdk()) {
            AdSdkManager.getInstance().destory(context);
        }
    }

    public static void startActivity(Context context, Intent intent) {
        LogUtils.i("Ad_SDK", "startActivity");
        Uri uri = intent.getData();
        if (uri == null) {
            LogUtils.e("Ad_SDK", "startActivity:failed");
            return;
        }
        LogUtils.i("Ad_SDK", "uri:" + uri.toString());
        AdInfoStatisticManager.getInstance(context).setLastUri(context.getApplicationContext(), uri, true);
    }

    public static boolean isNoad(Context context) {
        return AdAvoider.getInstance(context).isNoad();
    }
}

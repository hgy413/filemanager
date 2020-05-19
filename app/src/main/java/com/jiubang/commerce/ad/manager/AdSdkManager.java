package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.AdSdkDebugConfig;
import com.jiubang.commerce.ad.abtest.ABTestManager;
import com.jiubang.commerce.ad.avoid.AdAvoider;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.bean.AdOldUserTagInfoBean;
import com.jiubang.commerce.ad.bean.AdUserTagInfoBean;
import com.jiubang.commerce.ad.bean.MainModuleDataItemBeanWrapper;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineModuleInfoBean;
import com.jiubang.commerce.ad.intelligent.api.IntelligentApi;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.params.ClientParams;
import com.jiubang.commerce.ad.params.ModuleRequestParams;
import com.jiubang.commerce.ad.params.PresolveParams;
import com.jiubang.commerce.ad.params.UserTagParams;
import com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick;
import com.jiubang.commerce.ad.url.AdUrlPreParseTask;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.product.Product;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.statistics.BaseSeq105OperationStatistic;
import com.jiubang.commerce.statistics.adinfo.AdInfoStatisticManager;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.DrawUtils;
import com.jiubang.commerce.utils.FileCacheUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class AdSdkManager {
    private static boolean sIS_SHIELD_AD_SDK = false;
    private static AdSdkManager sInstance = null;
    private boolean mIsShowActivationGuideWindow = false;
    public Product mProduct = new Product(Product.DEFUAL, Product.DEFUAL, Product.DEFUAL);

    public interface IAdCheckListener {
        void onChecked(boolean z);
    }

    public interface IAdControlInterceptor {
        boolean isLoadAd(BaseModuleDataItemBean baseModuleDataItemBean);
    }

    public interface IAdSourceInterceptor {
        boolean continueLoadingAd(BaseModuleDataItemBean baseModuleDataItemBean);
    }

    public interface IAdvertHandleResultListener {
        void onAdvertImageDownloadFinish();

        void onHandleAdvertInfoFinish();
    }

    public interface IAdvertUserTagResultListener {
        void onAdRequestFail(int i);

        void onAdRequestSuccess(AdUserTagInfoBean adUserTagInfoBean);
    }

    public interface ILoadAdvertDataListener {
        void onAdClicked(Object obj);

        void onAdClosed(Object obj);

        void onAdFail(int i);

        void onAdImageFinish(AdModuleInfoBean adModuleInfoBean);

        void onAdInfoFinish(boolean z, AdModuleInfoBean adModuleInfoBean);

        void onAdShowed(Object obj);
    }

    private AdSdkManager() {
    }

    public static AdSdkManager getInstance() {
        if (sInstance == null) {
            sInstance = new AdSdkManager();
        }
        return sInstance;
    }

    public static void initSDK(Context context, String processName, String goId, String cId, String googleId, String channel, String dataChannel, String entranceId, ClientParams params) {
        Context context2 = context != null ? context.getApplicationContext() : null;
        AdSdkContants.initDirs(context2);
        ClientParams.save2Local(context2, params);
        AdSdkManager advertManager = getInstance();
        if (TextUtils.isEmpty(cId)) {
            advertManager.mProduct = new Product(context2);
        } else {
            advertManager.mProduct = new Product(cId, dataChannel, entranceId);
            if (context2.getResources().getIdentifier(Product.XML_NAME_KEYBOARD_NEW_STATISTIC, "integer", context2.getPackageName()) != 0) {
                advertManager.mProduct.setIsKeyBoardNewStatistic(true);
            } else {
                advertManager.mProduct.setIsKeyBoardNewStatistic(false);
            }
        }
        advertManager.mProduct.setGoId(goId).setGoogleId(googleId).setChannel(channel).setProcessName(processName);
        AdSdkThreadExecutorProxy.init();
        initStatisticsManager(context2, advertManager.mProduct);
        DataBaseHelper.getInstance(context2);
        DrawUtils.resetDensity(context2);
        AdImageManager.getInstance(context2);
        PreloadingControlManager.getInstance(context2);
        if (LogUtils.isShowLog()) {
            AdSdkDebugConfig.getInstance();
        }
        ABTestManager.getInstance(context2);
        FbNativeAdTrick.getInstance(context2);
        AdInfoStatisticManager.getInstance(context2).init();
        AdAvoider.getInstance(context2).detect(new Object[0]);
        IntelligentApi.init(context2, cId, entranceId, dataChannel, channel, googleId);
    }

    public static void setGoogleAdvertisingId(Context context, String googleAdvertisingId) {
        if (sInstance != null && !TextUtils.isEmpty(googleAdvertisingId)) {
            sInstance.getProduct().setGoogleId(googleAdvertisingId);
            initStatisticsManager(context, sInstance.getProduct());
        }
    }

    private static void initStatisticsManager(Context context, Product product) {
        if (product != null) {
            try {
                BaseSeq105OperationStatistic.sPRODUCT_ID = StringUtils.toInteger(AdSdkOperationStatistic.getStatisticCid(product), -1).intValue();
            } catch (Throwable thr) {
                thr.printStackTrace();
                return;
            }
        }
        if (context == null) {
        }
    }

    public void loadOnlineAdBean(Context context, int adPos, int returnAdCount, int requestAdCount, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, boolean isRequestData, ILoadAdvertDataListener loadAdvertDataListener) {
        if (loadAdvertDataListener != null) {
            if (!NetworkUtils.isNetworkOK(context)) {
                loadAdvertDataListener.onAdFail(17);
                return;
            }
            final int i = adPos;
            final int i2 = returnAdCount;
            final boolean z = isNeedDownloadIcon;
            final boolean z2 = isNeedDownloadBanner;
            final boolean z3 = isNeedPreResolve;
            final boolean z4 = isRequestData;
            final boolean z5 = isPreResolveBeforeShow;
            final Context context2 = context;
            final ILoadAdvertDataListener iLoadAdvertDataListener = loadAdvertDataListener;
            final int i3 = requestAdCount;
            new AdSdkThread(new Runnable() {
                public void run() {
                    int i;
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "loadOnlineAdBean(begin, adPos:" + i + ", returnAdCount:" + i2 + ", isNeedDownloadIcon:" + z + ", isNeedDownloadBanner:" + z2 + ", isNeedPreResolve:" + z3 + ", isRequestData:" + z4 + ", isPreResolveBeforeShow:" + z5 + ")");
                    }
                    if (!z4) {
                        String adCacheData = FileCacheUtils.readCacheDataToString(BaseOnlineModuleInfoBean.getCacheFileName(i), true);
                        if (!TextUtils.isEmpty(adCacheData)) {
                            try {
                                AdModuleInfoBean adInfoBean = BaseOnlineModuleInfoBean.getOnlineAdInfoList(context2, (BaseModuleDataItemBean) null, i, i2, true, (List<String>) null, new JSONObject(adCacheData));
                                if (adInfoBean != null) {
                                    BaseOnlineModuleInfoBean onlineModuleInfoBean = adInfoBean.getOnlineModuleInfoBean();
                                    long loadAdDataTime = onlineModuleInfoBean != null ? onlineModuleInfoBean.getSaveDataTime() : -1;
                                    if (BaseOnlineModuleInfoBean.checkOnlineAdInfoValid(loadAdDataTime)) {
                                        AdSdkManager.handleAdData(context2, true, adInfoBean, z, z3, z5, z2, iLoadAdvertDataListener);
                                        if (LogUtils.isShowLog()) {
                                            StringBuilder append = new StringBuilder().append("loadOnlineAdBean(end--cacheData, adPos:").append(i).append(", returnAdCount:").append(i2).append(", adSize:");
                                            if (adInfoBean.getOfflineAdInfoList() != null) {
                                                i = adInfoBean.getOfflineAdInfoList().size();
                                            } else {
                                                i = -1;
                                            }
                                            LogUtils.d("Ad_SDK", append.append(i).append(")").toString());
                                            return;
                                        }
                                        return;
                                    } else if (LogUtils.isShowLog()) {
                                        LogUtils.d("Ad_SDK", "loadOnlineAdBean(cacheData----cache data expired, loadOnlineAdTime:" + loadAdDataTime + ", adPos:" + i + ")");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtils.e("Ad_SDK", "loadOnlineAdBean(cacheData---error, Exception:" + (e != null ? e.getMessage() : "") + ", adPos:" + i + ")");
                            }
                        }
                    }
                    AdSdkRequestDataUtils.requestOnlineAdInfo(context2, i3, i, (AdSdkRequestHeader.S2SParams) null, new IConnectListener() {
                        public void onStart(THttpRequest request) {
                        }

                        public void onFinish(THttpRequest request, IResponse response) {
                            JSONObject resourceMapJsonObj;
                            JSONObject datasJson = null;
                            try {
                                datasJson = new JSONObject(StringUtils.toString(response.getResponse()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (datasJson != null) {
                                try {
                                    resourceMapJsonObj = datasJson.optJSONObject(BaseOnlineAdInfoBean.ONLINE_AD_JSON_TAG);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    iLoadAdvertDataListener.onAdFail(18);
                                    LogUtils.e("Ad_SDK", "loadOnlineAdBean(error, adPos:" + i + ", errorMessage:" + (e2 != null ? e2.getMessage() : "") + ")");
                                    return;
                                }
                            } else {
                                resourceMapJsonObj = null;
                            }
                            AdModuleInfoBean adInfoBean = BaseOnlineModuleInfoBean.getOnlineAdInfoList(context2, (BaseModuleDataItemBean) null, i, i2, true, (List<String>) null, resourceMapJsonObj);
                            List<BaseOnlineAdInfoBean> onlineAdInfoList = adInfoBean != null ? adInfoBean.getOnlineAdInfoList() : null;
                            if (onlineAdInfoList == null || onlineAdInfoList.isEmpty()) {
                                iLoadAdvertDataListener.onAdInfoFinish(false, (AdModuleInfoBean) null);
                                if (datasJson != null) {
                                    LogUtils.e("Ad_SDK", "loadOnlineAdBean(error, adPos:" + i + ", 错误代码::->" + datasJson.optInt("errorCode", -1) + ", 错误信息::->" + datasJson.optString("msg", "") + ")");
                                    return;
                                }
                                return;
                            }
                            AdSdkManager.handleAdData(context2, false, adInfoBean, z, z2, z3, z5, iLoadAdvertDataListener);
                        }

                        public void onException(THttpRequest request, int reason) {
                            iLoadAdvertDataListener.onAdFail(18);
                            LogUtils.e("Ad_SDK", "loadOnlineAdBean(error, adPos:" + i + ", reason:" + reason + ")");
                        }

                        public void onException(THttpRequest request, HttpResponse response, int reason) {
                            onException(request, reason);
                        }
                    });
                }
            }).start();
        }
    }

    public void loadAdBean(AdSdkParamsBuilder adSdkParams) {
        final Context context = adSdkParams.mContext;
        final int virtualModuleId = adSdkParams.mVirtualModuleId;
        final int shownCount = adSdkParams.getShownCount();
        final boolean isRequestData = adSdkParams.mIsRequestData;
        final boolean isAddFilterPackageNames = adSdkParams.mIsAddFilterPackageNames;
        String str = adSdkParams.mBuyuserchannel;
        Integer num = adSdkParams.mCdays;
        final ILoadAdvertDataListener loadAdvertDataListener = adSdkParams.mLoadAdvertDataListener;
        final IAdControlInterceptor adControlInterceptor = adSdkParams.mAdControlInterceptor;
        final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
        new AdSdkThread(adSdkParams.mUseThreadPool, (Runnable) new Runnable() {
            public void run() {
                AdAvoider.getInstance(context).detect(new Object[0]);
                if (!NetworkUtils.isNetworkOK(context)) {
                    loadAdvertDataListener.onAdFail(17);
                    LogUtils.e("Ad_SDK", "loadAdBean(Fail, Network unavailable, virtualModuleId:" + virtualModuleId + ")");
                    return;
                }
                final AdControlManager adControlManager = AdControlManager.getInstance(context);
                if (!isRequestData) {
                    MainModuleDataItemBeanWrapper mainWrapper = new MainModuleDataItemBeanWrapper();
                    List<BaseModuleDataItemBean> moduleDataItemList = adControlManager.getAdControlInfoFromCacheData(context, virtualModuleId, mainWrapper);
                    if (LogUtils.isShowLog() && mainWrapper.getMainModuleDataItemBean() != null) {
                        BaseModuleDataItemBean bean = mainWrapper.getMainModuleDataItemBean();
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdBean(Adfirst:" + bean.getAdfirst() + " Adsplit:" + bean.getAdsplit() + " AdCloseType:" + bean.getAdcolsetype() + " Adfrequency:" + bean.getAdFrequency() + ")");
                    }
                    if (moduleDataItemList != null && !moduleDataItemList.isEmpty()) {
                        if (shownCount >= 0) {
                            BaseModuleDataItemBean mdiBean = moduleDataItemList.get(0);
                            int adFrequency = mdiBean != null ? mdiBean.getAdFrequency() : 0;
                            if (adFrequency > 0 && shownCount >= adFrequency) {
                                AdModuleInfoBean adModuleInfoBean = new AdModuleInfoBean();
                                adModuleInfoBean.setSdkAdControlInfo(mdiBean);
                                loadAdvertDataListener.onAdInfoFinish(true, adModuleInfoBean);
                                return;
                            }
                        }
                        if (!AdAvoider.getInstance(context).shouldAvoid()) {
                            if (adControlInterceptor == null || mainWrapper.getMainModuleDataItemBean() == null || adControlInterceptor.isLoadAd(mainWrapper.getMainModuleDataItemBean())) {
                                adControlManager.loadAdInfo(adSdkParamsBuilder, true, 0, true, moduleDataItemList);
                                return;
                            }
                            LogUtils.e("Ad_SDK", "loadAdBean(Fail, Client cancel, virtualModuleId:" + virtualModuleId + ")");
                            loadAdvertDataListener.onAdFail(22);
                            return;
                        }
                    }
                }
                if (!AdSdkRequestDataUtils.canAdModuleReachable(context, virtualModuleId)) {
                    LogUtils.e("Ad_SDK", "ad module(" + virtualModuleId + ")removed-loadAdBean");
                    loadAdvertDataListener.onAdFail(19);
                    return;
                }
                adControlManager.getAdControlInfoFromNetwork(context, virtualModuleId, 0, isAddFilterPackageNames, adSdkParamsBuilder, new AdControlManager.AdControlRequestListener() {
                    public void onFinish(int statusCode, BaseModuleDataItemBean mainModuleDataItemBean, List<BaseModuleDataItemBean> moduleDataItemList) {
                        int adFrequency;
                        if (loadAdvertDataListener != null) {
                            if (LogUtils.isShowLog() && mainModuleDataItemBean != null) {
                                LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadAdBean(Adfirst:" + mainModuleDataItemBean.getAdfirst() + " Adsplit:" + mainModuleDataItemBean.getAdsplit() + " AdCloseType:" + mainModuleDataItemBean.getAdcolsetype() + " Adfrequency:" + mainModuleDataItemBean.getAdFrequency() + ")");
                            }
                            if (adControlInterceptor != null && mainModuleDataItemBean != null && !adControlInterceptor.isLoadAd(mainModuleDataItemBean)) {
                                LogUtils.e("Ad_SDK", "loadAdBean(Fail, Client cancel, virtualModuleId:" + virtualModuleId + ")");
                                loadAdvertDataListener.onAdFail(22);
                            } else if (statusCode != 16) {
                                loadAdvertDataListener.onAdFail(statusCode);
                                LogUtils.e("Ad_SDK", "requestAdControlInfo(end--fail, " + statusCode + ")");
                            } else {
                                if (moduleDataItemList != null && !moduleDataItemList.isEmpty() && shownCount >= 0) {
                                    BaseModuleDataItemBean mdiBean = moduleDataItemList.get(0);
                                    if (mdiBean != null) {
                                        adFrequency = mdiBean.getAdFrequency();
                                    } else {
                                        adFrequency = 0;
                                    }
                                    if (adFrequency > 0 && shownCount >= adFrequency) {
                                        AdModuleInfoBean adModuleInfoBean = new AdModuleInfoBean();
                                        adModuleInfoBean.setSdkAdControlInfo(mdiBean);
                                        loadAdvertDataListener.onAdInfoFinish(false, adModuleInfoBean);
                                        return;
                                    }
                                }
                                adControlManager.loadAdInfo(adSdkParamsBuilder, true, 0, true, moduleDataItemList);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void requestAdData(final Context context, List<Integer> virtualModuleIds, final AdSdkParamsBuilder apb) {
        if (virtualModuleIds != null) {
            final ArrayList<Integer> vmIds = new ArrayList<>();
            for (Integer id : virtualModuleIds) {
                if (!vmIds.contains(id)) {
                    vmIds.add(id);
                }
            }
            if (!vmIds.isEmpty()) {
                new AdSdkThread(new Runnable() {
                    public void run() {
                        AdSdkManager.this.syncRequestAdData(context, vmIds, true, apb);
                    }
                }).start();
            }
        }
    }

    public void requestBatchControlInfo(Context context, List<Integer> virtualModuleIds, AdSdkParamsBuilder apb, boolean requestNetwork, AdControlManager.IBacthControlListener listener) {
        if (listener != null) {
            final List<Integer> list = virtualModuleIds;
            final AdControlManager.IBacthControlListener iBacthControlListener = listener;
            final Context context2 = context;
            final AdSdkParamsBuilder adSdkParamsBuilder = apb;
            final boolean z = requestNetwork;
            new AdSdkThread(new Runnable() {
                public void run() {
                    if (list == null) {
                        iBacthControlListener.onFinish((List<BaseModuleDataItemBean>) null);
                        return;
                    }
                    ArrayList<Integer> vmIds = new ArrayList<>();
                    for (Integer id : list) {
                        if (!vmIds.contains(id)) {
                            vmIds.add(id);
                        }
                    }
                    if (vmIds.isEmpty()) {
                        iBacthControlListener.onFinish((List<BaseModuleDataItemBean>) null);
                        return;
                    }
                    List<ModuleRequestParams> moduleRequestParams = new ArrayList<>();
                    for (Integer mId : list) {
                        moduleRequestParams.add(new ModuleRequestParams(Integer.valueOf(mId.intValue()), 0));
                    }
                    AdControlManager.getInstance(context2).getBatchModuleControlInfo(context2, moduleRequestParams, adSdkParamsBuilder, z, iBacthControlListener);
                }
            }).start();
        }
    }

    public void requestUserTags(final Context context, final IAdvertUserTagResultListener listener) {
        if (context == null || listener == null) {
            throw new IllegalArgumentException("传入参数context和listener不能为空");
        }
        AdUserTagInfoBean adUserTagInfoBean = AdSdkSetting.getInstance(context).getUserTagInfoBean();
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdUserTagInfo->isValid:" + adUserTagInfoBean.isValid(context));
        }
        if (adUserTagInfoBean.isValid(context)) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "本地缓存直接获取:ADSdkManager.requestUserTags[ status:Success, tags: " + adUserTagInfoBean.getUserTags() + "  ]");
            }
            listener.onAdRequestSuccess(adUserTagInfoBean);
            return;
        }
        AdSdkRequestDataUtils.requestUserTagInfo(context, new IConnectListener() {
            public void onStart(THttpRequest request) {
            }

            public void onFinish(THttpRequest request, IResponse response) {
                String userTags;
                JSONObject datasJson = null;
                try {
                    datasJson = new JSONObject(StringUtils.toString(response.getResponse()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (datasJson != null) {
                    try {
                        userTags = datasJson.optString("tags");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        listener.onAdRequestFail(17);
                        LogUtils.e("Ad_SDK", "requestUserTags(error, errorMessage:" + (e2 != null ? e2.getMessage() : "") + ")");
                        return;
                    }
                } else {
                    userTags = null;
                }
                if (userTags == null) {
                    listener.onAdRequestFail(16);
                    return;
                }
                AdUserTagInfoBean adUserTagInfoBean = new AdUserTagInfoBean();
                adUserTagInfoBean.setUserTags(userTags);
                AdSdkSetting.getInstance(context).setUserTag(userTags, System.currentTimeMillis());
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "网络获取:ADSdkManager.requestUserTags[ status:Success, tags:" + adUserTagInfoBean.getUserTags() + "  ]");
                }
                listener.onAdRequestSuccess(adUserTagInfoBean);
            }

            public void onException(THttpRequest request, int reason) {
                listener.onAdRequestFail(17);
                LogUtils.e("Ad_SDK", "requestUserTags(error, reason:" + reason + ")");
            }

            public void onException(THttpRequest request, HttpResponse response, int reason) {
                onException(request, reason);
            }
        });
    }

    public void requestUserTags(final Context context, boolean isAsyncTask, int productId, final IAdvertUserTagResultListener listener) {
        if (context == null || listener == null) {
            throw new IllegalArgumentException("传入参数context和listener不能为空");
        }
        getInstance().getProduct().setCid(productId + "");
        AdUserTagInfoBean adUserTagInfoBean = AdSdkSetting.getInstance(context).getUserTagInfoBean();
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdUserTagInfo->isValid:" + adUserTagInfoBean.isValid(context));
        }
        if (adUserTagInfoBean.isValid(context)) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "本地缓存直接获取:ADSdkManager.requestUserTags[ status:Success, tags: " + adUserTagInfoBean.getUserTags() + "  ]");
            }
            listener.onAdRequestSuccess(adUserTagInfoBean);
            return;
        }
        AdSdkRequestDataUtils.requestUserTagInfo(context, isAsyncTask, new IConnectListener() {
            public void onStart(THttpRequest request) {
            }

            /* JADX WARNING: Removed duplicated region for block: B:12:0x0035 A[Catch:{ Exception -> 0x0087 }] */
            /* JADX WARNING: Removed duplicated region for block: B:15:0x0042  */
            /* JADX WARNING: Removed duplicated region for block: B:16:0x0044 A[SYNTHETIC, Splitter:B:16:0x0044] */
            /* JADX WARNING: Removed duplicated region for block: B:8:0x002d  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onFinish(com.gau.utils.net.request.THttpRequest r9, com.gau.utils.net.response.IResponse r10) {
                /*
                    r8 = this;
                    r1 = 0
                    org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x003d }
                    java.lang.Object r5 = r10.getResponse()     // Catch:{ Exception -> 0x003d }
                    java.lang.String r5 = com.jiubang.commerce.utils.StringUtils.toString(r5)     // Catch:{ Exception -> 0x003d }
                    r2.<init>(r5)     // Catch:{ Exception -> 0x003d }
                    java.lang.String r5 = "ZH"
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ba }
                    r6.<init>()     // Catch:{ Exception -> 0x00ba }
                    java.lang.String r7 = "datasJson= "
                    java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x00ba }
                    java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x00ba }
                    java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x00ba }
                    java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x00ba }
                    com.jb.ga0.commerce.util.LogUtils.d(r5, r6)     // Catch:{ Exception -> 0x00ba }
                    r1 = r2
                L_0x002b:
                    if (r1 == 0) goto L_0x0042
                    java.lang.String r5 = "tags"
                    java.lang.String r4 = r1.optString(r5)     // Catch:{ Exception -> 0x0087 }
                L_0x0033:
                    if (r4 != 0) goto L_0x0044
                    com.jiubang.commerce.ad.manager.AdSdkManager$IAdvertUserTagResultListener r5 = r9     // Catch:{ Exception -> 0x0087 }
                    r6 = 16
                    r5.onAdRequestFail(r6)     // Catch:{ Exception -> 0x0087 }
                L_0x003c:
                    return
                L_0x003d:
                    r3 = move-exception
                L_0x003e:
                    r3.printStackTrace()
                    goto L_0x002b
                L_0x0042:
                    r4 = 0
                    goto L_0x0033
                L_0x0044:
                    com.jiubang.commerce.ad.bean.AdUserTagInfoBean r0 = new com.jiubang.commerce.ad.bean.AdUserTagInfoBean     // Catch:{ Exception -> 0x0087 }
                    r0.<init>()     // Catch:{ Exception -> 0x0087 }
                    r0.setUserTags(r4)     // Catch:{ Exception -> 0x0087 }
                    android.content.Context r5 = r6     // Catch:{ Exception -> 0x0087 }
                    com.jiubang.commerce.ad.manager.AdSdkSetting r5 = com.jiubang.commerce.ad.manager.AdSdkSetting.getInstance(r5)     // Catch:{ Exception -> 0x0087 }
                    long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0087 }
                    r5.setUserTag(r4, r6)     // Catch:{ Exception -> 0x0087 }
                    boolean r5 = com.jb.ga0.commerce.util.LogUtils.isShowLog()     // Catch:{ Exception -> 0x0087 }
                    if (r5 == 0) goto L_0x0081
                    java.lang.String r5 = "Ad_SDK"
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0087 }
                    r6.<init>()     // Catch:{ Exception -> 0x0087 }
                    java.lang.String r7 = "网络获取:ADSdkManager.requestUserTags[ status:Success, tags:"
                    java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x0087 }
                    java.util.List r7 = r0.getUserTags()     // Catch:{ Exception -> 0x0087 }
                    java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x0087 }
                    java.lang.String r7 = "  ]"
                    java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x0087 }
                    java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0087 }
                    com.jb.ga0.commerce.util.LogUtils.i(r5, r6)     // Catch:{ Exception -> 0x0087 }
                L_0x0081:
                    com.jiubang.commerce.ad.manager.AdSdkManager$IAdvertUserTagResultListener r5 = r9     // Catch:{ Exception -> 0x0087 }
                    r5.onAdRequestSuccess(r0)     // Catch:{ Exception -> 0x0087 }
                    goto L_0x003c
                L_0x0087:
                    r3 = move-exception
                    r3.printStackTrace()
                    com.jiubang.commerce.ad.manager.AdSdkManager$IAdvertUserTagResultListener r5 = r9
                    r6 = 17
                    r5.onAdRequestFail(r6)
                    java.lang.String r6 = "Ad_SDK"
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder
                    r5.<init>()
                    java.lang.String r7 = "requestUserTags(error, errorMessage:"
                    java.lang.StringBuilder r7 = r5.append(r7)
                    if (r3 == 0) goto L_0x00b7
                    java.lang.String r5 = r3.getMessage()
                L_0x00a5:
                    java.lang.StringBuilder r5 = r7.append(r5)
                    java.lang.String r7 = ")"
                    java.lang.StringBuilder r5 = r5.append(r7)
                    java.lang.String r5 = r5.toString()
                    com.jb.ga0.commerce.util.LogUtils.e(r6, r5)
                    goto L_0x003c
                L_0x00b7:
                    java.lang.String r5 = ""
                    goto L_0x00a5
                L_0x00ba:
                    r3 = move-exception
                    r1 = r2
                    goto L_0x003e
                */
                throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.manager.AdSdkManager.AnonymousClass6.onFinish(com.gau.utils.net.request.THttpRequest, com.gau.utils.net.response.IResponse):void");
            }

            public void onException(THttpRequest request, int reason) {
                listener.onAdRequestFail(17);
                LogUtils.e("Ad_SDK", "requestUserTags(error, reason:" + reason + ")");
            }

            public void onException(THttpRequest request, HttpResponse response, int reason) {
                onException(request, reason);
            }
        });
    }

    public void requestUserTags(final Context context, final IAdvertUserTagResultListener listener, UserTagParams params, boolean isNew) {
        if (isNew) {
            requestUserTags(context, listener, params);
        } else if (context == null || listener == null) {
            throw new IllegalArgumentException("传入参数context和listener不能为空");
        } else {
            AdOldUserTagInfoBean adOldUserTagInfoBean = AdSdkSetting.getInstance(context).getOldUserTagInfoBean();
            if (adOldUserTagInfoBean.isValid(context)) {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "本地缓存直接获取:ADSdkManager.requestOldUserTags[ status:Success, tags: " + adOldUserTagInfoBean.getUserTags() + "  ]");
                }
                listener.onAdRequestSuccess(adOldUserTagInfoBean);
                return;
            }
            AdSdkRequestDataUtils.requestUserTagInfo(context, new IConnectListener() {
                public void onStart(THttpRequest request) {
                }

                public void onFinish(THttpRequest request, IResponse response) {
                    String userTags;
                    JSONObject datasJson = null;
                    try {
                        datasJson = new JSONObject(StringUtils.toString(response.getResponse()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (datasJson != null) {
                        try {
                            userTags = datasJson.optString("tags");
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            listener.onAdRequestFail(17);
                            LogUtils.e("Ad_SDK", "requestUserTags(error, errorMessage:" + (e2 != null ? e2.getMessage() : "") + ")");
                            return;
                        }
                    } else {
                        userTags = null;
                    }
                    if (userTags == null) {
                        listener.onAdRequestFail(16);
                        return;
                    }
                    AdOldUserTagInfoBean adOldUserTagInfoBean = new AdOldUserTagInfoBean();
                    adOldUserTagInfoBean.setUserTags(userTags);
                    AdSdkSetting.getInstance(context).setOldUserTag(userTags, System.currentTimeMillis());
                    if (LogUtils.isShowLog()) {
                        LogUtils.i("Ad_SDK", "网络获取:ADSdkManager.requestOldUserTags[ status:Success, tags:" + adOldUserTagInfoBean.getUserTags() + "  ]");
                    }
                    listener.onAdRequestSuccess(adOldUserTagInfoBean);
                }

                public void onException(THttpRequest request, int reason) {
                    listener.onAdRequestFail(17);
                    LogUtils.e("Ad_SDK", "requestUserTags(error, reason:" + reason + ")");
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            }, params, false);
        }
    }

    public void requestUserTags(final Context context, final IAdvertUserTagResultListener listener, UserTagParams params) {
        if (context == null || listener == null) {
            throw new IllegalArgumentException("传入参数context和listener不能为空");
        }
        AdUserTagInfoBean adUserTagInfoBean = AdSdkSetting.getInstance(context).getUserTagInfoBean();
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdUserTagInfo->isValid:" + adUserTagInfoBean.isValid(context));
        }
        if (adUserTagInfoBean.isValid(context)) {
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "本地缓存直接获取:ADSdkManager.requestUserTags[ status:Success, tags: " + adUserTagInfoBean.getUserTags() + "  ]");
            }
            listener.onAdRequestSuccess(adUserTagInfoBean);
            return;
        }
        AdSdkRequestDataUtils.requestUserTagInfo(context, new IConnectListener() {
            public void onStart(THttpRequest request) {
            }

            public void onFinish(THttpRequest request, IResponse response) {
                String userTags;
                JSONObject datasJson = null;
                try {
                    datasJson = new JSONObject(StringUtils.toString(response.getResponse()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (datasJson != null) {
                    try {
                        userTags = datasJson.optString("tags");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        listener.onAdRequestFail(17);
                        LogUtils.e("Ad_SDK", "requestUserTags(error, errorMessage:" + (e2 != null ? e2.getMessage() : "") + ")");
                        return;
                    }
                } else {
                    userTags = null;
                }
                if (userTags == null) {
                    listener.onAdRequestFail(16);
                    return;
                }
                AdUserTagInfoBean adUserTagInfoBean = new AdUserTagInfoBean();
                adUserTagInfoBean.setUserTags(userTags);
                AdSdkSetting.getInstance(context).setUserTag(userTags, System.currentTimeMillis());
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "网络获取:ADSdkManager.requestUserTags[ status:Success, tags:" + adUserTagInfoBean.getUserTags() + "  ]");
                }
                listener.onAdRequestSuccess(adUserTagInfoBean);
            }

            public void onException(THttpRequest request, int reason) {
                listener.onAdRequestFail(17);
                LogUtils.e("Ad_SDK", "requestUserTags(error, reason:" + reason + ")");
            }

            public void onException(THttpRequest request, HttpResponse response, int reason) {
                onException(request, reason);
            }
        }, params, true);
    }

    public boolean hasAvailableAd(Context context, int virtualModuleId) {
        boolean isFacebookExist;
        List<BaseModuleDataItemBean> moduleDataItemList = AdControlManager.getInstance(context).getAdControlInfoFromCacheData(context, virtualModuleId, (MainModuleDataItemBeanWrapper) null);
        if (moduleDataItemList == null || moduleDataItemList.isEmpty()) {
            return false;
        }
        if (1 != moduleDataItemList.size()) {
            return true;
        }
        BaseModuleDataItemBean moduleDataItemBean = moduleDataItemList.get(0);
        if (!moduleDataItemBean.isSdkOnlineAdType() || !AdModuleInfoBean.isFaceBookAd(moduleDataItemBean)) {
            return true;
        }
        if (AppUtils.isAppExist(context, AdSdkContants.PACKAGE_NAME_FACEBOOK) || AppUtils.isAppExist(context, AdSdkContants.PACKAGE_NAME_FACEBOOK_LITE)) {
            isFacebookExist = true;
        } else {
            isFacebookExist = false;
        }
        if (!isFacebookExist || !SystemUtils.IS_SDK_ABOVE_GBREAD) {
            return false;
        }
        return true;
    }

    public void hasAvailableAd(final Context context, final int virtualModuleId, final IAdCheckListener adCheckListener) {
        if (adCheckListener != null) {
            new AdSdkThread(new Runnable() {
                public void run() {
                    adCheckListener.onChecked(AdSdkManager.this.hasAvailableAd(context, virtualModuleId));
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void syncRequestAdData(Context context, List<Integer> virtualModuleIds, boolean isAddFilterPackageNames, AdSdkParamsBuilder apb) {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "syncRequestAdData(begin, virtualModuleId:" + virtualModuleIds + ")");
        }
        if (NetworkUtils.isNetworkOK(context)) {
            AdControlManager adControlManager = AdControlManager.getInstance(context);
            List<ModuleRequestParams> moduleRequestParams = new ArrayList<>();
            for (Integer mId : virtualModuleIds) {
                moduleRequestParams.add(new ModuleRequestParams(Integer.valueOf(mId.intValue()), 0));
            }
            adControlManager.saveBatchAdControlInfoFromNetwork(context, moduleRequestParams, isAddFilterPackageNames, apb);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "syncRequestAdData(end--Network unavailable, virtualModuleId:" + virtualModuleIds + ")");
        }
    }

    protected static void handleAdData(Context context, final boolean isCacheData, final AdModuleInfoBean adModuleInfoBean, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, final ILoadAdvertDataListener loadAdvertDataListener) {
        loadAdvertOtherInfo(context, adModuleInfoBean, isNeedDownloadIcon, isNeedDownloadBanner, isNeedPreResolve, isPreResolveBeforeShow, new IAdvertHandleResultListener() {
            public void onHandleAdvertInfoFinish() {
                if (loadAdvertDataListener != null) {
                    loadAdvertDataListener.onAdInfoFinish(isCacheData, adModuleInfoBean);
                }
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "[vmId:" + (adModuleInfoBean != null ? adModuleInfoBean.getVirtualModuleId() : -1) + "]handleAdData(onHandleAdvertInfoFinish, isCacheData:" + isCacheData + ", adModuleInfoBean:" + adModuleInfoBean + ", loadAdvertDataListener:" + loadAdvertDataListener + ")");
                }
            }

            public void onAdvertImageDownloadFinish() {
                if (loadAdvertDataListener != null) {
                    loadAdvertDataListener.onAdImageFinish(adModuleInfoBean);
                }
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "[vmId:" + (adModuleInfoBean != null ? adModuleInfoBean.getVirtualModuleId() : -1) + "]handleAdData(onAdvertImageDownloadFinish, isCacheData:" + isCacheData + ", adModuleInfoBean:" + adModuleInfoBean + ", loadAdvertDataListener:" + loadAdvertDataListener + ")");
                }
            }
        });
    }

    public static boolean loadAdvertOtherInfo(Context context, AdModuleInfoBean adModuleInfoBean, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, boolean isNeedPreResolve, boolean isPreResolveBeforeShow, IAdvertHandleResultListener handleResultListener) {
        Object obj;
        int i;
        final boolean isNeedDownloadImage = isNeedDownloadIcon || isNeedDownloadBanner;
        BaseModuleDataItemBean moduleDataItemBean = adModuleInfoBean != null ? adModuleInfoBean.getModuleDataItemBean() : null;
        int moduleId = moduleDataItemBean != null ? moduleDataItemBean.getModuleId() : -1;
        final List<AdInfoBean> adInfoList = adModuleInfoBean != null ? adModuleInfoBean.getAdInfoList() : null;
        if (LogUtils.isShowLog()) {
            StringBuilder append = new StringBuilder().append("[vmId:").append(moduleDataItemBean != null ? moduleDataItemBean.getVirtualModuleId() : -1).append("]loadAdvertOtherInfo(module:");
            if (moduleDataItemBean != null) {
                obj = Integer.valueOf(moduleDataItemBean.getModuleId());
            } else {
                obj = "-1";
            }
            StringBuilder append2 = append.append(obj).append(", adSize:");
            if (adInfoList != null) {
                i = adInfoList.size();
            } else {
                i = -1;
            }
            LogUtils.d("Ad_SDK", append2.append(i).append(", isNeedDownloadImage:").append(isNeedDownloadImage).append(", isNeedPreResolve: ").append(isNeedPreResolve).append(", isPreResolveBeforeShow:").append(isPreResolveBeforeShow).append(", isDownloadBanner:").append(isNeedDownloadBanner).append(")").toString());
        }
        if (adInfoList == null || adInfoList.isEmpty()) {
            if (handleResultListener != null) {
                handleResultListener.onHandleAdvertInfoFinish();
                handleResultListener.onAdvertImageDownloadFinish();
            }
            return false;
        }
        if (!isPreResolveBeforeShow) {
            if (handleResultListener != null) {
                handleResultListener.onHandleAdvertInfoFinish();
            }
            if (isNeedPreResolve) {
                preResolveAdvertUrl(context, moduleId, adInfoList, true, new PresolveParams.Builder().build(), new AdUrlPreParseTask.ExecuteTaskStateListener() {
                    public void onExecuteTaskComplete(Context context) {
                    }
                });
            }
            if (isNeedDownloadImage) {
                loadAdImage(context, adInfoList, isNeedDownloadIcon, isNeedDownloadBanner, handleResultListener);
            }
        } else if (isNeedPreResolve) {
            final IAdvertHandleResultListener iAdvertHandleResultListener = handleResultListener;
            final boolean z = isNeedDownloadIcon;
            final boolean z2 = isNeedDownloadBanner;
            preResolveAdvertUrl(context, moduleId, adInfoList, true, new PresolveParams.Builder().repeatClickEnable(false).isControlled(false).useCache(false).uaType(2).build(), new AdUrlPreParseTask.ExecuteTaskStateListener() {
                public void onExecuteTaskComplete(Context context) {
                    if (iAdvertHandleResultListener != null) {
                        iAdvertHandleResultListener.onHandleAdvertInfoFinish();
                    }
                    if (isNeedDownloadImage) {
                        AdSdkManager.loadAdImage(context, adInfoList, z, z2, iAdvertHandleResultListener);
                    }
                }
            });
        } else {
            if (handleResultListener != null) {
                handleResultListener.onHandleAdvertInfoFinish();
            }
            if (isNeedDownloadImage) {
                loadAdImage(context, adInfoList, isNeedDownloadIcon, isNeedDownloadBanner, handleResultListener);
            }
        }
        return true;
    }

    static void loadAdImage(Context context, List<AdInfoBean> adInfoList, boolean isNeedDownloadIcon, boolean isNeedDownloadBanner, IAdvertHandleResultListener handleResultListener) {
        if (handleResultListener != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                final Context context2 = context;
                final List<AdInfoBean> list = adInfoList;
                final boolean z = isNeedDownloadIcon;
                final boolean z2 = isNeedDownloadBanner;
                final IAdvertHandleResultListener iAdvertHandleResultListener = handleResultListener;
                new Thread(new Runnable() {
                    public void run() {
                        AdImageManager.getInstance(context2).syncLoadAdImage(list, z, z2);
                        iAdvertHandleResultListener.onAdvertImageDownloadFinish();
                    }
                }).start();
                return;
            }
            AdImageManager.getInstance(context).syncLoadAdImage(adInfoList, isNeedDownloadIcon, isNeedDownloadBanner);
            handleResultListener.onAdvertImageDownloadFinish();
        }
    }

    public static void preResolveAdvertUrl(Context context, int moduleId, List<AdInfoBean> adInfoList, boolean useCache, PresolveParams preParams, AdUrlPreParseTask.ExecuteTaskStateListener listener) {
        if (context != null && adInfoList != null && adInfoList.size() > 0 && preParams != null) {
            final List<AdInfoBean> tempAdInfoList = new ArrayList<>();
            for (int index = 0; index < adInfoList.size(); index++) {
                AdInfoBean adInfoBean = adInfoList.get(index);
                if (adInfoBean != null && !TextUtils.isEmpty(adInfoBean.getAdUrl())) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[vmId:" + adInfoBean.getVirtualModuleId() + "]preResolveAdvertUrl(index:" + index + ", moduleId:" + moduleId + ", IsAd:" + adInfoBean.getIsAd() + ", AdPreload: " + adInfoBean.getAdPreload() + ", adUrl:" + adInfoBean.getAdUrl() + ", " + tempAdInfoList.size() + ")");
                    }
                    if (!preParams.mIsControlled) {
                        tempAdInfoList.add(adInfoBean);
                    } else if (adInfoBean.getIsAd() == 1 && adInfoBean.getAdPreload() == 1) {
                        tempAdInfoList.add(adInfoBean);
                    }
                }
            }
            if (tempAdInfoList.size() > 0) {
                final Context context2 = context;
                final int i = moduleId;
                final boolean z = useCache;
                final PresolveParams presolveParams = preParams;
                final AdUrlPreParseTask.ExecuteTaskStateListener executeTaskStateListener = listener;
                AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
                    public void run() {
                        AdUrlPreParseTask.startExecuteTask(context2, String.valueOf(i), tempAdInfoList, z, presolveParams, executeTaskStateListener);
                    }
                });
            } else if (listener != null) {
                listener.onExecuteTaskComplete(context);
            }
        } else if (listener != null) {
            listener.onExecuteTaskComplete(context);
        }
    }

    public static synchronized void recordShowAdToCacheAdData(AdInfoBean adInfoBean) {
        synchronized (AdSdkManager.class) {
            if (adInfoBean != null) {
                if (!TextUtils.isEmpty(adInfoBean.getAdUrl()) && !TextUtils.isEmpty(adInfoBean.getAdInfoCacheFileName())) {
                    String cacheAdData = FileCacheUtils.readCacheDataToString(adInfoBean.getAdInfoCacheFileName(), true);
                    if (!TextUtils.isEmpty(cacheAdData)) {
                        try {
                            JSONObject adInfoJsonObject = new JSONObject(cacheAdData);
                            String hasShowAdUrls = adInfoJsonObject.optString(AdSdkContants.HAS_SHOW_AD_URL_LIST, "");
                            if (TextUtils.isEmpty(hasShowAdUrls)) {
                                hasShowAdUrls = hasShowAdUrls + AdSdkContants.SYMBOL_DOUBLE_LINE;
                            }
                            adInfoJsonObject.put(AdSdkContants.HAS_SHOW_AD_URL_LIST, hasShowAdUrls + adInfoBean.getAdUrl() + AdSdkContants.SYMBOL_DOUBLE_LINE);
                            FileCacheUtils.saveCacheDataToSdcard(adInfoBean.getAdInfoCacheFileName(), StringUtils.toString(adInfoJsonObject), true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return;
    }

    public String getGoId() {
        return this.mProduct == null ? "1" : this.mProduct.getGoId();
    }

    public String getGoogleId() {
        return this.mProduct == null ? "123456789" : this.mProduct.getGoogleId();
    }

    public String getCid() {
        return this.mProduct == null ? "-1" : this.mProduct.getCid();
    }

    public Product getProduct() {
        return this.mProduct;
    }

    public String getChannel() {
        return this.mProduct == null ? "200" : this.mProduct.getChannel();
    }

    public String getDataChannel() {
        return this.mProduct == null ? "-1" : this.mProduct.getDataChannel();
    }

    public String getEntranceId() {
        return this.mProduct == null ? "1" : this.mProduct.getEntranceId() + "";
    }

    public boolean isShowActivationGuideWindow() {
        return this.mIsShowActivationGuideWindow;
    }

    public void setIsShowActivationGuideWindow(boolean isShowActivationGuideWindow) {
        this.mIsShowActivationGuideWindow = isShowActivationGuideWindow;
    }

    public void destory(Context context) {
        PreloadingControlManager.destroy();
    }

    public static boolean isGoKeyboard() {
        Product product = getInstance().getProduct();
        if (product != null) {
            return product.isGoKeyboard();
        }
        return false;
    }

    public static boolean isNoNeedGPMonitorProduct() {
        try {
            if (!"1".equals(getInstance().getEntranceId())) {
                return false;
            }
            String cid = getInstance().getCid();
            if ("20".equals(cid)) {
                return true;
            }
            if ("56".equals(cid)) {
                return true;
            }
            return isGoKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setShieldAdSdk(boolean isShield) {
        sIS_SHIELD_AD_SDK = isShield;
    }

    public static boolean isShieldAdSdk() {
        return sIS_SHIELD_AD_SDK;
    }
}

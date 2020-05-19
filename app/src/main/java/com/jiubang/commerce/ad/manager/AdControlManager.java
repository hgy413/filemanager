package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.text.TextUtils;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkLogUtils;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.bean.MainModuleDataItemBeanWrapper;
import com.jiubang.commerce.ad.cache.AdCachePool;
import com.jiubang.commerce.ad.gomo.GomoAdHelper;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.bean.BaseAppInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseIntellModuleBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseResponseBean;
import com.jiubang.commerce.ad.ironscr.IronScrAd;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.params.ModuleRequestParams;
import com.jiubang.commerce.ad.params.OuterAdLoader;
import com.jiubang.commerce.ad.sdk.SdkAdProxy;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;
import com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick;
import com.jiubang.commerce.database.table.AdvertFilterTable;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.statistics.adinfo.AdInfoStatisticManager;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.utils.FileCacheUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class AdControlManager {
    private static AdControlManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;

    public interface AdControlRequestListener {
        void onFinish(int i, BaseModuleDataItemBean baseModuleDataItemBean, List<BaseModuleDataItemBean> list);
    }

    public interface AdIntellRequestListener {
        void onFinish(BaseIntellModuleBean baseIntellModuleBean);
    }

    public interface IBacthControlListener {
        void onFinish(List<BaseModuleDataItemBean> list);
    }

    public interface SdkAdSourceRequestListener {
        void onAdClicked(Object obj);

        void onAdClosed(Object obj);

        void onAdShowed(Object obj);

        void onException(int i);

        void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean);
    }

    private AdControlManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
    }

    public static AdControlManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdControlManager(context);
        }
        return sInstance;
    }

    public List<BaseModuleDataItemBean> getAdControlInfoFromCacheData(Context context, int virtualModuleId, MainModuleDataItemBeanWrapper mainWrapper) {
        int i = -1;
        String adControlCacheData = FileCacheUtils.readCacheDataToString(BaseModuleDataItemBean.getCacheFileName(virtualModuleId), true);
        if (!TextUtils.isEmpty(adControlCacheData)) {
            try {
                BaseModuleDataItemBean moduleDataItemBean = BaseModuleDataItemBean.parseMainModuleJsonObject(context, virtualModuleId, new JSONObject(adControlCacheData));
                if (mainWrapper != null) {
                    mainWrapper.setMainModuleDataItemBean(moduleDataItemBean);
                }
                List<BaseModuleDataItemBean> moduleDataItemList = null;
                if (moduleDataItemBean != null && ((moduleDataItemList = moduleDataItemBean.getChildModuleDataItemList()) == null || moduleDataItemList.isEmpty())) {
                    if (moduleDataItemList == null) {
                        moduleDataItemList = new ArrayList<>();
                    }
                    moduleDataItemList.add(moduleDataItemBean);
                }
                boolean isControlInfoValid = moduleDataItemBean != null ? BaseModuleDataItemBean.checkControlInfoValid(moduleDataItemBean.getSaveDataTime()) : false;
                if (!isControlInfoValid && mainWrapper != null) {
                    mainWrapper.setMainModuleDataItemBean((BaseModuleDataItemBean) null);
                }
                if (moduleDataItemList != null && !moduleDataItemList.isEmpty() && isControlInfoValid) {
                    if (LogUtils.isShowLog()) {
                        StringBuilder append = new StringBuilder().append("[vmId:").append(virtualModuleId).append("]getAdControlInfoFromCacheData(Success, virtualModuleId:").append(virtualModuleId).append(", size:");
                        if (moduleDataItemList != null) {
                            i = moduleDataItemList.size();
                        }
                        LogUtils.d("Ad_SDK", append.append(i).append(")").toString());
                    }
                    BaseResponseBean baseResponseBean = BaseResponseBean.getBaseResponseBeanFromCacheData(context, virtualModuleId);
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "virtualModuleId=" + virtualModuleId + " user=" + baseResponseBean.getUser() + " buychanneltype=" + baseResponseBean.getBuychanneltype());
                    }
                    for (int i2 = 0; i2 < moduleDataItemList.size(); i2++) {
                        moduleDataItemList.get(i2).setBaseResponseBean(baseResponseBean);
                    }
                    return moduleDataItemList;
                } else if (LogUtils.isShowLog()) {
                    StringBuilder append2 = new StringBuilder().append("[vmId:").append(virtualModuleId).append("]getAdControlInfoFromCacheData(Fail, virtualModuleId:").append(virtualModuleId).append(", size:");
                    if (moduleDataItemList != null) {
                        i = moduleDataItemList.size();
                    }
                    LogUtils.d("Ad_SDK", append2.append(i).append(")").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void getAdControlInfoFromNetwork(final Context context, final int virtualModuleId, int pageId, boolean isAddFilterPackageNames, AdSdkParamsBuilder apb, final AdControlRequestListener adControlRequestListener) {
        if (adControlRequestListener != null) {
            AdSdkRequestDataUtils.requestAdControlInfo(context, virtualModuleId, pageId, isAddFilterPackageNames, apb, (IConnectListener) new IConnectListener() {
                public void onStart(THttpRequest request) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getAdControlInfoFromNetwork(onStart, virtualModuleId:" + virtualModuleId + ")");
                    }
                }

                public void onFinish(THttpRequest request, IResponse response) {
                    try {
                        JSONObject json = new JSONObject(StringUtils.toString(response.getResponse()));
                        JSONObject resultJson = json.has("result") ? json.getJSONObject("result") : null;
                        int status = resultJson != null ? resultJson.getInt("status") : -1;
                        if (LogUtils.isShowLog()) {
                            LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getAdControlInfoFromNetwork(onFinish, status:" + status + "[" + (1 == status) + "], virtualModuleId:" + virtualModuleId + ")");
                        }
                        if (1 == status) {
                            if (!AdSdkRequestDataUtils.canAdModuleReachable(context, virtualModuleId, json.optJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_MFLAG))) {
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]ad module(" + virtualModuleId + ")removed-getAdControlInfoFromNetwork");
                                }
                                adControlRequestListener.onFinish(19, (BaseModuleDataItemBean) null, (List<BaseModuleDataItemBean>) null);
                                return;
                            }
                            BaseResponseBean baseResponseBean = BaseResponseBean.parseBaseResponseBeanJSONObject(context, virtualModuleId, json);
                            if (baseResponseBean != null) {
                                if (LogUtils.isShowLog()) {
                                    LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]virtualModuleId=" + virtualModuleId + " user=" + baseResponseBean.getUser() + " buychanneltype=" + baseResponseBean.getBuychanneltype());
                                }
                                baseResponseBean.saveSelfDataToSdcard(virtualModuleId);
                            }
                            JSONObject datasJson = json.getJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_DATAS);
                            if (LogUtils.isShowLog()) {
                                LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getAdControlInfoFromNetwork(" + virtualModuleId + ", " + datasJson + ")");
                            }
                            BaseModuleDataItemBean.saveAdDataToSdcard(virtualModuleId, datasJson);
                            BaseModuleDataItemBean moduleDataItemBean = BaseModuleDataItemBean.parseMainModuleJsonObject(context, virtualModuleId, datasJson);
                            List<BaseModuleDataItemBean> moduleDataItemList = null;
                            if (moduleDataItemBean != null) {
                                moduleDataItemList = moduleDataItemBean.getChildModuleDataItemList();
                                if (moduleDataItemList == null || moduleDataItemList.isEmpty()) {
                                    if (moduleDataItemList == null) {
                                        moduleDataItemList = new ArrayList<>();
                                    }
                                    moduleDataItemList.add(moduleDataItemBean);
                                }
                                for (int i = 0; i < moduleDataItemList.size(); i++) {
                                    moduleDataItemList.get(i).setBaseResponseBean(baseResponseBean);
                                }
                                try {
                                    Iterator i$ = moduleDataItemList.iterator();
                                    while (true) {
                                        if (i$.hasNext()) {
                                            BaseModuleDataItemBean moduleDataItem = i$.next();
                                            if (moduleDataItem != null && moduleDataItem.getClearFlag() == 1) {
                                                AdvertFilterTable.getInstance(context).deleteAllData();
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (moduleDataItemList != null) {
                                if (!moduleDataItemList.isEmpty()) {
                                    adControlRequestListener.onFinish(16, moduleDataItemBean, moduleDataItemList);
                                    return;
                                }
                            }
                            adControlRequestListener.onFinish(20, moduleDataItemBean, (List<BaseModuleDataItemBean>) null);
                            return;
                        }
                        if (LogUtils.isShowLog()) {
                            LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getAdControlInfoFromNetwork(onFinish--fail, status:" + status + ", responseJson:" + json + ")");
                        }
                        adControlRequestListener.onFinish(18, (BaseModuleDataItemBean) null, (List<BaseModuleDataItemBean>) null);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                public void onException(THttpRequest request, int reason) {
                    adControlRequestListener.onFinish(18, (BaseModuleDataItemBean) null, (List<BaseModuleDataItemBean>) null);
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getAdControlInfoFromNetwork(onException, reason:" + reason + ", virtualModuleId:" + virtualModuleId + ")");
                    }
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            });
        }
    }

    public void saveBatchAdControlInfoFromNetwork(final Context context, final List<ModuleRequestParams> moduleRequestParams, boolean isAddFilterPackageNames, AdSdkParamsBuilder apb) {
        AdSdkRequestDataUtils.requestAdControlInfo(context, moduleRequestParams, "", isAddFilterPackageNames, apb, (IConnectListener) new IConnectListener() {
            public void onStart(THttpRequest request) {
                LogUtils.d("Ad_SDK", "saveBatchAdControlInfoFromNetwork(onStart)");
            }

            public void onFinish(THttpRequest request, IResponse response) {
                List unused = AdControlManager.processBatchResponse(context, moduleRequestParams, request, response);
            }

            public void onException(THttpRequest request, int reason) {
                LogUtils.d("Ad_SDK", "saveBatchAdControlInfoFromNetwork(onException, reason:" + reason);
            }

            public void onException(THttpRequest request, HttpResponse response, int reason) {
                onException(request, reason);
            }
        });
    }

    /* access modifiers changed from: private */
    public static List<BaseModuleDataItemBean> processBatchResponse(Context context, List<ModuleRequestParams> moduleRequestParams, THttpRequest request, IResponse response) {
        List<BaseModuleDataItemBean> result = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(StringUtils.toString(response.getResponse()));
            JSONObject resultJson = json.has("result") ? json.getJSONObject("result") : null;
            int status = resultJson != null ? resultJson.getInt("status") : -1;
            LogUtils.d("Ad_SDK", "saveBatchAdControlInfoFromNetwork(onFinish, status:" + status + "[" + (1 == status) + "])");
            if (1 == status) {
                for (ModuleRequestParams param : moduleRequestParams) {
                    int virtualModuleId = param.getModuleId().intValue();
                    if (!AdSdkRequestDataUtils.canAdModuleReachable(context, virtualModuleId, json.optJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_MFLAG))) {
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]ad module(" + virtualModuleId + ")removed-saveBatchAdControlInfoFromNetwork");
                    } else {
                        BaseResponseBean baseResponseBean = BaseResponseBean.parseBaseResponseBeanJSONObject(context, virtualModuleId, json);
                        if (baseResponseBean != null) {
                            LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]virtualModuleId=" + virtualModuleId + " user=" + baseResponseBean.getUser() + " buychanneltype=" + baseResponseBean.getBuychanneltype());
                            baseResponseBean.saveSelfDataToSdcard(virtualModuleId);
                        }
                        JSONObject datasJson = json.getJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_DATAS);
                        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]saveBatchAdControlInfoFromNetwork(" + virtualModuleId + ", " + datasJson + ")");
                        BaseModuleDataItemBean.saveAdDataToSdcard(virtualModuleId, datasJson);
                        BaseModuleDataItemBean moduleDataItemBean = BaseModuleDataItemBean.parseMainModuleJsonObject(context, virtualModuleId, datasJson);
                        result.add(moduleDataItemBean);
                        if (moduleDataItemBean != null) {
                            List<BaseModuleDataItemBean> moduleDataItemList = moduleDataItemBean.getChildModuleDataItemList();
                            if (moduleDataItemList == null || moduleDataItemList.isEmpty()) {
                                if (moduleDataItemList == null) {
                                    moduleDataItemList = new ArrayList<>();
                                }
                                moduleDataItemList.add(moduleDataItemBean);
                            }
                            for (int i = 0; i < moduleDataItemList.size(); i++) {
                                moduleDataItemList.get(i).setBaseResponseBean(baseResponseBean);
                            }
                            try {
                                Iterator i$ = moduleDataItemList.iterator();
                                while (true) {
                                    if (i$.hasNext()) {
                                        BaseModuleDataItemBean moduleDataItem = i$.next();
                                        if (moduleDataItem != null && moduleDataItem.getClearFlag() == 1) {
                                            AdvertFilterTable.getInstance(context).deleteAllData();
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                LogUtils.d("Ad_SDK", "saveBatchAdControlInfoFromNetwork(onFinish--fail, status:" + status + ", responseJson:" + json + ")");
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public void getBatchModuleControlInfo(Context context, List<ModuleRequestParams> moduleRequestParams, AdSdkParamsBuilder apb, boolean requestNetwork, IBacthControlListener listener) {
        if (listener != null) {
            final List<BaseModuleDataItemBean> result = new ArrayList<>();
            ArrayList arrayList = new ArrayList();
            AdControlManager adControlManager = getInstance(context);
            for (ModuleRequestParams param : moduleRequestParams) {
                int virtualModuleId = param.getModuleId().intValue();
                MainModuleDataItemBeanWrapper mainWrapper = new MainModuleDataItemBeanWrapper();
                adControlManager.getAdControlInfoFromCacheData(context, virtualModuleId, mainWrapper);
                if (mainWrapper.getMainModuleDataItemBean() != null) {
                    result.add(mainWrapper.getMainModuleDataItemBean());
                } else {
                    arrayList.add(param);
                }
            }
            if (!requestNetwork || arrayList.isEmpty() || !NetworkUtils.isNetworkOK(context)) {
                listener.onFinish(result);
                return;
            }
            final Context context2 = context;
            final List<ModuleRequestParams> list = moduleRequestParams;
            final IBacthControlListener iBacthControlListener = listener;
            AdSdkRequestDataUtils.requestAdControlInfo(context, (List<ModuleRequestParams>) arrayList, "", true, apb, (IConnectListener) new IConnectListener() {
                public void onStart(THttpRequest request) {
                    LogUtils.d("Ad_SDK", "getBatchModuleControlInfo(onStart)");
                }

                public void onFinish(THttpRequest request, IResponse response) {
                    result.addAll(AdControlManager.processBatchResponse(context2, list, request, response));
                    iBacthControlListener.onFinish(result);
                }

                public void onException(THttpRequest request, int reason) {
                    LogUtils.d("Ad_SDK", "getBatchModuleControlInfo(onException, reason:" + reason);
                    iBacthControlListener.onFinish(result);
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void loadSingleAdSourceInfo(AdSdkParamsBuilder adSdkParams, List<BaseModuleDataItemBean> moduleDataItemList, int adSourceIndex, int pageId, boolean isLoadSdkAd, AdModuleInfoBean returnResultBean, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
        Object obj;
        Context context = adSdkParams.mContext;
        int virtualModuleId = adSdkParams.mVirtualModuleId;
        int adCount = adSdkParams.mReturnAdCount;
        final boolean isRequestData = adSdkParams.mIsRequestData;
        boolean z = adSdkParams.mIsAddFilterPackageNames;
        String str = adSdkParams.mBuyuserchannel;
        Integer num = adSdkParams.mCdays;
        boolean needShownFilter = adSdkParams.mNeedShownFilter;
        if (loadAdvertDataListener == null) {
            LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadSingleAdSourceInfo(failure, virtualModuleId:" + virtualModuleId + ", adSourceIndex:" + adSourceIndex + ", adCount:" + adCount + ", loadAdvertDataListener:" + loadAdvertDataListener + ")");
            return;
        }
        if (returnResultBean != null) {
            BaseModuleDataItemBean curModuleDataItemBean = (adSourceIndex < 0 || moduleDataItemList == null || moduleDataItemList.size() <= adSourceIndex) ? null : moduleDataItemList.get(adSourceIndex);
            boolean isReturn = false;
            if (curModuleDataItemBean != null && curModuleDataItemBean.isSdkOnlineAdType()) {
                SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = returnResultBean.getSdkAdSourceAdInfoBean();
                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                if (AdModuleInfoBean.isMobileCoreAd(curModuleDataItemBean) || (adViewList != null && !adViewList.isEmpty())) {
                    isReturn = true;
                }
            }
            if (isReturn || (returnResultBean.getAdInfoList() != null && !returnResultBean.getAdInfoList().isEmpty())) {
                loadAdvertDataListener.onAdInfoFinish(false, returnResultBean);
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]loadSingleAdSourceInfo(success, virtualModuleId:" + virtualModuleId + ", adSourceIndex:" + adSourceIndex + ", adCount:" + adCount + ")");
                    return;
                }
                return;
            }
        }
        int adSourceIndex2 = adSourceIndex + 1;
        if (moduleDataItemList == null || moduleDataItemList.size() <= adSourceIndex2) {
            if (moduleDataItemList != null) {
                try {
                    if (moduleDataItemList.size() == 1) {
                        BaseModuleDataItemBean moduleDataItemBean = moduleDataItemList.get(0);
                        List<BaseAppInfoBean> appInfoList = moduleDataItemBean != null ? moduleDataItemBean.getAppInfoList() : null;
                        if (appInfoList != null && appInfoList.size() > 0) {
                            String adControlCacheData = FileCacheUtils.readCacheDataToString(BaseModuleDataItemBean.getCacheFileName(virtualModuleId), true);
                            if (!TextUtils.isEmpty(adControlCacheData)) {
                                loadAdvertDataListener.onAdInfoFinish(true, BaseModuleDataItemBean.getOfflineAdInfoList(context, virtualModuleId, adCount, needShownFilter, moduleDataItemBean, new JSONObject(adControlCacheData)));
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadSingleAdSourceInfo(success---AlternateData, virtualModuleId:" + virtualModuleId + ", adSourceIndex:" + adSourceIndex2 + ", adCount:" + adCount + ", moduleDataItemList.size:" + (moduleDataItemList != null ? Integer.valueOf(moduleDataItemList.size()) : "null") + ")");
                                    return;
                                }
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadAdvertDataListener.onAdFail(21);
            if (LogUtils.isShowLog()) {
                StringBuilder append = new StringBuilder().append("[vmId:").append(virtualModuleId).append("]loadSingleAdSourceInfo(failure, virtualModuleId:").append(virtualModuleId).append(", adSourceIndex:").append(adSourceIndex2).append(", adCount:").append(adCount).append(", moduleDataItemList.size:");
                if (moduleDataItemList != null) {
                    obj = Integer.valueOf(moduleDataItemList.size());
                } else {
                    obj = "null";
                }
                LogUtils.i("Ad_SDK", append.append(obj).append(")").toString());
                return;
            }
            return;
        }
        BaseModuleDataItemBean moduleDataItemBean2 = moduleDataItemList.get(adSourceIndex2);
        if (moduleDataItemBean2 == null) {
            loadSingleAdSourceInfo(adSdkParams, moduleDataItemList, adSourceIndex2, pageId, isLoadSdkAd, (AdModuleInfoBean) null, loadAdvertDataListener);
            return;
        }
        final int tempAdSourceIndex = adSourceIndex2;
        try {
            if (!moduleDataItemBean2.isOfflineAdType()) {
                if (moduleDataItemBean2.isSdkOnlineAdType()) {
                    if (!isLoadSdkAd) {
                        loadAdvertDataListener.onAdInfoFinish(false, returnResultBean);
                        return;
                    } else if (AdModuleInfoBean.isFaceBookAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        final int i = virtualModuleId;
                        final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean2;
                        final Context context2 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
                        final List<BaseModuleDataItemBean> list = moduleDataItemList;
                        final int i2 = pageId;
                        final boolean z2 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener = loadAdvertDataListener;
                        SdkAdProxy.getInstance().loadFaceBookAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + i + "]loadSingleAdSourceInfo(FaceBook--onFinish, virtualModuleId:" + i + ", adCount:" + (adViewList != null ? adViewList.size() : -1) + ")");
                                }
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    if (sdkAdSourceAdInfoBean.isFakeFbNative()) {
                                        adModuleInfoBean.setFakeFbNativeControlInfo(baseModuleDataItemBean, FbNativeAdTrick.adWrapper2AdInfoBeans(adViewList));
                                    } else {
                                        adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                        adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean);
                                    }
                                }
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context2, adSdkParamsBuilder, baseModuleDataItemBean);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder, list, tempAdSourceIndex, i2, z2, adModuleInfoBean, iLoadAdvertDataListener);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder, list, tempAdSourceIndex, i2, z2, AdCachePool.getCacheAd(context2, adSdkParamsBuilder, baseModuleDataItemBean), iLoadAdvertDataListener);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + i + "]loadSingleAdSourceInfo(FaceBook--onException, virtualModuleId:" + i + ", " + statusCode + ")");
                                }
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                AdInfoStatisticManager.getInstance(context2).setHasClicked(AdControlManager.this.mContext, adViewObj, true);
                                iLoadAdvertDataListener.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (AdModuleInfoBean.isAdMobAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        final BaseModuleDataItemBean baseModuleDataItemBean2 = moduleDataItemBean2;
                        final Context context3 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder2 = adSdkParams;
                        final List<BaseModuleDataItemBean> list2 = moduleDataItemList;
                        final int i3 = pageId;
                        final boolean z3 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener2 = loadAdvertDataListener;
                        SdkAdProxy.getInstance().loadAdMobAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                    adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean2);
                                }
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context3, adSdkParamsBuilder2, baseModuleDataItemBean2);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder2, list2, tempAdSourceIndex, i3, z3, adModuleInfoBean, iLoadAdvertDataListener2);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder2, list2, tempAdSourceIndex, i3, z3, AdCachePool.getCacheAd(context3, adSdkParamsBuilder2, baseModuleDataItemBean2), iLoadAdvertDataListener2);
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener2.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener2.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener2.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (AdModuleInfoBean.isMobileCoreAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        final BaseModuleDataItemBean baseModuleDataItemBean3 = moduleDataItemBean2;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener3 = loadAdvertDataListener;
                        final AdSdkParamsBuilder adSdkParamsBuilder3 = adSdkParams;
                        final List<BaseModuleDataItemBean> list3 = moduleDataItemList;
                        final int i4 = pageId;
                        final boolean z4 = isLoadSdkAd;
                        SdkAdProxy.getInstance().loadMobileCoreAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                AdModuleInfoBean adModuleInfoBean = new AdModuleInfoBean();
                                adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean3);
                                iLoadAdvertDataListener3.onAdInfoFinish(false, adModuleInfoBean);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder3, list3, tempAdSourceIndex, i4, z4, (AdModuleInfoBean) null, iLoadAdvertDataListener3);
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener3.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener3.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener3.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (AdModuleInfoBean.isLoopMeAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        final int i5 = virtualModuleId;
                        final BaseModuleDataItemBean baseModuleDataItemBean4 = moduleDataItemBean2;
                        final Context context4 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder4 = adSdkParams;
                        final List<BaseModuleDataItemBean> list4 = moduleDataItemList;
                        final int i6 = pageId;
                        final boolean z5 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener4 = loadAdvertDataListener;
                        SdkAdProxy.getInstance().loadLoopMeAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + i5 + "]loadSingleAdSourceInfo(LoopMe--onFinish, virtualModuleId:" + i5 + ", adCount:" + (adViewList != null ? adViewList.size() : -1) + ")");
                                }
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                    adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean4);
                                }
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context4, adSdkParamsBuilder4, baseModuleDataItemBean4);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder4, list4, tempAdSourceIndex, i6, z5, adModuleInfoBean, iLoadAdvertDataListener4);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder4, list4, tempAdSourceIndex, i6, z5, AdCachePool.getCacheAd(context4, adSdkParamsBuilder4, baseModuleDataItemBean4), iLoadAdvertDataListener4);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + i5 + "]loadSingleAdSourceInfo(LoopMe--onException, virtualModuleId:" + i5 + ", " + statusCode + ")");
                                }
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener4.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener4.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener4.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (AdModuleInfoBean.isVungleAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadVungleAdAdInfo()");
                        }
                        AdModuleInfoBean adModuleInfoBean = new AdModuleInfoBean();
                        adModuleInfoBean.setSdkAdControlInfo(moduleDataItemBean2);
                        loadAdvertDataListener.onAdInfoFinish(false, adModuleInfoBean);
                        return;
                    } else if (AdModuleInfoBean.isApplovinAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadApplovinInfo()");
                        }
                        AdModuleInfoBean adModuleInfoBean2 = new AdModuleInfoBean();
                        adModuleInfoBean2.setSdkAdControlInfo(moduleDataItemBean2);
                        loadAdvertDataListener.onAdInfoFinish(false, adModuleInfoBean2);
                        return;
                    } else if (AdModuleInfoBean.isCheetahAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadCheetahInfo()");
                        }
                        AdModuleInfoBean adModuleInfoBean3 = new AdModuleInfoBean();
                        adModuleInfoBean3.setSdkAdControlInfo(moduleDataItemBean2);
                        loadAdvertDataListener.onAdInfoFinish(false, adModuleInfoBean3);
                        return;
                    } else if (AdModuleInfoBean.isCheetahVideoAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadCheetahVideoInfo()");
                        }
                        AdModuleInfoBean adModuleInfoBean4 = new AdModuleInfoBean();
                        adModuleInfoBean4.setSdkAdControlInfo(moduleDataItemBean2);
                        loadAdvertDataListener.onAdInfoFinish(false, adModuleInfoBean4);
                        return;
                    } else if (AdModuleInfoBean.isIronScrAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadIronScrInfo()");
                        }
                        final long startTime = System.currentTimeMillis();
                        AdSdkOperationStatistic.uploadAdRequestStatistic(context, IronScrAd.IRON_ID, adSdkParams.mTabCategory, moduleDataItemBean2, adSdkParams);
                        final int i7 = virtualModuleId;
                        final BaseModuleDataItemBean baseModuleDataItemBean5 = moduleDataItemBean2;
                        final Context context5 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder5 = adSdkParams;
                        final List<BaseModuleDataItemBean> list5 = moduleDataItemList;
                        final int i8 = tempAdSourceIndex;
                        final int i9 = pageId;
                        final boolean z6 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener5 = loadAdvertDataListener;
                        SdkAdProxy.getInstance().loadIronScrAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                int i;
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                if (LogUtils.isShowLog()) {
                                    StringBuilder append = new StringBuilder().append("[vmId:").append(i7).append("]loadSingleAdSourceInfo(IronScrAd--onFinish, virtualModuleId:").append(i7).append(", adCount:");
                                    if (adViewList != null) {
                                        i = adViewList.size();
                                    } else {
                                        i = -1;
                                    }
                                    LogUtils.i("Ad_SDK", append.append(i).append(")").toString());
                                }
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                    adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean5);
                                }
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context5, IronScrAd.IRON_ID, adSdkParamsBuilder5.mTabCategory, sdkAdSourceAdInfoBean == null ? -2 : adViewList != null ? adViewList.size() : -1, baseModuleDataItemBean5, System.currentTimeMillis() - startTime, adSdkParamsBuilder5);
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context5, adSdkParamsBuilder5, baseModuleDataItemBean5);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder5, list5, i8, i9, z6, adModuleInfoBean, iLoadAdvertDataListener5);
                            }

                            public void onException(int statusCode) {
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context5, IronScrAd.IRON_ID, adSdkParamsBuilder5.mTabCategory, -1, baseModuleDataItemBean5, System.currentTimeMillis() - startTime, adSdkParamsBuilder5);
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder5, list5, i8, i9, z6, AdCachePool.getCacheAd(context5, adSdkParamsBuilder5, baseModuleDataItemBean5), iLoadAdvertDataListener5);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + i7 + "]loadSingleAdSourceInfo(IronScrAd--onException, virtualModuleId:" + i7 + ", " + statusCode + ")");
                                }
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener5.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener5.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener5.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (AdModuleInfoBean.isMoPubAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                        final int i10 = virtualModuleId;
                        final BaseModuleDataItemBean baseModuleDataItemBean6 = moduleDataItemBean2;
                        final Context context6 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder6 = adSdkParams;
                        final List<BaseModuleDataItemBean> list6 = moduleDataItemList;
                        final int i11 = pageId;
                        final boolean z7 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener6 = loadAdvertDataListener;
                        SdkAdProxy.getInstance().loadMoPubAdInfo(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + i10 + "]loadSingleAdSourceInfo(MoPubAd--onFinish, virtualModuleId:" + i10 + ", adCount:" + (adViewList != null ? adViewList.size() : -1) + ")");
                                }
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                    adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean6);
                                }
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context6, adSdkParamsBuilder6, baseModuleDataItemBean6);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder6, list6, tempAdSourceIndex, i11, z7, adModuleInfoBean, iLoadAdvertDataListener6);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder6, list6, tempAdSourceIndex, i11, z7, AdCachePool.getCacheAd(context6, adSdkParamsBuilder6, baseModuleDataItemBean6), iLoadAdvertDataListener6);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + i10 + "]loadSingleAdSourceInfo(IronScrAd--onException, virtualModuleId:" + i10 + ", " + statusCode + ")");
                                }
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener6.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener6.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener6.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    } else if (OuterAdLoader.canProcess(adSdkParams, moduleDataItemBean2)) {
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]loadUnKnownAdSourceInfo(onStart)");
                        }
                        final int i12 = virtualModuleId;
                        final BaseModuleDataItemBean baseModuleDataItemBean7 = moduleDataItemBean2;
                        final Context context7 = context;
                        final AdSdkParamsBuilder adSdkParamsBuilder7 = adSdkParams;
                        final List<BaseModuleDataItemBean> list7 = moduleDataItemList;
                        final int i13 = pageId;
                        final boolean z8 = isLoadSdkAd;
                        final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener7 = loadAdvertDataListener;
                        OuterAdLoader.ProcessUnKnownAdSource(adSdkParams, moduleDataItemBean2, new SdkAdSourceRequestListener() {
                            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                                List<SdkAdSourceAdWrapper> adViewList = sdkAdSourceAdInfoBean != null ? sdkAdSourceAdInfoBean.getAdViewList() : null;
                                if (LogUtils.isShowLog()) {
                                    LogUtils.i("Ad_SDK", "[vmId:" + i12 + "]loadUnKnownAdSourceInfo(onFinish, virtualModuleId:" + i12 + ", adCount:" + (adViewList != null ? adViewList.size() : -1) + ")");
                                }
                                AdModuleInfoBean adModuleInfoBean = null;
                                if (adViewList != null && !adViewList.isEmpty()) {
                                    adModuleInfoBean = new AdModuleInfoBean();
                                    adModuleInfoBean.setSdkAdSourceAdInfoBean(sdkAdSourceAdInfoBean);
                                    adModuleInfoBean.setSdkAdControlInfo(baseModuleDataItemBean7);
                                }
                                if (adModuleInfoBean == null) {
                                    adModuleInfoBean = AdCachePool.getCacheAd(context7, adSdkParamsBuilder7, baseModuleDataItemBean7);
                                }
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder7, list7, tempAdSourceIndex, i13, z8, adModuleInfoBean, iLoadAdvertDataListener7);
                            }

                            public void onException(int statusCode) {
                                AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder7, list7, tempAdSourceIndex, i13, z8, AdCachePool.getCacheAd(context7, adSdkParamsBuilder7, baseModuleDataItemBean7), iLoadAdvertDataListener7);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.w("Ad_SDK", "[vmId:" + i12 + "]loadUnKnownAdSourceInfo(onException, virtualModuleId:" + i12 + ", " + statusCode + ")");
                                }
                            }

                            public void onAdShowed(Object adViewObj) {
                                iLoadAdvertDataListener7.onAdShowed(adViewObj);
                            }

                            public void onAdClicked(Object adViewObj) {
                                iLoadAdvertDataListener7.onAdClicked(adViewObj);
                            }

                            public void onAdClosed(Object adViewObj) {
                                iLoadAdvertDataListener7.onAdClosed(adViewObj);
                            }
                        });
                        return;
                    }
                } else if (AdModuleInfoBean.isGomoAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                    final BaseModuleDataItemBean baseModuleDataItemBean8 = moduleDataItemBean2;
                    final AdSdkParamsBuilder adSdkParamsBuilder8 = adSdkParams;
                    final int i14 = virtualModuleId;
                    final Context context8 = context;
                    final int i15 = adCount;
                    final boolean z9 = needShownFilter;
                    final List<BaseModuleDataItemBean> list8 = moduleDataItemList;
                    final int i16 = tempAdSourceIndex;
                    final int i17 = pageId;
                    final boolean z10 = isLoadSdkAd;
                    final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener8 = loadAdvertDataListener;
                    new AdSdkThread("loadGomoAd", (Runnable) new Runnable() {
                        public void run() {
                            AdModuleInfoBean adModuleInfoBean = null;
                            String[] posIds = baseModuleDataItemBean8 != null ? baseModuleDataItemBean8.getFbIds() : null;
                            int posId = (posIds == null || posIds.length <= 0) ? -1 : StringUtils.toInteger(posIds[0], -1).intValue();
                            if (posId != -1) {
                                String tabCategory = adSdkParamsBuilder8.mTabCategory;
                                if (LogUtils.isShowLog()) {
                                    LogUtils.d("Ad_SDK", "[vmId:" + i14 + "]loadGomoAds:tabCategory=" + tabCategory);
                                }
                                long startTime = System.currentTimeMillis();
                                AdSdkOperationStatistic.uploadAdRequestStatistic(context8, "", tabCategory, baseModuleDataItemBean8, adSdkParamsBuilder8);
                                adModuleInfoBean = GomoAdHelper.loadGomoAds(context8, baseModuleDataItemBean8, i14, i15, baseModuleDataItemBean8.getFbAdvCount(), posId, isRequestData, z9, (List<String>) null);
                                int result = -1;
                                if (!(adModuleInfoBean == null || adModuleInfoBean.getAdInfoList() == null)) {
                                    result = adModuleInfoBean.getAdInfoList().size();
                                }
                                if (result > 0 && adSdkParamsBuilder8.mReturnAdCount > 0 && result > adSdkParamsBuilder8.mReturnAdCount) {
                                    adModuleInfoBean.setAdInfoList(adModuleInfoBean.getAdInfoList().subList(0, adSdkParamsBuilder8.mReturnAdCount));
                                }
                                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context8, "", tabCategory, result, baseModuleDataItemBean8, System.currentTimeMillis() - startTime, adSdkParamsBuilder8);
                                if (LogUtils.isShowLog()) {
                                    LogUtils.d("Ad_SDK", "[vmId:" + i14 + "]loadGomoAds(success--GomoAd:" + "fbAdvCount:" + baseModuleDataItemBean8.getFbAdvCount() + ", fbId:" + posId + ", getAdCount:" + ((adModuleInfoBean == null || adModuleInfoBean.getAdInfoList() == null) ? "-1" : Integer.valueOf(adModuleInfoBean.getAdInfoList().size())) + ")");
                                }
                            }
                            AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder8, list8, i16, i17, z10, adModuleInfoBean, iLoadAdvertDataListener8);
                        }
                    }).start();
                    return;
                } else if (AdModuleInfoBean.isS2SAd(moduleDataItemBean2) && adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                    final AdSdkParamsBuilder adSdkParamsBuilder9 = adSdkParams;
                    final BaseModuleDataItemBean baseModuleDataItemBean9 = moduleDataItemBean2;
                    final Context context9 = context;
                    final int i18 = virtualModuleId;
                    final int i19 = adCount;
                    final boolean z11 = needShownFilter;
                    final List<BaseModuleDataItemBean> list9 = moduleDataItemList;
                    final int i20 = tempAdSourceIndex;
                    final int i21 = pageId;
                    final boolean z12 = isLoadSdkAd;
                    final AdSdkManager.ILoadAdvertDataListener iLoadAdvertDataListener9 = loadAdvertDataListener;
                    new AdSdkThread("loadS2SAd", (Runnable) new Runnable() {
                        public void run() {
                            String tabCategory = adSdkParamsBuilder9.mTabCategory;
                            String[] posIds = baseModuleDataItemBean9 != null ? baseModuleDataItemBean9.getFbIds() : null;
                            int posId = (posIds == null || posIds.length <= 0) ? -1 : StringUtils.toInteger(posIds[0], -1).intValue();
                            if (LogUtils.isShowLog()) {
                                LogUtils.d("Ad_SDK", "loadSingleAdSource:onlineApi:tabCategory=" + tabCategory);
                            }
                            long startTime = System.currentTimeMillis();
                            AdSdkOperationStatistic.uploadAdRequestStatistic(context9, "", tabCategory, baseModuleDataItemBean9, adSdkParamsBuilder9);
                            AdModuleInfoBean adModuleInfoBean = AdControlManager.this.loadOnlineAdInfo(context9, baseModuleDataItemBean9, i18, i19, baseModuleDataItemBean9.getFbAdvCount(), posId, isRequestData, z11, (List<String>) null, adSdkParamsBuilder9.mS2SParams);
                            int result = -1;
                            if (!(adModuleInfoBean == null || adModuleInfoBean.getAdInfoList() == null)) {
                                result = adModuleInfoBean.getAdInfoList().size();
                            }
                            if (result > 0 && adSdkParamsBuilder9.mReturnAdCount > 0 && result > adSdkParamsBuilder9.mReturnAdCount) {
                                adModuleInfoBean.setAdInfoList(adModuleInfoBean.getAdInfoList().subList(0, adSdkParamsBuilder9.mReturnAdCount));
                            }
                            AdSdkOperationStatistic.uploadAdRequestResultStatistic(context9, "", tabCategory, result, baseModuleDataItemBean9, System.currentTimeMillis() - startTime, adSdkParamsBuilder9);
                            if (LogUtils.isShowLog()) {
                                LogUtils.d("Ad_SDK", "[vmId:" + i18 + "]loadSingleAdSourceInfo(success--onlineAd, virtualModuleId:" + i18 + ", fbAdvCount:" + baseModuleDataItemBean9.getFbAdvCount() + ", adPosId:" + posId + ", getAdCount:" + ((adModuleInfoBean == null || adModuleInfoBean.getAdInfoList() == null) ? "-1" : Integer.valueOf(adModuleInfoBean.getAdInfoList().size())) + ")");
                            }
                            AdControlManager.this.loadSingleAdSourceInfo(adSdkParamsBuilder9, list9, i20, i21, z12, adModuleInfoBean, iLoadAdvertDataListener9);
                        }
                    }).start();
                    return;
                }
                loadSingleAdSourceInfo(adSdkParams, moduleDataItemList, adSourceIndex2, pageId, isLoadSdkAd, (AdModuleInfoBean) null, loadAdvertDataListener);
            }
            if (adSdkParams.commonLoadCondition(moduleDataItemBean2)) {
                String tabCategory = adSdkParams.mTabCategory;
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "loadSingleAdSource:offlineAd:tabCategory=" + tabCategory);
                }
                long startTime2 = System.currentTimeMillis();
                AdSdkOperationStatistic.uploadAdRequestStatistic(context, "", tabCategory, moduleDataItemBean2, adSdkParams);
                String adControlCacheData2 = FileCacheUtils.readCacheDataToString(BaseModuleDataItemBean.getCacheFileName(virtualModuleId), true);
                if (!TextUtils.isEmpty(adControlCacheData2)) {
                    try {
                        returnResultBean = BaseModuleDataItemBean.getOfflineAdInfoList(context, virtualModuleId, adCount, needShownFilter, moduleDataItemBean2, new JSONObject(adControlCacheData2));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                int result = -1;
                if (returnResultBean != null) {
                    if (returnResultBean.getAdInfoList() != null) {
                        result = returnResultBean.getAdInfoList().size();
                    }
                }
                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, "", tabCategory, result, moduleDataItemBean2, System.currentTimeMillis() - startTime2, adSdkParams);
                if (LogUtils.isShowLog()) {
                    LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]loadSingleAdSourceInfo(success--offlineAd[cache], virtualModuleId:" + virtualModuleId + ", getAdCount:" + ((returnResultBean == null || returnResultBean.getAdInfoList() == null) ? "-1" : Integer.valueOf(returnResultBean.getAdInfoList().size())) + ")");
                }
                loadSingleAdSourceInfo(adSdkParams, moduleDataItemList, adSourceIndex2, pageId, isLoadSdkAd, returnResultBean, loadAdvertDataListener);
                return;
            }
            loadSingleAdSourceInfo(adSdkParams, moduleDataItemList, adSourceIndex2, pageId, isLoadSdkAd, (AdModuleInfoBean) null, loadAdvertDataListener);
        } catch (Exception e3) {
            e3.printStackTrace();
            LogUtils.e("Ad_SDK", "[vmId:" + virtualModuleId + "]loadSingleAdSourceInfo(error, " + e3.getMessage() + ")", e3);
        }
    }

    public void loadAdInfo(AdSdkParamsBuilder adSdkParams, boolean isCacheData, int pageId, boolean isLoadSdkAd, List<BaseModuleDataItemBean> moduleDataItemList) {
        final Context context = adSdkParams.mContext;
        final int virtualModuleId = adSdkParams.mVirtualModuleId;
        final boolean isNeedDownloadIcon = adSdkParams.mIsNeedDownloadIcon;
        final boolean isNeedDownloadBanner = adSdkParams.mIsNeedDownloadBanner;
        final boolean isNeedPreResolve = adSdkParams.mIsNeedPreResolve;
        final boolean isPreResolveBeforeShow = adSdkParams.mIsPreResolveBeforeShow;
        final AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener = adSdkParams.mLoadAdvertDataListener;
        if (LogUtils.isShowLog() && moduleDataItemList != null && !moduleDataItemList.isEmpty()) {
            for (BaseModuleDataItemBean moduleDataItemBean : moduleDataItemList) {
                LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]广告源信息" + AdSdkLogUtils.getSimpleLogString(moduleDataItemBean));
            }
        }
        loadSingleAdSourceInfo(adSdkParams, moduleDataItemList, -1, pageId, isLoadSdkAd, (AdModuleInfoBean) null, new AdSdkManager.ILoadAdvertDataListener() {
            public void onAdInfoFinish(boolean isCache, AdModuleInfoBean adModuleInfoBean) {
                if (LogUtils.isShowLog()) {
                    BaseModuleDataItemBean moduleDataItemBean = adModuleInfoBean != null ? adModuleInfoBean.getModuleDataItemBean() : null;
                    if (moduleDataItemBean != null) {
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]onAdInfoFinish(return, virtualModuleId:" + virtualModuleId + ", ModuleId:" + moduleDataItemBean.getModuleId() + ", " + adModuleInfoBean.getAdType() + ", AdvDataSource:" + moduleDataItemBean.getAdvDataSource() + ", Onlineadvtype:" + moduleDataItemBean.getOnlineAdvType() + ")");
                    } else {
                        LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]onAdInfoFinish(return, virtualModuleId:" + virtualModuleId + ", adModuleInfoBean or ModuleDataItemBean is null)");
                    }
                }
                AdSdkManager.handleAdData(context, isCache, adModuleInfoBean, isNeedDownloadIcon, isNeedDownloadBanner, isNeedPreResolve, isPreResolveBeforeShow, loadAdvertDataListener);
            }

            public void onAdImageFinish(AdModuleInfoBean adModuleInfoBean) {
            }

            public void onAdFail(int statusCode) {
                if (loadAdvertDataListener != null) {
                    loadAdvertDataListener.onAdFail(statusCode);
                }
                if (LogUtils.isShowLog()) {
                    LogUtils.w("Ad_SDK", "[vmId:" + virtualModuleId + "]onAdFail(return, statusCode:" + statusCode + ")");
                }
            }

            public void onAdShowed(Object adViewObj) {
                loadAdvertDataListener.onAdShowed(adViewObj);
            }

            public void onAdClicked(Object adViewObj) {
                loadAdvertDataListener.onAdClicked(adViewObj);
            }

            public void onAdClosed(Object adViewObj) {
                loadAdvertDataListener.onAdClosed(adViewObj);
            }
        });
    }

    public AdModuleInfoBean loadOnlineAdInfo(Context context, BaseModuleDataItemBean moduleDataItemBean, int virtualModuleId, int adCount, int requestAdCount, int onlineAdPosId, boolean isRequestData, boolean needShownFilter, List<String> installFilterException, AdSdkRequestHeader.S2SParams params) {
        AdModuleInfoBean adInfoBean;
        int i;
        if (!isRequestData) {
            String adCacheData = FileCacheUtils.readCacheDataToString(BaseOnlineModuleInfoBean.getCacheFileName(onlineAdPosId), true);
            if (!TextUtils.isEmpty(adCacheData)) {
                try {
                    AdModuleInfoBean adInfoBean2 = BaseOnlineModuleInfoBean.getOnlineAdInfoList(context, moduleDataItemBean, onlineAdPosId, adCount, needShownFilter, installFilterException, new JSONObject(adCacheData));
                    List<BaseOnlineAdInfoBean> onlineAdInfoList = adInfoBean2 != null ? adInfoBean2.getOnlineAdInfoList() : null;
                    if (onlineAdInfoList != null && !onlineAdInfoList.isEmpty()) {
                        BaseOnlineModuleInfoBean onlineModuleInfoBean = adInfoBean2.getOnlineModuleInfoBean();
                        long loadAdDataTime = onlineModuleInfoBean != null ? onlineModuleInfoBean.getSaveDataTime() : -1;
                        if (BaseOnlineModuleInfoBean.checkOnlineAdInfoValid(loadAdDataTime)) {
                            if (!LogUtils.isShowLog()) {
                                return adInfoBean2;
                            }
                            StringBuilder append = new StringBuilder().append("loadOnlineAdInfo(end--cacheData, onlineAdPosId:").append(onlineAdPosId).append(", adCount:").append(adCount).append(", requestAdCount:").append(requestAdCount).append(", adSize:");
                            if (adInfoBean2.getOfflineAdInfoList() != null) {
                                i = adInfoBean2.getOfflineAdInfoList().size();
                            } else {
                                i = -1;
                            }
                            LogUtils.d("Ad_SDK", append.append(i).append(")").toString());
                            return adInfoBean2;
                        } else if (LogUtils.isShowLog()) {
                            LogUtils.d("Ad_SDK", "loadOnlineAdInfo(cacheData----cache data expired, loadOnlineAdTime:" + loadAdDataTime + ", onlineAdPosId:" + onlineAdPosId + ", adCount:" + adCount + ", requestAdCount:" + requestAdCount + ")");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (LogUtils.isShowLog()) {
                        LogUtils.e("Ad_SDK", "loadOnlineAdInfo(cacheData---error, Exception:" + (e != null ? e.getMessage() : "") + ", onlineAdPosId:" + onlineAdPosId + ", adCount:" + adCount + ", requestAdCount:" + requestAdCount + ")");
                    }
                }
            }
        }
        final ArrayList arrayList = new ArrayList();
        final int i2 = onlineAdPosId;
        final Context context2 = context;
        final BaseModuleDataItemBean baseModuleDataItemBean = moduleDataItemBean;
        final int i3 = adCount;
        final boolean z = needShownFilter;
        final List<String> list = installFilterException;
        final int i4 = virtualModuleId;
        AdSdkRequestDataUtils.requestOnlineAdInfo(context, requestAdCount, onlineAdPosId, params, new IConnectListener() {
            public void onStart(THttpRequest request) {
            }

            public void onFinish(THttpRequest request, IResponse response) {
                JSONObject resourceMapJsonObj;
                int i;
                List<BaseOnlineAdInfoBean> onlineAdInfoList = null;
                try {
                    JSONObject datasJson = new JSONObject(StringUtils.toString(response.getResponse()));
                    if (datasJson != null) {
                        resourceMapJsonObj = datasJson.optJSONObject(BaseOnlineAdInfoBean.ONLINE_AD_JSON_TAG);
                    } else {
                        resourceMapJsonObj = null;
                    }
                    if (LogUtils.isShowLog()) {
                        LogUtils.i("Ad_SDK", "loadOnlineAdInfo=" + resourceMapJsonObj);
                    }
                    if (resourceMapJsonObj != null && resourceMapJsonObj.length() >= 1) {
                        BaseOnlineModuleInfoBean.saveAdDataToSdcard(i2, resourceMapJsonObj);
                        AdModuleInfoBean adModuleInfoBean = BaseOnlineModuleInfoBean.getOnlineAdInfoList(context2, baseModuleDataItemBean, i2, i3, z, list, resourceMapJsonObj);
                        if (adModuleInfoBean != null) {
                            onlineAdInfoList = adModuleInfoBean.getOnlineAdInfoList();
                        }
                        if (onlineAdInfoList != null && !onlineAdInfoList.isEmpty()) {
                            arrayList.add(adModuleInfoBean);
                        }
                        if (LogUtils.isShowLog()) {
                            StringBuilder append = new StringBuilder().append("loadOnlineAdInfo(success, online ad size:");
                            if (onlineAdInfoList != null) {
                                i = onlineAdInfoList.size();
                            } else {
                                i = -1;
                            }
                            LogUtils.d("Ad_SDK", append.append(i).append(")").toString());
                        }
                    } else if (datasJson != null && LogUtils.isShowLog()) {
                        LogUtils.e("Ad_SDK", "loadOnlineAdInfo(error, " + i2 + ", 错误代码::->" + datasJson.optInt("errorCode", -1) + ", 错误信息::->" + datasJson.optString("msg", "") + ")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("Ad_SDK", "loadOnlineAdInfo(error, virtualModuleId:" + i4 + ", errorMessage:" + (e != null ? e.getMessage() : "") + ")");
                }
            }

            public void onException(THttpRequest request, int reason) {
                LogUtils.e("Ad_SDK", "loadOnlineAdInfo(error, virtualModuleId:" + i4 + ", reason:" + reason + ")");
            }

            public void onException(THttpRequest request, HttpResponse response, int reason) {
                onException(request, reason);
            }
        });
        if (arrayList.size() > 0) {
            adInfoBean = (AdModuleInfoBean) arrayList.get(0);
        } else {
            adInfoBean = null;
        }
        return adInfoBean;
    }

    public void loadIntelligentAdInfo(final Context context, final int adPos, final AdIntellRequestListener listener) {
        if (listener != null) {
            AdSdkRequestDataUtils.requestIntelligentAdInfo(context, adPos, new IConnectListener() {
                public void onStart(THttpRequest request) {
                    LogUtils.i("Ad_SDK", "[adPos:" + adPos + "]loadIntelligentAdInfo(start)");
                }

                public void onFinish(THttpRequest request, IResponse response) {
                    BaseIntellModuleBean moduleBean = null;
                    try {
                        JSONObject json = new JSONObject(StringUtils.toString(response.getResponse()));
                        moduleBean = BaseIntellModuleBean.parseJSONObject(context, adPos, json);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[adPos:" + adPos + "]loadIntelligentAdInfo(json:" + json + ")");
                        }
                        if ((moduleBean == null || moduleBean.getmSuccess() != 1) && LogUtils.isShowLog()) {
                            LogUtils.e("Ad_SDK", "[adPos:" + adPos + "]loadIntelligentAdInfo(serverError,message:" + (moduleBean != null ? moduleBean.getmErrorMessage() : "null") + ")");
                        }
                    } catch (Exception e) {
                        LogUtils.e("Ad_SDK", "loadIntelligentAdInfo--error, adPos:" + adPos, e);
                    } finally {
                        listener.onFinish(moduleBean);
                    }
                }

                public void onException(THttpRequest request, int reason) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[adPos:" + adPos + "]loadIntelligentAdInfo(onException, reason:" + reason + ")");
                    }
                    listener.onFinish((BaseIntellModuleBean) null);
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            });
        }
    }

    public void loadSearchPresolveAdInfo(Context context, int adPos, String key, AdIntellRequestListener listener) {
        if (listener != null) {
            final int i = adPos;
            final String str = key;
            final Context context2 = context;
            final AdIntellRequestListener adIntellRequestListener = listener;
            AdSdkRequestDataUtils.requestSearchPresolveAdInfo(context, adPos, key, new IConnectListener() {
                public void onStart(THttpRequest request) {
                    LogUtils.i("Ad_SDK", "[adPos:" + i + "]loadSearchPresolveAdInfo onStart key=" + str);
                }

                public void onFinish(THttpRequest request, IResponse response) {
                    BaseIntellModuleBean moduleBean = null;
                    try {
                        JSONObject json = new JSONObject(StringUtils.toString(response.getResponse()));
                        moduleBean = BaseIntellModuleBean.parseJSONObject(context2, i, json);
                        if (LogUtils.isShowLog()) {
                            LogUtils.i("Ad_SDK", "[adPos:" + i + "]loadSearchPresolveAdInfo(json:" + json + ")");
                        }
                        if ((moduleBean == null || moduleBean.getmSuccess() != 1) && LogUtils.isShowLog()) {
                            LogUtils.e("Ad_SDK", "[adPos:" + i + "]loadSearchPresolveAdInfo(serverError,message:" + (moduleBean != null ? moduleBean.getmErrorMessage() : "null") + ")");
                        }
                    } catch (Exception e) {
                        LogUtils.e("Ad_SDK", "loadSearchPresolveAdInfo--error, adPos:" + i, e);
                    } finally {
                        adIntellRequestListener.onFinish(moduleBean);
                    }
                }

                public void onException(THttpRequest request, int reason) {
                    if (LogUtils.isShowLog()) {
                        LogUtils.w("Ad_SDK", "[adPos:" + i + "]loadSearchPresolveAdInfo(onException, reason:" + reason + ")");
                    }
                    adIntellRequestListener.onFinish((BaseIntellModuleBean) null);
                }

                public void onException(THttpRequest request, HttpResponse response, int reason) {
                    onException(request, reason);
                }
            });
        }
    }

    private void statisticAdInfo(AdModuleInfoBean adModuleInfoBean) {
    }
}

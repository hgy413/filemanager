package com.jb.filemanager.ad.cache;

import android.app.AlarmManager;
import android.content.Context;
import android.util.SparseArray;

import com.facebook.ads.NativeAd;
import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.ad.AdType;

import com.jb.filemanager.ad.data.AdWrapper;
import com.jb.filemanager.ad.data.OneRequestAds;
import com.jb.filemanager.ad.event.NormalAdLoadCompleteEvent;
import com.jb.filemanager.ad.event.OnAdClickEvent;
import com.jb.filemanager.ad.event.OnAdCloseEvent;
import com.jb.filemanager.buyuser.BuyUserManager;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.sdk.AdmobAdConfig;
import com.jiubang.commerce.ad.sdk.MoPubAdConfig;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;
import com.loopme.LoopMeBanner;
import com.mopub.mobileads.MoPubView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * ZBoost广告数据管理类
 * Created by wangying on 16/1/12.
 */
public class AdDataManager {

    private static final String LOGGER_TAG = "AdDataManager";
    private static final long FAST_REQUEST_TIME_INTERVAL = 10 * 1000;

    // 获取应用分发虚拟ID管理类
    private AdIdManager mAdIdManager;
    // 缓存广告数据，Key为广告位入口
    private SparseArray<BaseCacheAdBean> mAdCachesHasMap = new SparseArray<>();
    // 定义重复请求的时间间隔
    private SparseArray<Long> mAdFastRequestTime = new SparseArray<>();
    // 记录对应广告的上次广告请求时间
    private SparseArray<Long> mAdLastTime = new SparseArray<>();

    public AdDataManager() {
        mAdIdManager = new AdIdManager();
    }

    /**
     * @param entrance entrance
     * @param adNum number
     * @param context context
     * @param cache 是否从缓存中读取
     */
    public void load(Context context, int entrance, int adNum, MoPubAdConfig moPubAdConfig, boolean cache) {
        // 快速请求则废弃本次请求要求
        if (isFastAdRequest(entrance)) {
            if (cache) {
                if (mAdCachesHasMap.get(entrance) != null && mAdCachesHasMap.get(entrance).getEffectiveAdCounts() > 0) {
                    postAdEvent(entrance, mAdCachesHasMap.get(entrance).getAdModuleInfoBean(), mAdCachesHasMap.get(entrance).filterNeedBeans());
                }
            }
        } else {
            if (cache) {
                if (mAdCachesHasMap.get(entrance) != null && mAdCachesHasMap.get(entrance).getEffectiveAdCounts() > 0) {
                    postAdEvent(entrance, mAdCachesHasMap.get(entrance).getAdModuleInfoBean(), mAdCachesHasMap.get(entrance).filterNeedBeans());
                } else {
                    // 直接去请求广告数据
                    int id = mAdIdManager.getAdId(entrance);
                    if (id != AdIdManager.WORNG_ID) {
                        startLoadAdsFromAppSDK(context, id, adNum, entrance, true, moPubAdConfig, null);
                    }
                }
            } else {
                int id = mAdIdManager.getAdId(entrance);
                if (id != AdIdManager.WORNG_ID) {
                    startLoadAdsFromAppSDK(context, id, adNum, entrance, false, moPubAdConfig, null);
                }
            }
        }
    }

    public boolean hasAd(int entrance) {
        // 缓存中是否有广告
        boolean result = false;
        if (mAdCachesHasMap.get(entrance) != null && mAdCachesHasMap.get(entrance).getEffectiveAdCounts() > 0) {
            result = true;
        }
        return result;
    }

    public void getAdFromCache(int entrance) {
        // 从缓存中读取广告
        if (mAdCachesHasMap.get(entrance) != null && mAdCachesHasMap.get(entrance).getEffectiveAdCounts() > 0) {
            postAdEvent(entrance, mAdCachesHasMap.get(entrance).getAdModuleInfoBean(), mAdCachesHasMap.get(entrance).filterNeedBeans());
        }
    }

    public void removeAd(int entrance) {
        // 不需要缓存则在缓存队列中移除广告数据
        BaseCacheAdBean cacheAdBean = mAdCachesHasMap.get(entrance);
        if (cacheAdBean != null) {
            mAdCachesHasMap.remove(entrance);
        }
    }

    /**
     * 判断是否为快速请求
     *
     * @param entrance entrance
     * @return result
     */
    private boolean isFastAdRequest(int entrance) {
        boolean isFast = true;
        long fastRequestTime;

        if (mAdFastRequestTime.get(entrance) == null) {
            // 没有获取到定义的快速请求的时间间隔，则获取默认的快速请求时间为3s
            fastRequestTime = FAST_REQUEST_TIME_INTERVAL;
        } else {
            fastRequestTime = mAdFastRequestTime.get(entrance);
        }

        if (mAdLastTime.get(entrance) == null) {
            // 没有上次请求时间
            isFast = false;
        } else {
            if ((System.currentTimeMillis() - mAdLastTime.get(entrance)) >= fastRequestTime) {
                isFast = false;
            }
        }

        // 这里要发个空event,回复请求标识
        if (isFast) {
            postAdEvent(entrance, null, null);
        }
        return isFast;
    }

    /**
     * 获取广告数据接口，提供过滤广告类型功能
     *
     * @param id        虚拟ID
     * @param adsNums   广告数目
     * @param entrance  广告入口ID
     * @param context   context不能为空
     * @param filter    广告类型过滤
     */
    private void startLoadAdsFromAppSDK(final Context context,
                                        final int id,
                                        final int adsNums,
                                        final int entrance,
                                        final boolean cache,
                                        final MoPubAdConfig moPubAdConfig,
                                        int... filter) {

        if (context == null) {
            return;
        }

        Logger.i(LOGGER_TAG, "entrance:" + entrance + "----->startLoadAdsFromAppSDK(" + id + "," + adsNums + "," + entrance + "," + "context" + "," + "isRefresh" + "," + "filter" + ")");
        boolean isNeedDownloadIcon = false;
        boolean isNeedDownloadBanner = false;
        boolean isPreResolveBeforeShow = false;
        boolean isRequestData = Logger.DEBUG;
        String tabCategory = null;
        boolean needShownFilter = true;

        Integer[] filterAdSourceArray;
        if (filter != null && filter.length > 0) {
            filterAdSourceArray = new Integer[filter.length];
            for (int i = 0; i < filter.length; i++) {
                filterAdSourceArray[i] = filter[i];
            }
        }

        // 修改
        int installedDays = getInstalledDays();

        AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener = new AdSdkManager.ILoadAdvertDataListener() {
            @Override
            public void onAdShowed(Object arg0) {
                // Nothing to do
            }

            @Override
            public void onAdInfoFinish(final boolean arg1,
                                       final AdModuleInfoBean arg0) {
                Logger.e(LOGGER_TAG, "onAdInfoFinish");
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onAdInfoFinishTemp(arg1, arg0, entrance, cache);
                    }
                });
            }

            @Override
            public void onAdImageFinish(AdModuleInfoBean arg0) {
                // Nothing to do
            }

            @Override
            public void onAdFail(int arg0) {
                final int code = arg0;
                Logger.e(LOGGER_TAG, "onAdInfoFinish " + arg0);
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onAdInfoFailTemp(code, entrance);
                    }
                });
            }

            @Override
            public void onAdClosed(Object arg0) {
                onAdClosedTemp(entrance, arg0);
            }

            @Override
            public void onAdClicked(Object arg0) {
                onAdClickedTemp(entrance, arg0);
            }
        };

        String buyChannel = BuyUserManager.getInstance().getBuyUserChannel();
        AdmobAdConfig admobAdConfig = new AdmobAdConfig(com.google.android.gms.ads.AdSize.BANNER);
        admobAdConfig.contentUrl(Const.ADMOB_AD_CONTENT_URL);
        final AdSdkParamsBuilder.Builder builder = new AdSdkParamsBuilder.Builder(
                context, id, buyChannel, null, tabCategory, loadAdvertDataListener);
        builder.isPreResolveBeforeShow(isPreResolveBeforeShow);
        builder.isNeedDownloadBanner(isNeedDownloadBanner);
        builder.isNeedDownloadIcon(isNeedDownloadIcon);
//      builder.isNeedPreResolve(isNeedPreResolve); // 这里缺省就可以了,设置成true,有bug: gomo源105的统计跟302的统计，点击差距非常非常之大，302有非常大的点击量， 请协助一起排查一下是否执行了预点击
        builder.isPreResolveBeforeShow(isPreResolveBeforeShow);
        builder.isRequestData(isRequestData);
        builder.returnAdCount(adsNums);
        builder.needShownFilter(needShownFilter);
        builder.cdays(installedDays);
        builder.admobAdConfig(admobAdConfig);
        builder.adControlInterceptor(new AdSdkManager.IAdControlInterceptor() {
            @Override
            public boolean isLoadAd(BaseModuleDataItemBean mainModuleDataItemBean) {
                if(AdModuleInfoBean.isFaceBookAd(mainModuleDataItemBean)) {
                    Logger.d("LOGGER_TAG", "adControlInterceptor isLoadAd " + AdSdkApi.isNoad(context));
                    return !AdSdkApi.isNoad(context);
                } else {
                    return true;
                }
            }
        });

        builder.moPubAdConfig(moPubAdConfig);
        AdSdkApi.loadAdBean(builder.build());

        // 记录请求广告的时间
        mAdLastTime.put(entrance, System.currentTimeMillis());
    }

    /**
     * 获取用户安装的时间
     *
     * @return days
     */
    private int getInstalledDays() {
        long installTime = AppUtils.getAppFirstInstallTime(TheApplication.getAppContext(), Const.PACKAGE_NAME);
        if (installTime == 0) {
            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
            installTime = sharedPreferencesManager.getLong(IPreferencesIds.KEY_FIRST_LAUNCH_TIME, 0);
        }

        if (installTime == 0) {
            return 99;
        }

        return (int) ((System.currentTimeMillis() - installTime) / (AlarmManager.INTERVAL_DAY));
    }

    private void onAdInfoFailTemp(int arg0, int entrance) {
        onAdFail(arg0, entrance);
        postAdEvent(entrance, null, null);
    }

    private void onAdInfoFinishTemp(boolean arg1, AdModuleInfoBean arg0, final int entrance, boolean cache) {
        ArrayList<AdWrapper> adBeanWrappers = parseAdDataFromAppSDK(arg0);
        if (adBeanWrappers == null || adBeanWrappers.size() <= 0) {
            return;
        }

        /*
         * 缓存广告数据
         */
        CommonCacheAdBean commonCacheAdBean = new CommonCacheAdBean(cache, 60 * 60 * 1000);
        commonCacheAdBean.setAdModuleInfoBean(arg0);
        commonCacheAdBean.setCacheAdWrappers(adBeanWrappers);
        mAdCachesHasMap.put(entrance, commonCacheAdBean);

        BaseCacheAdBean baseCacheAdBean = mAdCachesHasMap.get(entrance);
        postAdEvent(entrance, baseCacheAdBean.getAdModuleInfoBean(), baseCacheAdBean.filterNeedBeans());
    }

    private void onAdFail(int arg0, int entrance) {
        /*
         * //======================请求广告数据状态码====================
         * //请求成功:16 //网络错误：17 //请求错误：18 //模块下线：19 //获取广告控制信息列表为空:20
         * //获取广告信息列表为空:21 //客户端取消继续加载广告:22
         */
        String wrongMessage = "";
        switch (arg0) {
            case 17:
                wrongMessage = "网络错误";
                break;
            case 18:
                wrongMessage = "请求错误";
                break;
            case 19:
                wrongMessage = "模块下线";
                break;
            case 20:
                wrongMessage = "获取广告控制信息列表为空";
                break;
            case 21:
                wrongMessage = "获取广告信息列表为空";
                break;
            case 22:
                wrongMessage = "客户端取消继续加载广告";
                break;

            default:
                break;
        }
        Logger.i(LOGGER_TAG, "entrance:" + entrance + "----->"
                + "loadAdFail" + wrongMessage);
    }

    private void onAdClosedTemp(int entrance, Object arg0) {
        if (arg0 instanceof NativeAd) {
            Logger.i(LOGGER_TAG, "被关闭的是FBNativeAd");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_FB_NATIVE, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.facebook.ads.InterstitialAd) {
            Logger.i(LOGGER_TAG, "被关闭的是Facebook InterstitialAd");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_FACEBOOK_INTERSTITIAL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.formats.NativeContentAd) {
            Logger.i(LOGGER_TAG, "被关闭的是NativeContentAd");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_ADMOB_NATIVE_CONTENT, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.formats.NativeAppInstallAd) {
            Logger.i(LOGGER_TAG, "被关闭的是NativeAppInstallAd");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_ADMOB_NATIVE_INSTALL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.InterstitialAd) {
            Logger.i(LOGGER_TAG, "被关闭的是Google InterstitialAd");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_ADMOB_INTERSTITIAL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.mopub.mobileads.MoPubView) {
            Logger.i(LOGGER_TAG, "被关闭的是MoPub(Twitter) Banner Ad");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_MOPUB_BANNER, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.mopub.nativeads.NativeAd) {
            Logger.i(LOGGER_TAG, "被关闭的是MoPub(Twitter) Native Ad");
            EventBus.getDefault().post(new OnAdCloseEvent(AdType.TYPE_MOPUB_NATIVE, entrance, arg0.hashCode()));
        }
    }

    private void onAdClickedTemp(int entrance, Object arg0) {

        if (arg0 instanceof NativeAd) {
            Logger.i(LOGGER_TAG, "被点击的是FBNativeAd");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_FB_NATIVE, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.facebook.ads.InterstitialAd) {
            Logger.i(LOGGER_TAG, "被点击的是Facebook InterstitialAd");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_FACEBOOK_INTERSTITIAL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.formats.NativeContentAd) {
            Logger.i(LOGGER_TAG, "被点击的是NativeContentAd");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_ADMOB_NATIVE_CONTENT, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.formats.NativeAppInstallAd) {
            Logger.i(LOGGER_TAG, "被点击的是NativeAppInstallAd");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_ADMOB_NATIVE_INSTALL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.google.android.gms.ads.InterstitialAd) {
            Logger.i(LOGGER_TAG, "被点击的是Google InterstitialAd");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_ADMOB_INTERSTITIAL, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.mopub.mobileads.MoPubView) {
            Logger.i(LOGGER_TAG, "被点击的是MoPub(Twitter) Banner Ad");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_MOPUB_BANNER, entrance, arg0.hashCode()));
        } else if (arg0 instanceof com.mopub.nativeads.NativeAd) {
            Logger.i(LOGGER_TAG, "被点击的是MoPub(Twitter) Native Ad");
            EventBus.getDefault().post(new OnAdClickEvent(AdType.TYPE_MOPUB_NATIVE, entrance, arg0.hashCode()));
        }
    }

    /**
     * 解析应用分发SDK返回的广告数据
     *
     * @param adModuleInfoBean bean
     * @return result
     */
    private ArrayList<AdWrapper> parseAdDataFromAppSDK(AdModuleInfoBean adModuleInfoBean) {

        ArrayList<AdWrapper> adBeanWrappers = new ArrayList<>();
        if (adModuleInfoBean == null) {
            return null;
        }
        List<AdInfoBean> appAds = adModuleInfoBean.getAdInfoList();
        if (appAds != null) {
            if (appAds.size() > 0) {
                Logger.i(LOGGER_TAG, "本次成功获得离线广告");
                AdWrapper adBeanWrapper;
                for (int i = 0; i < appAds.size(); i++) {
                    adBeanWrapper = new AdWrapper();
                    adBeanWrapper.mAppAdInfoBean = appAds.get(i);
                    adBeanWrappers.add(adBeanWrapper);
                }
            }
        }

        // 获取其他SDK广告数据，包括FB广告数据,LoopMe广告
        SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean = adModuleInfoBean.getSdkAdSourceAdInfoBean();
        if (sdkAdSourceAdInfoBean != null) {
            ArrayList<SdkAdSourceAdWrapper> beanssForSdk = (ArrayList<SdkAdSourceAdWrapper>) sdkAdSourceAdInfoBean
                    .getAdViewList();
            if (beanssForSdk != null) {
                if (beanssForSdk.size() > 0) {
                    Logger.i(LOGGER_TAG, "获取其他SDK广告数据,数量：" + beanssForSdk.size());
                    AdWrapper adBeanWrapper;
                    Object objectAd = beanssForSdk.get(0).getAdObject();
                    /*
                     *
                     * 在这里进行SDK广告数据的区分
                     *
                     */
                    // Facebook广告数据
                    if (objectAd instanceof NativeAd) {
                        Logger.i(LOGGER_TAG, "本次成功获得Facebook Native广告,广告数量：" + beanssForSdk.size());
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mFbNativeAd = (NativeAd) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    } else if (objectAd instanceof com.facebook.ads.InterstitialAd) {
                        // facebook 全屏广告
                        Logger.i(LOGGER_TAG, "本次成功获得com.facebook.ads.InterstitialAd广告");
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mFbInterstitialAd = (com.facebook.ads.InterstitialAd) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    } else if (objectAd instanceof LoopMeBanner) {
                        // LoopMeBanner广告数据
                        Logger.i(LOGGER_TAG, "本次成功获得LoopMeBanner广告");
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mLoopMeBanner = (LoopMeBanner) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    } else if (objectAd instanceof com.google.android.gms.ads.formats.NativeAd) {
                        // admob(google) Native广告
                        if (objectAd instanceof com.google.android.gms.ads.formats.NativeContentAd) {
                            // admob(google) Native 内容广告
                            Logger.i(LOGGER_TAG, "本次成功获得com.google.android.gms.ads.formats.NativeContentAd广告");
                            for (int i = 0; i < beanssForSdk.size(); i++) {
                                adBeanWrapper = new AdWrapper();
                                adBeanWrapper.mAdmobNativeContentAd = (com.google.android.gms.ads.formats.NativeContentAd) beanssForSdk.get(i).getAdObject();
                                adBeanWrappers.add(adBeanWrapper);
                            }
                        } else if (objectAd instanceof com.google.android.gms.ads.formats.NativeAppInstallAd) {
                            // admob(google) Native 应用安装广告
                            Logger.i(LOGGER_TAG, "本次成功获得com.google.android.gms.ads.formats.NativeAppInstallAd广告");
                            for (int i = 0; i < beanssForSdk.size(); i++) {
                                adBeanWrapper = new AdWrapper();
                                adBeanWrapper.mAdmobNativeAppInstallAd = (com.google.android.gms.ads.formats.NativeAppInstallAd) beanssForSdk.get(i).getAdObject();
                                adBeanWrappers.add(adBeanWrapper);
                            }
                        }
                    } else if (objectAd instanceof com.google.android.gms.ads.InterstitialAd) {
                        // admob(google) 全屏广告
                        Logger.i(LOGGER_TAG, "本次成功获得com.google.android.gms.ads.InterstitialAd广告");
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mAdmobInterstitialAd = (com.google.android.gms.ads.InterstitialAd) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    } else if (objectAd instanceof MoPubView) {
                        // MoPub(twitter) banner 广告
                        Logger.i(LOGGER_TAG, "本次成功获得Twitter Banner广告");
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mMoPubView = (MoPubView) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    } else if (objectAd instanceof com.mopub.nativeads.NativeAd) {
                        Logger.i(LOGGER_TAG, "本次成功获得Twitter Native广告");
                        for (int i = 0; i < beanssForSdk.size(); i++) {
                            adBeanWrapper = new AdWrapper();
                            adBeanWrapper.mMoPubNative = (com.mopub.nativeads.NativeAd) beanssForSdk.get(i).getAdObject();
                            adBeanWrappers.add(adBeanWrapper);
                        }
                    }
                }
            }
        }
        return adBeanWrappers;
    }

    /**
     * 发送广告数据Event
     *
     * @param entrance entrance
     * @param adModuleInfoBean bean
     * @param adWrappers ad wrapper
     */
    private void postAdEvent(int entrance, AdModuleInfoBean adModuleInfoBean, ArrayList<AdWrapper> adWrappers) {
        OneRequestAds oneRequestAds = new OneRequestAds(entrance, adWrappers);
        oneRequestAds.setAdModuleInfoBean(adModuleInfoBean);
        Logger.i(LOGGER_TAG, "entrance:" + entrance + "----->" + "post NormalAdLoadCompleteEvent");
        EventBus.getDefault().post(new NormalAdLoadCompleteEvent(oneRequestAds));

        // 不需要缓存则在缓存队列中移除广告数据
        BaseCacheAdBean cacheAdBean = mAdCachesHasMap.get(entrance);
        if (cacheAdBean != null && !cacheAdBean.isNeedCache()) {
            mAdCachesHasMap.remove(entrance);
            Logger.i(LOGGER_TAG, "移除入口值entrance:" + entrance);
        }
    }

}

package com.jiubang.commerce.ad.cache;

import android.content.Context;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.cache.config.CacheAdConfig;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.sdk.AdmobAdConfig;
import com.jiubang.commerce.ad.sdk.FacebookAdConfig;

public class LoadAdTask implements Runnable, AdSdkManager.ILoadAdvertDataListener {
    private AdCacheBean mAdCacheBean;
    private AdSdkParamsBuilder mAdSdkParams;
    private CachePool mCachePool;
    private Context mContext;
    private int[] mFilterTags;
    private ILoadTask mILoadTask;

    interface IAdCallBackReapter {
        void onAdClicked(Object obj);

        void onAdClosed(Object obj);

        void onAdShowed(Object obj);
    }

    interface ILoadTask {
        void onTaskComplete(LoadAdTask loadAdTask);
    }

    public LoadAdTask(Context context, int[] filterTags, CachePool pool, CacheAdConfig adConfig, ILoadTask itask) {
        FacebookAdConfig fac;
        AdmobAdConfig aac;
        this.mContext = context;
        this.mCachePool = pool;
        this.mFilterTags = filterTags;
        this.mILoadTask = itask;
        if (adConfig != null) {
            fac = adConfig.getFacebookAdConfig();
        } else {
            fac = null;
        }
        if (adConfig != null) {
            aac = adConfig.getAdmobAdConfig();
        } else {
            aac = null;
        }
        AdSdkParamsBuilder.Builder builder = new AdSdkParamsBuilder.Builder(this.mContext, getVMID(), (String) null, this);
        builder.filterAdCacheTags(this.mFilterTags).facebookAdConfig(fac).admobAdConfig(aac).fbTimeout(5000);
        this.mAdSdkParams = builder.build();
    }

    public void run() {
        if (this.mAdCacheBean == null) {
            CachePool.printLog("loadAdTask start");
            AdSdkApi.loadAdBean(this.mAdSdkParams);
        }
    }

    public void onAdInfoFinish(boolean isCache, AdModuleInfoBean adModuleInfoBean) {
        if (2 == adModuleInfoBean.getAdType()) {
            CachePool.printLog("loadAdTask end:success");
            this.mAdCacheBean = new AdCacheBean(adModuleInfoBean);
            this.mCachePool.appendAd2Cache(this.mAdCacheBean);
        } else {
            CachePool.printLog("loadAdTask end:no need ad");
        }
        this.mILoadTask.onTaskComplete(this);
    }

    public void onAdImageFinish(AdModuleInfoBean adModuleInfoBean) {
    }

    public void onAdFail(int statusCode) {
        CachePool.printLog("loadAdTask end:fail");
        this.mILoadTask.onTaskComplete(this);
    }

    public void onAdShowed(Object adViewObj) {
        this.mAdCacheBean.onAdShowed(adViewObj);
    }

    public void onAdClicked(Object adViewObj) {
        this.mAdCacheBean.onAdClicked(adViewObj);
    }

    public void onAdClosed(Object adViewObj) {
        this.mAdCacheBean.onAdClosed(adViewObj);
    }

    public boolean isSuccess() {
        return this.mAdCacheBean != null;
    }

    private int getVMID() {
        return this.mCachePool.getCacheConfig().getVMID();
    }
}

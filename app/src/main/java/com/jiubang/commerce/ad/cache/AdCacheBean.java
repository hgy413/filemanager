package com.jiubang.commerce.ad.cache;

import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.cache.LoadAdTask;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;

public class AdCacheBean implements LoadAdTask.IAdCallBackReapter {
    static final long LIFE = 86400000;
    private AdModuleInfoBean mAdBean;
    private long mBirth = System.currentTimeMillis();
    private AdSdkParamsBuilder mClientAdSdkParams;

    public AdCacheBean(AdModuleInfoBean ad) {
        this.mAdBean = ad;
    }

    public AdModuleInfoBean getAdBean() {
        return this.mAdBean;
    }

    public void setAdBean(AdModuleInfoBean mAdBean2) {
        this.mAdBean = mAdBean2;
    }

    public int getTag() {
        return this.mAdBean.getModuleDataItemBean().getAdCacheFlag();
    }

    public boolean isValid() {
        return System.currentTimeMillis() - this.mBirth < 86400000;
    }

    public void setClientAdSdkParams(AdSdkParamsBuilder b) {
        this.mClientAdSdkParams = b;
    }

    public void destroy() {
        BaseModuleDataItemBean moduleDataItemBean = this.mAdBean.getModuleDataItemBean();
        if (moduleDataItemBean.isSdkOnlineAdType()) {
            for (SdkAdSourceAdWrapper w : this.mAdBean.getSdkAdSourceAdInfoBean().getAdViewList()) {
                Object adObject = w.getAdObject();
                if (AdModuleInfoBean.isFaceBookAd(moduleDataItemBean)) {
                    if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean)) {
                        ((AdView) adObject).destroy();
                    } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                        ((InterstitialAd) adObject).destroy();
                    }
                } else if (AdModuleInfoBean.isAdMobAd(moduleDataItemBean)) {
                    if (BaseModuleDataItemBean.isBannerAd(moduleDataItemBean) || BaseModuleDataItemBean.isBannerAd300_250(moduleDataItemBean)) {
                        ((com.google.android.gms.ads.AdView) adObject).destroy();
                    } else if (BaseModuleDataItemBean.isInterstitialAd(moduleDataItemBean)) {
                    }
                }
            }
        }
        this.mAdBean = null;
        this.mClientAdSdkParams = null;
    }

    public void onAdShowed(Object ad) {
        if (this.mClientAdSdkParams != null) {
            this.mClientAdSdkParams.mLoadAdvertDataListener.onAdShowed(ad);
        }
    }

    public void onAdClicked(Object ad) {
        if (this.mClientAdSdkParams != null) {
            this.mClientAdSdkParams.mLoadAdvertDataListener.onAdClicked(ad);
        }
    }

    public void onAdClosed(Object ad) {
        if (this.mClientAdSdkParams != null) {
            this.mClientAdSdkParams.mLoadAdvertDataListener.onAdClosed(ad);
        }
    }
}

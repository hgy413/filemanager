package com.jiubang.commerce.ad.sdk;

import com.google.android.gms.ads.AdSize;

public class AdmobAdConfig {
    public AdSize mBannerSize;
    public String mContentUrl = null;
    public boolean mReturnUrlsForImageAssets = false;
    public boolean mUseNativeAdExpress = false;

    public AdmobAdConfig(AdSize adSize) {
        this.mBannerSize = adSize;
    }

    public void setReturnUrlsForImageAssets(boolean b) {
        this.mReturnUrlsForImageAssets = b;
    }

    public void setUseNativeAdExpress(boolean b) {
        this.mUseNativeAdExpress = b;
    }

    public AdmobAdConfig contentUrl(String url) {
        this.mContentUrl = url;
        return this;
    }
}

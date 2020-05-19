package com.jiubang.commerce.ad.cache.config;

import com.jiubang.commerce.ad.sdk.AdmobAdConfig;
import com.jiubang.commerce.ad.sdk.FacebookAdConfig;

public class CacheAdConfig {
    private AdmobAdConfig mAdmobAdConfig;
    private FacebookAdConfig mFacebookAdConfig;

    public CacheAdConfig facebookAdConfig(FacebookAdConfig config) {
        this.mFacebookAdConfig = config;
        return this;
    }

    public CacheAdConfig admobAdConfig(AdmobAdConfig config) {
        this.mAdmobAdConfig = config;
        return this;
    }

    public FacebookAdConfig getFacebookAdConfig() {
        return this.mFacebookAdConfig;
    }

    public AdmobAdConfig getAdmobAdConfig() {
        return this.mAdmobAdConfig;
    }
}

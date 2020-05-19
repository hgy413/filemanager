package com.jiubang.commerce.ad.sdk;

public class MoPubAdConfig extends AbsAdConfig {
    public String mKeyWords;
    public MoPubNativeConfig mMoPubNativeConfig;

    public MoPubAdConfig moPubNativeConfig(MoPubNativeConfig config) {
        this.mMoPubNativeConfig = config;
        return this;
    }
}

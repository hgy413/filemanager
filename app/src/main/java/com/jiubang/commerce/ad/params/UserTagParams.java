package com.jiubang.commerce.ad.params;

public class UserTagParams {
    public final String mAccessKey;
    public final String mChannel;
    public final String mGoId;
    public final String mGoogleId;
    public final String mProductKey;

    public UserTagParams(String goId, String googleId, String channel, String productKey, String accessKey) {
        this.mGoId = goId;
        this.mGoogleId = googleId;
        this.mChannel = channel;
        this.mProductKey = productKey;
        this.mAccessKey = accessKey;
    }
}

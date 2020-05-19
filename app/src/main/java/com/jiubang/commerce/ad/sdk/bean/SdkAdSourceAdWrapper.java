package com.jiubang.commerce.ad.sdk.bean;

public class SdkAdSourceAdWrapper {
    private Object mAdObject;
    private String mAppKey;

    public SdkAdSourceAdWrapper(String appKey, Object adObject) {
        this.mAppKey = appKey;
        this.mAdObject = adObject;
    }

    public Object getAdObject() {
        return this.mAdObject;
    }

    public void setAdObject(Object mAdObject2) {
        this.mAdObject = mAdObject2;
    }

    public String getAppKey() {
        return this.mAppKey;
    }

    public void setAppKey(String mAppKey2) {
        this.mAppKey = mAppKey2;
    }
}

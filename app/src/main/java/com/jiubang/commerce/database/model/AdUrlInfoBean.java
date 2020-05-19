package com.jiubang.commerce.database.model;

public class AdUrlInfoBean {
    private String mAdUrl;
    private String mPackageName;
    private String mRedirectUrl;
    private long mUpdateTime;

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String getRedirectUrl() {
        return this.mRedirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.mRedirectUrl = redirectUrl;
    }

    public String getAdUrl() {
        return this.mAdUrl;
    }

    public void setAdUrl(String adUrl) {
        this.mAdUrl = adUrl;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }
}

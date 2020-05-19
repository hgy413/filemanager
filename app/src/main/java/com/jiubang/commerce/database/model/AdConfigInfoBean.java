package com.jiubang.commerce.database.model;

public class AdConfigInfoBean {
    private String mConfigKey;
    private String mConfigValue;
    private long mUpdateTime;

    public AdConfigInfoBean() {
    }

    public AdConfigInfoBean(String configKey, String configValue) {
        this.mConfigKey = configKey;
        this.mConfigValue = configValue;
        this.mUpdateTime = System.currentTimeMillis();
    }

    public String getConfigKey() {
        return this.mConfigKey;
    }

    public void setConfigKey(String configKey) {
        this.mConfigKey = configKey;
    }

    public String getConfigValue() {
        return this.mConfigValue;
    }

    public void setConfigValue(String configValue) {
        this.mConfigValue = configValue;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }
}

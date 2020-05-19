package com.jiubang.commerce.database.model;

public class InstalledPkgBean {
    private String mPackageName;
    private long mUpdateTime;

    public InstalledPkgBean() {
    }

    public InstalledPkgBean(String packageName, long updateTime) {
        this.mPackageName = packageName;
        this.mUpdateTime = updateTime;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }
}

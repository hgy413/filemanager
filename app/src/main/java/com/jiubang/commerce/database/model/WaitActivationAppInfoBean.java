package com.jiubang.commerce.database.model;

public class WaitActivationAppInfoBean {
    private Long mInstallTime;
    private String mPackageName;

    public WaitActivationAppInfoBean() {
    }

    public WaitActivationAppInfoBean(String packageName, long installTime) {
        this.mPackageName = packageName;
        this.mInstallTime = Long.valueOf(installTime);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public Long getInstallTime() {
        return this.mInstallTime;
    }

    public void setInstallTime(Long installTime) {
        this.mInstallTime = installTime;
    }
}

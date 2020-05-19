package com.jiubang.commerce.database.model;

public class AdShowClickBean {
    public static final String OPT_CLICK = "click";
    public static final String OPT_SHOW = "show";
    private String mOpt;
    private long mUpdateTime;
    private int mVMID;

    public int getVMID() {
        return this.mVMID;
    }

    public void setVMID(int vmid) {
        this.mVMID = vmid;
    }

    public String getOpt() {
        return this.mOpt;
    }

    public void setOpt(String opt) {
        this.mOpt = opt;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.mUpdateTime = updateTime;
    }
}

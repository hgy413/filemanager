package com.jiubang.commerce.database.model;

public class AdvertFilterBean {
    private String mAdvertPos;
    private String mMoudleId;
    private String mPackageName;
    private long mSaveTime;
    private int mShowCount;

    public String getmPackageName() {
        return this.mPackageName;
    }

    public void setmPackageName(String mPackageName2) {
        this.mPackageName = mPackageName2;
    }

    public String getmMoudleId() {
        return this.mMoudleId;
    }

    public void setmMoudleId(String mMoudleId2) {
        this.mMoudleId = mMoudleId2;
    }

    public String getmAdvertPos() {
        return this.mAdvertPos;
    }

    public void setmAdvertPos(String mAdvertPos2) {
        this.mAdvertPos = mAdvertPos2;
    }

    public int getmShowCount() {
        return this.mShowCount;
    }

    public void setmShowCount(int mShowCount2) {
        this.mShowCount = mShowCount2;
    }

    public long getmSaveTime() {
        return this.mSaveTime;
    }

    public void setmSaveTime(long mSaveTime2) {
        this.mSaveTime = mSaveTime2;
    }
}

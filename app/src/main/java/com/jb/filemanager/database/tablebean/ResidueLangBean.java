package com.jb.filemanager.database.tablebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class ResidueLangBean {
    String mPathId;
    String mPkgName;
    String mLangCode;
    String mAppName;

    public String getmPathId() {
        return mPathId;
    }

    public void setPathId(String mPathId) {
        this.mPathId = mPathId;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public String getLangCode() {
        return mLangCode;
    }

    public void setLangCode(String mLangCode) {
        this.mLangCode = mLangCode;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    ;

    @Override
    public String toString() {
        return "CleanResidueLangBean [mPathId=" + mPathId + ", mPkgName="
                + mPkgName + ", mLangCode=" + mLangCode + ", mAppName="
                + mAppName + "]";
    }
}

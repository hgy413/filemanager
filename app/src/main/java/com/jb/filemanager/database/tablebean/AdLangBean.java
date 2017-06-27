package com.jb.filemanager.database.tablebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class AdLangBean {
    String mAdId;
    String mLangCode;
    String mTitle;

    public String getAdId() {
        return mAdId;
    }

    public void setAdId(String adId) {
        this.mAdId = adId;
    }

    public String getLangCode() {
        return mLangCode;
    }

    public void setLangCode(String mLangCode) {
        this.mLangCode = mLangCode;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public String toString() {
        return "CleanAdLangBean [mAdId=" + mAdId + ", mLangCode=" + mLangCode
                + ", mTitle=" + mTitle + "]";
    };
}

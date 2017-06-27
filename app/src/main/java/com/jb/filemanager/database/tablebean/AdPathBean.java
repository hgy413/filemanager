package com.jb.filemanager.database.tablebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class AdPathBean {
    String mAdId;
    String mPath;

    public String getAdId() {
        return mAdId;
    }

    public void setAdId(String adId) {
        this.mAdId = adId;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    @Override
    public String toString() {
        return "CleanAdPathBean [mAdId=" + mAdId + ", mPath=" + mPath + "]";
    }
}

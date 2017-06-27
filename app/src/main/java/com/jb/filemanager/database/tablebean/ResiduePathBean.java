package com.jb.filemanager.database.tablebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class ResiduePathBean {
    String mPathId;
    String mPkgName;
    String mPath;

    public String getPathId() {
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

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    @Override
    public String toString() {
        return "CleanResiduePathBean [mPathId=" + mPathId + ", mPkgName="
                + mPkgName + ", mPath=" + mPath + "]";
    }
}

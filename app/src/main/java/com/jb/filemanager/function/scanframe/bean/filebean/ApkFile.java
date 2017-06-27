package com.jb.filemanager.function.scanframe.bean.filebean;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class ApkFile {
    private String mPackageString;
    private String mVersionNameString;
    private int mVersionCode;

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        mVersionCode = versionCode;
    }

    public String getPackage() {
        return mPackageString;
    }

    public void setPackage(String packageString) {
        mPackageString = packageString;
    }

    public String getVersionName() {
        return mVersionNameString;
    }

    public void setVersionName(String versionNameString) {
        mVersionNameString = versionNameString;
    }
}

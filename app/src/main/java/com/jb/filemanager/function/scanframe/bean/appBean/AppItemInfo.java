package com.jb.filemanager.function.scanframe.bean.appBean;

/**
 * Created by xiaoyu on 2016/10/20.<br>
 * 所有App信息均使用此bean类
 */

public class AppItemInfo implements Cloneable, Sizeable {

    /**
     * 是否在运行，默认为false
     */
    public boolean mIsRunning = false;

    /**
     * 是否被disable，默认为false
     */
    public boolean mIsEnable = false;

    /**
     * 缓存大小
     */
    public long mAppCacheSize;

    /**
     * 数据大小
     */
    public long mAppDataSize;

    /**
     * 应用程序大小
     */
    public long mAppCodeSize;
    /**
     * 应用程序名
     */
    public String mAppName;

    /**
     * 应用程序的标签
     */
    public String mAppLabel;

    /**
     * 是否为系统应用程序
     */
    public boolean mIsSysApp;

    /**
     * 应用程序包名
     */
    public String mAppProcessName;

    /**
     * 来自于name标签
     */
    public String mAppPackageName;

    /**
     * 应用程序的Uri
     */
    public String mAppUriString;

    public boolean mIsChecked;

    /**
     * 应用程序的版本号
     */

    public int mVersionCode;

    /**
     * 应用程序的版本名
     */
    public String mVersionName;

    /**
     * 应用程序安装时间,android版本2.3一下不具有本属性
     */
    public long mFirstInstallTime;

    /**
     * 应用程序的上次更新时间,android版本2.3一下不具有本属性
     */
    public long mLastUpdateTime;


    /**
     * 应用程序是否被选中
     */
    public Boolean mIsAppSelected;

    @Override
    public long getSize() {
        return mAppCacheSize + mAppDataSize + mAppCodeSize;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public void setRunning(boolean running) {
        mIsRunning = running;
    }

    public boolean isEnable() {
        return mIsEnable;
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
    }

    public long getAppCacheSize() {
        return mAppCacheSize;
    }

    public void setAppCacheSize(long appCacheSize) {
        mAppCacheSize = appCacheSize;
    }

    public long getAppDataSize() {
        return mAppDataSize;
    }

    public void setAppDataSize(long appDataSize) {
        mAppDataSize = appDataSize;
    }

    public long getAppCodeSize() {
        return mAppCodeSize;
    }

    public void setAppCodeSize(long appCodeSize) {
        mAppCodeSize = appCodeSize;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public String getAppLabel() {
        return mAppLabel;
    }

    public void setAppLabel(String appLabel) {
        mAppLabel = appLabel;
    }

    public boolean getIsSysApp() {
        return mIsSysApp;
    }

    public void setSysApp(boolean sysApp) {
        mIsSysApp = sysApp;
    }

    public String getAppProcessName() {
        return mAppProcessName;
    }

    public void setAppProcessName(String appProcessName) {
        mAppProcessName = appProcessName;
    }

    public String getAppPackageName() {
        return mAppPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        mAppPackageName = appPackageName;
    }

    public String getAppUriString() {
        return mAppUriString;
    }

    public void setAppUriString(String appUriString) {
        mAppUriString = appUriString;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        mVersionCode = versionCode;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        mVersionName = versionName;
    }

    public long getFirstInstallTime() {
        return mFirstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        mFirstInstallTime = firstInstallTime;
    }

    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        mLastUpdateTime = lastUpdateTime;
    }

    public Boolean getAppSelected() {
        return mIsAppSelected;
    }

    public void setAppSelected(Boolean appSelected) {
        mIsAppSelected = appSelected;
    }

    @Override
    public String toString() {
        return "AppItemInfo{" +
                "mIsRunning=" + mIsRunning +
                ", mIsEnable=" + mIsEnable +
                ", mAppCacheSize=" + mAppCacheSize +
                ", mAppDataSize=" + mAppDataSize +
                ", mAppCodeSize=" + mAppCodeSize +
                ", mAppName='" + mAppName + '\'' +
                ", mAppLable='" + mAppLabel + '\'' +
                ", mIsSysApp=" + mIsSysApp +
                ", mAppProcessName='" + mAppProcessName + '\'' +
                ", mAppPackageName='" + mAppPackageName + '\'' +
                ", mAppUriString='" + mAppUriString + '\'' +
                ", mIsChecked=" + mIsChecked +
                ", mVersionCode=" + mVersionCode +
                ", mVersionName='" + mVersionName + '\'' +
                ", mFirstInstallTime=" + mFirstInstallTime +
                ", mLastUpdateTime=" + mLastUpdateTime +
                ", mIsAppSelected=" + mIsAppSelected +
                '}';
    }

    public String toSimpleString() {
        return "AppItemInfo{" +
                ", mAppCacheSize=" + mAppCacheSize +
                ", mAppDataSize=" + mAppDataSize +
                ", mAppCodeSize=" + mAppCodeSize +
                ", mAppName='" + mAppName + '\'' +
                ", mAppLable='" + mAppLabel + '\'' +
                ", mIsSysApp=" + mIsSysApp +
                ", mAppProcessName='" + mAppProcessName + '\'' +
                ", mAppPackageName='" + mAppPackageName + '\'' +
                ", mVersionCode=" + mVersionCode +
                ", mVersionName='" + mVersionName + '\'' +
                '}';
    }
    @Override
    public AppItemInfo clone() {
        AppItemInfo bean = null;
        try {
            bean = (AppItemInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

}

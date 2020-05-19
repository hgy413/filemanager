package com.jiubang.commerce.ad.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdOldUserTagInfoBean;
import com.jiubang.commerce.ad.bean.AdUserTagInfoBean;

public class AdSdkSetting {
    private static final String ADSDK_OLD_USER_TAG_STRING = "ADSDK_OLD_USER_TAG_STRING";
    private static final String ADSDK_OLD_USER_TAG_UPDATE = "ADSDK_OLD_USER_TAG_UPDATE";
    public static final long ADSDK_OLD_USER_TAG_VALIAD_TIME = 28800000;
    private static final String ADSDK_SETTING = "ADSDK_SETTING";
    private static final String ADSDK_USER_TAG_STRING = "ADSDK_USER_TAG_STRING";
    private static final String ADSDK_USER_TAG_UPDATE = "ADSDK_USER_TAG_UPDATE";
    public static final long ADSDK_USER_TAG_VALIAD_TIME = 86400000;
    public static final long BUY_CHANNEL_TYPE_VALIAD_TIME = 86400000;
    private static AdSdkSetting sInstance;
    private Context mContext;
    private long mLastOldUserTagUpdateTime;
    private long mLastUserTagUpdateTime;
    private String mOldUserTagString;
    private SharedPreferences mSharedPreferences;
    private String mUserTagString;

    public AdUserTagInfoBean getUserTagInfoBean() {
        AdUserTagInfoBean userTagInfoBean = new AdUserTagInfoBean();
        userTagInfoBean.setUserTags(this.mUserTagString);
        return userTagInfoBean;
    }

    public AdOldUserTagInfoBean getOldUserTagInfoBean() {
        AdOldUserTagInfoBean adOldUserTagInfoBean = new AdOldUserTagInfoBean();
        adOldUserTagInfoBean.setUserTags(this.mOldUserTagString);
        return adOldUserTagInfoBean;
    }

    public void setUserTag(String mUserTagString2, long lastUpdateTime) {
        setUserTagString(mUserTagString2);
        setLastUserTagUpdateTime(lastUpdateTime);
    }

    public void setOldUserTag(String mOldUserTagString2, long lastUpdateTime) {
        setOldUserTagString(mOldUserTagString2);
        setLastOldUserTagUpdateTime(lastUpdateTime);
    }

    public String getUserTagString() {
        return this.mUserTagString;
    }

    public void setUserTagString(String mUserTagString2) {
        this.mUserTagString = mUserTagString2;
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putString(ADSDK_USER_TAG_STRING, mUserTagString2);
        editor.commit();
    }

    public void setOldUserTagString(String mOldUserTagString2) {
        this.mOldUserTagString = mOldUserTagString2;
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putString(ADSDK_OLD_USER_TAG_STRING, mOldUserTagString2);
        editor.commit();
    }

    public long getLastUserTagUpdateTime() {
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdSdkSetting.getLastUserTagUpdateTime:" + this.mLastUserTagUpdateTime);
        }
        return this.mLastUserTagUpdateTime;
    }

    public long getLastOldUserTagUpdateTime() {
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdSdkSetting.getLastUserTagUpdateTime:" + this.mLastOldUserTagUpdateTime);
        }
        return this.mLastOldUserTagUpdateTime;
    }

    public void setLastOldUserTagUpdateTime(long mLastOldUserTagUpdateTime2) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdSdkSetting.setLastUserTagUpdateTime:" + mLastOldUserTagUpdateTime2);
        }
        this.mLastOldUserTagUpdateTime = mLastOldUserTagUpdateTime2;
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putLong(ADSDK_OLD_USER_TAG_UPDATE, mLastOldUserTagUpdateTime2);
        editor.commit();
    }

    public void setLastUserTagUpdateTime(long mLastUserTagUpdateTime2) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "AdSdkSetting.setLastUserTagUpdateTime:" + mLastUserTagUpdateTime2);
        }
        this.mLastUserTagUpdateTime = mLastUserTagUpdateTime2;
        SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        editor.putLong(ADSDK_USER_TAG_UPDATE, mLastUserTagUpdateTime2);
        editor.commit();
    }

    public static AdSdkSetting getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdSdkSetting.class) {
                if (sInstance == null) {
                    sInstance = new AdSdkSetting(context);
                }
            }
        }
        return sInstance;
    }

    @SuppressLint({"InlinedApi"})
    private AdSdkSetting(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        if (Build.VERSION.SDK_INT >= 11) {
            this.mSharedPreferences = this.mContext.getSharedPreferences(ADSDK_SETTING, 4);
        } else {
            this.mSharedPreferences = this.mContext.getSharedPreferences(ADSDK_SETTING, 0);
        }
        init();
    }

    private void init() {
        this.mLastUserTagUpdateTime = this.mSharedPreferences.getLong(ADSDK_USER_TAG_UPDATE, 0);
        this.mUserTagString = this.mSharedPreferences.getString(ADSDK_USER_TAG_STRING, (String) null);
    }
}

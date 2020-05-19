package com.jiubang.commerce.ad.abtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.io.MultiprocessSharedPreferences;
import com.jiubang.commerce.ad.manager.AdSdkSetting;
import com.jiubang.commerce.utils.StringUtils;

public class CachedAbBean {
    private static final String SP_KEY_STR = "json-";
    private static final String SP_KEY_TIME = "time";
    private AbBean mFbNativeAbBean;
    private AbBean mInstallPreAbBean;
    private AbBean mIntelligentBean;
    private long mUpdateTime;

    public AbBean getFbNativeAbBean() {
        return this.mFbNativeAbBean;
    }

    public AbBean getInstallAbBean() {
        return this.mInstallPreAbBean;
    }

    public AbBean getIntelligentAbBean() {
        return this.mIntelligentBean;
    }

    public long getUpdateTime() {
        return this.mUpdateTime;
    }

    public long getLeftValidDuration() {
        return calculate(this.mUpdateTime);
    }

    public static long getValidDuration() {
        return AdSdkSetting.ADSDK_OLD_USER_TAG_VALIAD_TIME;
    }

    public static boolean checkLocalValid(Context context) {
        return calculate(getSP(context).getLong(SP_KEY_TIME, 0)) > 0;
    }

    public boolean isValid() {
        return getLeftValidDuration() > 0;
    }

    public void refreshAbBeanFromLocal(Context context) {
        SharedPreferences sp = getSP(context);
        this.mFbNativeAbBean = new AbBean(sp.getString("json-91", (String) null));
        this.mInstallPreAbBean = new AbBean(sp.getString("json-130", (String) null));
        this.mIntelligentBean = new AbBean(sp.getString("json-143", (String) null));
        this.mUpdateTime = sp.getLong(SP_KEY_TIME, 0);
    }

    public void update2Local(Context context, String bid, AbBean bean) {
        String jsonStr = (bean == null || !bean.isSuccess()) ? null : bean.getJsonStr();
        if (!TextUtils.isEmpty(jsonStr) && !StringUtils.isEmpty(bid)) {
            if ("91".equals(bid)) {
                this.mFbNativeAbBean = bean;
            } else if ("130".equals(bid)) {
                this.mInstallPreAbBean = bean;
            } else if ("143".equals(bid)) {
                this.mIntelligentBean = bean;
            }
            this.mUpdateTime = System.currentTimeMillis();
            getSP(context).edit().putString(SP_KEY_STR + bid, jsonStr).putLong(SP_KEY_TIME, this.mUpdateTime).commit();
        }
    }

    private static long calculate(long updateTime) {
        long elapsTime = Math.abs(System.currentTimeMillis() - updateTime);
        if (elapsTime < getValidDuration()) {
            return getValidDuration() - elapsTime;
        }
        return 0;
    }

    private static SharedPreferences getSP(Context context) {
        return MultiprocessSharedPreferences.getSharedPreferences(context, "adsdk_abtest_config", 0);
    }
}

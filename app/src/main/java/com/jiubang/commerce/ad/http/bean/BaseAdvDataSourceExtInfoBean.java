package com.jiubang.commerce.ad.http.bean;

import org.json.JSONObject;

public class BaseAdvDataSourceExtInfoBean {
    private int mPreloadPerDay;

    public void setPreloadPerDay(int n) {
        this.mPreloadPerDay = n;
    }

    public int getPreloadPerDay() {
        return this.mPreloadPerDay;
    }

    public static BaseAdvDataSourceExtInfoBean parseJsonObject(JSONObject json) {
        if (json == null) {
            return null;
        }
        BaseAdvDataSourceExtInfoBean bean = new BaseAdvDataSourceExtInfoBean();
        bean.mPreloadPerDay = json.optInt("preloadperday", -1);
        return bean;
    }
}

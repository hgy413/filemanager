package com.jiubang.commerce.statistics.adinfo.bean;

import org.json.JSONObject;

public class AdInfo {
    public String mTitle;

    public AdInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.mTitle = jsonObject.optString("title");
        }
    }
}

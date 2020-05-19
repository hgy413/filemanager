package com.jiubang.commerce.ad.http.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseTagInfoBean implements Serializable {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    private static final long serialVersionUID = 2505814186352540044L;
    private int mTagId;
    private String mTagName;

    public int getTagId() {
        return this.mTagId;
    }

    public void setTagId(int tagId) {
        this.mTagId = tagId;
    }

    public String getTagName() {
        return this.mTagName;
    }

    public void setTagName(String tagName) {
        this.mTagName = tagName;
    }

    public static List<BaseTagInfoBean> parseJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseTagInfoBean> tagInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            BaseTagInfoBean tagInfo = new BaseTagInfoBean();
            try {
                tagInfo.parseJsonObject(jsonArray.getJSONObject(index));
                tagInfoList.add(tagInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tagInfoList;
    }

    public void parseJsonObject(JSONObject jsonObj) {
        if (jsonObj != null) {
            this.mTagId = jsonObj.optInt("tagId", 0);
            this.mTagName = jsonObj.optString("tagName", "");
        }
    }
}

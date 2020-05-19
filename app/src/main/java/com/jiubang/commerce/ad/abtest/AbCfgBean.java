package com.jiubang.commerce.ad.abtest;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class AbCfgBean {
    private int mAdModuleId = -1;
    private List<Integer> mIntellAdIds = new ArrayList();
    private String mPlan;
    private List<Integer> mReqModuleIds = new ArrayList();

    public String getPlan() {
        return this.mPlan;
    }

    public int getAdModuleId() {
        return this.mAdModuleId;
    }

    public List<Integer> getReqModuleIds() {
        return this.mReqModuleIds;
    }

    public List<Integer> getIntellAdIds() {
        return this.mIntellAdIds;
    }

    static AbCfgBean parseJson(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.length() < 1) {
            return null;
        }
        AbCfgBean bean = new AbCfgBean();
        bean.mAdModuleId = jsonObject.optInt("advid");
        bean.mPlan = jsonObject.optString("plan");
        for (int i = 1; i < 5; i++) {
            bean.mIntellAdIds.add(Integer.valueOf(jsonObject.optInt("advid" + i, -1)));
        }
        JSONArray jsonArray = jsonObject.optJSONArray("modules");
        if (jsonArray == null) {
            return bean;
        }
        for (int i2 = 0; i2 < jsonArray.length(); i2++) {
            int rmid = jsonArray.optJSONObject(i2).optInt("req_moduleid", -1);
            if (rmid > 0) {
                bean.mReqModuleIds.add(Integer.valueOf(rmid));
            }
        }
        return bean;
    }

    public static List<AbCfgBean> parseJsonArray(JSONArray jsonArray) {
        ArrayList<AbCfgBean> result = new ArrayList<>();
        if (jsonArray != null && jsonArray.length() >= 1) {
            for (int i = 0; i < jsonArray.length(); i++) {
                AbCfgBean bean = parseJson(jsonArray.optJSONObject(i));
                if (bean != null) {
                    result.add(bean);
                }
            }
        }
        return result;
    }
}

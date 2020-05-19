package com.jiubang.commerce.ad.abtest;

import android.text.TextUtils;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class AbBean {
    static final double FRE = 0.036d;
    static final String ID = "1644437002540131_1708430099474154";
    private int mABTestId = -1;
    private List<AbCfgBean> mCfgs;
    private int mFilterId;
    private double mFrequency = FRE;
    private boolean mIsSuccess;
    private String mJsonStr;
    private String mTestId = ID;

    public AbBean(String jsonStr) {
        parseJsonStr(jsonStr);
    }

    public String getJsonStr() {
        return this.mJsonStr;
    }

    public boolean isSuccess() {
        return this.mIsSuccess;
    }

    public int getFilterId() {
        return this.mFilterId;
    }

    public int getABTestId() {
        return this.mABTestId;
    }

    public int getPlotId() {
        String plan = getPlan();
        if ("a".equals(plan)) {
            return FbNativeAdTrick.Plot.A.ordinal();
        }
        if ("b".equals(plan)) {
            return FbNativeAdTrick.Plot.B.ordinal();
        }
        if ("c".equals(plan)) {
            return FbNativeAdTrick.Plot.C.ordinal();
        }
        return -1;
    }

    public boolean requestIdContained(int requestId) {
        AbCfgBean bean = getFirstAbCfgBean();
        if (bean != null) {
            return bean.getReqModuleIds().contains(Integer.valueOf(requestId));
        }
        return false;
    }

    public int getAdPos() {
        AbCfgBean bean = getFirstAbCfgBean();
        if (bean != null) {
            return bean.getAdModuleId();
        }
        return -1;
    }

    public String getPlan() {
        AbCfgBean bean = getFirstAbCfgBean();
        if (bean != null) {
            return bean.getPlan();
        }
        return null;
    }

    public AbCfgBean getFirstAbCfgBean() {
        if (this.mCfgs == null || this.mCfgs.isEmpty()) {
            return null;
        }
        return this.mCfgs.get(0);
    }

    public boolean isFromNet() {
        return !TextUtils.isEmpty(this.mJsonStr);
    }

    public double getFrequency() {
        return this.mFrequency;
    }

    public String getTestId() {
        return this.mTestId;
    }

    private void parseJsonStr(String jsonStr) {
        JSONObject jsonObject = null;
        if (jsonStr != null) {
            try {
                jsonObject = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject != null) {
            this.mJsonStr = jsonStr;
            this.mIsSuccess = jsonObject.optBoolean("success", false);
            if (this.mIsSuccess) {
                JSONObject datas = jsonObject.optJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_DATAS);
                JSONObject infos = datas != null ? datas.optJSONObject("infos") : null;
                if (infos != null) {
                    this.mFilterId = infos.optInt("filter_id");
                    this.mABTestId = infos.optInt("abtest_id", -1);
                    this.mCfgs = AbCfgBean.parseJsonArray(infos.optJSONArray("cfgs"));
                    this.mFrequency = infos.optDouble("tfrequency", FRE);
                    this.mTestId = infos.optString("ttestId", ID);
                }
            }
        }
    }
}

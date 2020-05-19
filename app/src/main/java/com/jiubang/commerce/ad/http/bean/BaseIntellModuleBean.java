package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import java.util.List;
import org.json.JSONObject;

public class BaseIntellModuleBean {
    private int mAdPos;
    private List<BaseIntellAdInfoBean> mAdvs;
    private int mClickLimit;
    private String mErrorMessage;
    private int mFinalGpJump;
    private int mSuccess;

    public int getmAdPos() {
        return this.mAdPos;
    }

    public void setmAdPos(int mAdPos2) {
        this.mAdPos = mAdPos2;
    }

    public int getmSuccess() {
        return this.mSuccess;
    }

    public boolean isSuccess() {
        return 1 == this.mSuccess;
    }

    public void setmSuccess(int mSuccess2) {
        this.mSuccess = mSuccess2;
    }

    public int getmClickLimit() {
        return this.mClickLimit;
    }

    public void setmClickLimit(int mClickLimit2) {
        this.mClickLimit = mClickLimit2;
    }

    public String getmErrorMessage() {
        return this.mErrorMessage;
    }

    public void setmErrorMessage(String mErrorMessage2) {
        this.mErrorMessage = mErrorMessage2;
    }

    public List<BaseIntellAdInfoBean> getmAdvs() {
        return this.mAdvs;
    }

    public void setmAdvs(List<BaseIntellAdInfoBean> mAdvs2) {
        this.mAdvs = mAdvs2;
    }

    public static BaseIntellModuleBean parseJSONObject(Context context, int adPos, JSONObject json) {
        if (json == null || json.length() < 1) {
            return null;
        }
        BaseIntellModuleBean bean = new BaseIntellModuleBean();
        bean.mAdPos = adPos;
        bean.mSuccess = json.optInt("success", 0);
        bean.mClickLimit = json.optInt("clickLimit", -1);
        bean.mErrorMessage = json.optString("message");
        bean.mFinalGpJump = json.optInt("gpJump", 1);
        bean.mAdvs = BaseIntellAdInfoBean.parseJsonArray(context, json.optJSONArray("advs"), adPos, bean.mFinalGpJump);
        return bean;
    }
}

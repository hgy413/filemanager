package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import com.jiubang.commerce.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseIntellAdInfoBean {
    public static final int GP_JUMP_YES = 1;
    private String mAppName;
    private String mBannerUrl;
    private int mCorpId;
    private int mFinalGpJump;
    private String mIconUrl;
    private int mMapId;
    private int mNeedUA;
    private String mPackageName;
    private int mPreClick;
    private String mTargetUrl;

    public int getmCorpId() {
        return this.mCorpId;
    }

    public void setmCorpId(int mCorpId2) {
        this.mCorpId = mCorpId2;
    }

    public String getmPackageName() {
        return this.mPackageName;
    }

    public void setmPackageName(String mPackageName2) {
        this.mPackageName = mPackageName2;
    }

    public int getmMapId() {
        return this.mMapId;
    }

    public void setmMapId(int mMapId2) {
        this.mMapId = mMapId2;
    }

    public String getmTargetUrl() {
        return this.mTargetUrl;
    }

    public void setmTargetUrl(String mTargetUrl2) {
        this.mTargetUrl = mTargetUrl2;
    }

    public String getmAppName() {
        return this.mAppName;
    }

    public void setmAppName(String mAppName2) {
        this.mAppName = mAppName2;
    }

    public int getmPreClick() {
        return this.mPreClick;
    }

    public void setmPreClick(int mPreClick2) {
        this.mPreClick = mPreClick2;
    }

    public String getmIconUrl() {
        return this.mIconUrl;
    }

    public void setmIconUrl(String mIconUrl2) {
        this.mIconUrl = mIconUrl2;
    }

    public String getmBannerUrl() {
        return this.mBannerUrl;
    }

    public void setmBannerUrl(String mBannerUrl2) {
        this.mBannerUrl = mBannerUrl2;
    }

    public int getmFinalGpJump() {
        return this.mFinalGpJump;
    }

    public int getmNeedUA() {
        return this.mNeedUA;
    }

    public void setmNeedUA(int mNeedUA2) {
        this.mNeedUA = mNeedUA2;
    }

    public static int getUAType(int needUA) {
        switch (needUA) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return 0;
        }
    }

    public static List<BaseIntellAdInfoBean> parseJsonArray(Context context, JSONArray jsonArray, int adPos, int gpJump) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseIntellAdInfoBean> intellAdInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                BaseIntellAdInfoBean baseIntellAdInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), adPos, gpJump);
                if (baseIntellAdInfoBean != null && !AppUtils.isAppExist(context, baseIntellAdInfoBean.getmPackageName())) {
                    intellAdInfoList.add(baseIntellAdInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return intellAdInfoList;
    }

    public static BaseIntellAdInfoBean parseJsonObject(Context context, JSONObject jsonObject, int adPos, int gpJump) {
        if (jsonObject == null) {
            return null;
        }
        BaseIntellAdInfoBean intellAdInfoBean = new BaseIntellAdInfoBean();
        intellAdInfoBean.mCorpId = jsonObject.optInt("corpId");
        intellAdInfoBean.mPackageName = jsonObject.optString("packageName");
        intellAdInfoBean.mMapId = jsonObject.optInt("mapid");
        intellAdInfoBean.mTargetUrl = jsonObject.optString("targetUrl");
        intellAdInfoBean.mAppName = jsonObject.optString("appName");
        intellAdInfoBean.mPreClick = jsonObject.optInt("preClick");
        intellAdInfoBean.mIconUrl = jsonObject.optString("iconUrl");
        intellAdInfoBean.mBannerUrl = jsonObject.optString("bannerUrl");
        intellAdInfoBean.mNeedUA = jsonObject.optInt("needUA");
        intellAdInfoBean.mFinalGpJump = gpJump;
        return intellAdInfoBean;
    }
}

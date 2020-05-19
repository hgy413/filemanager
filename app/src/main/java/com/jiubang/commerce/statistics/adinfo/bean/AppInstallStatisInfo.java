package com.jiubang.commerce.statistics.adinfo.bean;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

public class AppInstallStatisInfo {
    public String mBannerUrl;
    public String mDesc;
    public String mId;
    public Uri mLastUri;
    public boolean mNeedPhase2Install;
    public String mPkgName;
    public String mTitle;
    public int mType;
    private long mUpdateTime;

    public AppInstallStatisInfo(JSONObject jsonObject) {
        this.mType = jsonObject.optInt("type");
        this.mLastUri = Uri.parse(jsonObject.optString("url", ""));
        this.mId = jsonObject.optString("ad_id");
        this.mPkgName = jsonObject.optString("pkg_name");
        this.mTitle = jsonObject.optString("title");
        this.mDesc = jsonObject.optString("desc");
        this.mBannerUrl = jsonObject.optString("banner_url");
        this.mUpdateTime = jsonObject.optLong("update_time");
        this.mNeedPhase2Install = jsonObject.optBoolean("need_phase2_install", false);
    }

    public AppInstallStatisInfo(String bannerUrl, String desc, String id, Uri lastUri, String pkgName, String title, int type, long updateTime) {
        this.mBannerUrl = bannerUrl;
        this.mDesc = desc;
        this.mId = id;
        this.mLastUri = lastUri;
        this.mPkgName = pkgName;
        this.mTitle = title;
        this.mType = type;
        this.mUpdateTime = updateTime;
        this.mNeedPhase2Install = false;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", this.mType);
            jsonObject.put("url", this.mLastUri.toString());
            jsonObject.put("ad_id", this.mId);
            jsonObject.put("pkg_name", this.mPkgName);
            jsonObject.put("title", this.mTitle);
            jsonObject.put("desc", this.mDesc);
            jsonObject.put("banner_url", this.mBannerUrl);
            jsonObject.put("update_time", this.mUpdateTime);
            jsonObject.put("need_phase2_install", this.mNeedPhase2Install);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public boolean isValid() {
        return System.currentTimeMillis() - this.mUpdateTime < 43200000;
    }
}

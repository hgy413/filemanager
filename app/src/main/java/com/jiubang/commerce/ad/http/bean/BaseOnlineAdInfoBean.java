package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseOnlineAdInfoBean {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    public static final String ONLINE_AD_JSON_TAG = "resourceMap";
    private String mAdInfoCacheFileName;
    private int mAdPos;
    private int mAdvDataSource;
    private String mAppInfo;
    private String mAppName;
    private String mBannerUrl;
    private String mClickUrl;
    private int mCorpId;
    private String mDismissUrl;
    private int mDownType;
    private String mIconUrl;
    private String mId;
    private String mInstallCallUrl;
    private int mMapId;
    private int mModuleId;
    private String mPackageName;
    private int mPreClick;
    private String mPreviewImgUrl;
    private float mPrice;
    private String mShowUrl;
    private String mTargetUrl;
    private int mUASwitcher;
    private int mVirtualModuleId;

    public int getModuleId() {
        return this.mModuleId;
    }

    public void setModuleId(int moduleId) {
        this.mModuleId = moduleId;
    }

    public int getAdPos() {
        return this.mAdPos;
    }

    public void setAdPos(int adPos) {
        this.mAdPos = adPos;
    }

    public String getId() {
        return this.mId;
    }

    public void setId(String d) {
        this.mId = d;
    }

    public int getCorpId() {
        return this.mCorpId;
    }

    public void setCorpId(int corpId) {
        this.mCorpId = corpId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public int getMapId() {
        return this.mMapId;
    }

    public void setMapId(int mapId) {
        this.mMapId = mapId;
    }

    public String getTargetUrl() {
        return this.mTargetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.mTargetUrl = targetUrl;
    }

    public int getDownType() {
        return this.mDownType;
    }

    public void setDownType(int downType) {
        this.mDownType = downType;
    }

    public String getIconUrl() {
        return this.mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
    }

    public String getBannerUrl() {
        return this.mBannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.mBannerUrl = bannerUrl;
    }

    public String getAppName() {
        return this.mAppName;
    }

    public void setAppName(String appName) {
        this.mAppName = appName;
    }

    public String getPreviewImgUrl() {
        return this.mPreviewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.mPreviewImgUrl = previewImgUrl;
    }

    public String getAppInfo() {
        return this.mAppInfo;
    }

    public void setAppInfo(String appInfo) {
        this.mAppInfo = appInfo;
    }

    public String getShowUrl() {
        return this.mShowUrl;
    }

    public void setShowUrl(String showUrl) {
        this.mShowUrl = showUrl;
    }

    public String getClickUrl() {
        return this.mClickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.mClickUrl = clickUrl;
    }

    public String getInstallCallUrl() {
        return this.mInstallCallUrl;
    }

    public void setInstallCallUrl(String installCallUrl) {
        this.mInstallCallUrl = installCallUrl;
    }

    public String getDismissUrl() {
        return this.mDismissUrl;
    }

    public void setDismissUrl(String dismissUrl) {
        this.mDismissUrl = dismissUrl;
    }

    public int getPreClick() {
        return this.mPreClick;
    }

    public void setPreClick(int preClick) {
        this.mPreClick = preClick;
    }

    public float getPrice() {
        return this.mPrice;
    }

    public void setPrice(float price) {
        this.mPrice = price;
    }

    public int getUASwitcher() {
        return this.mUASwitcher;
    }

    public void setUASwitcher(int switcher) {
        this.mUASwitcher = switcher;
    }

    public int getVirtualModuleId() {
        return this.mVirtualModuleId;
    }

    public void setVirtualModuleId(int virtualModuleId) {
        this.mVirtualModuleId = virtualModuleId;
    }

    public String getAdInfoCacheFileName() {
        return this.mAdInfoCacheFileName;
    }

    public void setAdInfoCacheFileName(String adInfoCacheFileName) {
        this.mAdInfoCacheFileName = adInfoCacheFileName;
    }

    public int getAdvDataSource() {
        return this.mAdvDataSource;
    }

    public void setAdvDataSource(int advDataSource) {
        this.mAdvDataSource = advDataSource;
    }

    public static List<BaseOnlineAdInfoBean> parseJsonArray(Context context, JSONArray jsonArray, int virtualModuleId, int moduleId, int adPos, int advDataSource) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseOnlineAdInfoBean> onlineAdInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                BaseOnlineAdInfoBean baseOnlineAdInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), virtualModuleId, moduleId, adPos, advDataSource);
                if (baseOnlineAdInfoBean != null) {
                    onlineAdInfoList.add(baseOnlineAdInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return onlineAdInfoList;
    }

    public static BaseOnlineAdInfoBean parseJsonObject(Context context, JSONObject jsonObject, int virtualModuleId, int moduleId, int adPos, int advDataSource) {
        if (jsonObject == null) {
            return null;
        }
        BaseOnlineAdInfoBean onlineAdInfoBean = new BaseOnlineAdInfoBean();
        onlineAdInfoBean.setVirtualModuleId(virtualModuleId);
        onlineAdInfoBean.setModuleId(moduleId);
        onlineAdInfoBean.setAdPos(adPos);
        onlineAdInfoBean.setId(jsonObject.optString("id", ""));
        onlineAdInfoBean.setCorpId(jsonObject.optInt("corpId", 0));
        onlineAdInfoBean.setPackageName(jsonObject.optString("packageName", ""));
        onlineAdInfoBean.setMapId(jsonObject.optInt("mapid", 0));
        onlineAdInfoBean.setTargetUrl(jsonObject.optString("targetUrl", ""));
        onlineAdInfoBean.setDownType(jsonObject.optInt("downType", 0));
        onlineAdInfoBean.setIconUrl(jsonObject.optString("iconUrl", ""));
        onlineAdInfoBean.setBannerUrl(jsonObject.optString("bannerUrl", ""));
        onlineAdInfoBean.setAppName(jsonObject.optString("appName", ""));
        onlineAdInfoBean.setPreviewImgUrl(jsonObject.optString("previewImgUrl", ""));
        onlineAdInfoBean.setAppInfo(jsonObject.optString("appInfo", ""));
        onlineAdInfoBean.setShowUrl(jsonObject.optString("showUrl", ""));
        onlineAdInfoBean.setClickUrl(jsonObject.optString("clickUrl", ""));
        onlineAdInfoBean.setInstallCallUrl(jsonObject.optString("installCallUrl", ""));
        onlineAdInfoBean.setDismissUrl(jsonObject.optString("dismissUrl", ""));
        onlineAdInfoBean.setPreClick(jsonObject.optInt("preClick", 0));
        onlineAdInfoBean.setPrice((float) jsonObject.optDouble("price", 0.0d));
        onlineAdInfoBean.setUASwitcher(jsonObject.optInt("ua", 0));
        onlineAdInfoBean.setAdInfoCacheFileName(BaseOnlineModuleInfoBean.getCacheFileName(adPos));
        onlineAdInfoBean.mAdvDataSource = advDataSource;
        return onlineAdInfoBean;
    }
}

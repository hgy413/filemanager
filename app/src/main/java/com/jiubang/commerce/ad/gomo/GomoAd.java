package com.jiubang.commerce.ad.gomo;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GomoAd {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    public static final long INVALID_BANNER_ID = -1;
    private String mAdInfoCacheFileName;
    private int mAdPos;
    private int mAdvDataSource;
    private String mAppDescription;
    private String mAppName;
    private long mBannerId = -1;
    private String mBannerUrl;
    private int mCorpId;
    private int mDownloadCount;
    private String mIconUrl;
    private int mMapId;
    private int mModuleId;
    private String mPackageName;
    private int mParentAdPos;
    private int mPreClick;
    private double mScore;
    private String mSize;
    private String mTargetUrl;
    private int mVmId;

    public int getAdPos() {
        return this.mAdPos;
    }

    public void setAdPos(int adPos) {
        this.mAdPos = adPos;
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

    public int getPreClick() {
        return this.mPreClick;
    }

    public void setPreClick(int preClick) {
        this.mPreClick = preClick;
    }

    public int getParentAdPos() {
        return this.mParentAdPos;
    }

    public void setParentAdPos(int parentAdPos) {
        this.mParentAdPos = parentAdPos;
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

    public double getmScore() {
        return this.mScore;
    }

    public void setmScore(double mScore2) {
        this.mScore = mScore2;
    }

    public int getmDownloadCount() {
        return this.mDownloadCount;
    }

    public void setmDownloadCount(int mDownloadCount2) {
        this.mDownloadCount = mDownloadCount2;
    }

    public String getmSize() {
        return this.mSize;
    }

    public void setmSize(String mSize2) {
        this.mSize = mSize2;
    }

    public String getmAppDescription() {
        return this.mAppDescription;
    }

    public int getVmId() {
        return this.mVmId;
    }

    public void setVmId(int mVmId2) {
        this.mVmId = mVmId2;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public void setModuleId(int mModuleId2) {
        this.mModuleId = mModuleId2;
    }

    public long getBannerId() {
        return this.mBannerId;
    }

    public static List<GomoAd> parseJsonArray(Context context, JSONArray jsonArray, int parentAdPos, int advDataSource, int vmid, int moduleId) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<GomoAd> onlineAdInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                GomoAd baseOnlineAdInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), parentAdPos, advDataSource, vmid, moduleId);
                if (baseOnlineAdInfoBean != null) {
                    onlineAdInfoList.add(baseOnlineAdInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return onlineAdInfoList;
    }

    public static GomoAd parseJsonObject(Context context, JSONObject jsonObject, int parentAdPos, int advDataSource, int vmid, int moduleId) {
        if (jsonObject == null) {
            return null;
        }
        String pkg = jsonObject.optString("packageName", "");
        GomoAd onlineAdInfoBean = new GomoAd();
        onlineAdInfoBean.setParentAdPos(parentAdPos);
        onlineAdInfoBean.setAdPos(jsonObject.optInt("advposid", 0));
        onlineAdInfoBean.setCorpId(jsonObject.optInt("corpId", 0));
        onlineAdInfoBean.setPackageName(pkg);
        onlineAdInfoBean.setMapId(jsonObject.optInt("mapid", 0));
        onlineAdInfoBean.setTargetUrl(jsonObject.optString("targetUrl", ""));
        onlineAdInfoBean.setIconUrl(jsonObject.optString("iconUrl", ""));
        onlineAdInfoBean.setBannerUrl(jsonObject.optString("bannerUrl", ""));
        onlineAdInfoBean.setAppName(jsonObject.optString("appName", ""));
        onlineAdInfoBean.setPreClick(jsonObject.optInt("preClick", 0));
        onlineAdInfoBean.setmScore(jsonObject.optDouble("score"));
        onlineAdInfoBean.setmDownloadCount(jsonObject.optInt("downloadCount", 0));
        onlineAdInfoBean.setmSize(jsonObject.optString("size", ""));
        onlineAdInfoBean.mAppDescription = jsonObject.optString("description", "");
        onlineAdInfoBean.setAdInfoCacheFileName(GomoAdModuleInfo.getCacheFileName(parentAdPos));
        onlineAdInfoBean.mAdvDataSource = advDataSource;
        onlineAdInfoBean.mVmId = vmid;
        onlineAdInfoBean.mModuleId = moduleId;
        onlineAdInfoBean.mBannerId = jsonObject.optLong("bannerId", -1);
        return onlineAdInfoBean;
    }
}

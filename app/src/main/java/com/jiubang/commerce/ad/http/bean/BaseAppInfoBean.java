package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class BaseAppInfoBean implements Serializable {
    public static final int AD_PRELOAD_NO = 0;
    public static final int AD_PRELOAD_YES = 1;
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    public static final int IS_AD_NO = 0;
    public static final int IS_AD_YES = 1;
    public static final int IS_H5_AD_NO = 0;
    public static final int IS_H5_AD_YES = 1;
    private static final long serialVersionUID = 1;
    private int mAdId;
    private String mAdInfoCacheFileName;
    private int mAdPreload;
    private int mAdSrc;
    private int mAdType;
    private String mAdUrl;
    private int mAdvDataSource;
    private String mBanner;
    private String mBannerDescribe;
    private String mBannerTitle;
    private String mCategory;
    private String mClickCallUrl;
    private String mCparams;
    private int mDSize;
    private String mDetail;
    private String mDeveloper;
    private int mDownType;
    private String mDownUrl;
    private int mDownloadCount;
    private String mDownloadCountStr;
    private int mFrequency;
    private String mIcon;
    private List<String> mImageList;
    private String mImgfull;
    private String mInstallCallUrl;
    private int mIsAd;
    private boolean mIsBrandAdv = false;
    private boolean mIsH5Adv;
    private int mIsRemd;
    private int mMapId;
    private int mModuleId;
    private String mName;
    private int mOType;
    private String mPackageName;
    private int mPayType;
    private String mPreView;
    private String mPrice;
    private String mPriceRange;
    private String mRemdMsg;
    private String mScore;
    private String mSerialNum;
    private String mShowCallUrl;
    private int mShowType;
    private String mSize;
    private String mSupport;
    private List<BaseTagInfoBean> mTagInfoList;
    private int mUASwitcher;
    private String mUpdateLog;
    private String mUpdateTime;
    private String mVersionName;
    private String mVersionNumber;
    private int mVirtualModuleId;

    public int getMapId() {
        return this.mMapId;
    }

    public void setMapId(int mapId) {
        this.mMapId = mapId;
    }

    public String getSerialNum() {
        return this.mSerialNum;
    }

    public void setSerialNum(String serialNum) {
        this.mSerialNum = serialNum;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public void setBanner(String banner) {
        this.mBanner = banner;
    }

    public String getBannerTitle() {
        return this.mBannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.mBannerTitle = bannerTitle;
    }

    public String getBannerDescribe() {
        return this.mBannerDescribe;
    }

    public void setBannerDescribe(String bannerDescribe) {
        this.mBannerDescribe = bannerDescribe;
    }

    public boolean getIsH5Adv() {
        return this.mIsH5Adv;
    }

    public void setIsH5Adv(boolean b) {
        this.mIsH5Adv = b;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getPreView() {
        return this.mPreView;
    }

    public void setPreView(String preView) {
        this.mPreView = preView;
    }

    public List<String> getImageList() {
        return this.mImageList;
    }

    public void setImageList(List<String> imageList) {
        this.mImageList = imageList;
    }

    public String getVersionName() {
        return this.mVersionName;
    }

    public void setVersionName(String versionName) {
        this.mVersionName = versionName;
    }

    public String getVersionNumber() {
        return this.mVersionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.mVersionNumber = versionNumber;
    }

    public String getScore() {
        return this.mScore;
    }

    public void setScore(String score) {
        this.mScore = score;
    }

    public String getDeveloper() {
        return this.mDeveloper;
    }

    public void setDeveloper(String developer) {
        this.mDeveloper = developer;
    }

    public int getPayType() {
        return this.mPayType;
    }

    public void setPayType(int payType) {
        this.mPayType = payType;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public String getSize() {
        return this.mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public int getDownloadCount() {
        return this.mDownloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.mDownloadCount = downloadCount;
    }

    public String getDownloadCountStr() {
        return this.mDownloadCountStr;
    }

    public void setDownloadCountStr(String downloadCountStr) {
        this.mDownloadCountStr = downloadCountStr;
    }

    public String getDetail() {
        return this.mDetail;
    }

    public void setDetail(String detail) {
        this.mDetail = detail;
    }

    public int getAdPreload() {
        return this.mAdPreload;
    }

    public void setAdPreload(int adPreload) {
        this.mAdPreload = adPreload;
    }

    public String getUpdateLog() {
        return this.mUpdateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.mUpdateLog = updateLog;
    }

    public String getSupport() {
        return this.mSupport;
    }

    public void setSupport(String support) {
        this.mSupport = support;
    }

    public String getUpdateTime() {
        return this.mUpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.mUpdateTime = updateTime;
    }

    public int getOType() {
        return this.mOType;
    }

    public void setOType(int oType) {
        this.mOType = oType;
    }

    public int getDownType() {
        return this.mDownType;
    }

    public void setDownType(int downType) {
        this.mDownType = downType;
    }

    public String getDownUrl() {
        return this.mDownUrl;
    }

    public void setDownUrl(String downUrl) {
        this.mDownUrl = downUrl;
    }

    public int getShowType() {
        return this.mShowType;
    }

    public void setShowType(int showType) {
        this.mShowType = showType;
    }

    public List<BaseTagInfoBean> getTagInfoList() {
        return this.mTagInfoList;
    }

    public void setTagInfoList(List<BaseTagInfoBean> tagInfoList) {
        this.mTagInfoList = tagInfoList;
    }

    public int getIsRemd() {
        return this.mIsRemd;
    }

    public void setIsRemd(int isRemd) {
        this.mIsRemd = isRemd;
    }

    public String getRemdMsg() {
        return this.mRemdMsg;
    }

    public void setRemdMsg(String remdMsg) {
        this.mRemdMsg = remdMsg;
    }

    public String getPriceRange() {
        return this.mPriceRange;
    }

    public void setPriceRange(String priceRange) {
        this.mPriceRange = priceRange;
    }

    public int getIsAd() {
        return this.mIsAd;
    }

    public void setIsAd(int isAd) {
        this.mIsAd = isAd;
    }

    public String getAdUrl() {
        return this.mAdUrl;
    }

    public void setAdUrl(String adUrl) {
        this.mAdUrl = adUrl;
    }

    public int getAdSrc() {
        return this.mAdSrc;
    }

    public void setAdSrc(int adSrc) {
        this.mAdSrc = adSrc;
    }

    public String getShowCallUrl() {
        return this.mShowCallUrl;
    }

    public void setShowCallUrl(String showCallUrl) {
        this.mShowCallUrl = showCallUrl;
    }

    public String getClickCallUrl() {
        return this.mClickCallUrl;
    }

    public void setClickCallUrl(String clickCallUrl) {
        this.mClickCallUrl = clickCallUrl;
    }

    public String getInstallCallUrl() {
        return this.mInstallCallUrl;
    }

    public void setInstallCallUrl(String installCallUrl) {
        this.mInstallCallUrl = installCallUrl;
    }

    public int getUASwitcher() {
        return this.mUASwitcher;
    }

    public void setUASwitcher(int switcher) {
        this.mUASwitcher = switcher;
    }

    public String getCategory() {
        return this.mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public int getAdType() {
        return this.mAdType;
    }

    public void setAdType(int adType) {
        this.mAdType = adType;
    }

    public int getVirtualModuleId() {
        return this.mVirtualModuleId;
    }

    public void setVirtualModuleId(int virtualModuleId) {
        this.mVirtualModuleId = virtualModuleId;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public void setModuleId(int moduleId) {
        this.mModuleId = moduleId;
    }

    public int getAdId() {
        return this.mAdId;
    }

    public void setAdId(int adId) {
        this.mAdId = adId;
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

    public String getCparams() {
        return this.mCparams;
    }

    public void setCparams(String mCparams2) {
        this.mCparams = mCparams2;
    }

    public String getImgfull() {
        return this.mImgfull;
    }

    public void setImgfull(String mImgfull2) {
        this.mImgfull = mImgfull2;
    }

    public boolean isBrandAdv() {
        return this.mIsBrandAdv;
    }

    public int getDSize() {
        return this.mDSize;
    }

    public void setDSize(int mDSize2) {
        this.mDSize = mDSize2;
    }

    public int getFrequency() {
        return this.mFrequency;
    }

    public void setFrequency(int mFrequency2) {
        this.mFrequency = mFrequency2;
    }

    public static List<BaseAppInfoBean> parseJsonArray(Context context, JSONArray jsonArray, int virtualModuleId, int moduleId, int adId, int advDataSource, boolean isBrandAdv) {
        if (jsonArray == null || jsonArray.length() < 1) {
            return null;
        }
        List<BaseAppInfoBean> appInfoList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                BaseAppInfoBean baseAppInfoBean = parseJsonObject(context, jsonArray.getJSONObject(index), virtualModuleId, moduleId, adId, advDataSource, isBrandAdv);
                if (baseAppInfoBean != null) {
                    appInfoList.add(baseAppInfoBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appInfoList;
    }

    public static BaseAppInfoBean parseJsonObject(Context context, JSONObject jsonObject, int virtualModuleId, int moduleId, int adId, int advDataSource, boolean isBrandAdv) {
        if (jsonObject == null) {
            return null;
        }
        BaseAppInfoBean appInfoBean = new BaseAppInfoBean();
        appInfoBean.mMapId = jsonObject.optInt("mapid", 0);
        appInfoBean.mSerialNum = jsonObject.optString("serialNum", "");
        appInfoBean.mPackageName = jsonObject.optString("pkgname", "");
        appInfoBean.mName = jsonObject.optString("name", "");
        appInfoBean.mBanner = jsonObject.optString("banner", "");
        appInfoBean.mBannerTitle = jsonObject.optString("bannertitle", "");
        appInfoBean.mBannerDescribe = jsonObject.optString("bannerdescribe", "");
        appInfoBean.mIsH5Adv = jsonObject.optInt("ish5adv", 0) == 1;
        appInfoBean.mIcon = jsonObject.optString("icon", "");
        appInfoBean.mPreView = jsonObject.optString("preview", "");
        if (jsonObject.has("images")) {
            try {
                JSONArray imageJsonArray = jsonObject.getJSONArray("images");
                if (imageJsonArray != null && imageJsonArray.length() > 0) {
                    List<String> imageList = new ArrayList<>();
                    for (int index = 0; index < imageJsonArray.length(); index++) {
                        imageList.add(imageJsonArray.optString(index, ""));
                    }
                    appInfoBean.mImageList = imageList;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        appInfoBean.mVersionName = jsonObject.optString("versionName", "");
        appInfoBean.mVersionNumber = jsonObject.optString("versionNumber", "");
        appInfoBean.mScore = jsonObject.optString("score", "");
        appInfoBean.mDeveloper = jsonObject.optString("developer", "");
        appInfoBean.mPayType = jsonObject.optInt("paytype", 0);
        appInfoBean.mPrice = jsonObject.optString("price", "");
        appInfoBean.mSize = jsonObject.optString("size", "");
        appInfoBean.mDownloadCount = jsonObject.optInt("downloadCount", 0);
        appInfoBean.mDownloadCountStr = jsonObject.optString("downloadCount_s", "");
        appInfoBean.mDetail = jsonObject.optString("detail", "");
        appInfoBean.mAdPreload = jsonObject.optInt("adpreload", 0);
        appInfoBean.mUpdateLog = jsonObject.optString("updateLog", "");
        appInfoBean.mSupport = jsonObject.optString("support", "");
        appInfoBean.mUpdateTime = jsonObject.optString("updateTime", "");
        appInfoBean.mOType = jsonObject.optInt("otype", 0);
        appInfoBean.mDownType = jsonObject.optInt("downtype", 0);
        appInfoBean.mDownUrl = jsonObject.optString("downurl", "");
        appInfoBean.mShowType = jsonObject.optInt("showtype", 0);
        appInfoBean.mTagInfoList = BaseTagInfoBean.parseJsonArray(jsonObject.optJSONArray("tags"));
        appInfoBean.mIsRemd = jsonObject.optInt("isremd", 0);
        appInfoBean.mRemdMsg = jsonObject.optString("remdmsg", "");
        appInfoBean.mPriceRange = jsonObject.optString("pricerange", "");
        appInfoBean.mIsAd = jsonObject.optInt("isad", 0);
        appInfoBean.mAdUrl = jsonObject.optString("adurl", "");
        appInfoBean.mAdSrc = jsonObject.optInt("adsrc", 0);
        appInfoBean.mShowCallUrl = jsonObject.optString("showcallurl", "");
        appInfoBean.mClickCallUrl = jsonObject.optString("clickcallurl", "");
        appInfoBean.mInstallCallUrl = jsonObject.optString("installcallurl", "");
        appInfoBean.mCategory = jsonObject.optString("category", "");
        appInfoBean.mAdType = jsonObject.optInt("adtype", 0);
        appInfoBean.mUASwitcher = jsonObject.optInt("ua", 0);
        appInfoBean.mCparams = jsonObject.optString("cparams", "");
        appInfoBean.mImgfull = jsonObject.optString("imgfull", "");
        appInfoBean.mDSize = jsonObject.optInt("dsize", 0);
        appInfoBean.mFrequency = jsonObject.optInt("frequency", 0);
        appInfoBean.mVirtualModuleId = virtualModuleId;
        appInfoBean.mModuleId = moduleId;
        appInfoBean.mAdId = adId;
        appInfoBean.mAdInfoCacheFileName = BaseModuleDataItemBean.getCacheFileName(appInfoBean.mVirtualModuleId);
        appInfoBean.mAdvDataSource = advDataSource;
        appInfoBean.mIsBrandAdv = isBrandAdv;
        return appInfoBean;
    }
}

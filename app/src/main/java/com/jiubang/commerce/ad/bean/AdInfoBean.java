package com.jiubang.commerce.ad.bean;

import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.gomo.GomoAd;
import com.jiubang.commerce.ad.http.bean.BaseAppInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseIntellAdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean;
import com.jiubang.commerce.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdInfoBean implements Serializable {
    public static final int AD_TYPE_FIXED_DISPLAY = 1;
    public static final int AD_TYPE_RANDOM_DISPLAY = 2;
    public static final int UATYPE_GO_FAKE = 4;
    public static final int UATYPE_GO_FULL = 2;
    public static final int UATYPE_GO_HALF = 3;
    public static final int UATYPE_GO_NONE = 1;
    public static final int UATYPE_GO_SWITCH = 0;
    private static final long serialVersionUID = 1;
    private int mAdId;
    private String mAdInfoCacheFileName;
    private int mAdPreload;
    private int mAdSrc;
    private int mAdType;
    private String mAdUrl;
    private int mAdvDataSource;
    private String mAppDescription;
    private String mBanner;
    private String mBannerDescribe;
    private String mBannerTitle;
    private String mCategory;
    private String mClickCallUrl;
    private int mCorpId = -1;
    private String[] mCorrelationNames;
    private String mCparams;
    private long mDSize;
    private String mDetail;
    private String mDeveloper;
    private int mDownType;
    private String mDownUrl;
    private String mDownloadCountStr;
    private String[] mFilterNames;
    private int mFinalGpJump;
    private int mFrequency;
    private long mGomoAdBannerId = -1;
    private String mIcon;
    private String mImgfull;
    private String mInstallCallUrl;
    private int mIsAd;
    private boolean mIsBrandAdv = false;
    private boolean mIsH5Adv;
    private int mMapId;
    private int mModuleId;
    private String mName;
    private int mOnlineAdvType;
    private String mPackageName;
    private String mPreview;
    private String mPrice;
    private String mRemdMsg;
    private String mScore;
    private boolean mShouldSendReferBroadcast = false;
    private String mShowCallUrl;
    private String mSize;
    private int mUASwitcher;
    private int mUAType = 0;
    private String mVersionName;
    private String mVersionNumber;
    private int mVirtualModuleId;

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

    public String getCategory() {
        return this.mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public int getMapId() {
        return this.mMapId;
    }

    public void setMapId(int mMapId2) {
        this.mMapId = mMapId2;
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

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
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

    public void setPreview(String preview) {
        this.mPreview = preview;
    }

    public String getPreview() {
        return this.mPreview;
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

    public String getPrice() {
        return this.mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public String getDeveloper() {
        return this.mDeveloper;
    }

    public void setDeveloper(String developer) {
        this.mDeveloper = developer;
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

    public String getRemdMsg() {
        return this.mRemdMsg;
    }

    public void setRemdMsg(String remdMsg) {
        this.mRemdMsg = remdMsg;
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

    public int getAdPreload() {
        return this.mAdPreload;
    }

    public void setAdPreload(int adPreload) {
        this.mAdPreload = adPreload;
    }

    public int getAdType() {
        return this.mAdType;
    }

    public void setAdType(int adType) {
        this.mAdType = adType;
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

    public int getOnlineAdvType() {
        return this.mOnlineAdvType;
    }

    public void setOnlineAdvType(int mOnlineAdvType2) {
        this.mOnlineAdvType = mOnlineAdvType2;
    }

    public String getSize() {
        return this.mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public boolean isBrandAdv() {
        return this.mIsBrandAdv;
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

    public long getDSize() {
        return this.mDSize;
    }

    public int getCorpId() {
        return this.mCorpId;
    }

    public String getAppDescription() {
        return this.mAppDescription;
    }

    public void setDSize(int mDSize2) {
        this.mDSize = (long) mDSize2;
    }

    public int getFrequency() {
        return this.mFrequency;
    }

    public void setFrequency(int mFrequency2) {
        this.mFrequency = mFrequency2;
    }

    public int getmFinalGpJump() {
        return this.mFinalGpJump;
    }

    public int getUAType() {
        return this.mUAType;
    }

    public void setUAType(int mUAType2) {
        this.mUAType = mUAType2;
    }

    public String[] getCorrelationNames() {
        return this.mCorrelationNames;
    }

    public String[] getFilterNames() {
        return this.mFilterNames;
    }

    public long getGomoAdBannerId() {
        return this.mGomoAdBannerId;
    }

    public boolean shouldSendReferBroadcast() {
        return this.mShouldSendReferBroadcast;
    }

    public static List<AdInfoBean> conversionFormOnlineAdInfoBean(List<BaseOnlineAdInfoBean> onlineAdInfoList) {
        if (onlineAdInfoList == null || onlineAdInfoList.isEmpty()) {
            return null;
        }
        List<AdInfoBean> adInfoList = new ArrayList<>();
        for (BaseOnlineAdInfoBean onlineAdInfoBean : onlineAdInfoList) {
            if (onlineAdInfoBean != null) {
                AdInfoBean adInfoBean = new AdInfoBean();
                adInfoBean.mUASwitcher = onlineAdInfoBean.getUASwitcher();
                adInfoBean.mVirtualModuleId = onlineAdInfoBean.getVirtualModuleId();
                adInfoBean.mModuleId = onlineAdInfoBean.getModuleId();
                adInfoBean.mAdId = onlineAdInfoBean.getAdPos();
                adInfoBean.mCategory = "";
                adInfoBean.mMapId = onlineAdInfoBean.getMapId();
                adInfoBean.mPackageName = onlineAdInfoBean.getPackageName();
                adInfoBean.mName = onlineAdInfoBean.getAppName();
                adInfoBean.mIcon = onlineAdInfoBean.getIconUrl();
                adInfoBean.mBanner = onlineAdInfoBean.getBannerUrl();
                adInfoBean.mBannerTitle = "";
                adInfoBean.mBannerDescribe = "";
                adInfoBean.mIsH5Adv = false;
                adInfoBean.mPreview = onlineAdInfoBean.getPreviewImgUrl();
                adInfoBean.mVersionName = "";
                adInfoBean.mVersionNumber = BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE;
                adInfoBean.mScore = "";
                adInfoBean.mPrice = String.valueOf(onlineAdInfoBean.getPrice());
                adInfoBean.mDeveloper = "";
                adInfoBean.mDownloadCountStr = "";
                adInfoBean.mDetail = onlineAdInfoBean.getAppInfo();
                adInfoBean.mDownType = onlineAdInfoBean.getDownType();
                adInfoBean.mDownUrl = onlineAdInfoBean.getTargetUrl();
                adInfoBean.mRemdMsg = adInfoBean.mDetail;
                adInfoBean.mAdSrc = 0;
                adInfoBean.mShowCallUrl = onlineAdInfoBean.getShowUrl();
                adInfoBean.mClickCallUrl = onlineAdInfoBean.getClickUrl();
                adInfoBean.mInstallCallUrl = onlineAdInfoBean.getInstallCallUrl();
                adInfoBean.mIsAd = 1;
                adInfoBean.mAdUrl = onlineAdInfoBean.getTargetUrl();
                adInfoBean.mAdPreload = onlineAdInfoBean.getPreClick();
                adInfoBean.mAdType = 2;
                adInfoBean.mAdInfoCacheFileName = onlineAdInfoBean.getAdInfoCacheFileName();
                adInfoBean.mAdvDataSource = onlineAdInfoBean.getAdvDataSource();
                adInfoBean.mCorpId = onlineAdInfoBean.getCorpId();
                adInfoList.add(adInfoBean);
            }
        }
        return adInfoList;
    }

    public static List<AdInfoBean> conversionFormGomoAdInfoBean(List<GomoAd> onlineAdInfoList) {
        if (onlineAdInfoList == null || onlineAdInfoList.isEmpty()) {
            return null;
        }
        List<AdInfoBean> adInfoList = new ArrayList<>();
        for (GomoAd onlineAdInfoBean : onlineAdInfoList) {
            if (onlineAdInfoBean != null) {
                AdInfoBean adInfoBean = new AdInfoBean();
                adInfoBean.mVirtualModuleId = onlineAdInfoBean.getVmId();
                adInfoBean.mModuleId = onlineAdInfoBean.getModuleId();
                adInfoBean.mAdId = onlineAdInfoBean.getAdPos();
                adInfoBean.mMapId = onlineAdInfoBean.getMapId();
                adInfoBean.mPackageName = onlineAdInfoBean.getPackageName();
                adInfoBean.mName = onlineAdInfoBean.getAppName();
                adInfoBean.mIcon = onlineAdInfoBean.getIconUrl();
                adInfoBean.mBanner = onlineAdInfoBean.getBannerUrl();
                adInfoBean.mScore = StringUtils.toString(Double.valueOf(onlineAdInfoBean.getmScore()));
                adInfoBean.mDownloadCountStr = "" + onlineAdInfoBean.getmDownloadCount();
                adInfoBean.mAdSrc = 0;
                adInfoBean.mIsAd = 1;
                adInfoBean.mAdUrl = onlineAdInfoBean.getTargetUrl();
                adInfoBean.mAdPreload = onlineAdInfoBean.getPreClick();
                adInfoBean.mSize = onlineAdInfoBean.getmSize();
                adInfoBean.mAdType = 2;
                adInfoBean.mAdInfoCacheFileName = onlineAdInfoBean.getAdInfoCacheFileName();
                adInfoBean.mAdvDataSource = onlineAdInfoBean.getAdvDataSource();
                adInfoBean.mCorpId = onlineAdInfoBean.getCorpId();
                adInfoBean.mAppDescription = onlineAdInfoBean.getmAppDescription();
                adInfoBean.mRemdMsg = onlineAdInfoBean.getmAppDescription();
                adInfoBean.mBannerDescribe = onlineAdInfoBean.getmAppDescription();
                adInfoBean.mDetail = onlineAdInfoBean.getmAppDescription();
                adInfoBean.mGomoAdBannerId = onlineAdInfoBean.getBannerId();
                adInfoList.add(adInfoBean);
            }
        }
        return adInfoList;
    }

    public static List<AdInfoBean> conversionFormAppInfoBean(List<BaseAppInfoBean> appInfoList) {
        if (appInfoList == null || appInfoList.isEmpty()) {
            return null;
        }
        List<AdInfoBean> adInfoList = new ArrayList<>();
        for (BaseAppInfoBean appInfoBean : appInfoList) {
            if (appInfoBean != null) {
                AdInfoBean adInfoBean = new AdInfoBean();
                adInfoBean.mUASwitcher = appInfoBean.getUASwitcher();
                adInfoBean.mVirtualModuleId = appInfoBean.getVirtualModuleId();
                adInfoBean.mModuleId = appInfoBean.getModuleId();
                adInfoBean.mAdId = appInfoBean.getAdId();
                adInfoBean.mCategory = appInfoBean.getCategory();
                adInfoBean.mMapId = appInfoBean.getMapId();
                adInfoBean.mPackageName = appInfoBean.getPackageName();
                adInfoBean.mName = appInfoBean.getName();
                adInfoBean.mIcon = appInfoBean.getIcon();
                adInfoBean.mBanner = appInfoBean.getBanner();
                adInfoBean.mBannerTitle = appInfoBean.getBannerTitle();
                adInfoBean.mBannerDescribe = appInfoBean.getBannerDescribe();
                adInfoBean.mIsH5Adv = appInfoBean.getIsH5Adv();
                adInfoBean.mPreview = appInfoBean.getPreView();
                adInfoBean.mVersionName = appInfoBean.getVersionName();
                adInfoBean.mVersionNumber = appInfoBean.getVersionNumber();
                adInfoBean.mScore = appInfoBean.getScore();
                adInfoBean.mPrice = appInfoBean.getPrice();
                adInfoBean.mDeveloper = appInfoBean.getDeveloper();
                adInfoBean.mDownloadCountStr = appInfoBean.getDownloadCountStr();
                adInfoBean.mDetail = appInfoBean.getDetail();
                adInfoBean.mDownType = appInfoBean.getDownType();
                adInfoBean.mDownUrl = appInfoBean.getDownUrl();
                adInfoBean.mRemdMsg = appInfoBean.getRemdMsg();
                adInfoBean.mAdSrc = appInfoBean.getAdSrc();
                adInfoBean.mShowCallUrl = appInfoBean.getShowCallUrl();
                adInfoBean.mClickCallUrl = appInfoBean.getClickCallUrl();
                adInfoBean.mInstallCallUrl = appInfoBean.getInstallCallUrl();
                adInfoBean.mIsAd = appInfoBean.getIsAd();
                adInfoBean.mAdUrl = appInfoBean.getAdUrl();
                adInfoBean.mAdPreload = appInfoBean.getAdPreload();
                adInfoBean.mAdType = appInfoBean.getAdType();
                adInfoBean.mAdInfoCacheFileName = appInfoBean.getAdInfoCacheFileName();
                adInfoBean.mAdvDataSource = appInfoBean.getAdvDataSource();
                adInfoBean.mSize = appInfoBean.getSize();
                adInfoBean.mIsBrandAdv = appInfoBean.isBrandAdv();
                adInfoBean.mCparams = appInfoBean.getCparams();
                adInfoBean.mImgfull = appInfoBean.getImgfull();
                adInfoBean.mDSize = (long) appInfoBean.getDSize();
                adInfoBean.mFrequency = appInfoBean.getFrequency();
                adInfoList.add(adInfoBean);
            }
        }
        return adInfoList;
    }

    public static List<AdInfoBean> conversionFormIntellAdInfoBean(List<BaseIntellAdInfoBean> intellInfoList) {
        if (intellInfoList == null || intellInfoList.isEmpty()) {
            return null;
        }
        List<AdInfoBean> adInfoList = new ArrayList<>();
        for (BaseIntellAdInfoBean intellInfoBean : intellInfoList) {
            if (intellInfoBean != null) {
                adInfoList.add(transferSingleIntellAdInfoBean(intellInfoBean));
            }
        }
        return adInfoList;
    }

    public static AdInfoBean transferSingleIntellAdInfoBean(BaseIntellAdInfoBean intellInfoBean) {
        AdInfoBean adInfoBean = new AdInfoBean();
        adInfoBean.mUAType = BaseIntellAdInfoBean.getUAType(intellInfoBean.getmNeedUA());
        adInfoBean.mUASwitcher = 0;
        adInfoBean.mMapId = intellInfoBean.getmMapId();
        adInfoBean.mPackageName = intellInfoBean.getmPackageName();
        adInfoBean.mName = intellInfoBean.getmAppName();
        adInfoBean.mIcon = intellInfoBean.getmIconUrl();
        adInfoBean.mBanner = intellInfoBean.getmBannerUrl();
        adInfoBean.mIsAd = 1;
        adInfoBean.mAdUrl = intellInfoBean.getmTargetUrl();
        adInfoBean.mAdPreload = intellInfoBean.getmPreClick();
        adInfoBean.mFinalGpJump = intellInfoBean.getmFinalGpJump();
        adInfoBean.mCorpId = intellInfoBean.getmCorpId();
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "IntellAdInfoBean--" + intellInfoBean.getmAppName() + " packageName=" + intellInfoBean.getmPackageName() + " gpJump=" + intellInfoBean.getmFinalGpJump() + " needUA=" + intellInfoBean.getmNeedUA() + " uaType=" + adInfoBean.getUAType() + " " + intellInfoBean.getmTargetUrl());
        }
        return adInfoBean;
    }
}

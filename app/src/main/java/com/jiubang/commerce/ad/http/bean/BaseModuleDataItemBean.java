package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.FileCacheUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class BaseModuleDataItemBean implements Serializable {
    public static final int ADV_DATA_SOURCE_BIG_DATA_LOCAL_PRIORITY = 0;
    public static final int ADV_DATA_SOURCE_BIG_DATA_LOCAL_RECURRENCE = 5;
    public static final int ADV_DATA_SOURCE_GENERAL_ONLINE_DATA = 12;
    public static final int ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_BIG_DATA = 3;
    public static final int ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_QUETTRA = 4;
    public static final int ADV_DATA_SOURCE_INTELLIGENT = 13;
    public static final int ADV_DATA_SOURCE_INTELLIGENT_CLASSIFY = 15;
    public static final int ADV_DATA_SOURCE_LOCAL_CONFIG = 1;
    public static final int ADV_DATA_SOURCE_LOCAL_CONFIG_CN = 14;
    public static final int ADV_DATA_SOURCE_MOBIVISTA = 7;
    public static final int ADV_DATA_SOURCE_PARR_BOGART = 6;
    public static final int AD_DATA_SOURCE_ADMOB = 8;
    public static final int AD_DATA_SOURCE_APPLOVIN = 20;
    public static final int AD_DATA_SOURCE_CHEETAH = 21;
    public static final int AD_DATA_SOURCE_CHEETAH_VIDEO = 38;
    public static final int AD_DATA_SOURCE_FACEBOOK_INTERSTITIAL = 11;
    public static final int AD_DATA_SOURCE_FACEBOOK_NATIVE = 2;
    public static final int AD_DATA_SOURCE_GOMO = 35;
    public static final int AD_DATA_SOURCE_IRONSCR = 37;
    public static final int AD_DATA_SOURCE_LOOP_ME = 16;
    public static final int AD_DATA_SOURCE_MOBILE_CORE_BANNER = 10;
    public static final int AD_DATA_SOURCE_MOBILE_CORE_INTERSTITIAL = 9;
    public static final int AD_DATA_SOURCE_MOPUB = 39;
    public static final int AD_DATA_SOURCE_S2S = 36;
    public static final String AD_DATA_SOURCE_TYPE_OFFLINE = "0";
    public static final String AD_DATA_SOURCE_TYPE_ONLINE = "1";
    public static final int AD_DATA_SOURCE_VUNGLE = 34;
    public static final int DATA_TYPE_CHILD_MODULES = 1;
    public static final int DATA_TYPE_CONTENTS = 2;
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    public static final int ONLINE_AD_SHOW_TYPE_BANNER = 1;
    public static final int ONLINE_AD_SHOW_TYPE_BANNER_300_250 = 5;
    public static final int ONLINE_AD_SHOW_TYPE_FULL_SCREEN = 2;
    public static final int ONLINE_AD_SHOW_TYPE_NATIVE = 3;
    private static final long serialVersionUID = 1;
    private int mActType;
    private int mAdCacheFlag;
    private int mAdFrequency;
    private int mAdMobBanner;
    private int mAdSplitInner;
    private int mAdcolsetype;
    private int mAdfirst;
    private int mAdsplit;
    private int mAdvDataSource;
    private BaseAdvDataSourceExtInfoBean mAdvDataSourceExtInfoBean;
    private int mAdvDataSourceType;
    private int mAdvPositionId;
    private int mAdvScene;
    private String mBackImage;
    private String mBanner;
    private BaseResponseBean mBaseResponseBean;
    private List<BaseModuleDataItemBean> mChildModuleDataItemList;
    private List<BaseModuleInfoBean> mChildModuleList;
    private int mClearflag;
    private int mClickEffect;
    private List<BaseContentResourceInfoBean> mContentResourceInfoList;
    private int mDataType;
    private long mDataVersion;
    private int mEffect;
    private int mFbAdvAbplan;
    private int mFbAdvCount;
    private int mFbAdvPos;
    private String[] mFbIds;
    private int mFbNumperLine;
    private String mFbTabId;
    private int mGoRandom;
    private int mHasAnimation;
    private String mHasShowAdUrlList;
    private String mIcon;
    private int mLayout;
    private String mModuleDesc;
    private int mModuleId;
    private String mModuleName;
    private String mModuleSubTitle;
    private int mOnlineAdvPositionId;
    private int mOnlineAdvType;
    private int mPageId;
    private int mPages;
    private int mPreLoadSwitch;
    private int mPreLoadSwitchType;
    private String mPreview;
    private long mSaveDataTime;
    private int mSequence;
    private int mShowRandom;
    private int mStatisticsType;
    private String mUrl;
    private int mVirtualModuleId;

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

    public int getAdvPositionId() {
        return this.mAdvPositionId;
    }

    public void setAdvPositionId(int advPositionId) {
        this.mAdvPositionId = advPositionId;
    }

    public String getModuleName() {
        return this.mModuleName;
    }

    public void setModuleName(String moduleName) {
        this.mModuleName = moduleName;
    }

    public String getModuleDesc() {
        return this.mModuleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.mModuleDesc = moduleDesc;
    }

    public String getModuleSubTitle() {
        return this.mModuleSubTitle;
    }

    public void setModuleSubTitle(String moduleSubTitle) {
        this.mModuleSubTitle = moduleSubTitle;
    }

    public String getBackImage() {
        return this.mBackImage;
    }

    public void setBackImage(String backImage) {
        this.mBackImage = backImage;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public void setBanner(String banner) {
        this.mBanner = banner;
    }

    public String getPreview() {
        return this.mPreview;
    }

    public void setPreview(String preview) {
        this.mPreview = preview;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public int getShowRandom() {
        return this.mShowRandom;
    }

    public void setShowRandom(int showRandom) {
        this.mShowRandom = showRandom;
    }

    public int getGoRandom() {
        return this.mGoRandom;
    }

    public void setGoRandom(int goRandom) {
        this.mGoRandom = goRandom;
    }

    public int getActType() {
        return this.mActType;
    }

    public void setActType(int actType) {
        this.mActType = actType;
    }

    public int getSequence() {
        return this.mSequence;
    }

    public void setSequence(int sequence) {
        this.mSequence = sequence;
    }

    public int getPreLoadSwitch() {
        return this.mPreLoadSwitch;
    }

    public void setPreLoadSwitch(int preLoadSwitch) {
        this.mPreLoadSwitch = preLoadSwitch;
    }

    public int getPreLoadSwitchType() {
        return this.mPreLoadSwitchType;
    }

    public void setPreLoadSwitchType(int preLoadSwitchType) {
        this.mPreLoadSwitchType = preLoadSwitchType;
    }

    public int getEffect() {
        return this.mEffect;
    }

    public void setEffect(int effect) {
        this.mEffect = effect;
    }

    public int getAdvDataSource() {
        return this.mAdvDataSource;
    }

    public void setAdvDataSource(int advDataSource) {
        this.mAdvDataSource = advDataSource;
    }

    public BaseAdvDataSourceExtInfoBean getAdvDataSourceExtInfoBean() {
        return this.mAdvDataSourceExtInfoBean;
    }

    public int getAdvScene() {
        return this.mAdvScene;
    }

    public void setAdvScene(int advScene) {
        this.mAdvScene = advScene;
    }

    public int getAdvDataSourceType() {
        return this.mAdvDataSourceType;
    }

    public void setAdvDataSourceType(int advDataSourceType) {
        this.mAdvDataSourceType = advDataSourceType;
    }

    public int getFbAdvCount() {
        return this.mFbAdvCount;
    }

    public void setFbAdvCount(int fbAdvCount) {
        this.mFbAdvCount = fbAdvCount;
    }

    public String[] getFbIds() {
        return this.mFbIds;
    }

    public void setFbIds(String[] fbIds) {
        this.mFbIds = fbIds;
    }

    public int getFbAdvPos() {
        return this.mFbAdvPos;
    }

    public void setFbAdvPos(int fbAdvPos) {
        this.mFbAdvPos = fbAdvPos;
    }

    public String getFbTabId() {
        return this.mFbTabId;
    }

    public void setFbTabId(String fbTabId) {
        this.mFbTabId = fbTabId;
    }

    public int getFbNumperLine() {
        return this.mFbNumperLine;
    }

    public void setFbNumperLine(int fbNumperLine) {
        this.mFbNumperLine = fbNumperLine;
    }

    public int getHasAnimation() {
        return this.mHasAnimation;
    }

    public void setHasAnimation(int hasAnimation) {
        this.mHasAnimation = hasAnimation;
    }

    public int getAdFrequency() {
        return this.mAdFrequency;
    }

    public void setAdFrequency(int adFrequency) {
        this.mAdFrequency = adFrequency;
    }

    public int getAdfirst() {
        return this.mAdfirst;
    }

    public void setAdfirst(int mAdfirst2) {
        this.mAdfirst = mAdfirst2;
    }

    public int getAdsplit() {
        return this.mAdsplit;
    }

    public void setAdsplit(int mAdsplit2) {
        this.mAdsplit = mAdsplit2;
    }

    public int getAdcolsetype() {
        return this.mAdcolsetype;
    }

    public void setAdcolsetype(int mAdcolsetype2) {
        this.mAdcolsetype = mAdcolsetype2;
    }

    public int getOnlineAdvPositionId() {
        return this.mOnlineAdvPositionId;
    }

    public void setOnlineAdvPositionId(int onlineAdvPositionId) {
        this.mOnlineAdvPositionId = onlineAdvPositionId;
    }

    public int getFbAdvAbplan() {
        return this.mFbAdvAbplan;
    }

    public void setFbAdvAbplan(int fbAdvAbplan) {
        this.mFbAdvAbplan = fbAdvAbplan;
    }

    public int getAdMobBanner() {
        return this.mAdMobBanner;
    }

    public void setAdMobBanner(int adMobBanner) {
        this.mAdMobBanner = adMobBanner;
    }

    public int getOnlineAdvType() {
        return this.mOnlineAdvType;
    }

    public void setOnlineAdvType(int onlineAdvType) {
        this.mOnlineAdvType = onlineAdvType;
    }

    public long getDataVersion() {
        return this.mDataVersion;
    }

    public void setDataVersion(long dataVersion) {
        this.mDataVersion = dataVersion;
    }

    public int getDataType() {
        return this.mDataType;
    }

    public void setDataType(int dataType) {
        this.mDataType = dataType;
    }

    public int getLayout() {
        return this.mLayout;
    }

    public void setLayout(int layout) {
        this.mLayout = layout;
    }

    public int getPages() {
        return this.mPages;
    }

    public void setPages(int pages) {
        this.mPages = pages;
    }

    public int getPageId() {
        return this.mPageId;
    }

    public void setPageId(int pageId) {
        this.mPageId = pageId;
    }

    public int getStatisticsType() {
        return this.mStatisticsType;
    }

    public void setStatisticsType(int statisticsType) {
        this.mStatisticsType = statisticsType;
    }

    public int getClearFlag() {
        return this.mClearflag;
    }

    public void setClearFlag(int flag) {
        this.mClearflag = flag;
    }

    public List<BaseModuleInfoBean> getChildModuleList() {
        return this.mChildModuleList;
    }

    public void setChildModuleList(List<BaseModuleInfoBean> childModuleList) {
        this.mChildModuleList = childModuleList;
    }

    public List<BaseContentResourceInfoBean> getContentResourceInfoList() {
        return this.mContentResourceInfoList;
    }

    public void setContentResourceInfoList(List<BaseContentResourceInfoBean> contentResourceInfoList) {
        this.mContentResourceInfoList = contentResourceInfoList;
    }

    public void setSaveDataTime(long saveDataTime) {
        this.mSaveDataTime = saveDataTime;
    }

    public long getSaveDataTime() {
        return this.mSaveDataTime;
    }

    public void setHasShowAdUrlList(String hasShowAdUrlList) {
        this.mHasShowAdUrlList = hasShowAdUrlList;
    }

    public String getHasShowAdUrlList() {
        return this.mHasShowAdUrlList;
    }

    public void setAdSplitInner(int split) {
        this.mAdSplitInner = split;
    }

    public int getAdSplitInner() {
        return this.mAdSplitInner;
    }

    public void setClickEffect(int effect) {
        this.mClickEffect = effect;
    }

    public int getClickEffect() {
        return this.mClickEffect;
    }

    public void setChildModuleDataItemList(List<BaseModuleDataItemBean> childModuleDataItemList) {
        this.mChildModuleDataItemList = childModuleDataItemList;
    }

    public List<BaseModuleDataItemBean> getChildModuleDataItemList() {
        return this.mChildModuleDataItemList;
    }

    public int getAdCacheFlag() {
        return this.mAdCacheFlag;
    }

    public boolean isSdkOnlineAdType() {
        if (!"1".equals(String.valueOf(getAdvDataSourceType())) || getOnlineAdvPositionId() > 0 || getAdvDataSource() == 35 || getAdvDataSource() == 36) {
            return false;
        }
        return true;
    }

    public boolean isOfflineAdType() {
        if (AD_DATA_SOURCE_TYPE_OFFLINE.equals(String.valueOf(this.mAdvDataSourceType))) {
            return true;
        }
        return false;
    }

    public List<BaseAppInfoBean> getAppInfoList() {
        if (this.mContentResourceInfoList == null || this.mContentResourceInfoList.isEmpty()) {
            return null;
        }
        List<BaseAppInfoBean> appInfoList = new ArrayList<>();
        for (BaseContentResourceInfoBean contentResourceInfoBean : this.mContentResourceInfoList) {
            if (!(contentResourceInfoBean == null || contentResourceInfoBean.getAppInfoBean() == null)) {
                appInfoList.add(contentResourceInfoBean.getAppInfoBean());
            }
        }
        return appInfoList;
    }

    public static BaseModuleDataItemBean parseMainModuleJsonObject(Context context, int virtualModuleId, JSONObject mainModuleJsonObject) {
        if (mainModuleJsonObject == null || mainModuleJsonObject.length() <= 0) {
            return null;
        }
        JSONObject jsonObject = null;
        if (mainModuleJsonObject.has(String.valueOf(virtualModuleId))) {
            try {
                jsonObject = mainModuleJsonObject.getJSONObject(String.valueOf(virtualModuleId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jsonObject == null) {
            jsonObject = mainModuleJsonObject;
        }
        BaseModuleDataItemBean mainModuleDataItemBean = parseModuleJsonObject(context, virtualModuleId, jsonObject);
        if (mainModuleDataItemBean == null) {
            return null;
        }
        List<BaseModuleInfoBean> childModuleInfoList = mainModuleDataItemBean.getChildModuleList();
        BaseModuleDataItemBean moduleDataItemBean = null;
        if (childModuleInfoList != null && !childModuleInfoList.isEmpty()) {
            List<BaseModuleDataItemBean> childModuleDataItemList = new ArrayList<>();
            for (BaseModuleInfoBean childModuleInfoBean : childModuleInfoList) {
                if (childModuleInfoBean != null && mainModuleJsonObject.has(String.valueOf(childModuleInfoBean.getModuleId()))) {
                    try {
                        moduleDataItemBean = parseModuleJsonObject(context, virtualModuleId, mainModuleJsonObject.getJSONObject(String.valueOf(childModuleInfoBean.getModuleId())));
                        if (moduleDataItemBean != null) {
                            childModuleDataItemList.add(moduleDataItemBean);
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
            mainModuleDataItemBean.mChildModuleDataItemList = childModuleDataItemList;
        }
        if (mainModuleJsonObject.has(AdSdkContants.SAVE_DATA_TIME)) {
            mainModuleDataItemBean.mSaveDataTime = mainModuleJsonObject.optLong(AdSdkContants.SAVE_DATA_TIME, 0);
        }
        if (mainModuleJsonObject.has(AdSdkContants.HAS_SHOW_AD_URL_LIST)) {
            mainModuleDataItemBean.mHasShowAdUrlList = mainModuleJsonObject.optString(AdSdkContants.HAS_SHOW_AD_URL_LIST, "");
        }
        if (moduleDataItemBean == null || !LogUtils.isShowLog()) {
            return mainModuleDataItemBean;
        }
        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]BaseModuleDataItemBean.parseMainModuleJsonObject(moduleId:" + moduleDataItemBean.mModuleId + "[" + virtualModuleId + "], AdvPositionId:" + moduleDataItemBean.mAdvPositionId + ", OnlineAdvPositionId:" + moduleDataItemBean.mOnlineAdvPositionId + ", AdvDataSource:" + moduleDataItemBean.mAdvDataSource + ", FbAdvCount:" + moduleDataItemBean.mFbAdvCount + ", AdvDataSourceType:" + moduleDataItemBean.mAdvDataSourceType + ", SaveDataTime:" + moduleDataItemBean.mSaveDataTime + ", 是否为SDK广告:" + moduleDataItemBean.isSdkOnlineAdType() + ", 是否为离线广告:" + moduleDataItemBean.isOfflineAdType() + ")");
        return mainModuleDataItemBean;
    }

    public static BaseModuleDataItemBean parseModuleJsonObject(Context context, int virtualModuleId, JSONObject moduleJsonObject) {
        String[] strArr = null;
        if (moduleJsonObject == null || moduleJsonObject.length() <= 0) {
            return null;
        }
        BaseModuleDataItemBean moduleDataItemBean = new BaseModuleDataItemBean();
        moduleDataItemBean.mModuleId = moduleJsonObject.optInt("moduleId", 0);
        moduleDataItemBean.mAdvPositionId = moduleJsonObject.optInt("advpositionid", 0);
        moduleDataItemBean.mModuleName = moduleJsonObject.optString("moduleName", "");
        moduleDataItemBean.mModuleDesc = moduleJsonObject.optString("moduleDesc", "");
        moduleDataItemBean.mModuleSubTitle = moduleJsonObject.optString("moduleSubtitle", "");
        moduleDataItemBean.mBackImage = moduleJsonObject.optString("backImage", "");
        moduleDataItemBean.mBanner = moduleJsonObject.optString("banner", "");
        moduleDataItemBean.mPreview = moduleJsonObject.optString("preview", "");
        moduleDataItemBean.mIcon = moduleJsonObject.optString("icon", "");
        moduleDataItemBean.mUrl = moduleJsonObject.optString("url", "");
        moduleDataItemBean.mShowRandom = moduleJsonObject.optInt("showrandom", 0);
        moduleDataItemBean.mGoRandom = moduleJsonObject.optInt("gorandom", 0);
        moduleDataItemBean.mActType = moduleJsonObject.optInt("acttype", 0);
        moduleDataItemBean.mSequence = moduleJsonObject.optInt("sequence", 0);
        moduleDataItemBean.mPreLoadSwitch = moduleJsonObject.optInt("preloadswitch", 0);
        moduleDataItemBean.mPreLoadSwitchType = moduleJsonObject.optInt("preloadswitchtype", 0);
        moduleDataItemBean.mAdFrequency = moduleJsonObject.optInt("adfrequency", 0);
        moduleDataItemBean.mAdSplitInner = moduleJsonObject.optInt("adsplit_inner", 0);
        moduleDataItemBean.mClickEffect = moduleJsonObject.optInt("click_effect", 0);
        moduleDataItemBean.mAdfirst = moduleJsonObject.optInt("adfirst", 0);
        moduleDataItemBean.mAdsplit = moduleJsonObject.optInt("adsplit", 0);
        moduleDataItemBean.mAdcolsetype = moduleJsonObject.optInt("adcolsetype", 0);
        moduleDataItemBean.mOnlineAdvPositionId = moduleJsonObject.optInt("onlineadvpositionid", 0);
        moduleDataItemBean.mEffect = moduleJsonObject.optInt("effect", 0);
        moduleDataItemBean.mAdvDataSource = moduleJsonObject.optInt("advdatasource", 0);
        moduleDataItemBean.mAdvDataSourceExtInfoBean = BaseAdvDataSourceExtInfoBean.parseJsonObject(moduleJsonObject.optJSONObject("advdatasourceextinfo"));
        moduleDataItemBean.mAdvScene = moduleJsonObject.optInt("advscene", 0);
        moduleDataItemBean.mFbAdvCount = moduleJsonObject.optInt("fbadvcount", 0);
        moduleDataItemBean.mAdvDataSourceType = moduleJsonObject.optInt("advdatasourcetype", 0);
        String fbIds = moduleJsonObject.optString("fbid", "");
        if (!TextUtils.isEmpty(fbIds)) {
            strArr = fbIds.split("#");
        }
        moduleDataItemBean.mFbIds = strArr;
        moduleDataItemBean.mFbAdvPos = moduleJsonObject.optInt("fbadvpos", 0);
        moduleDataItemBean.mFbTabId = moduleJsonObject.optString("fbtabid", "");
        moduleDataItemBean.mFbNumperLine = moduleJsonObject.optInt("fbnumperline", 0);
        moduleDataItemBean.mHasAnimation = moduleJsonObject.optInt("hasanimation", 0);
        moduleDataItemBean.mFbAdvAbplan = moduleJsonObject.optInt("fbadvabplan", 0);
        moduleDataItemBean.mAdMobBanner = moduleJsonObject.optInt("admobbanner", 0);
        moduleDataItemBean.mOnlineAdvType = moduleJsonObject.optInt("onlineadvtype", 3);
        moduleDataItemBean.mDataVersion = (long) moduleJsonObject.optInt("dataVersion", 0);
        moduleDataItemBean.mDataType = moduleJsonObject.optInt("dataType", 1);
        moduleDataItemBean.mLayout = moduleJsonObject.optInt("layout", 0);
        moduleDataItemBean.mAdCacheFlag = moduleJsonObject.optInt("adcache_flag");
        moduleDataItemBean.mPages = moduleJsonObject.optInt("pages", 0);
        moduleDataItemBean.mPageId = moduleJsonObject.optInt("pageid", 0);
        moduleDataItemBean.mStatisticsType = moduleJsonObject.optInt("statisticstype", 0);
        moduleDataItemBean.mClearflag = moduleJsonObject.optInt("clearflag", 0);
        if (moduleDataItemBean.mDataType == 1 && moduleJsonObject.has("childmodules")) {
            moduleDataItemBean.mChildModuleList = BaseModuleInfoBean.parseJsonArray(context, moduleJsonObject.optJSONArray("childmodules"), virtualModuleId);
        }
        if (moduleJsonObject.has("contents")) {
            moduleDataItemBean.mContentResourceInfoList = BaseContentResourceInfoBean.parseJsonArray(context, moduleJsonObject.optJSONArray("contents"), virtualModuleId, moduleDataItemBean.mModuleId, moduleDataItemBean.mAdvPositionId, moduleDataItemBean.mAdvDataSource);
        }
        moduleDataItemBean.setVirtualModuleId(virtualModuleId);
        if (!LogUtils.isShowLog()) {
            return moduleDataItemBean;
        }
        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "] 广告模块控制信息::->(child, moduleId:" + moduleDataItemBean.mModuleId + "[" + virtualModuleId + "], AdvPositionId:" + moduleDataItemBean.mAdvPositionId + ", OnlineAdvPositionId:" + moduleDataItemBean.mOnlineAdvPositionId + ", AdvDataSource:" + moduleDataItemBean.mAdvDataSource + ", FbAdvCount:" + moduleDataItemBean.mFbAdvCount + ", AdvDataSourceType:" + moduleDataItemBean.mAdvDataSourceType + ", 是否为SDK广告:" + moduleDataItemBean.isSdkOnlineAdType() + ", 是否为离线广告:" + moduleDataItemBean.isOfflineAdType() + ")");
        return moduleDataItemBean;
    }

    public static boolean saveAdDataToSdcard(int virtualModuleId, JSONObject cacheDataJsonObject) {
        if (cacheDataJsonObject == null || cacheDataJsonObject.length() < 1) {
            return false;
        }
        if (!cacheDataJsonObject.has(AdSdkContants.SAVE_DATA_TIME)) {
            try {
                cacheDataJsonObject.put(AdSdkContants.SAVE_DATA_TIME, System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return FileCacheUtils.saveCacheDataToSdcard(getCacheFileName(virtualModuleId), StringUtils.toString(cacheDataJsonObject), true);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static AdModuleInfoBean getOfflineAdInfoList(Context context, int virtualModuleId, int adCount, boolean needShownFilter, BaseModuleDataItemBean currenModuleDataItemBean, JSONObject moduleDataItemJsonObj) {
        List<BaseAppInfoBean> appInfoList;
        int i;
        BaseModuleDataItemBean lastAdModuleDataItemBean;
        BaseModuleDataItemBean mainModuleDataItemBean = parseMainModuleJsonObject(context, virtualModuleId, moduleDataItemJsonObj);
        List<BaseModuleDataItemBean> childModuleDataItemList = null;
        if (mainModuleDataItemBean != null && ((childModuleDataItemList = mainModuleDataItemBean.getChildModuleDataItemList()) == null || childModuleDataItemList.isEmpty())) {
            if (childModuleDataItemList == null) {
                childModuleDataItemList = new ArrayList<>();
            }
            childModuleDataItemList.add(mainModuleDataItemBean);
        }
        if (childModuleDataItemList == null || childModuleDataItemList.isEmpty()) {
            return null;
        }
        List<BaseAppInfoBean> adAppInfoList = null;
        boolean isRequestData = true;
        BaseModuleDataItemBean offineModuleDataItemBean = null;
        Iterator i$ = childModuleDataItemList.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            BaseModuleDataItemBean childModuleDataItemBean = i$.next();
            if (currenModuleDataItemBean == null || !currenModuleDataItemBean.isOfflineAdType() || currenModuleDataItemBean.getModuleId() == childModuleDataItemBean.getModuleId()) {
                offineModuleDataItemBean = childModuleDataItemBean;
                appInfoList = childModuleDataItemBean != null ? childModuleDataItemBean.getAppInfoList() : null;
                if (appInfoList != null && !appInfoList.isEmpty()) {
                    if (childModuleDataItemBean.isOfflineAdType() || currenModuleDataItemBean == null || currenModuleDataItemBean.getModuleId() != childModuleDataItemBean.getModuleId() || (lastAdModuleDataItemBean = childModuleDataItemList.get(childModuleDataItemList.size() - 1)) == null || !lastAdModuleDataItemBean.isOfflineAdType()) {
                        String hasShowAdUrls = mainModuleDataItemBean.getHasShowAdUrlList();
                    }
                }
            }
        }
        String hasShowAdUrls2 = mainModuleDataItemBean.getHasShowAdUrlList();
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]getOfflineAdInfoList(hasShowAdUrls::->" + hasShowAdUrls2 + ")");
        }
        List<String> installAppList = new ArrayList<>();
        Iterator i$2 = appInfoList.iterator();
        while (true) {
            if (!i$2.hasNext()) {
                break;
            }
            BaseAppInfoBean appInfoBean = i$2.next();
            if (appInfoBean != null) {
                if (adAppInfoList == null) {
                    adAppInfoList = new ArrayList<>();
                }
                if (!needShownFilter || TextUtils.isEmpty(hasShowAdUrls2) || hasShowAdUrls2.indexOf(AdSdkContants.SYMBOL_DOUBLE_LINE + appInfoBean.getAdUrl() + AdSdkContants.SYMBOL_DOUBLE_LINE) < 0) {
                    if (!AppUtils.isAppExist(context, appInfoBean.getPackageName())) {
                        if (adCount > 0 && adAppInfoList.size() >= adCount) {
                            isRequestData = false;
                            break;
                        }
                        adAppInfoList.add(appInfoBean);
                    } else {
                        installAppList.add(appInfoBean.getPackageName());
                    }
                }
            }
        }
        if (childModuleDataItemList.size() != 1) {
            if (adAppInfoList != null && adAppInfoList.size() < adCount) {
                try {
                    moduleDataItemJsonObj.put(AdSdkContants.HAS_SHOW_AD_URL_LIST, "");
                    saveAdDataToSdcard(virtualModuleId, moduleDataItemJsonObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (BaseAppInfoBean appInfoBean2 : appInfoList) {
                    if (appInfoBean2 != null && !adAppInfoList.contains(appInfoBean2) && !installAppList.contains(appInfoBean2.getPackageName()) && !AppUtils.isAppExist(context, appInfoBean2.getPackageName())) {
                        adAppInfoList.add(appInfoBean2);
                        if (adCount > 0 && adAppInfoList.size() >= adCount) {
                            break;
                        }
                    }
                }
            }
            isRequestData = false;
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[vmId:" + virtualModuleId + "]getOfflineAdInfoList(onlineAd, requtstAdData:" + false + ", virtualModuleId:" + virtualModuleId + ", adCount:" + adCount + ", availableCount:" + 0 + ", returnAdCount:" + adAppInfoList.size() + ")");
            }
        } else if (LogUtils.isShowLog()) {
            StringBuilder append = new StringBuilder().append("[vmId:").append(virtualModuleId).append("]getOfflineAdInfoList(offlineAd, requtstAdData:").append(isRequestData).append(", virtualModuleId:").append(virtualModuleId).append(", adCount:").append(adCount).append(", availableCount:").append(0).append(", returnAdCount:");
            if (adAppInfoList != null) {
                i = adAppInfoList.size();
            } else {
                i = -1;
            }
            LogUtils.i("Ad_SDK", append.append(i).append(")").toString());
        }
        if (offineModuleDataItemBean == null) {
            return null;
        }
        AdModuleInfoBean adInfoBean = new AdModuleInfoBean();
        offineModuleDataItemBean.setBaseResponseBean(currenModuleDataItemBean != null ? currenModuleDataItemBean.getBaseResponseBean() : null);
        adInfoBean.setOfflineAdInfoList(context, offineModuleDataItemBean, adAppInfoList);
        adInfoBean.setIsRequestData(isRequestData);
        if (adAppInfoList == null || adAppInfoList.size() <= 0 || !LogUtils.isShowLog()) {
            return adInfoBean;
        }
        for (BaseAppInfoBean appInfo : adAppInfoList) {
            if (appInfo != null) {
                LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]return offline ad info::>(count:" + adAppInfoList.size() + "--" + adCount + ", VirtualModuleId:" + appInfo.getVirtualModuleId() + ", ModuleId:" + appInfo.getModuleId() + ", MapId:" + appInfo.getMapId() + ", packageName:" + appInfo.getPackageName() + ", Name:" + appInfo.getName() + ", availableCount:" + 0 + ", mAdvDataSourceType:" + offineModuleDataItemBean.getAdvDataSourceType() + ")");
            }
        }
        return adInfoBean;
    }

    public static boolean checkControlInfoValid(long loadDataTime) {
        if (loadDataTime <= 0 || loadDataTime > System.currentTimeMillis() - 14400000) {
            return true;
        }
        return false;
    }

    public static String getCacheFileName(int virtualModuleId) {
        return StringUtils.toString(Integer.valueOf(virtualModuleId));
    }

    public static boolean isBannerAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || 1 != moduleDataItemBean.getOnlineAdvType()) {
            return false;
        }
        return true;
    }

    public static boolean isBannerAd300_250(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || 5 != moduleDataItemBean.getOnlineAdvType()) {
            return false;
        }
        return true;
    }

    public static boolean isInterstitialAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || 2 != moduleDataItemBean.getOnlineAdvType()) {
            return false;
        }
        return true;
    }

    public static boolean isNativeAd(BaseModuleDataItemBean moduleDataItemBean) {
        if (moduleDataItemBean == null || 3 != moduleDataItemBean.getOnlineAdvType()) {
            return false;
        }
        return true;
    }

    public BaseResponseBean getBaseResponseBean() {
        return this.mBaseResponseBean;
    }

    public void setBaseResponseBean(BaseResponseBean mBaseResponseBean2) {
        this.mBaseResponseBean = mBaseResponseBean2;
    }

    public String getStatistics105Remark() {
        BaseResponseBean baseResponseBean = getBaseResponseBean();
        if (baseResponseBean != null) {
            return baseResponseBean.isNormalChannel() ? baseResponseBean.getUser() : baseResponseBean.getBuychanneltype();
        }
        return "-1";
    }
}

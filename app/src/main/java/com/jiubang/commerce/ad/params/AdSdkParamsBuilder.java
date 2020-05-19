package com.jiubang.commerce.ad.params;

import android.content.Context;
import android.text.TextUtils;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.ironscr.IronScrAd;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.sdk.AdmobAdConfig;
import com.jiubang.commerce.ad.sdk.FacebookAdConfig;
import com.jiubang.commerce.ad.sdk.MoPubAdConfig;

public class AdSdkParamsBuilder {
    static final long DEFAULT_TIMEOUT = 30000;
    public final AdSdkManager.IAdControlInterceptor mAdControlInterceptor;
    public final AdSdkManager.IAdSourceInterceptor mAdSourceInterceptor;
    public final AdmobAdConfig mAdmobAdConfig;
    public final boolean mApplyAdCache;
    public final String mBuyuserchannel;
    public final Integer mCdays;
    public final Context mContext;
    public final boolean mDetectVpn;
    public final FacebookAdConfig mFacebookAdConfig;
    public final int[] mFilterAdCacheTags;
    public final AdSet mFilterAdSourceArray;
    public final IronScrAd.IronScrAdConfig mIronScrAdConfig;
    public final boolean mIsAddFilterPackageNames;
    public final boolean mIsNeedDownloadBanner;
    public final boolean mIsNeedDownloadIcon;
    public final boolean mIsNeedPreResolve;
    public final boolean mIsPreResolveBeforeShow;
    public final boolean mIsRequestData;
    public final boolean mIsUploadAdRequestStatistic;
    public final boolean mIsUploadClientAdRequest;
    public final AdSdkManager.ILoadAdvertDataListener mLoadAdvertDataListener;
    public final MoPubAdConfig mMoPubAdConfig;
    public final boolean mNeedShownFilter;
    public final OuterAdLoader mOuterAdLoader;
    public final int mReturnAdCount;
    public final AdSdkRequestHeader.S2SParams mS2SParams;
    private int mShownCount;
    public final AdSet mSupportAdObjectTypeArray;
    public final String mTabCategory;
    public final long mTimeOut;
    public final boolean mUseThreadPool;
    public final Integer mUserFrom;
    public final int mVirtualModuleId;

    public int getShownCount() {
        return this.mShownCount;
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public AdSdkManager.IAdControlInterceptor mAdControlInterceptor;
        /* access modifiers changed from: private */
        public AdSdkManager.IAdSourceInterceptor mAdSourceInterceptor;
        /* access modifiers changed from: private */
        public AdmobAdConfig mAdmobAdConfig;
        /* access modifiers changed from: private */
        public boolean mApplyAdCache;
        /* access modifiers changed from: private */
        public String mBuyuserchannel;
        /* access modifiers changed from: private */
        public Integer mCdays;
        /* access modifiers changed from: private */
        public Context mContext;
        /* access modifiers changed from: private */
        public boolean mDetectVpn;
        /* access modifiers changed from: private */
        public FacebookAdConfig mFacebookAdConfig;
        /* access modifiers changed from: private */
        public int[] mFilterAdCacheTags;
        /* access modifiers changed from: private */
        public AdSet mFilterAdSourceArray;
        /* access modifiers changed from: private */
        public IronScrAd.IronScrAdConfig mIronScrAdConfig;
        /* access modifiers changed from: private */
        public boolean mIsAddFilterPackageNames;
        /* access modifiers changed from: private */
        public boolean mIsNeedDownloadBanner;
        /* access modifiers changed from: private */
        public boolean mIsNeedDownloadIcon;
        /* access modifiers changed from: private */
        public boolean mIsNeedPreResolve;
        /* access modifiers changed from: private */
        public boolean mIsPreResolveBeforeShow;
        /* access modifiers changed from: private */
        public boolean mIsRequestData;
        /* access modifiers changed from: private */
        public boolean mIsUploadAdRequestStatistic;
        public boolean mIsUploadClientAdRequest;
        /* access modifiers changed from: private */
        public AdSdkManager.ILoadAdvertDataListener mLoadAdvertDataListener;
        /* access modifiers changed from: private */
        public MoPubAdConfig mMoPubAdConfig;
        /* access modifiers changed from: private */
        public boolean mNeedShownFilter;
        /* access modifiers changed from: private */
        public OuterAdLoader mOuterAdLoader;
        /* access modifiers changed from: private */
        public int mReturnAdCount;
        public AdSdkRequestHeader.S2SParams mS2SParams;
        /* access modifiers changed from: private */
        public int mShownCount;
        /* access modifiers changed from: private */
        public AdSet mSupportAdObjectTypeArray;
        /* access modifiers changed from: private */
        public String mTabCategory;
        /* access modifiers changed from: private */
        public long mTimeOut;
        /* access modifiers changed from: private */
        public boolean mUseThreadPool;
        /* access modifiers changed from: private */
        public Integer mUserFrom;
        /* access modifiers changed from: private */
        public int mVirtualModuleId;

        public Builder(Context context, int virtualModuleId, String tabCategory, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
            this.mReturnAdCount = 0;
            this.mIsAddFilterPackageNames = true;
            this.mUserFrom = null;
            this.mShownCount = -1;
            this.mNeedShownFilter = true;
            this.mUseThreadPool = false;
            this.mTimeOut = AdSdkParamsBuilder.DEFAULT_TIMEOUT;
            this.mIsUploadClientAdRequest = false;
            this.mS2SParams = null;
            this.mFilterAdCacheTags = null;
            this.mApplyAdCache = false;
            this.mIsUploadAdRequestStatistic = true;
            this.mDetectVpn = true;
            this.mContext = context;
            this.mVirtualModuleId = virtualModuleId;
            this.mTabCategory = TextUtils.isEmpty(tabCategory) ? String.valueOf(virtualModuleId) : tabCategory;
            this.mLoadAdvertDataListener = loadAdvertDataListener;
        }

        public Builder(Context context, int virtualModuleId, String buyChannel, Integer userFrom, String tabCategory, AdSdkManager.ILoadAdvertDataListener loadAdvertDataListener) {
            this.mReturnAdCount = 0;
            this.mIsAddFilterPackageNames = true;
            this.mUserFrom = null;
            this.mShownCount = -1;
            this.mNeedShownFilter = true;
            this.mUseThreadPool = false;
            this.mTimeOut = AdSdkParamsBuilder.DEFAULT_TIMEOUT;
            this.mIsUploadClientAdRequest = false;
            this.mS2SParams = null;
            this.mFilterAdCacheTags = null;
            this.mApplyAdCache = false;
            this.mIsUploadAdRequestStatistic = true;
            this.mDetectVpn = true;
            this.mContext = context;
            this.mVirtualModuleId = virtualModuleId;
            this.mBuyuserchannel = buyChannel;
            this.mUserFrom = userFrom;
            this.mTabCategory = TextUtils.isEmpty(tabCategory) ? String.valueOf(virtualModuleId) : tabCategory;
            this.mLoadAdvertDataListener = loadAdvertDataListener;
        }

        private Builder() {
            this.mReturnAdCount = 0;
            this.mIsAddFilterPackageNames = true;
            this.mUserFrom = null;
            this.mShownCount = -1;
            this.mNeedShownFilter = true;
            this.mUseThreadPool = false;
            this.mTimeOut = AdSdkParamsBuilder.DEFAULT_TIMEOUT;
            this.mIsUploadClientAdRequest = false;
            this.mS2SParams = null;
            this.mFilterAdCacheTags = null;
            this.mApplyAdCache = false;
            this.mIsUploadAdRequestStatistic = true;
            this.mDetectVpn = true;
        }

        public Builder returnAdCount(int returnAdCount) {
            this.mReturnAdCount = returnAdCount;
            return this;
        }

        public Builder isNeedDownloadIcon(boolean isNeedDownloadIcon) {
            this.mIsNeedDownloadIcon = isNeedDownloadIcon;
            return this;
        }

        public Builder isNeedDownloadBanner(boolean isNeedDownloadBanner) {
            this.mIsNeedDownloadBanner = isNeedDownloadBanner;
            return this;
        }

        public Builder isNeedPreResolve(boolean isNeedPreResolve) {
            this.mIsNeedPreResolve = false;
            return this;
        }

        public Builder isPreResolveBeforeShow(boolean isPreResolveBeforeShow) {
            this.mIsPreResolveBeforeShow = isPreResolveBeforeShow;
            return this;
        }

        public Builder isRequestData(boolean isRequestData) {
            this.mIsRequestData = isRequestData;
            return this;
        }

        public Builder isAddFilterPackageNames(boolean isAddFilterPackageNames) {
            this.mIsAddFilterPackageNames = isAddFilterPackageNames;
            return this;
        }

        public Builder supportAdTypeArray(AdSet supportAdObjectTypeArray) {
            this.mSupportAdObjectTypeArray = supportAdObjectTypeArray;
            return this;
        }

        public Builder filterAdSourceArray(AdSet filterAdSourceArray) {
            this.mFilterAdSourceArray = filterAdSourceArray;
            return this;
        }

        public Builder buyuserchannel(String buyuserchannel) {
            this.mBuyuserchannel = buyuserchannel;
            return this;
        }

        public Builder userFrom(Integer userFrom) {
            this.mUserFrom = userFrom;
            return this;
        }

        public Builder cdays(Integer cdays) {
            this.mCdays = cdays;
            return this;
        }

        public Builder tabCategory(String tabCategory) {
            this.mTabCategory = tabCategory;
            return this;
        }

        public Builder shownCount(int shownCount) {
            this.mShownCount = shownCount;
            return this;
        }

        public Builder needShownFilter(boolean b) {
            this.mNeedShownFilter = b;
            return this;
        }

        public Builder adControlInterceptor(AdSdkManager.IAdControlInterceptor interceptor) {
            this.mAdControlInterceptor = interceptor;
            return this;
        }

        public Builder adSourceInterceptor(AdSdkManager.IAdSourceInterceptor interceptor) {
            this.mAdSourceInterceptor = interceptor;
            return this;
        }

        public Builder facebookAdConfig(FacebookAdConfig config) {
            this.mFacebookAdConfig = config;
            return this;
        }

        public Builder admobAdConfig(AdmobAdConfig config) {
            this.mAdmobAdConfig = config;
            return this;
        }

        public Builder ironScrAdConfig(IronScrAd.IronScrAdConfig config) {
            this.mIronScrAdConfig = config;
            return this;
        }

        public Builder moPubAdConfig(MoPubAdConfig config) {
            this.mMoPubAdConfig = config;
            return this;
        }

        public Builder useThreadPool(boolean useThreadPool) {
            this.mUseThreadPool = useThreadPool;
            return this;
        }

        public Builder fbTimeout(long timeout) {
            this.mTimeOut = Math.max(3000, timeout);
            return this;
        }

        public Builder isUploadClientAdRequest(boolean isUploadClientAdRequest) {
            this.mIsUploadClientAdRequest = isUploadClientAdRequest;
            return this;
        }

        public Builder s2SParams(AdSdkRequestHeader.S2SParams params) {
            this.mS2SParams = params;
            return this;
        }

        public Builder filterAdCacheTags(int[] filterAdCacheTags) {
            this.mFilterAdCacheTags = filterAdCacheTags;
            return this;
        }

        public Builder applyAdCache(boolean b) {
            this.mApplyAdCache = b;
            return this;
        }

        public Builder isUploadAdRequestStatistic(boolean b) {
            this.mIsUploadAdRequestStatistic = b;
            return this;
        }

        public Builder outerAdLoader(OuterAdLoader loader) {
            this.mOuterAdLoader = loader;
            return this;
        }

        public Builder detectVpn(boolean detect) {
            this.mDetectVpn = detect;
            return this;
        }

        public AdSdkParamsBuilder build() {
            return new AdSdkParamsBuilder(this);
        }
    }

    private AdSdkParamsBuilder(Builder builder) {
        this.mShownCount = -1;
        this.mContext = builder.mContext;
        this.mVirtualModuleId = builder.mVirtualModuleId;
        this.mReturnAdCount = builder.mReturnAdCount;
        this.mIsNeedDownloadIcon = builder.mIsNeedDownloadIcon;
        this.mIsNeedDownloadBanner = builder.mIsNeedDownloadBanner;
        this.mIsNeedPreResolve = builder.mIsNeedPreResolve;
        this.mIsPreResolveBeforeShow = builder.mIsPreResolveBeforeShow;
        this.mIsRequestData = builder.mIsRequestData;
        this.mIsAddFilterPackageNames = builder.mIsAddFilterPackageNames;
        this.mSupportAdObjectTypeArray = builder.mSupportAdObjectTypeArray;
        this.mFilterAdSourceArray = builder.mFilterAdSourceArray;
        this.mBuyuserchannel = builder.mBuyuserchannel;
        this.mCdays = builder.mCdays;
        this.mTabCategory = builder.mTabCategory;
        this.mShownCount = builder.mShownCount;
        this.mNeedShownFilter = builder.mNeedShownFilter;
        this.mLoadAdvertDataListener = builder.mLoadAdvertDataListener;
        this.mAdControlInterceptor = builder.mAdControlInterceptor;
        this.mAdSourceInterceptor = builder.mAdSourceInterceptor;
        this.mFacebookAdConfig = builder.mFacebookAdConfig;
        this.mAdmobAdConfig = builder.mAdmobAdConfig;
        this.mIronScrAdConfig = builder.mIronScrAdConfig;
        this.mMoPubAdConfig = builder.mMoPubAdConfig;
        this.mUseThreadPool = builder.mUseThreadPool;
        if (builder.mApplyAdCache) {
            this.mTimeOut = DEFAULT_TIMEOUT == builder.mTimeOut ? 3000 : builder.mTimeOut;
        } else {
            this.mTimeOut = builder.mTimeOut;
        }
        this.mIsUploadClientAdRequest = builder.mIsUploadClientAdRequest;
        this.mS2SParams = builder.mS2SParams;
        this.mFilterAdCacheTags = builder.mFilterAdCacheTags;
        this.mApplyAdCache = builder.mApplyAdCache;
        this.mIsUploadAdRequestStatistic = builder.mIsUploadAdRequestStatistic;
        this.mOuterAdLoader = builder.mOuterAdLoader;
        this.mUserFrom = builder.mUserFrom;
        this.mDetectVpn = builder.mDetectVpn;
    }

    public final boolean checkFilterAndSupportAdvs(BaseModuleDataItemBean moduleDataItemBean) {
        return (this.mFilterAdSourceArray == null || !this.mFilterAdSourceArray.isContain(moduleDataItemBean)) && (this.mSupportAdObjectTypeArray == null || this.mSupportAdObjectTypeArray.isContain(moduleDataItemBean));
    }

    public final boolean commonLoadCondition(BaseModuleDataItemBean moduleDataItemBean) {
        return checkFilterAndSupportAdvs(moduleDataItemBean) && (this.mAdSourceInterceptor == null || this.mAdSourceInterceptor.continueLoadingAd(moduleDataItemBean)) && (this.mFilterAdCacheTags == null || !AdModuleInfoBean.isContainValue(this.mFilterAdCacheTags, moduleDataItemBean.getAdCacheFlag()));
    }

    public static Builder createEmptyBuilder(String buyChannel, Integer cdays, Integer userFrom) {
        return new Builder().buyuserchannel(buyChannel).cdays(cdays).userFrom(userFrom);
    }
}

package com.jiubang.commerce.ad.tricks.fb;

import android.content.Context;
import com.facebook.ads.NativeAd;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.abtest.ABTestManager;
import com.jiubang.commerce.ad.abtest.AbBean;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.http.bean.BaseIntellAdInfoBean;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdWrapper;
import java.util.ArrayList;
import java.util.List;

public class FbNativeAdTrick implements ABTestManager.IABTestConfigListener {
    private static FbNativeAdTrick sInstance = null;
    private Context mContext;

    public enum Plot {
        Default(true, false, false, false, false),
        A(false, true, false, false, false),
        B(false, false, true, false, true),
        C(false, false, true, true, false);
        
        /* access modifiers changed from: private */
        public int mAdPos;
        private final boolean mShouldClick;
        private final boolean mShouldDefault;
        private final boolean mShouldIntercept;
        private final boolean mShouldRequest;
        private final boolean mShouldTransfer;

        private Plot(boolean shouldDefault, boolean shouldClick, boolean shouldRequest, boolean shouldTransfer, boolean shouldIntercept) {
            this.mShouldDefault = shouldDefault;
            this.mShouldClick = shouldClick;
            this.mShouldRequest = shouldRequest;
            this.mShouldTransfer = shouldTransfer;
            this.mShouldIntercept = shouldIntercept;
        }

        public int getAdPos() {
            return this.mAdPos;
        }

        public boolean isShouldDefault() {
            return this.mShouldDefault;
        }

        public boolean isShouldClick() {
            return this.mShouldClick;
        }

        public boolean isShouldRequest() {
            return this.mShouldRequest;
        }

        public boolean isShouldTransfer() {
            return this.mShouldTransfer;
        }

        public boolean isShouldIntercept() {
            return this.mShouldIntercept;
        }

        public static Plot fromValue(int value) {
            if (values().length <= value || value < 0) {
                return Default;
            }
            return values()[value];
        }
    }

    private FbNativeAdTrick(Context context) {
        this.mContext = context.getApplicationContext();
        ABTestManager.getInstance(this.mContext).register(this);
    }

    public static FbNativeAdTrick getInstance(Context context) {
        if (sInstance == null) {
            synchronized (FbNativeAdTrick.class) {
                if (sInstance == null) {
                    sInstance = new FbNativeAdTrick(context);
                }
            }
        }
        return sInstance;
    }

    public void onABTestUpdate() {
    }

    public Plot getPlot(int requestAdId) {
        AbBean abBean = ABTestManager.getInstance(this.mContext).getAbBean("91");
        int abTestId = abBean != null ? abBean.getPlotId() : Plot.Default.ordinal();
        int adPos = abBean != null ? abBean.getAdPos() : -1;
        boolean requestIdContained = abBean != null ? abBean.requestIdContained(requestAdId) : false;
        Plot result = Plot.Default;
        Plot plot = Plot.fromValue(abTestId);
        int unused = plot.mAdPos = adPos;
        switch (plot) {
            case A:
            case B:
            case C:
                if (requestIdContained && plot.mAdPos > 0) {
                    result = plot;
                    break;
                }
        }
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "FbNativeAdTrick plot=:" + result.toString());
        }
        return result;
    }

    public static AdInfoBean transfer2AdInfoBean(int virtualModuleId, int adPos, NativeAd ad, BaseIntellAdInfoBean intellInfoBean) {
        String adTitle = ad.getAdTitle();
        String adSubTitle = ad.getAdSubtitle();
        String adBody = ad.getAdBody();
        String adIcon = ad.getAdIcon() != null ? ad.getAdIcon().getUrl() : "";
        String adCover = ad.getAdCoverImage() != null ? ad.getAdCoverImage().getUrl() : "";
        double adStarRating = ad.getAdStarRating() != null ? ad.getAdStarRating().getValue() : 0.0d;
        AdInfoBean adInfoBean = AdInfoBean.transferSingleIntellAdInfoBean(intellInfoBean);
        adInfoBean.setVirtualModuleId(virtualModuleId);
        adInfoBean.setAdId(adPos);
        adInfoBean.setName(adTitle);
        adInfoBean.setRemdMsg(adBody);
        adInfoBean.setDetail(adBody);
        adInfoBean.setIcon(adIcon);
        adInfoBean.setBanner(adCover);
        adInfoBean.setBannerDescribe(adBody);
        adInfoBean.setBannerTitle(adSubTitle);
        adInfoBean.setPreview(adCover);
        adInfoBean.setDownloadCountStr("" + adStarRating);
        adInfoBean.setScore("" + adStarRating);
        return adInfoBean;
    }

    public static List<AdInfoBean> adWrapper2AdInfoBeans(List<SdkAdSourceAdWrapper> wrappers) {
        ArrayList<AdInfoBean> beans = new ArrayList<>();
        for (SdkAdSourceAdWrapper wrapper : wrappers) {
            Object adObject = wrapper.getAdObject();
            if (adObject != null && (adObject instanceof AdInfoBean)) {
                beans.add((AdInfoBean) adObject);
            }
        }
        return beans;
    }
}

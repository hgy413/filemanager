package com.jiubang.commerce.ad.params;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.manager.AdControlManager;
import com.jiubang.commerce.ad.sdk.bean.SdkAdSourceAdInfoBean;
import com.jiubang.commerce.statistics.AdSdkOperationStatistic;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.TimeOutGuard;

public abstract class OuterAdLoader {
    private BaseModuleDataItemBean mBaseModuleDataItemBean;

    public abstract long getTimeOut();

    public abstract void loadAd(OuterSdkAdSourceListener outerSdkAdSourceListener);

    private final void setBaseModuleDataItemBean(BaseModuleDataItemBean bean) {
        this.mBaseModuleDataItemBean = bean;
    }

    public BaseModuleDataItemBean getAdSourceInfo() {
        return this.mBaseModuleDataItemBean;
    }

    public final int getAdSourceType() {
        return this.mBaseModuleDataItemBean.getAdvDataSource();
    }

    public final String getAdRequestId() {
        String[] faceBookIds = this.mBaseModuleDataItemBean.getFbIds();
        if (faceBookIds == null || faceBookIds.length <= 0) {
            return null;
        }
        return faceBookIds[0];
    }

    public static void ProcessUnKnownAdSource(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean bean, AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener) {
        OuterAdLoader adLoader = adSdkParams.mOuterAdLoader;
        adLoader.setBaseModuleDataItemBean(bean);
        final Context context = adSdkParams.mContext;
        TimeOutGuard timeOutGurad = new TimeOutGuard();
        long timeOut = Math.max(1, adLoader.getTimeOut());
        final String adRequestId = adLoader.getAdRequestId();
        final long startTime = System.currentTimeMillis();
        final BaseModuleDataItemBean baseModuleDataItemBean = bean;
        final AdSdkParamsBuilder adSdkParamsBuilder = adSdkParams;
        final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener2 = sdkAdSourceRequestListener;
        timeOutGurad.start(timeOut, new TimeOutGuard.TimeOutTask() {
            public void onTimeOut() {
                LogUtils.e("Ad_SDK", "[vmId:" + baseModuleDataItemBean.getVirtualModuleId() + "]ProcessUnKnownAdSource:time out");
                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context, adRequestId, adSdkParamsBuilder.mTabCategory, -2, baseModuleDataItemBean, System.currentTimeMillis() - startTime, adSdkParamsBuilder);
                sdkAdSourceRequestListener2.onFinish((SdkAdSourceAdInfoBean) null);
            }
        }, (Object) null);
        AdSdkOperationStatistic.uploadAdRequestStatistic(context, adRequestId, adSdkParams.mTabCategory, bean, adSdkParams);
        final Context context2 = context;
        final String str = adRequestId;
        final AdSdkParamsBuilder adSdkParamsBuilder2 = adSdkParams;
        final BaseModuleDataItemBean baseModuleDataItemBean2 = bean;
        final long j = startTime;
        final AdControlManager.SdkAdSourceRequestListener sdkAdSourceRequestListener3 = sdkAdSourceRequestListener;
        adLoader.loadAd(new OuterSdkAdSourceListener(new AdControlManager.SdkAdSourceRequestListener() {
            public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, (sdkAdSourceAdInfoBean == null || sdkAdSourceAdInfoBean.getAdViewList() == null) ? 0 : sdkAdSourceAdInfoBean.getAdViewList().size(), baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                sdkAdSourceRequestListener3.onFinish(sdkAdSourceAdInfoBean);
            }

            public void onException(int statusCode) {
                AdSdkOperationStatistic.uploadAdRequestResultStatistic(context2, str, adSdkParamsBuilder2.mTabCategory, -1, baseModuleDataItemBean2, System.currentTimeMillis() - j, adSdkParamsBuilder2);
                sdkAdSourceRequestListener3.onException(statusCode);
            }

            public void onAdShowed(Object adViewObj) {
                sdkAdSourceRequestListener3.onAdShowed(adViewObj);
            }

            public void onAdClicked(Object adViewObj) {
                sdkAdSourceRequestListener3.onAdClicked(adViewObj);
            }

            public void onAdClosed(Object adViewObj) {
                sdkAdSourceRequestListener3.onAdClosed(adViewObj);
            }
        }, timeOutGurad));
    }

    public static boolean canProcess(AdSdkParamsBuilder adSdkParams, BaseModuleDataItemBean bean) {
        OuterAdLoader adLoader = adSdkParams.mOuterAdLoader;
        if (adLoader == null) {
            return false;
        }
        adLoader.setBaseModuleDataItemBean(bean);
        if (!StringUtils.isEmpty(adLoader.getAdRequestId())) {
            return true;
        }
        return false;
    }

    public static class OuterSdkAdSourceListener {
        private AdControlManager.SdkAdSourceRequestListener mOriginal;
        private boolean mResultRetrived = false;
        private TimeOutGuard mTimeOutGuard;

        public OuterSdkAdSourceListener(AdControlManager.SdkAdSourceRequestListener listener, TimeOutGuard guard) {
            this.mOriginal = listener;
            this.mTimeOutGuard = guard;
        }

        private synchronized void refreshResult(boolean ret) {
            this.mResultRetrived = ret;
        }

        private synchronized boolean isResultRetrived() {
            return this.mResultRetrived;
        }

        public void onFinish(SdkAdSourceAdInfoBean sdkAdSourceAdInfoBean) {
            if (!isResultRetrived() && !this.mTimeOutGuard.hadTimeOut()) {
                refreshResult(true);
                this.mTimeOutGuard.cancel();
                this.mOriginal.onFinish(sdkAdSourceAdInfoBean);
            }
        }

        public void onException(int statusCode) {
            if (!isResultRetrived() && !this.mTimeOutGuard.hadTimeOut()) {
                refreshResult(true);
                this.mTimeOutGuard.cancel();
                this.mOriginal.onException(statusCode);
            }
        }

        public void onAdShowed(Object adViewObj) {
            this.mOriginal.onAdShowed(adViewObj);
        }

        public void onAdClicked(Object adViewObj) {
            this.mOriginal.onAdClicked(adViewObj);
        }

        public void onAdClosed(Object adViewObj) {
            this.mOriginal.onAdClosed(adViewObj);
        }
    }
}

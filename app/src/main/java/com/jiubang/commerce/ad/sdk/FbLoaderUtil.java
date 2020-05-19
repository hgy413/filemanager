package com.jiubang.commerce.ad.sdk;

import android.content.Context;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.abtest.ABTestManager;
import com.jiubang.commerce.ad.sdk.SdkAdSourceListener;
import java.util.Random;

class FbLoaderUtil {
    static final String TAG = "FBTest";

    FbLoaderUtil() {
    }

    private static boolean getTestResult(Context context) {
        double num = new Random().nextDouble();
        LogUtils.d(TAG, "r=" + num);
        boolean ret = num <= ABTestManager.getInstance(context).getFrequency();
        if (ret) {
            LogUtils.d(TAG, "test Satisfy");
        } else {
            LogUtils.d(TAG, "test notSatisfy");
        }
        return ret;
    }

    public static void testLoad(Context context, final SdkAdSourceListener.FBSingleNativeAdListener listener) {
        if (getTestResult(context)) {
            NativeAd nativeAd = new NativeAd(context, ABTestManager.getInstance(context).getFBId());
            nativeAd.setAdListener(new SdkAdSourceListener.FBSingleNativeAdListener() {
                public void onError(Ad ad, AdError adError) {
                    if (!hasOnErrorCalled()) {
                        LogUtils.d(FbLoaderUtil.TAG, "test onError");
                        setOnErrorCalled();
                        listener.onError(ad, adError);
                    }
                }

                public void onAdLoaded(Ad ad) {
                    LogUtils.d(FbLoaderUtil.TAG, "test onAdLoaded");
                    listener.onAdLoaded(ad);
                }

                public void onAdClicked(Ad ad) {
                    LogUtils.d(FbLoaderUtil.TAG, "test onAdClicked");
                    listener.onAdClicked(ad);
                }
            });
            nativeAd.loadAd();
            return;
        }
        listener.onError((Ad) null, (AdError) null);
    }
}

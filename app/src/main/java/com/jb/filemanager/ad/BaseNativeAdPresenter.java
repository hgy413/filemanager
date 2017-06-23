package com.jb.filemanager.ad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.jb.filemanager.BuildConfig;

/**
 * Created by bill wang on 16/11/2.
 * 原生广告管理类
 */

public abstract class BaseNativeAdPresenter {
    protected Context mContext;
    protected NativeAd mNativeAd;
    protected ViewGroup mAdContainer;
    protected View mAdView;

    protected String mPlacementIdStr;

    public BaseNativeAdPresenter(Context context, String placementIdStr) {
        mContext = context;
        mPlacementIdStr = placementIdStr;
    }

    protected void setAdContainer(ViewGroup adContainer) {
        mAdContainer = adContainer;
    }

    // 加载facebook广告
    public void loadAd() {
        Context ctx = mContext.getApplicationContext();
        mNativeAd = new NativeAd(ctx, mPlacementIdStr);
        mNativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() != 1001) { // 1001是广告没有展示
                    destroyAd();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                showAd(ad);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // When testing your app with Facebook's ad units
        // you must specify the device hashed ID to ensure the delivery of test ads,
        // add the following code before loading an ad:
        if (BuildConfig.DEBUG) {
            AdSettings.addTestDevice("41db1deec3dad987b78f37108dfe7a5d"); // htc
        }

        mNativeAd.loadAd();
    }

    public void destroyAd() {
        if (mNativeAd != null) {
            try {
                mNativeAd.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 如果ad为NativeAd 就构建广告展示view
     */
    protected abstract void showAd(Ad ad);
}

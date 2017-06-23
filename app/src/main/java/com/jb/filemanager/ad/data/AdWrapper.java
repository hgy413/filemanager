package com.jb.filemanager.ad.data;

import com.facebook.ads.NativeAd;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.loopme.LoopMeBanner;

/**
 * ZBoost广告模型<br>
 * V1.17版本以后所欲ZBoost广告数据模型<br>
 * V1.20增加了PubNativeAd<br>
 * V1.22增加了LoopMeBanner<br>
 *
 * @author wangying
 */
public class AdWrapper {

    /**
     * FB的NativedAd
     */
    public NativeAd mFbNativeAd = null;

    /**
     * FB的InterstitialAd
     */
    public com.facebook.ads.InterstitialAd mFbInterstitialAd = null;


    /**
     * 应用分发广告数据
     */
    public AdInfoBean mAppAdInfoBean = null;

    /**
     * PubNativeAd(为FB广告替代源)
     */
    public PubNativeAd mPubNativeAd = null;

    /**
     * LoopMeBanner视频广告
     */
    public LoopMeBanner mLoopMeBanner = null;

    /**
     * Admob:NativeContentAd
     */
    public com.google.android.gms.ads.formats.NativeContentAd mAdmobNativeContentAd = null;

    /**
     * Admob:NativeAppInstallAd
     */
    public com.google.android.gms.ads.formats.NativeAppInstallAd mAdmobNativeAppInstallAd = null;

    /**
     * Admob:InterstitialAd
     */
    public com.google.android.gms.ads.InterstitialAd mAdmobInterstitialAd = null;

    /**
     * MoPub(Twitter):Banner
     * need to destroy the ad when activity/fragment destroy by call mMoPubView.destroy();
     */
    public com.mopub.mobileads.MoPubView mMoPubView = null;

    /**
     * MoPub(Twitter):Banner
     * need to destroy the ad when activity/fragment destroy by call mMoPubView.destroy();
     */
    public com.mopub.nativeads.NativeAd mMoPubNative = null;

    /**
     * 判断是否为应用分发广告
     *
     * @return result
     */
    public boolean isAppCenterAd() {
        return mAppAdInfoBean != null;
    }

    /**
     * 判断是否为Facebook广告数据
     *
     * @return result
     */
    public boolean isFBNativeAd() {
        return mFbNativeAd != null;
    }

    /**
     * 判断是否为Facebook全屏广告数据
     *
     * @return result
     */
    public boolean isFBInterstitialAd() {
        return mFbInterstitialAd != null;
    }

    /**
     * 是否是直接拿到的PubNativeAd
     *
     * @return result
     */
    public boolean isPubNativeAd() {
        return mPubNativeAd != null;
    }

    /**
     * 判断是否为LoopMeBanner广告
     *
     * @return result
     */
    public boolean isLoopMeBanner() {
        return mLoopMeBanner != null;
    }

    /**
     * 判断是否为Admob:NativeContentAd广告
     *
     * @return result
     */
    public boolean isAdmobNativeContentAd() {
        return mAdmobNativeContentAd != null;
    }


    /**
     * 判断是否为Admob：NativeAppInstallAd
     *
     * @return result
     */
    public boolean isAdmobNativeAppInstallAd() {
        return mAdmobNativeAppInstallAd != null;
    }

    /**
     * 判断是否为Admob：InterstitialAd
     *
     * @return result
     */
    public boolean isAdmobInterstitialAd() {
        return mAdmobInterstitialAd != null;
    }

    /**
     * 判断是否为MoPub(Twitter) Banner广告
     *
     * @return result
     */
    public boolean isMoPubBanner() {
        return mMoPubView != null;
    }

    /**
     * 判断是否为MoPub(Twitter) Native广告
     *
     * @return result
     */
    public boolean isMopubNative() {
        return mMoPubNative != null;
    }
}

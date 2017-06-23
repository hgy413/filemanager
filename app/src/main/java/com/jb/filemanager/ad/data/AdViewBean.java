package com.jb.filemanager.ad.data;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.jb.filemanager.ad.AdType;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.mopub.mobileads.MoPubView;

/**
 * 广告视图Bean
 * @author chenhewen
 *
 */
public class AdViewBean {

	public boolean isFbNativeAd() {
		return mAdType == AdType.TYPE_FB_NATIVE;
	}
	public boolean isAppCenterAd() {
		return mAdType == AdType.TYPE_APP_CENTER;
	}
	public boolean isPubNativeAd() {
		return mAdType == AdType.TYPE_PUB_NATIVE;
	}
	public boolean isAdmobNativeInsallAd() {
		return mAdType == AdType.TYPE_ADMOB_NATIVE_INSTALL;
	}

	public boolean isAdmobNativeContentAd() {
		return mAdType == AdType.TYPE_ADMOB_NATIVE_CONTENT;
	}

	public boolean isAdmobInterstitialAd() {
		return mAdType == AdType.TYPE_ADMOB_INTERSTITIAL;
	}

	public boolean isMoPubBannerAd() {
		return mAdType == AdType.TYPE_MOPUB_BANNER;
	}

	public boolean isMoPubNative() {
		return mAdType == AdType.TYPE_MOPUB_NATIVE;
	}

	public boolean isFbInterstitialAd() {
		return mAdType == AdType.TYPE_FACEBOOK_INTERSTITIAL;
	}

	/**
	 * [别看晕!] 这个HashCode不是当前广告类的HashCode, 是具体类型广告的HashCode.
	 */
	private int mHashCode;
	/**
	 * 广告类型
	 */
	private int mAdType;
	/**
	 *
	 */
	private int mEntranceId;
	/**
	 * Facebook 广告Bean
	 */
	private NativeAd mFbAdBean;

    /**
     * Facebook Interstitial广告Bean
     */
    private com.facebook.ads.InterstitialAd mFbInterstitialAd;


    /**
	 * PubNative 广告Bean
	 */
	private PubNativeAd mPubAdBean;

	/**
	 * AppCenter 广告Bean
	 */
	private AdInfoBean mAcAdBean;
	/**
	 *AdmobNative install 广告Bean
	 */
	private NativeAppInstallAd mAdmobNativeInstallAd;
	/**
	 * AdmobNative Content 广告Bean
	 */
	private NativeContentAd mAdmobNativeContentAd;
	/**
	 * Admob Interstitial 广告Bean
	 */
	private InterstitialAd mAdmobInterstitialAd;

	/**
	 * MoPub(Twitter) Banner 广告View;
	 */
	private MoPubView mMoPubView;

	/**
	 * MoPub(Twitter) Native 广告;
	 */
	private com.mopub.nativeads.NativeAd mMoPubNative;

	/**
	 * appcenter非要我们帮他们统计, 这应该是他们自己的业务, 唉~
	 */
	private AdModuleInfoBean mAdModuleInfoBean;

	public int getHashCode() {
		return mHashCode;
	}

	public void setHashCode(int hashCode) {
		mHashCode = hashCode;
	}

	public int getEntranceId() {
		return mEntranceId;
	}

	public void setEntranceId(int entranceId) {
		mEntranceId = entranceId;
	}

	public int getAdType() {
		return mAdType;
	}

	public void setAdType(int adType) {
		mAdType = adType;
	}

	public NativeAd getFbAdBean() {
		return mFbAdBean;
	}

	public void setFbAdBean(NativeAd fbAdBean) {
		mFbAdBean = fbAdBean;
	}

	public PubNativeAd getPubAdBean() {
		return mPubAdBean;
	}

	public void setPubAdBean(PubNativeAd pubAdBean) {
		mPubAdBean = pubAdBean;
	}

	public AdInfoBean getAcAdBean() {
		return mAcAdBean;
	}

	public void setAcAdBean(AdInfoBean acAdBean) {

		mAcAdBean = acAdBean;
	}

	public NativeAppInstallAd getAdmobNativeInstallAd() {
		return mAdmobNativeInstallAd;
	}

	public void setAdmobNativeInstallAd(NativeAppInstallAd admobNativeInstallAd) {
		mAdmobNativeInstallAd = admobNativeInstallAd;
	}

	public NativeContentAd getAdmobNativeContentAd() {
		return mAdmobNativeContentAd;
	}

	public void setAdmobNativeContentAd(NativeContentAd admobNativeContentAd) {
		mAdmobNativeContentAd = admobNativeContentAd;
	}

	public AdModuleInfoBean getAdModuleInfoBean() {
		return mAdModuleInfoBean;
	}
	public void setAdModuleInfoBean(AdModuleInfoBean adModuleInfoBean) {
		mAdModuleInfoBean = adModuleInfoBean;
	}

	public void setAdmobInterstitialAd(InterstitialAd interstitialAd) {
		mAdmobInterstitialAd = interstitialAd;
	}

	public InterstitialAd getAdmobInterstitialAd() {
		return mAdmobInterstitialAd;
	}

	public void setMoPubBannerAd(MoPubView moPubView) {
		mMoPubView = moPubView;
	}

	public MoPubView getMoPubBannerAd() {
		return mMoPubView;
	}

	public void setMoPubNativeAd(com.mopub.nativeads.NativeAd moPubNativeAd) {
		mMoPubNative = moPubNativeAd;
	}

	public com.mopub.nativeads.NativeAd getMoPubNativeAd() {
		return mMoPubNative;
	}


	public void setFbInterstitialAd(com.facebook.ads.InterstitialAd interstitialAd) {
        mFbInterstitialAd = interstitialAd;
    }

    public com.facebook.ads.InterstitialAd getFbInterstitialAd() {
        return mFbInterstitialAd;
    }
}

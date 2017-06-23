package com.jb.filemanager.ad.data;


import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import com.jb.filemanager.ad.AdType;
import com.jb.filemanager.util.Logger;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.mopub.mobileads.MoPubView;

/**
 * AdManager 和 展示层之间的适配器
 * @author chenhewen
 *
 */
public class AdAdapter {

	protected static final String TAG = "AdAdapter";

	public static AdViewBean boxData(AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {

		AdViewBean returnBean = new AdViewBean();
		if (wrapperBean.isFBNativeAd()) {
			boxFBNativeAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isFBInterstitialAd()) {
			boxFBInterstitialAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isPubNativeAd()) {
			boxPubNativeAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isAppCenterAd()) {
			boxAppCenterAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isAdmobNativeAppInstallAd()) {
			boxAdmobNativeInstallAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isAdmobNativeContentAd()) {
			boxAdmobNativeContentAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isAdmobInterstitialAd()) {
			boxAdmobInterstitialAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isMoPubBanner()) {
			boxMoPubBannerAd(returnBean, wrapperBean, adModuleInfoBean);
		} else if (wrapperBean.isMopubNative()) {
			boxMoPubNativeAd(returnBean, wrapperBean, adModuleInfoBean);
		}
		Logger.d(TAG, getAdTypeNameStr(wrapperBean) + " 广告加载成功!");

		return returnBean;
	}


	public static String getAdTypeNameStr(AdWrapper wrapperBean) {

		if (wrapperBean.isAppCenterAd()) {
			return "App Center";
		} else if (wrapperBean.isFBNativeAd()) {
			return "FB Native";
		} else if (wrapperBean.isPubNativeAd()) {
			return "Pub Native";
		} else if (wrapperBean.isAdmobNativeAppInstallAd()) {
			return "Admob Native Install";
		} else if (wrapperBean.isAdmobNativeContentAd()) {
			return "Admob Native Content";
		} else if (wrapperBean.isAdmobInterstitialAd()) {
			return "Admob Interstitial";
		} else if (wrapperBean.isFBInterstitialAd()) {
			return "FB Interstitial";
		} else if (wrapperBean.isMopubNative()) {
			return "MoPub native";
		} else if (wrapperBean.isLoopMeBanner()) {
			return "Loop me banner";
		}

		return "Unknown type";
	}


	/**
	 * 组装 Facebook 广告数据
	 * @param returnBean
	 * @param wrapperBean
	 */
	private static void boxFBNativeAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		returnBean.setAdType(AdType.TYPE_FB_NATIVE);
		returnBean.setFbAdBean(wrapperBean.mFbNativeAd);
		returnBean.setHashCode(wrapperBean.mFbNativeAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	private static void boxAdmobNativeInstallAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		returnBean.setAdType(AdType.TYPE_ADMOB_NATIVE_INSTALL);
		NativeAppInstallAd installAd = wrapperBean.mAdmobNativeAppInstallAd;
		returnBean.setAdmobNativeInstallAd(installAd);
		returnBean.setHashCode(installAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	private static void boxAdmobNativeContentAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		returnBean.setAdType(AdType.TYPE_ADMOB_NATIVE_CONTENT);
		NativeContentAd contentAd = wrapperBean.mAdmobNativeContentAd;
		returnBean.setAdmobNativeContentAd(contentAd);
		returnBean.setHashCode(contentAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	private static void boxAdmobInterstitialAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		returnBean.setAdType(AdType.TYPE_ADMOB_INTERSTITIAL);
		InterstitialAd interstitialAd = wrapperBean.mAdmobInterstitialAd;
		returnBean.setAdmobInterstitialAd(interstitialAd);
		returnBean.setHashCode(interstitialAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	private static void boxFBInterstitialAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		returnBean.setAdType(AdType.TYPE_FACEBOOK_INTERSTITIAL);
		com.facebook.ads.InterstitialAd interstitialAd = wrapperBean.mFbInterstitialAd;
		returnBean.setFbInterstitialAd(interstitialAd);
		returnBean.setHashCode(interstitialAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	/**
	 * 组装 应用分发 广告数据
	 * @param returnBean
	 * @param wrapperBean
	 */
	private static void boxAppCenterAd(final AdViewBean returnBean, AdWrapper wrapperBean, AdModuleInfoBean adModuleInfoBean) {
		AdInfoBean appBean = wrapperBean.mAppAdInfoBean;
		returnBean.setAdType(AdType.TYPE_APP_CENTER);
		returnBean.setAcAdBean(appBean);
		returnBean.setHashCode(appBean.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
	}

	private static void boxPubNativeAd(AdViewBean returnBean, AdWrapper wrapperBean,
			AdModuleInfoBean adModuleInfoBean) {
		PubNativeAd pub = wrapperBean.mPubNativeAd;
		returnBean.setAdType(AdType.TYPE_PUB_NATIVE);
		returnBean.setHashCode(wrapperBean.mPubNativeAd.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
		returnBean.setPubAdBean(pub);
	}

	private static void boxMoPubBannerAd(AdViewBean returnBean,
										 AdWrapper wrapperBean,
										 AdModuleInfoBean adModuleInfoBean) {
		MoPubView moPubView = wrapperBean.mMoPubView;
		returnBean.setAdType(AdType.TYPE_MOPUB_BANNER);
		returnBean.setHashCode(wrapperBean.mMoPubView.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
		returnBean.setMoPubBannerAd(moPubView);
	}

	private static void boxMoPubNativeAd(AdViewBean returnBean,
										 AdWrapper wrapperBean,
										 AdModuleInfoBean adModuleInfoBean) {
		com.mopub.nativeads.NativeAd nativeAd = wrapperBean.mMoPubNative;
		returnBean.setAdType(AdType.TYPE_MOPUB_NATIVE);
		returnBean.setHashCode(wrapperBean.mMoPubNative.hashCode());
		returnBean.setAdModuleInfoBean(adModuleInfoBean);
		returnBean.setMoPubNativeAd(nativeAd);
	}
}

package com.jb.filemanager.ad.event;

import com.jb.filemanager.ad.data.AdWrapper;
import com.jb.filemanager.ad.data.OneRequestAds;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;

import java.util.ArrayList;

/**
 * 广告获取完毕事件<br>
 *
 * @author wangying
 *
 */
public class NormalAdLoadCompleteEvent {

	/**
	 * 请求一次广告返回的数据
	 */
	private OneRequestAds mZBoostOneRequestAds;

	public NormalAdLoadCompleteEvent(OneRequestAds zBoostOneRequestAds) {
		mZBoostOneRequestAds = zBoostOneRequestAds;
	}

	/**
	 * 获取广告数据
	 *
	 * @return
	 */
	public ArrayList<AdWrapper> getAdsList() {
		return mZBoostOneRequestAds.getZboostAdwappers();
	}

	/**
	 * 获取应用分发的广告原始数据，主要用于应用分发统计需要,可能为null
	 *
	 * @return 可能为null
	 */
	public AdModuleInfoBean getAdModuleInfoBean() {
		return mZBoostOneRequestAds.getAdModuleInfoBean();
	}

	/**
	 * 是否为FBAd
	 *
	 * @return
	 */
	public boolean isFBAd() {
		return mZBoostOneRequestAds.isFbAd();
	}

	/**
	 * 是否为应用分发的离线广告
	 *
	 * @return
	 */
	public boolean isAppAd() {
		return mZBoostOneRequestAds.isAppAd();
	}

	/**
	 * 是否为直接获取的PubNativeAd
	 *
	 * @return
	 */
	public boolean isPubNativeAd() {
		return mZBoostOneRequestAds.isPubNativeAd();
	}

	/**
	 * 是否为LoopMeBannerAd
	 *
	 * @return
	 */
	public boolean isLoopMeBannerAd() {
		return mZBoostOneRequestAds.isLoopMeBannerAd();
	}

	/**
	 * 是否为AdmobNativeContentAd
	 * @return
	 */
	public boolean isAdmobNativeContentAd() {
		return mZBoostOneRequestAds.isAdmobNativeContentAd();
	}

	/**
	 * 是否为AdmobNativeAppInstallAd
	 * @return
	 */
	public boolean isAdmobNativeAppInstallAd() {
		return mZBoostOneRequestAds.isAdmobNativeAppInstallAd();
	}

	/**
	 * 是否为AdmobInterstitialAd
	 * @return
	 */
	public boolean isAdmobInterstitialAd() {
		return mZBoostOneRequestAds.isAdmobInterstitialAd();
	}

	/**
	 * 判断是否是本界面所需要的广告数据
	 *
	 * @param entrance
	 *            参考ZBoostAdManager入口参数　 <br>
	 *            {@link AdEntry#ENTRANCE_CLEAN},<br>
	 *            {@link AdEntry#ENTRANCE_BOOST},<br>
	 *            {@link AdEntry#ENTRANCE_TOAST_NOTIFICATION},<br>
	 *            {@link AdEntry#ENTRANCE_TOAST_SHORTCUT},<br>
	 *            {@link AdEntry#ENTRANCE_TOAST_FLOATVIEW},<br>
	 *            {@link AdEntry#ENTRANCE_CPU},<br>
	 *            V1.22 {@link AdEntry#ENTRANCE_GAME_BOOST_FOLDER},<br>
	 *            V1.22 {@link AdEntry#ENTRANCE_GAME_BOOST_IN_APP},<br>
	 * @return
	 */
	public boolean isRightAdData(int entrance) {
		return mZBoostOneRequestAds.getEntrance() == entrance;
	}

	/**
	 * 获取广告入口
	 * @return
     */
	public int getEntrance() {
		return mZBoostOneRequestAds.getEntrance();
	}

}

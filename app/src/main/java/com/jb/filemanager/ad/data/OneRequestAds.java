package com.jb.filemanager.ad.data;

import com.jiubang.commerce.ad.bean.AdModuleInfoBean;

import java.util.ArrayList;

/**
 * ZBoost请求一次广告返回的数据模型<br>
 * 除了游戏加速外事件<br>
 *
 * @author wangying
 *
 */
public class OneRequestAds {

	/**
	 * 广告入口
	 */
	private int mEntrance;
	/**
	 * 一次请求获得所有解析出来的广告数据
	 */
	private ArrayList<AdWrapper> mZBoostAdWrappers;
	/**
	 * 应用分发SDK获取的原始数据，如果不是使用应用分发SDK获得的广告数据，则此数据单元为null
	 */
	private AdModuleInfoBean mAdModuleInfoBean = null;

	public OneRequestAds(int entrance,
						 ArrayList<AdWrapper> zBoostAdWrappers) {
		mEntrance = entrance;
		mZBoostAdWrappers = zBoostAdWrappers;
	}

	/**
	 * 获取已经解析的广告数据
	 *
	 * @return
	 */
	public ArrayList<AdWrapper> getZboostAdwappers() {
		return mZBoostAdWrappers;
	}

	/**
	 * 获取本次广告入口，用于界面接收到广告数据检验是否为本界面的广告数据
	 *
	 * @return 广告入口　 <br>
	 */
	public int getEntrance() {
		return mEntrance;
	}

	/**
	 * 设置通过应用分发SDK获得原始广告数据
	 *
	 * @param adModuleInfoBean
	 */
	public void setAdModuleInfoBean(AdModuleInfoBean adModuleInfoBean) {
		mAdModuleInfoBean = adModuleInfoBean;
	}

	/**
	 * 获得通过应用分发SDK获得原始广告数据
	 *
	 * @return 返回可能为null
	 */
	public AdModuleInfoBean getAdModuleInfoBean() {
		return mAdModuleInfoBean;
	}

	/**
	 * 本次请求到得广告数据是否为应用分发离线广告
	 *
	 * @return
	 */
	public boolean isAppAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isAppCenterAd();
		}
		return false;
	}

	/**
	 * 本次请求到得广告数据是否为Facebook的NativeAd
	 *
	 * @return
	 */
	public boolean isFbAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isFBNativeAd();
		}
		return false;
	}

	/**
	 * 本次请求得到的广告数据是否为客户端直接请求的得到的PubNativeAd
	 *
	 * @return
	 */
	public boolean isPubNativeAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isPubNativeAd();
		}
		return false;
	}

	/**
	 * 本次请求得到的广告是否为LoopMeBannerAd
	 *
	 * @return
	 */
	public boolean isLoopMeBannerAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isLoopMeBanner();
		}
		return false;
	}

	/**
	 * 本次请求得到的广告是否为AdmobNativeContentAd
	 * @return
	 */
	public boolean isAdmobNativeContentAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isAdmobNativeContentAd();
		}
		return false;
	}

	/**
	 * 本次请求得到的广告是否为AdmobNativeAppInstallAd
	 * @return
	 */
	public boolean isAdmobNativeAppInstallAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isAdmobNativeAppInstallAd();
		}
		return false;
	}

	/**
	 * 本次请求得到的广告是否为AdmobNativeAppInstallAd
	 * @return
	 */
	public boolean isAdmobInterstitialAd() {
		if (hasAdData()) {
			return mZBoostAdWrappers.get(0).isAdmobInterstitialAd();
		}
		return false;
	}

	/**
	 * 判断是否有有效的广告数据
	 *
	 * @return
	 */
	private boolean hasAdData() {
		return mZBoostAdWrappers != null && mZBoostAdWrappers.size() >= 1;
	}

}

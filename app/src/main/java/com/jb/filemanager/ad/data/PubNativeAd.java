package com.jb.filemanager.ad.data;


import com.jb.filemanager.util.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ZBoost使用的PubnativeAd广告数据模型
 * 
 * @author wangying
 *
 */
public class PubNativeAd {

	// 点击跳转链接
	private String mClickUrl;
	// 下发数据描述
	private String mDescription;
	// icon下载链接
	private String mIconUrl;
	// banner下载链接
	private String mBannerUrl;
	// 应用名
	private String mAppName;
	// 应用评分
	private float mAppScoreRate;
	// 服务器确认url
	private String mImpressionBeaconUrl;

	public String getClickUrl() {
		return mClickUrl;
	}

	public void setClickUrl(String clickUrl) {
		mClickUrl = clickUrl;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setIconUrl(String iconUrl) {
		mIconUrl = iconUrl;
	}

	public String getBannerUrl() {
		return mBannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		mBannerUrl = bannerUrl;
	}

	public String getAppName() {
		return mAppName;
	}

	public void setAppName(String appName) {
		mAppName = appName;
	}

	public float getAppScoreRate() {
		return mAppScoreRate;
	}

	public void setAppScoreRate(float appScoreRate) {
		mAppScoreRate = appScoreRate;
	}

	public String getImpressionBeaconUrl() {
		return mImpressionBeaconUrl;
	}

	public void setImpressionBeaconUrl(String impressionBeaconUrl) {
		mImpressionBeaconUrl = impressionBeaconUrl;
	}

	/**
	 * 展示1s钟后向服务器发送确认消息
	 * 
	 * @param url
	 */
	public void sendImpressionToServer(String url) {
		URL impressionUrl;
		Logger.i("PUBIMPRESSION", "impressUrl =" + url);
		HttpURLConnection httpURLConnection = null;
		try {
			impressionUrl = new URL(url);
			httpURLConnection = (HttpURLConnection) impressionUrl
					.openConnection();
			httpURLConnection.connect();
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String result = "";
			String inputLine = "";
			while ((inputLine = bufferedReader.readLine()) != null) {
				result += inputLine + "\n";
			}
			Logger.i("PUBIMPRESSION", "impressresult = " + result);
		} catch (Exception e) {
			Logger.i("PubNativeAd", "在向PubNative服务器确认时发生错误：" + e.toString());
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	@Override
	public String toString() {
		return mAppName + ";" + mAppScoreRate + ";" + mDescription
				+ "icon_url=" + mIconUrl + "服务器确认url=" + mImpressionBeaconUrl;
	}

}

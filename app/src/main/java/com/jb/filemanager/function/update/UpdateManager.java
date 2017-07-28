package com.jb.filemanager.function.update;

import android.content.Context;
import android.support.v4.BuildConfig;


import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileManagerUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.device.Machine;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 
 * @author wangying
 * 
 */
public class UpdateManager {

	public static final String SERVICE_URL = "http://version.api.goforandroid.com/api/v1/product/versions?"; // 版本更新请求的地址

	public static final int FILEMANAER_ID = 1454; // 产品ID

	public static final int UPDATE_WAY_FORCE = 1; 		// 强制升级
	public static final int UPDATE_WAY_NORMAL = 2; 		// 正常提示升级
	public static final int UPDATE_WAY_SYS_NOTIFY = 2; 	// 系统提示
	public static final int UPDATE_WAY_OTHER = 4; 		// 其他

	public static final int NO_DATA = 99; // 没有数据

	public static final long ONE_DAY = BuildConfig.DEBUG ? 0 : 24 * 60 * 60 * 1000; // 一天

	/**
	 * SharedPrefernce中的字段
	 */
	public static final String LAST_CHECK_TIME = "last_check_time";

	public static final String UPDATE_APP_NAME = "update_app_name";
	public static final String UPDATE_VERSION_NAME = "update_version_name";
	public static final String UPDATE_VERSION_NUMBER = "update_version_number";
	public static final String UPDATE_VERSION_DETAIL = "update_version_detail";
	public static final String UPDATE_VERSION_LOG = "update_version_log";
	public static final String UPDATE_WAY = "update_way";
	public static final String UPDATE_GP_URL = "update_gp_url";
	public static final String UPDATE_CHANNEL = "update_channel";
	public static final String UPDATE_VERSION_LANG = "update_version_lang";

	public static final String UPDATE_VERSION_LATER = "update_version_later";
	public static final String UPDATE_VERSION_LATER_TIME = "update_version_later_time";
	public static final String UPDATE_VERSION_CANCEL = "update_version_cancel";

	private int mCounts = 0;
	private Context mContext;

	private SharedPreferencesManager mSPM = SharedPreferencesManager.getInstance(mContext);
	private ArrayList<VersionInfo> mVersionInfos = new ArrayList<>();

	public UpdateManager() {
		mContext = TheApplication.getAppContext();
	}

	/**
	 * 联网进行版本检查
	 */
	public void checkVersion() {
		new CheckVersionThread().start();
	}

	/**
	 *
	 * @author wangying
	 *
	 */
	class CheckVersionThread extends Thread {

		@Override
		public void run() {
			// 获取服务器数据
			String data = getVersionInfos();
			if (data == null) {
				Logger.i("UP", " 获取服务器资源失败！");

			} else if (data.trim().length() <= 0) {
				Logger.i("UP", "获取服务器数据为『』");
			} else {
				parseCheckVersionByteData(data);
			}

		}

	}

	private String getVersionInfos() {

		int count = 0;

		if (!Machine.isNetworkOK(mContext)) {
			return null;
		} else {
			DefaultHttpClient client = null;
			InputStream in = null;
			ByteArrayOutputStream bos = null;
			String str = null;
			while (count < 3) {
				count++; // 确保3次重连
				try {
					HttpGet httpRequest = new HttpGet(createUrlString());
					client = new DefaultHttpClient();
					HttpResponse httpResponse = client.execute(httpRequest);
					/* 若状态码为200 ok */
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						count = 3;
						// 更新检查时间
						mSPM.commitLong(LAST_CHECK_TIME,
								System.currentTimeMillis());
						Logger.i("UP", "code=200");
						Logger.i("UP", "counts=" + count);
						/* 读 */
						in = httpResponse.getEntity().getContent();
						bos = new ByteArrayOutputStream();
						int ch;
						byte[] resp = new byte[64];
						while ((ch = in.read(resp)) != -1) {
							bos.write(resp, 0, ch);
						}
						bos.flush();
						String httpResult = new String(bos.toByteArray(),
								"utf-8");
						str = httpResult;
					}
				} catch (Exception e) {
					Logger.e("UP", "Http error");
					Logger.i("UP", e.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (bos != null) {
						try {
							bos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (client != null) {
						try {
							client.getConnectionManager().shutdown();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
			return str;
		}

	}

	private String createUrlString() {
		Locale locale = Locale.getDefault();
		return SERVICE_URL + "product_id=" + FILEMANAER_ID + "&version_number=" + FileManagerUtil.getVersionCode(mContext) + "&country=" + locale.getCountry() + "&lang=" + locale.getLanguage();
	}

	/**
	 * 分析版本控制请求后服务器返回的版本信息,在返回的代码为200时调用
	 *
	 * @param Data
	 *            返回的信息
	 */
	private void parseCheckVersionByteData(String Data) {

		Logger.i("UP", "Data = " + Data);

		VersionInfo versioninfo;

		String countMark = "###";
		stringNumbers(Data, countMark);

		int firstIndexNumber = 0;
		int secondInexNumber = Data.indexOf(countMark);

		for (int i = 0; i < mCounts; i++) {
			Logger.i("UP", "firstIndexNumber=" + firstIndexNumber);
			Logger.i("UP", "secondInexNumber=" + secondInexNumber);
			Logger.i("UP",
					"str=" + Data.substring(firstIndexNumber, secondInexNumber));

			versioninfo = parseOneVersionInfo(Data.substring(firstIndexNumber,
					secondInexNumber));
			Logger.i("UP", "解析了一个VersionInfo");
			if (versioninfo != null) {
				mVersionInfos.add(versioninfo);
			}
			firstIndexNumber = secondInexNumber + 3;
			secondInexNumber = Data.indexOf(countMark, firstIndexNumber);

		}
		Logger.i("UP", "str=" + Data.substring(firstIndexNumber, Data.length()));
		versioninfo = parseOneVersionInfo(Data.substring(firstIndexNumber,
				Data.length()));
		if (versioninfo != null) {
			mVersionInfos.add(versioninfo);
		}

		if (mVersionInfos.size() >= 1) {
			checkToLocalVersion();
		}

	}

	private int stringNumbers(String str, String mark) {

		if (!str.contains(mark)) {
			return 0;
		} else if (str.contains(mark)) {
			mCounts++;
			stringNumbers(str.substring(str.indexOf(mark) + 3), mark);
			return mCounts;
		}

		return 0;
	}

	/**
	 * 解析每个版本的数据，无效数据直接返回null
	 *
	 * @param str data
	 * @return version info
	 */
	private VersionInfo parseOneVersionInfo(String str) {

		try {
			JSONObject jsonObject = new JSONObject(str);
			VersionInfo versionInfo = new VersionInfo();
			versionInfo.setVersionName(jsonObject.optString("version_name"));
			versionInfo.setChannel(jsonObject.optInt("channel"));
			versionInfo.setLanguage(jsonObject.optString("lang"));
			versionInfo.setUpdateWay(jsonObject.optInt("suggest"));
			versionInfo.setGa(jsonObject.optString("url"));
			versionInfo.setUpdateLog(jsonObject.optString("update_log"));
			versionInfo.setVersionDetail(jsonObject.optString("detail"));
			versionInfo.setVersionCode(jsonObject.optInt("version_number"));
			return versionInfo;
		} catch (JSONException e) {
			Logger.i("UP", "解析单个版本信息时发生错误！");
			return null;
		}
	}

	private void checkToLocalVersion() {

		Logger.i("UP", "checkToLocalVersion begin!");

		Locale locale = Locale.getDefault();

		VersionInfo enMatch = null;
		VersionInfo languageMatch = null;
		VersionInfo showVersionInfo = null;

		for (VersionInfo versionInfo : mVersionInfos) {
			// 判断是否为最新版本信息
			String languageAndCountry = locale.getLanguage() + "-" + locale.getCountry().toLowerCase();
			if (versionInfo.getLanguage().equals("en")) {
				if (mSPM.getInt(UPDATE_VERSION_NUMBER, 0) < versionInfo.getVersionCode()) {
					enMatch = versionInfo;
				}
			}

			if (versionInfo.getLanguage().equals(locale.getLanguage()) || versionInfo.getLanguage().equals(languageAndCountry)) {
				// 有新的最新版本
				if (mSPM.getInt(UPDATE_VERSION_NUMBER, 0) < versionInfo.getVersionCode()) {
					languageMatch = versionInfo;
					break;
				}
			}
		}


		if (languageMatch != null) {
			showVersionInfo = languageMatch;
		} else if (enMatch != null) {
			showVersionInfo = enMatch;
		}

		if (showVersionInfo != null) {
			mSPM.commitString(UPDATE_VERSION_NAME, showVersionInfo.getVersionName());
			mSPM.commitInt(UPDATE_VERSION_NUMBER, showVersionInfo.getVersionCode());
			mSPM.commitString(UPDATE_VERSION_DETAIL, showVersionInfo.getVersionDetail());
			mSPM.commitString(UPDATE_VERSION_LOG, showVersionInfo.getUpdateLog());
			mSPM.commitInt(UPDATE_WAY, showVersionInfo.getUpdateWay());
			mSPM.commitString(UPDATE_GP_URL, showVersionInfo.getGa());
			mSPM.commitInt(UPDATE_CHANNEL, showVersionInfo.getChannel());
			mSPM.commitString(UPDATE_VERSION_LANG, showVersionInfo.getLanguage());

			mSPM.commitBoolean(UPDATE_VERSION_LATER, false);
			mSPM.commitBoolean(UPDATE_VERSION_CANCEL, false);
			mSPM.commitLong(UPDATE_VERSION_LATER_TIME, 0);
		}
	}

	public static boolean isNeedToCheckVersion() {
		boolean result = false;
		long lastLaunchTime = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getLong(UpdateManager.LAST_CHECK_TIME, 0);
		if (AppUtils.isAppExist(TheApplication.getAppContext(), Const.GP_PACKAGE)
				&& (System.currentTimeMillis() - lastLaunchTime) >= UpdateManager.ONE_DAY) {
			result = true;
		}
		return result;
	}

	public static boolean needDialogShow() {
		return /*LauncherModel.getInstance().getSharedPreferencesManager().isLoadDone() &&*/ needDialogShowByUpdateWay() && needDialogShowByVersion() && needDialogShowByButton();
	}

	private static boolean needDialogShowByVersion() {
		int nowVersionCode = FileManagerUtil.getVersionCode(TheApplication.getAppContext());
		int newVersionCode = SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getInt(UpdateManager.UPDATE_VERSION_NUMBER, 0);
		return newVersionCode > nowVersionCode;
	}

	private static boolean needDialogShowByButton() {
		return !SharedPreferencesManager.getInstance(TheApplication.getAppContext())
				.getBoolean(UpdateManager.UPDATE_VERSION_CANCEL, false)
				&& (System.currentTimeMillis() - SharedPreferencesManager.getInstance(TheApplication.getAppContext())
				.getLong(UpdateManager.UPDATE_VERSION_LATER_TIME, 0)) >= UpdateManager.ONE_DAY
				&& Machine.isNetworkOK(TheApplication.getAppContext());
	}

	private static boolean needDialogShowByUpdateWay() {
		// 目前全都需要显示
		return true;
	}

	/**
	 *
	 * @author wangying
	 *
	 */
	class VersionInfo {

		private String mVersionName = null; 	// 版本名
		private int mVersionCode = 0; 			// 版本号
		private String mGa = null; 				// GA连接，获取的是最新版本的内的GA下载链接
		private String mVersionDetail = null; 	// 版本描述
		private String mUpdateLog = null; 		// 升级log
		private String mLanguage = null; 		// 语言
		private int mChannel = 0;			 	// 渠道
		private int mUpdateWay = 99; 			// 升级方式

		public String getVersionName() {
			return mVersionName;
		}

		public void setVersionName(String mVersionName) {
			this.mVersionName = mVersionName;
		}

		public int getVersionCode() {
			return mVersionCode;
		}

		public void setVersionCode(int versionCode) {
			mVersionCode = versionCode;
		}

		public int getUpdateWay() {
			return mUpdateWay;
		}

		public void setUpdateWay(int updateWay) {
			mUpdateWay = updateWay;
		}

		public String getGa() {
			return mGa;
		}

		public void setGa(String mGa) {
			this.mGa = mGa;
		}

		public String getVersionDetail() {
			return mVersionDetail;
		}

		public void setVersionDetail(String detail) {
			this.mVersionDetail = detail;
		}

		public String getLanguage() {
			return mLanguage;
		}

		public void setLanguage(String mLanguage) {
			this.mLanguage = mLanguage;
		}

		public int getChannel() {
			return mChannel;
		}

		public void setChannel(int channel) {
			mChannel = channel;
		}

		public void setUpdateLog(String log) {
			mUpdateLog = log;
		}

		public String getUpdateLog() {
			return mUpdateLog;
		}
	}

}

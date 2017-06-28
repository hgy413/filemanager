package com.jb.filemanager.function.applock.manager;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

public class AppLockerCenter {

	private static AppLockerCenter sAppLockerCenter = null;
	/**
	 * 数据层
	 */
	private AppLockerDataManager mAppLockerDataManager = null;		// 主要数据接口
	/**
	 * 业务层
	 */
	private LockerReceiverManager mLockerReceiverManager = null;	// 监听广播的业务
	private LockerServiceManager mLockerServiceManager = null;		// 业务核心，应用锁服务
	private LockerMonitorManager mLockerMonitorManager = null;      //栈顶应用监视器


	private AppLockerCenter() {
		mLockerMonitorManager = LockerMonitorManager.getInstance();
		mAppLockerDataManager = AppLockerDataManager.getInstance();
		mLockerReceiverManager = LockerReceiverManager.getInstance();
		mLockerServiceManager = LockerServiceManager.getInstance();
		//当没有使用这个功能 则将对应文件夹的所有图片删除掉
		if (!SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_APP_LOCK_ENABLE, false)) {
			AntiPeepFileUtil.deleteAllFile();
		}
	}
	
	public static AppLockerCenter getInstance() {
		if (sAppLockerCenter == null) {
			sAppLockerCenter = new AppLockerCenter();
		}
		return sAppLockerCenter;
	}

	public void onDestory() {
		mAppLockerDataManager.onDestory();
		mLockerReceiverManager.onDestory();
		mLockerServiceManager.onDestory();
	}
}

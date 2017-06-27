package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;

/**
 * app升级信息完成Event com.gto.zero.zboost.eventbus.event.AppUpdateEvent
 * 
 * @author wangying <br/>
 *         create at 2015-1-26 上午10:11:36
 */
public class AppUpdateEvent {

	private AppItemInfo mAppItemInfo;

	public AppUpdateEvent(AppItemInfo appItemInfo) {
		mAppItemInfo = appItemInfo;
	}

	public AppItemInfo getAppItemInfo() {
		return mAppItemInfo;
	}

}

package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;

/**
 * app安装数据更新完成Event com.gto.zero.zboost.eventbus.event.AppInstallEvent
 *
 * @author wangying <br/>
 *         create at 2015-1-26 上午10:10:16
 */
public class AppInstallEvent {

	private AppItemInfo mAppItemInfo;

	public AppInstallEvent(AppItemInfo appItemInfo) {
		mAppItemInfo = appItemInfo;
	}

	public AppItemInfo getAppItemInfo() {
		return mAppItemInfo;
	}

}

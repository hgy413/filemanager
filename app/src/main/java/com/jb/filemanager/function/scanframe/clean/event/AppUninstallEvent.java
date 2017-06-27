package com.jb.filemanager.function.scanframe.clean.event;

/**
 * app卸载数据更新完成Event
 * com.gto.zero.zboost.eventbus.event.AppUninstallEvent
 * @author wangying <br/>
 * create at 2015-1-26 上午10:10:34
 */
public class AppUninstallEvent {

	private String mPackageNameString;
	
	public AppUninstallEvent(String packageName) {
		mPackageNameString = packageName;
	}
	
	public String getPackageNameString() {
		return mPackageNameString;
	}
	
}

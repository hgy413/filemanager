package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 
 * <br>类描述: 用来发送系统应用变化的.比如enable/disable的事件
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2015年2月27日]
 */
public class PackageChangedEvent {
	
	private String mPackageName;

	public PackageChangedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * 变化的程序的包名<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}
}

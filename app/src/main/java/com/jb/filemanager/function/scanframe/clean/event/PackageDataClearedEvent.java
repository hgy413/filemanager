package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 有程序被清除数据完成后发出的事件<br>
 * 会使用{@code EventBus.post(Object)}发出<br>
 */
public class PackageDataClearedEvent {

	private String mPackageName;

	public PackageDataClearedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * 被清除数据的程序的包名.<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}

}

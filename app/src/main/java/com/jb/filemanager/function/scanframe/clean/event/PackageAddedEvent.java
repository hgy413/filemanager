package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 有程序安装完成后发出的事件<br>
 * 会使用{@code EventBus.post(Object)}发出<br>
 */
public class PackageAddedEvent {

	private String mPackageName;

	public PackageAddedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * 安装的程序的包名.<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}

}

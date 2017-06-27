package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 有程序替换安装(升/降级安装)完成后发出的事件<br>
 * 会使用{@code EventBus.post(Object)}发出<br>
 */
public class PackageReplacedEvent {

	private String mPackageName;

	public PackageReplacedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * 替换安装的程序的包名.<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}

}

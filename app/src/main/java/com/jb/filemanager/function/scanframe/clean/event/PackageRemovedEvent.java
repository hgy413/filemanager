package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 有程序卸载完成后发出的事件<br>
 * 会使用{@code EventBus.post(Object)}发出<br>
 */
public class PackageRemovedEvent {

	private String mPackageName;

	public PackageRemovedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * 卸载的程序的包名.<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}

}

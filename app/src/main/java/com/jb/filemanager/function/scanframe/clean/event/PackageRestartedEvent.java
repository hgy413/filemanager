package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 当程序Force Close的时候会发出该Event
 * @author chenhewen
 */
public class PackageRestartedEvent {
	
	private String mPackageName;

	public PackageRestartedEvent(String packageName) {
		mPackageName = packageName;
	}

	/**
	 * Force Close的包名.<br>
	 * @return
	 */
	public String getPackageName() {
		return mPackageName;
	}
}

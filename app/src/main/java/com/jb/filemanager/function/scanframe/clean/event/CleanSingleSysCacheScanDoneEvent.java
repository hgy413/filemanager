package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 单个系统缓存的扫描事件
 * 
 * @author chenbenbin
 * 
 */
public class CleanSingleSysCacheScanDoneEvent {
	private String mPackageName;
	private long mSize;

	public CleanSingleSysCacheScanDoneEvent(String packageName, long size) {
		super();
		mPackageName = packageName;
		mSize = size;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public void setPackageName(String packageName) {
		mPackageName = packageName;
	}

	public long getSize() {
		return mSize;
	}

	public void setSize(long size) {
		mSize = size;
	}

	@Override
	public String toString() {
		return "CleanSingleAppCacheScanDoneEvent [PackageName=" + mPackageName
				+ ", Size=" + mSize + "]";
	}
}

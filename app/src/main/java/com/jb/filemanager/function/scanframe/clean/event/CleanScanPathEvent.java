package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.clean.CleanConstants;

/**
 * 清理功能：扫描路径改变事件
 * 
 * @author chenbenbin
 * 
 */
public enum CleanScanPathEvent {
	SDCard, Residue, SysCache;

	/**
	 * 当前扫描路径
	 */
	private String mPath;
	/**
	 * 上次时间
	 */
	private long mLastTime;

	public void setPath(String path) {
		mPath = path;
	}

	public String getPath() {
		return mPath;
	}

	public long getLastTime() {
		return mLastTime;
	}

	public void setLastTime(long lastTime) {
		mLastTime = lastTime;
	}

	public boolean isSendTime() {
		long curTime = System.currentTimeMillis();
		if (curTime - mLastTime > CleanConstants.EVENT_INTERVAL) {
			mLastTime = curTime;
			return true;
		}
		return false;
	}
}

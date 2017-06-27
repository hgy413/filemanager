package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 清理模块的数据库初始化事件
 * 
 * @author chenbenbin
 * 
 */
public enum CleanDBDataInitDoneEvent {
	RESIDUE, APP_CACHE, AD;
	private boolean mIsDone = false;

	public boolean isDone() {
		return mIsDone;
	}

	public void setIsDone(boolean isDone) {
		mIsDone = isDone;
	}

	public static boolean isAllDone() {
		boolean done = true;
		for (CleanDBDataInitDoneEvent event : CleanDBDataInitDoneEvent.values()) {
			done &= event.isDone();
		}
		return done;
	}

	public boolean isResidue() {
		return this.equals(RESIDUE);
	}
	
	public boolean isAd() {
		return this.equals(AD);
	}

	public boolean isAppCache() {
		return this.equals(APP_CACHE);
	}

}

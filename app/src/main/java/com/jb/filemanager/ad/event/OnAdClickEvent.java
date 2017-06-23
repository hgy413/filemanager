package com.jb.filemanager.ad.event;

/**
 * 广告点击事件
 * @author chenhewen
 *
 */
public class OnAdClickEvent {

	int mAdType;

	int mEntrance;

	int mHashCode;


	public OnAdClickEvent(int adType, int entrance, int hashCode) {
		mAdType = adType;
		mEntrance = entrance;
		mHashCode = hashCode;
	}

	public int getAdType() {
		return mAdType;
	}

	public void setAdType(int adType) {
		mAdType = adType;
	}

	public int getEntrance() {
		return mEntrance;
	}

	public void setEntrance(int entrance) {
		mEntrance = entrance;
	}

	public int getHashCode() {
		return mHashCode;
	}

	public void setHashCode(int hashCode) {
		mHashCode = hashCode;
	}
}

package com.jb.filemanager.function.applock.event;

import android.content.ComponentName;

/**
 * 当要去锁上某个应用时发出（此时要展示锁，等）
 * @author zhanghuijun
 *
 */
public class AppLockerLockAppEvent {
	private ComponentName mComponentName;

	public AppLockerLockAppEvent(ComponentName mComponentName) {
		this.mComponentName = mComponentName;
	}

	public ComponentName getComponentName() {
		return mComponentName;
	}
	
}

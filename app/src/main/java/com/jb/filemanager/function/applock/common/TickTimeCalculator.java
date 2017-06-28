package com.jb.filemanager.function.applock.common;

import android.os.SystemClock;

/**
 * 嘀嗒时间计算器<br>
 * 
 * @author laojiale
 *
 */
public class TickTimeCalculator {

	/**
	 * in ms
	 */
	private final long mTickInterval;

	private long mLastTickTime;
	private long mNextTickIntervalTime;
	private long mPassTime;

	/**
	 * TickTimeCalculator
	 * 
	 * @param tickInterval
	 *            (in ms)
	 */
	public TickTimeCalculator(long tickInterval) {
		mTickInterval = tickInterval;
	}

	public void tick() {
		if (mLastTickTime > 0) {
			mPassTime = SystemClock.elapsedRealtime() - mLastTickTime;
			mNextTickIntervalTime = mTickInterval - mPassTime;
		} else {
			mNextTickIntervalTime = 0;
		}
		if (mNextTickIntervalTime < 0) {
			mNextTickIntervalTime = 0;
		}
		if (mNextTickIntervalTime > mTickInterval) {
			mNextTickIntervalTime = mTickInterval;
		}
		mLastTickTime = SystemClock.elapsedRealtime();
	}

	public long getPassTime() {
		return mPassTime;
	}

	public long getNextTickIntervalTime() {
		return mNextTickIntervalTime;
	}

	public void reset() {
		mPassTime = 0;
		mLastTickTime = 0;
		mNextTickIntervalTime = 0;
	}

}

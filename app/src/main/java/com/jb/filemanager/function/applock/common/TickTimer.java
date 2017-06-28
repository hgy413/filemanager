package com.jb.filemanager.function.applock.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * TickTimer<br>
 * 
 * @author laojiale
 *
 */
public class TickTimer {

	private final TickTimeCalculator mTickTimeCalculator;

	/**
	 * boolean representing if the timer was cancelled
	 */
	private volatile boolean mCancelled = true;

	private static final int MSG = 1;

	private final Handler mHandler;

	private final List<TickTimerListener> mTickTimerListeners = new ArrayList<TickTimerListener>();
	private final List<TickTimerListener> mTempTickTimerListeners = new ArrayList<TickTimerListener>();

	private final Object mLock = new Object();

	/**
	 * TickHandler
	 */
	private class TickHandler extends Handler {

		public TickHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mCancelled) {
				return;
			}
			long nextTickIntervalTime;
			synchronized (mLock) {
				mTickTimeCalculator.tick();
				final long passTime = mTickTimeCalculator.getPassTime();
				// onTick
				onTick(passTime);
				nextTickIntervalTime = mTickTimeCalculator
						.getNextTickIntervalTime();
			}
			sendMessageDelayed(obtainMessage(MSG), nextTickIntervalTime);
		}
	};

	/**
	 * TickTimer
	 * 
	 * @param looper
	 *            the looper that run the onTick
	 * @param tickInterval
	 */
	public TickTimer(Looper looper, long tickInterval) {
		mTickTimeCalculator = new TickTimeCalculator(tickInterval);
		mHandler = new TickHandler(looper);
	}

	public final void start() {
		if (!mCancelled) {
			return;
		}
		mCancelled = false;
		synchronized (mLock) {
			mTickTimeCalculator.reset();
		}
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
	}

	public final void stop() {
		if (mCancelled) {
			return;
		}
		mCancelled = true;
		mHandler.removeMessages(MSG);
	}

	public void addListener(TickTimerListener l) {
		if (l == null) {
			return;
		}
		synchronized (mLock) {
			if (!mTickTimerListeners.contains(l)) {
				mTickTimerListeners.add(l);
			}
		}
	}

	public void removeListener(TickTimerListener l) {
		if (l == null) {
			return;
		}
		synchronized (mLock) {
			if (mTickTimerListeners.contains(l)) {
				mTickTimerListeners.remove(l);
			}
		}
	}

	private void onTick(long passTime) {
		mTempTickTimerListeners.addAll(mTickTimerListeners);
		for (TickTimerListener tickTimerListener : mTempTickTimerListeners) {
			tickTimerListener.onTick(passTime);
		}
		mTempTickTimerListeners.clear();
	}

	/**
	 * TickTimerListener
	 */
	public interface TickTimerListener {
		public void onTick(long passTime);
	}

}

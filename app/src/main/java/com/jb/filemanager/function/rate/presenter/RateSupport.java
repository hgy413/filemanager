package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

/**
 * Created by nieyh on 2016/9/7.
 */
public class RateSupport implements RateContract.Support {

	private SharedPreferencesManager mSharedPreferencesManager;

	public RateSupport() {
		this.mSharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
	}

	@Override
	public boolean isRateSuccess() {
		return mSharedPreferencesManager.getBoolean(IPreferencesIds.KEY_RATE_SUCCESS, false);
	}

	@Override
	public boolean isShortTimeExtGp() {
		return mSharedPreferencesManager.getBoolean(IPreferencesIds.KEY_EXIT_GP_SHORT_TIME, false);
	}

	@Override
	public void commitShortTimeExtGp(boolean is) {
		mSharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_EXIT_GP_SHORT_TIME, is);
	}

	@Override
	public void commitRateSuccess() {
		mSharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_RATE_SUCCESS, true);
	}

	@Override
	public long getFirstInstallTime() {
		return mSharedPreferencesManager.getLong(IPreferencesIds.KEY_FIRST_INSTALL_TIME, System.currentTimeMillis());
	}

	@Override
	public long getExtGpTime() {
		return mSharedPreferencesManager.getLong(IPreferencesIds.KEY_LAST_TIME, System.currentTimeMillis());
	}

	@Override
	public void setExtGpTime(long time) {
		mSharedPreferencesManager.commitLong(IPreferencesIds.KEY_LAST_TIME, System.currentTimeMillis());
	}

	@Override
	public boolean isClickDialogNo() {
		return mSharedPreferencesManager.getBoolean(IPreferencesIds.KEY_IS_POP, false);
	}

	@Override
	public void commitClickDialogNo() {
		mSharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_IS_POP, true);
	}

	@Override
	public int getRateAppearTimes() {
		return mSharedPreferencesManager.getInt(IPreferencesIds.KEY_APPEAR_TIMES, 0);
	}

	@Override
	public void commitRateAppearTimes(int times) {
		mSharedPreferencesManager.commitInt(IPreferencesIds.KEY_APPEAR_TIMES, times);
	}

	@Override
	public void release() {
		mSharedPreferencesManager = null;
	}

}

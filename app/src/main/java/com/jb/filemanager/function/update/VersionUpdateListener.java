package com.jb.filemanager.function.update;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.ui.dialog.ConfirmCommonDialog;

/**
 * 
 * @author wangying
 * 
 */
public class VersionUpdateListener implements ConfirmCommonDialog.OnConfirmDetailListener {

	SharedPreferencesManager mSharedPreferencesManager;
	Context mContext;
	Activity mActivity;

	public VersionUpdateListener(Context context) {
		mSharedPreferencesManager = SharedPreferencesManager.getInstance(context);
		mContext = context;
		mActivity = (Activity) context;
	}

	@Override
	public void onConfirm() {

		if (mSharedPreferencesManager.getInt(UpdateManager.UPDATE_WAY,
				UpdateManager.UPDATE_WAY_NORMAL) != 1) {
			mSharedPreferencesManager.commitBoolean(
					UpdateManager.UPDATE_VERSION_LATER, true);

			mSharedPreferencesManager.commitLong(
					UpdateManager.UPDATE_VERSION_LATER_TIME,
					System.currentTimeMillis());
		}

		Uri uri = Uri.parse(mSharedPreferencesManager
				.getString(UpdateManager.UPDATE_GP_URL,
						"market://details?id=com.jb.filemanager"));
		Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
		marketIntent.setPackage("com.android.vending");
		marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(marketIntent);
		} catch (ActivityNotFoundException e) {
			mActivity.finish();
		}

		// TODO @王兆琦 统计
		//StatisticsTools.uploadOperateIdNew(StatisticsConstants.UPDATE_DIALOG_CLICK);
	}

	@Override
	public void onCancel() {

		/**
		 * later被点过
		 */
		if (mSharedPreferencesManager.getBoolean(UpdateManager.UPDATE_VERSION_LATER,
				false)) {
			mSharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_CANCEL, true);
		} else {
			mSharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_LATER, true);
			mSharedPreferencesManager.commitLong(UpdateManager.UPDATE_VERSION_LATER_TIME, System.currentTimeMillis());
		}
	}

	@Override
	public void onBackPress() {
		if (mSharedPreferencesManager.getInt(UpdateManager.UPDATE_WAY,
				UpdateManager.UPDATE_WAY_NORMAL) != 1) {
			if (mSharedPreferencesManager.getBoolean(
					UpdateManager.UPDATE_VERSION_LATER, false)) {
				mSharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_CANCEL, true);
			} else {
				mSharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_LATER, true);
				mSharedPreferencesManager.commitLong(UpdateManager.UPDATE_VERSION_LATER_TIME, System.currentTimeMillis());
			}
		} else {
			mActivity.finish();
		}
	}

}

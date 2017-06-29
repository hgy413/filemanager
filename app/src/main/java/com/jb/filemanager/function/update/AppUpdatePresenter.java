package com.jb.filemanager.function.update;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.ui.dialog.CommonVerifyDialog;
import com.jb.filemanager.ui.dialog.UpdateConfirmDialog;

import java.lang.ref.WeakReference;

/**
 * Created by bill wang on 16/3/10.
 *
 */
public class AppUpdatePresenter {

    private WeakReference<Activity> mActivityRef;
    private final Handler mHandler = new Handler();
    private UpdateConfirmDialog mUpdateConfirmDialog;
    private DialogShow mDialogShow;

    private boolean mShowing;

    public AppUpdatePresenter(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        init();
    }

    private void init() {
        if (UpdateManager.isNeedToCheckVersion()) {
//            new UpdateManager().checkVersion();
        }
    }

    public void onResume() {
        // 判断是否需要Dialog显示
        if (UpdateManager.needDialogShow()) {
            mShowing = true;
            if (mUpdateConfirmDialog != null) {
                if (mUpdateConfirmDialog.isShowing()) {
                    mUpdateConfirmDialog.dismiss();
                } else {
                    mHandler.removeCallbacks(mDialogShow);
                }
            }
            Activity activity = mActivityRef.get();
            if (activity != null) {
                mUpdateConfirmDialog = new UpdateConfirmDialog(activity);
                mDialogShow = new DialogShow(mUpdateConfirmDialog, activity);
                mHandler.postDelayed(mDialogShow, 1000);
            }
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * @author wangying
     */
    class DialogShow implements Runnable {

        private UpdateConfirmDialog mDialog;
        private Context mContext;
        private SharedPreferencesManager mPm;

        public DialogShow(UpdateConfirmDialog dialog, Context context) {
            mDialog = dialog;
            mContext = context;
            mPm = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        }

        @Override
        public void run() {
           // mDialog.setHeight((int) mContext.getResources().getDimension(
                    //R.dimen.dialog_update_height));
            mDialog.setDialogTitle(TheApplication.getAppContext().getString(R.string.update_notice), 0xff444444);
            String cancelTxt = null;
            if (!mPm.getBoolean(UpdateManager.UPDATE_VERSION_LATER, false)) {
                cancelTxt = TheApplication.getAppContext().getString(R.string.update_later);
            } else {
                cancelTxt = TheApplication.getAppContext().getString(R.string.update_cancel);
            }
            if (mPm.getInt(UpdateManager.UPDATE_WAY, UpdateManager.UPDATE_WAY_NORMAL) == UpdateManager.UPDATE_WAY_FORCE) {
                cancelTxt = null;
            }
            String sureTxt = TheApplication.getAppContext().getString(R.string.update_update);
            if (cancelTxt == null) {
                mDialog.setContentText(sureTxt);
            } else {
                mDialog.setContentText(cancelTxt, sureTxt);
            }
            mDialog.setUpdateTipText(mPm.getString(UpdateManager.UPDATE_VERSION_DETAIL,
                    ""));
            mDialog.setOnCommonDialogListener(new CommonVerifyDialog.OnCommonDialogListener() {
                @Override
                public void onCancel(View view) {
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
                    if (sharedPreferencesManager.getBoolean(UpdateManager.UPDATE_VERSION_LATER,
                            false)) {
                        sharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_CANCEL, true);
                    } else {
                        sharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_LATER, true);
                        sharedPreferencesManager.commitLong(UpdateManager.UPDATE_VERSION_LATER_TIME, System.currentTimeMillis());
                    }

                    mShowing = false;
                }

                @Override
                public void onSure(View view) {
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
                    if (sharedPreferencesManager.getInt(UpdateManager.UPDATE_WAY,
                            UpdateManager.UPDATE_WAY_NORMAL) != 1) {
                        sharedPreferencesManager.commitBoolean(
                                UpdateManager.UPDATE_VERSION_LATER, true);

                        sharedPreferencesManager.commitLong(
                                UpdateManager.UPDATE_VERSION_LATER_TIME,
                                System.currentTimeMillis());
                    }

                    Uri uri = Uri.parse(sharedPreferencesManager
                            .getString(UpdateManager.UPDATE_GP_URL,
                                    "market://details?id=com.ace.network"));
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
                    marketIntent.setPackage("com.android.vending");
                    marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        if (mContext != null) {
                            mContext.startActivity(marketIntent);
                        }
                    } catch (ActivityNotFoundException e) {
                        if (mActivityRef != null && mActivityRef.get() != null) {
                            mActivityRef.get().finish();
                        }
                    }

                    // TODO @王兆琦 统计
                    // StatisticsTools.uploadOperateIdNew(StatisticsConstants.UPDATE_DIALOG_CLICK);

                    mShowing = false;
                }
            });

            if (mActivityRef.get() != null && !mActivityRef.get().isFinishing()) {
                mDialog.show();

                // 统计
               // StatisticsTools.uploadOperateIdNew(StatisticsConstants.UPDATE_DIALOG_SHOW);
            }
        }

    }
}

package com.jb.filemanager.function.update;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.ui.dialog.ConfirmCommonDialog;
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
            new UpdateManager().checkVersion();
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
            mUpdateConfirmDialog = new UpdateConfirmDialog(mActivityRef.get(), false);
            mDialogShow = new DialogShow(mUpdateConfirmDialog, mActivityRef.get());
            mHandler.postDelayed(mDialogShow, 1000);
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
            mPm = SharedPreferencesManager.getInstance(context);
        }

        @Override
        public void run() {
           // mDialog.setHeight((int) mContext.getResources().getDimension(
                    //R.dimen.dialog_update_height));
            mDialog.setTitleText(R.string.update_notice);
            if (!mPm.getBoolean(UpdateManager.UPDATE_VERSION_LATER, false)) {
                mDialog.setCancelText(R.string.update_later);
            } else {
                mDialog.setCancelText(R.string.update_cancel);
            }
            if (mPm.getInt(UpdateManager.UPDATE_WAY, UpdateManager.UPDATE_WAY_NORMAL) == UpdateManager.UPDATE_WAY_FORCE) {
                mDialog.setCancelGone();
            }
            mDialog.setOkText(R.string.update_update);
            mDialog.setContentText(mPm.getString(UpdateManager.UPDATE_VERSION_DETAIL,
                    ""));
            mDialog.setOnConfirmDetailListener(new ConfirmCommonDialog.OnConfirmDetailListener() {
                @Override
                public void onConfirm() {
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
                                    "market://details?id=com.jb.filemanager"));
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
                    marketIntent.setPackage("com.android.vending");
                    marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        mContext.startActivity(marketIntent);
                    } catch (ActivityNotFoundException e) {
                        if (mActivityRef != null && mActivityRef.get() != null) {
                            mActivityRef.get().finish();
                        }
                    }

                    // TODO @王兆琦 统计
                   // StatisticsTools.uploadOperateIdNew(StatisticsConstants.UPDATE_DIALOG_CLICK);

                    mShowing = false;
                }

                @Override
                public void onCancel() {
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
                public void onBackPress() {
                    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
                    if (sharedPreferencesManager.getInt(UpdateManager.UPDATE_WAY,
                            UpdateManager.UPDATE_WAY_NORMAL) != 1) {
                        if (sharedPreferencesManager.getBoolean(
                                UpdateManager.UPDATE_VERSION_LATER, false)) {
                            sharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_CANCEL, true);
                        } else {
                            sharedPreferencesManager.commitBoolean(UpdateManager.UPDATE_VERSION_LATER, true);
                            sharedPreferencesManager.commitLong(UpdateManager.UPDATE_VERSION_LATER_TIME, System.currentTimeMillis());
                        }
                    } else {
                        if (mActivityRef != null && mActivityRef.get() != null) {
                            mActivityRef.get().finish();
                        }
                    }

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

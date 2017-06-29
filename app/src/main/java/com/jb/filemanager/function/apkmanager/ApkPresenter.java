package com.jb.filemanager.function.apkmanager;

import android.content.Intent;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 13:37
 */

class ApkPresenter implements ApkManagerContract.Presenter {
    private ApkManagerContract.View mView;
    private ApkManagerContract.Support mSupport;

    ApkPresenter(ApkManagerContract.View view, ApkManagerContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            if (systemBack) {
                mView.finishActivity();
            } else {
                mView.finishActivity();
            }
        }
    }

    @Override
    public void onPressHomeKey() {

    }
}

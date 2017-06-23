package com.jb.filemanager.function.setting;

import android.content.Intent;

/**
 * Setting的P层
 * Created by miwo on 2016/9/2.
 */
public class SettingPresenter implements SettingContract.Presenter{

    private SettingContract.View mView;
    private SettingContract.Support mSupport;

    SettingPresenter(SettingContract.View view, SettingContract.Support support) {
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
            mView.finishActivity();
        }
    }

    @Override
    public void onPressHomeKey() {

    }
}

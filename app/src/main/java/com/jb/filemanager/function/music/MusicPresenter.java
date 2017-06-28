package com.jb.filemanager.function.music;

import android.content.Intent;

import java.io.File;


/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicPresenter implements MusicContract.Presenter {

    private MusicContract.View mView;
    private MusicContract.Support mSupport;

    MusicPresenter(MusicContract.View view, MusicContract.Support support) {
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
        // nothing to do
    }

    @Override
    public boolean isSelected(File file) {
        return false;
    }
}

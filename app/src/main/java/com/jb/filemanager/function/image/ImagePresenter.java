package com.jb.filemanager.function.image;

import android.content.Intent;


/**
 * Created by bill wang on 2017/6/27.
 *
 */

class ImagePresenter implements ImageContract.Presenter {


    private ImageContract.View mView;
    private ImageContract.Support mSupport;

    ImagePresenter(ImageContract.View view, ImageContract.Support support) {
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
}

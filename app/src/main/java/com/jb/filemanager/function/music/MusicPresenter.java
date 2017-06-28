package com.jb.filemanager.function.music;

import android.content.Intent;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicPresenter implements MusicContract.Presenter {

    public static final int MUSIC_STATUS_NORMAL = 0;
    public static final int MUSIC_STATUS_SELECT = 1;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private int mStatus = MUSIC_STATUS_NORMAL;

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
        boolean result = false;
        if (file != null) {
            try {
                result = mSelectedFiles.contains(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void addOrRemoveSelected(File file) {
        if (file != null) {
            try {
                if (mSelectedFiles.contains(file)) {
                    mSelectedFiles.remove(file);
                } else {
                    mSelectedFiles.add(file);
                }

                if (mSelectedFiles.size() > 0) {
                    mStatus = MUSIC_STATUS_SELECT;
                } else {
                    mStatus = MUSIC_STATUS_NORMAL;
                }

                mView.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

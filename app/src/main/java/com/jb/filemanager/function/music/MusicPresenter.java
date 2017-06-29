package com.jb.filemanager.function.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.annotation.NonNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;


/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicPresenter implements MusicContract.Presenter,
        LoaderManager.LoaderCallbacks<List<MusicInfo>>{
    private static final int LOADER_ID = 1;
    public static final int MUSIC_STATUS_NORMAL = 0;
    public static final int MUSIC_STATUS_SELECT = 1;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private int mStatus = MUSIC_STATUS_NORMAL;

    private MusicContract.View mView;
    private Loader mSupportLoader;
    private final LoaderManager mLoaderManager;
    private List<MusicInfo> mMusicInfoList;
    MusicPresenter(@NonNull MusicContract.View view, @NonNull Loader loader,
                   @NonNull LoaderManager loaderManager) {
        mView = checkNotNull(view);
        mSupportLoader = checkNotNull(loader);
        mLoaderManager = checkNotNull(loaderManager);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public void restartLoader() {
        mLoaderManager.restartLoader(LOADER_ID, null, this);
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
        mLoaderManager.destroyLoader(LOADER_ID);
        mView = null;
        mSupportLoader = null;
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

    // 此函数用于讲Loader传递给LoaderManager
    @Override
    public Loader<List<MusicInfo>> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(this,
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_MODIFIED },
//                MediaStore.Audio.Media.SIZE + " > 0 ",
//                null,
//                MediaStore.Audio.Media.DATE_MODIFIED + " desc");
        return mSupportLoader;
    }



    @Override
    public void onLoaderReset(Loader<List<MusicInfo>> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
        restartLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<MusicInfo>> loader, List<MusicInfo> data) {
        mMusicInfoList = data;
        if (data != null && data.size() >0) {
            // 加载失败提示
        } else {
            mView.updateView(mMusicInfoList);
        }
    }
}

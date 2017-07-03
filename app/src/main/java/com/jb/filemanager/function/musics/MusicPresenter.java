package com.jb.filemanager.function.musics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;


import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-6-30.
 *
 */

public class MusicPresenter implements MusicContract.Presenter,
        LoaderManager.LoaderCallbacks<GroupList<String, MusicInfo>> {
    private final MusicContract.View mView;
    private final MusicContract.Support mSupport;
    private final LoaderManager mLoaderManager;
    private final MusicsLoader mMusicLoader;
    private GroupList<String, MusicInfo> mMusicMaps;
    public MusicPresenter(@NonNull MusicContract.View view, @NonNull MusicContract.Support support,
                          @NonNull MusicsLoader loader, @NonNull LoaderManager manager){
        mView = checkNotNull(view);
        mSupport = checkNotNull(support);
        mLoaderManager = checkNotNull(manager);
        mMusicLoader = checkNotNull(loader);

    }
    @Override
    public Loader<GroupList<String,MusicInfo>> onCreateLoader(int id, Bundle args) {
        return mMusicLoader;
    }

    @Override
    public void onLoadFinished(Loader<GroupList<String, MusicInfo>> loader, GroupList<String, MusicInfo> data) {
        mMusicMaps = data;
        if (mMusicMaps == null) {
            //Todo 显示没有音乐提示
        } else {
            // 显示列表
            mView.showMusicList(mMusicMaps);
        }
    }

    @Override
    public void onLoaderReset(Loader<GroupList<String, MusicInfo>> loader) {

    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public void onClickBackButton(boolean finishActivity) {
        if (mView != null) {
            if (finishActivity) {
                ((AppCompatActivity)mView).finish();
            } else {
                ((AppCompatActivity)mView).finish();
            }
        }
    }
}

package com.jb.filemanager.function.musics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;
import java.util.Map;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-6-30.
 */

public class MusicPresenter implements MusicContract.Presenter,
        LoaderManager.LoaderCallbacks<Map<String, List<MusicInfo>>> {
    private final MusicContract.View mView;
    private final MusicContract.Support mSupport;
    private final LoaderManager mLoaderManager;
    private final MusicsLoader mMusicLoader;
    private Map<String, List<MusicInfo>> mMusicMaps;
    public MusicPresenter(@NonNull MusicContract.View view, @NonNull MusicContract.Support support,
                          @NonNull MusicsLoader loader, @NonNull LoaderManager manager){
        mView = checkNotNull(view);
        mSupport = checkNotNull(support);
        mLoaderManager = checkNotNull(manager);
        mMusicLoader = checkNotNull(loader);
    }
    @Override
    public Loader<Map<String, List<MusicInfo>>> onCreateLoader(int id, Bundle args) {
        return mMusicLoader;
    }

    @Override
    public void onLoadFinished(Loader<Map<String, List<MusicInfo>>> loader, Map<String, List<MusicInfo>> data) {
        mMusicMaps = data;
        if (mMusicMaps == null) {
            //Todo 显示没有音乐提示
        } else {
            // 显示列表
            // mView.
        }
    }

    @Override
    public void onLoaderReset(Loader<Map<String, List<MusicInfo>>> loader) {

    }
}

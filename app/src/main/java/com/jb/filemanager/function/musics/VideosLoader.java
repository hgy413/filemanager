package com.jb.filemanager.function.musics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by boole on 7/7/17.
 */

public class VideosLoader extends AsyncTaskLoader {
    private MusicSupport mMusicSupport;
    public VideosLoader(@NonNull Context context, @NonNull MusicSupport support) {
        super(context);
        mMusicSupport = checkNotNull(support);
    }

    @Override
    public GroupList<String, MusicInfo> loadInBackground() {
        return mMusicSupport.getAllMusicInfo();
    }
}

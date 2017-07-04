package com.jb.filemanager.function.musics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-6-30.
 */

public class MusicsLoader extends AsyncTaskLoader{
    private MusicSupport mMusicSupport;
    public MusicsLoader(Context context, @NonNull MusicSupport support) {
        super(context);
        mMusicSupport = checkNotNull(support);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //forceLoad();
    }

    @Override
    public GroupList<String, MusicInfo> loadInBackground() {
        return (GroupList)mMusicSupport.getAllMusicInfo();
    }
}

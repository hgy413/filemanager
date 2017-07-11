package com.jb.filemanager.function.samefile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by bool on 17-6-30.
 */

public class MusicsLoader extends AsyncTaskLoader{
    private SameFileSupport mSameFileSupport;
    public MusicsLoader(Context context, @NonNull SameFileSupport support) {
        super(context);
        mSameFileSupport = support;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //forceLoad();
    }

    @Override
    public GroupList<String, FileInfo> loadInBackground() {
        return (GroupList) mSameFileSupport.getAllMusicInfo();
    }
}

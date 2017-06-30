package com.jb.filemanager.function.musics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Map;

import static com.mopub.common.Preconditions.NoThrow.checkNotNull;

/**
 * Created by bool on 17-6-30.
 */

public class MusicsLoader extends AsyncTaskLoader{
    private MusicSupport mMusicSupport;
    public MusicsLoader(Context context, @NonNull MusicSupport support) {
        super(context);
        checkNotNull(support);
        mMusicSupport = support;
    }

    @Override
    public Map<String, List<MusicInfo>> loadInBackground() {
        return mMusicSupport.getAllMusicInfo();
    }
}

package com.jb.filemanager.function.musics;

import android.provider.MediaStore;

/**
 * Created by bool on 17-6-30.
 */

public class MusicInfo {
//    private static final String[] MUSIC_PROPERTIES = {
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.SIZE,
//            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.DATE_MODIFIED,
//            MediaStore.Audio.Media.MIME_TYPE,
//            MediaStore.Audio.Media.DURATION,
//            MediaStore.Audio.Media.ARTIST
//    };

    public String mId;
    public String mName;
    public int mSize;
    public String mFullPath;
    public long mModified;
    public String mType;
    public String mDuration;
    public String mArtist;
}

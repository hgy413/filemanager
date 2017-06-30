package com.jb.filemanager.function.music;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;

/**
 * Created by bill wang on 2017/6/28.
 */

public class MusicSupport implements MusicContract.Support {
    private Context mContext;
    private static final String[] PROJECTION_MUSIC = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
    };
    MusicSupport() {
        mContext = TheApplication.getInstance();
    }


    @Override
    public Loader<Cursor> getAllMusicCursor() {
        return new CursorLoader(mContext,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_MODIFIED },
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MediaStore.Audio.Media.DATE_MODIFIED + " desc");
    }

    @Override
    public Cursor queryData(long start, long end) {
        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_MUSIC,
                MediaStore.Audio.Media.SIZE + " > 0" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + " > ?" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + "<= ?",
                new String[]{String.valueOf(start), String.valueOf(end)},
                MediaStore.Audio.Media.DISPLAY_NAME);
    }
}

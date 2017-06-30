package com.jb.filemanager.function.musics;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bool on 17-6-30.
 */

public class MusicSupport implements MusicContract.Support {
    private static final String[] MUSIC_PROPERTIES = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
    };
    private final Context mContext;
    private ContentResolver mResolver;
    public MusicSupport() {
        mContext = TheApplication.getInstance();
        mResolver = mContext.getContentResolver();
    }

    @Override
    public Map<String, List<MusicInfo>> getAllMusicInfo() {
        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROPERTIES,
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MUSIC_PROPERTIES[4]
                );
        Map<String, List<MusicInfo>> map = new HashMap<>();

        while(cursor.moveToNext()) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(0);
                long modify = cursor.getLong(1);
                //Logger.e("wangzq", path + " " + String.valueOf(modify));

                boolean isSameDay = TimeUtil.isSameDayOfMillis(lastModify, modify);
                if (!isSameDay) {
                    // modify 的单位是秒
                    lastModify = modify;
                    String timeString = TimeUtil.getTime(modify * 1000);
                    long startMills = TimeUtil.getStartMillsInDay(modify);
                    long endMills = startMills + TimeUtil.MILLIS_IN_DAY;

                    String[] row = new String[] { String.valueOf(group), timeString, String.valueOf(startMills), String.valueOf(endMills) };
                    matrixCursor.addRow(row);
                }
            }
        }
        return map;
    }

    protected MusicInfo cursorToMusicInfo(Cursor cursor) {
        MusicInfo info = new MusicInfo();
        info.mId = cursor.getString(0);
        info.mName = cursor.getString(1);
        info.mSize = cursor.getInt(2);
        info.mFullPath = cursor.getString(3);
        info.mModified = cursor.getInt(4);
        info.mType = cursor.getString(5);
        info.mDuration = cursor.getString(6);
        info.mArtist = cursor.getString(7);


    }
}

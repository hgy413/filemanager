package com.jb.filemanager.function.musics;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mopub.common.Preconditions.NoThrow.checkNotNull;


/**
 * Created by bool on 17-6-30.
 * 数据查询和验证
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
    public Map<String, ArrayList<MusicInfo>> getAllMusicInfo() {
        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROPERTIES,
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MUSIC_PROPERTIES[4]
                );
        Map<String, ArrayList<MusicInfo>> map = new ArrayMap<>();
        long lastModify = 0L;
        long modify = 0l;
        MusicInfo info = null;
        ArrayList<MusicInfo> infoList;

        while(cursor.moveToNext()) {
            if (null != (info = cursorToMusicInfo(cursor))) {
                boolean isSameDay = TimeUtil.isSameDayOfMillis(lastModify, modify);
                if (!isSameDay) {
                    lastModify = modify;
                    modify = info.mModified;
                    infoList = new ArrayList<>();
                    map.put((TimeUtil.getDateByMillisecond(modify)).toString(), infoList);
                } else {
                    infoList = map.get((TimeUtil.getDateByMillisecond(modify)).toString());
                }
                if (infoList != null) {
                    infoList.add(info);
                }
            }
        }
        return map;
    }

    protected MusicInfo cursorToMusicInfo(@NonNull Cursor cursor) {
        checkNotNull(cursor);
        MusicInfo info = new MusicInfo();
        info.mId = cursor.getString(0);
        info.mName = cursor.getString(1);
        info.mSize = cursor.getInt(2);
        info.mFullPath = cursor.getString(3);
        info.mModified = cursor.getLong(4);
        info.mType = cursor.getString(5);
        info.mDuration = cursor.getLong(6);
        info.mArtist = cursor.getString(7);
        if (!TextUtils.isEmpty(info.mFullPath)) {
            File file = new File(info.mFullPath);
            if (file.exists() && file.isFile()) {
                // 当获得的fileName为空时，从filePath中获得文件名
                if (TextUtils.isEmpty(info.mName)) {
                    info.mName = "";
                    if (!TextUtils.isEmpty(info.mFullPath)) {
                        info.mName = info.mFullPath.substring(info.mFullPath.lastIndexOf(File.separator) + 1);
                    }
                }
//                if (mPresenterRef != null && mPresenterRef.get() != null) {
//                    if (isChecked) {
//                        if (mPresenterRef.get().isSelected(file)) {
//                            mPresenterRef.get().addOrRemoveSelected(file);
//                        }
//                    } else {
//                        if (!mPresenterRef.get().isSelected(file)) {
//                            mPresenterRef.get().addOrRemoveSelected(file);
//                        }
//                    }
//                }
            }
        }
        return info;
    }

    int getMuscisNum() {
        //mResolver.query()
        return 0;
    }
}

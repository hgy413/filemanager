package com.jb.filemanager.function.samefile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static com.mopub.common.Preconditions.NoThrow.checkNotNull;


/**
 * Created by bool on 17-6-30.
 * 数据查询和验证
 */

public class SameFileSupport implements SameFileContract.Support {
    private static final Uri NUSIC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String[] MUSIC_PROPERTIES = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
    };
    private final Context mContext;
    private ContentResolver mResolver;
    public SameFileSupport() {
        mContext = TheApplication.getInstance();
        mResolver = mContext.getContentResolver();
    }

    @Override
    public GroupList<String, FileInfo> getAllMusicInfo() {
        Cursor cursor = mResolver.query(NUSIC_URI,
                MUSIC_PROPERTIES,
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MUSIC_PROPERTIES[4]
                );
        GroupList<String, FileInfo> list = new GroupList<>();
        long lastModify = 0L;
        long modify = 0l;
        FileInfo info = null;
        ArrayList<FileInfo> infoList;

        while(cursor.moveToNext()) {
            if (null != (info = cursorToMusicInfo(cursor))) {
                modify = info.mModified;
                boolean isSameDay = TimeUtil.isSameDayOfMillis(lastModify, modify);
                if (!isSameDay) {
                    lastModify = modify;
                    infoList = new ArrayList<>();
                    String date = (TimeUtil.getYandMandD(new Date(modify))).toString();
                    list.put(date, infoList);
                } else {
                    String date = (TimeUtil.getYandMandD(new Date(modify))).toString();
                    infoList = list.get(date);
                }
                if (infoList != null) {
                    infoList.add(info);
                }
            }
        }
        return list;
    }

    protected FileInfo cursorToMusicInfo(@NonNull Cursor cursor) {
        checkNotNull(cursor);
        FileInfo info = new FileInfo();
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
            } else {
                info = null;
            }
        }
        return info;
    }

    @Override
    public int getMuscisNum() {
        int musicCount = 0;
        Cursor cursor = mResolver.query(NUSIC_URI,
                new String[]{MediaStore.Audio.Media._COUNT},
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                null);
        if (cursor.moveToNext()){
            musicCount = cursor.getInt(0);
        }
        return musicCount;
    }

    @Override
    public void delete(ArrayList<String> fullPathList) {
    }

    @Override
    public Object getAllDownloadInfo() {
        return null;
    }
    @Override
    public GroupList<String,FileInfo> getAllVideoInfo() {
        return null;
    }
}

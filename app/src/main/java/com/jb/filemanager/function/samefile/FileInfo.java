package com.jb.filemanager.function.samefile;

import com.jb.filemanager.Const;

/**
 * Created by bool on 17-6-30.
 */

public class FileInfo {
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

    public Const.FILE_TYPE mFileType;
    public String mId;
    public String mName;
    public long mSize;
    public String mFullPath;
    public long mModified;
    public String mType;
    public long mDuration;
    public String mArtist;
    public boolean isSelected = false;
}

package com.jb.filemanager.function.samefile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.util.FileTypeUtil;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.TimeUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by bool on 17-6-30.
 * 数据查询和验证
 */

public class SameFileSupport implements SameFileContract.Support {
    private static final Uri MUSIC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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
    private static final String[] VIDEO_PROPERTIES = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.ARTIST
    };
    private final String mDownPath;
    // 缓存文件头信息-文件头信息
    public static final ArrayMap<String, String> mFileTypes = new ArrayMap<String, String>();
    private final Context mContext;
    private ContentResolver mResolver;
    public SameFileSupport() {
        mContext = TheApplication.getInstance();
        mResolver = mContext.getContentResolver();
        mDownPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public GroupList<String, FileInfo> getAllMusicInfo() {
        return getMediaInfo(MUSIC_URI, MUSIC_PROPERTIES);
    }

    @Override
    public GroupList<String,FileInfo> getAllVideoInfo() {
        return getMediaInfo(VIDEO_URI, VIDEO_PROPERTIES);
    }
    public static int getVideoNumber() {
       // TheApplication.getAppContext().getContentResolver().query(VIDEO_URI, m);
        return 0;
    }
    @Override
    public GroupList<String, FileInfo> getAllDownloadInfo() {
        File download = new File(mDownPath);
        GroupList<String, FileInfo> fileGroupList = new GroupList<>();
        List<File> fileList = null;
        // First: Get All Files object.
        if (download != null && download.exists() && download.isDirectory()) {
            fileList = getFiles(download);
        }
        // Second: Get All file's information.
        ArrayList<FileInfo> infoList = new ArrayList<>();
        for (File file : fileList) {
            FileInfo info = getFileInfo(file);
            Log.d("文件名字", file.getName());
            infoList.add(info);
        }
        // Third: Grouping by directory.
        GroupList<String, FileInfo> infoGroupList = new GroupList<>();
        //Todo Grouped by file type.
        ArrayList list;
        for (FileInfo info : infoList) {
            String type = getFileTypeName(info.mFullPath);
            list = infoGroupList.get(type);
            if (null == list) {
                list = new ArrayList();
                infoGroupList.put(type,list);
            }
            list.add(info);
        }
        return infoGroupList;
    }

    protected GroupList<String, FileInfo> getMediaInfo(final Uri uri, final String[] mediaProperties) {
        GroupList<String, FileInfo> list;
        try (Cursor cursor = mResolver.query(uri,
                mediaProperties,
                MediaStore.Audio.Media.SIZE + " >= 0 ",
                null,
                mediaProperties[4]
        )) {
            list = new GroupList<>();
            long lastModify = 0L;
            long modify = 0l;
            String date = "";
            FileInfo info = null;
            ArrayList<FileInfo> infoList;
            int i = cursor.getCount();
            i = 0;
            while (cursor.moveToNext()) {
                i++;
                if (null != (info = cursorToFileInfo(cursor))) {
                    modify = info.mModified;
                    if (uri == MUSIC_URI) {
                        info.mFileType = FileManager.AUDIO;
                    } else if (uri == VIDEO_URI) {
                        info.mFileType = FileManager.VIDEO;
                    }
                    boolean isSameDay = TimeUtil.isSameDayOfMillis(lastModify, modify);
                    if (!isSameDay) {
                        lastModify = modify;
                        date = TimeUtil.getYandMandD(new Date(info.mModified));
                        infoList = new ArrayList<>();
                        list.put(date, infoList);
                    } else {
                        date = TimeUtil.getYandMandD(new Date(info.mModified));
                        infoList = list.get(date);
                    }
                    if (infoList != null) {
                        infoList.add(info);
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    protected FileInfo cursorToFileInfo(@NonNull Cursor cursor) {
        FileInfo info = new FileInfo();
        info.mId = cursor.getString(0);
        info.mName = cursor.getString(1);
        info.mSize = cursor.getInt(2);
        info.mFullPath = cursor.getString(3);
        //此处返回数据为秒钟 所以乘以1000
        info.mModified = cursor.getLong(4) * 1000;
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
        Cursor cursor = mResolver.query(MUSIC_URI,
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
    public List<File> getFiles(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isDirectory()) {
                list.addAll(getFiles(f));
            } else {
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public void updateDatabaseRename(String oldFile, String newFile) {
        Uri targetUri = null;
        File file = new File(newFile);
        if (!file.exists() || file.isDirectory()){
            return;
        }
        MediaScannerConnection.scanFile(TheApplication.getAppContext(), new String[]{oldFile, newFile}, null, null); // 修改后的文件添加到系统数据库
    }

    public FileInfo getFileInfo(File file){
        FileInfo info = new FileInfo();
        info.mFullPath = file.getPath();
        info.mName = file.getName();
        info.mModified = file.lastModified();
        info.mSize = file.length();
        info.mFileType = FileUtil.getFileType(info.mFullPath); // get file type
        return info;
    }



    private String getFileTypeName(@NonNull String path) {
        switch (FileUtil.getFileType(path)) {
            case FileManager.IMAGE:
                return "Picture";
            case FileManager.VIDEO:
                return "Video";
            case FileManager.APP:
                return "Application";
            case FileManager.AUDIO:
                return "Music";
            case FileManager.TXT:
                return "Txt";
            case FileManager.PDF:
                return "Pdf";
            case FileManager.DOC:
                return "Doc";
            case FileManager.ZIP:
                return "Zip";
            case FileManager.OTHERS:
            default:
                return "Other";
        }
    }
}

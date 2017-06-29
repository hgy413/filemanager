package com.jb.filemanager.manager.file;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

public class FileManager {

    // 图片
    public static final int PICTURE = 0;

    // 视频
    public static final int VIDEO = 1;

    // 音乐
    public static final int MUSIC = 2;

    // 其他文件
    public static final int OTHERS = 3;

    // 应用
    public static final int APP = 4;

    // 文件夹
    public static final int DIR = 5;

    // 通讯录
    public static final int CONTACTS = 6;

    // 短信
    public static final int SMS = 7;

    // 通话记录
    public static final int PHONE = 8;

    // 书签
    public static final int BOOKMARKS = 9;

    // 日程
    public static final int SCHEDULE = 10;


    public static final int LOADER_IMAGE = 0;
    public static final int LOADER_VIDEO = 1;
    public static final int LOADER_APP = 2;
    public static final int LOADER_AUDIO = 3;
    public static final int LOADER_DOC = 4;
    public static final int LOADER_FILES = 5;

    private static FileManager sInstance;

    private ArrayList<File> mCopyFiles;
    private ArrayList<File> mCutFiles;

    public static FileManager getInstance() {
        synchronized (FileManager.class) {
            if (sInstance == null) {
                sInstance = new FileManager();
            }
            return sInstance;
        }
    }


    public void setCopyFiles(ArrayList<File> copyFiles) {
        clearCopyFiles();
        mCopyFiles = copyFiles;
    }

    public void setCutFiles(ArrayList<File> cutFiles) {
        clearCutFiles();
        mCutFiles = cutFiles;
    }

    public ArrayList<File> getCopyFiles() {
        return mCopyFiles;
    }

    public ArrayList<File> getCutFiles() {
        return mCutFiles;
    }

    public void clearCopyFiles() {
        if (mCopyFiles != null) {
            mCopyFiles.clear();
        }
    }

    public void clearCutFiles() {
        if (mCutFiles != null) {
            mCutFiles.clear();
        }
    }
}

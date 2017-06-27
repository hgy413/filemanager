package com.jb.filemanager.manager.file;

import com.jb.filemanager.Const;
import com.jb.filemanager.home.event.SelectFileEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileFilter;
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
    private String mCurrentPath;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();

    public static FileManager getInstance() {
        synchronized (FileManager.class) {
            if (sInstance == null) {
                sInstance = new FileManager();
            }
            return sInstance;
        }
    }

    public boolean isSelected(File file) {
        boolean result = false;
        if (file != null) {
            try {
                result = mSelectedFiles.contains(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void addSelected(File file) {
        if (file != null) {
            try {
                if (!mSelectedFiles.contains(file)) {
                    mSelectedFiles.add(file);
                    EventBus.getDefault().post(new SelectFileEvent(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeSelected(File file) {
        if (file != null) {
            try {
                if (mSelectedFiles.contains(file)) {
                    mSelectedFiles.remove(file);
                    EventBus.getDefault().post(new SelectFileEvent(file));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<File> getSelectedFiles() {
        return mSelectedFiles;
    }

    public void updateCurrentPath(String path) {
        mCurrentPath = path;
    }

    public boolean createFolder(String folderName) {
        boolean result = false;
        try {
            String path = mCurrentPath + File.separator + folderName;
            File file = new File(path);
            result = file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<File> deleteSelectedFiles() {
        ArrayList<File> result = new ArrayList<>();
        for (File file : mSelectedFiles) {
            result.addAll(deleteRecursive(file));
        }
        return result;
    }

    private ArrayList<File> deleteRecursive(File fileOrDirectory) {
        ArrayList<File> result = new ArrayList<>();
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                result.addAll(deleteRecursive(child));
            }
        } else {
            boolean success = fileOrDirectory.delete();
            if (!success) {
                result.add(fileOrDirectory);
            }
        }
        return result;
    }

    public boolean renameSelectedFile(String newName) {
        boolean result = false;
        if (mSelectedFiles != null && mSelectedFiles.size() == 1) {
            File file = mSelectedFiles.get(0);
            result = file.renameTo(new File(mCurrentPath + File.separator + newName));
        }
        return result;
    }
}

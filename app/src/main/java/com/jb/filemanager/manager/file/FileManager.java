package com.jb.filemanager.manager.file;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

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


    public static FileManager getInstance() {
        synchronized (FileManager.class) {
            if (sInstance == null) {
                sInstance = new FileManager();
            }
            return sInstance;
        }
    }

    public boolean createFolder(String fullPath) {
        boolean result = false;
        try {
            File file = new File(fullPath);
            result = file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<File> deleteSelectedFiles(ArrayList<File> deleteFiles) {
        ArrayList<File> result = new ArrayList<>();
        for (File file : deleteFiles) {
            result.addAll(deleteRecursive(file));
        }
        return result;
    }

    public boolean renameSelectedFile(File file, String newFilePath) {
        boolean result = false;
        if (file != null && file.exists() && !TextUtils.isEmpty(newFilePath)) {
            result = file.renameTo(new File(newFilePath));
        }
        return result;
    }

    public int[] countFolderAndFile(File parent) {
        return countFolderAndFileRecursive(parent);
    }

    // private start
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

    private int[] countFolderAndFileRecursive(File parent) {
        int[] result = new int[2];
        int folder = 0;
        int file = 0;
        if (parent != null && parent.exists() && parent.isDirectory()) {
            File[] childFiles = parent.listFiles();
            for (File child : childFiles) {
                if (child.isDirectory()) {
                    int[] temp = countFolderAndFileRecursive(child);
                    folder += temp[0];
                    file += temp[1];

                    folder++;
                } else {
                    file++;
                }
            }
        }
        result[0] = folder;
        result[1] = file;
        return result;
    }

    // private end
}

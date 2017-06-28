package com.jb.filemanager.function.applock.manager;

import android.os.Environment;

import com.jb.filemanager.Const;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 防偷窥文件操作类
 *
 * @author chenbenbin
 */
public class AntiPeepFileUtil {
    public static final String ANTI_PEEP = "AntiPeep";
    public static final String UNREAD_FOLDER = Const.BOOST_DIR + File.separator + "." + ANTI_PEEP;
    private static FileFilter sAntiPeepFileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && isJPG(pathname);
        }
    };

    /**
     * 是否为JPG文件
     */
    private static boolean isJPG(File pathname) {
        return "jpg".toLowerCase().equals(FileUtil.getExtension(pathname.getName()).toLowerCase());
    }

    /**
     * 创建未读的防偷窥文件
     */
    public static File createPeepFile() {
        File file = new File(UNREAD_FOLDER
                + File.separator + System.currentTimeMillis() + ".jpg");
        if (file.exists()) {
            return file;
        }
        return file;
    }

    /**
     * 将未读文件拷贝到公共相册存储路径
     */
    public static void setAllRead() {
        File unreadFolder = new File(UNREAD_FOLDER);
        if (!unreadFolder.exists()) {
            return;
        }
        String readPath = getReadPath();
        if (readPath == null) {
            return;
        }
        // 开始copy
        for (File file : unreadFolder.listFiles()) {
            FileUtil.copyFile(file.getPath(), readPath + File.separator + file.getName());
            FileUtil.deleteFile(file.getPath());
        }
    }

    /**
     * 获取公共相册存储路径
     */
    public static String getReadPath() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + Const.ROOT_FOLDER_NAME + ANTI_PEEP);
        if (!file.exists() && !file.mkdirs()) {
            // 不存在 && 创建失败则返回
            return null;
        }
        return file.getPath();
    }

    /**
     * 删除所有问题
     */
    public static void deleteAllFile() {
        FileUtil.deleteCategory(UNREAD_FOLDER);
        FileUtil.deleteCategory(getReadPath());
    }

    /**
     * 获取已读文件列表
     */
    public static List<File> getReadList() {
        String readPath = getReadPath();
        if (readPath == null) {
            return new ArrayList<File>();
        }
        File readFolder = new File(readPath);
        File[] files = readFolder.listFiles(sAntiPeepFileFilter);
        return Arrays.asList(files);
    }

    /**
     * 获取未读文件列表
     */
    public static List<File> getUnreadList() {
        File unreadFolder = new File(UNREAD_FOLDER);
        if (!unreadFolder.exists() || !unreadFolder.isDirectory()) {
            return new ArrayList<File>();
        }
        File[] files = unreadFolder.listFiles(sAntiPeepFileFilter);
        return Arrays.asList(files);
    }

    /**
     * 获取未读文件列表
     */
    public static int getUnreadListSize() {
        File unreadFolder = new File(UNREAD_FOLDER);
        if (!unreadFolder.exists() || !unreadFolder.isDirectory()) {
            return 0;
        }
        File[] files = unreadFolder.listFiles(sAntiPeepFileFilter);
        return files.length;
    }

}

package com.jb.filemanager.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jb.filemanager.manager.file.FileManager;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

public class FileUtil {

    public static final String HIDDEN_PREFIX = ".";

    public FileUtil() {

    }

    /*
     * Folder (directories) filter.
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /*
     * Folder (directories) filter with hidden file.
     */
    public static FileFilter sDirFilterWithHidden = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    /*
     * File (not directories) filter.
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /*
     * File (not directories) filter with hidden file.
     */
    public static FileFilter sFileFilterWithHidden = new FileFilter() {
        @Override
        public boolean accept(File file) {
            // Return files only (not directories) and skip hidden files
            return file.isFile();
        }
    };

    /*
     * File and folder comparator. TODO Expose sorting option method
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    private static String[] getVolumePathsFor14(Context context) {
        List<String> availablePaths = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method methodGetPaths = storageManager.getClass().getMethod("getVolumePaths");
            Method methodGetStatus = storageManager.getClass().getMethod("getVolumeState", String.class);
            String[] paths = (String[]) methodGetPaths.invoke(storageManager);

            for (String path : paths) {
                String status = (String) (methodGetStatus.invoke(storageManager, path));
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    availablePaths.add(path);
                }
            }
        } catch (Exception e) {
            Log.e("filemanager", "getVolumePathsFor14 >>> " + e.toString());
        }
        if (availablePaths.size() > 0) {
            String[] strings = new String[availablePaths.size()];
            availablePaths.toArray(strings);
            return strings;
        } else {
            return null;
        }
    }

    public static String[] getVolumePaths(Context context) {
        return getVolumePathsFor14(context);
    }

    /**
     * 获取文件类型，不包括目录！
     * @param name 文件名称
     * @return result
     */
    public static int getFileType(String name) {
        int lastDot = name.lastIndexOf(".");
        if (lastDot < 0) {
            return FileManager.OTHERS;
        }
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                name.substring(lastDot + 1).toLowerCase(Locale.getDefault()));
        if (mimeType == null) {
            return FileManager.OTHERS;
        } else if (mimeType.matches("image/.+")) {
            return FileManager.PICTURE;
        } else if (mimeType.matches("audio/.+")) {
            return FileManager.MUSIC;
        } else if (mimeType.matches("video/.+")) {
            return FileManager.VIDEO;
        } else if (mimeType.equals("application/vnd.android.package-archive")) {
            return FileManager.APP;
        } else {
            return FileManager.OTHERS;
        }
    }

    public static String getDirName(String dirPath) {
        String[] array = dirPath.split(File.separator);
        return array[array.length - 1];
    }

    public static long getSize(File file) {
        long size;
        if (file.isDirectory()) {
            size = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    size += getSize(child);
                }
            }
        } else {
            size = file.length();
        }
        return size;
    }


    public static boolean isInternalStoragePath(Context context, String path) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getPrimaryVolumeMethod = StorageManager.class.getMethod("getPrimaryVolume", (Class<?>[]) null);
            getPrimaryVolumeMethod.setAccessible(true);
            Object volume = getPrimaryVolumeMethod.invoke(storageManager, (Object[]) null);
            boolean isRemovable = (Boolean) volume.getClass().getMethod("isRemovable", (Class<?>[]) null).invoke(volume, (Object[]) null);
            String primaryPath = (String) volume.getClass().getMethod("getPath", (Class<?>[]) null).invoke(volume, (Object[]) null);
            String desc = (String) volume.getClass().getMethod("toString", (Class<?>[]) null).invoke(volume, (Object[]) null);
            Log.d("filemanager", "desc >>> " + desc);
            if (path != null && path.equals(primaryPath)) {
                return !isRemovable;
            }
        } catch (Exception e) {
            Log.e("filemanager", "getInternalStoragePath >>> " + e.toString());
        }
        return false;
    }
}

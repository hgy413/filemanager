package com.jb.filemanager.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.jb.filemanager.manager.file.FileManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
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

    public static boolean copyFilesToDest(ArrayList<File> fileArrayList, String destDir) {
        boolean result = true;
        if (checkCanPaste(fileArrayList, destDir)) {
            for (File file : fileArrayList) {
                result = result && copyFileOrDirectory(file.getAbsolutePath(), destDir);
            }
        }
        return result;
    }

    public static boolean cutFilesToDest(ArrayList<File> fileArrayList, String destDir) {
        boolean result = true;
        if (checkCanPaste(fileArrayList, destDir)) {
            for (File file : fileArrayList) {
                result = result && file.renameTo(new File(destDir + File.separator + file.getName()));
            }
        }
        return result;
    }

    public static boolean createFolder(String fullPath) {
        boolean result = false;
        try {
            File file = new File(fullPath);
            result = file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<File> deleteSelectedFiles(ArrayList<File> deleteFiles) {
        ArrayList<File> result = new ArrayList<>();
        for (File file : deleteFiles) {
            result.addAll(deleteRecursive(file));
        }
        return result;
    }

    public static boolean renameSelectedFile(File file, String newFilePath) {
        boolean result = false;
        if (file != null && file.exists() && !TextUtils.isEmpty(newFilePath)) {
            result = file.renameTo(new File(newFilePath));
        }
        return result;
    }

    public static int[] countFolderAndFile(File parent) {
        return countFolderAndFileRecursive(parent);
    }

    // private start
    private static ArrayList<File> deleteRecursive(File fileOrDirectory) {
        ArrayList<File> result = new ArrayList<>();
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                result.addAll(deleteRecursive(child));
            }
            fileOrDirectory.delete();
        } else {
            boolean success = fileOrDirectory.delete();
            if (!success) {
                result.add(fileOrDirectory);
            }
        }
        return result;
    }

    private static int[] countFolderAndFileRecursive(File parent) {
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

    private static boolean checkCanPaste(ArrayList<File> fileArrayList, String destDir) {
        boolean result = false;
        if (fileArrayList != null && fileArrayList.size() > 0 && !TextUtils.isEmpty(destDir)) {
            result = true;
            for (File file : fileArrayList) {
                if (destDir.startsWith(file.getAbsolutePath())) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    private static boolean copyFileOrDirectory(String srcDir, String dstDir) {
        boolean result = true;
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                for (String file : files) {
                    String src1 = (new File(src, file).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    // private end
}

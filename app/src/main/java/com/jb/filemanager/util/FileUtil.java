package com.jb.filemanager.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.jiubang.commerce.utils.FileUtils.deleteDirectory;


/**
 * Created by bill wang on 2017/6/23.
 *
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtil {

    public static final String FILE_NAME_REG = "^[^~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{\\}【】‘；：”“’。，、？]{1,}";
    public static final String FOLDER_NAME_REG = "^[^~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{\\}【】‘；：”“’。，、？]{1,}";


    public static final String HIDDEN_PREFIX = ".";

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * sdcard head
     */
    public final static String SDCARD = Environment
            .getExternalStorageDirectory().getPath();

    /**
     * The extension separator character.
     *
     * @since 1.4
     */
    public static final char EXTENSION_SEPARATOR = '.';

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

    /*
     * File and folder comparator. 名称升序
     */
    public static Comparator<File> sNameAscendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /*
     * File and folder comparator. 名称降序
     */
    public static Comparator<File> sNameDescendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return f2.getName().toLowerCase().compareTo(
                    f1.getName().toLowerCase());
        }
    };

    /*
     * File and folder comparator. 大小升序
     */
    public static Comparator<File> sSizeAscendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return Long.valueOf(f1.length()).compareTo(f2.length());
        }
    };

    /*
     * File and folder comparator. 大小降序
     */
    public static Comparator<File> sSizeDescendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return Long.valueOf(f2.length()).compareTo(f1.length());
        }
    };

    /*
     * File and folder comparator. 日期升序
     */
    public static Comparator<File> sDateAscendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
        }
    };

    /*
     * File and folder comparator. 日期降序
     */
    public static Comparator<File> sDateDescendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
        }
    };

    /*
     * File and folder comparator. 类型升序
     */
    public static Comparator<File> sTypeAscendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            String f1Path = f1.getAbsolutePath();
            String f2Path = f2.getAbsolutePath();

            int file1LastDot = f1Path.lastIndexOf(".");
            int file2LastDot = f2Path.lastIndexOf(".");
            if (file1LastDot < 0 && file2LastDot < 0) {
                return f1.getName().toLowerCase().compareTo(
                        f2.getName().toLowerCase());
            } else if (file1LastDot < 0 && file2LastDot >= 0) {
                return 1;
            } else if (file1LastDot >= 0 && file2LastDot < 0) {
                return -1;
            } else {
                try {
                    if (f1Path.length() <= file1LastDot + 1 && f2Path.length() <= file2LastDot + 1) {
                        return f1.getName().toLowerCase().compareTo(
                                f2.getName().toLowerCase());
                    } else if (f1Path.length() <= file1LastDot + 1) {
                        return -1;
                    } else if (f2Path.length() <= file2LastDot + 1) {
                        return 1;
                    } else {
                        String f1Type = f1.getAbsolutePath().substring(file1LastDot + 1).toLowerCase(Locale.getDefault());
                        String f2Type = f2.getAbsolutePath().substring(file2LastDot + 1).toLowerCase(Locale.getDefault());
                        if (f1Type.equals(f2Type)) {
                            return f1.getName().toLowerCase().compareTo(
                                    f2.getName().toLowerCase());
                        } else {
                            return f1Type.compareTo(f2Type);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }
    };

    /*
     * File and folder comparator. 类型降序
     */
    public static Comparator<File> sTypeDescendComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            String f1Path = f1.getAbsolutePath();
            String f2Path = f2.getAbsolutePath();

            int file1LastDot = f1Path.lastIndexOf(".");
            int file2LastDot = f2Path.lastIndexOf(".");
            if (file1LastDot < 0 && file2LastDot < 0) {
                return f2.getName().toLowerCase().compareTo(
                        f1.getName().toLowerCase());
            } else if (file1LastDot < 0 && file2LastDot >= 0) {
                return -1;
            } else if (file1LastDot >= 0 && file2LastDot < 0) {
                return 1;
            } else {
                try {
                    if (f1Path.length() <= file1LastDot + 1 && f2Path.length() <= file2LastDot + 1) {
                        return f2.getName().toLowerCase().compareTo(
                                f1.getName().toLowerCase());
                    } else if (f1Path.length() <= file1LastDot + 1) {
                        return 1;
                    } else if (f2Path.length() <= file2LastDot + 1) {
                        return -1;
                    } else {
                        String f1Type = f1.getAbsolutePath().substring(file1LastDot + 1).toLowerCase(Locale.getDefault());
                        String f2Type = f2.getAbsolutePath().substring(file2LastDot + 1).toLowerCase(Locale.getDefault());
                        if (f1Type.equals(f2Type)) {
                            return f2.getName().toLowerCase().compareTo(
                                    f1.getName().toLowerCase());
                        } else {
                            return f2Type.compareTo(f1Type);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
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

    public static long checkSpacePaste(ArrayList<File> fileArrayList, String destDir) {
        long result = 0L;
        if (fileArrayList != null && fileArrayList.size() > 0 && !TextUtils.isEmpty(destDir)) {
            // TODO @wangzq 检查空间
            long needSpace = 0L;
            for (File file : fileArrayList) {
                needSpace += FileUtil.getSize(file);
            }

            StatFs stat = new StatFs(destDir);
            long totalSize = APIUtil.getAvailableBytes(stat);

            result = totalSize - needSpace;
        }
        return result;
    }

    public static boolean copyFileOrDirectory(String srcDir, String dstDir) {
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

    /**
<<<<<<< cd68d768d714c4a084b3d42ddf7b3879c9218a09
     * 统一获取raw文件流中数据:全部数据
     */
    public static String getAllStrDataFromRaw(Context context, int rawId) {
        String strData = null;
        if (context == null) {
            return strData;
        }
        // 从资源获取流
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawId);
            if (is != null) {
                byte[] buffer = new byte[is.available()];
                int len = is.read(buffer); // 读取流内容
                if (len > 0) {
                    strData = new String(buffer, 0, len).trim(); // 生成字符串
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strData;
    }

    /**
     * 根据给定路径参数删除单个文件的方法 私有方法，供内部其它方法调用
     *
     * @param filePath 要删除的文件路径
     * @return 成功返回true, 失败返回false
     */
    public static boolean deleteFile(String filePath) {
        // 定义返回结果
        boolean result = false;
        // //判断路径参数是否为空
        // if(filePath == null || "".equals(filePath)) {
        // //如果路径参数为空
        // System.out.println("文件路径不能为空~！");
        // } else {
        // //如果路径参数不为空
        // File file = new File(filePath);
        // //判断给定路径下要删除的文件是否存在
        // if( !file.exists() ) {
        // //如果文件不存在
        // System.out.println("指定路径下要删除的文件不存在~！");
        // } else {
        // //如果文件存在，就调用方法删除
        // result = file.delete();
        // }
        // }

        if (filePath != null && !"".equals(filePath.trim())) {
            File file = new File(filePath);
            if (file.exists()) {
                result = file.delete();
            }
        }
        return result;
    }


    public static String getNameFromFilepath(String filepath) {
        if (!TextUtils.isEmpty(filepath)) {
            int pos = filepath.lastIndexOf(UNIX_SEPARATOR);
            if (pos != -1) {
                return filepath.substring(pos + 1);
            }
        }
        return "";
    }

    /**
     * Gets the name minus the path from a full filename.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text
     * after the last forward or backslash is returned.
     * <p>
     * <pre>
     * a/b/c.txt --> c.txt
     * a.txt     --> a.txt
     * a/b/c     --> c
     * a/b/c/    --> ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is
     * running on.
     *
     * @param filename the filename to query, null returns null
     * @return the name of the file without the path, or an empty string if none
     * exists
     */
    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }


    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format. The
     * position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is
     * running on.
     *
     * @param filename the filename to find the last path separator in, null returns
     *                 -1
     * @return the index of the last separator character, or -1 if there is no
     * such character
     * @see -io-2.4.jar -> FilenameUtils(这里去掉了关于windows路径分隔符的判断)
     */
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        return filename.lastIndexOf(UNIX_SEPARATOR);
    }


    /**
     * 指定路径文件是否存在
     *
     * @param filePath
     * @return
     * @author huyong
     */
    public static boolean isFileExist(String filePath) {
        boolean result = false;
        try {
            File file = new File(filePath);
            result = file.exists();
            file = null;
        } catch (Exception e) {
        }
        return result;
    }

    /**
=======
>>>>>>> 添加打开弹窗
     * Gets the extension of a filename.
     * <p>
     * This method returns the textual part of the filename after the last dot.
     * There must be no directory separator after the dot.
     * <p>
     * <pre>
     * foo.txt      --> "txt"
     * a/b/c.jpg    --> "jpg"
     * a/b.txt/c    --> ""
     * a/b/c        --> ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is
     * running on.
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or
     * {@code null} if the filename is {@code null}.
     * @see -io-2.4.jar -> FilenameUtils
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * Returns the index of the last extension separator character, which is a
     * dot.
     * <p>
     * This method also checks that there is no directory separator after the
     * last dot. To do this it uses {@link #indexOfLastSeparator(String)} which
     * will handle a file in either Unix or Windows format.
     * <p>
     * The output will be the same irrespective of the machine that the code is
     * running on.
     *
     * @param filename the filename to find the last path separator in, null returns
     *                 -1
     * @return the index of the last separator character, or -1 if there is no
     * such character
     * @see -io-2.4.jar -> FilenameUtils
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    /*
     * @param path 要删除的文件夹路径
     *
     * @return 是否成功
     */
    public static boolean deleteCategory(String path) {
        if (path == null || "".equals(path)) {
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            deleteDirectory(path);
        }

        return file.delete();
    }

    /**
     * 复制文件，仅支持非目录文件的复制
     *
     * @param srcStr
     * @param decStr
     * @return
     */
    public static boolean copyFile(String srcStr, String decStr) {
        // 前提
        File srcFile = new File(srcStr);
        if (!srcFile.exists()) {
            return false;
        }
        File decFile = new File(decStr);
        if (!decFile.exists()) {
            File parent = decFile.getParentFile();
            parent.mkdirs();

            try {
                decFile.createNewFile();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(srcFile);
            output = new FileOutputStream(decFile);
            byte[] data = new byte[4 * 1024]; // 4k
            while (true) {
                int len = input.read(data);
                if (len <= 0) {
                    break;
                }
                output.write(data, 0, len);

                // just test how it will perform when a exception occure on
                // backing up
                // throw new IOException();
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (Exception e2) {
                }
            }
            if (null != output) {
                try {
                    output.flush();
                    output.close();
                } catch (Exception e2) {
                }
            }
        }

        return true;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String filePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String filePath, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(filePath);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(filePath));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String filePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String filePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }
}

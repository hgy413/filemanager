package com.jb.filemanager.function.zipfile.util;

import android.text.TextUtils;
import android.util.Log;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jiubang.commerce.utils.StringUtils;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/27
 *     desc  : 压缩相关工具类
 * </pre>
 */
public final class ZipUtils {

    private static final int KB = 1024;

    private ZipUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 批量压缩文件
     *
     * @param resFiles    待压缩文件集合
     * @param zipFilePath 压缩文件路径
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<File> resFiles, final String zipFilePath)
            throws IOException {
        return zipFiles(resFiles, zipFilePath, null);
    }

    /**
     * 批量压缩文件
     *
     * @param resFiles    待压缩文件集合
     * @param zipFilePath 压缩文件路径
     * @param comment     压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<File> resFiles, final String zipFilePath, final String comment)
            throws IOException {
        return zipFiles(resFiles, FileUtils.getFileByPath(zipFilePath), comment);
    }

    /**
     * 批量压缩文件
     *
     * @param resFiles 待压缩文件集合
     * @param zipFile  压缩文件
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<File> resFiles, final File zipFile)
            throws IOException {
        return zipFiles(resFiles, zipFile, null);
    }

    /**
     * 批量压缩文件
     *
     * @param resFiles 待压缩文件集合
     * @param zipFile  压缩文件
     * @param comment  压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<File> resFiles, final File zipFile, final String comment)
            throws IOException {
        if (resFiles == null || zipFile == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File resFile : resFiles) {
                if (!zipFile(resFile, "", zos, comment)) return false;
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                CloseUtils.closeIO(zos);
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径
     * @param zipFilePath 压缩文件路径
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFile(final String resFilePath, final String zipFilePath)
            throws IOException {
        return zipFile(resFilePath, zipFilePath, null);
    }

    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径
     * @param zipFilePath 压缩文件路径
     * @param comment     压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFile(final String resFilePath, final String zipFilePath, final String comment)
            throws IOException {
        return zipFile(FileUtils.getFileByPath(resFilePath), FileUtils.getFileByPath(zipFilePath), comment);
    }

    /**
     * 压缩文件
     *
     * @param resFile 待压缩文件
     * @param zipFile 压缩文件
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFile(final File resFile, final File zipFile)
            throws IOException {
        return zipFile(resFile, zipFile, null);
    }

    /**
     * 压缩文件
     *
     * @param resFile 待压缩文件
     * @param zipFile 压缩文件
     * @param comment 压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFile(final File resFile, final File zipFile, final String comment)
            throws IOException {
        if (resFile == null || zipFile == null) return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            return zipFile(resFile, "", zos, comment);
        } finally {
            if (zos != null) {
                CloseUtils.closeIO(zos);
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param resFile  待压缩文件
     * @param rootPath 相对于压缩文件的路径
     * @param zos      压缩文件输出流
     * @param comment  压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    private static boolean zipFile(final File resFile, String rootPath, final ZipOutputStream zos, final String comment)
            throws IOException {
        rootPath = rootPath + (isSpace(rootPath) ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            // 如果是空文件夹那么创建它，我把'/'换为File.separator测试就不成功，eggPain
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                if (!StringUtils.isEmpty(comment)) entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    // 如果递归返回false则返回false
                    if (!zipFile(file, rootPath, zos, comment)) return false;
                }
            }
        } else {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(resFile));
                ZipEntry entry = new ZipEntry(rootPath);
                if (!StringUtils.isEmpty(comment)) entry.setComment(comment);
                zos.putNextEntry(entry);
                byte buffer[] = new byte[KB];
                int len;
                while ((len = is.read(buffer, 0, KB)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            } finally {
                CloseUtils.closeIO(is);
            }
        }
        return true;
    }

    /**
     * 批量解压文件
     *
     * @param zipFiles    压缩文件集合
     * @param destDirPath 目标目录路径
     * @return {@code true}: 解压成功<br>{@code false}: 解压失败
     * @throws IOException IO错误时抛出
     */
    public static boolean unzipFiles(final Collection<File> zipFiles, final String destDirPath)
            throws IOException {
        return unzipFiles(zipFiles, FileUtils.getFileByPath(destDirPath));
    }

    /**
     * 批量解压文件
     *
     * @param zipFiles 压缩文件集合
     * @param destDir  目标目录
     * @return {@code true}: 解压成功<br>{@code false}: 解压失败
     * @throws IOException IO错误时抛出
     */
    public static boolean unzipFiles(final Collection<File> zipFiles, final File destDir)
            throws IOException {
        if (zipFiles == null || destDir == null) return false;
        for (File zipFile : zipFiles) {
            if (!unzipFile(zipFile, destDir)) return false;
        }
        return true;
    }

    /**
     * 解压文件
     *
     * @param zipFilePath 待解压文件路径
     * @param destDirPath 目标目录路径
     * @return {@code true}: 解压成功<br>{@code false}: 解压失败
     * @throws IOException IO错误时抛出
     */
    public static boolean unzipFile(final String zipFilePath, final String destDirPath)
            throws IOException {
        return unzipFile(FileUtils.getFileByPath(zipFilePath), FileUtils.getFileByPath(destDirPath));
    }

    /**
     * 解压文件
     *
     * @param zipFile 待解压文件
     * @param destDir 目标目录
     * @return {@code true}: 解压成功<br>{@code false}: 解压失败
     * @throws IOException IO错误时抛出
     */
    public static boolean unzipFile(final File zipFile, final File destDir)
            throws IOException {
        return unzipFileByKeyword(zipFile, destDir, null) != null;
    }

    /**
     * 解压带有关键字的文件
     *
     * @param zipFilePath 待解压文件路径
     * @param destDirPath 目标目录路径
     * @param keyword     关键字
     * @return 返回带有关键字的文件链表
     * @throws IOException IO错误时抛出
     */
    public static List<File> unzipFileByKeyword(final String zipFilePath, final String destDirPath, final String keyword)
            throws IOException {
        return unzipFileByKeyword(FileUtils.getFileByPath(zipFilePath),
                FileUtils.getFileByPath(destDirPath), keyword);
    }

    /**
     * 解压带有关键字的文件
     *
     * @param zipFile 待解压文件
     * @param destDir 目标目录
     * @param keyword 关键字
     * @return 返回带有关键字的文件链表
     * @throws IOException IO错误时抛出
     */
    public static List<File> unzipFileByKeyword(final File zipFile, final File destDir, final String keyword)
            throws IOException {
        if (zipFile == null || destDir == null) return null;
        List<File> files = new ArrayList<>();
        ZipFile zf = new ZipFile(zipFile);
        Enumeration<?> entries = zf.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            String entryName = entry.getName();
            if (StringUtils.isEmpty(keyword) || FileUtils.getFileName(entryName).toLowerCase().contains(keyword.toLowerCase())) {
                String filePath = destDir + File.separator + entryName;
                File file = new File(filePath);
                files.add(file);
                if (entry.isDirectory()) {
                    if (!FileUtils.createOrExistsDir(file)) return null;
                } else {
                    if (!FileUtils.createOrExistsFile(file)) return null;
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = new BufferedInputStream(zf.getInputStream(entry));
                        out = new BufferedOutputStream(new FileOutputStream(file));
                        byte buffer[] = new byte[KB];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    } finally {
                        CloseUtils.closeIO(in, out);
                    }
                }
            }
        }
        return files;
    }

    /**
     * 获取压缩文件中的文件路径链表
     *
     * @param zipFilePath 压缩文件路径
     * @return 压缩文件中的文件路径链表
     * @throws IOException IO错误时抛出
     */
    public static List<String> getFilesPath(final String zipFilePath)
            throws IOException {
        return getFilesPath(FileUtils.getFileByPath(zipFilePath));
    }

    /**
     * 获取压缩文件中的文件路径链表
     *
     * @param zipFile 压缩文件
     * @return 压缩文件中的文件路径链表
     * @throws IOException IO错误时抛出
     */
    public static List<String> getFilesPath(final File zipFile)
            throws IOException {
        if (zipFile == null) return null;
        List<String> paths = new ArrayList<>();
        Enumeration<?> entries = getEntries(zipFile);
        while (entries.hasMoreElements()) {
            paths.add(((ZipEntry) entries.nextElement()).getName());
        }
        return paths;
    }

    /**
     * 获取压缩文件中的注释链表
     *
     * @param zipFilePath 压缩文件路径
     * @return 压缩文件中的注释链表
     * @throws IOException IO错误时抛出
     */
    public static List<String> getComments(final String zipFilePath)
            throws IOException {
        return getComments(FileUtils.getFileByPath(zipFilePath));
    }

    /**
     * 获取压缩文件中的注释链表
     *
     * @param zipFile 压缩文件
     * @return 压缩文件中的注释链表
     * @throws IOException IO错误时抛出
     */
    public static List<String> getComments(final File zipFile)
            throws IOException {
        if (zipFile == null) return null;
        List<String> comments = new ArrayList<>();
        Enumeration<?> entries = getEntries(zipFile);
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            comments.add(entry.getComment());
        }
        return comments;
    }

    /**
     * 获取压缩文件中的文件对象
     *
     * @param zipFilePath 压缩文件路径
     * @return 压缩文件中的文件对象
     * @throws IOException IO错误时抛出
     */
    public static Enumeration<?> getEntries(final String zipFilePath)
            throws IOException {
        return getEntries(FileUtils.getFileByPath(zipFilePath));
    }

    /**
     * 获取压缩文件中的文件对象
     *
     * @param zipFile 压缩文件
     * @return 压缩文件中的文件对象
     * @throws IOException IO错误时抛出
     */
    public static Enumeration<?> getEntries(final File zipFile)
            throws IOException {
        if (zipFile == null) return null;
        return new ZipFile(zipFile).entries();
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    ////////////////////////////////////////add by xiaoyu 2017-6-30 18:32:46////////////////////////

    /**
     * 判断Zip压缩包是否加密
     *
     * @param zipFile zip格式压缩包文件
     * @return <code>true</code> encrypt, otherwise <code>false</code>
     * @throws ZipException when passed parameter is null
     */
    public static boolean isZipEncrypted(File zipFile) throws ZipException {
        net.lingala.zip4j.core.ZipFile zipFile1 = new net.lingala.zip4j.core.ZipFile(zipFile);
        return zipFile1.isEncrypted();
    }

    /**
     * 判断Rar压缩包是否加密
     *
     * @param rarFile rar格式的压缩包文件
     * @return <code>true</code> encrypt, otherwise <code>false</code>
     * @throws IOException  IO异常
     * @throws RarException Rar异常
     */
    public static boolean isRarEncrypted(File rarFile)
            throws IOException, RarException {
        Archive archive = new Archive(rarFile);
        boolean result = archive.isEncrypted();
        if (archive != null) {
            archive.close();
        }
        return result;
    }

    /**
     * 验证压缩文件是否合法(是否为文件, 是否为压缩文件), 暂且支持zip和rar格式
     *
     * @param file f
     * @return true or false
     */
    public static boolean isValidPackFile(File file) {
        if (file == null || !file.exists() || !file.isFile() || file.isDirectory()) return false;
        String extension = FileUtils.getFileExtension(file);
        Log.e("zipUtil", file.getName());
        if (TextUtils.isEmpty(extension)) return false;
        if ("zip".equalsIgnoreCase(extension)) {
            try {
                net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(file);
                return zipFile.isValidZipFile(); // 此步骤很耗时
            } catch (ZipException e) {
                e.printStackTrace();
                return false;
            }
        } else if ("rar".equalsIgnoreCase(extension)) {
            try {
                Archive archive = new Archive(file);
                archive.close();
                archive = null;
                return true;
            } catch (RarException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 查验zip格式压缩包密码是否正确
     *
     * @param file f
     * @param pass p
     * @return r
     */
    public static boolean checkZipPassword(File file, String pass) {
        try {
            net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(file);
            try {
                zipFile.setPassword(pass);
            } catch (ZipException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取压缩包内指定目录的所有文件和文件夹
     *
     * @param file             压缩包文件
     * @param targetParentPath 指定目录(根目录时传入"/")
     * @param password         密码
     * @return list, 一旦发生异常则返回null
     */
    public static List<ZipPreviewFileBean> listFiles(File file, String targetParentPath, String password) {
        List<ZipPreviewFileBean> result = new ArrayList<>();
        if (file == null || !file.exists() || !file.isFile() || file.isDirectory()) return null;
        String extension = FileUtils.getFileExtension(file);
        if ("zip".equalsIgnoreCase(extension)) {
            if (!TextUtils.isEmpty(password)) {
                try {
                    net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(file);
                    zipFile.setPassword(password);
                    List<FileHeader> headers = zipFile.getFileHeaders();
                    for (FileHeader header : headers) {
                        String parentPath = FileUtils.getParentPath(header.getFileName());
                        if (targetParentPath.equals(parentPath)) {
                            result.add(new ZipPreviewFileBean(header));
                        }
                    }
                    zipFile = null;
                } catch (ZipException e) {
                    e.printStackTrace();
                    return null;
                }
                return result;
            } else {
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        String parentPath = FileUtils.getParentPath(zipEntry.getName());
                        if (targetParentPath.equals(parentPath)) {
                            result.add(new ZipPreviewFileBean(zipEntry));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    CloseUtils.closeIO(zipFile);
                    zipFile = null;
                }
                return result;
            }
        } else if ("rar".equalsIgnoreCase(extension)) {
            Archive archive = null;
            try {
                archive = new Archive(file);
                List<de.innosystec.unrar.rarfile.FileHeader> headers = archive.getFileHeaders();
                for (de.innosystec.unrar.rarfile.FileHeader header : headers) {
                    String name = FileUtils.formatterRarFileNameCode(header);
                    String parentPath = FileUtils.getParentPath(name);
                    if (targetParentPath.equals(parentPath)) {
                        result.add(new ZipPreviewFileBean(header));
                    }
                }
                return result;
            } catch (RarException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                CloseUtils.closeIO(archive);
                archive = null;
            }
        } else {
            return null;
        }
    }

    /**
     * @param file             f
     * @param targetParentPath t
     * @return l
     * @throws ZipException z
     * @throws IOException  i
     * @see #listFiles(File, String, String)
     */
    public static List<ZipPreviewFileBean> listFiles(File file, String targetParentPath) throws ZipException, IOException {
        return listFiles(file, targetParentPath, null);
    }

    /**
     * 验证压缩包文件是否加密
     *
     * @param file f
     * @return b
     */
    public static boolean isPackFileEncrypted(File file) throws ZipException, IOException, RarException {
        String extension = FileUtils.getFileExtension(file);
        if ("zip".equalsIgnoreCase(extension)) {
            return isZipEncrypted(file);
        } else if ("rar".equalsIgnoreCase(extension)) {
            return isRarEncrypted(file);
        }
        return false;
    }

    /**
     * 判断是否为rar格式的压缩包文件
     *
     * @param file f
     * @return b
     */
    public static boolean isRarFormatFile(File file) {
        String extension = FileUtils.getFileExtension(file);
        return "rar".equalsIgnoreCase(extension);
    }
    /**
     * 判断是否为zip格式的压缩包文件
     *
     * @param file f
     * @return b
     */
    public static boolean isZipFormatFile(File file) {
        String extension = FileUtils.getFileExtension(file);
        return "zip".equalsIgnoreCase(extension);
    }
}

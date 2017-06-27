package com.jb.filemanager.util;

import android.text.TextUtils;

import com.jb.filemanager.function.scanframe.bean.common.FileType;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class FileTypeUtil {
    public static final String[] DOCUMENT = {"docx", "doc", "docm", "dotx",
            "dotm", "xls", "xlsx", "xlsm", "xltx", "xltm", "xlsb", "xlam",
            "pptx", "ppt", "pptm", "ppsx", "ppsm", "potx", "potm", "ppam",
            "pdf"};
    /**
     * TXT和DOCUMENT一样都属于文本类型，但不属于敏感类型，所以区分数组
     */
    public static final String[] TXT = {"txt", "log"};
    public static final String[] VIDEO = {"wmv", "asf", "asx", "rm", "rmvb",
            "mpg", "mpeg", "mpe", "vob", "dv", "3gp", "3g2", "mov", "avi",
            "mkv", "mp4", "m4v", "flv"};
    public static final String[] MUSIC = {"wav", "mp3", "aif", "cd", "midi",
            "wma"};
    public static final String[] APK = {"apk"};
    public static final String[] IMAGE = {"jpg", "bmp", "jpeg", "png", "gif"};
    public static final String[] COMPRESSION = {"rar", "gz", "gtar", "tar",
            "tgz", "z", "zip"};

    /**
     * 总文件后缀个数
     */
    private static final int TOTAL_FILE_TYPE = DOCUMENT.length + TXT.length
            + VIDEO.length + MUSIC.length + APK.length + IMAGE.length
            + COMPRESSION.length;
    /**
     * 敏感文件后缀个数
     */
    private static final int SENSITIVE_FILE_TYPE = DOCUMENT.length
            + VIDEO.length + MUSIC.length + APK.length + IMAGE.length;

    /**
     * 所有文件类型的映射表
     */
    private static HashMap<String, FileType> sFileTypeMap = new HashMap<>(
            TOTAL_FILE_TYPE);
    /**
     * 敏感文件的映射表
     */
    private static HashMap<String, FileType> sSensitiveFileTypeMap = new HashMap<>(
            SENSITIVE_FILE_TYPE);

    /**
     * 将文件类型Set存入到HashMap中，再进行查找;<br>
     * HashMap的查找效率比直接遍历HashSet高，经测试有3.2倍的提速
     */
    static {
        // 消耗时间6ms，测试机型Nexus 5
        initFileTypeMap(VIDEO, FileType.VIDEO);
        initFileTypeMap(MUSIC, FileType.MUSIC);
        initFileTypeMap(DOCUMENT, FileType.DOCUMENT);
        initFileTypeMap(TXT, FileType.DOCUMENT);
        initFileTypeMap(APK, FileType.APK);
        initFileTypeMap(IMAGE, FileType.IMAGE);
        initFileTypeMap(COMPRESSION, FileType.COMPRESSION);

        initSensitiveFileTypeMap(VIDEO, FileType.VIDEO);
        initSensitiveFileTypeMap(MUSIC, FileType.MUSIC);
        initSensitiveFileTypeMap(DOCUMENT, FileType.DOCUMENT);
        initSensitiveFileTypeMap(APK, FileType.APK);
        initSensitiveFileTypeMap(IMAGE, FileType.IMAGE);
    }

    /**
     * 初始化所有文件的映射表
     *
     * @param array 文件后缀数组
     * @param type  文件类型
     */
    private static void initFileTypeMap(String[] array, FileType type) {
        for (String file : array) {
            sFileTypeMap.put(file, type);
        }
    }

    /**
     * 初始化敏感文件的映射表
     *
     * @param array 文件后缀数组
     * @param type  文件类型
     */
    private static void initSensitiveFileTypeMap(String[] array, FileType type) {
        for (String file : array) {
            sSensitiveFileTypeMap.put(file, type);
        }
    }

    /**
     * 根据后缀名判断文件类型
     */
    public static FileType getFileTypeFromPostfix(String postfix) {
        FileType fileType = null;
        if (!TextUtils.isEmpty(postfix)) {
            fileType = sFileTypeMap.get(postfix);
        }
        return fileType != null ? fileType : FileType.OTHER;
    }

    /**
     * 判断文件类型
     */
    public static FileType getFileType(String path) {
        return getFileTypeCommon(sFileTypeMap, path);
    }

    /**
     * 判断敏感文件类型
     */
    public static FileType getFileTypeSensitive(String path) {
        return getFileTypeCommon(sSensitiveFileTypeMap, path);
    }

    /**
     * 获取文件类型的公共方法
     *
     * @param map  查询的表
     * @param path 文件路径
     */
    private static FileType getFileTypeCommon(HashMap<String, FileType> map,
                                              String path) {
        String ext = com.jb.filemanager.util.file.FileUtil.getExtension(path).toLowerCase(Locale.US);
        if (TextUtils.isEmpty(ext)) {
            return FileType.OTHER;
        }
        FileType fileType = map.get(ext);
        if (fileType == null) {
            return FileType.OTHER;
        }
        return fileType;
    }
}
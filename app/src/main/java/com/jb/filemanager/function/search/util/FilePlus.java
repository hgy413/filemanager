package com.jb.filemanager.function.search.util;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by nieyh on 17-7-5.
 * 文件plus <br/>
 * 不要纠结命名, 除非你告诉我一个更加好的名字
 */

public class FilePlus {

    /**
     * 这里的常量中间隔开这么多
     * 是为了以后扩展使用
     * */
    //音乐文件
    private static final int FILE_TYPE_MP3 = 1;

    //视频文件
    private static final int FILE_TYPE_MP4 = 21;

    //图片文件
    private static final int FILE_TYPE_JPEG = 31;
    private static final int FILE_TYPE_PNG = 32;

    //文档
    private static final int FILE_TYPE_TEXT = 100;
    private static final int FILE_TYPE_PDF = 101;
    private static final int FILE_TYPE_MS_WORD = 102;
    private static final int FILE_TYPE_MS_POWERPOINT = 103;
    //压缩文件
    private static final int FILE_TYPE_ZIP = 107;
    private static final int FILE_TYPE_RAR = 108;

    //未知类型
    private static final int FILE_TYPE_UNKNOW = -1;
    //路径
    private static final int FILE_TYPE_DIRECTORY = -2;

    /**
     * 文件类型Map
     * */
    private static final HashMap<String, Integer> sFileTypeMap
            = new HashMap<>();

    static {
        sFileTypeMap.put("MP3", FILE_TYPE_MP3);

        sFileTypeMap.put("MP4", FILE_TYPE_MP4);

        sFileTypeMap.put("JPG", FILE_TYPE_JPEG);
        sFileTypeMap.put("JPEG", FILE_TYPE_JPEG);
        sFileTypeMap.put("PNG", FILE_TYPE_PNG);

        sFileTypeMap.put("TXT", FILE_TYPE_TEXT);
        sFileTypeMap.put("PDF", FILE_TYPE_PDF);
        sFileTypeMap.put("DOC", FILE_TYPE_MS_WORD);
        sFileTypeMap.put("DOCX", FILE_TYPE_MS_WORD);
        sFileTypeMap.put("PPT", FILE_TYPE_MS_POWERPOINT);

        sFileTypeMap.put("ZIP", FILE_TYPE_ZIP);
        sFileTypeMap.put("RAR", FILE_TYPE_RAR);
    }

    /**
     * 是否是视频
     * */
    public static boolean isVideoFileType(File file) {
        int type = getFileType(file);
        return type == FILE_TYPE_MP4;
    }

    /**
     * 是否是图片类型
     * */
    public static boolean isImageFileType(File file) {
        int type = getFileType(file);
        return type == FILE_TYPE_JPEG || type == FILE_TYPE_PNG;
    }

    /**
     * 是否是图片类型
     * */
    public static boolean isDirectoryType(int type) {
        return type == FILE_TYPE_DIRECTORY;
    }

    /**
     * 获取文件类型
     * */
    public static int getFileType(File file) {
        if (file == null) {
            return FILE_TYPE_UNKNOW;
        }
        if (file.isDirectory()) {
            return FILE_TYPE_DIRECTORY;
        }
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot < 0) {
            return FILE_TYPE_UNKNOW;
        }
        String external = name.substring(lastDot + 1).toUpperCase(Locale.ROOT);
        if (sFileTypeMap.containsKey(external)) {
            //返回文件类型
            return sFileTypeMap.get(external);
        }
        //返回未知
        return FILE_TYPE_UNKNOW;
    }
}

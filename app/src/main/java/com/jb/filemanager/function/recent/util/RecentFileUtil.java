package com.jb.filemanager.function.recent.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.util.FileUtils;

import java.io.File;

/**
 * Created by xiaoyu on 2017/7/14 15:21.<br>
 * 处理最近文件的时间范围的工具类
 */

public final class RecentFileUtil {

    public static final int VALUE_ONE_DAY_TIME = 24 * 60;
    public static final int VALUE_TWO_DAY_TIME = VALUE_ONE_DAY_TIME * 2;
    public static final int VALUE_THREE_DAY_TIME = VALUE_ONE_DAY_TIME * 3;
    public static final int VALUE_FOUR_DAY_TIME = VALUE_ONE_DAY_TIME * 4;
    public static final int VALUE_FIVE_DAY_TIME = VALUE_ONE_DAY_TIME * 5;
    public static final int VALUE_SIX_DAY_TIME = VALUE_ONE_DAY_TIME * 6;
    public static final int VALUE_SEVEN_DAY_TIME = VALUE_ONE_DAY_TIME * 7;
    public static final int VALUE_EARLY_TIME = VALUE_ONE_DAY_TIME * 8;
    // 算作最近文件的最长时间 : 30天 单位毫秒
    public static final long MAX_MODIFY_SCAN_TIME = VALUE_ONE_DAY_TIME * 30 * 60 * 1000L;

    private RecentFileUtil() {
        throw new IllegalStateException("don't try to institute me");
    }

    /**
     * 1个小时内，按照几分钟内，一天是按照几个小时内, 一周内按照天, 超过一周更早
     *
     * @param deltaTime 当前时间与最后修改时间的差值
     * @return 1-59分钟 1-23小时 1-7天 更早
     */
    public static int calculateWithinMinute(long deltaTime) {
        int min = (int) (deltaTime / 1000 / 60); // 毫秒转化为分钟
        // 1小时以内, 返回分钟值
        if (min < 60) {
            return min;
        }
        // 1天以内, 返回整数小时转化为分钟的值
        if (min < VALUE_ONE_DAY_TIME) {
            return (min / 60 + 1) * 60;
        }
        // 1周以内, 返回整数天转化为分钟的值
        if (min < VALUE_SEVEN_DAY_TIME) {
            return (min / VALUE_ONE_DAY_TIME + 1) * VALUE_ONE_DAY_TIME;
        }
        // 超过一周, 返回VALUE_EARLY_TIME
        return VALUE_EARLY_TIME;
    }

    /**
     * 最近文件类型 : 文件或者图片
     *
     * @param filePath 文件名
     * @return f
     */
    public static boolean isPictureType(String filePath) {
        String extension = FileUtils.getFileExtension(filePath);
        if (TextUtils.isEmpty(extension)) return false;
        return "bmp".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension)
                || "png".equalsIgnoreCase(extension);
    }

    /**
     * 将分钟转化字符串以显示
     *
     * @param min 分钟
     * @return s
     */
    public static String formatWithinTime(Context context, int min) {
        // 1小时以内, 返回分钟值
        if (min < 60) {
            return context.getString(R.string.winthin_min, min);
        }
        // 1天以内, 返回整数小时转化为分钟的值
        if (min < VALUE_ONE_DAY_TIME) {
            return context.getString(R.string.within_hours, min / 60);
        }
        // 1周以内, 返回整数天转化为分钟的值
        if (min < VALUE_SEVEN_DAY_TIME) {
            return context.getString(R.string.within_days, min / 60 / 24);
        }
        // 超过一周, 返回VALUE_EARLY_TIME
        return context.getString(R.string.early);
    }

    private static final String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"}, {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
            {".sh", "text/plain"}, {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"}, {"", "*/*"}};

    private static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if ("".equals(end)) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 调用系统应用打开文件
     *
     * @param context c
     * @param file f
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
        }
    }
}

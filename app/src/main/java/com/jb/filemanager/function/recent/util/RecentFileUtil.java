package com.jb.filemanager.function.recent.util;

import android.text.TextUtils;

import com.jb.filemanager.function.zipfile.util.FileUtils;

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
     * @param filePath 文件名
     * @return f
     */
    public static boolean isPictureType(String filePath) {
        String extension = FileUtils.getFileExtension(filePath);
        if (TextUtils.isEmpty(extension)) return false;
        return "bmp".equalsIgnoreCase(extension) || "jpg".equalsIgnoreCase(extension)
                || "png".equalsIgnoreCase(extension);
    }
}

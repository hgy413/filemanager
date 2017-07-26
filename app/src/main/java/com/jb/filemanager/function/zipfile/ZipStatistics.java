package com.jb.filemanager.function.zipfile;

import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.Logger;

/**
 * Created by xiaoyu on 2017/7/26 10:27.
 */

public final class ZipStatistics {

    // 点击折叠分类
    public static final String ZIP_SORT = "c000_zip_sort";
    // 点击分类后复选框
    public static final String ZIP_SORT_SELECT = "c000_zip_sortselect";
    // 点击单个文件复选框
    public static final String ZIP_SINGLE = "c000_zip_single";
    // 点击查看
    public static final String ZIP_LOOK = "c000_zip_look";
    // 点击解压
    public static final String ZIP_EXTRACT = "c000_zip_extrat";
    // 点击取消
    public static final String ZIP_CANCEL = "c000_zip_cancel";
    // 查看内点击单个文件
    public static final String ZIP_OPEN = "c000_zip_open";
    // 查看内点击复选框
    public static final String ZIP_SELECT = "c000_zip_select";
    // 查看内点击解压
    public static final String ZIP_INEXTRACT = "c000_zip_inextrat";
    // 解压过程展示
    public static final String ZIP_GO = "f000_zip_go";
    // 解压过程选择最小化
    public static final String ZIP_SMALL = "c000_zip_small";
    // 解压后点击See
    public static final String ZIP_SEE = "c000_zip_see";

    public static void upload(String id) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = id;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("ZipStatistics", id);
    }
}

package com.jb.filemanager.function.image;

import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.Logger;

/**
 * Created by xiaoyu on 2017/7/26 14:24.
 */

public final class ImageStatistics {
    //点击折叠分类
    public static final String IMG_GROUP_FLOD = "c000_pho_sort";
    //点击分类后复选框
    public static final String IMG_GROUP_CHECK = "c000_pho_sortselcet";
    //点击单个文件复选框
    public static final String IMG_ITEM_CHECK = "c000_pho_select";
    //点击搜索
    public static final String IMG_SEARCH_CLI = "c000_pho_search";
    //点击剪切
    public static final String IMG_CUT_CLI = "c000_pho_cut";
    //点击复制
    public static final String IMG_COPY_CLI = "c000_pho_copy";
    //点击粘贴
    public static final String IMG_PASTE_CLI = "c000_pho_paste";
    //点击删除
    public static final String IMG_DELETE_CLI = "c000_pho_delete";
    //点击More
    public static final String IMG_MORE_CLI = "c000_pho_more";
    //点击Details
    public static final String IMG_DETAILS_CLI = "c000_pho_detail";
    //点击Rename
    public static final String IMG_RENAME_CLI = "c000_pho_rename";
    //点击单个图片
    public static final String IMG_ITEM_CLI = "c000_pho_single";
    //扩大图片
    public static final String IMG_2_LARGE = "t000_pho_big";
    //左右切换图片
    public static final String IMG_DETAIL_SWTICH = "t000_pho_left";
    //缩小图片
    public static final String IMG_2_SMALL = "t000_pho_small";
    //点击设置为壁纸
    public static final String IMG_SET_WALLPAPER = "c000_pho_wallpaper";
    //点击删除图片（图片内）
    public static final String IMG_DETAIL_DELETE = "c000_pho_indelete";
    //退出图片管理
    public static final String IMG_EXIT = "c000_pho_exit";

    public static void upload(String id) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = id;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("ImageStatistics", id);
    }

    public static void upload(String id, int entrance) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = id;
        bean.mEntrance = String.valueOf(entrance);
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("ImageStatistics", id + "--" + entrance);
    }
}

package com.jb.filemanager.function.recent;

import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.Logger;

/**
 * Created by xiaoyu on 2017/7/26 11:36.
 */

public final class RecentStatistics {
    // 点击折叠分类
    public static final String RECENT_SORT = "c000_recent_sort";
    // 点击分类后复选框
    public static final String RECENT_SORTSELECT = "c000_recent_sortselect";
    // 点击单个文件复选框
    public static final String RECENT_SELECT = "c000_recent_select";
    // 点击搜索
    public static final String RECENT_SEARCH = "c000_recent_search";
    // 点击剪切
    public static final String RECENT_CUT = "c000_recent_cut";
    // 点击复制
    public static final String RECENT_COPY = "c000_recent_copy";
    // 点击粘贴
    public static final String RECENT_PASTE = "c000_recent_paste";
    // 点击删除
    public static final String RECENT_DELETE = "c000_recent_delete";
    // 点击more
    public static final String RECENT_MORE = "c000_recent_more";
    // 点击details
    public static final String RECENT_DETAIL = "c000_recent_detail";
    // 点解rename
    public static final String RECENT_RENAME = "c000_recent_rename";
    // 点击单个文件
    public static final String RECENT_SINGLE = "c000_recent_single";

    public static void upload(String id) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = id;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d("RecentStatistics", id);
    }
}

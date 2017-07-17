package com.jb.filemanager.function.recent.listener;

import com.jb.filemanager.function.recent.bean.BlockBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 13:46.
 */

public interface RecentFileInnerListener {
    void onDataFlushComplete(List<BlockBean> data);
}

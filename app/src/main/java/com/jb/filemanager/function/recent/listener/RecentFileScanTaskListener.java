package com.jb.filemanager.function.recent.listener;

import com.jb.filemanager.function.recent.bean.BlockBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/14 14:25.
 */

public interface RecentFileScanTaskListener {
    void onPreScan();
    void onScanning();
    void onPostScan(List<BlockBean> result);
    void onScanCancel();
    void onScanError();
}

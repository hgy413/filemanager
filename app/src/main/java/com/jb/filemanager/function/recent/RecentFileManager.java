package com.jb.filemanager.function.recent;

import android.util.Log;

import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentFileScanTaskListener;
import com.jb.filemanager.function.recent.task.RecentFileScanTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/13 18:50.
 * <p>
 *     应用启动时遍历所有文件包括手机存储和SD card,按照最后修改时间来分组:<br>
 *         <ol>
 *         <li>时间是第一依据</li>
 *         <li>父目录是第二依据</li>
 *         </ol>
 *     换句话讲, 划分到同一组的文件一定是同一目录下最后修改时间在同一范围内.<br>
 *
 *      之后, 一直监视文件系统根目录和SD card
 * </p>
 */

public final class RecentFileManager implements RecentFileScanTaskListener {

    private static RecentFileManager sInstance;
    private List<BlockBean> mBlockList = new ArrayList<>();

    private RecentFileManager() {
    }

    public static RecentFileManager getInstance() {
        if (sInstance == null) {
            synchronized (RecentFileManager.class) {
                if (sInstance == null) {
                    sInstance = new RecentFileManager();
                }
            }
        }
        return sInstance;
    }

    public void scanAllFile() {
        RecentFileScanTask recentFileScanTask = new RecentFileScanTask();
        recentFileScanTask.setListener(this);
        recentFileScanTask.execute();
    }

    // ---------------扫描全盘任务回调接口--------开始---------------------
    @Override
    public void onPreScan() {

    }

    @Override
    public void onScanning() {

    }

    @Override
    public void onPostScan(List<BlockBean> result) {
        mBlockList.clear();
        mBlockList.addAll(result);
        for (BlockBean bean : result) {
            String dirName = bean.getBlockDirName();
            int childCount = bean.getChildCount();
            Log.e("Recent", "dirName = " + dirName + ";;count = " + childCount);
        }
    }

    @Override
    public void onScanCancel() {

    }

    @Override
    public void onScanError() {

    }
    // ---------------扫描全盘任务回调接口--------结束---------------------

}

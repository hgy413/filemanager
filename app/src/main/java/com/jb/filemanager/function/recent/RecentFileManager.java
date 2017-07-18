package com.jb.filemanager.function.recent;

import android.util.Log;

import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentFileInnerListener;
import com.jb.filemanager.function.recent.listener.RecentFileScanTaskListener;
import com.jb.filemanager.function.recent.task.RecentFileScanTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/13 18:50.
 * <p>
 * 应用启动时遍历所有文件包括手机存储和SD card,按照最后修改时间来分组:<br>
 * <ol>
 * <li>时间是第一依据</li>
 * <li>父目录是第二依据</li>
 * </ol>
 * 换句话讲, 划分到同一组的文件一定是同一目录下最后修改时间在同一范围内.<br>
 * <p>
 * 之后, 一直监视文件系统根目录和SD card
 * </p>
 */

public final class RecentFileManager implements RecentFileScanTaskListener {

    private static RecentFileManager sInstance;
    private List<BlockBean> mBlockList = new ArrayList<>();
    private WeakReference<RecentFileInnerListener> mReference;
    private boolean mIsScanning = false;
    private RecentFileScanTask mRecentFileScanTask;

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

    public List<BlockBean> getRecentFiles() {
        return mBlockList;
    }

    public void scanAllFile() {
        if (mIsScanning) return;
        mRecentFileScanTask = new RecentFileScanTask();
        mRecentFileScanTask.setListener(this);
        mRecentFileScanTask.execute();
    }

    public void setFlushDataCallbackListener(RecentFileInnerListener listener) {
        mReference = new WeakReference<RecentFileInnerListener>(listener);
    }

    /**
     * 页面退出时调用
     */
    public void cancelScanTask() {
        if (mRecentFileScanTask != null) {
            mRecentFileScanTask.cancel(true);
        }
    }
    // ---------------扫描全盘任务回调接口--------开始---------------------
    @Override
    public void onPreScan() {
        Log.e("recent", "开始扫描");
        mIsScanning = true;
    }

    @Override
    public void onScanning() {

    }

    @Override
    public void onPostScan(List<BlockBean> result) {
        mBlockList.clear();
        mBlockList.addAll(result);
        mIsScanning = false;
        if (mReference != null) {
            RecentFileInnerListener listener = mReference.get();
            if (listener != null) {
                listener.onDataFlushComplete(mBlockList);
            }
        }
    }

    @Override
    public void onScanCancel() {
        mIsScanning = false;

    }

    @Override
    public void onScanError() {
        mIsScanning = false;

    }
    // ---------------扫描全盘任务回调接口--------结束---------------------

}

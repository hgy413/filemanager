package com.jb.filemanager.function.recent.task;

import android.os.AsyncTask;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentFileScanTaskListener;
import com.jb.filemanager.function.recent.util.RecentFileUtil;
import com.jb.filemanager.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/13 19:50.
 * <p>
 * 应用开启扫描全盘, 找出最近文件
 * </p>
 */

public class RecentFileScanTask extends AsyncTask<Void, Integer, List<BlockBean>> {

    private static final int DEPTH_THRESHOLD = 4;
    private List<BlockBean> mGroupList = new ArrayList<>();
    private RecentFileScanTaskListener mListener;
    private long mFlagTime = System.currentTimeMillis();

    public void setListener(RecentFileScanTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mGroupList.clear();
        mFlagTime = System.currentTimeMillis();
        if (mListener != null) {
            mListener.onPreScan();
        }
    }

    @Override
    protected List<BlockBean> doInBackground(Void... params) {
        for (String path : StorageUtil.getAllExternalPaths(TheApplication.getAppContext())) {
            if (isCancelled()) return null;
            File root = new File(path);
            if (root.isDirectory()) {
                for (File dir : root.listFiles()) {
                    if (isCancelled()) return null;
                    scanPath(dir, 0);
                }
            }
        }
        return mGroupList;
    }

    // 扫描传入的路径
    // 扫描某个具体的文件夹
    // 是文件夹略过, 是文件进行判断
    // 一个文件夹可能不需要BlockBean, 也可能需要一个或多个BlockBean
    // 注意图片和文件不可放到同一个Block
    private void scanPath(File dir, int depth) {
        if (depth > DEPTH_THRESHOLD) return;
        if (!dir.exists() || !dir.isDirectory()) return;
        List<BlockBean> currentDir = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (isCancelled()) return;
            if (file.isDirectory()) {
                scanPath(file, depth + 1);
            } else {
                long deltaTime = mFlagTime - file.lastModified();
                // 将deltaTime转化, 小于最长时间值进行操作
                if (deltaTime <= RecentFileUtil.MAX_MODIFY_SCAN_TIME) {
                    // 可加入Block需符合三个条件
                    // 1. 同一文件夹下的文件
                    // 2. 同一修改时间的文件
                    // 3. 同一类型的文件(图片或者文件)
                    // 当前操作处于同一目录下, 所以条件1必然符合.需判定条件2和3
                    boolean isExist = false;
                    int blockTime = RecentFileUtil.calculateWithinMinute(deltaTime);
                    for (BlockBean blockBean : currentDir) {
                        if (isCancelled()) return;
                        if (blockBean.getWithinTime() == blockTime && (RecentFileUtil
                                .isPictureType(file.getName()) == blockBean.isPictureType())) {
                            blockBean.addBlockItemFile(file);
                            isExist = true;
                        }
                    }
                    if (!isExist) {
                        BlockBean blockBean = new BlockBean(file);
                        currentDir.add(blockBean);
                    }
                }
            }
        }
        // 当前文件夹遍历完毕
        for (BlockBean blockBean : currentDir) {
            if (blockBean.getChildCount() > 1) {
                mGroupList.add(blockBean);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mListener != null) {
            mListener.onScanning();
        }
    }

    @Override
    protected void onCancelled() {
        mListener.onScanCancel();
    }

    @Override
    protected void onPostExecute(List<BlockBean> v) {
        sortResult();
        if (mListener != null) {
            if (v == null) {
                mListener.onScanError();
            } else {
                mListener.onPostScan(v);
            }
        }
    }

    private void sortResult() {
        Collections.sort(mGroupList, new Comparator<BlockBean>() {
            @Override
            public int compare(BlockBean o1, BlockBean o2) {
                return o1.getWithinTime() - o2.getWithinTime();
            }
        });
    }

}

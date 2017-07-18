package com.jb.filemanager.function.recent.presenter;

import com.jb.filemanager.function.recent.RecentFileManager;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentFileInnerListener;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:33.
 */

public class RecentFilePresenter implements RecentFileContract.Presenter, RecentFileInnerListener {

    private RecentFileContract.View mView;
    private List<BlockBean> mBlockList;

    public RecentFilePresenter(RecentFileContract.View view) {
        mView = view;
    }

    @Override
    public void onCreate() {
        mBlockList = RecentFileManager.getInstance().getRecentFiles();
        mView.setListViewData(mBlockList);
        RecentFileManager.getInstance().setFlushDataCallbackListener(this);
        RecentFileManager.getInstance().scanAllFile();
    }

    @Override
    public void onDestroy() {
        RecentFileManager.getInstance().cancelScanTask();
    }

    @Override
    public void onDataFlushComplete(List<BlockBean> data) {
        mView.notifyListDataChanged();
    }
}

package com.jb.filemanager.function.recent.presenter;

import com.jb.filemanager.function.recent.RecentFileManager;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.bean.BlockItemFileBean;
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
    public void onItemCheckChanged() {
        int selectCount = 0;
        int totalCount = 0;
        for (BlockBean blockBean : mBlockList) {
            List<BlockItemFileBean> itemFiles = blockBean.getItemFiles();
            for (BlockItemFileBean itemFile : itemFiles) {
                totalCount ++;
                if (itemFile.isSelected()) {
                    selectCount ++;
                }
            }
        }
        if (selectCount > 0) {
            mView.switchSelectMode(true);
            mView.setSearchTitleSelectBtnState(selectCount == totalCount ? 2 : 1);
            mView.setSearchTitleSelectCount(selectCount);
        } else {
            mView.switchSelectMode(false);
        }
    }

    @Override
    public void onTitleCancelBtnClick() {
        changeAllItemState(false);
        mView.switchSelectMode(false);
        mView.notifyListDataChanged();
    }

    @Override
    public void onTitleSelectBtnClick() {
        int selectCount = 0;
        int totalCount = 0;
        for (BlockBean blockBean : mBlockList) {
            List<BlockItemFileBean> itemFiles = blockBean.getItemFiles();
            for (BlockItemFileBean itemFile : itemFiles) {
                totalCount ++;
                if (itemFile.isSelected()) {
                    selectCount ++;
                }
            }
        }
        if (selectCount == totalCount) {
            // 设为全不选
            changeAllItemState(false);
            mView.setSearchTitleSelectBtnState(0);
            mView.setSearchTitleSelectCount(0);
            mView.switchSelectMode(false);
        } else {
            // 设为全选
            changeAllItemState(true);
            mView.setSearchTitleSelectBtnState(2);
            mView.setSearchTitleSelectCount(totalCount);
        }
        mView.notifyListDataChanged();
    }

    private void changeAllItemState(boolean isSelect) {
        for (BlockBean blockBean : mBlockList) {
            List<BlockItemFileBean> itemFiles = blockBean.getItemFiles();
            for (BlockItemFileBean itemFile : itemFiles) {
                itemFile.setSelected(isSelect);
            }
        }
    }

    @Override
    public void onDataFlushComplete(List<BlockBean> data) {
        mView.setSearchTitleSelectBtnState(0);
        mView.setSearchTitleSelectCount(0);
        mView.switchSelectMode(false);
        mView.notifyListDataChanged();
    }
}

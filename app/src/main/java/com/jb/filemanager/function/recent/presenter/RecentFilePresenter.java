package com.jb.filemanager.function.recent.presenter;

import android.util.Log;

import com.jb.filemanager.function.recent.RecentFileManager;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.bean.BlockItemFileBean;
import com.jb.filemanager.function.recent.listener.RecentFileInnerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:33.
 */

public class RecentFilePresenter implements RecentFileContract.Presenter, RecentFileInnerListener {

    private RecentFileContract.View mView;
    private List<BlockBean> mBlockList;
    private ArrayList<File> mCurrentSelect = new ArrayList<>();

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
        int totalCount = 0;
        mCurrentSelect.clear();
        for (BlockBean blockBean : mBlockList) {
            List<BlockItemFileBean> itemFiles = blockBean.getItemFiles();
            for (BlockItemFileBean itemFile : itemFiles) {
                totalCount ++;
                if (itemFile.isSelected()) {
                    mCurrentSelect.add(new File(itemFile.getFilePath()));
                }
            }
        }
        if (mCurrentSelect.size() > 0) {
            mView.switchSelectMode(true);
            mView.setSearchTitleSelectBtnState(mCurrentSelect.size() == totalCount ? 2 : 1);
            mView.setSearchTitleSelectCount(mCurrentSelect.size());
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

    @Override
    public ArrayList<File> getCurrentSelectFile() {
        return mCurrentSelect;
    }

    @Override
    public void afterCopy() {
        onTitleCancelBtnClick();
    }

    @Override
    public void afterCut() {
        onTitleCancelBtnClick();
    }

    @Override
    public void afterRename() {
        onTitleCancelBtnClick();
    }

    @Override
    public void afterDelete() {
        onTitleCancelBtnClick();
        mView.switchWidgetsState(true);
        RecentFileManager.getInstance().scanAllFile();
    }

    @Override
    public void reloadData() {
        mView.switchWidgetsState(true);
        RecentFileManager.getInstance().scanAllFile();
    }

    private void changeAllItemState(boolean isSelect) {
        mCurrentSelect.clear();
        for (BlockBean blockBean : mBlockList) {
            List<BlockItemFileBean> itemFiles = blockBean.getItemFiles();
            for (BlockItemFileBean itemFile : itemFiles) {
                itemFile.setSelected(isSelect);
                if (isSelect) {
                    mCurrentSelect.add(new File(itemFile.getFilePath()));
                }
            }
        }
    }

    @Override
    public void onDataFlushComplete(List<BlockBean> data) {
        mView.switchWidgetsState(false);
        mView.setSearchTitleSelectBtnState(0);
        mView.setSearchTitleSelectCount(0);
        mView.switchSelectMode(false);
        mView.notifyListDataChanged();
    }
}

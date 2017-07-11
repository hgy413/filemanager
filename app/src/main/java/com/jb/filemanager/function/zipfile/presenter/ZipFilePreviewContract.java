package com.jb.filemanager.function.zipfile.presenter;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/30 15:04.
 */

public interface ZipFilePreviewContract {
    interface View {
        void addBreadcrumbRoot(String rootDir);
        void updateListData(List<ZipPreviewFileBean> data);
        void showProgressDialog();
        void hideProgressDialog();
        void navigationBackward(boolean isEmpty);
        void navigationForward(String path);
        void showToast(String toast);
        void setExtractBtnVisibility(boolean isShow);
    }

    interface Presenter {
        void onCreate(String filePath, String password);
        void onListItemClick(ZipPreviewFileBean item);
        void onProgressDialogCancel();
        void onExtractFiles();
        void onBreadcrumbClick(String path);
        void onBackPressed();
        void onItemStateClick();
    }
}

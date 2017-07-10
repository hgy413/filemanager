package com.jb.filemanager.function.zipfile.presenter;

import com.jb.filemanager.function.zipfile.bean.ZipFileGroupBean;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/30 10:10.
 */

public interface ZipActivityContract {
    interface View {
        void setWidgetsState(boolean isLoading);
        void setListData(List<ZipFileGroupBean> data);
        void notifyDataSetChanged();
        void showOperationDialog(ZipFileItemBean fileItem);
        void hideMoreOperator();
        void showMoreOperator(int count);
    }
    interface Presenter{
        void onCreate();
        void onItemClick(int groupPosition, int childPosition);// 弹窗
        void onItemStateChange();
        void extractZipFile(ZipFileItemBean fileItem);
        void onDestroy();
    }
}

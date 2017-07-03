package com.jb.filemanager.function.zipfile;

/**
 * Created by xiaoyu on 2017/6/30 15:04.
 */

public interface ZipFilePreViewContract {
    interface View {
        void setWidgetsState(boolean isLoadingData);
        void setListData();
    }

    interface Presenter {

    }
}

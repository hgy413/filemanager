package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.bean.IntruderDisplayBean;

import java.util.List;

/**
 * Created by nieyh on 2017/1/4.
 */

public interface IntruderGalleryContract {
    interface View {
        void showDataLoading();
        void showDataLoaded();
        void showGalleryData(List<IntruderDisplayBean> intruderDisplayBeanList);
        void showNoDataLayout();
    }

    interface Presenter {
        void start();
        void release();
    }
}

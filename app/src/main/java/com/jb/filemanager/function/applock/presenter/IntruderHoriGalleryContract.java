package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;
import com.jb.filemanager.function.applock.model.bean.IntruderDisplaySubBean;

import java.util.List;

/**
 * Created by nieyh on 2017/1/6.
 */

public interface IntruderHoriGalleryContract {

    interface Persenter {
        void loadData();
        void dealViewDestory();
        void dealSlideToLastPhoto();
        void dealSlideToNextPhoto();
        String getPhotoPath();
        int getCurrentPos();
    }

    interface View {
        void showDataLoading();
        void showDataLoaded();
        void showPhotoData(List<IntruderDisplaySubBean> subBeans);
        void onFinish();
        void refreshNotice(String notice);
        void scrollToPosition(int currentPosition, int time);
    }

    interface Support {
        void updatePhotoData();
        List<AntiPeepBean> gainUnreadPhotos();
        void updatePhotoAllToReaded();
        void deletePhoto(AntiPeepBean antiPeepBean);
    }
}

package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.manager.AntiPeepDataManager;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;

import java.util.List;

/**
 * Created by nieyh on 2017/1/6.
 */

public class IntruderHoriGallerySupport implements IntruderHoriGalleryContract.Support {

    @Override
    public void updatePhotoData() {
        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).updateUnreadPhoto();
    }

    @Override
    public List<AntiPeepBean> gainUnreadPhotos() {
        return AntiPeepDataManager.getInstance(TheApplication.getAppContext()).getUnreadPhotoAfterUpdate();
    }

    @Override
    public void updatePhotoAllToReaded() {
        // 设置目前的入侵者图片都已读，下次不再弹出
        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).setAllPhotoRead();
    }

    @Override
    public void deletePhoto(AntiPeepBean antiPeepBean) {
        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).deletePhoto(antiPeepBean);
    }
}

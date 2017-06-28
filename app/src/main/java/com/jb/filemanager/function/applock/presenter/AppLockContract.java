package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;

import java.util.List;

/**
 * Created by nieyh on 2017/1/3.
 */

public interface AppLockContract {
    interface Presenter {
        void loadData();
        void search(String keyWord);
        void release();
        void refreshOperateButState();
        void dealiIntruderEntranceOnclick();
        void dealUpdateLockerInfo();
    }

    interface View {
        void showAppLockGroupData(List<AppLockGroupData> appLockGroupDataList);
        void showDataLoading();
        void showDataLoaded();
        void showIntruderTipDialog();
        void showIntruderTipOpened();
        void showIntruderTipClosed();
        void showIntruderPhotoCounts(int counts);
        void gotoIntruderVertGallery();
    }

    interface Support extends ITaskSupport {
        String getAppLockGroupName();
        boolean getIntruderSwitcherState();
        List<LockerItem> getAppLockAppDatas();
        void saveLockerInfo(List<LockerItem> lockerItemList, List<LockerItem> unLockerItemList);
        void updateIntruderPhoto();
        int getIntruderPhotoSize();
        void startAppLockerMonitor();
        void stopAppLockerMonitor();
    }
}

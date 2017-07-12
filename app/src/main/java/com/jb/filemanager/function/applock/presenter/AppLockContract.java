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
        void dealUpdateLockerInfo();
    }

    interface View {
        void showAppLockGroupData(List<AppLockGroupData> appLockGroupDataList);
        void showDataLoading();
        void showDataLoaded();
        void showLockAppsNum(int nums);
    }

    interface Support {
        String[] getFloatListGroupTitle();
        void toAsynWork(Runnable work);
        void toUiWork(Runnable work, long delay);
        void removeUiWork(Runnable work);
        List<LockerItem> getAppLockAppDatas();
        List<LockerItem> getRecommedAppDatas();
        void saveLockerInfo(List<LockerItem> lockerItemList, List<LockerItem> unLockerItemList);
        void startAppLockerMonitor();
        void stopAppLockerMonitor();
    }
}

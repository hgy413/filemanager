package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.bean.AppLockGroupData;
import com.jb.filemanager.function.applock.model.bean.LockerItem;

import java.util.List;

/**
 * Created by nieyh on 2016/12/30.
 */

public interface AppLockPreContract {

    interface View {
        void showDataLoading();
        void showDataLoadFinish();
        void showDefaultRecommendAppsNum(int num);
        void showAppDatas(List<AppLockGroupData> appLockGroupDataList);
        void gotoAppLockerView();
        void gotoSetPsdView();
        void showPermisstionGuideView();
    }

    interface Presenter {
        void loadData();
        void release();
        void dealPermissionCheck();
        void dealResume();
        void handleOperateClick();
        void cacheInitInfo(String passcode, String answer, String question, boolean isLockForLeave);
        void search(String keyWord);
    }

    interface Support {
        List<LockerItem> getAppLockAppDatas();
        List<LockerItem> getRecommedAppDatas();
        String[] getFloatListGroupTitle();
        void toUiWork(Runnable work, long delay);
        void removeUiWork(Runnable work);
        void toAsynWork(Runnable work);
        boolean isHaveUsageStatePremisstion();
        void saveLockerInfo(List<LockerItem> allLockerItemList);
        void savePasscodeAndQuestion(boolean isPatternPsd, String passcode, String question, String answer, boolean isLockForLeave);
        void setLockerEnable();
    }
}

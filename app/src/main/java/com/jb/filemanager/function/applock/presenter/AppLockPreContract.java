package com.jb.filemanager.function.applock.presenter;

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
        void showAppDatas(List<LockerItem> lockerItems);
        void showButtonEnableToLock();
        void showButtonDisEnable();
        void gotoAppLockerView();
        void showPermisstionGuideView();
    }

    interface Presenter {
        void loadData();
        void release();
        void dealPermissionCheck();
        void dealResume();
        void cacheInitInfo(boolean isPatternPsd, String passcode, String answer, String question);
        void search(String keyWord);
        void refreshOperateButState();
        boolean isShouldShowBackTipDialog();
    }

    interface Support extends ITaskSupport {
        List<LockerItem> getAppLockAppDatas();
        List<LockerItem> getRecommedAppDatas();
        boolean isHaveUsageStatePremisstion();
        void saveLockerInfo(List<LockerItem> allLockerItemList);
        void savePasscodeAndQuestion(boolean isPatternPsd, String passcode, String question, String answer);
        void setLockerEnable();
        boolean isBackTipDialogPop();
    }
}

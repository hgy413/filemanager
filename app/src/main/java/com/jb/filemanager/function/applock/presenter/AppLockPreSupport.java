package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.manager.LockerMonitorManager;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.device.Machine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 2016/12/30.
 */

public class AppLockPreSupport implements AppLockPreContract.Support {

    private TaskSupportImpl mTaskSupportImpl;

    public AppLockPreSupport() {
        mTaskSupportImpl = new TaskSupportImpl();
    }

    @Override
    public List<LockerItem> getAppLockAppDatas() {
        return AppLockerDataManager.getInstance().getAppLockAppsData().getLockerItems();
    }

    @Override
    public List<LockerItem> getRecommedAppDatas() {
        return AppLockerDataManager.getInstance().getRecommendLockAppDatas();
    }

    @Override
    public void toAsynWork(Runnable work) {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.toAsynWork(work);
        }
    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.toUiWork(work, delay);
        }
    }

    @Override
    public boolean isHaveUsageStatePremisstion() {
        boolean need = true;
        if (Machine.HAS_SDK_5_1_1) {
            // 5.1或以上
            need = AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(TheApplication.getAppContext());
        } else if (Machine.HAS_SDK_LOLLIPOP) {
            // 5.0
            need = AppUtils.isPermissionPackageUsageStatsGrandedOnLollipop(TheApplication.getAppContext());
        }
        return need;
    }

    @Override
    public void release() {
        if(mTaskSupportImpl != null) {
            mTaskSupportImpl.release();
        }
    }

    @Override
    public void saveLockerInfo(List<LockerItem> allLockerItemList) {
        List<LockerItem> selectBeans = new ArrayList<>();
        for (LockerItem lockerItem : allLockerItemList) {
            if (lockerItem.isChecked) {
                selectBeans.add(lockerItem);
            }
        }
        // 上锁勾选的应用
        AppLockerDataManager.getInstance().lockItem(selectBeans.toArray(new LockerItem[selectBeans.size()]));
    }

    @Override
    public void removeUiWork(Runnable work) {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.removeUiWork(work);
        }
    }

    @Override
    public void savePasscodeAndQuestion(boolean isPatternPsd, String passcode, String question, String answer) {
        // 上锁勾选的应用
        AppLockerDataManager.getInstance().saveLockerAnswer(answer);
        AppLockerDataManager.getInstance().saveLockerQuestion(question);
        AppLockerDataManager.getInstance().modifyLockerPassword(passcode, isPatternPsd);
        AppLockerDataManager.getInstance().updatePassWord();
    }

    @Override
    public void setLockerEnable() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_APP_LOCK_ENABLE, true);
        LockerMonitorManager.getInstance().startMonitor();
    }

    @Override
    public boolean isBackTipDialogPop() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        int times = sharedPreferencesManager.getInt(IPreferencesIds.KEY_BACK_TIP_DIALOG_SHOW_TIMES, 0);
        if (times > 0) {
            return false;
        }
        //直接增加一次
        sharedPreferencesManager.commitInt(IPreferencesIds.KEY_BACK_TIP_DIALOG_SHOW_TIMES, ++times);
        return true;
    }
}

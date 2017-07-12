package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.manager.LockerMonitorManager;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.model.bean.LockerItem;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

import java.util.List;

/**
 * Created by nieyh on 2017/1/3.
 */

public class AppLockSupport implements AppLockContract.Support {

    private TaskSupportImpl mSubSupport;

    public AppLockSupport() {
        this.mSubSupport = new TaskSupportImpl();
    }

    @Override
    public List<LockerItem> getAppLockAppDatas() {
        return AppLockerDataManager.getInstance().getAppLockAppsData().getLockerItems();
    }

    @Override
    public void toAsynWork(Runnable work) {
        mSubSupport.toAsynWork(work);
    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        mSubSupport.toUiWork(work, delay);
    }

    @Override
    public void removeUiWork(Runnable work) {
        if (mSubSupport != null) {
            mSubSupport.removeUiWork(work);
        }
    }

    @Override
    public void release() {
        if (mSubSupport != null) {
            mSubSupport.release();
        }
    }

    @Override
    public String getAppLockGroupName() {
        return TheApplication.getAppContext().getString(R.string.app_lock_group_apps_name);
    }

    @Override
    public boolean getIntruderSwitcherState() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
//        return sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, false);
        return true;
    }

    @Override
    public void saveLockerInfo(List<LockerItem> lockerItemList, List<LockerItem> unLockerItemList) {
        // 上锁勾选的应用
        AppLockerDataManager.getInstance().lockItem(lockerItemList.toArray(new LockerItem[lockerItemList.size()]));
        AppLockerDataManager.getInstance().unlockItem(unLockerItemList.toArray(new LockerItem[unLockerItemList.size()]));
    }

    @Override
    public void updateIntruderPhoto() {
//        AntiPeepDataManager.getInstance(TheApplication.getAppContext()).updateAllPhoto();
    }

    @Override
    public int getIntruderPhotoSize() {
//        return AntiPeepDataManager.getInstance(TheApplication.getAppContext()).getAllPhotoAfterUpdate().size();
        return 1;
    }

    @Override
    public void startAppLockerMonitor() {
        LockerMonitorManager.getInstance().startMonitor();
    }

    @Override
    public void stopAppLockerMonitor() {
        LockerMonitorManager.getInstance().stopMonitor();
    }
}

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
        if (work != null) {
            TheApplication.postRunOnShortTaskThread(work);
        }
    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        if (work != null) {
            TheApplication.postRunOnUiThread(work, delay);
        }
    }

    @Override
    public void removeUiWork(Runnable work) {
        if (work != null) {
            TheApplication.removeFromUiThread(work);
        }
    }

    @Override
    public String[] getFloatListGroupTitle() {
        return TheApplication.getAppContext().getResources().getStringArray(R.array.applock_listview_group_title);
    }

    @Override
    public void saveLockerInfo(List<LockerItem> lockerItemList, List<LockerItem> unLockerItemList) {
        // 上锁勾选的应用
        AppLockerDataManager.getInstance().lockItem(lockerItemList.toArray(new LockerItem[lockerItemList.size()]));
        AppLockerDataManager.getInstance().unlockItem(unLockerItemList.toArray(new LockerItem[unLockerItemList.size()]));
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

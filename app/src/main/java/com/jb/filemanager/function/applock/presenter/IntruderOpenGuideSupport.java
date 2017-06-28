package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;

/**
 * Created by nieyh on 2017/1/4.
 */

public class IntruderOpenGuideSupport implements IntruderOpenGuideContract.Support {
    @Override
    public void setIntruderOpen() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_APP_LOCK_REVEAL_ENABLE, true);
    }

    @Override
    public int getIntruderWrongTimes() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getInt(IPreferencesIds.KEY_APP_LOCK_WRONG_PSD_TIMES, 2);
    }
}

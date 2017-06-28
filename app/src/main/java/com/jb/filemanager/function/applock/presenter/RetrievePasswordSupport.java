package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.AppLockerDataManager;

/**
 * Created by nieyh on 2017/1/9.
 */

public class RetrievePasswordSupport implements RetrievePasswordContract.Support {

    @Override
    public String getSecurityQuestion() {
        return AppLockerDataManager.getInstance().getLockerQuestion();
    }

    @Override
    public String getSecurityAnswer() {
        return AppLockerDataManager.getInstance().getLockerAnswer();
    }
}

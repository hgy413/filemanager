package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;

/**
 * Created by nieyh on 2017/1/1.
 */

public class PsdInitSupport implements PsdInitContract.Support {

    @Override
    public void updatePasscode(String passcode, boolean isPattern) {
//        AppLockerDataManager.getInstance().modifyLockerPassword(passcode, isPattern);
//        AppLockerDataManager.getInstance().updatePassWord();
    }

    @Override
    public void updateIssureQuestion(String question, String answer) {
        AppLockerDataManager.getInstance().saveLockerQuestion(question);
        AppLockerDataManager.getInstance().saveLockerAnswer(answer);
    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        TheApplication.postRunOnUiThread(work, delay);
    }

    @Override
    public void removeUiWork(Runnable work) {
        TheApplication.removeFromUiThread(work);
    }

}

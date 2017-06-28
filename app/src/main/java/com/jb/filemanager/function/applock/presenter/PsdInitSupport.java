package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.function.applock.model.AppLockerDataManager;

/**
 * Created by nieyh on 2017/1/1.
 */

public class PsdInitSupport implements PsdInitContract.Support {

    private TaskSupportImpl mTaskSupportImpl;

    public PsdInitSupport() {
        mTaskSupportImpl = new TaskSupportImpl(TaskSupportImpl.TYPE_SINGLE_UI);
    }

    @Override
    public void updatePasscode(String passcode, boolean isPattern) {
        AppLockerDataManager.getInstance().modifyLockerPassword(passcode, isPattern);
        AppLockerDataManager.getInstance().updatePassWord();
    }

    @Override
    public void updateIssureQuestion(String question, String answer) {
        AppLockerDataManager.getInstance().saveLockerQuestion(question);
        AppLockerDataManager.getInstance().saveLockerAnswer(answer);
    }

    @Override
    public void toAsynWork(Runnable work) {

    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.toUiWork(work, delay);
        }
    }

    @Override
    public void removeUiWork(Runnable work) {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.removeUiWork(work);
        }
    }

    @Override
    public void release() {
        if (mTaskSupportImpl != null) {
            mTaskSupportImpl.release( );
        }
    }
}

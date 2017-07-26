package com.jb.filemanager.function.applock.presenter;


import com.jb.filemanager.function.applock.view.PatternView;

import java.util.List;

/**
 * Created by nieyh on 2017/1/1.
 */

public interface PsdInitContract {
    interface View {
        void showPsdViewDismissQuestion();
        void showProblemViewDismissPsd();
        void showStepTopPatternTip(int step);
        void clearPsd();
        void showPatternError();
        void toBack();
        void showPatternDiffTip();
        void showPatternShort();
        void invisiableStep3();
        void showAnswerShortTip();
        int getLockOptions();
        void setResult(String passcode, String question, String answer, boolean isLockForLeave);
        String getProblem();
        String getAnswer();
    }

    interface Support {
        void updatePasscode(String passcode, boolean isPattern);
        void updateIssureQuestion(String question, String answer);
        void toUiWork(Runnable work, long delay);
        void removeUiWork(Runnable work);
    }

    interface Presenter {
        void start();
        void cachePattern(List<PatternView.Cell> currentCells);
        void dealBackPress(boolean isSystemBack);
        void setMode(int mode);
        void dealSaveSecureProblem();
        void dealPatternStart();
        void release();
    }
}

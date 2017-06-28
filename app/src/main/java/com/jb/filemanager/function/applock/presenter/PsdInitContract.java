package com.jb.filemanager.function.applock.presenter;


import com.jb.filemanager.function.applock.view.PatternView;

import java.util.List;

/**
 * Created by nieyh on 2017/1/1.
 */

public interface PsdInitContract {
    interface View {
        void showStep(int step, boolean isLarge);
        void showPsdViewDismissQuestion(boolean isPatternPsd);
        void showProblemViewDismissPsd();
        void showStepTopPatternTip(int step);
        void showStepTopNumberTip(int step);
        void clearPsd(boolean isPatternPsd);
        void showPatternError();
        void toBack(/*boolean isSystemBack*/);
        void showPatternDiffTip();
        void showNumberErrorAnim();
        void showPatternShort();
        void invisiableStep3();
        void showAnswerShortTip();
        void dismissStepView();
        void setResult(boolean isPatternPsd, String passcode, String question, String answer);
        String getProblem();
        String getAnswer();
        void showLockerSwitch();
        void dismissLockerSwitch();
        void showNumberSwitch();
        void showPatternSwitch();
        void showNumberPsdView();
        void showPatternPsdView();
        void cleanQuestionCache();
    }

    interface Support extends ITaskSupport {
        void updatePasscode(String passcode, boolean isPattern);
        void updateIssureQuestion(String question, String answer);
    }

    interface Presenter {
        void start();
        void cachePattern(List<PatternView.Cell> currentCells);
        void cacheNumber(String[] number);
        void chgLockerType();
        void dealBackPress();
        void setMode(int mode);
        void setPsdType(boolean isPatternPsd);
        void dealSaveSecureProblem();
        void dealPatternStart();
        void dealPasscodeInput();
        void dealPasscodeAllDeleted();
        void release();
        String getBackStatisticConst(boolean isSystemBack);
        String getHomeStatisticConst();
    }
}

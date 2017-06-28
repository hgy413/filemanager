package com.jb.filemanager.function.applock.presenter;

/**
 * Created by nieyh on 2017/1/9.
 */

public interface RetrievePasswordContract {

    interface View {
        void gotoResetPsd();
        String getAnswer();
        void showQuestion(String question);
        void showAnswerErrorTip();
    }

    interface Support {
        String getSecurityQuestion();
        String getSecurityAnswer();
    }

    interface Presenter {
        void start();
        void dealSure();
    }
}

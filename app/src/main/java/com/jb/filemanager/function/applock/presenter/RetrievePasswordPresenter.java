package com.jb.filemanager.function.applock.presenter;

/**
 * Created by nieyh on 2017/1/9.
 */

public class RetrievePasswordPresenter implements RetrievePasswordContract.Presenter {

    private RetrievePasswordContract.View mView;

    private RetrievePasswordContract.Support mSupport;

    public RetrievePasswordPresenter(RetrievePasswordContract.View view, RetrievePasswordContract.Support support) {
        this.mView = view;
        this.mSupport = support;
    }

    @Override
    public void start() {
        if (mSupport != null && mView != null) {
            mView.showQuestion(mSupport.getSecurityQuestion());
        }
    }

    @Override
    public void dealSure() {
        if (mSupport != null && mView != null) {
            if (mSupport.getSecurityAnswer().equals(mView.getAnswer())) {
                mView.gotoResetPsd();
            } else {
                mView.showAnswerErrorTip();
            }
        }
    }
}

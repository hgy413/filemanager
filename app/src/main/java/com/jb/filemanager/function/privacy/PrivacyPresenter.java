package com.jb.filemanager.function.privacy;

import com.jb.filemanager.function.privacy.event.PrivacyConfirmClosedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by nieyh on 2016/9/1.
 *
 */
public class PrivacyPresenter implements PrivacyContract.Presenter{

    private PrivacyContract.View mView;

    private PrivacyContract.Support mSupport;

    public PrivacyPresenter(PrivacyContract.View view, PrivacyContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void joinUepPlan(boolean isJoin) {
        PrivacyHelper.joinUepPlan(isJoin);
    }

    @Override
    public void agreePrivacy() {
        PrivacyHelper.agreePrivacy();
    }

    @Override
    public boolean isAgreePrivacy() {
        return PrivacyHelper.isAgreePrivacy();
    }

    @Override
    public void sendPrivacyConfirmClosedMsg() {
        EventBus.getDefault().post(new PrivacyConfirmClosedEvent());
    }

    @Override
    public void start() {
        if (mSupport != null && mView != null) {
            mView.showVersion(mSupport.gainVersion());
        }
    }

    @Override
    public void release() {
        mView = null;
        mSupport = null;
    }
}

package com.jb.filemanager.function.applock.presenter;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.IntruderSwitcherStateEvent;

/**
 * Created by nieyh on 2017/1/4.
 */

public class InruderOpenGuidePresenter implements IntruderOpenGuideContract.Presenter {

    private IntruderOpenGuideContract.View mView;

    private IntruderOpenGuideContract.Support mSupport;

    private boolean isEnable;

    public InruderOpenGuidePresenter(IntruderOpenGuideContract.View mView, IntruderOpenGuideContract.Support mSupport) {
        this.mView = mView;
        this.mSupport = mSupport;
    }

    @Override
    public void start() {
        if (mSupport != null && mView != null) {
            mView.showIntruderTipTimes(mSupport.getIntruderWrongTimes());
            isEnable = mView.openCamera();
        }
    }

    @Override
    public void checkPremissionState() {
        if (mView != null && mSupport != null) {
            if (isEnable) {
                mSupport.setIntruderOpen();
                TheApplication.getGlobalEventBus().post(new IntruderSwitcherStateEvent(true));
            } else {
                mView.showPremisstionGetFail();
            }
            mView.close();
        }
    }
}

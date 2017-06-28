package com.jb.filemanager.function.applock.presenter;

/**
 * Created by nieyh on 2017/1/4.
 */

public interface IntruderOpenGuideContract {
    interface View {
        void showPremisstionGetFail();
        void showIntruderTipTimes(int times);
        boolean openCamera();
        void releaseCamera();
        void close();
    }

    interface Presenter {
        void checkPremissionState();
        void start();
    }

    interface Support {
        void setIntruderOpen();
        int getIntruderWrongTimes();
    }
}

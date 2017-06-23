package com.jb.filemanager.function.privacy;

/**
 * Created by nieyh on 2016/9/1.
 */
public interface PrivacyContract {

    interface View {
        void showVersion(String version);
    }

    interface Presenter {
        void joinUepPlan(boolean isJoin);

        void agreePrivacy();

        boolean isAgreePrivacy();

        void sendPrivacyConfirmClosedMsg();

        void start();

        void release();
    }

    interface Support {
        String gainVersion();
    }
}

package com.jb.filemanager.function.feedback;

import android.content.Intent;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class FeedbackContract {
    interface Support {
        boolean isNetWork();
        String getFeedBackCommon();
        String getFeedBackSuggestion();
        String getFeedBackProblem();
        String getFeedBackForceInstall();
        String getFeedbackDeviceInfo(String content);
        String getFeedbackContent();
        Intent newFeedBackIntent(String text, String titleContent, String tos);
        boolean isNeedDismissWarnLayout();
        void setWarnLayoutShowed();
    }

    interface View {
        void startActivity(Intent intent);
        void showCheckNetWorkTip();
        void showNoEmailTip();
        void setContainerNull();
        void dismissWarnLayout();
    }

    interface Presenter {
        void sendFeedBack(String detail, String title);
        void sendWarnTip();
        void release();
        void start();
    }
}

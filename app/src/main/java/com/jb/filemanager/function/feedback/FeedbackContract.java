package com.jb.filemanager.function.feedback;

import android.content.Intent;

import com.jb.filemanager.function.rate.presenter.RateFactorSupport;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public interface FeedbackContract {
    interface Support extends RateFactorSupport {
        boolean isNetWork();
        String[] getProblemArray();
        String getFeedbackDeviceInfo(String content);
        String getFeedbackContent();
        Intent newFeedBackIntent(String text, String titleContent, String tos);
        boolean isNeedDismissWarnLayout();
        boolean isNeedDismissWarn2Layout();
        void setWarnLayoutShowed();
        void setWarn2LayoutShowed();
    }

    interface View {
        void startActivity(Intent intent);
        void showCheckNetWorkTip();
        void showWarnTip1();
        void showWarnTip2();
        void showNoEmailTip();
        void setContainerNull();
        void dismissWarnLayout();
    }

    interface Presenter {
        void clickTipSecondBtu();
        void sendFeedBack(String detail, String title);
        void sendWarnTip();
        void release();
        void start();
    }
}

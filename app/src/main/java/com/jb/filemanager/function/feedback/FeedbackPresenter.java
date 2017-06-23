package com.jb.filemanager.function.feedback;

import android.content.Intent;

/**
 * Created by bill wang on 2017/6/21.
 */

public class FeedbackPresenter implements FeedbackContract.Presenter {

    private FeedbackContract.View mView;

    private FeedbackContract.Support mSupport;

    public FeedbackPresenter(FeedbackContract.View mView, FeedbackContract.Support mSupport) {
        this.mView = mView;
        this.mSupport = mSupport;
    }

    @Override
    public void sendFeedBack(String detail, String title) {
        if (!mSupport.isNetWork()) {
            mView.showCheckNetWorkTip();
            return;
        }

        String feedbackcontent = "";
        if (mSupport.getFeedBackCommon().equals(title)) {
            feedbackcontent = "Common";
        } else if (mSupport.getFeedBackSuggestion().equals(title)) {
            feedbackcontent = "Suggestion";
        } else if (mSupport.getFeedBackProblem().equals(title)) {
            feedbackcontent = "Problem";
        } else if (mSupport.getFeedBackForceInstall().equals(title)) {
            feedbackcontent = "Forced/Tricked Installation";
        }
        String devinfo = mSupport.getFeedbackDeviceInfo(feedbackcontent);
        String notice = mSupport.getFeedbackContent();
        String text = detail + "\n\n" + notice + "\n" + devinfo;
        String titleContent = "Feedback, " + feedbackcontent;
        //String tos = "gsfeedbackservice@gmail.com;reportspamgodev@gmail.com";
        String tos = "acenetworkpro@gmail.com";
        Intent emailIntent = mSupport.newFeedBackIntent(text, titleContent, tos);

        try {
            mView.startActivity(emailIntent);
        } catch (Exception ex) {
            mView.showNoEmailTip();
        }
        mView.setContainerNull();
    }

    @Override
    public void sendWarnTip() {
        if (mSupport != null) {
            mSupport.setWarnLayoutShowed();
        }
    }

    @Override
    public void release() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void start() {
        if (mSupport != null && mView != null && mSupport.isNeedDismissWarnLayout()) {
            mView.dismissWarnLayout();
        }
    }
}

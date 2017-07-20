package com.jb.filemanager.function.feedback;

import android.content.Intent;

import com.jb.filemanager.Const;

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
    public void clickTipSecondBtu() {
        if (mSupport.isNeedDismissWarnLayout()) {
            //如果第一个弹窗已经消失 则直接设置第二个弹窗以后也不再出现
            mSupport.setWarn2LayoutShowed();
        }
    }

    @Override
    public void sendFeedBack(String detail, String title) {
        if (!mSupport.isNetWork()) {
            mView.showCheckNetWorkTip();
            return;
        }

        String feedbackcontent = "";
        String[] array = mSupport.getProblemArray();
        if (array[0].equals(title)) {
            feedbackcontent = "Common";
        } else if (array[1].equals(title)) {
            feedbackcontent = "Suggestion";
        } else if (array[2].equals(title)) {
            feedbackcontent = "Problem";
        } else if (array[3].equals(title)) {
            feedbackcontent = "Forced/Tricked Installation";
        }
        String devinfo = mSupport.getFeedbackDeviceInfo(feedbackcontent);
        String notice = mSupport.getFeedbackContent();
        String text = detail + "\n\n" + notice + "\n" + devinfo;
        String titleContent = "Feedback, " + feedbackcontent;
        String tos = Const.FILEMANAGER_BUS_EMAIL;
        Intent emailIntent = mSupport.newFeedBackIntent(text, titleContent, tos);

        try {
            mView.startActivity(emailIntent);
        } catch (Exception ex) {
            mView.showNoEmailTip();
        }
        mView.setContainerNull();
        if (mSupport != null) {
            //添加评分引导成功
            mSupport.addRateFactor();
        }
    }

    @Override
    public void sendWarnTip() {
        if (mSupport != null) {
            mSupport.setWarnLayoutShowed();
            //添加评分引导成功
            mSupport.addRateFactor();
        }
    }

    @Override
    public void release() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void start() {
        if (mSupport != null && mView != null) {
            boolean isNeedDismissWarn1 = mSupport.isNeedDismissWarnLayout();
            boolean isNeedDismissWarn2 = mSupport.isNeedDismissWarn2Layout();
            if (isNeedDismissWarn1) {
                if (isNeedDismissWarn2) {
                    mView.dismissWarnLayout();
                } else {
                    mView.showWarnTip2();
                }
            } else {
                mView.showWarnTip1();
            }
        }
    }
}

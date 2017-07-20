package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;

/**
 * 评分Feedback对话框
 * Created by nieyh on 2016/7/12.
 */
public class RateFeedbackDialog extends AbsRateDialog {
    private final String TAG = "RateFeedbackDialog";

    public RateFeedbackDialog(Activity act) {
        super(act);
    }

    @Override
    protected void initMiddleView(RelativeLayout middleView) {
        View.inflate(TheApplication.getAppContext(), R.layout.view_feedback_middle, middleView);
        setTitleTxt(R.string.googleplay_dialog_feedback_title);
        setOkTxt(R.string.common_ok);
        setNoTxt(R.string.googleplay_dialog_thanks_btn);
    }

    @Override
    public void show() {
        super.show();
    }
}
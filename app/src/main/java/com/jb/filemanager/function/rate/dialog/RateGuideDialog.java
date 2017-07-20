package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;

/**
 * Created by nieyh on 2016/7/12.
 */
public class RateGuideDialog extends AbsRateDialog {

    private final String TAG = "RateGuideDialog";

    public RateGuideDialog(Activity act) {
        super(act);
    }

    @Override
    protected void initMiddleView(RelativeLayout middleView) {
        View.inflate(TheApplication.getAppContext(), R.layout.view_rate_guide_middle, middleView);
        TextView content = (TextView) middleView.getChildAt(0);
        content.setText(TheApplication.getAppContext().getString(R.string.googleplay_dialog_cheer_tipword, getString(R.string.app_name)));
        setTitleTxt(R.string.googleplay_dialog_cheers_title);
        setOkTxt(R.string.common_like);
        setNoTxt(R.string.googleplay_dialog_thanks_btn);
    }

    @Override
    public void show() {
        super.show();
    }

}

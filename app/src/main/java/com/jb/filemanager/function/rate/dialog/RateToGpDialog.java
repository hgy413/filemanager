package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;

/**
 * Created by nieyh on 2016/7/12.
 */
public class RateToGpDialog extends AbsRateDialog {

    private final String TAG = "RateToGpDialog";

    public RateToGpDialog(Activity act) {
        super(act);
    }

    @Override
    protected void initMiddleView(RelativeLayout middleView) {
        View.inflate(TheApplication.getAppContext(), R.layout.view_to_gp_middle, middleView);
        setTitleTxt(R.string.googleplay_dialog_love_titel);
        setOkTxt(R.string.common_go);
        setNoTxt(R.string.googleplay_dialog_thanks_btn);
    }

    @Override
    public void show() {
        super.show();
    }
}

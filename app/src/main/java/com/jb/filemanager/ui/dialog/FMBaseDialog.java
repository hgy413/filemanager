package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jb.filemanager.R;

/**
 * Created by bill wang on 2017/7/11.
 *
 */

public class FMBaseDialog extends BaseDialog {

    public FMBaseDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
    }

    @Override
    public void setContentView(@NonNull View view) {
        FrameLayout dialogView = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.dialog_base, null);
        FrameLayout contentContainer = (FrameLayout) dialogView.findViewById(R.id.fl_dialog_content);
        contentContainer.addView(view);
        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        super.setContentView(dialogView, new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}

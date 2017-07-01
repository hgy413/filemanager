package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bill wang on 17/4/14.
 *
 */

public class ScreenWidthDialog extends BaseDialog {

    public ScreenWidthDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
    }

    public ScreenWidthDialog(Activity act, View content, boolean cancelOutside) {
        super(act, cancelOutside);
        int width = act.getResources().getDisplayMetrics().widthPixels;
        setContentView(content, new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setContentView(@NonNull View view) {
        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        super.setContentView(view, new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
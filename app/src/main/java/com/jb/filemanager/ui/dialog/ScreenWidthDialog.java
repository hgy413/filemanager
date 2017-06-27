package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bill wang on 17/4/14.
 *
 */

public class ScreenWidthDialog extends BaseDialog {

    public ScreenWidthDialog(Activity act, View content, boolean cancelOutside) {
        super(act, cancelOutside);
        int width = act.getResources().getDisplayMetrics().widthPixels;
        setContentView(content, new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
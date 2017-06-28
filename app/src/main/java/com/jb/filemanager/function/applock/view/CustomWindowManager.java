package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * 自定义WindowManager
 * Created by Administrator on 2015/7/15.
 */
public class CustomWindowManager {

    private boolean mIsAdd = false;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    public CustomWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        if (Build.VERSION.SDK_INT >= 19) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            //低版本使用Toast无法接受点击事件 只能兼容使用TYPE_SYSTEM_ALERT
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mLayoutParams.packageName = context.getPackageName();
    }

    public void addView(View view) {
        if (mIsAdd) {
            removeView(view);
        }
        try {
//            view.requestLayout();
//            mLayoutParams.dimAmount = 1.0f;
//            view.setFocusable(true);
//            view.requestFocus();
            mWindowManager.addView(view, mLayoutParams);
            mIsAdd = true;
        } catch (Exception e) {
            mIsAdd = false;
            e.printStackTrace();
        }
    }

    public void updateBackgroundDim(View view, float dim) {
        try {
            mLayoutParams.dimAmount = dim;
            mWindowManager.updateViewLayout(view, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView(View view) {
        try {
            mWindowManager.removeView(view);
            mIsAdd = false;
        } catch (Exception e) {
            mIsAdd = true;
            e.printStackTrace();
        }
    }
}

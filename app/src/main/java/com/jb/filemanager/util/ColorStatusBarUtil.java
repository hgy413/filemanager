package com.jb.filemanager.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jb.filemanager.R;


/**
 * 彩色通知栏工具类
 *
 * @author chenbenbin
 */
public class ColorStatusBarUtil {

    /**
     * 是否支持彩色通知栏
     */
    public static boolean isSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 透明状态栏
     */
    public static void transparentStatusBar(Activity activity) {
        if (isSupport()) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_color));
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * 获取透明状态栏的特性
     */
    public static int getTransparentStatusBarFeature() {
        return isSupport() ? WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS : 0;
    }

    /**
     * 获取通知栏高度，若不支持，则返回0
     */
    public static int getHeight() {
        if (!isSupport()) {
            return 0;
        }
        return WindowUtil.getStatusBarHeight();
    }

    /**
     * 增加状态栏高度的高度
     */
    public static void appendStatusBarHeight(View view) {
        if (isSupport()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height += getHeight();
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 增加状态栏高度的上内边距
     */
    public static void appendStatusBarTopPadding(View view) {
        if (isSupport()) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getHeight(),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加状态栏高度的上外边距
     */
    public static void appendStatusBarTopMargin(View view) {
        if (isSupport()) {
            ViewGroup.LayoutParams sourceParams = view.getLayoutParams();
            if (sourceParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) sourceParams;
                layoutParams.topMargin += getHeight();
                view.setLayoutParams(layoutParams);
            }
        }
    }
}

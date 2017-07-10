package com.jb.filemanager.util;

import android.app.Notification;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StatFs;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;

import com.jb.filemanager.BuildConfig;

/**
 * Created by bill wang on 16/8/29.
 * 解决API版本不同问题
 */
public class APIUtil {

    public static int getColor(Context context, int resId) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getApplicationContext().getResources().getColor(resId, null);
        } else {
            //noinspection deprecation
            color = context.getApplicationContext().getResources().getColor(resId);
        }
        return color;
    }

    public static Notification build(Notification.Builder builder) {
        Notification result = null;
        if (builder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                result = builder.build();
            } else {
                //noinspection deprecation
                result = builder.getNotification();
            }
        }
        return result;
    }

    public static Drawable getDrawable(Context context, int resId) {
        Drawable result = null;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = context.getResources().getDrawable(resId, null);
            } else {
                //noinspection deprecation
                result = context.getResources().getDrawable(resId);
            }
        }
        return result;
    }

    public static void setBackground(View view, Drawable drawable) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                //noinspection deprecation
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    public static void removeGlobalOnLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            //noinspection deprecation
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    public static String fromHtml(String content) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(content, 0).toString();
        } else {
            //noinspection deprecation
            result = Html.fromHtml(content).toString();
        }
        return result;
    }

    public static long getAvailableBytes(StatFs statFs) {
        long result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            result = statFs.getAvailableBytes();
        } else {
            //noinspection deprecation
            result = (long)statFs.getFreeBlocks() * (long)statFs.getBlockSize();
        }
        return result;
    }

    public static long getTotalBytes(StatFs statFs) {
        long result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            result = statFs.getTotalBytes();
        } else {
            //noinspection deprecation
            result = (long)statFs.getBlockSize() *(long)statFs.getBlockCount();
        }
        return result;
    }
}
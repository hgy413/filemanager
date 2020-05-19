package com.jiubang.commerce.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

@Deprecated
public class LogUtils {
    public static final String LOG_TAG = "Ad_SDK";
    public static boolean sIS_SHOW_LOG = false;

    public static void setEnableLog(boolean onOff) {
        sIS_SHOW_LOG = onOff;
    }

    public static void v(String tag, String msg) {
        if (sIS_SHOW_LOG) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sIS_SHOW_LOG) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (sIS_SHOW_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sIS_SHOW_LOG) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sIS_SHOW_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sIS_SHOW_LOG) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sIS_SHOW_LOG) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sIS_SHOW_LOG) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (sIS_SHOW_LOG) {
            Log.w(tag, tr);
        }
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void showToast(Context context, CharSequence text, int duration) {
        if (sIS_SHOW_LOG) {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void showToast(Context context, int resId, int duration) {
        if (sIS_SHOW_LOG) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    public static String getCurrentStackTraceString() {
        return Log.getStackTraceString(new Throwable());
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }
}

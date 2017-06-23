package com.jb.filemanager.util;

import android.util.Log;

import com.jb.filemanager.BuildConfig;

/**
 * Created by skye on 7/5/16.
 *
 */
public class Logger {

    public static boolean DEBUG = BuildConfig.DEBUG;

    public static void setDEBUG(boolean debug) {
        DEBUG = debug;
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     */
    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     * @param tr  抛出的异常
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.v(tag, msg, tr);
        }
    }

    /**
     *
     * @param tag 标识符
     * @param msg 打印信息
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     * @param tr  抛出的异常
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    /**
     *
     * @param tag 标识符
     * @param msg 打印信息
     */
    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     * @param tr  抛出的异常
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.i(tag, msg, tr);
        }
    }

    /**
     *
     * @param tag 标识符
     * @param msg 打印信息
     */
    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     * @param tr  抛出的异常
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    /**
     *
     * @param tag 标识符
     * @param msg 打印信息
     */
    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * @param tag 标识符
     * @param msg 打印信息
     * @param tr  抛出的异常
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            Log.e(tag, msg, tr);
        }
    }
}
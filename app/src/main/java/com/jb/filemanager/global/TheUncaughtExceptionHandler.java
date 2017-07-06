package com.jb.filemanager.global;

import com.jb.filemanager.function.zipfile.ExtractManager;

/**
 * Created by xiaoyu on 2017/7/6 16:57.
 */

public final class TheUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static TheUncaughtExceptionHandler sInstance;

    private TheUncaughtExceptionHandler() {
    }

    public static TheUncaughtExceptionHandler getInstance() {
        if (sInstance == null) {
            synchronized (TheUncaughtExceptionHandler.class) {
                if (sInstance == null) {
                    sInstance = new TheUncaughtExceptionHandler();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (ExtractManager.getInstance().isExtracting()) {
            ExtractManager.getInstance().onExtractError();
        }
        e.printStackTrace();
    }
}

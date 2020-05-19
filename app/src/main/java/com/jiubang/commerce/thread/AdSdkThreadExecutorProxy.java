package com.jiubang.commerce.thread;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdSdkThreadExecutorProxy {
    private static ExecutorService sExecutorService = null;
    private static Handler sMainHandler;

    public static void init() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static void setThreadPoolSize(int size) {
        if (size >= 1) {
            if (sExecutorService != null) {
                sExecutorService.shutdown();
            }
            sExecutorService = Executors.newFixedThreadPool(size);
        }
    }

    public static boolean execute(Runnable runnable) {
        if (sExecutorService == null || runnable == null) {
            return false;
        }
        sExecutorService.execute(runnable);
        return true;
    }

    public static void cancel(Runnable task) {
        if (sMainHandler != null) {
            sMainHandler.removeCallbacks(task);
        }
    }

    public static void destroy() {
        if (sMainHandler != null) {
            sMainHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    public static void runOnMainThread(Runnable r) {
        if (sMainHandler == null) {
            init();
        }
        sMainHandler.post(r);
    }

    public static void runOnMainThread(Runnable r, long delay) {
        if (sMainHandler == null) {
            init();
        }
        sMainHandler.postDelayed(r, delay);
    }
}

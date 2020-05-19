package com.jiubang.commerce.thread;

import android.text.TextUtils;

public class AdSdkThread {
    private String mName = null;
    private Runnable mTask = null;
    private boolean mUsePool = false;

    public AdSdkThread(Runnable runnable) {
        this.mTask = runnable;
    }

    public AdSdkThread(boolean usePool, Runnable runnable) {
        this.mTask = runnable;
        this.mUsePool = usePool;
    }

    public AdSdkThread(String name, Runnable runnable) {
        this.mName = name;
        this.mTask = runnable;
    }

    public void start() {
        if (!this.mUsePool || !AdSdkThreadExecutorProxy.execute(this.mTask)) {
            Thread thread = new Thread(this.mTask);
            if (!TextUtils.isEmpty(this.mName)) {
                thread.setName(this.mName);
            }
            thread.start();
        }
    }

    public boolean isAlive() {
        return false;
    }
}

package com.jiubang.commerce.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeOutGuard {
    private boolean mHadTimeOut = false;
    private byte[] mLock = new byte[0];
    private Object mParam = null;
    private Timer mTimer;

    public void start(long timeOutTime, TimeOutTask task, Object param) {
        this.mParam = param;
        cancel();
        setHasTimeOut(false);
        task.setGuard(this);
        this.mTimer = new Timer(TimeOutGuard.class.getName(), true);
        this.mTimer.schedule(task, timeOutTime);
    }

    public void cancel() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer.purge();
            this.mTimer = null;
        }
    }

    public Object getParam() {
        return this.mParam;
    }

    public boolean hadTimeOut() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mHadTimeOut;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void setHasTimeOut(boolean b) {
        synchronized (this.mLock) {
            this.mHadTimeOut = b;
        }
    }

    public static abstract class TimeOutTask extends TimerTask {
        private TimeOutGuard mGuard;

        public abstract void onTimeOut();

        public void setGuard(TimeOutGuard guard) {
            this.mGuard = guard;
        }

        public void run() {
            if (this.mGuard != null && !this.mGuard.hadTimeOut()) {
                this.mGuard.setHasTimeOut(true);
                onTimeOut();
            }
        }
    }
}

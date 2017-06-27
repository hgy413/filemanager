package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 专清扫描完成事件
 *
 * @author chenbenbin
 */
public enum CleanAppDeepCacheScanDoneEvent {
    WHATSAPP, FACEBOOK;

    private boolean mIsDone = false;

    public boolean isDone() {
        return mIsDone;
    }

    public void setDone(boolean done) {
        mIsDone = done;
    }

    @Override
    public String toString() {
        return super.toString() +
                " : mIsDone=" + mIsDone +
                '}';
    }
}

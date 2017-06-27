package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 深度缓存完成事件
 *
 * @author chenbenbin
 */
public enum CleanDeepScanDoneEvent {
    /**
     * 深度缓存
     */
    DeepCacheScanDoneEvent;

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

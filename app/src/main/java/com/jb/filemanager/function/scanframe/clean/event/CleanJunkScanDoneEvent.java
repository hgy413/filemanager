package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 普通垃圾扫描完成事件
 *
 * @author chenbenbin
 */
public class CleanJunkScanDoneEvent {

    private boolean mIsDone = false;

    public boolean isDone() {
        return mIsDone;
    }

    public void setIsDone(boolean isDone) {
        mIsDone = isDone;
    }

    @Override
    public String toString() {
        return "CleanJunkScanDoneEvent{" +
                "mIsDone=" + mIsDone +
                '}';
    }
}

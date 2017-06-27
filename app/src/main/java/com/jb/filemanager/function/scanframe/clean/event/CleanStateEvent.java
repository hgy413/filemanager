package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 清理流程的状态
 *
 * @author chenbenbin
 */
public enum CleanStateEvent {
    SCAN_ING, SCAN_FINISH, SCAN_SUSPEND, DELETE_ING, DELETE_SUSPEND, DELETE_FINISH;
    /**
     * 上次时间
     */
    private long mLastTime;

    public long getLastTime() {
        return mLastTime;
    }

    public void setLastTime(long lastTime) {
        mLastTime = lastTime;
    }

    public static void cleanAllData() {
        for (CleanStateEvent event : CleanStateEvent.values()) {
            event.setLastTime(0);
        }
    }
}
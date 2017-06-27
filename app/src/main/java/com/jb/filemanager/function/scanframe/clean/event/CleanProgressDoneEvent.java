package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;

/**
 * 清理扫描进度条动画结束事件
 *
 * @author chenbenbin
 */
public enum CleanProgressDoneEvent {
    /**
     * 缓存
     */
    CacheProgressDoneEvent,
    /**
     * 残留文件
     */
    ResidueProgressDoneEvent,
    /**
     * SD卡扫描结束
     */
    SDCardProgressDoneEvent,
    /**
     * Ad扫描结束
     */
    AdProgressDoneEvent,
    /**
     * 内存扫描结束
     **/
    MemoryProgressDoneEvent;

    public static CleanProgressDoneEvent getEvent(GroupType type) {
        switch (type) {
            case APP_CACHE:
                return CacheProgressDoneEvent;
            case RESIDUE:
                return ResidueProgressDoneEvent;
            case MEMORY:
                return MemoryProgressDoneEvent;
            case AD:
                return AdProgressDoneEvent;
            default:
                return SDCardProgressDoneEvent;
        }
    }

    private boolean mDone = false;

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    /**
     * 是否全部事件都完成
     */
    public static boolean isAllDone() {
        boolean isAllDone = true;
        for (CleanProgressDoneEvent event : CleanProgressDoneEvent.values()) {
            isAllDone = isAllDone && event.isDone();
        }
        return isAllDone;
    }

    /**
     * 是否全部事件都完成
     */
    public static boolean isAllDoneWithoutMemory() {
        boolean isAllDone = true;
        for (CleanProgressDoneEvent event : CleanProgressDoneEvent.values()) {
            isAllDone = isAllDone && event.isDone();
        }
        return CleanProgressDoneEvent.CacheProgressDoneEvent.isDone()
                && CleanProgressDoneEvent.ResidueProgressDoneEvent.isDone()
                && CleanProgressDoneEvent.AdProgressDoneEvent.isDone()
                && CleanProgressDoneEvent.SDCardProgressDoneEvent.isDone();
    }

    /**
     * 清空结束状态
     */
    public static void cleanAllDone() {
        for (CleanProgressDoneEvent event : CleanProgressDoneEvent.values()) {
            event.setDone(false);
        }
    }
}

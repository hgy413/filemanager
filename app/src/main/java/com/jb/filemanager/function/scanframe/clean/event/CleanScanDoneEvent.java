package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;

/**
 * 清理扫描结束事件
 *
 * @author chenbenbin
 */
public enum CleanScanDoneEvent {
    /**
     * 1.1 应用缓存
     */
    AppCacheScanDoneEvent,
    /**
     * 1.2 系统缓存
     */
    SysCacheScanDoneEvent,
    /**
     * 2. 残留文件
     */
    ResidueScanDoneEvent,
    /**
     * 3. 广告扫描结束
     */
    AdScanDoneEvent,
    /**
     * 4. 临时文件
     * 5. apk文件
     * 6. 大文件
     * SD卡扫描结束
     */
    SDCardScanDoneEvent,
    /**
     * 7. 内存垃圾
     **/
    AppMemoryScanDoneEvent;

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

    /**
     * 是否全部事件都完成
     */
    public static boolean isAllDone() {
        boolean isAllDone = true;
        for (CleanScanDoneEvent event : CleanScanDoneEvent.values()) {
            isAllDone = isAllDone && event.isDone();
        }
        return isAllDone;
    }
    public static boolean isAllDoneWithoutMemory() {
        return AppCacheScanDoneEvent.isDone()
                && SysCacheScanDoneEvent.isDone()
                && ResidueScanDoneEvent.isDone()
                && AdScanDoneEvent.isDone()
                && SDCardScanDoneEvent.isDone();
    }

    /**
     * 清空结束状态
     */
    public static void cleanAllDone() {
        for (CleanScanDoneEvent event : CleanScanDoneEvent.values()) {
            event.setDone(false);
        }
    }

    public static boolean isDone(GroupType type) {
        switch (type) {
            case APP_CACHE:
                // 缓存事件包括应用缓存和系统缓存
                return AppCacheScanDoneEvent.isDone()
                        && SysCacheScanDoneEvent.isDone();
            case RESIDUE:
                return ResidueScanDoneEvent.isDone();
            case TEMP:
            case APK:
            case BIG_FILE:
            case BIG_FOLDER:
                return SDCardScanDoneEvent.isDone();
            case AD:
                return AdScanDoneEvent.isDone();
            case MEMORY:
                return AppMemoryScanDoneEvent.isDone();
            default:
                return false;
        }
    }

}

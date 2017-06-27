package com.jb.filemanager.function.scanframe.clean;


import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.scanframe.clean.event.CleanAppDeepCacheScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanCheckedFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanDeepScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanJunkScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanNoneCheckedEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanProgressDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanPathEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanSingleSysCacheScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanStateEvent;

/**
 * 清理事件管理器
 *
 * @author chenbenbin
 */
public class CleanEventManager {
    private static final long TWO_SECOND = 2000;
    private static CleanEventManager sInstance;
    /**
     * 清理流程的状态
     */
    private CleanStateEvent mState = CleanStateEvent.DELETE_FINISH;

    private CleanEventManager() {
    }

    public static CleanEventManager getInstance() {
        if (sInstance == null) {
            sInstance = new CleanEventManager();
        }
        return sInstance;
    }

    public CleanStateEvent getCleanState() {
        return mState;
    }

    public void setCleanState(CleanStateEvent cleanState) {
        mState = cleanState;
    }

    // ******************************************************************************关键点事件********************************************************************************//

    /**
     * 状态1：开始扫描
     */
    public void sendScanStartEvent() {
        setCleanState(CleanStateEvent.SCAN_ING);
        mState.setLastTime(System.currentTimeMillis());
        TheApplication.getGlobalEventBus().post(mState);
    }

    /**
     * 状态2：扫描完毕
     */
    public void sendScanFinishEvent() {
        setCleanState(CleanStateEvent.SCAN_FINISH);
        mState.setLastTime(System.currentTimeMillis());
        TheApplication.getGlobalEventBus().post(mState);
    }

    /**
     * 状态6：扫描中断
     */
    public void sendScanSuspendEvent() {
        setCleanState(CleanStateEvent.SCAN_SUSPEND);
        mState.setLastTime(System.currentTimeMillis());
        TheApplication.getGlobalEventBus().post(mState);
    }

    /**
     * 通知SD卡扫描完毕
     */
    public void sendSDCardScanDoneEvent() {
        sendScanDoneEvent(CleanScanDoneEvent.SDCardScanDoneEvent);
    }

    /**
     * 通知残留文件扫描完毕
     */
    public void sendResidueScanDoneEvent() {
        sendScanDoneEvent(CleanScanDoneEvent.ResidueScanDoneEvent);
    }

    /**
     * 系统缓存文件扫描完毕
     */
    public void sendSysCacheScanDoneEvent() {
        sendScanDoneEvent(CleanScanDoneEvent.SysCacheScanDoneEvent);
    }

    /**
     * 应用缓存文件扫描完毕
     */
    public void sendAppCacheScanDoneEvent() {
        sendScanDoneEvent(CleanScanDoneEvent.AppCacheScanDoneEvent);
    }

    /**
     * 深度缓存文件扫描完毕
     */
    public void sendDeepCacheScanDoneEvent() {
        CleanDeepScanDoneEvent event = CleanDeepScanDoneEvent.DeepCacheScanDoneEvent;
        event.setDone(true);
        TheApplication.getGlobalEventBus().post(event);
    }

    /**
     * 专清扫描完毕
     */
    public void sendAppDeepCacheScanDoneEvent(CleanAppDeepCacheScanDoneEvent event) {
        event.setDone(true);
        TheApplication.getGlobalEventBus().post(event);
    }

    /**
     * 深度缓存文件扫描完毕
     */
    public void sendJunkScanDoneEvent() {
        CleanJunkScanDoneEvent junkScanDoneEvent = new CleanJunkScanDoneEvent();
        junkScanDoneEvent.setIsDone(true);
        TheApplication.getGlobalEventBus().post(junkScanDoneEvent);
    }

    /**
     * 广告垃圾扫描完毕
     */
    public void sendAdScanDoneEvent() {
        sendScanDoneEvent(CleanScanDoneEvent.AdScanDoneEvent);
    }

    /**
     * 内存扫描完毕
     */
    public void sendAppMemoryScanDoneEvent() {
        boolean isOtherDone = true;
        for (CleanScanDoneEvent event : CleanScanDoneEvent.values()) {
            if (event == CleanScanDoneEvent.AppMemoryScanDoneEvent) {
                continue;
            }
            isOtherDone &= event.isDone();
        }
        if (isOtherDone) {
            sendScanDoneEvent(CleanScanDoneEvent.AppMemoryScanDoneEvent);
        }
    }

    /**
     * 发送扫描各个完毕事件，并统计是否全部发送完毕，则再发总事件
     */
    private void sendScanDoneEvent(CleanScanDoneEvent event) {
        event.setDone(true);
        TheApplication.getGlobalEventBus().post(event);
        if (CleanScanDoneEvent.isAllDone()) {
            CleanCheckedFileSizeEvent.clearSuspendSize();
            CleanScanFileSizeEvent.clearSuspendSize();
            sendScanFinishEvent();
        } else {
            sendAppMemoryScanDoneEvent();
        }
    }

    /**
     * 通知进度条动画结束事件
     */
    public void sendProgressDoneEvent(CleanProgressDoneEvent event) {
//        Logger.w("CleanEventManager", "发送" + event.name());
        TheApplication.getGlobalEventBus().post(event);
    }

    /**
     * 状态3：删除中
     */
    public void sendDeleteStartEvent() {
        setCleanState(CleanStateEvent.DELETE_ING);
        mState.setLastTime(System.currentTimeMillis());
        TheApplication.getGlobalEventBus().post(mState);
    }

    /**
     * 状态4：删除中断
     */
    public void sendDeleteSuspendEvent() {
        setCleanState(CleanStateEvent.DELETE_SUSPEND);
        if (System.currentTimeMillis() - mState.getLastTime() > TWO_SECOND) {
            // 防止多次点击跳转2次结果页
            mState.setLastTime(System.currentTimeMillis());
            TheApplication.getGlobalEventBus().post(mState);
        }
    }

    /**
     * 状态5：完成全部文件删除
     */
    public void sendDeleteFinishEvent() {
        setCleanState(CleanStateEvent.DELETE_FINISH);
        if (System.currentTimeMillis() - mState.getLastTime() > 2000) {
            // 防止多次点击跳转2次结果页
            mState.setLastTime(System.currentTimeMillis());
            TheApplication.getGlobalEventBus().post(mState);
        }
    }

    // ******************************************************************************过程事件********************************************************************************//

    /**
     * 通知扫描路径
     *
     * @param event 事件类型
     * @param path  路径
     */
    public void sendScanPathEvent(CleanScanPathEvent event, String path) {
        event.setPath(path);
        if (event.isSendTime()) {
            TheApplication.getGlobalEventBus().post(event);
        }
    }

    /**
     * 通知扫描的文件大小
     *
     * @param event    事件类型
     * @param scanSize 扫描出的文件大小
     */
    public void sendScanFileSizeEvent(CleanScanFileSizeEvent event,
                                      long scanSize) {
        event.addSize(scanSize);
        if (event.isSendTime()) {
            TheApplication.getGlobalEventBus().postSticky(event);
        }
    }

    /**
     * 通知缓存文件大小
     *
     * @param totalSize 总的文件大小
     */
    public void sendSysCacheSize(long totalSize) {
        // 扫描出的文件大小
        CleanScanFileSizeEvent scanEvent = CleanScanFileSizeEvent.CacheSize;
        scanEvent.addSize(totalSize);
        if (scanEvent.isSendTime()) {
            TheApplication.getGlobalEventBus().postSticky(scanEvent);
        }
        // 选中的文件大小
        CleanCheckedFileSizeEvent checkedEvent = CleanCheckedFileSizeEvent.CacheSize;
        checkedEvent.addSize(totalSize);
        if (checkedEvent.isSendTime()) {
            TheApplication.getGlobalEventBus().postSticky(checkedEvent);
        }
    }

    /**
     * 通知单个应用系统缓存被清除
     *
     * @param delSize 减少的空间大小(正数)
     */
    public void sendSingleSysCacheDelete(long delSize, boolean isSelected) {
        // 扫描出的文件大小
        CleanScanFileSizeEvent scanEvent = CleanScanFileSizeEvent.CacheSize;
        scanEvent.setSize(scanEvent.getSize() - delSize);
        if (scanEvent.isSendTime()) {
            TheApplication.getGlobalEventBus().postSticky(scanEvent);
        }
        if (isSelected) {
            // 选中的文件大小
            CleanCheckedFileSizeEvent checkedEvent = CleanCheckedFileSizeEvent.CacheSize;
            checkedEvent.setSize(checkedEvent.getSize() - delSize);
            if (checkedEvent.isSendTime()) {
                TheApplication.getGlobalEventBus().postSticky(checkedEvent);
            }
        }
    }

    /**
     * 发送单个系统缓存的扫描事件
     *
     * @param packageName 应用名
     * @param size        单个大小
     */
    public void sendSingleSysCacheSize(String packageName, long size) {
        TheApplication.getGlobalEventBus().post(
                new CleanSingleSysCacheScanDoneEvent(packageName, size));
    }

    /**
     * 通知选中的文件大小(单个文件)
     *
     * @param event 事件类型
     * @param size  选中的文件大小
     */
    public void sendCheckedFileSizeEvent(CleanCheckedFileSizeEvent event,
                                         long size) {
        event.addSize(size);
        if (event.isSendTime()) {
            TheApplication.getGlobalEventBus().postSticky(event);
        }
    }

    /**
     * 通知选中文件的大小(一组文件)
     *
     * @param event 事件类型
     * @param size  选中的文件大小
     */
    public void sendCheckedFileAllSizeEvent(CleanCheckedFileSizeEvent event,
                                            long size) {
        event.setSize(size);
        TheApplication.getGlobalEventBus().postSticky(event);
    }

    /**
     * 清空数据
     */
    public void cleanEventData() {
        CleanScanFileSizeEvent.cleanAllSizeData();
        CleanCheckedFileSizeEvent.cleanAllSizeData();
        CleanScanDoneEvent.cleanAllDone();
    }

    // ******************************************************************************标志位事件********************************************************************************//

    /**
     * 通知清理列表的文件的勾选状态(有/没有)
     */
    public void sendNoneFileCheckedEvent(CleanNoneCheckedEvent event) {
        TheApplication.getGlobalEventBus().post(event);
    }

}
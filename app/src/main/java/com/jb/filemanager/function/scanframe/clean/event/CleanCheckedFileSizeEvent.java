package com.jb.filemanager.function.scanframe.clean.event;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.clean.CleanConstants;

import java.util.HashSet;

/**
 * 清理文件：选中的文件大小 - 通知事件
 *
 * @author chenbenbin
 */
public enum CleanCheckedFileSizeEvent {
    /**
     * 缓存文件(系统缓存 + 应用缓存)
     */
    CacheSize(GroupType.APP_CACHE),
    /**
     * 残留文件
     */
    ResidueFileSize(GroupType.RESIDUE),
    /**
     * 广告
     */
    AdSize(GroupType.AD),
    /**
     * 临时文件
     */
    TempFileSize(GroupType.TEMP),
    /**
     * APK文件
     */
    APKFileSize(GroupType.APK),
    /**
     * 大文件
     */
    BigFileSize(GroupType.BIG_FILE),
    /**
     * 大文件夹
     */
    BigFolderSize(GroupType.BIG_FOLDER),
    /**
     * 内存
     */
    MemoryFileSize(GroupType.MEMORY);

    /**
     * 选中文件大小
     */
    private long mSize;
    /**
     * 上次消息发送时间
     */
    private long mLastSendTime;
    private GroupType mType;
    /**
     * 中断时候的扫描结果<br>
     * 用于首次加载时，若遇到应用缓存数据更新，需要重新扫描，则保持当前的扫描数值，当大于这个数值时才更新数据
     */
    private long mSuspendSize;

    CleanCheckedFileSizeEvent(GroupType type) {
        mType = type;
    }

    public static CleanCheckedFileSizeEvent get(GroupType type) {
        for (CleanCheckedFileSizeEvent event : CleanCheckedFileSizeEvent
                .values()) {
            if (event.mType == type) {
                return event;
            }
        }
        return ResidueFileSize;
    }

    public long getSize() {
        // 若当前的数据小于中断时候的扫描结果则不更新
        return mSize > mSuspendSize ? mSize : mSuspendSize;
    }

    public void addSize(long size) {
        mSize += size;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public boolean isSendTime() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastSendTime > CleanConstants.EVENT_INTERVAL) {
            mLastSendTime = curTime;
            return true;
        }
        return false;
    }


    // ************************************* 静态方法 ****************************************//
    /**
     * JunkFile事件集合
     */
    private static final HashSet<CleanCheckedFileSizeEvent> JUNK_FILE_SET = new HashSet<CleanCheckedFileSizeEvent>() {
        {
            add(CacheSize);
            add(ResidueFileSize);
            add(TempFileSize);
            add(APKFileSize);
            add(BigFileSize);
            add(AdSize);
            add(MemoryFileSize);
        }
    };

    /**
     * 获取JunkFile选中的全部类型文件的大小
     */
    public static long getJunkFileAllSize() {
        long size = 0;
        for (CleanCheckedFileSizeEvent event : JUNK_FILE_SET) {
            size += event.getSize();
        }
        return size;
    }

    /**
     * 获取选中的全部类型文件的大小
     */
    public static long getAllSize() {
        long size = 0;
        for (CleanCheckedFileSizeEvent event : CleanCheckedFileSizeEvent.values()) {
            size += event.getSize();
        }
        return size;
    }

    /**
     * 清空全部类型文件的大小数据
     */
    public static void cleanAllSizeData() {
        for (CleanCheckedFileSizeEvent event : CleanCheckedFileSizeEvent.values()) {
            event.setSize(0);
        }
    }

    /**
     * 初始化选中的数据
     */
    public static void initData() {
        CleanCheckedFileSizeEvent.ResidueFileSize.setSize(0);
        CleanCheckedFileSizeEvent.TempFileSize.setSize(CleanScanFileSizeEvent.TempFileSize.getSize());
        CleanCheckedFileSizeEvent.APKFileSize.setSize(CleanScanFileSizeEvent.APKFileSize.getSize());
        CleanCheckedFileSizeEvent.BigFileSize.setSize(0);
        CleanCheckedFileSizeEvent.MemoryFileSize.setSize(CleanScanFileSizeEvent.MemoryFileSize.getSize());
    }

    /**
     * 获取JunkFile选中的全部类型文件的大小
     */
    public static long getJunkFileAllSize(boolean isWithoutMemory) {
        long size = 0;
        for (CleanCheckedFileSizeEvent event : JUNK_FILE_SET) {
            if (isWithoutMemory && event.mType == GroupType.MEMORY) {
                continue;
            }
            size += event.getSize();
        }
        return size;
    }

    /**
     * 重新扫描时触发：将当前的扫描大小结果拷贝记录
     */
    public static void updateSuspendSize() {
        for (CleanCheckedFileSizeEvent event : CleanCheckedFileSizeEvent.values()) {
            event.mSuspendSize = event.mSize;
            event.mSize = 0;
        }
    }

    /**
     * 清空中断时候的扫描结果
     */
    public static void clearSuspendSize() {
        for (CleanCheckedFileSizeEvent event : CleanCheckedFileSizeEvent.values()) {
            event.mSuspendSize = 0;
        }
    }
}
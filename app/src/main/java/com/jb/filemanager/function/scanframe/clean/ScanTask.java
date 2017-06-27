package com.jb.filemanager.function.scanframe.clean;


/**
 * 扫描任务
 *
 * @author chenbenbin
 */
public abstract class ScanTask implements ITask {
    // 标志位
    protected volatile boolean mIsScanning = false;
    protected volatile boolean mIsSwitch = false;
    protected volatile boolean mIsStop = false;

    @Override
    public boolean isRunning() {
        return mIsScanning;
    }
}

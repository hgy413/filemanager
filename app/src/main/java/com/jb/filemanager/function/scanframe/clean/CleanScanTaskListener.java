package com.jb.filemanager.function.scanframe.clean;

/**
 * 扫描任务监听器
 *
 * @author chenbenbin
 */
public interface CleanScanTaskListener {
    /**
     * 任务切换完成
     */
    void onSwitchDone(ITask task);

    /**
     * 任务执行完毕
     */
    void onTaskDone(ITask task);
}

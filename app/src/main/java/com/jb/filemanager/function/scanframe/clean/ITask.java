package com.jb.filemanager.function.scanframe.clean;

/**
 * 任务接口
 *
 * @author chenbenbin
 */
public interface ITask {
    /**
     * 是否可以运行
     */
    boolean isAvailable();
    /**
     * 开始任务
     */
    void startTask();
    /**
     * 结束任务
     */
    void stopTask();
    /**
     * 切换任务
     */
    void switchTask();

    /**
     * 任务是否在执行中
     */
    boolean isRunning();
}

package com.jb.filemanager.function.scanframe.clean;

import android.content.Context;

import com.jb.filemanager.util.Logger;

import java.util.LinkedList;

/**
 * Created by xiaoyu on 2016/12/14 21:11.
 */

public class CleanScanTaskManager implements CleanScanTaskListener {
    private static final String TAG = "CleanManager_Scan";
    private static CleanScanTaskManager sInstance;
    private Context mContext;
    private LinkedList<ITask> mScanTasks = new LinkedList<>();
    //private CleanDeepCacheScanTask mDeepCacheScanTask;
    private CleanJunkFileScanTask mJunkFileScanTask;

    private CleanScanTaskManager(Context context) {
        mContext = context;
        init();
    }

    public static CleanScanTaskManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CleanScanTaskManager(context);
        }
        return sInstance;
    }

    private void init() {
        mJunkFileScanTask = new CleanJunkFileScanTask(mContext);
        //mDeepCacheScanTask = new CleanDeepCacheScanTask(mContext);
        mJunkFileScanTask.setTaskListener(this);
        //mDeepCacheScanTask.setTaskListener(this);
    }

   /* public CleanDeepCacheScanTask getDeepCacheScanTask() {
        return mDeepCacheScanTask;
    }*/

    public CleanJunkFileScanTask getJunkFileScanTask() {
        return mJunkFileScanTask;
    }

    /**
     * 正在运行的任务
     */
    private ITask mRunningTask;

    /**
     * 初始化默认的任务队列
     */
    public void initDefaultTaskList() {
        mScanTasks.clear();
        mScanTasks.add(mJunkFileScanTask);
        //mScanTasks.add(mDeepCacheScanTask);
    }

    /**
     * 启动默认扫描任务
     */
    public void startDefaultTaskList() {
        if (!mScanTasks.isEmpty()) {
            startScanTask(mScanTasks.get(0));
        }
    }

    /**
     * 开始JunkFile扫描
     */
    public void startJunkFileScanTask() {
        startScanTask(mJunkFileScanTask);
    }

    /**
     * 开始深度缓存扫描
     */
    /*public void startDeepCacheScanTask() {
        if (!mDeepCacheScanTask.canRestartScan() && !mScanTasks.contains(mDeepCacheScanTask)) {
            Logger.w(TAG, "DeepCache上次扫描时间在1分钟之内，不重复扫描!");
            return;
        }
        startScanTask(mDeepCacheScanTask);
    }*/

    /**
     * 开始WhatApp扫描
     */
   /* public void startWhatAppScanTask() {
        if (!mDeepCacheScanTask.isRunning()) {
            startScanTask(mDeepCacheScanTask.getWhatsAppScanTask());
        }
    }*/

    /**
     * 开始Facebook扫描
     */
    /*public void startFacebookScanTask() {
        if (!mDeepCacheScanTask.isRunning()) {
            startScanTask(mDeepCacheScanTask.getFacebookScanTask());
        }
    }*/

    /**
     * 开始任务扫描
     */
    public void startScanTask(ITask task) {
        String taskName = task.getClass().getSimpleName();
        Logger.w(TAG, "请求" + taskName + "扫描");
        if (mScanTasks.isEmpty()) {
            mScanTasks.add(task);
        }
        ITask firstTask = mScanTasks.get(0);
        if (firstTask.equals(task)) {
            if (!task.isRunning()) {
                // 动作A: 启动任务
                // 条件: 首位为空or要启动的任务且未启动，则启动之
                Logger.w(TAG, "动作A: 启动任务: " + taskName);
                loopScanTask();
                return;
            } else if (mRunningTask == task) {
                // 动作B: 不处理
                // 条件: 要启动的任务正在执行中
                Logger.w(TAG, "动作B: 不处理: " + taskName);
                return;
            }
        }

        // 执行动作C: 切换任务
        // 条件: 要启动的任务正在执行中
        Logger.w(TAG, "执行动作C: 切换任务: " + taskName);
        firstTask.switchTask();
    }

    /**
     * 执行递归循环扫描SD卡的异步线程
     */
    private void loopScanTask() {
        if (mScanTasks.isEmpty()) {
            mRunningTask = null;
            return;
        }
        mRunningTask = mScanTasks.get(0);
        mRunningTask.startTask();
    }

    @Override
    public void onSwitchDone(ITask task) {
        if (mScanTasks.contains(task)) {
            mScanTasks.remove(task);
            mScanTasks.add(task);
        }
        loopScanTask();
    }

    @Override
    public void onTaskDone(ITask task) {
        if (mScanTasks.contains(task)) {
            mScanTasks.remove(task);
        }
        loopScanTask();
    }

    /**
     * 停止扫描
     */
    public void stopAllTask() {
        mScanTasks.clear();
        if (mRunningTask != null) {
            mRunningTask.stopTask();
        }
    }

    /**
     * 退出应用
     */
    public void onAppExit() {
        stopAllTask();
    }
}

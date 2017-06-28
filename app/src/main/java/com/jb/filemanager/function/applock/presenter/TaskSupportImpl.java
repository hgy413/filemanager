package com.jb.filemanager.function.applock.presenter;

import android.os.Handler;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nieyh on 2017/1/4.<br/>
 * 基础的用于任务调度Support<p/> 方法:
 * <ol>
 * <li>{@link #toAsynWork(Runnable)} 启动异步任务</li>
 * <li>{@link #toUiWork(Runnable, long)} 启动ui任务</li>
 * <li>{@link #removeUiWork(Runnable)} 删除ui任务</li>
 * </ol>
 */

public class TaskSupportImpl implements ITaskSupport {

    @IntDef({TYPE_SINGLE_SYNC, TYPE_SINGLE_UI, TYPE_THREAD_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThreadMode {
    }

    private final String TAG = "TaskSupportImpl";

    private ExecutorService mSingleTask;

    private Handler mHandler;

    public static final int TYPE_SINGLE_SYNC = 1;

    public static final int TYPE_SINGLE_UI = 2;

    public static final int TYPE_THREAD_ALL = 3;

    private boolean isStop = false;

    public TaskSupportImpl() {
        mSingleTask = Executors.newSingleThreadExecutor();
        mHandler = new Handler();
    }

    /**
     * @param mode 代表模式
     *             <ol>
     *             <li>{@link #TYPE_THREAD_ALL} 包括ui线程与异步</li>
     *             <li>{@link #TYPE_SINGLE_UI} 单纯ui操作</li>
     *             <li>{@link #TYPE_SINGLE_SYNC} 单纯异步操作</li>
     *             </ol>
     */
    public TaskSupportImpl(@ThreadMode int mode) {
        switch (mode) {
            case TYPE_SINGLE_SYNC:
                mSingleTask = Executors.newSingleThreadExecutor();
                break;
            case TYPE_SINGLE_UI:
                mHandler = new Handler();
                break;
            case TYPE_THREAD_ALL:
                mSingleTask = Executors.newSingleThreadExecutor();
                mHandler = new Handler();
                break;
        }
    }

    @Override
    public void toAsynWork(Runnable work) {
        synchronized (TAG) {
            if (!isStop) {
                if (mSingleTask != null) {
                    mSingleTask.execute(work);
                }
            }
        }
    }

    @Override
    public void toUiWork(Runnable work, long delay) {
        synchronized (this) {
            if (!isStop) {
                if (mHandler != null) {
                    if (delay == 0) {
                        mHandler.post(work);
                    } else {
                        mHandler.postDelayed(work, delay);
                    }
                }
            }
        }
    }

    @Override
    public void removeUiWork(Runnable work) {
        synchronized (this) {
            if (mHandler != null) {
                mHandler.removeCallbacks(work);
            }
        }
    }

    @Override
    public void release() {
        synchronized (TAG) {
            if (mSingleTask != null) {
                mSingleTask.shutdownNow();
                mSingleTask = null;
            }
        }
        synchronized (this) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
        isStop = true;
    }
}

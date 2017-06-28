package com.jb.filemanager.function.applock.presenter;

/**
 * Created by nieyh on 2017/1/4.
 */

public interface ITaskSupport {
    void toAsynWork(Runnable work);

    void toUiWork(Runnable work, long delay);

    void removeUiWork(Runnable work);

    void release();
}

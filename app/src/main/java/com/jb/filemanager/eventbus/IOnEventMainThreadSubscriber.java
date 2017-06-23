package com.jb.filemanager.eventbus;

/**
 * 接收指定类型事件的接口.承接这个接口以免手误错写接收方法.<br>
 * 各个方法的意义参看{@code EventBus}的介绍<br>
 * @author laojiale
 *
 * @param <T>
 */
public interface IOnEventMainThreadSubscriber<T> {

    /**
     * 在主线程接收事件.<br>
     * @param event event
     */
    void onEventMainThread(T event);

}
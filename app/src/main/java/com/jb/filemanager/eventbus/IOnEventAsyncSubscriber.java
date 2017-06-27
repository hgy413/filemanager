package com.jb.filemanager.eventbus;

/**
 * 接收指定类型事件的接口.承接这个接口以免手误错写接收方法.<br>
 * 方法的意义参看{@code EventBus}的介绍<br>
 * @author laojiale
 *
 * @param <T>
 */
public interface IOnEventAsyncSubscriber<T> {

	/**
	 * 事件处理会在单独的线程中执行，主要用于在后台线程中执行耗时操作，每个事件会开启一个线程（有线程池）。<br>
	 * @param event
	 */
	public void onEventAsync(T event);
}

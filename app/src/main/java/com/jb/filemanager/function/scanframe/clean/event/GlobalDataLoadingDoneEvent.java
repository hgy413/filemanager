package com.jb.filemanager.function.scanframe.clean.event;

/**
 * 程序必要的全局数据(如设置项数据等)加载完成后发出的事件<br>
 * 会使用{@code EventBus.post(Object)}发出<br>
 * 
 * @author laojiale
 * 
 */
public class GlobalDataLoadingDoneEvent {

	public GlobalDataLoadingDoneEvent() {
		isLoaded = true;
	}

	//是否加载完成
	public static boolean isLoaded = false;

}

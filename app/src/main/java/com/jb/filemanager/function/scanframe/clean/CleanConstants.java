package com.jb.filemanager.function.scanframe.clean;

/**
 * 清理功能常量
 * 
 * @author chenbenbin
 * 
 */
public class CleanConstants {
	/**
	 * 发送消息事件的时间间隔<br>
	 * 避免消息发送的速度比界面刷新的速度更快
	 */
	public static final long EVENT_INTERVAL = 20;
	/**
	 * 打开应用缓存页面的请求码
	 */
	public static final int REQUEST_CODE_FOR_SYS_CACHE = 0x382;
	/**
	 * 打开结果页的请求常量
	 */
	public static final String DONE_ACTIVITY_INTENT_EXTRA = "done_activity_intent_extra";
	/**
	 * 打开结果页：清理完成
	 */
	public static final int DONE_ACTIVITY_INTENT_EXTRA_NORMAL = 0;
	/**
	 * 打开结果页：扫描结果为空
	 */
	public static final int DONE_ACTIVITY_INTENT_EXTRA_NONE = 1;
	/**
	 * 清理主界面顶部：进入橙色状态的阀值：20M
	 */
	public static final long MAIN_TOP_MIDDLE_LEVEL = 20 * 1024 * 1024;
	/**
	 * 清理主界面顶部：进入红色状态的阀值：300M
	 */
	public static final long MAIN_TOP_HIGH_LEVEL = 300 * 1024 * 1024;

}

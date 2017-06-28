package com.jb.filemanager.function.applock.constant;

/**
 * 自定义Action
 * Created by makai on 15-6-9.
 */
public interface ICustomAction {

	/**
	 * 安装卸载
	 */
	String ACTION_SENIOR_INSTALL_AND_UNINSTALL = "action.senior.install.and.uninstall";

	/**
	 * 状态栏
	 */
	String ACTION_SENIOR_STATUS_BAR = "action.senior.status.bar";

	/**
	 * 来电
	 */
	String ACTION_SENIOR_CALLING = "action.senior.calling";

	/**
	 * 设置
	 */
	String ACTION_SENIOR_SETTING = "action.senior.setting";

	/**
	 * wifi
	 */
	String ACTION_SWITCH_WIFI = "action.switch.wifi";

	/**
	 * 蓝牙
	 */
	String ACTION_SWITCH_BLUETOOTH = "action.switch.bluetooth";

	/**
	 * 移动数据
	 */
	String ACTION_SWITCH_MOBILE_NETWORK_DATA = "action.switch.mobile.network.data";

	/**
	 *　数据同步
	 */
	String ACTION_SWITCH_DATA_SYNCHRONIZATION = "action.switch.data.synchronization";

	/**
	 * 内置全场景模式
	 */
	String ACTION_INNER_SENSE_ALL_MODEL = "action.inner.sense.all.model";

	/**
	 * 内置访客景模式
	 */
	String ACTION_INNER_SENSE_VISITOR_MODEL = "action.inner.sense.visitor.model";


	/***************************************自定义*************************************************/

	/**
	 * 用户输入密码进入主页面
	 */
	String ACTION_BROADCAST_ENTER_MAIN_ACTIVITY = "com.jiubang.alocker.enter.main";

	/**
	 * 用户没有输入密码，选择退出
	 */
	String ACTION_BROADCAST_EXIT_MAIN_ACTIVITY = "com.jiubang.alocker.exit.main";

	/**
	 * 主题Action
	 */
	String ACTION_A_LOCKER_THEME = "com.jiubang.alocker.theme";

	/**
	 * 主题发生变化
	 */
	String ACTION_A_LOCKER_THEME_CHANGE = "com.jiubang.alocker.theme.change";

	/**
	 * 推荐应用加锁层 退出
	 */
	String ACTION_BROADCAST_EXIT_RECOMMEND_APP = "com.jiubang.alocker.exit.recommend_app_layer";

	/**
	 * 推荐应用加锁层 动画结束
	 */
	String ACTION_BROADCAST_RECOMMEND_APP_ANIMATION_FINISH = "com.jiubang.alocker.recommend_app_layer.animation_finish";

	/**
	 * 隐藏拨号Action
	 */
	String ACTION_SECRET_CODE = "android.provider.Telephony.SECRET_CODE";

	/**
	 * 监听vip状态
	 */
	String ACTION_MONITOR_VIP = "com.jiubang.alocker.monitor.vip";

	/**
	 * 刷新vip状态
	 */
	String ACTION_REFRESH_VIP = "com.jiubang.alocker.refresh.vip";

	/**
	 * 裁剪图片完成
	 */
	String ACTION_BROADCAST_CROP_WALLPAPER_FINISHED = "com.jiubang.alocker.crop_wallpaper_finished";

	/**
	 * 墙纸设置完成
	 */
	String ACTION_BROADCAST_SET_WALLPAPER_FINISHED = "com.jiubang.alocker.set_wallpaper_finished";

	/**
	 * 墙纸删除完成
	 */
	String ACTION_BROADCAST_DEL_WALLPAPER_FINISHED = "com.jiubang.alocker.del_wallpaper_finished";

	/**
	 * 主题设置完成
	 */
	String ACTION_BROADCAST_SET_THEME_FINISHED = "com.jiubang.alocker.set_theme_finished";

	/**
	 * 主题删除完成
	 */
	String ACTION_BROADCAST_DEL_THEME_FINISHED = "com.jiubang.alocker.del_theme_finished";

    /**
     * 设置隐藏 icon 操作被中断
     */
    String ACTION_BROADCAST_INTERRUPT_SET_HIDE_ICON = "com.jiubang.alocker.interrupt_set_hide_icon";

	String ACTION_REQUEST_NATIVE_ADS = "action_request_native_ads";

	String ACTION_REQUEST_NATIVE_WEATHER = "action_request_native_weather";

}

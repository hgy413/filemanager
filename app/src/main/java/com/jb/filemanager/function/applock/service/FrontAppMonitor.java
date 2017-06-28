package com.jb.filemanager.function.applock.service;

import android.content.ComponentName;
import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.common.TickTimer;
import com.jb.filemanager.function.applock.event.OnFrontAppChangedEvent;
import com.jb.filemanager.function.applock.event.OnFrontAppTickEvent;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.device.Machine;

/**
 * 负责监听前台应用的变化<br>
 */
public class FrontAppMonitor implements TickTimer.TickTimerListener {
	private final static String LOG_TAG = FrontAppMonitor.class.getSimpleName();

	public final static String INVALID_PACKAGE_NAME = "invalid_package_name";
	private final static String INVALID_ACTIVITY_NAME = "invalid_activity_name";
	private final static ComponentName INVALID_COMPONENT_NAME = new ComponentName(
			INVALID_PACKAGE_NAME, INVALID_ACTIVITY_NAME);

	private Context mContext;
	private volatile ComponentName mTopComponentName;

	public FrontAppMonitor(Context context) {
		mContext = context.getApplicationContext();
	}

	private void checkFrontApp() {
		ComponentName topActivity = null;
		// 5.1或以上
		if (Machine.HAS_SDK_5_1_1) {
			if (AppUtils
					.isPermissionPackageUsageStatsGrandedLollipopMr1(mContext)) {
				topActivity = AppUtils.getFrontActivityLollipopMr1(mContext);
			}
		}
		// 5.0
		else if (Machine.HAS_SDK_LOLLIPOP) {
			if (AppUtils
					.isPermissionPackageUsageStatsGrandedOnLollipop(mContext)) {
				topActivity = AppUtils.getFrontActivityOnLollipop(mContext);
			}
		}
		// 5.0以下
		else {
			topActivity = AppUtils.getTopActivity(mContext);
		}
		String oldPackageName = INVALID_PACKAGE_NAME;
		if (mTopComponentName != null) {
			oldPackageName = mTopComponentName.getPackageName();
		}
		if (topActivity == null) {
			topActivity = INVALID_COMPONENT_NAME;
		}
		mTopComponentName = topActivity;

		boolean isChanged = false;
		if (!oldPackageName.equals(mTopComponentName.getPackageName())) {
			OnFrontAppChangedEvent.getInstance().setComponentName(mTopComponentName);
			TheApplication.getGlobalEventBus().post(
					OnFrontAppChangedEvent.getInstance());
			isChanged = true;
		}
		OnFrontAppTickEvent.getInstance().setComponentName(mTopComponentName);
		OnFrontAppTickEvent.getInstance().setIsFontAppChanged(isChanged);
		TheApplication.getGlobalEventBus()
				.post(OnFrontAppTickEvent.getInstance());
	}

	/**
	 * onDestroy
	 */
	public void onDestroy() {

	}

	@Override
	public void onTick(long passTime) {
		checkFrontApp();
	}

}

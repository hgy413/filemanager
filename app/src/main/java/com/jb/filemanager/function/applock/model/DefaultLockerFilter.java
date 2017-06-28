package com.jb.filemanager.function.applock.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.constant.ICustomAction;
import com.jb.filemanager.function.applock.constant.LockerEnv;
import com.jb.filemanager.manager.PackageManagerLocker;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.device.Machine;

import java.util.Iterator;
import java.util.List;

/**
 *
 * Created by makai on 15-6-11.
 */
public class DefaultLockerFilter {

	public void doFilter(List<String> mAppLockerData, List<ComponentName> mSpecialData, ComponentName componentName) {
		if (LockerEnv.Package.FAKE_ITEM_PACKAGE.equals(componentName.getPackageName())) {
			doFakeAppFilter(mAppLockerData, mSpecialData, componentName);
		} else {
			mAppLockerData.add(componentName.getPackageName());
		}
	}

	public void doFakeAppFilter(List<String> mAppLockerData, List<ComponentName> mSpecialData, ComponentName componentName) {
		String className = componentName.getClassName();
		if (ICustomAction.ACTION_SENIOR_INSTALL_AND_UNINSTALL.equals(className)) {
			List<String> installPackageName = AppUtils.getInstallPackageName(TheApplication.getAppContext());
			List<String> unInstallPackageName = AppUtils.getUnInstallPackageName(TheApplication.getAppContext());
			if (null != installPackageName && null != unInstallPackageName) {
				for (String pkg : installPackageName) {
					Iterator<String> iterator = unInstallPackageName.iterator();
					while (iterator.hasNext()) {
						String next = iterator.next();
						if (next.equals(pkg)) {
							iterator.remove();
						}
					}
				}
				if (!installPackageName.isEmpty()) {
					mAppLockerData.addAll(installPackageName);
				}

				if (!unInstallPackageName.isEmpty()) {
					mAppLockerData.addAll(unInstallPackageName);
				}
			}
		} else if (ICustomAction.ACTION_SENIOR_SETTING.equals(className)) {
			List<String> settingPackageName = AppUtils.getSettingPackageName(TheApplication.getAppContext());
			if (null != settingPackageName && !settingPackageName.isEmpty()) {
				mAppLockerData.addAll(settingPackageName);
			}
		} else if (ICustomAction.ACTION_SENIOR_STATUS_BAR.equals(className)) {
			mAppLockerData.add(LockerEnv.Package.SYSTEM_UI_PACKAGE);
		} else {
			mSpecialData.add(componentName);
		}
	}

	public static boolean doFilterFavoriteProvide(Context context, ComponentName componentName) {
		if (LockerEnv.Package.FAKE_ITEM_PACKAGE.equals(componentName.getPackageName())) {
			String cls = componentName.getClassName();
			if (ICustomAction.ACTION_SENIOR_CALLING.equals(cls) || ICustomAction.ACTION_SWITCH_MOBILE_NETWORK_DATA.equals(cls)) {
				return !Machine.isCanUseSim(context);
			} else if (ICustomAction.ACTION_SENIOR_STATUS_BAR.equals(cls)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					return true;
				}
				Intent intent = new Intent();
				intent.setClassName(LockerEnv.Package.SYSTEM_UI_PACKAGE, LockerEnv.Package.SYSTEM_UI_CLASSNAME);
				List<ResolveInfo> resolveInfos = PackageManagerLocker.getInstance().queryIntentActivities(intent, 0);
				return !(resolveInfos != null && resolveInfos.size() > 0);
			} else if (ICustomAction.ACTION_SENIOR_SETTING.equals(cls)) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.BRAND.toLowerCase().contains("htc")) {
					return true;
				}
			}
		}
		return false;
	}

}

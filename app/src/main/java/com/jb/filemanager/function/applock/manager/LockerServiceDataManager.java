package com.jb.filemanager.function.applock.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.AppLockerLockAppEvent;
import com.jb.filemanager.function.applock.event.AppLockerUnlockAppEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.service.FrontAppMonitor;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;

import java.util.List;

/**
 * 锁屏管理器
 * 功能描述：<br>
 * 1、通过获取已经所有上锁的应用.
 * 2、对于获取到的栈顶应用包名进行过滤.
 * 3、如果需要展示浮窗 则发送事件 {@link AppLockerLockAppEvent}
 */
class LockerServiceDataManager {

    public static final String TAG = "LockerServiceDataManager";

    /**
     * Context
     */
    private Context mContext = null;
    /**
     * 常用桌面
     */
    private String mUseLauncherPackageName = null;
    /**
     * 桌面包名
     */
    private List<String> mLauncherList = null;
    /**
     * 上一次的ComponentName
     */
    private ComponentName mLastComponentName = null;
    /**
     * 是否需要检查
     */
    private boolean mNeedCheck = true;

    /**
     * 所有的已经加锁的应用的包名
     */
    private List<String> mAppLockerData;

    public LockerServiceDataManager(Context context) {
        mContext = context;
        mLauncherList = AppUtils.getLauncherPackageNames(mContext);
        startLoadData();
    }

    public void reset() {
        mNeedCheck = true;
        mLastComponentName = null;
    }

    /**
     * 开始获取数据
     * 同步方法
     */
    public void startLoadData() {
        mAppLockerData = AppLockerDataManager.getInstance().getLockAppsNamesInfo();
    }

    /**
     * 检查状态变化
     */
    public boolean checkStatusChanage(ComponentName componentName) {
        if (componentName == null) {
            return false;
        }
        if (componentName.getPackageName().equals(FrontAppMonitor.INVALID_PACKAGE_NAME)) {
            // 获取不到栈顶时，不做任何操作
            return false;
        }
        if ((mLastComponentName == null && null != componentName) || (componentName != null && !componentName.getPackageName()
                .equals(mLastComponentName.getPackageName()))) {
            // 不是上一个，栈顶程序变了
            mNeedCheck = true;
            mLastComponentName = componentName;
        } else {
            mNeedCheck = false;
        }
        if (mNeedCheck) {
            // 由于存在监听时间间隔中用户快速切换程序，导致不弹锁，所以要加上标志mNeedCheck
            String pkgname = componentName.getPackageName();
            if (!TextUtils.isEmpty(pkgname) && pkgname.equals("com.android.settings")) {
                return false;
            }
            if (!componentName.getPackageName().equals(Const.PACKAGE_NAME) && findAppByAppLockerData(pkgname)) {
                // 上锁
                TheApplication.getGlobalEventBus().post(new AppLockerLockAppEvent(componentName));
                return true;
            } else {
                // 解开锁 防止页面被异常切换
                TheApplication.getGlobalEventBus().post(new AppLockerUnlockAppEvent());
                for (String value : mLauncherList) {
                    if (value.equals(pkgname)) {
                        mUseLauncherPackageName = pkgname;
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 查询是否存在这个应用程序
     */
    private boolean findAppByAppLockerData(String mLastPackageName) {
        if (null != mAppLockerData) {
            for (String pkg : mAppLockerData) {
                if (mLastPackageName.equals(pkg)) {
                    return true;
                }
            }
        } else {
            Logger.d(TAG, "mAppLockerData : " + mAppLockerData.size());
        }
        return false;
    }

    /**
     * 选择退出
     */
    public void choiceCancel(String pkgName) {
        AppUtils.gotoLauncherWithoutChoice(mContext, mUseLauncherPackageName);
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (!pkgName.equals(mContext.getPackageName())) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (null != runningAppProcessInfo.pkgList && runningAppProcessInfo.pkgList.length > 0) {
                    for (String value : runningAppProcessInfo.pkgList) {
                        if (pkgName.equals(value)) {
                            Process.killProcess(runningAppProcessInfo.pid);
                            break;
                        }
                    }
                }
            }
            activityManager.killBackgroundProcesses(pkgName);
        }
    }
}

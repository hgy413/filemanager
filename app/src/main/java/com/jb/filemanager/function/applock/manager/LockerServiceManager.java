package com.jb.filemanager.function.applock.manager;

import android.content.ComponentName;
import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.AppLockerDataChangedEvent;
import com.jb.filemanager.function.applock.event.AppLockerKillAppEvent;
import com.jb.filemanager.function.applock.event.AppLockerLockAppEvent;
import com.jb.filemanager.function.applock.event.AppLockerUnlockAppEvent;
import com.jb.filemanager.function.applock.event.OnFrontAppTickEvent;

import org.greenrobot.eventbus.Subscribe;

/**
 * 应用锁服务管理
 * 功能描述核心处理 上锁 与 展示悬浮窗<br>
 * 通过管理 LockerServiceImpl 类来处理所有逻辑
 */
public class LockerServiceManager {

    public static final String TAG = "LockerServiceManager";

    private static LockerServiceManager sInstance;
    /**
     * LockerServiceImpl
     */
    private LockerServiceImpl mLockerServiceImpl = null;
    /**
     * Context
     */
    private Context mContext = null;

    //辅助功能正在运行
    private boolean isAccessibilityIsRuning = false;

    /**
     * 当运行辅助功能，或者停止辅助功能时候要置位这个标记。
     * <b>防止冲突<b/>
     *
     * @param accessibilityIsRuning 是否正在运行
     */
    public void setAccessibilityIsRuning(boolean accessibilityIsRuning) {
        isAccessibilityIsRuning = accessibilityIsRuning;
    }


    private LockerServiceManager() {
        mContext = TheApplication.getAppContext();
        mLockerServiceImpl = new LockerServiceImpl(mContext);
        TheApplication.getGlobalEventBus().register(this);
    }

    public static LockerServiceManager getInstance() {
        if (null == sInstance) {
            sInstance = new LockerServiceManager();
        }
        return sInstance;
    }

    /**
     * 以一定的时间间隔不断发送的事件，附带当前前台应用的信息
     */
    public void onFrontAppTick(ComponentName componentName) {
//        Logger.w(TAG, componentName.getPackageName());
        mLockerServiceImpl.actionOnFrontAppTick(componentName);
    }

    /**
     * 重新设置上一次加锁记录
     */
    public void resetRecordLockerPackage() {
        mLockerServiceImpl.actionScreenOpenClearData();
    }

    /**
     * 锁应用
     */
    public void lockApp(String pkgname) {
        mLockerServiceImpl.actionLockApp(pkgname);
    }

    /**
     * 选择退出
     */
    public void choiceQuit(String pkgname) {
        mLockerServiceImpl.actionUserChoiceCancel(pkgname);
    }

    /**
     * 刷新数据，当锁应用发生变化时调用
     */
    public void refreshLockerData() {
        mLockerServiceImpl.actionRefreshLockerData();
    }

    /**
     * 以一定的时间间隔不断发送的事件，附带当前前台应用的信息
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OnFrontAppTickEvent event) {
        onFrontAppTick(event.getTopActivity());
    }

    /**
     * 当要去锁上某个应用时发出
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(AppLockerLockAppEvent event) {
        lockApp(event.getComponentName().getPackageName());
    }

    @Subscribe
    public void onEventMainThread(AppLockerKillAppEvent event) {
        choiceQuit(event.pkgName);
    }

    @Subscribe
    public void onEventMainThread(AppLockerUnlockAppEvent event) {
        mLockerServiceImpl.actionUnLockApp();
    }

    /**
     * 应用锁的数据更新
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(AppLockerDataChangedEvent event) {
        refreshLockerData();
    }

    /**
     * 销毁
     */
    public void onDestory() {
        TheApplication.getGlobalEventBus().unregister(this);
        mLockerServiceImpl.onDestory();
        mLockerServiceImpl = null;
        sInstance = null;
    }
}

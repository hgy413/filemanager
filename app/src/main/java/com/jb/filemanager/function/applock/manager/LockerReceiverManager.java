package com.jb.filemanager.function.applock.manager;

import android.content.Context;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.ScreenStateEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;

import org.greenrobot.eventbus.Subscribe;

/**
 * 将AppLock所有Receiver的业务逻辑整合到这里
 * @author zhanghuijun
 */
public class LockerReceiverManager {

    private static LockerReceiverManager sLockerReceiverManager = null;

    private Context mContext = null;

    private LockerReceiverManager() {
        mContext = TheApplication.getAppContext();
        TheApplication.getGlobalEventBus().register(this);
    }

    public static LockerReceiverManager getInstance() {
        if (sLockerReceiverManager == null) {
            sLockerReceiverManager = new LockerReceiverManager();
        }
        return sLockerReceiverManager;
    }

    /**
     * 卸载时调用
     */
    private void onAppRemove(String packageName) {
        AppLockerDataManager.getInstance().unlockItem(packageName);
    }

    /**
     * 屏幕关闭的时候 关闭顶部应用的检测.<br>
     *     {@link ScreenStateEvent} 屏幕状态事件 <br>
     *     由{@link com.jb.filemanager.receiver.ScreenStateReceiver} 屏幕监听器发送
     * @param event
     */
    @Subscribe
    public void onEventMainThread(ScreenStateEvent event) {
        final int state = event.mScreenState;
        switch (state) {
            case ScreenStateEvent.SCREEN_ON:
                LockerMonitorManager.getInstance().startMonitor();
                break;
            case ScreenStateEvent.SCREEN_OFF:
                LockerMonitorManager.getInstance().stopMonitor();
                break;
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        TheApplication.getGlobalEventBus().unregister(this);
        sLockerReceiverManager = null;
    }
}

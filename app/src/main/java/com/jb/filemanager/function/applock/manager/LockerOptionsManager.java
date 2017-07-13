package com.jb.filemanager.function.applock.manager;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.event.ScreenStateEvent;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-13.
 */

public class LockerOptionsManager {

    private List<String> mLockerHistoryList = new ArrayList<>();

    private static LockerOptionsManager sInstance;

    private LockerOptionsManager() {
        TheApplication.getGlobalEventBus().register(this);
    }

    public static LockerOptionsManager getInstance() {
        if (sInstance == null) {
            sInstance = new LockerOptionsManager();
        }
        return sInstance;
    }

    /**
     * 是否需要上锁
     * */
    public boolean isNeedLocker(String pkgName) {
        // 是否离开后就可以重新上锁
        if (AppLockerDataManager.getInstance().isLockForLeave()) {
            Logger.w("nieyh", "每次都应该锁上 " + pkgName);
            return true;
        } else {
            if (mLockerHistoryList.contains(pkgName)) {
                Logger.w("nieyh", "存在历史记录" + pkgName + " 不应该上锁");
                return false;
            } else {
                mLockerHistoryList.add(pkgName);
                Logger.w("nieyh", "不存在历史记录" + pkgName + " 应该上锁");
                return true;
            }
        }
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
            case ScreenStateEvent.SCREEN_OFF:
                mLockerHistoryList.clear();
                break;
        }
    }

}

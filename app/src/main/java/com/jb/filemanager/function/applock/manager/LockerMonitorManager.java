package com.jb.filemanager.function.applock.manager;

import android.os.HandlerThread;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.applock.common.TickTimer;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.service.FrontAppMonitor;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.Logger;

/**
 * Created by nieyh on 2016/12/28.
 * 初始化监视器 监视栈顶包名
 */

public class LockerMonitorManager {

    private static LockerMonitorManager sIntance;

    private HandlerThread mAsyncHandlerThread;

    private TickTimer mTickTimer;

    private FrontAppMonitor mFrontAppMonitor;

    private LockerMonitorManager() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_APP_LOCK_ENABLE, false);
        boolean isHave = AppLockerDataManager.getInstance().isHaveLockerApp();
        if (isEnable && isHave) {
            startMonitor();
        }
    }

    public static LockerMonitorManager getInstance() {
        if (sIntance == null) {
            sIntance = new LockerMonitorManager();
        }
        return sIntance;
    }

    /**
     * 初始化监视器
     * */
    private void initMonitor() {
        mAsyncHandlerThread = new HandlerThread("monitor-thread");
        mAsyncHandlerThread.start();
        mTickTimer = new TickTimer(mAsyncHandlerThread.getLooper(), 300);
        mFrontAppMonitor = new FrontAppMonitor(TheApplication.getAppContext());
        mTickTimer.addListener(mFrontAppMonitor);
        if (AppUtils.isScreenOn(TheApplication.getAppContext())) {
            mTickTimer.start();
            Logger.w("monitor", "start!");
        }
    }

    /**
     * 开启监视器
     * */
    public void startMonitor() {
        if (mAsyncHandlerThread == null) {
            initMonitor();
        } else if (mTickTimer != null) {
            mTickTimer.start();
            Logger.w("monitor", "start!");
        }
    }

    public void stopMonitor() {
        if (mTickTimer != null) {
            mTickTimer.stop();
            Logger.w("monitor", "stop!");
        }
    }
}

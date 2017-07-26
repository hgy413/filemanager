package com.jb.filemanager.function.tip.manager;

import android.os.Environment;
import android.os.StatFs;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.tip.view.StateTipDialog;
import com.jb.filemanager.function.tip.view.TipLayer;
import com.jb.filemanager.home.event.SwitcherChgStateEvent;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.Logger;

/**
 * Created by nieyh on 17-6-30.
 * Usb状态监听
 */

public class StorageTipManager {

    private final String TAG = "StorageTipManager";

    private static StorageTipManager sInstance;
    //每十分钟检测一次
    private final long INTERVAL_TIME = 10 * 60 * 1000;
    //比率阈值
    private final float RATE_THRESHOLD = 0.1f;

    private StorageTipManager() {
    }

    public static StorageTipManager getInstance() {
        if (sInstance == null) {
            sInstance = new StorageTipManager();
        }
        return sInstance;
    }

    /**
     * 提示存储空间不足
     * */
    private Runnable mTipStorageWork = new Runnable() {
        @Override
        public void run() {
            if (checkStorageState()) {
                tryShowTipWindow();
            }
            TheApplication.postRunOnShortTaskThread(this, INTERVAL_TIME);
        }
    };

    /**
     * 尝试展示提示对话框
     */
    private void tryShowTipWindow() {
        StateTipDialog.show(TheApplication.getAppContext(), TipLayer.FREE_SPACE_TIP_LAYER);
    }

    /**
     * 查看开关是否开启
     *
     * @return 开启状态
     */
    public boolean isSwitchEnable() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_LOW_SPACE_WARNING_ENABLE, true);
    }

    /**
     * 改变功能开关 详情：如果之前功能是关闭的，则执行则将开启功能.
     *
     * @return 切换后的状态
     */
    public boolean changerSwitch() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_LOW_SPACE_WARNING_ENABLE, true);
        isEnable = !isEnable;
        if (isEnable) {
            Logger.w(TAG, "开启存储剩余空间监听开关");
            // 开启功能
            startMonitor();
        } else {
            Logger.w(TAG, "关闭存储剩余空间监听开关");
            // 关闭功能
            stopMonitor();
        }
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_LOW_SPACE_WARNING_ENABLE, isEnable);
        TheApplication.getGlobalEventBus().post(SwitcherChgStateEvent.buildFreeSpaceStateChgEvent(isEnable));
        return isEnable;
    }

    /**
     * 准备
     */
    public void toReady() {
        if (isSwitchEnable()) {
            startMonitor();
        }
    }

    /**
     * 注册usb状态监听器
     */
    private void startMonitor() {
        TheApplication.postRunOnShortTaskThread(mTipStorageWork);
    }

    /**
     * 停止usb状态监听
     */
    private void stopMonitor() {
        TheApplication.removeFromShortTaskThread(mTipStorageWork);
    }

    /**
     * 检查存储状态
     * @return
     * <ol>
     *     <li><b>true</b>剩余存储低于10%</li>
     *     <li><b>false</b>剩余存储高于10%</li>
     * </ol>
     * */
    private boolean checkStorageState() {
        StatFs internalStat = new StatFs(Environment.getDataDirectory().getPath());
        StatFs externalStat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long totalSize = APIUtil.getTotalBytes(internalStat);
        long availableSize = APIUtil.getAvailableBytes(internalStat);
        float rate = availableSize / (float)totalSize;
        if (rate < RATE_THRESHOLD) {
            return true;
        }
        totalSize = APIUtil.getTotalBytes(externalStat);
        availableSize = APIUtil.getAvailableBytes(externalStat);

        rate = availableSize / (float)totalSize;
        if (rate < RATE_THRESHOLD) {
            return true;
        }
        return false;
    }
}

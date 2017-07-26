package com.jb.filemanager.function.tip.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.tip.view.StateTipDialog;
import com.jb.filemanager.function.tip.view.TipLayer;
import com.jb.filemanager.home.event.SwitcherChgStateEvent;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.Logger;

/**
 * Created by nieyh on 17-6-30.
 * Usb状态监听
 */

public class UsbStateManager {

    private final String TAG = "UsbStateManager";

    private static UsbStateManager sInstance;

    private boolean isFirstTip;

    private UsbStateManager() {
        isFirstTip = true;
    }

    public static UsbStateManager getInstance() {
        if (sInstance == null) {
            sInstance = new UsbStateManager();
        }
        return sInstance;
    }

    //监听应用安装
    BroadcastReceiver mUsbStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //粘性广播 会持续接收到广告
            if (bundle != null) {
                boolean isConnected = bundle.getBoolean("connected");
                boolean isHostConnected = bundle.getBoolean("host_connected");
                if (isConnected && !isHostConnected) {
                    if (isFirstTip) {
                        isFirstTip = false;
                        return;
                    }
                    //当USB连接上 并且 设备不是充当主设备 (也就是代表着usb连接电脑设备)
                    tryShowTipWindow();
                }
            }
        }
    };

    /**
     * 尝试展示提示对话框
     */
    private void tryShowTipWindow() {
        StateTipDialog.show(TheApplication.getAppContext(), TipLayer.USB_STATE_TIP_LAYER);
    }

    /**
     * 查看开关是否开启
     *
     * @return 开启状态
     */
    public boolean isSwitchEnable() {
        return SharedPreferencesManager.getInstance(TheApplication.getAppContext()).getBoolean(IPreferencesIds.KEY_USB_CONNECTED_TIP_ENABLE, true);
    }

    /**
     * 改变功能开关 详情：如果之前功能是关闭的，则执行则将开启功能.
     *
     * @return 切换后的状态
     */
    public boolean changerSwitch() {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(TheApplication.getAppContext());
        boolean isEnable = sharedPreferencesManager.getBoolean(IPreferencesIds.KEY_USB_CONNECTED_TIP_ENABLE, true);
        isFirstTip = true;
        isEnable = !isEnable;
        if (isEnable) {
            Logger.w(TAG, "开启USB监听开关");
            // 开启功能
            startMonitor();
        } else {
            Logger.w(TAG, "关闭USB监听开关");
            // 关闭功能
            stopMonitor();
        }
        sharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_USB_CONNECTED_TIP_ENABLE, isEnable);
        TheApplication.getGlobalEventBus().post(SwitcherChgStateEvent.buildUsbStateChgEvent(isEnable));
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
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.hardware.usb.action.USB_STATE");
            TheApplication.getAppContext().registerReceiver(mUsbStateReceiver, intentFilter);
        } catch (Exception e) {
        }
    }

    /**
     * 停止usb状态监听
     */
    private void stopMonitor() {
        try {
            TheApplication.getAppContext().unregisterReceiver(mUsbStateReceiver);
        } catch (Exception e) {
        }
    }
}

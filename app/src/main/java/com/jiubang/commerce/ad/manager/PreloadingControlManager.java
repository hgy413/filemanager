package com.jiubang.commerce.ad.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Proxy;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.RootTools;

public class PreloadingControlManager {
    private static PreloadingControlManager sInstance;
    private static int sUSB_PLUGGED_IN = 2;
    private Context mContext;
    private boolean mIsRooted = false;
    private BroadcastReceiver mUsbBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                int unused = PreloadingControlManager.this.mUsbStated = intent.getIntExtra("plugged", 0);
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mUsbStated = 0;

    private PreloadingControlManager(Context context) {
        this.mContext = context;
        initilize();
    }

    public void initilize() {
        this.mIsRooted = RootTools.isRootAvailable();
        this.mContext.registerReceiver(this.mUsbBroadcastReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public static PreloadingControlManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreloadingControlManager(context);
        }
        return sInstance;
    }

    public boolean canPreloading() {
        if (!NetworkUtils.isNetworkOK(this.mContext)) {
            return false;
        }
        if (LogUtils.isShowLog() || !NetworkUtils.isWifiEnable(this.mContext) || (!isUseProxy() && sUSB_PLUGGED_IN != this.mUsbStated && !this.mIsRooted)) {
            return true;
        }
        return false;
    }

    private boolean isUseProxy() {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "isUseProxy(host=" + Proxy.getDefaultHost() + ")");
        }
        return !TextUtils.isEmpty(Proxy.getDefaultHost());
    }

    public static void destroy() {
        try {
            if (sInstance != null && sInstance.mContext != null && sInstance.mUsbBroadcastReceiver != null) {
                sInstance.mContext.unregisterReceiver(sInstance.mUsbBroadcastReceiver);
            }
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
    }
}

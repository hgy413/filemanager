package com.jiubang.commerce.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;
import java.util.List;

public class NetStateMonitor {
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    /* access modifiers changed from: private */
    public static final Object LOCK = new Object();
    private static volatile NetStateMonitor sInstance;
    /* access modifiers changed from: private */
    public static List<INetStatusListener> sListeners;
    private NetBrocastReceiver mBrocastReceiver;
    private Context mContext;

    public interface INetStatusListener {
        void onNetStateChange(boolean z);

        void onWifiStateChange(boolean z);
    }

    public static NetStateMonitor getInstance(Context context) {
        if (sInstance == null) {
            synchronized (NetStateMonitor.class) {
                if (sInstance == null) {
                    sInstance = new NetStateMonitor(context);
                }
            }
        }
        return sInstance;
    }

    public void onDestroy() {
        if (sInstance != null) {
            sInstance.stopBroadcastReceiver();
            this.mContext = null;
        }
    }

    public void registerReceiver() {
        if (this.mBrocastReceiver == null) {
            startBroadcastReceiver();
        }
    }

    public void registerListener(INetStatusListener wifiListener) {
        if (wifiListener != null) {
            registerReceiver();
            synchronized (LOCK) {
                int size = sListeners.size();
                int i = 0;
                while (i < size) {
                    if (sListeners.get(i) != wifiListener) {
                        i++;
                    } else {
                        return;
                    }
                }
                sListeners.add(wifiListener);
            }
        }
    }

    public void unregisterListener(INetStatusListener wifiListener) {
        if (wifiListener != null) {
            synchronized (LOCK) {
                sListeners.remove(wifiListener);
            }
        }
    }

    private NetStateMonitor(Context context) {
        this.mContext = context;
        sListeners = new ArrayList();
    }

    private void startBroadcastReceiver() {
        if (this.mBrocastReceiver == null) {
            this.mBrocastReceiver = new NetBrocastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_CHANGE_ACTION);
        this.mContext.registerReceiver(this.mBrocastReceiver, filter);
    }

    private void stopBroadcastReceiver() {
        if (this.mBrocastReceiver != null) {
            try {
                this.mContext.unregisterReceiver(this.mBrocastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.mBrocastReceiver = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean isWifiEnable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (activeNetInfo == null || !activeNetInfo.isConnected() || activeNetInfo.getType() != 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager;
        NetworkInfo networkInfo;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null || (networkInfo = connectivityManager.getActiveNetworkInfo()) == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static class NetBrocastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            boolean isNetConnected;
            String mAction = intent.getAction();
            synchronized (NetStateMonitor.LOCK) {
                if (NetStateMonitor.CONNECTIVITY_CHANGE_ACTION.equals(mAction)) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    if (networkInfo != null) {
                        isNetConnected = networkInfo.isConnected();
                    } else {
                        isNetConnected = NetStateMonitor.isNetWorkAvailable(context);
                    }
                    boolean isWifi = NetStateMonitor.isWifiEnable(context);
                    List<INetStatusListener> temps = new ArrayList<>();
                    temps.addAll(NetStateMonitor.sListeners);
                    for (INetStatusListener listener : temps) {
                        if (NetStateMonitor.sListeners.contains(listener) && listener != null) {
                            listener.onNetStateChange(isNetConnected);
                            listener.onWifiStateChange(isWifi);
                        }
                    }
                    temps.clear();
                }
            }
        }
    }
}

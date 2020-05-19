package com.jiubang.commerce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetWorkDynamicBroadcastReceiver extends BroadcastReceiver {
    private static List<INetWorkListener> sListenerList;
    private static byte[] sLock = new byte[0];
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    public interface INetWorkListener {
        void onNetworkChanged(boolean z);
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (this.mConnectivityManager == null) {
                this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            }
            this.mNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
            boolean isNetworkOk = this.mNetworkInfo != null && this.mNetworkInfo.isAvailable();
            synchronized (sLock) {
                if (sListenerList != null) {
                    for (INetWorkListener listener : sListenerList) {
                        listener.onNetworkChanged(isNetworkOk);
                    }
                }
            }
        }
    }

    public static void registerListener(INetWorkListener listener) {
        synchronized (sLock) {
            if (sListenerList == null) {
                sListenerList = new CopyOnWriteArrayList();
            }
            if (listener != null) {
                sListenerList.add(listener);
            }
        }
    }

    public static void unRegisterListener(INetWorkListener listener) {
        synchronized (sLock) {
            if (listener != null) {
                if (sListenerList != null) {
                    sListenerList.remove(listener);
                }
            }
        }
    }
}

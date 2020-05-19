package com.gau.utils.net.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import org.apache.http.HttpHost;

public class NetUtil {
    public static final int NETWORKTYPE_CMNET = 3;
    public static final int NETWORKTYPE_CMWAP = 2;
    public static final int NETWORKTYPE_NULL = -1;
    public static final int NETWORKTYPE_WIFI = 1;

    public static boolean isPerferNetWorkCanUse(Context context) {
        return isPerferNetWorkCanUse((ConnectivityManager) context.getSystemService("connectivity"));
    }

    private static boolean isPerferNetWorkCanUse(ConnectivityManager connectivity) {
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null || (info.getState() != NetworkInfo.State.CONNECTING && info.getState() != NetworkInfo.State.CONNECTED)) {
            return false;
        }
        return true;
    }

    public static boolean isDefineProxy(Context context) {
        if (!isPerferNetWorkCanUse((ConnectivityManager) context.getSystemService("connectivity"))) {
            return false;
        }
        if (Proxy.getDefaultHost() == null && Proxy.getHost(context) == null) {
            return false;
        }
        return true;
    }

    public static boolean isCanUse(NetworkInfo network) {
        if (network == null || (network.getState() != NetworkInfo.State.CONNECTING && network.getState() != NetworkInfo.State.CONNECTED)) {
            return false;
        }
        return true;
    }

    public static HttpHost getProxy(Context context) {
        String proxy = Proxy.getHost(context);
        if (proxy == null) {
            proxy = Proxy.getDefaultHost();
        }
        return new HttpHost(proxy, Proxy.getPort(context));
    }

    public static int getNetWorkType(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null || (info.getState() != NetworkInfo.State.CONNECTING && info.getState() != NetworkInfo.State.CONNECTED)) {
            return -1;
        }
        if (info.getType() == 1) {
            return 1;
        }
        if (info.getType() != 0) {
            return -1;
        }
        if (Proxy.getDefaultHost() == null && Proxy.getHost(context) == null) {
            return 3;
        }
        return 2;
    }
}

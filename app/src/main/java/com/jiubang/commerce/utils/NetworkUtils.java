package com.jiubang.commerce.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import com.jb.ga0.commerce.util.LogUtils;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

public class NetworkUtils {
    private static final String LOG_TAG = "appcenter_network";
    public static final int NETWORK_TYPE_2G = 2;
    public static final int NETWORK_TYPE_3G = 3;
    public static final int NETWORK_TYPE_4G = 4;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_OTHER = 5;
    public static final int NETWORK_TYPE_UNKOWN = 0;
    public static final int NETWORK_TYPE_WIFI = 1;
    public static final int TYPE_BLUETOOTH = 7;
    public static final int TYPE_DUMMY = 8;
    public static final int TYPE_ETHERNET = 9;
    public static final int TYPE_MOBILE_DUN = 4;
    public static final int TYPE_MOBILE_HIPRI = 5;
    public static final int TYPE_MOBILE_MMS = 2;
    public static final int TYPE_MOBILE_SUPL = 3;
    public static final int TYPE_WIMAX = 6;

    public static boolean isNetworkOK(Context context) {
        NetworkInfo networkInfo;
        if (context == null) {
            return false;
        }
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            if (cm == null || (networkInfo = cm.getActiveNetworkInfo()) == null || !networkInfo.isConnected()) {
                return false;
            }
            return true;
        } catch (NoSuchFieldError e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public static int getNetworkType(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null) {
            return 0;
        }
        if (LogUtils.isShowLog()) {
            LogUtils.d(LOG_TAG, "网络类型：" + info.getType());
        }
        switch (info.getType()) {
            case 0:
                if (LogUtils.isShowLog()) {
                    LogUtils.d(LOG_TAG, "手机网制类型：" + info.getSubtype());
                }
                switch (info.getSubtype()) {
                    case 0:
                        return 0;
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                        return 2;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 14:
                    case 15:
                        return 3;
                    case 13:
                        return 4;
                    default:
                        return 0;
                }
            case 1:
                return 1;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return 5;
            default:
                return 0;
        }
    }

    public static String buildNetworkState(Context context) {
        int networkType = getNetworkType(context);
        if (networkType == 0) {
            return "unknown";
        }
        if (networkType == 1) {
            return "wifi";
        }
        if (networkType == 2) {
            return "2g";
        }
        if (networkType == 3) {
            return "3g";
        }
        if (networkType == 4) {
            return "4g";
        }
        if (networkType == 5) {
            return "other";
        }
        return "unknown";
    }

    public static boolean isVpnConnected() {
        if (Build.VERSION.SDK_INT >= 9) {
            try {
                Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
                if (niList != null) {
                    Iterator i$ = Collections.list(niList).iterator();
                    while (i$.hasNext()) {
                        NetworkInterface intf = (NetworkInterface) i$.next();
                        if (intf.isUp() && intf.getInterfaceAddresses().size() != 0) {
                            if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

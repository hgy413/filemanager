package com.jb.filemanager.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bill wang on 2017/6/21.
 *
 */
@SuppressWarnings("unused")
public class NetworkUtil {

    public static final String NOMAC = "00:00:00:00:00:00";

    // Copy from TelephonyManager.java source
    /**
     * Current network is GSM
     */
    public static final int NETWORK_TYPE_GSM = 16;
    /**
     * Current network is TD_SCDMA
     */
    public static final int NETWORK_TYPE_TD_SCDMA = 17;
    /**
     * Current network is IWLAN
     */
    public static final int NETWORK_TYPE_IWLAN = 18;

    /**
     * Unknown network class.
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    public static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    public static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    public static final int NETWORK_CLASS_4_G = 3;

    public static final String MOBILE_2G_CLASS_NAME = "2G";
    public static final String MOBILE_3G_CLASS_NAME = "3G";
    public static final String MOBILE_4G_CLASS_NAME = "4G";

    //信号强度的等级
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;


    /**
     * Return general class of network type, such as "3G" or "4G". In cases
     * where classification is contentious, this method is conservative.
     */
    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case NETWORK_TYPE_IWLAN:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getNetworkClassName(int networkType) {
        int networkClass = NetworkUtil.getNetworkClass(networkType);
        String networkClassString;
        switch (networkClass) {
            case NetworkUtil.NETWORK_CLASS_2_G:
                networkClassString = MOBILE_2G_CLASS_NAME;
                break;
            case NetworkUtil.NETWORK_CLASS_3_G:
                networkClassString = MOBILE_3G_CLASS_NAME;
                break;
            case NetworkUtil.NETWORK_CLASS_4_G:
                networkClassString = MOBILE_4G_CLASS_NAME;
                break;
            default:
                networkClassString = "";
                break;
        }
        return networkClassString;
    }

    /**
     * 检查当前网络状态是否可用
     */
    public static boolean isNetworkOK(Context mContext) {
        boolean result = false;
        if (mContext != null) {
            ConnectivityManager cm = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                android.net.NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 判断当前网络是否为WiFi
     *
     * @param context context
     * @return result
     */
    public static boolean isWiFiNetWork(Context context) {
        context = context.getApplicationContext();
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null && info.isConnected() && (info.getType() == ConnectivityManager.TYPE_WIFI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前网络是否是Mobile 或者 wifi网络
     * isNetworkOK 方法在蓝牙联通时可能也会返回true
     * */
    public static boolean isWifiOrMobileConnect(Context context) {
        if (context != null) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null && info.isConnected() && (info.getType() == ConnectivityManager.TYPE_WIFI || (info.getType() == ConnectivityManager.TYPE_MOBILE))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在arp攻击
     *
     * @return result
     * @author xiaoyu
     */
    public static boolean isWiFiArpAttacked(Context context) {
        // 1. 获取BSSID，也就是网关的Mac（mac1）
        // 2. 通过route表信息获取全网段路由对应的网关IP
        // 3. 通过arp表，查询此网关IP对应Mac(mac2)
        boolean isWiFiNetWork = isWiFiNetWork(context);
        if (isWiFiNetWork) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            String bssid = connectionInfo.getBSSID(); // 获取网关的MAC地址

            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo != null) {
                int gateway = dhcpInfo.gateway;
                String gateWayIP = Formatter.formatIpAddress(gateway); // 网关的IP地址
                String macAddress = getMacAddress(gateWayIP); // 记录的网关的MAC地址
                if (null != bssid && bssid.equals(macAddress)) {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 判断SSL证书是否安全, 即判断当前时间是否在证书的有效期内<br>
     * 避免联网产生的不确定性, 在此只检查有限时间
     *
     * @param context context
     * @return result
     */
    public static boolean isSSLSafe(Context context) {
        long startTime = System.currentTimeMillis();
        File certDir = new File("/system/etc/security/cacerts/");
        long currLong = new Date().getTime();
        File[] files = certDir.listFiles();
        for (File file : files) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("Not After")) {
                        String notAfterDate = line.split(":", 2)[1];
                        long longCertDate = TimeUtil.getLongCertDate(notAfterDate);
                        if (longCertDate < currLong) { // 结束时间早于当前时间
                            // 解决方案待确定, 暂时不做处理
                            // Log.e("NetworkUtil", "结束时间风险 : " + new Date(longCertDate).toLocaleString());
                            // return  false;
                        }
                    }
                    if (line.contains("Not Before")) {
                        String notBeforeDate = line.split(":", 2)[1];
                        long longCertDate = TimeUtil.getLongCertDate(notBeforeDate);
                        if (longCertDate > currLong) { // 开始时间早于当前时间
                            // 解决方案待确定, 暂时不做处理
                            // Log.e("NetworkUtil", "开始时间风险 : " + new Date(longCertDate).toLocaleString());
                            // return false;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        long endTime = System.currentTimeMillis();
        Logger.d("currentTimeMillis",endTime-startTime+"");
        return true;
    }

    public static int getSignalStrengthLevel(SignalStrength signalStrength, String[] parts) {
        int level;
        if (parts == null||parts.length < 12) {
            //如果位数不够的话  就按照gsm网络处理
            level = getDefaultSingleStrength(signalStrength.getGsmSignalStrength());
            //直接返回
            return level;
        }
        //判断是gsm网络
        if (signalStrength.isGsm()) {
            level = getLeteLevel(signalStrength,parts);
            if (level == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                //如果数据异常   那么使用普通的gsm数据
                level = getDefaultSingleStrength(signalStrength.getGsmSignalStrength());
            }
        } else {
            //比较CDMA和evdo的数据
            int cdmaLevel = getCdmaLevel(signalStrength);
            int evdoLevel = getEvdoLevel(signalStrength);
            if (evdoLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                /* We don't know evdo, use cdma */
                level = cdmaLevel;
            } else if (cdmaLevel == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                /* We don't know cdma, use evdo */
                level = evdoLevel;
            } else {
                /* We know both, use the lowest level */
                level = cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
            }
        }
        return level;
    }

    private static int getDefaultSingleStrength(int asu) {
        int level;
        if (asu <= 2 || asu == 99) level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        else if (asu >= 12) level = SIGNAL_STRENGTH_GREAT;
        else if (asu >= 8) level = SIGNAL_STRENGTH_GOOD;
        else if (asu >= 5) level = SIGNAL_STRENGTH_MODERATE;
        else level = SIGNAL_STRENGTH_POOR;
        return level;
    }

    private static int getEvdoLevel(SignalStrength signalStrength){
        int level;
        try {
            long evdoDbm =  signalStrength.getEvdoDbm();
            long evdoSnr = signalStrength.getEvdoSnr();
            int levelEvdoDbm;
            int levelEvdoSnr;

            if (evdoDbm >= -65) levelEvdoDbm = SIGNAL_STRENGTH_GREAT;
            else if (evdoDbm >= -75) levelEvdoDbm = SIGNAL_STRENGTH_GOOD;
            else if (evdoDbm >= -90) levelEvdoDbm = SIGNAL_STRENGTH_MODERATE;
            else if (evdoDbm >= -105) levelEvdoDbm = SIGNAL_STRENGTH_POOR;
            else levelEvdoDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

            if (evdoSnr >= 7) levelEvdoSnr = SIGNAL_STRENGTH_GREAT;
            else if (evdoSnr >= 5) levelEvdoSnr = SIGNAL_STRENGTH_GOOD;
            else if (evdoSnr >= 3) levelEvdoSnr = SIGNAL_STRENGTH_MODERATE;
            else if (evdoSnr >= 1) levelEvdoSnr = SIGNAL_STRENGTH_POOR;
            else levelEvdoSnr = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

            level = (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
        } catch (NumberFormatException e) {
//            Toast.makeText(TheApplication.getAppContext(), "解析出错了", Toast.LENGTH_SHORT).show();
            level = getDefaultSingleStrength(signalStrength.getGsmSignalStrength());
        }
        return level;
    }

    private static int getLeteLevel(SignalStrength signalStrength, String[] parts){
        int level = 0;
        int  rsrpIconLevel = -1, snrIconLevel = -1;

        int rsrpThreshType = 0;
        int[] threshRsrp;
        if (rsrpThreshType == 0) {
            threshRsrp = new int[] {-140, -115, -105, -95, -85, -44};
        } else {
            threshRsrp = new int[] {-140, -128, -118, -108, -98, -44};
        }
        try {
            long mLteRsrp = Long.parseLong(parts[9]);

            if ((mLteRsrp) > threshRsrp[5]) rsrpIconLevel = -1;
            else if (mLteRsrp >= threshRsrp[4]) rsrpIconLevel = SIGNAL_STRENGTH_GREAT;
            else if (mLteRsrp >= threshRsrp[3]) rsrpIconLevel = SIGNAL_STRENGTH_GOOD;
            else if (mLteRsrp >= threshRsrp[2]) rsrpIconLevel = SIGNAL_STRENGTH_MODERATE;
            else if (mLteRsrp >= threshRsrp[1]) rsrpIconLevel = SIGNAL_STRENGTH_POOR;
            else if (mLteRsrp >= threshRsrp[0])
                rsrpIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

                /*
                int mLteRssnr = Integer.parseInt(parts[11]);*/
            long mLteRssnr = Long.parseLong(parts[11]);
            if (mLteRssnr > 300) snrIconLevel = -1;
            else if (mLteRssnr >= 130) snrIconLevel = SIGNAL_STRENGTH_GREAT;
            else if (mLteRssnr >= 45) snrIconLevel = SIGNAL_STRENGTH_GOOD;
            else if (mLteRssnr >= 10) snrIconLevel = SIGNAL_STRENGTH_MODERATE;
            else if (mLteRssnr >= -30) snrIconLevel = SIGNAL_STRENGTH_POOR;
            else if (mLteRssnr >= -200)
                snrIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

        /* Choose a measurement type to use for notification */

            /*
             * The number of bars displayed shall be the smaller of the bars
             * associated with LTE RSRP and the bars associated with the LTE
             * RS_SNR
             */
            if (snrIconLevel != -1 && rsrpIconLevel != -1) {
                return (rsrpIconLevel < snrIconLevel ? rsrpIconLevel : snrIconLevel);
            }

            if (snrIconLevel != -1) return snrIconLevel;

            if (rsrpIconLevel != -1) return rsrpIconLevel;

                /* Valid values are (0-63, 99) as defined in TS 36.331 */
            long mLteSignalStrength = Long.parseLong(parts[8]);
            if (mLteSignalStrength > 63) level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            else if (mLteSignalStrength >= 12) level = SIGNAL_STRENGTH_GREAT;
            else if (mLteSignalStrength >= 8) level = SIGNAL_STRENGTH_GOOD;
            else if (mLteSignalStrength >= 5) level = SIGNAL_STRENGTH_MODERATE;
            else if (mLteSignalStrength >= 0) level = SIGNAL_STRENGTH_POOR;
            if (level == SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                int asu = signalStrength.getGsmSignalStrength();
                level = getDefaultSingleStrength(asu);
            }
        } catch (NumberFormatException e) {
//            Toast.makeText(TheApplication.getAppContext(), "解析出错了", Toast.LENGTH_SHORT).show();
            level = getDefaultSingleStrength(signalStrength.getGsmSignalStrength());
        }
        return level;
    }

    private static int getCdmaLevel(SignalStrength signalStrength){
        int level;
        try {
            int cdmaDbm = signalStrength.getCdmaDbm();
            int cdmaEcio = signalStrength.getCdmaEcio();
            int levelDbm;
            int levelEcio;

            if (cdmaDbm >= -75) levelDbm = SIGNAL_STRENGTH_GREAT;
            else if (cdmaDbm >= -85) levelDbm = SIGNAL_STRENGTH_GOOD;
            else if (cdmaDbm >= -95) levelDbm = SIGNAL_STRENGTH_MODERATE;
            else if (cdmaDbm >= -100) levelDbm = SIGNAL_STRENGTH_POOR;
            else levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

            // Ec/Io are in dB*10
            if (cdmaEcio >= -90) levelEcio = SIGNAL_STRENGTH_GREAT;
            else if (cdmaEcio >= -110) levelEcio = SIGNAL_STRENGTH_GOOD;
            else if (cdmaEcio >= -130) levelEcio = SIGNAL_STRENGTH_MODERATE;
            else if (cdmaEcio >= -150) levelEcio = SIGNAL_STRENGTH_POOR;
            else levelEcio = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;

            level = (levelDbm < levelEcio) ? levelDbm : levelEcio;
        } catch (NumberFormatException e) {
            level = getDefaultSingleStrength(signalStrength.getGsmSignalStrength());
        }
        return level;
    }

    /**
     * 获取WiFi是否连接
     */
    public static boolean getWifiConnect(Context context) {
        boolean result = false;
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (wifi != null && connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (wifi.isWifiEnabled() && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取网络信息
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    //将十进制整数形式转换成127.0.0.1形式的ip地址
    //无符号右移
    public static String longToIp(long longIp) {
        //直接右移24位
        //将高8位置0，然后右移16位
        //将高16位置0，然后右移8位
        //将高24位置0
        return "" + String.valueOf((longIp >>> 24)) +
                "." +
                String.valueOf((longIp & 0x00FFFFFF) >>> 16) +
                "." +
                String.valueOf((longIp & 0x0000FFFF) >>> 8) +
                "." +
                String.valueOf((longIp & 0x000000FF));
    }

    /**
     * 获取地址 将十进制整数形式转换成1.0.0.127形式的ip地址
     */
    public static String getContraryIP(long i) {
        //每一个 被强转为char(8位) 高24位置0 保留低8位
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    // 将127.0.0.1 形式的IP地址转换成10进制整数，这里没有进行任何错误处理
    public static long ipToLong(String strIP) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    /*
     * 获取Mac地址
     */
    public static String getMacAddress(String ip) {
        String mac_re = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        int buf = 8 * 1024;
        String hw = NOMAC;
        BufferedReader bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(mac_re, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), buf);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            return hw;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hw;
    }

    /*
     * @param capabilities 加密类型描述字符串
     * @param concise 是否获取简明显示
     * @return 获取加密类型字符串
     * */
    public static String getSecurity(String capabilities, boolean concise) {
        int type = 0;
        if (capabilities.contains("WEP")) {
            type = 1;
        } else if (capabilities.contains("PSK")) {
            type = 2 ;
        } else if (capabilities.contains("EAP")) {
            type = 3;
        }
        boolean wpa = capabilities.contains("WPA-PSK");
        boolean wpa2 = capabilities.contains("WPA2-PSK");
        switch(type) {
            case 3:
                return concise ?  "802.1x" : "802.1x EAP";
            case 2:
                if (wpa && wpa2) {
                    return concise ? "WPA/WPA2" : "WPA2";
                } else if (wpa) {
                    return concise ? "WPA" : "WPA PSK";
                } else if (wpa2) {
                    return concise ? "WPA" : "WPA PSK";
                } else {
                    return concise ? "WPA/WPA2" :"WPA/WPA2 PSK";
                }
            case 1:
                return "WEP";
            case 0:
            default:
                return concise ? "" : "None";
        }
    }

}
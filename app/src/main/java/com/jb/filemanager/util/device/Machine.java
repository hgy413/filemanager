package com.jb.filemanager.util.device;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.jb.filemanager.util.BuildProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.FROYO;
import static android.os.Build.VERSION_CODES.GINGERBREAD;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR2;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.KITKAT_WATCH;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * @author jiangxuwen
 */
public class Machine {

    private static final String TAG = "Machine";
    public static int LEPHONE_ICON_SIZE = 72;
    private static boolean sCheckTablet = false;
    private static boolean sIsTablet = false;

    // 硬件加速
    public static int LAYER_TYPE_NONE = 0x00000000;
    public static int LAYER_TYPE_SOFTWARE = 0x00000001;
    public static int LAYER_TYPE_HARDWARE = 0x00000002;

    // 5.1.1 SDK版本号
    public static final int SDK_VERSION_CODE_5_1 = 22;        //5.1和5.1.1都是22
    public static final int SDK_VERSION_CODE_5_1_1 = 22;
    // 6.0
    public static final int SDK_VERSION_CODE_6 = 23;
    //7.0
    public static final int SDK_VERSION_CODE_7 = 24;

    // SDK 版本判断
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    /**
     * SDK >= 8
     */
    public static final boolean HAS_SDK_FROYO = SDK_VERSION >= FROYO;
    /**
     * SDK >= 9
     */
    public static final boolean HAS_SDK_GINGERBREAD = SDK_VERSION >= GINGERBREAD;
    /**
     * SDK >= 11
     */
    public static final boolean HAS_SDK_HONEYCOMB = SDK_VERSION >= HONEYCOMB;
    /**
     * SDK >= 12
     */
    public static final boolean HAS_SDK_HONEYCOMB_MR1 = SDK_VERSION >= HONEYCOMB_MR1;
    /**
     * SDK >= 13
     */
    public static final boolean HAS_SDK_HONEYCOMB_MR2 = SDK_VERSION >= HONEYCOMB_MR2;
    /**
     * SDK >= 14
     */
    public static final boolean HAS_SDK_ICS = SDK_VERSION >= ICE_CREAM_SANDWICH;
    /**
     * SDK >= 15
     */
    public static final boolean HAS_SDK_ICS_15 = SDK_VERSION >= ICE_CREAM_SANDWICH_MR1;
    /**
     * SDK >= 15 && 版本为4.0.4
     */
    public static final boolean HAS_SDK_ICS_MR1 = HAS_SDK_ICS_15
            && Build.VERSION.RELEASE.equals("4.0.4");// HTC oneX 4.0.4系统
    /**
     * SDK >= 16
     */
    public static final boolean HAS_SDK_JELLY_BEAN = SDK_VERSION >= JELLY_BEAN;
    /**
     * SDK >= 17
     */
    public static final boolean HAS_SDK_JELLY_BEAN_MR1 = SDK_VERSION >= JELLY_BEAN_MR1;
    /**
     * SDK >= 18
     */
    public static final boolean HAS_SDK_JELLY_BEAN_MR2 = SDK_VERSION >= JELLY_BEAN_MR2;
    /**
     * SDK >= 19
     */
    public static final boolean HAS_SDK_KITKAT = SDK_VERSION >= KITKAT;
    /**
     * SDK >= 20
     */
    public static final boolean HAS_SDK_KITKAT_WATCH = SDK_VERSION >= KITKAT_WATCH;
    /**
    /**
     * SDK >= 21 android版本是否为5.0以上
     */
    public static final boolean HAS_SDK_LOLLIPOP = SDK_VERSION >= LOLLIPOP;
    /**
     * SDK >= 22 android版本是否为5.1(5.1.1)以上
     */
    public static final boolean HAS_SDK_5_1_1 = SDK_VERSION >= SDK_VERSION_CODE_5_1_1;
    /**
     * SDK >= 23 android版本是否为6.0以上
     */
    public static final boolean HAS_SDK_6 = SDK_VERSION >= SDK_VERSION_CODE_6;

    /**
     * SDK >= 24 android版本是否为7.0以上
     * */
    public static final boolean HAS_SDK_NOUGAT = SDK_VERSION >= SDK_VERSION_CODE_7;

    /**
     * SDK < 11
     */
    public static boolean SDK_UNDER_HONEYCOMB = SDK_VERSION < HONEYCOMB; // 版本小于3.0
    /**
     * SDK < 14
     */
    public static boolean SDK_UNDER_ICS = SDK_VERSION < ICE_CREAM_SANDWICH;
    /**
     * SDK < 16
     */
    public static boolean SDK_UNDER_JELLY_BEAN = SDK_VERSION < JELLY_BEAN;
    /**
     * SDK < 19
     */
    public static boolean SDK_UNDER_KITKAT = SDK_VERSION < KITKAT;
    /**
     * SDK < 20
     */
    public static boolean SDK_UNDER_KITKAT_WATCH = SDK_VERSION < Build.VERSION_CODES.KITKAT_WATCH;
    /**
     * SDK < 21
     */
    public static boolean SDK_UNDER_LOLIP = SDK_VERSION < 21;

    private static Method sAcceleratedMethod = null;

    private final static String LEPHONEMODEL[] = {"3GW100", "3GW101",
            "3GC100", "3GC101"};
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX", "MX2",
            "mx2", "MX3", "mx3", "MX4", "mx4", "mx4pro", "MX4PRO"};
    private final static String M9BOARD[] = {"m9", "M9"};
    private final static String MXBOARD[] = {"mx", "MX"};
    private final static String ONE_X_MODEL[] = {"HTC One X", "HTC One S",
            "HTC Butterfly", "HTC One XL", "htc one xl",
            "HTC Droid Incredible 4G LTE", "HTC 802w"};
    private final static String[] F100 = {"I_SKT"};
    private static final String[] C8816 = {"C8816"};
    private static final String XIAOMI_UI = "miui";
    private final static String SONY_T2[] = {"XM50h"};
    private final static String SONYC2305[] = {"arima89_we_s_jb2"};
    private final static String[] HUAWEI_X1 = {"MediaPad X1 7.0"};
    private final static String[] HUAWEI_G610 = {"G610-U00"};
    private final static String[] HUAWEI_H30_L01 = {"BalongV9R1"};
    private final static String[] HUAWEI_MT7 = {"MT7-CL00"};
    private final static String LG_G3 = "APQ8084";
    // 红米2
    private final static String[] HONGMI_2 = {"2014811"};
    // imei
    public static final String DEFAULT_RANDOM_DEVICE_ID = "0000000000000000"; // 默认随机IMEI
    public static final String RANDOM_DEVICE_ID = "random_device_id"; // IMEI存入sharedPreference中的key
    public static final String SHAREDPREFERENCES_RANDOM_DEVICE_ID = "randomdeviceid"; // 保存IMEI的sharedPreference文件名
    private final static String KITKAT_WITHOUT_NAVBAR[] = {"xt1030", "HUAWEI MT2-L01", "HUAWEI P7-L00", "H60-L01"}; // 部分不想透明操作栏的手机机型

    // 国产四天王：ZTE，GIONEE，MX，vivo的牌子
    private final static String BRADN_ZTE = "ZTE";
    private final static String BRADN_GIONEE = "GiONEE";
    private final static String BRADN_VIVO = "vivo";
    // HTC牌子
    private final static String BRADN_HTC = "htc";
    // alps牌子
    private final static String BRADN_ALPS = "alps";

    private static boolean sSupportGLES20 = false;
    private static boolean sDetectedDevice = false;

    // 用于判断设备是否支持绑定widget
    private static boolean sSupportBindWidget = false;
    // 是否已经进行过绑定widget的判断
    private static boolean sDetectedBindWidget = false;

    public final static String[] S5360_MODEL = {"GT-S5360"};

    public static boolean sDetectedSupportAPITransparentStatusBar;
    public static boolean sIsSupportAPITransparentStatusBar;

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";

    public static boolean isLephone() {
        final String model = Build.MODEL;
        if (model == null) {
            return false;
        }
        final int size = LEPHONEMODEL.length;
        for (int i = 0; i < size; i++) {
            if (model.equals(LEPHONEMODEL[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isM9() {
        return isPhone(M9BOARD);
    }

    public static boolean isMX() {
        return isPhone(MXBOARD);
    }

    public static boolean isF100() {
        return isPhone(F100);
    }

    public static boolean isMeizu() {
        return isPhone(MEIZUBOARD);
    }

    public static boolean isONE_X() {
        return isModel(ONE_X_MODEL);
    }

    public static boolean isC8816() {
        return isPhone(C8816);
    }

    public static boolean isSONYC2305() {
        return isPhone(SONYC2305);
    }

    public static boolean isHONGMI_2() {
        return isModel(HONGMI_2);
    }

    public static boolean isLGG3() {
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        return LG_G3.equals(board);
    }

    private static boolean isPhone(String[] boards) {
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        final int size = boards.length;
        for (int i = 0; i < size; i++) {
            if (board.equals(boards[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isModel(String[] models) {
        final String board = Build.MODEL;
        if (board == null) {
            return false;
        }
        final int size = models.length;
        try {
            for (int i = 0; i < size; i++) {
                if (board.equals(models[i])
                        || board.equals(models[i].toLowerCase())
                        || board.equals(models[i].toUpperCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 因为主题2.0新起进程，无法获取GoLauncher.getContext()， 所以重载此方法，以便主题2.0调用
     *
     * @param context
     * @return
     */
    public static boolean isCnUser(Context context) {
        boolean result = false;

        if (context != null) {
            // 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            // SIM卡状态
            boolean simCardUnable = manager.getSimState() != TelephonyManager.SIM_STATE_READY;
            String simOperator = manager.getSimOperator();

            if (simCardUnable || TextUtils.isEmpty(simOperator)) {
                // 如果没有SIM卡的话simOperator为null，然后获取本地信息进行判断处理
                // 获取当前国家或地区，如果当前手机设置为简体中文-中国，则使用此方法返回CN
                String curCountry = Locale.getDefault().getCountry();
                result = (curCountry != null && curCountry.contains("CN"));
                /*
                if (curCountry != null && curCountry.contains("CN")) {
                    // 如果获取的国家信息是CN，则返回TRUE
                    result = true;
                } else {
                    // 如果获取不到国家信息，或者国家信息不是CN
                    result = false;
                }*/
            } else if (simOperator.startsWith("460")) {
                // 如果有SIM卡，并且获取到simOperator信息。
                /**
                 * 中国大陆的前5位是(46000) 中国移动：46000、46002 中国联通：46001 中国电信：46003
                 */
                result = true;
            }
        }

        return result;
    }

    private static String sUserLocation = null;

    /**
     * @param context
     * @return
     */
    public static String getUserLocation(Context context) {
        String result = getLocation(context);

        if (result.contains("CN") || result.contains("cn")) {
            result = "cn";
        } else if (result.contains("IN") || result.contains("in")) {
            result = "in";
        } else if (result.contains("US") || result.contains("us")) {
            result = "us";
        } else if (result.contains("PH") || result.contains("ph")) {
            result = "ph";
        } else if (result.contains("ID") || result.contains("id")) {
            result = "id";
        } else if (result.contains("BR") || result.contains("br")) {
            result = "br";
        } else if (result.contains("RU") || result.contains("ru")) {
            result = "ru";
        } else if (result.contains("MX") || result.contains("mx")) {
            result = "mx";
        } else if (result.contains("TR") || result.contains("tr")) {
            result = "tr";
        } else if (result.contains("IR") || result.contains("ir")) {
            result = "ir";
        } else if (result.contains("MY") || result.contains("my")) {
            result = "my";
        } else if (result.contains("PK") || result.contains("pk")) {
            result = "pk";
        } else if (result.contains("EG") || result.contains("eg")) {
            result = "eg";
        } else if (result.equalsIgnoreCase("es-AR")
                || result.equalsIgnoreCase("ar")) {
            result = "ar";
        } else if (result.equalsIgnoreCase("es")) {
            result = "es";
        } else if (result.contains("MA") || result.contains("ma")) {
            result = "ma";
        } else if (result.contains("th") || result.contains("TH")) {
            result = "th";
        } else if (result.contains("GB") || result.contains("gb")) {
            result = "gb";
        } else if (result.contains("ro") || result.contains("RO")) {
            result = "ro";
        } else if (result.contains("ng") || result.contains("NG")) {
            result = "ng";
        } else if (result.contains("bd") || result.contains("BD")) {
            result = "bd";
        } else {
            result = "cn";
        }

        return result;
    }

    /**
     * @param context
     * @return
     */
    public static String getLocation(Context context) {
        if (sUserLocation != null) {
            return sUserLocation;
        }
        String result = null;

        if (context != null) {
            // 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
            TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            // SIM卡状态
            if (manager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                String simOperator = manager.getSimOperator();
                String simCountry = manager.getSimCountryIso();
                if (!TextUtils.isEmpty(simOperator)) {
                    // 如果有SIM卡，并且获取到simOperator信息。
                    if (simOperator.startsWith("404")
                            || simOperator.startsWith("405")) {
                        result = "in";
                    } else if (simOperator.startsWith("310")) {
                        result = "us";
                    } else if (simOperator.startsWith("4600")) {
                        result = "cn";
                    } else if (simOperator.startsWith("515")) {
                        result = "ph";
                    } else if (simOperator.startsWith("510")) {
                        result = "id";
                    } else if (simOperator.startsWith("724")) {
                        result = "br";
                    } else if (simOperator.startsWith("250")) {
                        result = "ru";
                    } else if (simOperator.startsWith("334")) {
                        result = "mx";
                    } else if (simOperator.startsWith("286")) {
                        result = "tr";
                    } else if (simOperator.startsWith("432")) {
                        result = "ir";
                    } else if (simOperator.startsWith("502")) {
                        result = "my";
                    } else if (simOperator.startsWith("410")) {
                        result = "pk";
                    } else if (simOperator.startsWith("602")) {
                        result = "eg";
                    } else if (simOperator.startsWith("470")) {
                        result = "bd";
                    } else if (simOperator.startsWith("722")) {
                        result = "ar";
                    } else if (simOperator.startsWith("214")) {
                        result = "es";
                    } else if (simOperator.startsWith("604")) {
                        result = "ma";
                    } else if (simOperator.startsWith("520")) {
                        result = "th";
                    } else if (simOperator.startsWith("621")) {
                        result = "ng";
                    } else if (simOperator.startsWith("234")) {
                        result = "gb";
                    } else if (simOperator.startsWith("226")) {
                        result = "ro";
                    }
                }

                // 获取不到simOperator信息或不在特殊处理的列表内
                if (TextUtils.isEmpty(result) && !TextUtils.isEmpty(simCountry)) {
                    result = simCountry;
                }
            }

            // 从sim卡取不到国家信息
            if (TextUtils.isEmpty(result)) {
                String curCountry = Locale.getDefault().getCountry();
                if (!TextUtils.isEmpty(curCountry)) {
                    result = curCountry;
                }
            }
        }
        if (TextUtils.isEmpty(result)) {
            return "unknow";
        }
        sUserLocation = result;
        return result;
    }

    // 根据系统版本号判断时候为华为2.2 or 2.2.1, Y 则catch
    public static boolean isHuawei() {
        boolean resault = false;
        String androidVersion = Build.VERSION.RELEASE;// os版本号
        String brand = Build.BRAND;// 商标
        if (androidVersion == null || brand == null) {
            return resault;
        }
        if (brand.equalsIgnoreCase("Huawei")) {
            resault = true;
        }
        return resault;
    }

    public static boolean isHuaweiHONORH30_L01() {
        return isPhone(HUAWEI_H30_L01);
    }

    public static boolean isHuaweiG610() {
        return isPhone(HUAWEI_G610);
    }

    public static boolean isHuaweiMT7() {
        return isPhone(HUAWEI_MT7);
    }

    public static boolean isSamsung() {
        boolean result = false;
        String androidVersion = Build.VERSION.RELEASE;// os版本号
        String brand = Build.BRAND;// 商标
        if (androidVersion == null || brand == null) {
            return result;
        }
        if (brand.equalsIgnoreCase("samsung")) {
            result = true;
        }
        if (Build.MANUFACTURER != null
                && Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            result = true;
        }
        return result;
    }

    // 判断当前设备是否为平板
    private static boolean isPad(Context context) {
        // if (DrawUtils.sDensity >= 1.5 || DrawUtils.sDensity <= 0) {
        // return false;
        // }
        // if (DrawUtils.sWidthPixels < DrawUtils.sHeightPixels) {
        // if (DrawUtils.sWidthPixels > 480 && DrawUtils.sHeightPixels > 800) {
        // return true;
        // }
        // } else {
        // if (DrawUtils.sWidthPixels > 800 && DrawUtils.sHeightPixels > 480) {
        // return true;
        // }
        // }
        // return false;
        if (isModel(SONY_T2) || isModel(HUAWEI_X1)) {
            // 机型过滤，这些手机并不是平板
            return false;
        }
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isTablet(Context context) {
        if (sCheckTablet) {
            return sIsTablet;
        }
        sCheckTablet = true;
        sIsTablet = isPad(context);
        return sIsTablet;
    }

    /**
     * 判断当前网络是否可以使用
     *
     * @param context
     * @return
     * @author huyong
     */
    public static boolean isNetworkOK(Context context) {
        boolean result = false;
        if (context != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        result = true;
                    }
                }
            } catch (NoSuchFieldError e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 设置硬件加速
     *
     * @param view
     * @param mode
     */
    public static void setHardwareAccelerated(View view, int mode) {
        if (SDK_UNDER_HONEYCOMB) {
            return;
        }
        try {
            if (null == sAcceleratedMethod) {
                sAcceleratedMethod = View.class.getMethod("setLayerType",
                        new Class[]{Integer.TYPE, Paint.class});
            }
            sAcceleratedMethod.invoke(view,
                    new Object[]{Integer.valueOf(mode), null});
        } catch (Throwable e) {
            SDK_UNDER_HONEYCOMB = true;
        }
    }

    public static boolean isIceCreamSandwichOrHigherSdk() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 获取Android中的Linux内核版本号
     */
    public static String getLinuxKernel() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == process) {
            return null;
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);
        String result = "";
        String line;

        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.equals("")) {
            String Keyword = "version ";
            int index = result.indexOf(Keyword);
            line = result.substring(index + Keyword.length());
            if (null != line) {
                index = line.indexOf(" ");
                return line.substring(0, index);
            }
        }
        return null;
    }

    /**
     * 判断应用软件是否运行在前台
     *
     * @param context
     * @param packageName 应用软件的包名
     * @return
     */
    public static boolean isTopActivity(Context context, String packageName) {
        return isTopActivity(context, packageName, null);
    }


    /**
     * <br>
     * 功能简述: 获取真实的imei号。 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param context     判断某一Activity是否运行在前台
     * @param context
     * @param packageName 应用软件的包名
     * @return
     */
    public static boolean isTopActivity(Context context, String packageName,
                                        String className) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager
                    .getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // Activity位于堆栈的顶层,如果Activity的类为空则判断的是当前应用是否在前台
                if (packageName.equals(tasksInfo.get(0).topActivity
                        .getPackageName())
                        && (className == null || className.equals(tasksInfo
                        .get(0).topActivity.getClassName()))) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * <br>
     * 功能简述:获取Android ID的方法 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = null;
        if (context != null) {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }

    /**
     * 获取本地mac地址
     *
     * @return mac address
     */
    public static String getLocalMacAddress(Context context) {
        String result = null;
        if (context != null) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            result = info.getMacAddress();
        }
        return result;
    }

    /**
     * 获取国家
     *
     * @param context
     * @return
     */
    public static String getCountry(Context context) {
        String ret = null;

        try {
            TelephonyManager telManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toLowerCase();
            }
        } catch (Throwable e) {
            // e.printStackTrace();
        }
        if (ret == null || ret.equals("")) {
            ret = Locale.getDefault().getCountry().toLowerCase();
        }
        return ret;
    }

    /**
     * 是否支持OpenGL2.0
     *
     * @param context
     * @return
     */
    public static boolean isSupportGLES20(Context context) {
        if (!sDetectedDevice) {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            sSupportGLES20 = info.reqGlEsVersion >= 0x20000;
            sDetectedDevice = true;
        }
        return sSupportGLES20;
    }

    /**
     * 判断是否为韩国用户
     *
     * @return
     */
    public static boolean isKorea(Context context) {
        boolean isKorea = false;
        String country = getCountry(context);
        if (country.equals("kr")) {
            isKorea = true;
        }
        return isKorea;
    }

    public static boolean canHideNavBar() {
        return !isModel(KITKAT_WITHOUT_NAVBAR);
        /*
        if (isModel(KITKAT_WITHOUT_NAVBAR)) {
            return false;
        }
        return true;*/
    }

    public static boolean isSupportBindWidget(Context context) {
        if (!sDetectedBindWidget) {
            sSupportBindWidget = false;
            if (Build.VERSION.SDK_INT >= 16) {
                try {
                    // 在某些设备上，没有支持"android.appwidget.action.APPWIDGET_BIND"的activity
                    Intent intent = new Intent(
                            "android.appwidget.action.APPWIDGET_BIND");
                    PackageManager packageManager = context.getPackageManager();
                    List<ResolveInfo> list = packageManager
                            .queryIntentActivities(intent, 0);
                    if (list == null || list.size() <= 0) {
                        sSupportBindWidget = false;
                    } else {
                        // 假如有支持上述action的activity，还需要判断是否已经进行了授权创建widget
                        AppWidgetManager.class.getMethod(
                                "bindAppWidgetIdIfAllowed", int.class,
                                ComponentName.class);
                        sSupportBindWidget = true;
                    }
                } catch (NoSuchMethodException e) { // 虽然是4.1以上系统，但是不支持绑定权限，仍按列表方式添加系统widget
                    e.printStackTrace();
                }
            }
            sDetectedBindWidget = true;
        }
        return sSupportBindWidget;
    }

    // 判断是不是米UI V5及以上系统
    public static boolean isMIUI() {
        boolean result = false;
        // String manufacturer = android.os.Build.MANUFACTURER;
        String host = Build.HOST;
        // int sdk_int = android.os.Build.VERSION.SDK_INT;

        // 修改判断条件，针对rom而不是机器生产商
        if (HAS_SDK_JELLY_BEAN
                // sdk_int >= android.os.Build.VERSION_CODES.JELLY_BEAN
                // && manufacturer != null && manufacturer.toLowerCase() != null
                && host != null && host.toLowerCase() != null
                // && manufacturer.toLowerCase().equals(XIAOMI)
                && host.toLowerCase().contains(XIAOMI_UI)) {
            result = true;
        }
        return result||isMIUI2();
    }

    /**
     * 是否小米4<br>
     *
     * @return
     */
    public static boolean isMi4() {
        return "MI 4LTE".equalsIgnoreCase(Build.MODEL);
    }

    /**
     * 是否为三星i545<br>
     *
     * @return
     */
    public static boolean isSCHI545() {
        return "SCH-I545".equalsIgnoreCase(Build.MODEL);
    }

    public static String getMiuiVer() {
        return getSystemProperty("ro.miui.ui.version.name");
    }

    public static boolean isSamsungGTI8552() {
        return "GT-I8552".equalsIgnoreCase(Build.MODEL);
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public static void setDefaultLauncher(Context context) {
        if (isMIUI()) {
            String ver = getMiuiVer();
            if (null != ver && ver.equalsIgnoreCase("v5")) {
                return;
            }

            if (null != ver && ver.equalsIgnoreCase("v5")
                    && Build.MODEL.equalsIgnoreCase("MI 2")) {
                return;
            }

            Intent mIntent = new Intent(Intent.ACTION_MAIN);
            // 小米v5 系统,不支持弹出设置默认launcher,跳到显示页面
            mIntent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.Settings$DisplaySettingsActivity"));
            try {
                context.startActivity(mIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } /*
         * else { mIntent.addCategory("android.intent.category.HOME"); }
		 */

    }

    /**
     * 判断MobileData是否处于连接状态 权限：&lt;uses-permission
     * android:name="android.permission.ACCESS_NETWORK_STATE /&gt;
     *
     * @param context Context
     * @return MobileData是否处于已连接状态
     */
    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return false;
            }

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }

            if (!networkInfo.isConnected()) {
                return false;
            }

            if (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 获取当前的网络类型
     *
     * @param context
     * @return
     */
    public static String getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        String netkWorkType = "unknown";
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                netkWorkType = "wifi";
            } else if (type.equalsIgnoreCase("MOBILE")) {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        netkWorkType = "2g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        netkWorkType = "2g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        netkWorkType = "2g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        netkWorkType = "gprs";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netkWorkType = "3g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netkWorkType = "2g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netkWorkType = "4g";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        netkWorkType = "2g";
                        break;
                    default:
                        netkWorkType = "unknown";
                        break;
                }
            }
        }

        return netkWorkType;
    }

    /**
     * 获取正在运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
     */
    public static String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(
                intent, 0);
        if (res == null || res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }

    /**
     * 是否为国产四天王的手机ZTE，GIONEE，MX，vivo
     */
    public static boolean isChinaFourKings() {
        return (isMeizu() || isZteBrand() || isGioneeBrand() || isVivoBrand());
    }

    /**
     * 是否为ZTE
     */
    public static boolean isZteBrand() {
        return isBrand(BRADN_ZTE);
    }

    /**
     * 是否为GIONEE
     */
    public static boolean isGioneeBrand() {
        return isBrand(BRADN_GIONEE);
    }

    /**
     * 是否为vivo
     */
    public static boolean isVivoBrand() {
        return isBrand(BRADN_VIVO);
    }

    /**
     * 是否为htc
     */
    public static boolean isHTCBrand() {
        return isBrand(BRADN_HTC);
    }

    /**
     * 是否为Alps
     */
    public static boolean isAlpsBrand() {
        return isBrand(BRADN_ALPS);
    }

    private static boolean isBrand(String targetBrand) {
        String brand = Build.BRAND;
        return brand.toLowerCase().contains(targetBrand.toLowerCase());
    }

    //sim卡是否可读
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否屏亮
     *
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 获取gmail
     *
     * @param context
     * @return
     */
    public static String getGmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google"); //获取google账户
        Account account = accounts.length > 0 ? accounts[0] : null; //取第一个账户
        return account == null ? null : account.name;
    }

    /**
     * 魅族
     * 是否有 SmartBar
     *
     * @return
     */
    public static boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        // 反射不到Build.hasSmartBar(),则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    public static boolean isMIUI2() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    private static boolean isPropertiesExist(String... keys) {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            for (String key : keys) {
                String str = prop.getProperty(key);
                if (str == null)
                    return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isEMUI() {
        return isPropertiesExist(KEY_EMUI_VERSION_CODE);
    }

    private static final String KEY_EMUI_VERSION_CODE2 = "ro.build.hw_emui_api_level";

    public static boolean isEMUI2() {
        try {
            //BuildProperties 是一个工具类
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_EMUI_VERSION_CODE2, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }
}

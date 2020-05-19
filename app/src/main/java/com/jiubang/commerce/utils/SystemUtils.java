package com.jiubang.commerce.utils;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.preferences.PreferencesManager;
import java.util.Locale;
import java.util.Random;

public class SystemUtils {
    public static final String FILE_SHARE_COMMERCE_AD_PHEAD = "commerce_ad_phead_share";
    public static final boolean IS_JELLY_BEAN = (Build.VERSION.SDK_INT >= 16);
    public static final boolean IS_SDK_ABOVE_GBREAD;
    public static final boolean IS_SDK_ABOVE_ICS;
    public static final boolean IS_SDK_ABOVE_KITKAT;
    public static final boolean IS_SDK_ABOVE_L;
    public static final boolean IS_SDK_ABOVE_M;
    public static final int VERSION_CODES_FROYO = 8;

    static {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5 = true;
        if (Build.VERSION.SDK_INT >= 9) {
            z = true;
        } else {
            z = false;
        }
        IS_SDK_ABOVE_GBREAD = z;
        if (Build.VERSION.SDK_INT >= 14) {
            z2 = true;
        } else {
            z2 = false;
        }
        IS_SDK_ABOVE_ICS = z2;
        if (Build.VERSION.SDK_INT >= 19) {
            z3 = true;
        } else {
            z3 = false;
        }
        IS_SDK_ABOVE_KITKAT = z3;
        if (Build.VERSION.SDK_INT >= 21) {
            z4 = true;
        } else {
            z4 = false;
        }
        IS_SDK_ABOVE_L = z4;
        if (Build.VERSION.SDK_INT < 22) {
            z5 = false;
        }
        IS_SDK_ABOVE_M = z5;
    }

    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        return TextUtils.isEmpty(androidId) ? AdSdkApi.UNABLE_TO_RETRIEVE : androidId;
    }

    public static String getLocal(Context context) {
        String ret = null;
        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService("phone");
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ret)) {
            ret = Locale.getDefault().getCountry().toUpperCase();
        }
        if (TextUtils.isEmpty(ret)) {
            ret = "ZZ";
        }
        return ret == null ? "error" : ret;
    }

    public static String getImsi(Context context) {
        String simOperator = "000";
        if (context != null) {
            try {
                simOperator = ((TelephonyManager) context.getSystemService("phone")).getSimOperator();
            } catch (Throwable th) {
            }
        }
        return TextUtils.isEmpty(simOperator) ? "000" : simOperator;
    }

    public static int getEntranceId() {
        return 1;
    }

    public static String getLanguage(Context context) {
        String ret = null;
        try {
            ret = Locale.getDefault().getLanguage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TextUtils.isEmpty(ret) ? "en" : StringUtils.toLowerCase(ret);
    }

    public static String getDisplay(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width + "*" + dm.heightPixels;
    }

    public static String getDpi(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
        return String.valueOf(dm.densityDpi);
    }

    public static String getVirtualIMEI(Context context) {
        return new Object() {
            private static final String DEFAULT_RANDOM_DEVICE_ID = "0000000000000000";
            private static final String RANDOM_DEVICE_ID = "random_device_id";

            public String getVirtualIMEI(Context context) {
                String deviceidString = getDeviceIdFromSharedpreference(context);
                if (deviceidString != null && deviceidString.equals(DEFAULT_RANDOM_DEVICE_ID)) {
                    deviceidString = getDeviceIdFromSDcard();
                    if (deviceidString == null) {
                        try {
                            long randomDeviceid = SystemClock.elapsedRealtime();
                            Random rand = new Random();
                            long randomLong = rand.nextLong();
                            while (randomLong == Long.MIN_VALUE) {
                                randomLong = rand.nextLong();
                            }
                            deviceidString = String.valueOf(randomDeviceid + Math.abs(randomLong));
                            saveDeviceIdToSDcard(deviceidString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    saveDeviceIdToSharedpreference(context, deviceidString);
                } else if (getDeviceIdFromSDcard() == null) {
                    saveDeviceIdToSDcard(deviceidString);
                }
                return deviceidString;
            }

            private String getDeviceIdFromSDcard() {
                return getStringFromSDcard(SystemUtils.getmDEVICE_ID_SDPATH());
            }

            private String getStringFromSDcard(String filePath) {
                try {
                    if (SDCardUtils.isSDCardAvaiable()) {
                        return new String(FileUtils.readByteFromSDFile(filePath));
                    }
                    return null;
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            }

            private void saveDeviceIdToSDcard(String deviceId) {
                writeToSDCard(deviceId, SystemUtils.getmDEVICE_ID_SDPATH());
            }

            private void saveDeviceIdToSharedpreference(Context context, String deviceId) {
                PreferencesManager pm = new PreferencesManager(context, SystemUtils.FILE_SHARE_COMMERCE_AD_PHEAD, 0);
                pm.putString(RANDOM_DEVICE_ID, deviceId);
                pm.commit();
            }

            private String getDeviceIdFromSharedpreference(Context context) {
                return new PreferencesManager(context, SystemUtils.FILE_SHARE_COMMERCE_AD_PHEAD, 0).getString(RANDOM_DEVICE_ID, DEFAULT_RANDOM_DEVICE_ID);
            }

            private void writeToSDCard(String data, String filePath) {
                if (data != null) {
                    try {
                        if (SDCardUtils.isSDCardAvaiable()) {
                            FileUtils.saveByteToSDFile(data.getBytes(), filePath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.getVirtualIMEI(context);
    }

    static String getmDEVICE_ID_SDPATH() {
        return AdSdkContants.getExternalPath() + "/air/as/statistics/deviceId" + ".txt";
    }
}

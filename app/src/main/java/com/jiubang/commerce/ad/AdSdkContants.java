package com.jiubang.commerce.ad;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;

public class AdSdkContants {
    private static final String ADVERT_CONFIG_PATH = (SDCARD + LAUNCHER_DIR + "/config/");
    private static final String ADVERT_DATA_CACHE_FILE_PATH = (SDCARD + LAUNCHER_DIR + "/advert/cacheFile/");
    private static final String ADVERT_DATA_CACHE_IMAGE_PATH = (SDCARD + LAUNCHER_DIR + "/advert/cacheImage/");
    public static final long AD_CONTROL_DATA_VALID_DURATION = 14400000;
    public static final int APP_TYPE_ALL = 0;
    public static final int APP_TYPE_APP = 2;
    public static final int APP_TYPE_CLEAN_MEMORY = 5;
    public static final int APP_TYPE_GAME = 1;
    public static final int APP_TYPE_INSTALL = 3;
    public static final int APP_TYPE_UNINSTALL = 4;
    public static final boolean CACHE_AD_DATA_ENCRYPT = true;
    private static final String DEBUG_CONFIG_FILEPATH = (SDCARD + LAUNCHER_DIR + "/debug/debug.ini");
    private static final String DIR_TAG = "GoAdSdk";
    public static final String GOMO_AD_CACHE_FILE_NAME_PREFIX = "gomo_ad_";
    public static final long GOMO_AD_VALID_CACHE_DURATION = 1800000;
    public static final String HAS_SHOW_AD_URL_LIST = "hasShowAdUrlList";
    private static final String LAUNCHER_DIR = "/GoAdSdk";
    public static final String ONLINE_AD_CACHE_FILE_NAME_PREFIX = "online_ad_";
    public static final long ONLINE_AD_VALID_CACHE_DURATION = 3600000;
    public static final String PACKAGE_NAME_FACEBOOK = "com.facebook.katana";
    public static final String PACKAGE_NAME_FACEBOOK_LITE = "com.facebook.lite";
    public static final int REQUEST_AD_STATUS_CODE_AD_CONTROL_EMPTY = 20;
    public static final int REQUEST_AD_STATUS_CODE_AD_INFO_LIST_EMPTY = 21;
    public static final int REQUEST_AD_STATUS_CODE_CLIENT_CANCEL = 22;
    public static final int REQUEST_AD_STATUS_CODE_MODULE_OFFLINE = 19;
    public static final int REQUEST_AD_STATUS_CODE_NETWORK_ERROR = 17;
    public static final int REQUEST_AD_STATUS_CODE_REQUEST_ERROR = 18;
    public static final int REQUEST_AD_STATUS_CODE_SUCCESS = 16;
    public static final int REQUEST_AD_STATUS_USERTAG_CODE_NETWORK_ERROR = 17;
    public static final int REQUEST_AD_USERTAG_STATUS_CODE_PARSER_ERROR = 16;
    public static final String SAVE_DATA_TIME = "saveDataTime";
    private static final String SDCARD = Environment.getExternalStorageDirectory().getPath();
    public static final String SYMBOL_DOUBLE_LINE = "||";
    public static final int UPLOAD_FILTER_PACKAGENAME_MAX_NUMBER = 30;
    private static String sADVERT_CONFIG_PATH = null;
    private static String sADVERT_DATA_CACHE_FILE_PATH = null;
    private static String sADVERT_DATA_CACHE_IMAGE_PATH = null;
    private static String sDEBUG_CONFIG_FILEPATH = null;
    private static String sExternalPath = null;
    private static String sOBB_DIR = null;

    public static String getExternalPath() {
        if (sExternalPath == null) {
            sExternalPath = SDCARD;
        }
        return sExternalPath;
    }

    public static String getADVERT_CONFIG_PATH() {
        if (sADVERT_CONFIG_PATH == null) {
            sADVERT_CONFIG_PATH = ADVERT_CONFIG_PATH;
        }
        return sADVERT_CONFIG_PATH;
    }

    public static String getADVERT_DATA_CACHE_FILE_PATH() {
        if (sADVERT_DATA_CACHE_FILE_PATH == null) {
            sADVERT_DATA_CACHE_FILE_PATH = ADVERT_DATA_CACHE_FILE_PATH;
        }
        return sADVERT_DATA_CACHE_FILE_PATH;
    }

    public static String getADVERT_DATA_CACHE_IMAGE_PATH() {
        if (sADVERT_DATA_CACHE_IMAGE_PATH == null) {
            sADVERT_DATA_CACHE_IMAGE_PATH = ADVERT_DATA_CACHE_IMAGE_PATH;
        }
        return sADVERT_DATA_CACHE_IMAGE_PATH;
    }

    public static String getDEBUG_CONFIG_FILEPATH() {
        if (sDEBUG_CONFIG_FILEPATH == null) {
            sDEBUG_CONFIG_FILEPATH = DEBUG_CONFIG_FILEPATH;
        }
        return sDEBUG_CONFIG_FILEPATH;
    }

    public static void initDirs(Context context) {
        String dir;
        if (Build.VERSION.SDK_INT >= 11) {
            String obbDir = getObbDir(context);
            if (TextUtils.isEmpty(obbDir)) {
                dir = SDCARD;
            } else {
                dir = obbDir;
            }
            sExternalPath = dir;
            sADVERT_CONFIG_PATH = dir + LAUNCHER_DIR + "/config/";
            sADVERT_DATA_CACHE_FILE_PATH = dir + LAUNCHER_DIR + "/advert/cacheFile/";
            sADVERT_DATA_CACHE_IMAGE_PATH = dir + LAUNCHER_DIR + "/advert/cacheImage/";
            sDEBUG_CONFIG_FILEPATH = dir + LAUNCHER_DIR + "/debug/debug.ini";
            return;
        }
        sADVERT_CONFIG_PATH = ADVERT_CONFIG_PATH;
        sADVERT_DATA_CACHE_FILE_PATH = ADVERT_DATA_CACHE_FILE_PATH;
        sADVERT_DATA_CACHE_IMAGE_PATH = ADVERT_DATA_CACHE_IMAGE_PATH;
        sDEBUG_CONFIG_FILEPATH = DEBUG_CONFIG_FILEPATH;
    }

    private static String getObbDir(Context context) {
        if (TextUtils.isEmpty(sOBB_DIR) && Build.VERSION.SDK_INT >= 11) {
            File obbFile = null;
            try {
                obbFile = context.getApplicationContext().getObbDir();
            } catch (Throwable thr) {
                thr.printStackTrace();
            }
            sOBB_DIR = obbFile != null ? obbFile.getAbsolutePath() : null;
        }
        return sOBB_DIR;
    }
}

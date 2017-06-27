package com.jb.filemanager;

import android.os.Environment;

import java.io.File;

/**
 * Created by bill wang on 16/8/16.
 * 常量定义处
 */
public class Const {
    public static final String APP_CHANNEL = "200";
    // 机器
    public static final String SDCARD = Environment.getExternalStorageDirectory().getPath();

    // network master
    public static final String PACKAGE_NAME = "com.jb.filemanager";
    public static final String PROCESS_NAME_MAIN = PACKAGE_NAME;
    public static final String PROCESS_NAME_PUSH = PACKAGE_NAME + ":pushservice";

    //其他功能进程
    public static final String PROCESS_NAME_FUNCTION = PACKAGE_NAME + ":function";
    public final static String PROCESS_NAME_INTELLIGENT_PRELOAD_SERVICE = PROCESS_NAME_MAIN + ":com.jiubang.commerce.service.IntelligentPreloadService";
    public final static String PROCESS_NAME_CHARGE_LOCKER = PROCESS_NAME_MAIN + ":com.jiubang.commerce.chargelocker";

    // 程序路径
    public static final String ROOT_FOLDER_NAME = "filemanager";
    public static final String FILE_MANAGER_DIR = SDCARD + File.separator + ROOT_FOLDER_NAME;
    public static final String IMAGE_CACHE_DIR = FILE_MANAGER_DIR + "thumbs";
    public final static String BOOST_DIR = SDCARD + File.separator + ROOT_FOLDER_NAME;

    // 本地图片缓存路径
    public static final String CACHE_DIR = BOOST_DIR + "/cache";
    // 备份路径
    public static final String BACK_UP_PATH_STRING = BOOST_DIR + "/backup/";
    public static final String BACK_UP_PATH = BACK_UP_PATH_STRING;


    // crash 路径
    public static final String LOG_DIR = FILE_MANAGER_DIR + "/log/";

    public static final String GP_PACKAGE = "com.android.vending";
    /**
     * Facebook相关包名
     */
    public static final String PACKAGE_FB = "com.facebook.katana";
    public static final String PACKAGE_FB_LITE = "com.facebook.lite";

    /**
     * 参看AndroidManifest.xml中的配置:<br>
     * <provider
     * android:name="com.gau.go.gostaticsdk.StaticDataContentProvider"
     * android:authorities="com.ace.network.staticsdkprovider" />
     */
    public static final String STATISTICS_SDK_PROVIDER_AUTHORITIES = "com.jb.filemanager.staticsdkprovider";

    // About 跳转
    public static final String ABOUT_GOOGLE_PLAY_M = "market://details?id=com.ace.network";
    public static final String ABOUT_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.ace.network";

    // 广告政策
    public static final String FACEBOOK_AD_CHOICE_URL = "https://m.facebook.com/ads/ad_choices";
    public static final String ADMOB_AD_CONTENT_URL = "http://goappdl.goforandroid.com/soft/promote/com.jb.security.html";

}
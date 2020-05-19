package com.jiubang.commerce.statistics;

import android.content.Context;
import android.text.TextUtils;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.notification.ActivationGuideNotification;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.product.Product;

public class AdSdkOperationStatistic extends BaseSeq105OperationStatistic {
    public static final String ACTIVATION_GUIDE_NOTIFICATION_NT_A000 = "nt_a000";
    public static final String ACTIVATION_GUIDE_NOTIFICATION_NT_F000 = "nt_f000";
    public static final String ACTIVATION_GUIDE_WINDOW_AV_A000 = "av_a000";
    public static final String ACTIVATION_GUIDE_WINDOW_AV_F000 = "av_f000";
    private static final String ADV_DATA_SOURCE_ADMOB = "512";
    private static final String ADV_DATA_SOURCE_BIG_DATA_LOCAL_PRIORITY = "500";
    private static final String ADV_DATA_SOURCE_BIG_DATA_LOCAL_RECURRENCE = "504";
    private static final String ADV_DATA_SOURCE_FACEBOOK = "511";
    private static final String ADV_DATA_SOURCE_GENERAL_ONLINE_DATA = "507";
    private static final String ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_BIG_DATA = "502";
    private static final String ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_QUETTRA = "503";
    private static final String ADV_DATA_SOURCE_INTELLIGENT = "508";
    private static final String ADV_DATA_SOURCE_INTELLIGENT_CLASSIFY = "510";
    private static final String ADV_DATA_SOURCE_LOCAL_CONFIG = "501";
    private static final String ADV_DATA_SOURCE_LOCAL_CONFIG_CN = "509";
    private static final String ADV_DATA_SOURCE_MOBILE_CORE_ONLINE = "513";
    private static final String ADV_DATA_SOURCE_MOBILE_CORE_SDK = "514";
    private static final String ADV_DATA_SOURCE_MOBIVISTA = "506";
    private static final String ADV_DATA_SOURCE_PARR_BOGART = "505";
    public static final String INTERNAL_TABCATEGORY = "sdk_inner_call";
    public static final String MATERIAL_AD_A000 = "offline_ad_source_a000";
    public static final String MATERIAL_AD_F000 = "offline_ad_source_f000";
    public static final String PRODUCT_ID_2324_GAME = "49";
    public static final String PRODUCT_ID_ACE_CLEANER = "123";
    public static final String PRODUCT_ID_ACE_SECURITY = "124";
    public static final String PRODUCT_ID_ACE_SECURITY_PLUS = "139";
    public static final String PRODUCT_ID_ALPHA_SECURITY = "137";
    public static final String PRODUCT_ID_APP_LOCKER = "100";
    public static final String PRODUCT_ID_BLUE_BATTERY = "141";
    public static final String PRODUCT_ID_BUBBLE_FISH = "143";
    public static final String PRODUCT_ID_CONNECT_MEE = "89";
    public static final String PRODUCT_ID_COOL_SMS = "134";
    public static final String PRODUCT_ID_CUCKOO_NEWS = "107";
    public static final String PRODUCT_ID_DOOM_RACING = "140";
    public static final String PRODUCT_ID_DOUBLE_OPEN = "105";
    public static final String PRODUCT_ID_GAME_SHOP = "86";
    public static final String PRODUCT_ID_GOMO_GAME = "116";
    public static final String PRODUCT_ID_GO_BACKUP = "3";
    public static final String PRODUCT_ID_GO_BFLASHLIGHT = "122";
    public static final String PRODUCT_ID_GO_CALLER = "117";
    public static final String PRODUCT_ID_GO_CONTACT = "68";
    public static final String PRODUCT_ID_GO_DARLING = "113";
    public static final String PRODUCT_ID_GO_DOUBLE_OPEN = "126";
    public static final String PRODUCT_ID_GO_KEYBOARD = "56";
    public static final String PRODUCT_ID_GO_KEYBOARD_IOS = "74";
    public static final String PRODUCT_ID_GO_KEYBOARD_PRO = "119";
    public static final String PRODUCT_ID_GO_KEYBOARD_THEME = "1004";
    public static final String PRODUCT_ID_GO_LAUNCHER = "11";
    public static final String PRODUCT_ID_GO_LAUNCHER_LAB = "82";
    public static final String PRODUCT_ID_GO_LAUNCHER_THEME = "5";
    public static final String PRODUCT_ID_GO_LOCKER = "26";
    public static final String PRODUCT_ID_GO_LOCKER_THEME = "1005";
    public static final String PRODUCT_ID_GO_LOCKER_VIP = "27";
    public static final String PRODUCT_ID_GO_MUSIC_PLAYER = "109";
    public static final String PRODUCT_ID_GO_NETWORK_SECURITY = "125";
    public static final String PRODUCT_ID_GO_POWER_MASTER = "8";
    public static final String PRODUCT_ID_GO_POWER_MASTER_PRO = "121";
    public static final String PRODUCT_ID_GO_SECURITY = "106";
    public static final String PRODUCT_ID_GO_SMS = "6";
    public static final String PRODUCT_ID_GO_SMS_THEME = "1003";
    public static final String PRODUCT_ID_GO_TOUCHER = "19";
    public static final String PRODUCT_ID_GO_TRANSFER = "120";
    public static final String PRODUCT_ID_GO_WEATHER = "2";
    public static final String PRODUCT_ID_GO_WEATHER_THEME = "1002";
    public static final String PRODUCT_ID_HI_KEYBOARD = "131";
    public static final String PRODUCT_ID_ILOCKER = "92";
    public static final String PRODUCT_ID_KITTY_PLAY = "39";
    public static final String PRODUCT_ID_KITTY_PLAY_EX = "47";
    public static final String PRODUCT_ID_LETS_CLEAN = "138";
    public static final String PRODUCT_ID_MINI_LAUNCHER = "41";
    public static final String PRODUCT_ID_MINI_LAUNCHER_PRO = "54";
    public static final String PRODUCT_ID_MUSIC_PLAYER_MASTER = "132";
    public static final String PRODUCT_ID_MY_WEATHER_REPORTER = "133";
    public static final String PRODUCT_ID_NEXT_BROWSER = "21";
    public static final String PRODUCT_ID_NEXT_GAME = "59";
    public static final String PRODUCT_ID_NEXT_LAUNCHER = "13";
    public static final String PRODUCT_ID_NEXT_LAUNCHER_THEME = "1006";
    public static final String PRODUCT_ID_ONE_KEY_LOCKER = "104";
    public static final String PRODUCT_ID_POWER_MASTER_PLUS = "127";
    public static final String PRODUCT_ID_PRIVACY_BUTLER = "130";
    public static final String PRODUCT_ID_RELEASE_ME = "90";
    public static final String PRODUCT_ID_SIMPLE_CLOCK = "129";
    public static final String PRODUCT_ID_STIKER_PHOTO_EDITOR = "136";
    public static final String PRODUCT_ID_SUPER_SECURITY = "144";
    public static final String PRODUCT_ID_SUPER_WALLPAPER = "9001";
    public static final String PRODUCT_ID_S_PHOTO_EDITOR = "128";
    public static final String PRODUCT_ID_TASK_MANAGEMENT = "4";
    public static final String PRODUCT_ID_V_LAUNCHER = "135";
    public static final String PRODUCT_ID_ZERO_CAMERA = "87";
    public static final String PRODUCT_ID_ZERO_DIAL = "93";
    public static final String PRODUCT_ID_ZERO_FLASHLIGHT = "112";
    public static final String PRODUCT_ID_ZERO_LAUNCHER = "73";
    public static final String PRODUCT_ID_ZERO_LAUNCHER_THEME = "1007";
    public static final String PRODUCT_ID_ZERO_LOCKER = "84";
    public static final String PRODUCT_ID_ZERO_LOCKER_THEME = "1009";
    public static final String PRODUCT_ID_ZERO_READ = "85";
    public static final String PRODUCT_ID_ZERO_SHARE = "88";
    public static final String PRODUCT_ID_ZERO_SMS = "83";
    public static final String PRODUCT_ID_ZERO_SMS_THEME = "1008";
    public static final String PRODUCT_ID_ZERO_SPEED = "91";

    public static boolean uploadAdInstallAppStatistic(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        boolean uploadInstallAppStatistic = BaseSeq105OperationStatistic.uploadInstallAppStatistic(context, packageName);
        ActivationGuideNotification.saveToWaitActivationList(context, packageName);
        return uploadInstallAppStatistic;
    }

    public static void uploadAdActiveStaticstic(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            BaseSeq105OperationStatistic.uploadActivateAppStatistic(context, packageName);
        }
    }

    public static void uploadAdShowStaticstic(Context context, String sender, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark, String callUrl) {
        if (!TextUtils.isEmpty(sender)) {
            uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_SHOW, 1, entrance, tabCategory, position, associatedObj, aId, remark);
            if (!TextUtils.isEmpty(callUrl)) {
                uploadRequestUrl(context, callUrl);
            }
        }
    }

    public static void uploadAdDownloadClickStaticstic(Context context, String sender, String entrance, String packageName, String tabCategory, String position, String associatedObj, String aId, String remark, String downloadCallUrl, String installCallUrl) {
        if (!TextUtils.isEmpty(sender)) {
            uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_CLICK, 1, entrance, tabCategory, position, associatedObj, aId, remark);
            if (!TextUtils.isEmpty(packageName)) {
                saveReadyInstallList(context, getFunctionId(BaseSeq105OperationStatistic.SDK_AD_INSTALL), sender, packageName, BaseSeq105OperationStatistic.SDK_AD_INSTALL, entrance, tabCategory, position, associatedObj, aId, remark, String.valueOf(BaseSeq105OperationStatistic.OPERATION_LOG_SEQ), installCallUrl);
            }
            if (!TextUtils.isEmpty(downloadCallUrl)) {
                uploadRequestUrl(context, downloadCallUrl);
            }
        }
    }

    public static void uploadAdDownloadedStatistic(Context context, String sender, String aId, String tabCategory, String position, String entrance, String associatedObj, String remark) {
        uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_DOWNLOADED, 1, entrance, tabCategory, position, associatedObj, aId, remark);
    }

    public static void uploadAdRequestStatistic(Context context, String fbId, String tabCategory, BaseModuleDataItemBean moduleDataItemBean, AdSdkParamsBuilder params) {
        if (params.mIsUploadAdRequestStatistic) {
            String entrance = String.valueOf(moduleDataItemBean.getOnlineAdvType());
            String position = String.valueOf(moduleDataItemBean.getModuleId());
            String remark = moduleDataItemBean.getStatistics105Remark();
            uploadAdRequest(context, fbId, entrance, tabCategory, position, getAdvDataSource(moduleDataItemBean.getAdvDataSource(), moduleDataItemBean.getOnlineAdvPositionId()), remark);
        }
    }

    public static void uploadAdRequestResultStatistic(Context context, String fbId, String tabCategory, int result, BaseModuleDataItemBean moduleDataItemBean, long duration, AdSdkParamsBuilder params) {
        if (params.mIsUploadAdRequestStatistic) {
            String entrance = String.valueOf(moduleDataItemBean.getOnlineAdvType());
            String position = String.valueOf(moduleDataItemBean.getModuleId());
            String aId = String.valueOf(result);
            String remark = moduleDataItemBean.getStatistics105Remark();
            String advDataSource = getAdvDataSource(moduleDataItemBean.getAdvDataSource(), moduleDataItemBean.getOnlineAdvPositionId());
            uploadAdRequestResult(context, fbId, entrance, tabCategory, position, advDataSource, aId, remark);
            uploadAdRequestDuration(context, fbId, entrance, "" + duration, position, advDataSource, aId, remark);
        }
    }

    private static void uploadAdRequest(Context context, String sender, String entrance, String tabCategory, String position, String associatedObj, String remark) {
        uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_REQUEST, 1, entrance, tabCategory, position, associatedObj, "", remark);
    }

    private static void uploadAdRequestResult(Context context, String sender, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark) {
        uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_REQUEST_RESULT, 1, entrance, tabCategory, position, associatedObj, aId, remark);
    }

    private static void uploadAdRequestDuration(Context context, String sender, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark) {
        uploadSqe105StatisticData(context, sender, BaseSeq105OperationStatistic.SDK_AD_REQUEST_DURATION, 1, entrance, tabCategory, position, associatedObj, aId, remark);
    }

    public static void uploadClientAdRequest(Context context, String tabCategory, String position) {
        if (!INTERNAL_TABCATEGORY.equals(tabCategory)) {
            uploadSqe105StatisticData(context, "", BaseSeq105OperationStatistic.SDK_CLIENT_AD_REQUEST, 1, "", tabCategory, position, "", "", "");
        }
    }

    public static void uploadAdShowActivationGuideStaticstic(Context context, String optionCode, String sender, String position, String aId, String remark) {
        if (!TextUtils.isEmpty(optionCode)) {
            uploadSqe105StatisticData(context, getFunctionId(optionCode), sender, optionCode, 1, "", "", position, "", aId, remark);
        }
    }

    public static void uploadAdActivationGuideBtnClickStaticstic(Context context, String optionCode, String sender, String position, String aId, String remark) {
        if (!TextUtils.isEmpty(optionCode)) {
            uploadSqe105StatisticData(context, getFunctionId(optionCode), sender, optionCode, 1, "", "", position, "", aId, remark);
        }
    }

    public static void uploadMaterialAdF00(Context context, String sender, String aId, String tabCategory, String position) {
        uploadSqe105StatisticData(context, getFunctionId(MATERIAL_AD_F000), sender, MATERIAL_AD_F000, 1, "", tabCategory, position, "", aId, (String) null);
    }

    public static void uploadMaterialAdA00(Context context, String sender, String aId, String tabCategory, String position) {
        uploadSqe105StatisticData(context, getFunctionId(MATERIAL_AD_A000), sender, MATERIAL_AD_A000, 1, "", tabCategory, position, "", aId, (String) null);
    }

    public static String getStatisticCid(Product product) {
        if (product.isNewInit()) {
            return product.getStatisticId105() + "";
        }
        String cid = product.getCid();
        String entranceId = product.getEntranceId();
        if ("4".equals(cid) || "9".equals(cid)) {
            if (entranceId.equals("2")) {
                return PRODUCT_ID_GO_KEYBOARD_THEME;
            }
            return "56";
        } else if ("5".equals(cid)) {
            if (entranceId.equals("2")) {
                return "5";
            }
            return "11";
        } else if ("6".equals(cid)) {
            if (entranceId.equals("2")) {
                return PRODUCT_ID_GO_SMS_THEME;
            }
            return "6";
        } else if ("7".equals(cid)) {
            if (entranceId.equals("2")) {
                return PRODUCT_ID_GO_LOCKER_THEME;
            }
            return "26";
        } else if ("8".equals(cid) || "22".equals(cid)) {
            if (entranceId.equals("2")) {
                return PRODUCT_ID_ZERO_LAUNCHER_THEME;
            }
            return PRODUCT_ID_ZERO_LAUNCHER;
        } else if ("10".equals(cid)) {
            return "47";
        } else {
            if ("11".equals(cid)) {
                if (entranceId.equals("2")) {
                    return PRODUCT_ID_NEXT_LAUNCHER_THEME;
                }
                return "13";
            } else if ("12".equals(cid)) {
                if (entranceId.equals("2")) {
                    return PRODUCT_ID_GO_WEATHER_THEME;
                }
                return "2";
            } else if ("13".equals(cid)) {
                if (entranceId.equals("2")) {
                    return PRODUCT_ID_ZERO_SMS_THEME;
                }
                return PRODUCT_ID_ZERO_SMS;
            } else if ("15".equals(cid)) {
                return "91";
            } else {
                if ("16".equals(cid)) {
                    return "8";
                }
                if ("21".equals(cid)) {
                    return PRODUCT_ID_ZERO_CAMERA;
                }
                if ("20".equals(cid)) {
                    return PRODUCT_ID_APP_LOCKER;
                }
                if ("31".equals(cid)) {
                    return "56";
                }
                if (AdSdkApi.PRODUCT_ID_NEXT_BROWSER.equals(cid)) {
                    return "21";
                }
                if ("33".equals(cid)) {
                    return PRODUCT_ID_ONE_KEY_LOCKER;
                }
                if ("34".equals(cid)) {
                    return "68";
                }
                if ("35".equals(cid)) {
                    return PRODUCT_ID_DOUBLE_OPEN;
                }
                if ("36".equals(cid)) {
                    return PRODUCT_ID_CUCKOO_NEWS;
                }
                if ("37".equals(cid)) {
                    return PRODUCT_ID_GO_SECURITY;
                }
                if ("38".equals(cid)) {
                    return PRODUCT_ID_GO_MUSIC_PLAYER;
                }
                if ("39".equals(cid)) {
                    return PRODUCT_ID_GO_KEYBOARD_PRO;
                }
                if ("40".equals(cid)) {
                    return PRODUCT_ID_GOMO_GAME;
                }
                if ("42".equals(cid)) {
                    return PRODUCT_ID_GO_CALLER;
                }
                if ("41".equals(cid)) {
                    return PRODUCT_ID_ZERO_FLASHLIGHT;
                }
                if ("43".equals(cid)) {
                    return PRODUCT_ID_GO_POWER_MASTER_PRO;
                }
                if ("44".equals(cid)) {
                    return PRODUCT_ID_GO_DARLING;
                }
                if ("45".equals(cid)) {
                    return PRODUCT_ID_GO_TRANSFER;
                }
                if ("46".equals(cid)) {
                    return PRODUCT_ID_GO_BFLASHLIGHT;
                }
                if ("19".equals(cid)) {
                    return PRODUCT_ID_SUPER_WALLPAPER;
                }
                if ("47".equals(cid)) {
                    return PRODUCT_ID_ACE_CLEANER;
                }
                if ("48".equals(cid)) {
                    return "13";
                }
                if ("49".equals(cid)) {
                    return PRODUCT_ID_ACE_SECURITY;
                }
                if ("50".equals(cid)) {
                    return PRODUCT_ID_GO_DOUBLE_OPEN;
                }
                if ("51".equals(cid)) {
                    return PRODUCT_ID_POWER_MASTER_PLUS;
                }
                if ("52".equals(cid)) {
                    return PRODUCT_ID_S_PHOTO_EDITOR;
                }
                if ("53".equals(cid)) {
                    return PRODUCT_ID_HI_KEYBOARD;
                }
                if ("54".equals(cid)) {
                    return PRODUCT_ID_MUSIC_PLAYER_MASTER;
                }
                if ("57".equals(cid)) {
                    return PRODUCT_ID_GO_NETWORK_SECURITY;
                }
                if ("56".equals(cid)) {
                    return "130";
                }
                if ("55".equals(cid)) {
                    return PRODUCT_ID_SIMPLE_CLOCK;
                }
                if ("58".equals(cid)) {
                    return PRODUCT_ID_MY_WEATHER_REPORTER;
                }
                if ("59".equals(cid)) {
                    return PRODUCT_ID_COOL_SMS;
                }
                if ("60".equals(cid)) {
                    return PRODUCT_ID_V_LAUNCHER;
                }
                if ("61".equals(cid)) {
                    return PRODUCT_ID_STIKER_PHOTO_EDITOR;
                }
                if (AdSdkApi.PRODUCT_ID_ALPHA_SECURITY.equals(cid)) {
                    return PRODUCT_ID_ALPHA_SECURITY;
                }
                if (AdSdkApi.PRODUCT_ID_LETS_CLEAN.equals(cid)) {
                    return PRODUCT_ID_LETS_CLEAN;
                }
                if (AdSdkApi.PRODUCT_ID_ACE_SECURITY_PLUS.equals(cid)) {
                    return PRODUCT_ID_ACE_SECURITY_PLUS;
                }
                if (AdSdkApi.PRODUCT_ID_DOOM_RACING.equals(cid)) {
                    return PRODUCT_ID_DOOM_RACING;
                }
                if (AdSdkApi.PRODUCT_ID_BLUE_BATTERY.equals(cid)) {
                    return PRODUCT_ID_BLUE_BATTERY;
                }
                if (AdSdkApi.PRODUCT_ID_BUBBLE_FISH.equals(cid)) {
                    return "143";
                }
                if ("68".equals(cid)) {
                    return "19";
                }
                if (AdSdkApi.PRODUCT_ID_SUPER_SECURITY.equals(cid)) {
                    return PRODUCT_ID_SUPER_SECURITY;
                }
                return "-1";
            }
        }
    }

    public static String getAdvDataSource(int advDataSource, int onlineAdvPositionId) {
        if (advDataSource == 0) {
            return ADV_DATA_SOURCE_BIG_DATA_LOCAL_PRIORITY;
        }
        if (advDataSource == 1) {
            return ADV_DATA_SOURCE_LOCAL_CONFIG;
        }
        if (advDataSource == 3) {
            return ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_BIG_DATA;
        }
        if (advDataSource == 4) {
            return ADV_DATA_SOURCE_GO_LAUNCHER_CLEAR_QUETTRA;
        }
        if (advDataSource == 5) {
            return ADV_DATA_SOURCE_BIG_DATA_LOCAL_RECURRENCE;
        }
        if (advDataSource == 6) {
            return ADV_DATA_SOURCE_PARR_BOGART;
        }
        if (advDataSource == 7) {
            return ADV_DATA_SOURCE_MOBIVISTA;
        }
        if (advDataSource == 12) {
            return ADV_DATA_SOURCE_GENERAL_ONLINE_DATA;
        }
        if (advDataSource == 13) {
            return ADV_DATA_SOURCE_INTELLIGENT;
        }
        if (advDataSource == 14) {
            return ADV_DATA_SOURCE_LOCAL_CONFIG_CN;
        }
        if (advDataSource == 15) {
            return ADV_DATA_SOURCE_INTELLIGENT_CLASSIFY;
        }
        if (advDataSource == 11 || advDataSource == 2) {
            return ADV_DATA_SOURCE_FACEBOOK;
        }
        if (advDataSource == 8) {
            return ADV_DATA_SOURCE_ADMOB;
        }
        if (advDataSource != 9 && advDataSource != 10) {
            return String.valueOf(advDataSource);
        }
        if (onlineAdvPositionId > 0) {
            return ADV_DATA_SOURCE_MOBILE_CORE_ONLINE;
        }
        return ADV_DATA_SOURCE_MOBILE_CORE_SDK;
    }
}

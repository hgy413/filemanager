package com.jiubang.commerce.ad.http;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkApi;
import com.jiubang.commerce.ad.avoid.AdAvoider;
import com.jiubang.commerce.ad.avoid.CountryDetector;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.params.UserTagParams;
import com.jiubang.commerce.ad.url.AdRedirectUrlUtils;
import com.jiubang.commerce.product.Product;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AdSdkRequestHeader {
    public static final String ABTEST_SERVER_URL = "http://abtest.goforandroid.com/abtestcenter/ab";
    public static final String ANDROID_ID = "aid";
    public static final String BUY_CHANNEL = "buychannel";
    public static final String BUY_CHANNEL_TYPE_REQUEST_URL = "http://newstoredata.goforandroid.com/newstore/usertype";
    public static final String DEFAULT_BUY_CHANNEL = "buychannel-none";
    public static final String INTELLIGENT_REQUEST_URL = "http://adviap.goforandroid.com/adv_iap/smartload";
    public static final boolean IS_INNER = true;
    public static final String ONLINE_AD_ACCESSKEY = "accessKey";
    public static final int ONLINE_AD_NET_DATA_CONNECTION_TIME_OUT = 15000;
    public static final int ONLINE_AD_NET_DATA_READ_TIME_OUT = 15000;
    public static final String ONLINE_AD_PRODKEY = "prodKey";
    public static final String ONLINE_AD_REQUEST_URL = "http://advs2sonline.goforandroid.com/s2sadv";
    public static final String PRODUCT_ID = "cid";
    public static final int PVERSION = 21;
    public static final String RESPONSE_RESULT = "result";
    public static final String RESPONSE_STATUS = "status";
    public static final int RESPONSE_STATUS_ERROR_CODE_0 = 0;
    public static final int RESPONSE_STATUS_ERROR_CODE_1 = -1;
    public static final int RESPONSE_STATUS_ERROR_CODE_2 = -2;
    public static final int RESPONSE_STATUS_OK = 1;
    public static final String SEARCH_PRESOLVE_REQUEST_URL = "http://advsearch.goforandroid.com/adv_search/search";
    public static final String SERVER_URL_SIT = "http://gotest.3g.net.cn/newstore/";
    public static final String SIMB_REQUEST_URL = "http://adviap.goforandroid.com/adv_iap/smartload_install";
    public static final String URL_PREFIX = "common?funid=";
    public static final String USERTAG_AD_REQUEST_URL = "http://adviap.goforandroid.com/adv_iap/userTag";
    public static boolean sIS_TEST_SERVER = false;
    public static String sSERVER_URL = "http://newstoredata.goforandroid.com/newstore/";

    public static class S2SParams {
        public String mApplovinPlacement = null;
    }

    public static void setTestServer(boolean isTestServer) {
        sIS_TEST_SERVER = isTestServer;
    }

    public static String getUrl(String funId) {
        return (sIS_TEST_SERVER ? SERVER_URL_SIT : sSERVER_URL) + URL_PREFIX + funId + "&rd=" + System.currentTimeMillis();
    }

    public static String getABTestUrl() {
        return sIS_TEST_SERVER ? ABTEST_SERVER_URL : ABTEST_SERVER_URL;
    }

    public static String getOnlineAdUrl() {
        return ONLINE_AD_REQUEST_URL;
    }

    public static String getUserTagUrl() {
        return USERTAG_AD_REQUEST_URL;
    }

    public static String getBuyChannelTypeUrl() {
        return BUY_CHANNEL_TYPE_REQUEST_URL;
    }

    public static JSONObject createPhead(Context context, AdSdkParamsBuilder apb) {
        AdSdkManager advertManager = AdSdkManager.getInstance();
        return createPhead(context, advertManager.getGoId(), advertManager.getGoogleId(), advertManager.getCid(), advertManager.getChannel(), advertManager.getDataChannel(), advertManager.getEntranceId(), apb);
    }

    private static JSONObject createPhead(Context context, String goid, String gadid, String cid, String channel, String dataChannel, String entranceId, AdSdkParamsBuilder apb) {
        Object valueOf;
        String local;
        if (context == null) {
            return null;
        }
        try {
            String buyuserchannel = apb.mBuyuserchannel;
            Integer cdays = apb.mCdays;
            Integer userFrom = apb.mUserFrom;
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("pversion", 21);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put(PRODUCT_ID, cid);
            if ("2".equals(StringUtils.toString(entranceId))) {
                valueOf = "99";
            } else {
                valueOf = Integer.valueOf(AppUtils.getAppVersionCode(context, context.getPackageName()));
            }
            pheadJson.put("cversion", valueOf);
            pheadJson.put("cversionname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("uid", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("gadid", gadid);
            pheadJson.put("goid", goid);
            pheadJson.put("channel", channel);
            if (apb.mDetectVpn) {
                boolean vpn = NetworkUtils.isVpnConnected();
                LogUtils.d("Ad_SDK", "Open Vpn detect:" + vpn);
                local = vpn ? CountryDetector.AVOID_COUNTRY_CODE : StringUtils.toUpperCase(SystemUtils.getLocal(context));
            } else {
                LogUtils.d("Ad_SDK", "Close Vpn detect!");
                local = StringUtils.toUpperCase(SystemUtils.getLocal(context));
            }
            pheadJson.put(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_IP_LOCAL, local);
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("model", Build.MODEL);
            pheadJson.put("requesttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            pheadJson.put("entranceId", entranceId);
            pheadJson.put("hasmarket", GoogleMarketUtils.isMarketExist(context) ? 1 : 0);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("dpi", SystemUtils.getDisplay(context));
            pheadJson.put("dataChannel", dataChannel);
            if (TextUtils.isEmpty(buyuserchannel)) {
                buyuserchannel = DEFAULT_BUY_CHANNEL;
            }
            pheadJson.put(BUY_CHANNEL, buyuserchannel);
            int cdaysPrimitive = 1;
            if (cdays != null) {
                cdaysPrimitive = Math.max(cdays.intValue(), 1);
            }
            pheadJson.put("cdays", cdaysPrimitive);
            pheadJson.put("pkgname", context.getPackageName());
            if (userFrom != null) {
                pheadJson.put("user_from", userFrom.intValue());
            }
            if (AdAvoider.getInstance(context).shouldAvoid()) {
                pheadJson.put("iscn", 1);
                return pheadJson;
            }
            pheadJson.put("iscn", 2);
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createPheadForOnlineFromParams(Context context, UserTagParams userTagParams, boolean isNew) {
        String userType;
        if (isNew) {
            userType = "new";
        } else {
            userType = "old";
        }
        return createPheadForUserTag(context, userTagParams.mGoId, userTagParams.mGoogleId, userTagParams.mChannel, userType);
    }

    public static JSONObject createPheadForOnline(Context context, int adCount, int adPos, S2SParams params) {
        AdSdkManager advertManager = AdSdkManager.getInstance();
        return createPheadForOnline(context, advertManager.getGoId(), advertManager.getGoogleId(), advertManager.getChannel(), adCount, adPos, params);
    }

    public static JSONObject createPheadForOnline(Context context, String goid, String gadid, String channel, int adCount, int adPos, S2SParams params) {
        int i = 0;
        if (context == null) {
            return null;
        }
        try {
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("advposids", String.valueOf(adPos));
            pheadJson.put("channel", channel);
            pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
            pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("goid", goid);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("sbuy", 0);
            if (GoogleMarketUtils.isMarketExist(context)) {
                i = 1;
            }
            pheadJson.put("hasmarket", i);
            pheadJson.put("dpi", SystemUtils.getDpi(context));
            pheadJson.put("resolution", SystemUtils.getDisplay(context));
            pheadJson.put("adid", gadid);
            pheadJson.put("count", adCount);
            pheadJson.put("cip", "");
            pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            pheadJson.put("model", Build.MODEL);
            pheadJson.put("modelbrand", Build.BRAND);
            if (params == null) {
                return pheadJson;
            }
            pheadJson.put("placement", params.mApplovinPlacement);
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createPheadForUserTag(Context context, String goid, String gadid, String channel, String userType) {
        int i = 0;
        if (context == null) {
            return null;
        }
        try {
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("channel", channel);
            pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
            pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("goid", goid);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("sbuy", 0);
            if (GoogleMarketUtils.isMarketExist(context)) {
                i = 1;
            }
            pheadJson.put("hasmarket", i);
            pheadJson.put("dpi", SystemUtils.getDpi(context));
            pheadJson.put("resolution", SystemUtils.getDisplay(context));
            pheadJson.put("adid", gadid);
            pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            pheadJson.put("usertype", userType);
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createPheadForIntelligent(Context context, int adPos) {
        int i = 0;
        if (context == null) {
            return null;
        }
        AdSdkManager advertManager = AdSdkManager.getInstance();
        String goid = advertManager.getGoId();
        String gadid = advertManager.getGoogleId();
        String channel = advertManager.getChannel();
        String cid = advertManager.getCid();
        try {
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("advposid", String.valueOf(adPos));
            pheadJson.put("pkgs", AppUtils.getInstallAppInfoWithoutSys(context));
            pheadJson.put("channel", channel);
            pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
            pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("goid", goid);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("sbuy", 0);
            if (GoogleMarketUtils.isMarketExist(context)) {
                i = 1;
            }
            pheadJson.put("hasmarket", i);
            pheadJson.put("dpi", SystemUtils.getDpi(context));
            pheadJson.put("resolution", SystemUtils.getDisplay(context));
            pheadJson.put("adid", gadid);
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "[Intelligent:" + adPos + "]gaid=" + gadid);
            }
            pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            pheadJson.put(PRODUCT_ID, cid);
            pheadJson.put("pversion", 21);
            pheadJson.put("uid", SystemUtils.getVirtualIMEI(context));
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createPheadForSearchPresolve(Context context, int adPos) {
        int i = 0;
        if (context == null) {
            return null;
        }
        AdSdkManager advertManager = AdSdkManager.getInstance();
        String goid = advertManager.getGoId();
        String gadid = advertManager.getGoogleId();
        String channel = advertManager.getChannel();
        String cid = advertManager.getCid();
        try {
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("advposid", String.valueOf(adPos));
            pheadJson.put("pkgs", AppUtils.getInstallAppInfoWithoutSys(context));
            pheadJson.put("channel", channel);
            pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
            pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("goid", goid);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("sbuy", 0);
            if (GoogleMarketUtils.isMarketExist(context)) {
                i = 1;
            }
            pheadJson.put("hasmarket", i);
            pheadJson.put("dpi", SystemUtils.getDpi(context));
            pheadJson.put("resolution", SystemUtils.getDisplay(context));
            pheadJson.put("adid", gadid);
            pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            pheadJson.put(PRODUCT_ID, cid);
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createPheadForSIMB(Context context, int adPos) {
        int i = 0;
        if (context == null) {
            return null;
        }
        AdSdkManager advertManager = AdSdkManager.getInstance();
        String goid = advertManager.getGoId();
        String gadid = advertManager.getGoogleId();
        String channel = advertManager.getChannel();
        String cid = advertManager.getCid();
        try {
            JSONObject pheadJson = new JSONObject();
            pheadJson.put("advposid", String.valueOf(adPos));
            pheadJson.put("pkgs", AppUtils.getInstallAppInfoWithoutSys(context));
            pheadJson.put("channel", channel);
            pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
            pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
            pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
            pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
            pheadJson.put("goid", goid);
            pheadJson.put(ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
            pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
            pheadJson.put("imsi", SystemUtils.getImsi(context));
            pheadJson.put("sys", Build.VERSION.RELEASE);
            pheadJson.put("sdk", Build.VERSION.SDK_INT);
            pheadJson.put("net", NetworkUtils.buildNetworkState(context));
            pheadJson.put("sbuy", 0);
            if (GoogleMarketUtils.isMarketExist(context)) {
                i = 1;
            }
            pheadJson.put("hasmarket", i);
            pheadJson.put("dpi", SystemUtils.getDpi(context));
            pheadJson.put("resolution", SystemUtils.getDisplay(context));
            pheadJson.put("adid", gadid);
            pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            return pheadJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> getOnlineAdRequestParameKey() {
        Map<String, String> parameKeyMap = new HashMap<>();
        Product product = AdSdkManager.getInstance().getProduct();
        if (product.isNewInit()) {
            parameKeyMap.put(ONLINE_AD_PRODKEY, product.getAdRequestProductKey());
            parameKeyMap.put(ONLINE_AD_ACCESSKEY, product.getAdRequestAccessKey());
        } else {
            String productId = AdSdkManager.getInstance().getCid();
            if (TextUtils.isEmpty(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "QI1ORHMZK7Q58R0Y7FCH0Z9S");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "BFEJ0NGAJTXAANYHHEIC7BIFC456ZAXJ");
            } else if ("8".equals(productId) || "22".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "E52B8QOI0EL3WWN1W3303F0E");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "GQ6ZO3H6E08VKSYT5JH5GRX54D8STTCR");
            } else if ("11".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "QYT61Y5YD2SQFKVZ1J5LQ0V3");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "KQOE2K47M5WXRBTIGGR52U4YHPQZNUXA");
            } else if ("6".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "669IO7IIH1LVEGMY1V7MM29Z");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "8ZW4DQF9KXYSD4SY01TFW4O3FTU3IAQ8");
            } else if ("7".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "7WARFBXAFC7VA35FWHDDN6I9");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "ZOIG12T0XCAAWXTRX5FN0GBF52EBCK9H");
            } else if ("5".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "QNRUVO790PNQNGCM65TU387I");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "4EP7066V6ZQ3KCWZD33I3R04RW3JVL66");
            } else if ("9".equals(productId) || "4".equals(productId) || "31".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "8YZN10M5Y87YMR8QYM73SWSM");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "94HYC3NQ5PFIE38YT85Z8SCVZBWRJVG4");
            } else if ("12".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "PTE0ICLOEGNIOOLS08LJPTVR");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "Q1BGHV5DTEHRT87FIB0LCE7K61N0W58Z");
            } else if ("13".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "6U267MSOA2F6S896QJO0NYDJ");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "FVMRFDZDD3ZO9YGRIH2063KOS6QMGZMZ");
            } else if ("15".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "27LYD9AGENAJPCU3O0XV9WLF");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "YO0FINC2I0JSPDMB2KWMLZRJ94BHT6IA");
            } else if ("16".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "QYWXEZGVND26KHVYF9SF7NGK");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "28J3ZCJLXTW06HJYEPOEKOSVVQADNNML");
            } else if ("19".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "I6140ZNLO7SHXTUQ4QD20FSD");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "D4PILZZL2QDKQU8882XZ6O9KCR2KHTQS");
            } else if ("20".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "OOXT0131V6M6WICWLF9TSV6I");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "JXLK1Z8CIF9Z58BSCPP8LL734VL0LI77");
            } else if ("10".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "N7XKESY5GTK3UIO981SE43P6");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "HPJOVTREZ28LWF51WWA1YI6KUDY7C1SF");
            } else if ("21".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "7G59NTHN7UBHXSSXI4QCMN35");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "3VX5LRSDBKSPFWF78MQ9DOL83ZUBFIO0");
            } else if ("23".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "64YUVPYFS1LWBYAW239XM1WD");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "VN0MA5ZN2VRJY44JKO5GUJO3PHTI7NKX");
            } else if (AdSdkApi.PRODUCT_ID_NEXT_BROWSER.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "ME7F79ED4WCSSR0JK26A383K");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "IOHOHJ6UNRAJLG1NM07NWWAS3PYWV99C");
            } else if ("33".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "319LT8R7X1D5P0UAUJI785ZR");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "LJB685VL4CV0EOJEGAZUMKAFFV2ZLZ8T");
            } else if ("34".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "A2JTFB32PHJWSRIYB5RQLVII");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "UQS68UJL5FZQLYWE5EEVJLBVEDX7Q1YN");
            } else if ("35".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "CXPNA5TBWRRPXGJOJP2Y29LI");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "OOUNXPE2XMQFUNZ5WT2COYAT0JDDD2YY");
            } else if ("36".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "SL9AKA7QZYRPY7R86NGS2NDI");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "CU7HLLYG2BC800BROMLJ1TU229ZFFNXJ");
            } else if ("37".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "GD5VJ2YC7FDW6NC3QUE0BT1M");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "BWCJH13C3BV3LXZL7P5X5E6TEA9TJTP5");
            } else if ("38".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "Z8V3B7LJR2NYHVWPCTC99KQ6");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "79FJ6VTO03QULVOH2P70ANUAO0YLR083");
            } else if ("39".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "JTRMXDWHW2XNHP0DUV5SABEA");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "8PL66OYSQW17XHSNCXM7MUUG4DZSCC6N");
            } else if ("40".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "HVNJREFZRWLC42GXWDW2KMIL");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "YHOFOOJ9YWBZHS7MWZ8MT913DHJTKH7C");
            } else if ("42".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "XYTMDSPT6B4UGME8DV81MQ1N");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "DSJ3Y7C5CC0Y7Z4EXE25U9KLDMR7KDQF");
            } else if ("41".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "45H0IFU7FPCRYLL5BBDZJI1K");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "XOOIVECU55DXTTFDM4PKR0VF1787DC98");
            } else if ("43".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "K1X2H5IKBSPACU19EG2RW4LF");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "1BAY3NYD96DP2IBCCEB1SPSGLUWKTPOX");
            } else if ("44".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "2KLX0761R3ZI878QS07YEUFT");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "TOHE0GC78R8027PXAGE2N1KLU4HZJ5CC");
            } else if ("45".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "XM6US68JL1YIGHAHWXRDU9OE");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "T39WSC9SG1JDJ379YJLGQOH38YHN2JS5");
            } else if ("46".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "FEX7T4JJ5QV9KHV83OH66BJH");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "OP0GKB1SSQ7NNAP6HLUI7O4LNICZRI4D");
            } else if ("47".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "Q0ONNHDWJ87VW70092FBSPYZ");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "TZ8Z8Q4SEACARB1AAEV1ARDZ8JCI6T82");
            } else if ("48".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "9UMQUN988XM5V8V5E2A78WDP");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "IS2KU051538ZL3CHXKYJGF515380250E");
            } else if ("49".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "KY5GFQULX8TWG75ARKGNGRYJ");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "WE972EQZZ0HLLBB7WM0P9KE3799Z47O0");
            } else if ("50".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "J1DNSCU6M1KF3ZOP24WNAW8I");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "00CL5EQ620ICFK7GFG212Z56HS1Q260M");
            } else if ("51".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "8BUBM75LZY5LE1AEE8RO12OS");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "G32DWNMRH04Z4HK9NREE68UN6UIRL2TV");
            } else if ("52".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "XR04XN25K1N4589O1PM3CSV7");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "Y4FYBIPZFMM988LTU2IHOBH415DAJS0K");
            } else if ("53".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "NPYWQ94RB3HCBW6KZA2065AF");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "IFSZRV560EGWZ3IVR10G1W8HWT5T67UV");
            } else if ("54".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "0W1PO081T9690NL7I0DMED7D");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "A6WSNI5AV9TUQ35L9JPLYWP0ONMVKQFM");
            } else if ("57".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "4PEF9TYL0AB1HC5KR0GPC23H");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "WD6T8OPDTJTOZVKG0J18XUE0PE7288OQ");
            } else if ("56".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "WOA4THF3ZCCW6ROBKRPB3SOI");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "JXE4TDASEI0RFY60PJED5K99HL7KAM7F");
            } else if ("55".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "LXFK8QBLH00CIZ3JRGJ9295U");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "2D4WXO9PTRQYZ4QQYHO8GXDMO5TVBQYK");
            } else if ("58".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "GH0SBKA8DZGSY5MJGV02ASVY");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "5ZWOWLUPO199C6BC0HE4XWT2WM84RG3C");
            } else if ("59".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "VFS4TIXIUB5S4V0JMATOWI3Q");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "KCMV96HABW4K1FD2JGSR0H3V5JP31CVG");
            } else if ("60".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "EY7MZEEWKXEQTENBK10GZV8B");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "63OFBR5S75KFZP509CLJJBGZGI3QNKGF");
            } else if ("61".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "W7Y8GCQKITAYGM9MLQRWIANW");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "MB22ORYZKIBVJLJYOK7NV6HVT6LBRHPP");
            } else if (AdSdkApi.PRODUCT_ID_ALPHA_SECURITY.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "KY947Y1O3EZM2P6JXQP159L2");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "G0K3Q045FDUMZ2TLT7R9QAKKYW6HPPA7");
            } else if (AdSdkApi.PRODUCT_ID_LETS_CLEAN.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "WLU3YIW5667PCHZQ1QJPDRYR");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "AOFIBR2OHY4QTCUVWFJ5FKKTIY6UD078");
            } else if (AdSdkApi.PRODUCT_ID_ACE_SECURITY_PLUS.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "NRY4FU4KL5BSY4NX9UUVJLMZ");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "7X05HIAWY2C05CYMXT264CLBK1XDWX7W");
            } else if (AdSdkApi.PRODUCT_ID_DOOM_RACING.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "ZG9QE7FR95VCJFWJ4ZDBTC8B");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "N6L631UBCXFUGOJYUGHLMJ69W9F5IT7L");
            } else if (AdSdkApi.PRODUCT_ID_BLUE_BATTERY.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "485IGVHXRY1HWAU4N9VJ6MC0");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "EGIFNV479IFVYZF4XOHV2IXDMTLDY4TT");
            } else if (AdSdkApi.PRODUCT_ID_BUBBLE_FISH.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "8R6TNFMBR7LE1SW8N8QSB3UO");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "4LKZRZAD1NN7HIN4TG6J5JR6QMHB2HPX");
            } else if ("68".equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "XQLUQU8TO8M737AK5X1TS8ZA");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "XO6T0U2H09OK0WHS7XA2DAIRF3WYWFMU");
            } else if (AdSdkApi.PRODUCT_ID_SUPER_SECURITY.equals(productId)) {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "TEX3VG7Q43DHZVIRNTGULW8H");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "BBHBC2VQ3M8T2E96OOZ9T5Q929ECGB80");
            } else {
                parameKeyMap.put(ONLINE_AD_PRODKEY, "QI1ORHMZK7Q58R0Y7FCH0Z9S");
                parameKeyMap.put(ONLINE_AD_ACCESSKEY, "BFEJ0NGAJTXAANYHHEIC7BIFC456ZAXJ");
            }
        }
        return parameKeyMap;
    }
}

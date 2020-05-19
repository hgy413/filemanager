package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import com.gau.utils.net.util.HeartSetting;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.ResourcesProvider;
import com.jiubang.commerce.ad.http.bean.ParamsBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.url.AdRedirectJumpTask;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;

public class AppDetailsJumpUtil {
    protected static final int URL_TYPE_NORMAL = 0;
    protected static final int URL_TYPE_REDIRECT = 1;
    public static long sREQUEST_TIME_OUT_DURATION;
    private static AdRedirectJumpTask.ExecuteTaskStateListener sTaskStateListener = new AdRedirectJumpTask.ExecuteTaskStateListener() {
        public void onExecuteTaskComplete(Context context, int stateCode, String pkgName, String newAdUrl, String redirectUrl, String googlePalyUrl, long startTime, boolean isShowFloatWindow) {
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "AppDetailsJumpUtil.onExecuteTaskComplete(" + stateCode + ", " + newAdUrl + ", " + googlePalyUrl + ", " + startTime + ", " + isShowFloatWindow + ")");
            }
            AdvanceParseRedirectUrl.getInstance(context).saveFinalUrl(pkgName, redirectUrl, newAdUrl);
            if (stateCode == 18) {
                Toast.makeText(context, ResourcesProvider.getInstance(context).getString("desksetting_net_error"), 1).show();
            } else if (System.currentTimeMillis() - startTime > AppDetailsJumpUtil.sREQUEST_TIME_OUT_DURATION) {
            } else {
                if (stateCode != 16 || TextUtils.isEmpty(newAdUrl)) {
                    if (!TextUtils.isEmpty(redirectUrl)) {
                        googlePalyUrl = redirectUrl;
                    }
                    AppDetailsJumpUtil.executeTaskFailure(context, googlePalyUrl, isShowFloatWindow);
                    return;
                }
                GoogleMarketUtils.gotoGoogleMarket(context, newAdUrl, true, isShowFloatWindow);
            }
        }

        public void onRequestTimeOut(Context context, String googlePalyUrl, String redirectUrl, boolean isShowFloatWindow) {
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "AppDetailsJumpUtil.onRequestTimeOut(" + context + ", " + googlePalyUrl + ", " + redirectUrl + ", " + isShowFloatWindow + ")");
            }
            if (!TextUtils.isEmpty(redirectUrl)) {
                googlePalyUrl = redirectUrl;
            }
            AppDetailsJumpUtil.executeTaskFailure(context, googlePalyUrl, isShowFloatWindow);
        }
    };

    static {
        sREQUEST_TIME_OUT_DURATION = HeartSetting.DEFAULT_HEART_TIME_INTERVAL;
        if (AdSdkManager.isGoKeyboard()) {
            sREQUEST_TIME_OUT_DURATION = 5000;
        }
    }

    public static boolean gotoAppDetails(Context context, ParamsBean paramsBean, String pkgName, int moduleId, int mapId, int aId, String adUrl, String downUrl, int isAd, boolean isOpenBrowser) {
        return gotoAppDetails(context, paramsBean, pkgName, moduleId, mapId, aId, adUrl, downUrl, isAd, isOpenBrowser, false, true, "", true);
    }

    public static boolean gotoAppDetails(Context context, ParamsBean paramsBean, String pkgName, int moduleId, int mapId, int aId, String adUrl, String downUrl, int isAd, boolean isOpenBrowser, boolean isShowToast, String toastMessageStr, boolean isShowFloatWindow) {
        return gotoAppDetails(context, paramsBean, pkgName, moduleId, mapId, aId, adUrl, downUrl, isAd, isOpenBrowser, false, isShowToast, toastMessageStr, isShowFloatWindow);
    }

    public static boolean gotoAppDetailsDialog(Context context, ParamsBean paramsBean, String pkgName, int moduleId, int mapId, int aId, String adUrl, String downUrl, int isAd, boolean isOpenBrowser, boolean isShowFloatWindow) {
        return gotoAppDetails(context, paramsBean, pkgName, moduleId, mapId, aId, adUrl, downUrl, isAd, isOpenBrowser, true, false, "", isShowFloatWindow);
    }

    private static boolean gotoAppDetails(Context context, ParamsBean paramsBean, String pkgName, int moduleId, int mapId, int aId, String adUrl, String downUrl, int isAd, boolean isOpenBrowser, boolean isShowDialog, boolean isShowToast, String toastMessageStr, boolean isShowFloatWindow) {
        String url;
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "gotoAppDetails(" + context + ", " + adUrl + ", " + downUrl + ", " + isAd + ", " + isShowDialog + ", " + isShowToast + ", " + toastMessageStr + ", " + isShowFloatWindow + ")");
        }
        if (context == null || (TextUtils.isEmpty(adUrl) && TextUtils.isEmpty(downUrl))) {
            return false;
        }
        if (!AdRedirectJumpTask.isRedirectUrl(adUrl, isAd) || !NetworkUtils.isNetworkOK(context)) {
            if (StringUtils.isEmpty(adUrl)) {
                url = downUrl;
            } else {
                url = adUrl;
            }
            GoogleMarketUtils.gotoGoogleMarket(context, url, isOpenBrowser, isShowFloatWindow);
        } else {
            String adFinalUrl = AdvanceParseRedirectUrl.getInstance(context).getFinalUrl(adUrl, new long[0]);
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "gotoAppDetails(" + adUrl + ", " + downUrl + ", " + adFinalUrl + ")");
            }
            if (!TextUtils.isEmpty(adFinalUrl)) {
                GoogleMarketUtils.gotoGoogleMarket(context, adFinalUrl, isOpenBrowser, isShowFloatWindow);
            } else {
                if (LogUtils.isShowLog()) {
                    LogUtils.i("Ad_SDK", "gotoAppDetails(302跳转," + context + ", " + adUrl + ", " + downUrl + ", " + isAd + ", " + isShowDialog + ", " + isShowToast + ", " + toastMessageStr + ", " + isShowFloatWindow + ")");
                }
                if (isShowDialog) {
                    Intent intent = new Intent(context, AdUrlPreParseLoadingActivity.class);
                    intent.addFlags(131072);
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_PARAMSBEAN, paramsBean);
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_PKG, pkgName);
                    intent.putExtra("moduleId", String.valueOf(moduleId));
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_MAP_ID, String.valueOf(mapId));
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_AD_ID, String.valueOf(aId));
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_DOWNLOAD_URL, downUrl);
                    intent.putExtra("redirectUrl", adUrl);
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_TIME_OUT_DURATION, sREQUEST_TIME_OUT_DURATION);
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_IS_SHOW_FLOAT_WINDOW, isShowFloatWindow);
                    intent.putExtra(AdUrlPreParseLoadingActivity.INTENT_KEY_IS_OPEN_BROWSER, isOpenBrowser);
                    context.startActivity(intent);
                } else {
                    AdRedirectJumpTask.startExecuteTask(context, paramsBean, pkgName, String.valueOf(moduleId), String.valueOf(mapId), String.valueOf(aId), adUrl, downUrl, sREQUEST_TIME_OUT_DURATION, isShowFloatWindow, isShowToast, toastMessageStr, sTaskStateListener);
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void executeTaskFailure(Context context, String adUrl, boolean isShowFloatWindow) {
        if (TextUtils.isEmpty(adUrl)) {
            Toast.makeText(context, ResourcesProvider.getInstance(context).getString("desksetting_net_error"), 1).show();
        } else {
            GoogleMarketUtils.gotoGoogleMarket(context, adUrl, true, isShowFloatWindow);
        }
    }
}

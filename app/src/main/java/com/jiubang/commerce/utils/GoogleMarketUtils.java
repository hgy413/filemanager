package com.jiubang.commerce.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.jiubang.commerce.ad.intelligent.api.IntelligentApi;
import com.jiubang.commerce.ad.window.ExitGoogleWindowManager;
import com.jiubang.commerce.service.AdService;
import java.util.List;

public class GoogleMarketUtils {
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP = "http://play.google.com/store/apps/details";
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS = "https://play.google.com/store/apps/details";
    public static final String GOOGLE_MARKET_APP_DETAIL = "market://details?id=";
    public static final String MARKET_PACKAGE = "com.android.vending";

    public static boolean gotoGoogleMarket(Context context, String uriString, boolean isOpenBrowser, boolean isShowFloatWindow) {
        if (context == null || TextUtils.isEmpty(uriString)) {
            return false;
        }
        if (isMarketExist(context)) {
            try {
                if (!uriString.startsWith(GOOGLE_MARKET_APP_DETAIL)) {
                    if (uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP)) {
                        uriString = uriString.substring("id=".length() + uriString.indexOf("id="));
                    } else if (uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS)) {
                        uriString = uriString.substring("id=".length() + uriString.indexOf("id="));
                    } else if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                        return gotoBrowser(context, uriString);
                    }
                    uriString = GOOGLE_MARKET_APP_DETAIL + uriString;
                }
                informGPSelfOpen(context);
                openGP(context, uriString);
                if (isShowFloatWindow) {
                    showFloatWindow(context);
                }
                return true;
            } catch (Exception e) {
                gotoBrowser(context, uriString);
                return false;
            }
        } else if (isOpenBrowser) {
            return gotoBrowser(context, uriString);
        } else {
            return false;
        }
    }

    public static void openGP(Context context, String uriString) {
        Intent marketIntent = new Intent("android.intent.action.VIEW", Uri.parse(uriString));
        marketIntent.setPackage(MARKET_PACKAGE);
        if (context instanceof Activity) {
            marketIntent.addFlags(524288);
            marketIntent.addFlags(1073741824);
        } else {
            marketIntent.addFlags(268435456);
            marketIntent.addFlags(32768);
        }
        context.startActivity(marketIntent);
    }

    private static void informGPSelfOpen(Context context) {
        IntelligentApi.startServiceWithCommand(context, IntelligentApi.COMMAND_SELF_OPEN_GP, (String[]) null);
    }

    public static boolean isMarketExist(Context context) {
        return AppUtils.isAppExist(context, MARKET_PACKAGE);
    }

    public static boolean isMarketUrl(String url) {
        if (TextUtils.isEmpty(url) || (!url.contains("play.google.com") && !url.contains("market://details"))) {
            return false;
        }
        return true;
    }

    public static GPMarketUrlResult isAbsoMarketUrl(String url) {
        return new GPMarketUrlResult(url);
    }

    /* access modifiers changed from: private */
    public static boolean gotoBrowser(Context context, String uriString) {
        String packageName;
        String activityName = null;
        if (context == null || TextUtils.isEmpty(uriString)) {
            return false;
        }
        if (!uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP) && !uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS) && (uriString.startsWith(GOOGLE_MARKET_APP_DETAIL) || (!uriString.startsWith("http://") && !uriString.startsWith("https://")))) {
            if (uriString.startsWith(GOOGLE_MARKET_APP_DETAIL)) {
                uriString = uriString.replace(GOOGLE_MARKET_APP_DETAIL, "?id=");
            }
            uriString = BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS + uriString;
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uriString));
            List<ResolveInfo> resolveList = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveList != null && resolveList.size() > 0) {
                ActivityInfo activityInfo = resolveList.get(0) != null ? resolveList.get(0).activityInfo : null;
                if (activityInfo != null) {
                    packageName = activityInfo.packageName;
                } else {
                    packageName = null;
                }
                if (activityInfo != null) {
                    activityName = activityInfo.name;
                }
                if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(activityName)) {
                    intent.setClassName(packageName, activityName);
                }
            }
            if (context instanceof Activity) {
                intent.addFlags(1073741824);
            } else {
                intent.addFlags(268435456);
                intent.addFlags(32768);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showFloatWindow(Context context) {
        if (context != null && !SystemUtils.IS_SDK_ABOVE_L) {
            ExitGoogleWindowManager.getInstance().setEntranceActivity(context);
            Intent intent = new Intent();
            intent.setClassName(context, "com.jiubang.commerce.service.AdService");
            Bundle bundle = new Bundle();
            bundle.putInt(AdService.AD_SERVICES_REQUEST, 16);
            intent.putExtras(bundle);
            context.startService(intent);
        }
    }

    public static final class GPMarketUrlResult {
        private String mBrowserUrl;
        private boolean mIsGPUrl;
        private String mMarketUrl;
        private final String mOrigin;

        public GPMarketUrlResult(String url) {
            this.mOrigin = url;
            if (!TextUtils.isEmpty(url)) {
                String url2 = url.toLowerCase();
                if (url2.startsWith(GoogleMarketUtils.BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP) || url2.startsWith(GoogleMarketUtils.BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS)) {
                    this.mIsGPUrl = true;
                    int start = url2.indexOf("id=");
                    this.mBrowserUrl = url2;
                    this.mMarketUrl = GoogleMarketUtils.GOOGLE_MARKET_APP_DETAIL + url2.substring("id=".length() + start);
                } else if (url2.startsWith(GoogleMarketUtils.GOOGLE_MARKET_APP_DETAIL)) {
                    this.mIsGPUrl = true;
                    this.mBrowserUrl = "https://play.google.com/store/apps/details?id=" + url2.substring("id=".length() + url2.indexOf("id="));
                    this.mMarketUrl = url2;
                }
            }
        }

        public String getBrowserUrl() {
            return this.mBrowserUrl;
        }

        public String getMarketUrl() {
            return this.mMarketUrl;
        }

        public boolean isGPUrl() {
            return this.mIsGPUrl;
        }

        public String getOrigin() {
            return this.mOrigin;
        }

        public boolean jump(Context context) {
            if (!isGPUrl()) {
                return false;
            }
            if (GoogleMarketUtils.isMarketExist(context)) {
                GoogleMarketUtils.openGP(context, getMarketUrl());
            } else {
                boolean unused = GoogleMarketUtils.gotoBrowser(context, getBrowserUrl());
            }
            return true;
        }
    }
}

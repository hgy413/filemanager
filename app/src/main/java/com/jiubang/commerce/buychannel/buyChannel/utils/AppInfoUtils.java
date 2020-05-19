package com.jiubang.commerce.buychannel.buyChannel.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.buychannel.BuildConfig;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.utils.AdvertisingIdClient;
import java.security.MessageDigest;

public class AppInfoUtils {
    public static final String GOOGLE_ADVERTING_DEFAULT_ID = "UNABLE-TO-RETRIEVE";
    /* access modifiers changed from: private */
    public static String sGoogleAdvertingId;

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    public static String getAdvertisingId(final Context context) {
        if (!TextUtils.isEmpty(sGoogleAdvertingId)) {
            return sGoogleAdvertingId;
        }
        sGoogleAdvertingId = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getString(BuySdkConstants.GOOGLE_AD_ID, (String) null);
        if (!TextUtils.isEmpty(sGoogleAdvertingId)) {
            return sGoogleAdvertingId;
        }
        new Thread(new Runnable() {
            public void run() {
                String unused = AppInfoUtils.sGoogleAdvertingId = AppInfoUtils.getGoogleAdvertisingId(context);
                if (!TextUtils.isEmpty(AppInfoUtils.sGoogleAdvertingId)) {
                    BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).edit().putString(BuySdkConstants.GOOGLE_AD_ID, AppInfoUtils.sGoogleAdvertingId).commit();
                }
            }
        }, "getAdvertisingId").start();
        return GOOGLE_ADVERTING_DEFAULT_ID;
    }

    /* access modifiers changed from: private */
    public static String getGoogleAdvertisingId(Context context) {
        AdvertisingIdClient.AdInfo info = null;
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable e3) {
            e3.printStackTrace();
        }
        if (info != null) {
            return info.getId();
        }
        return null;
    }

    public static String getLauncherPackageName(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
            if (res.activityInfo != null && !res.activityInfo.packageName.equals("android")) {
                return res.activityInfo.packageName;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isAppExit(Context context, String apkName) {
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = context.getPackageManager().getPackageInfo(apkName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pkgInfo != null) {
            return true;
        }
        return false;
    }

    public static void openAppWithPkgName(Context context, String apkName) {
        try {
            PackageManager pm = context.getPackageManager();
            new Intent();
            context.startActivity(pm.getLaunchIntentForPackage(apkName));
        } catch (Exception e) {
            LogUtils.e((String) null, e.toString());
        }
    }

    public static void printKeyHash(Context context) {
        try {
            for (Signature signature : context.getPackageManager().getPackageInfo("com.jiubang.commerce.gomultiple", 64).signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), 0));
            }
        } catch (Exception e) {
        }
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }
}

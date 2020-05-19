package com.jiubang.commerce.ad.url;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class ReferrerUtil {
    public static final String REF_ACTION = "com.android.vending.INSTALL_REFERRER";
    public static final String REF_KEY = "referrer";

    public static boolean sendGPUrlBroadcast(Context context, String url) {
        Uri uri;
        LogUtils.i(new String[]{"wbq", "sendGPUrlBroadcast:", "url=", url});
        if (context == null || !GoogleMarketUtils.isMarketUrl(url) || (uri = Uri.parse(url)) == null) {
            return false;
        }
        String pkgName = uri.getQueryParameter("id");
        String referrerStr = uri.getQueryParameter(REF_KEY);
        LogUtils.i(new String[]{"wbq", "sendGPRef:", "pkgName=", pkgName, " referrer=", referrerStr});
        Intent intent = getReferIntent(pkgName, referrerStr);
        if (intent == null) {
            return false;
        }
        boolean ret = safelySendBroadcast(context, intent);
        Intent componentIntent = getReferIntent(pkgName, referrerStr);
        ArrayList list = resolveComponents(context, pkgName, componentIntent);
        if (list == null || list.size() <= 0) {
            return ret;
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (!StringUtils.isEmpty(str)) {
                componentIntent.setClassName(pkgName, str);
                safelySendBroadcast(context, componentIntent);
            }
        }
        return ret;
    }

    public static Intent getReferIntent(String packageName, String referrerStr) {
        if (StringUtils.isEmpty(packageName) || StringUtils.isEmpty(referrerStr)) {
            return null;
        }
        Intent intent = new Intent(REF_ACTION);
        intent.putExtra(REF_KEY, referrerStr);
        if (Build.VERSION.SDK_INT >= 12) {
            intent.addFlags(32);
        }
        intent.setPackage(packageName);
        return intent;
    }

    public static boolean safelySendBroadcast(Context context, Intent intent) {
        Context aContext = context != null ? context.getApplicationContext() : null;
        if (aContext == null || intent == null) {
            return false;
        }
        try {
            aContext.sendBroadcast(intent);
            return true;
        } catch (Throwable thr) {
            LogUtils.w("wbq", "Error-", thr);
            thr.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> resolveComponents(Context context, String packageName, Intent intent) {
        if (context == null || StringUtils.isEmpty(packageName) || intent == null) {
            return null;
        }
        ArrayList localArrayList = new ArrayList();
        try {
            for (ResolveInfo info : context.getPackageManager().queryBroadcastReceivers(intent, 0)) {
                if (info.activityInfo != null && packageName.equals(info.activityInfo.packageName)) {
                    localArrayList.add(info.activityInfo.name);
                }
            }
            return localArrayList;
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            return localArrayList;
        }
    }
}

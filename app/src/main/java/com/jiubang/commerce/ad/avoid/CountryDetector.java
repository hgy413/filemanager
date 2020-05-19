package com.jiubang.commerce.ad.avoid;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.io.MultiprocessSharedPreferences;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;

public class CountryDetector implements IAvoidDetector {
    public static final String AVOID_COUNTRY_CODE = "CN";
    static final String CC = "cc";
    static final String NOAD = "noad";
    private String mCC = getCountryCodeFromLocal(this.mContext);
    private Context mContext;

    public CountryDetector(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void detect(Object... params) {
        String ipCountry = null;
        Integer noad = null;
        if (params != null && params.length > 1) {
            ipCountry = params[0] instanceof String ? StringUtils.toUpperCase(params[0]) : null;
            noad = params[1] instanceof Integer ? params[1] : null;
        }
        if (TextUtils.isEmpty(this.mCC)) {
            String deviceCountry = getDeviceCountryCode();
            log("ipCountry=", ipCountry, " deviceCountry=", deviceCountry, " noad=", "" + noad);
            if (AVOID_COUNTRY_CODE.equals(ipCountry) || AVOID_COUNTRY_CODE.equals(deviceCountry)) {
                this.mCC = AVOID_COUNTRY_CODE;
                saveCountryCode2Local(this.mContext, AVOID_COUNTRY_CODE);
            }
        } else {
            log("no need", " ipCountry=", ipCountry, " noad=", "" + noad);
        }
        if (noad != null) {
            saveNoad2Local(this.mContext, noad.intValue());
        }
    }

    public boolean shouldAvoid() {
        return AVOID_COUNTRY_CODE.equals(getCountryCodeFromLocal(this.mContext));
    }

    public boolean isNoad() {
        return 1 == getNoadFromLocal(this.mContext);
    }

    private String getDeviceCountryCode() {
        return StringUtils.toUpperCase(SystemUtils.getLocal(this.mContext));
    }

    private void log(String... msg) {
        if (LogUtils.isShowLog() && msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder("CountryDetector:");
            for (String m : msg) {
                sb.append(m);
            }
            LogUtils.d("Ad_SDK", sb.toString());
        }
    }

    static String getCountryCodeFromLocal(Context context) {
        return getSP(context).getString(CC, (String) null);
    }

    static void saveCountryCode2Local(Context context, String code) {
        if (!StringUtils.isEmpty(code)) {
            SharedPreferences sp = getSP(context);
            if (!code.equals(sp.getString(CC, (String) null))) {
                sp.edit().putString(CC, code).commit();
            }
        }
    }

    static int getNoadFromLocal(Context context) {
        return getSP(context).getInt("noad", 0);
    }

    static void saveNoad2Local(Context context, int noad) {
        SharedPreferences sp = getSP(context);
        if (sp.getInt("noad", 0) != noad) {
            sp.edit().putInt("noad", noad).commit();
        }
    }

    private static SharedPreferences getSP(Context context) {
        return MultiprocessSharedPreferences.getSharedPreferences(context, "adsdk_avoider", 0);
    }
}

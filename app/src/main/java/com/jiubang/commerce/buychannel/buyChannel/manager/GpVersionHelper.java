package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.jiubang.commerce.buychannel.BuildConfig;

public class GpVersionHelper {
    private static GpVersionHelper sInstance;
    private Context mContext;
    private PackageManager mPackageManager;

    public static GpVersionHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (GpVersionHelper.class) {
                if (sInstance == null) {
                    sInstance = new GpVersionHelper(context);
                }
            }
        }
        return sInstance;
    }

    private GpVersionHelper(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mPackageManager = this.mContext.getPackageManager();
    }

    public String getGpVersionName() {
        String versionName;
        try {
            synchronized (this.mContext) {
                versionName = this.mPackageManager.getPackageInfo("com.android.vending", 8192).versionName;
            }
            String str = versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            Object obj = BuildConfig.FLAVOR;
            return BuildConfig.FLAVOR;
        }
    }

    public boolean campareWhitGpVersion6_8_24() {
        String versionName = getGpVersionName();
        if (TextUtils.isEmpty(versionName) || !versionName.contains(".")) {
            return false;
        }
        String[] versionSp = versionName.split("\\.");
        int[] v6_8_24 = {6, 8, 24};
        try {
            if (versionSp.length < 1) {
                return false;
            }
            int a = Integer.parseInt(versionSp[0]);
            if (a > v6_8_24[0]) {
                return true;
            }
            if (a < v6_8_24[0] || versionSp.length < 2) {
                return false;
            }
            int b = Integer.parseInt(versionSp[1]);
            if (b > v6_8_24[1]) {
                return true;
            }
            if (b < v6_8_24[1] || versionSp.length < 3 || Integer.parseInt(versionSp[2]) < v6_8_24[2]) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

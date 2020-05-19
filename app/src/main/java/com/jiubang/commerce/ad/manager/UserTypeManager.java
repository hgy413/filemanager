package com.jiubang.commerce.ad.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class UserTypeManager {
    private static final String USER_TYPE_SETTING = "USER_TYPE_SETTING";
    private static UserTypeManager sInstance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public static UserTypeManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdSdkSetting.class) {
                if (sInstance == null) {
                    sInstance = new UserTypeManager(context);
                }
            }
        }
        return sInstance;
    }

    private UserTypeManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        if (Build.VERSION.SDK_INT >= 11) {
            this.mSharedPreferences = this.mContext.getSharedPreferences(USER_TYPE_SETTING, 4);
        } else {
            this.mSharedPreferences = this.mContext.getSharedPreferences(USER_TYPE_SETTING, 0);
        }
    }

    public long getLastUserTypeUpdateTime(String buychannel) {
        return 1;
    }
}

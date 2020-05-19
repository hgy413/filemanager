package com.jiubang.commerce.ad.params;

import android.content.Context;
import android.content.SharedPreferences;
import com.jb.ga0.commerce.util.io.MultiprocessSharedPreferences;

public class ClientParams {
    static final String KEY_BUY_CHANNEL = "buyChannel";
    static final String KEY_INSTALL_TIME = "installTime";
    static final String KEY_IS_UPGRADE = "isUpgrade";
    private String mBuyChannel;
    private long mInstalledTime;
    private boolean mIsUpgrade;

    public ClientParams(String buyChannel, long installTime, boolean isUpgrade) {
        this.mBuyChannel = buyChannel;
        this.mInstalledTime = installTime;
        this.mIsUpgrade = isUpgrade;
    }

    public String getBuyChannel() {
        return this.mBuyChannel;
    }

    public long getInstalledTime() {
        return this.mInstalledTime;
    }

    public int getCDays() {
        return (int) Math.max(1, (System.currentTimeMillis() - this.mInstalledTime) / 86400000);
    }

    public boolean getIsUpgrade() {
        return this.mIsUpgrade;
    }

    public static void save2Local(Context context, ClientParams param) {
        if (param != null) {
            getSP(context).edit().putString(KEY_BUY_CHANNEL, param.getBuyChannel()).putLong("installTime", Math.max(1, param.getInstalledTime())).putBoolean(KEY_IS_UPGRADE, param.getIsUpgrade()).commit();
        }
    }

    public static ClientParams getFromLocal(Context context) {
        SharedPreferences sp = getSP(context);
        return new ClientParams(sp.getString(KEY_BUY_CHANNEL, (String) null), sp.getLong("installTime", 1), sp.getBoolean(KEY_IS_UPGRADE, false));
    }

    static SharedPreferences getSP(Context context) {
        return MultiprocessSharedPreferences.getSharedPreferences(context, "adsdk_client_params", 0);
    }
}

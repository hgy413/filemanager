package com.jiubang.commerce.ad.intelligent.api;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.jb.ga0.commerce.util.LogUtils;

public class NativePreHelper {
    private static final String SP_FILE = "intelligentConfig";
    private static final String SP_KEY = "config";

    public static void startNativeAdPresolve(Context context, String title, String adPos) {
        passDyStartServiceWithCommand(context, IntelligentApi.COMMAND_NATIVE_AD_PRESOLVE, new String[]{title, adPos});
    }

    public static void passDyStartServiceWithCommand(Context context, String command, String[] param) {
        if (context != null && isAlreadyRunning(context) && isEnabled(context)) {
            Intent intent = getServiceIntent(context);
            intent.putExtra(IntelligentApi.COMMAND, command);
            if (param != null) {
                intent.putExtra(IntelligentApi.COMMAND_PARAM, param);
            }
            try {
                context.startService(intent);
            } catch (Exception e) {
                LogUtils.w(IntelligentApi.TAG, "passDyStartServiceWithCommand error:", e);
            }
        }
    }

    public static boolean isAlreadyRunning(Context context) {
        try {
            for (ActivityManager.RunningServiceInfo info : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
                String process = info.process;
                if (process != null && process.trim().endsWith(IntelligentApi.PROCESS_SUFFIX)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            LogUtils.e(IntelligentApi.TAG, "NativePreHelper-isAlreadyRunning Error:", e);
            return true;
        }
    }

    private static Intent getServiceIntent(Context context) {
        return new Intent().setComponent(new ComponentName(context, "com.jiubang.commerce.service.IntelligentPreloadService"));
    }

    private static boolean isEnabled(Context context) {
        return context.getSharedPreferences(SP_FILE, 4).getBoolean(SP_KEY, true);
    }
}

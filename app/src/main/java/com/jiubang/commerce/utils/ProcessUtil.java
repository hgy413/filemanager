package com.jiubang.commerce.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import com.jb.ga0.commerce.util.LogUtils;
import java.util.List;

public class ProcessUtil {
    public static void killSelf() {
        Process.killProcess(Process.myPid());
    }

    public static void killProcWithSuffix(Context context, String suffix) {
        try {
            for (ActivityManager.RunningServiceInfo info : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
                String process = info.process;
                LogUtils.d("wbq", "service process:" + process);
                if (process != null && process.trim().equals(getFullProcessName(context, suffix))) {
                    LogUtils.d("wbq", "kill process:" + process);
                    Process.killProcess(info.pid);
                    return;
                }
            }
        } catch (Exception e) {
            LogUtils.w("wbq", "isOthersRunning Error:", e);
        }
    }

    public static String getFullProcessName(Context context, String suffix) {
        return context.getPackageName() + ":" + suffix;
    }

    public static String getCurrentProcessName(Context context) {
        int pid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> infos = ((ActivityManager) context.getApplicationContext().getSystemService("activity")).getRunningAppProcesses();
        if (infos == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo process : infos) {
            if (process.pid == pid) {
                return process.processName;
            }
        }
        return "";
    }

    public static boolean isMainProcess(Context context) {
        return context.getApplicationContext().getPackageName().equals(getCurrentProcessName(context));
    }
}

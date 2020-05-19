package com.gau.utils.net.util;

public class NetLog {
    public static final String LOG_LABEL = "netLog";
    private static boolean sIsPrintLog = false;

    public static boolean isPrintLog() {
        return sIsPrintLog;
    }

    public static void printLog(boolean printlog) {
        sIsPrintLog = printlog;
    }

    public static void info(String info, Throwable tr) {
        if (sIsPrintLog) {
        }
    }

    public static void erro(String info, Throwable tr) {
        if (sIsPrintLog) {
        }
    }

    public static void debug(String info, Throwable tr) {
        if (sIsPrintLog) {
        }
    }
}

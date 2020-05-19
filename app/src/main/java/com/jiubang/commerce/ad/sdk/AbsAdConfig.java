package com.jiubang.commerce.ad.sdk;

public abstract class AbsAdConfig {
    static final long DEFAULT_TIMEOUT = 30000;
    public long mTimeOut = DEFAULT_TIMEOUT;

    public static long getTimeOut(AbsAdConfig config) {
        if (config != null) {
            return Math.max(1, config.mTimeOut);
        }
        return DEFAULT_TIMEOUT;
    }
}

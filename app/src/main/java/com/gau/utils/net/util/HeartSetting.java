package com.gau.utils.net.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartSetting {
    public static final long DEFAULT_HEART_TIME_INTERVAL = 10000;
    public static final long HEART_TIME_NO_SETTING = -1;
    private long mCommonHeartTime = -1;
    private Map<String, String> mHeartMap = new ConcurrentHashMap();

    public String putHeartUrl(String host, String heartUrl) {
        return this.mHeartMap.put(host, heartUrl);
    }

    public String removeHeartUrl(String host) {
        return this.mHeartMap.remove(host);
    }

    public String getHeartUrl(String host) {
        return this.mHeartMap.get(host);
    }

    public Map<String, String> getAllHeartUrl() {
        return this.mHeartMap;
    }

    public void setCommonHeartTime(long heartTime) {
        this.mCommonHeartTime = heartTime;
    }

    public long getCommonHeartTime() {
        return this.mCommonHeartTime;
    }
}

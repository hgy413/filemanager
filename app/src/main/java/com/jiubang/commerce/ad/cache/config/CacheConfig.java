package com.jiubang.commerce.ad.cache.config;

import android.content.Context;
import android.util.SparseIntArray;
import com.jiubang.commerce.ad.manager.AdSdkManager;

public class CacheConfig {
    static final int DEFAULT_THREAD_NUM = 2;
    protected SparseIntArray mLimitNum = new SparseIntArray();
    protected int mMaxTotalLimitNum = 5;
    protected int mThreadNum = 2;
    protected int mVMID = -1;

    public enum AdCacheFlag {
        NONE,
        HIGH_ECPM,
        HIGH_FILL,
        ADMOB_LOW,
        BANNER,
        FULL_SCREEN,
        ADMOB_BANNER
    }

    public CacheConfig() {
        this.mLimitNum.put(AdCacheFlag.HIGH_ECPM.ordinal(), 1);
        this.mLimitNum.put(AdCacheFlag.HIGH_FILL.ordinal(), 1);
        this.mLimitNum.put(AdCacheFlag.ADMOB_LOW.ordinal(), 1);
        this.mLimitNum.put(AdCacheFlag.BANNER.ordinal(), 1);
        this.mLimitNum.put(AdCacheFlag.FULL_SCREEN.ordinal(), 1);
        this.mLimitNum.put(AdCacheFlag.ADMOB_BANNER.ordinal(), 1);
    }

    public int getVMID() {
        return this.mVMID;
    }

    public int getThreadNum() {
        return this.mThreadNum;
    }

    public SparseIntArray getLimitNum() {
        return this.mLimitNum;
    }

    public int getMaxTotalLimitNum() {
        return this.mMaxTotalLimitNum;
    }

    public boolean isValid() {
        return this.mVMID > 0;
    }

    public static CacheConfig createConfig(Context context) {
        String entranceId = AdSdkManager.getInstance().getEntranceId();
        String cid = AdSdkManager.getInstance().getCid();
        if ("4".equals(cid)) {
            return new GoKeyboardConfig(entranceId);
        }
        if ("37".equals(cid)) {
            return new GoSecurityConfig(entranceId);
        }
        if ("15".equals(cid)) {
            return new ZeroSpeedConfig(entranceId);
        }
        if ("6".equals(cid)) {
            return new GoSmsConfig(entranceId);
        }
        if ("21".equals(cid)) {
            return new ZeroCameraConfig(entranceId);
        }
        return null;
    }

    public static class GoKeyboardConfig extends CacheConfig {
        public GoKeyboardConfig(String entranceId) {
            this.mVMID = "2".equals(entranceId) ? 1896 : -1;
        }
    }

    public static class GoSecurityConfig extends CacheConfig {
        public GoSecurityConfig(String entranceId) {
            this.mVMID = "1".equals(entranceId) ? 2250 : -1;
            this.mLimitNum = new SparseIntArray();
            this.mLimitNum.put(AdCacheFlag.HIGH_ECPM.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.HIGH_FILL.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.ADMOB_LOW.ordinal(), 1);
        }
    }

    public static class ZeroSpeedConfig extends CacheConfig {
        public ZeroSpeedConfig(String entranceId) {
            this.mVMID = "1".equals(entranceId) ? 2248 : -1;
            this.mLimitNum = new SparseIntArray();
            this.mLimitNum.put(AdCacheFlag.HIGH_ECPM.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.HIGH_FILL.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.ADMOB_LOW.ordinal(), 1);
        }
    }

    public static class GoSmsConfig extends CacheConfig {
        public GoSmsConfig(String entranceId) {
            this.mVMID = "1".equals(entranceId) ? 2252 : -1;
            this.mLimitNum = new SparseIntArray();
            this.mLimitNum.put(AdCacheFlag.HIGH_ECPM.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.HIGH_FILL.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.ADMOB_LOW.ordinal(), 1);
        }
    }

    public static class ZeroCameraConfig extends CacheConfig {
        public ZeroCameraConfig(String entranceId) {
            this.mVMID = "1".equals(entranceId) ? 2254 : -1;
            this.mLimitNum = new SparseIntArray();
            this.mLimitNum.put(AdCacheFlag.HIGH_ECPM.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.HIGH_FILL.ordinal(), 1);
            this.mLimitNum.put(AdCacheFlag.ADMOB_LOW.ordinal(), 1);
        }
    }
}

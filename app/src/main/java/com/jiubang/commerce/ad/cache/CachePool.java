package com.jiubang.commerce.ad.cache;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.cache.config.CacheConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CachePool {
    private CacheConfig mCacheConfig;
    private SparseArray<List<AdCacheBean>> mCachedAds;
    private ExecutorService mExecutorService;
    private byte[] mMUTEX = new byte[0];

    /* access modifiers changed from: protected */
    public abstract void adjustCache();

    public abstract AdCacheBean getAd(int i);

    public CachePool(Context context) {
        this.mCacheConfig = CacheConfig.createConfig(context);
        if (isConfigValid()) {
            init(context);
        } else {
            printLog("Cache not supported");
        }
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        this.mCachedAds = new SparseArray<>();
        this.mExecutorService = Executors.newFixedThreadPool(this.mCacheConfig.getThreadNum());
    }

    public int getCapacity(int tag) {
        int size;
        synchronized (this.mMUTEX) {
            List<AdCacheBean> list = this.mCachedAds.get(tag);
            size = list != null ? list.size() : 0;
        }
        return size;
    }

    public int getTotalCapactity() {
        int total;
        synchronized (this.mMUTEX) {
            total = 0;
            for (int i = 0; i < this.mCachedAds.size(); i++) {
                List<AdCacheBean> list = this.mCachedAds.valueAt(i);
                total += list != null ? list.size() : 0;
            }
        }
        return total;
    }

    public void cleanCache() {
        synchronized (this.mMUTEX) {
            for (int i = 0; i < this.mCachedAds.size(); i++) {
                List<AdCacheBean> list = this.mCachedAds.valueAt(i);
                if (list != null) {
                    for (AdCacheBean b : list) {
                        b.destroy();
                    }
                    list.clear();
                }
            }
            this.mCachedAds.clear();
        }
    }

    public FilterTagResult retriveFilterTags() {
        int limitSize;
        FilterTagResult result = new FilterTagResult();
        ArrayList<Integer> tags = new ArrayList<>();
        synchronized (this.mMUTEX) {
            SparseIntArray limits = this.mCacheConfig.getLimitNum();
            limitSize = limits.size();
            for (int i = 0; i < limitSize; i++) {
                int tag = limits.keyAt(i);
                List<AdCacheBean> list = this.mCachedAds.get(tag);
                int size = list != null ? list.size() : 0;
                if (size >= limits.get(tag)) {
                    tags.add(Integer.valueOf(tag));
                } else {
                    result.mNeedNum += limits.get(tag) - size;
                }
            }
        }
        if (tags.size() != limitSize) {
            int[] tmp = new int[tags.size()];
            for (int i2 = 0; i2 < tags.size(); i2++) {
                tmp[i2] = tags.get(i2).intValue();
            }
            result.mFilterTags = tmp;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public boolean isConfigValid() {
        if (this.mCacheConfig != null) {
            return this.mCacheConfig.isValid();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void appendAd2Cache(AdCacheBean ad) {
        if (ad != null) {
            printLog("append1Ad2Cache tag=" + ad.getTag());
            synchronized (this.mMUTEX) {
                int tag = ad.getTag();
                List<AdCacheBean> list = this.mCachedAds.get(tag);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(ad);
                this.mCachedAds.put(tag, list);
            }
        }
    }

    /* access modifiers changed from: protected */
    public AdCacheBean popAdFromCache(int tag) {
        AdCacheBean remove;
        synchronized (this.mMUTEX) {
            List<AdCacheBean> list = this.mCachedAds.get(tag);
            remove = (list == null || list.isEmpty()) ? null : list.remove(0);
        }
        return remove;
    }

    /* access modifiers changed from: protected */
    public void checkAdValidation() {
        printLog("checkAdValidation");
        synchronized (this.mMUTEX) {
            for (int i = 0; i < this.mCachedAds.size(); i++) {
                List<AdCacheBean> list = this.mCachedAds.valueAt(i);
                if (list != null) {
                    Iterator<AdCacheBean> iter = list.iterator();
                    while (iter.hasNext()) {
                        if (!iter.next().isValid()) {
                            iter.remove();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public CacheConfig getCacheConfig() {
        return this.mCacheConfig;
    }

    /* access modifiers changed from: protected */
    public void execute(Runnable runnable) {
        this.mExecutorService.execute(runnable);
    }

    public static void printLog(String str) {
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", "AdCachePool:" + str);
        }
    }

    public static class FilterTagResult {
        public int[] mFilterTags = null;
        public int mNeedNum = 0;

        public boolean needLoadAd() {
            return this.mNeedNum > 0;
        }
    }
}

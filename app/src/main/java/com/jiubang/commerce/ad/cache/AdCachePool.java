package com.jiubang.commerce.ad.cache;

import android.content.Context;
import android.content.IntentFilter;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jb.ga0.commerce.util.CustomAlarmManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.alarm.AlarmConstant;
import com.jiubang.commerce.ad.bean.AdModuleInfoBean;
import com.jiubang.commerce.ad.cache.CachePool;
import com.jiubang.commerce.ad.cache.LoadAdTask;
import com.jiubang.commerce.ad.cache.config.CacheAdConfig;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.receiver.NetWorkDynamicBroadcastReceiver;
import com.jiubang.commerce.thread.AdSdkThreadExecutorProxy;
import com.jiubang.commerce.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;

public class AdCachePool extends CachePool implements CustomAlarm.OnAlarmListener, NetWorkDynamicBroadcastReceiver.INetWorkListener, LoadAdTask.ILoadTask {
    static final int MAX_CONCURRENT_NUM = 1;
    static final int MAX_CONCURRENT_TASK_NUM = 1;
    private static AdCachePool sInstance;
    /* access modifiers changed from: private */
    public CacheAdConfig mCacheAdConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mEnabled = true;
    private FailureObserver mFailureObserver;
    private boolean mIsConcurrentLoadAd;
    private byte[] mMUTEX = new byte[0];
    private NetWorkDynamicBroadcastReceiver mReceiver;
    private List<LoadAdTask> mRunngTask;

    private AdCachePool(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        super.init(context);
        this.mContext = context.getApplicationContext();
        this.mRunngTask = new ArrayList();
        this.mFailureObserver = new FailureObserver();
        CustomAlarmManager.getInstance(this.mContext).getAlarm(AlarmConstant.MODULE_NAME).alarmRepeat(1, 2000, 43200000, true, this);
        this.mReceiver = new NetWorkDynamicBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this.mReceiver, filter);
        AdSdkThreadExecutorProxy.runOnMainThread(new Runnable() {
            public void run() {
                NetWorkDynamicBroadcastReceiver.registerListener(AdCachePool.this);
            }
        }, 5000);
        printLog("initialzed");
    }

    public static AdCachePool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdCachePool.class) {
                if (sInstance == null) {
                    AdCachePool pool = new AdCachePool(context);
                    if (pool.isConfigValid()) {
                        sInstance = pool;
                    }
                }
            }
        }
        return sInstance;
    }

    public static AdModuleInfoBean getCacheAd(Context context, AdSdkParamsBuilder b, BaseModuleDataItemBean moduleDataItemBean) {
        boolean z = false;
        AdCacheBean ad = null;
        if (b.mApplyAdCache) {
            int tag = moduleDataItemBean.getAdCacheFlag();
            int vmid = moduleDataItemBean.getVirtualModuleId();
            String[] fbids = moduleDataItemBean.getFbIds();
            String adid = (fbids == null || fbids.length <= 0) ? "" : fbids[0];
            AdCachePool pool = getInstance(context);
            if (pool != null) {
                ad = pool.getAd(tag);
            } else {
                ad = null;
            }
            if (LogUtils.isShowLog()) {
                StringBuilder append = new StringBuilder().append("[vmId:").append(vmid).append("]").append("adid=").append(adid).append(" getAd tag:").append(tag).append(" result:");
                if (ad != null) {
                    z = true;
                }
                printLog(append.append(z).toString());
            }
        }
        if (ad == null) {
            return null;
        }
        ad.setClientAdSdkParams(b);
        return ad.getAdBean();
    }

    public AdCacheBean getAd(int tag) {
        AdCacheBean ad = popAdFromCache(tag);
        if (ad != null) {
            adjustCache();
        }
        return ad;
    }

    /* access modifiers changed from: protected */
    public void adjustCache() {
        if (!this.mEnabled) {
            printLog("adjustCache--not enabled");
        } else if (!checkRunningTaskNum()) {
            printLog("adjustCache--Running Task too much");
        } else if (!NetworkUtils.isNetworkOK(this.mContext) || !this.mFailureObserver.good2Go()) {
            printLog("adjustCache--network not ok or fail too much");
        } else {
            printLog("adjustCache");
            execute(new Runnable() {
                public void run() {
                    CachePool.FilterTagResult result = AdCachePool.this.retriveFilterTags();
                    if (result.needLoadAd()) {
                        int count = AdCachePool.this.getAvailableCount(result);
                        if (LogUtils.isShowLog()) {
                            CachePool.printLog("adjustCache:count=" + count);
                        }
                        for (int i = 0; i < count; i++) {
                            LoadAdTask task = new LoadAdTask(AdCachePool.this.mContext, result.mFilterTags, AdCachePool.this, AdCachePool.this.mCacheAdConfig, AdCachePool.this);
                            AdCachePool.this.recordTask(task);
                            task.run();
                        }
                        return;
                    }
                    CachePool.printLog("adjustCache:no need to load ad");
                }
            });
        }
    }

    public void onAlarm(int alarmId) {
        checkAdValidation();
        adjustCache();
    }

    public void onNetworkChanged(boolean isAvailable) {
        printLog("onNetworkChanged:" + isAvailable);
        if (isAvailable) {
            adjustCache();
        }
    }

    public void onTaskComplete(LoadAdTask task) {
        this.mFailureObserver.noted(task);
        removeTask(task);
        adjustCache();
    }

    public void setConcurrentLoadAd(boolean b) {
        this.mIsConcurrentLoadAd = b;
    }

    public void setCacheAdConfig(CacheAdConfig adConfig) {
        this.mCacheAdConfig = adConfig;
    }

    public void informClientLoadAdBean() {
        adjustCache();
    }

    public void setStatus(boolean enable, boolean cleanCache) {
        this.mEnabled = enable;
        if (cleanCache) {
            cleanCache();
        }
    }

    /* access modifiers changed from: private */
    public void recordTask(LoadAdTask task) {
        if (task != null) {
            synchronized (this.mMUTEX) {
                this.mRunngTask.add(task);
            }
        }
    }

    private void removeTask(LoadAdTask task) {
        synchronized (this.mMUTEX) {
            this.mRunngTask.remove(task);
        }
    }

    private boolean checkRunningTaskNum() {
        int size;
        synchronized (this.mMUTEX) {
            size = this.mRunngTask.size();
        }
        if (size < 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public int getAvailableCount(CachePool.FilterTagResult result) {
        int i = 1;
        if (this.mIsConcurrentLoadAd) {
            synchronized (this.mMUTEX) {
                i = Math.max(Math.min(result.mNeedNum - this.mRunngTask.size(), 1), 1);
            }
        }
        return i;
    }

    static class FailureObserver {
        static final int MAX_COUNT = 10;
        private int mContinuousFailCount = 0;
        private int mCount = 0;
        private long mTime = 0;

        FailureObserver() {
        }

        public void noted(LoadAdTask task) {
            this.mContinuousFailCount = task.isSuccess() ? 0 : this.mContinuousFailCount + 1;
            int num = this.mContinuousFailCount / 10;
            if (num > this.mCount) {
                this.mTime = System.currentTimeMillis();
            }
            if (num == this.mCount) {
                num = this.mCount;
            }
            this.mCount = num;
        }

        public boolean good2Go() {
            if (LogUtils.isShowLog()) {
                CachePool.printLog("ContinuousFailCount=" + this.mContinuousFailCount);
            }
            return this.mContinuousFailCount < 10 || Math.abs(System.currentTimeMillis() - this.mTime) > 3600000;
        }
    }
}

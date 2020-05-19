package com.jiubang.commerce.ad.abtest;

import android.content.Context;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jiubang.commerce.ad.abtest.AbTestHttpHandler;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.utils.AlarmProxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ABTestManager {
    public static final String BID_FB_PRESOLVE = "91";
    @Deprecated
    public static final String BID_INSTALL_PRESOLVE = "130";
    public static final String BID_INTELLIGENT = "143";
    public static final String TAG = "AdSdkABTest";
    private static ABTestManager sInstance;
    /* access modifiers changed from: private */
    public CachedAbBean mCachedAbBean;
    /* access modifiers changed from: private */
    public Context mContext;
    private List<IABTestConfigListener> mListeners;
    private byte[] mLock = new byte[0];
    private UpdateTask mUpdateTask;

    public interface IABTestConfigListener {
        void onABTestUpdate();
    }

    public static ABTestManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ABTestManager.class) {
                if (sInstance == null) {
                    sInstance = new ABTestManager(context);
                }
            }
        }
        return sInstance;
    }

    public AbBean getAbBean(String bid) {
        if ("91".equals(bid)) {
            return this.mCachedAbBean.getFbNativeAbBean();
        }
        if ("130".equals(bid)) {
            return this.mCachedAbBean.getInstallAbBean();
        }
        if ("143".equals(bid)) {
            return this.mCachedAbBean.getIntelligentAbBean();
        }
        return null;
    }

    public void register(IABTestConfigListener listener) {
        if (listener != null) {
            synchronized (this.mLock) {
                if (!this.mListeners.contains(listener)) {
                    if (this.mCachedAbBean.isValid()) {
                        listener.onABTestUpdate();
                    }
                    this.mListeners.add(listener);
                }
            }
        }
    }

    public void unregister(IABTestConfigListener listener) {
        if (listener != null) {
            synchronized (this.mLock) {
                if (this.mListeners.contains(listener)) {
                    this.mListeners.remove(listener);
                }
            }
        }
    }

    public double getFrequency() {
        AbBean bean = getAbBean("91");
        if (bean == null) {
            bean = new AbBean((String) null);
        }
        return bean.getFrequency();
    }

    public String getFBId() {
        AbBean bean = getAbBean("91");
        if (bean == null) {
            bean = new AbBean((String) null);
        }
        return bean.getTestId();
    }

    /* access modifiers changed from: private */
    public void informUpdate() {
        synchronized (this.mLock) {
            for (IABTestConfigListener onABTestUpdate : this.mListeners) {
                onABTestUpdate.onABTestUpdate();
            }
        }
    }

    private ABTestManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mUpdateTask = new UpdateTask();
        this.mCachedAbBean = new CachedAbBean();
        this.mCachedAbBean.refreshAbBeanFromLocal(this.mContext);
        this.mListeners = new CopyOnWriteArrayList();
        check();
    }

    private void check() {
        long delay;
        long leftValidDuration = this.mCachedAbBean.getLeftValidDuration();
        if (leftValidDuration > 0) {
            delay = leftValidDuration;
        } else {
            delay = 0;
        }
        if (delay > 0) {
            informUpdate();
        }
        setAlarm(delay);
    }

    private void setAlarm(long triggerDelay) {
        AlarmProxy.getAlarm(this.mContext).cancelAarm(1);
        AlarmProxy.getAlarm(this.mContext).alarmRepeat(1, triggerDelay, CachedAbBean.getValidDuration(), true, this.mUpdateTask);
    }

    class UpdateTask implements CustomAlarm.OnAlarmListener, Runnable, AbTestHttpHandler.IABTestHttpListener {
        UpdateTask() {
        }

        public void onAlarm(int alarmId) {
            new AdSdkThread("ABTestUpdateTask", (Runnable) this).start();
        }

        public void run() {
            new AbTestHttpHandler(ABTestManager.this.mContext, "91", this).startRequest();
        }

        public void onFinish(String bid, AbBean bean) {
            if (bean.isSuccess()) {
                ABTestManager.this.mCachedAbBean.update2Local(ABTestManager.this.mContext, bid, bean);
                ABTestManager.this.informUpdate();
            }
        }
    }
}

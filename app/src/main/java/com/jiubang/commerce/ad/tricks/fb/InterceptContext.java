package com.jiubang.commerce.ad.tricks.fb;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.ad.PresolveUtils;
import com.jiubang.commerce.ad.bean.AdInfoBean;
import com.jiubang.commerce.ad.tricks.fb.FbNativeAdTrick;
import com.jiubang.commerce.utils.GoogleMarketUtils;

public class InterceptContext extends ContextWrapper {
    private AdInfoBean mAdInfoBean;
    private FbNativeAdTrick.Plot mPlot;

    public InterceptContext(Context base, FbNativeAdTrick.Plot plot) {
        super(base);
        this.mPlot = plot;
    }

    public Context getApplicationContext() {
        return this;
    }

    @TargetApi(14)
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        getBaseContext().registerComponentCallbacks(callback);
    }

    @TargetApi(14)
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        getBaseContext().unregisterComponentCallbacks(callback);
    }

    public void startActivity(Intent intent) {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "InterceptContext:startActivity:" + intent.toString());
        }
        if (!this.mPlot.isShouldIntercept() || this.mAdInfoBean == null) {
            LogUtils.d("Ad_SDK", "InterceptContext:not startInterceptTask");
            super.startActivity(intent);
            return;
        }
        LogUtils.d("Ad_SDK", "InterceptContext:startInterceptTask");
        new InterceptTask(intent).start(this.mAdInfoBean);
    }

    public void setAdInfoBean(AdInfoBean bean) {
        this.mAdInfoBean = bean;
    }

    /* access modifiers changed from: private */
    public void realStartActivity(Intent intent) {
        getBaseContext().startActivity(intent);
    }

    class InterceptTask implements Runnable, PresolveUtils.IResolveListener {
        private boolean mHadJump = false;
        private boolean mIsTimeOut = false;
        private Intent mOriginIntent;

        public InterceptTask(Intent intent) {
            this.mOriginIntent = intent;
        }

        public void start(AdInfoBean adInfoBean) {
            CustomThreadExecutorProxy.getInstance().runOnAsyncThread(this, 1500);
            PresolveUtils.realClickAdInfoBean(InterceptContext.this.getBaseContext(), adInfoBean, this);
        }

        public void run() {
            setTimeOut(true);
            if (!hadJump()) {
                LogUtils.d("Ad_SDK", "InterceptTask timeout jump old intent");
                setHadJump(true);
                InterceptContext.this.realStartActivity(this.mOriginIntent);
            }
        }

        public void onResolved(String resolvedUrl) {
            LogUtils.d("Ad_SDK", "InterceptTask resolvedUrl=" + resolvedUrl);
            if (hadTimeOut()) {
                LogUtils.d("Ad_SDK", "InterceptTask timeout");
                return;
            }
            GoogleMarketUtils.GPMarketUrlResult gpr = new GoogleMarketUtils.GPMarketUrlResult(resolvedUrl);
            if (gpr.isGPUrl()) {
                LogUtils.d("Ad_SDK", "InterceptTask onResolved Jump GP success");
                setHadJump(true);
                gpr.jump(InterceptContext.this.getBaseContext());
                return;
            }
            LogUtils.d("Ad_SDK", "InterceptTask onResolved not GP URL, jump old intent");
            InterceptContext.this.realStartActivity(this.mOriginIntent);
        }

        private synchronized void setTimeOut(boolean b) {
            this.mIsTimeOut = b;
        }

        private synchronized boolean hadTimeOut() {
            return this.mIsTimeOut;
        }

        private synchronized void setHadJump(boolean b) {
            this.mHadJump = b;
        }

        private synchronized boolean hadJump() {
            return this.mHadJump;
        }
    }
}

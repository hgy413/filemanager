package com.jiubang.commerce.ad.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import com.jb.ga0.commerce.util.LogUtils;
import java.lang.ref.WeakReference;

public abstract class SdkAdContext extends ContextWrapper {
    private WeakReference<Activity> mActivityWeRef;

    public abstract boolean needPassActivity2FbNativeAd();

    public SdkAdContext(Context base, Activity activity) {
        super(base);
        if (activity != null) {
            this.mActivityWeRef = new WeakReference<>(activity);
        }
    }

    public Activity getActivity() {
        if (this.mActivityWeRef != null) {
            return (Activity) this.mActivityWeRef.get();
        }
        return null;
    }

    public Context getContext() {
        Activity activity = getActivity();
        if (!needPassActivity2FbNativeAd() || activity == null) {
            return this;
        }
        LogUtils.i("Ad_SDK", "SdkAdContext return activity");
        return activity;
    }
}

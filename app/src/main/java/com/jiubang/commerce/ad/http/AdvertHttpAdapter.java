package com.jiubang.commerce.ad.http;

import android.content.Context;
import com.gau.utils.net.HttpAdapter;
import com.gau.utils.net.request.THttpRequest;

public class AdvertHttpAdapter {
    private static volatile AdvertHttpAdapter sInstance = null;
    private HttpAdapter mHttpAdapter = null;

    private AdvertHttpAdapter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.mHttpAdapter = new HttpAdapter(context.getApplicationContext());
        this.mHttpAdapter.setMaxConnectThreadNum(2);
    }

    public static AdvertHttpAdapter getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdvertHttpAdapter.class) {
                if (sInstance == null) {
                    sInstance = new AdvertHttpAdapter(context);
                }
            }
        }
        return sInstance;
    }

    private void recycle() {
        this.mHttpAdapter = null;
    }

    public static void destory() {
        if (sInstance != null) {
            sInstance.recycle();
            sInstance = null;
        }
    }

    public HttpAdapter getHttpAdapter() {
        return this.mHttpAdapter;
    }

    public void addTask(THttpRequest request) {
        if (this.mHttpAdapter != null) {
            this.mHttpAdapter.addTask(request);
        }
    }

    public void addTask(THttpRequest request, boolean isAsync) {
        request.setIsAsync(isAsync);
        request.setIsKeepAlive(false);
        addTask(request);
    }

    public void cancelTask(THttpRequest request) {
        if (this.mHttpAdapter != null) {
            this.mHttpAdapter.cancelTask(request);
        }
    }

    public void cleanup() {
        if (this.mHttpAdapter != null) {
            this.mHttpAdapter.cleanup();
            this.mHttpAdapter = null;
        }
    }

    public void setMaxConnectThreadNum(int num) {
        if (this.mHttpAdapter != null) {
            this.mHttpAdapter.setMaxConnectThreadNum(num);
        }
    }
}

package com.gau.utils.net.connector;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;

public abstract class AbstractConnector implements IConnector {
    protected IConnectListener mConnectObserver;
    protected Context mContext;
    protected boolean mIsIdle;
    protected THttpRequest mRequest;

    public AbstractConnector(THttpRequest httpRequest, Context context) throws IllegalArgumentException {
        this.mIsIdle = true;
        if (httpRequest == null || context == null) {
            throw new IllegalArgumentException("httpRequest==null");
        }
        this.mRequest = httpRequest;
        this.mContext = context;
        this.mConnectObserver = httpRequest.getReceiver();
    }

    public AbstractConnector(THttpRequest httpRequest, IConnectListener connectReceiver, Context context) throws IllegalArgumentException {
        this(httpRequest, context);
        this.mConnectObserver = connectReceiver;
    }

    public THttpRequest getRequset() {
        return this.mRequest;
    }

    public boolean isIdle() {
        return this.mIsIdle;
    }
}

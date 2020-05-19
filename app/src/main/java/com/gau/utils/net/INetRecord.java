package com.gau.utils.net;

import com.gau.utils.net.request.THttpRequest;

public interface INetRecord {
    void onConnectSuccess(THttpRequest tHttpRequest, Object obj, Object obj2);

    void onException(Exception exc, Object obj, Object obj2);

    void onStartConnect(THttpRequest tHttpRequest, Object obj, Object obj2);

    void onTransFinish(THttpRequest tHttpRequest, Object obj, Object obj2);
}

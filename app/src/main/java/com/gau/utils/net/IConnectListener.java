package com.gau.utils.net;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import org.apache.http.HttpResponse;

public interface IConnectListener {
    void onException(THttpRequest tHttpRequest, int i);

    void onException(THttpRequest tHttpRequest, HttpResponse httpResponse, int i);

    void onFinish(THttpRequest tHttpRequest, IResponse iResponse);

    void onStart(THttpRequest tHttpRequest);
}

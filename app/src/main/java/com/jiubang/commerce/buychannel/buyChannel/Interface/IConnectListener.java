package com.jiubang.commerce.buychannel.buyChannel.Interface;

import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;

public interface IConnectListener {
    void onException(THttpRequest tHttpRequest, int i);

    void onFinish(THttpRequest tHttpRequest, IResponse iResponse);

    void onStart(THttpRequest tHttpRequest);
}

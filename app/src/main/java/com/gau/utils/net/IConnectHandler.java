package com.gau.utils.net;

import org.apache.http.HttpRequest;

public interface IConnectHandler {
    void connectAsynchronous();

    void connectSynchronous(HttpRequest httpRequest);
}

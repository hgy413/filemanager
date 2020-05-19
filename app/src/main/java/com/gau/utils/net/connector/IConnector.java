package com.gau.utils.net.connector;

public interface IConnector {
    void cancelCurrentConnect();

    void closeConnect();

    void connect();

    void connectAsynchronous();
}

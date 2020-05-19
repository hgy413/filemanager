package com.jiubang.commerce.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class ConnectionManager {
    public static HttpClient getNewHttpClient(HttpParams params) {
        return new DefaultHttpClient();
    }
}

package com.jiubang.commerce.utils;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class HttpRequestUtils {
    private static final int NETWORK_CONNECTION_TIMEOUT = 30000;
    private static final int NETWORK_SO_TIMEOUT = 30000;

    public static HttpResponse executeHttpRequest(String httpUri) throws IOException, Exception {
        return executeHttpRequest(httpUri, 30000, 30000);
    }

    public static HttpResponse executeHttpRequest(String httpUri, int connectionTimeout, int soTimeout) throws IOException, Exception {
        HttpGet httpRequest = new HttpGet(httpUri);
        try {
            BasicHttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
            HttpConnectionParams.setSoTimeout(httpParameters, 30000);
            try {
                return new DefaultHttpClient(httpParameters).execute(httpRequest);
            } catch (Exception e) {
                return ConnectionManager.getNewHttpClient(httpParameters).execute(httpRequest);
            }
        } catch (Exception e2) {
            throw e2;
        }
    }
}

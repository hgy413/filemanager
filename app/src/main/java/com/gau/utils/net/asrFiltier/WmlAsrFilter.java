package com.gau.utils.net.asrFiltier;

import org.apache.http.HttpMessage;

public class WmlAsrFilter implements IAsrFilter {
    public boolean isAsrResponse(HttpMessage httpMsg) {
        String value = httpMsg.getFirstHeader("Content-Type").getValue();
        return value != null && value.indexOf("text/vnd.wap.wml") >= 0;
    }
}

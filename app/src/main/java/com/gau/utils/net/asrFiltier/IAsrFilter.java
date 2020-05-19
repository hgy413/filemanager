package com.gau.utils.net.asrFiltier;

import org.apache.http.HttpMessage;

public interface IAsrFilter {
    boolean isAsrResponse(HttpMessage httpMessage);
}

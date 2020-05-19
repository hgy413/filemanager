package com.gau.utils.net;

import java.io.IOException;

public class NetException extends IOException {
    public int mErrorCode = 0;

    public NetException(int errorCode) {
        this.mErrorCode = errorCode;
    }
}

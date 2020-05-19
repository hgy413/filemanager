package com.gau.utils.net.response;

public interface IResponse {
    public static final int RESPONSE_TYPE_BYTEARRAY = 1;
    public static final int RESPONSE_TYPE_EXCEPTION = -1;
    public static final int RESPONSE_TYPE_JSONARRAY = 5;
    public static final int RESPONSE_TYPE_JSONOBJECT = 4;
    public static final int RESPONSE_TYPE_STREAM = 2;
    public static final int RESPONSE_TYPE_STRING = 3;

    Object getResponse();

    int getResponseType();
}

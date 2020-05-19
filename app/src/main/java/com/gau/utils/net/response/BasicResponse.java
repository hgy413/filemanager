package com.gau.utils.net.response;

public class BasicResponse implements IResponse {
    private Object mResponse;
    private int mResponseType;

    public BasicResponse(int type, Object response) {
        this.mResponseType = type;
        this.mResponse = response;
    }

    public int getResponseType() {
        return this.mResponseType;
    }

    public Object getResponse() {
        return this.mResponse;
    }
}

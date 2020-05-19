package com.jiubang.commerce.ad.sdk;

public class SdkAdProxy {
    public static SdkAdSourceInterface getInstance() {
        return SdkAdSourceListener.getInstance();
    }
}

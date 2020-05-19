package com.jiubang.commerce.buychannel.buyChannel.utils;

import com.jiubang.commerce.buychannel.BuildConfig;

public class TextUtils {
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.equals(BuildConfig.FLAVOR)) {
            return true;
        }
        return false;
    }
}

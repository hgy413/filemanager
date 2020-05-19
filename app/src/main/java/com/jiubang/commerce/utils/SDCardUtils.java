package com.jiubang.commerce.utils;

import android.os.Environment;

public class SDCardUtils {
    public static boolean isSDCardAvaiable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
}

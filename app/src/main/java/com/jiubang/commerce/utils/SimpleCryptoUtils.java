package com.jiubang.commerce.utils;

import android.text.TextUtils;

public class SimpleCryptoUtils {
    private static final String HEX = "0123456789ABCDEF";

    public static String toHex(String txt) {
        return TextUtils.isEmpty(txt) ? txt : toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return TextUtils.isEmpty(hex) ? hex : new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(i * 2, (i * 2) + 2), 16).byteValue();
        }
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(buf.length * 2);
        for (byte appendHex : buf) {
            appendHex(result, appendHex);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 15)).append(HEX.charAt(b & 15));
    }
}

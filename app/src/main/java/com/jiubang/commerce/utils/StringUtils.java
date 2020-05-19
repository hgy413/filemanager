package com.jiubang.commerce.utils;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class StringUtils {
    private static final String DEFAULT_CHARSET_UTF8 = "UTF-8";

    public static String toUpperCase(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim().toUpperCase();
    }

    public static String toLowerCase(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim().toLowerCase();
    }

    public static Integer toInteger(Object srcStr, Integer defaultValue) {
        if (srcStr == null) {
            return defaultValue;
        }
        try {
            if (!isInt(srcStr)) {
                return defaultValue;
            }
            String s = srcStr.toString().replaceAll("(\\s)", "");
            if (s.length() > 0) {
                return Integer.valueOf(s);
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Long toLong(Object srcStr, Long defaultValue) {
        if (srcStr == null) {
            return defaultValue;
        }
        try {
            if (!isInt(srcStr)) {
                return defaultValue;
            }
            String s = srcStr.toString().replaceAll("(\\s)", "");
            return Long.valueOf(s.length() > 0 ? Long.parseLong(s) : defaultValue.longValue());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean isInt(Object srcStr) {
        if (srcStr == null) {
            return false;
        }
        return Pattern.compile("([-]?[\\d]+)").matcher(srcStr.toString().replaceAll("(\\s)", "")).matches();
    }

    public static String toString(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim();
    }

    public static boolean isNumber(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static String trim(Object srcStr) {
        if (srcStr != null) {
            return srcStr.toString().trim();
        }
        return null;
    }

    public static byte[] stringToByteArray(String src) {
        if (TextUtils.isEmpty(src)) {
            return null;
        }
        try {
            return src.getBytes(DEFAULT_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byteArrayToString(byte[] src) {
        if (src == null) {
            return null;
        }
        try {
            return new String(src, DEFAULT_CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] jsonToByteArray(JSONObject json) {
        if (json == null) {
            return null;
        }
        return stringToByteArray(json.toString());
    }

    public static JSONObject byteArrayToJson(byte[] src) {
        if (src == null) {
            return null;
        }
        String str = byteArrayToString(src);
        if (str == null) {
            return null;
        }
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String urlEncode(String url) {
        String url2;
        try {
            if (url.indexOf(63) > 0) {
                String uri = URLEncoder.encode(url.substring(url.indexOf("://") + 3, url.indexOf("?")), DEFAULT_CHARSET_UTF8).replace("%2F", "/");
                String paramStr = url.substring(url.indexOf(63) + 1);
                if (paramStr == null || paramStr.length() <= 0) {
                    url2 = url.substring(0, url.indexOf("://")) + "://" + uri;
                } else {
                    url2 = url.substring(0, url.indexOf("://")) + "://" + uri + "?" + URLEncodedUtils.format(URLEncodedUtils.parse(URI.create(paramStr), DEFAULT_CHARSET_UTF8), DEFAULT_CHARSET_UTF8);
                }
            } else {
                url2 = url.substring(0, url.indexOf("://")) + "://" + URLEncoder.encode(url.substring(url.indexOf("://") + 3), DEFAULT_CHARSET_UTF8).replace("%2F", "/");
            }
            return url2.trim().replace(" ", "%20").trim().replace("+", "%20");
        } catch (Exception e) {
            return url;
        }
    }

    public static String sqliteEscape(String keyWord) {
        return keyWord.replace("/", "//").replace("'", "''").replace("[", "/[").replace("]", "/]").replace("%", "/%").replace("&", "/&").replace("_", "/_").replace("(", "/(").replace(")", "/)");
    }

    public static List<Integer> integerArrayConvertList(Integer[] paramArray) {
        if (paramArray == null || paramArray.length < 1) {
            return null;
        }
        List<Integer> paramList = new ArrayList<>();
        for (Integer param : paramArray) {
            paramList.add(param);
        }
        return paramList;
    }

    public static String[] listConvertStringArray(List<String> paramList) {
        if (paramList == null || paramList.size() < 0) {
            return null;
        }
        String[] paramArray = new String[paramList.size()];
        for (int index = 0; index < paramList.size(); index++) {
            paramArray[index] = paramList.get(index);
        }
        return paramArray;
    }

    public static long getByteCountFromStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        int unitIndex = -1;
        long unitSize = -1;
        String str2 = str.toUpperCase(Locale.ENGLISH);
        if (str2.endsWith("KB")) {
            unitIndex = str2.lastIndexOf("KB");
            unitSize = 1024;
        } else if (str2.endsWith("MB")) {
            unitIndex = str2.lastIndexOf("MB");
            unitSize = (long) Math.pow(1024.0d, 2.0d);
        } else if (str2.endsWith("GB")) {
            unitIndex = str2.lastIndexOf("GB");
            unitSize = (long) Math.pow(1024.0d, 3.0d);
        } else if (str2.endsWith("B")) {
            unitIndex = str2.lastIndexOf("B");
            unitSize = 1;
        }
        if (unitIndex == -1) {
            return -1;
        }
        try {
            return (long) (((float) unitSize) * Float.parseFloat(str2.substring(0, unitIndex)));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim());
    }
}

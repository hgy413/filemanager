package com.jb.filemanager.manager;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.xml.sax.ErrorHandler;

import java.util.Locale;

/**
 * Created by xiaoyu on 2016/10/26 13:58.
 */

public class LanguageManager {

    private static LanguageManager sInstance;

    /**
     * string的xml文件的相关标签
     */
    private static final String TAG_ITEM = "item";
    private static final String TAG_STRING = "string";
    private static final String TAG_STRING_ARRAY = "string-array";
    /**
     * 相关属性
     */
    private static final String ATTR_NAME = "name";

    private final Context mContext;

    private Resources mResources;

    /**
     * 默认语言
     */
    private static final String DEFAULT_LANGUAGE = "en_US";
    /**
     * 中国大陆语言
     */
    private static final String LANGUAGE_ZH_CN = "zh_CN";
    /**
     * 当前使用的语言码
     */
    private String mLanguageKeyCode = DEFAULT_LANGUAGE;

    // 网络部分
    private RequestQueue mQueue;
    private ErrorHandler mErrorHandler;
    private Response.Listener<String> mResponseHandler;

    private LanguageManager(Context context) {
        mContext = context.getApplicationContext();
        Resources resources = mContext.getResources();
        mResources = new Resources(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
    }

    public static LanguageManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LanguageManager(context);
        }
        return sInstance;
    }

    /**
     * 获取当前的语言码<br>
     * 格式为zh_CN<br>
     * 一般用于上传到服务器的语言参数值<br>
     *
     * @return
     */
    public String getCurrentLanguageWithLocale() {
        if (TextUtils.isEmpty(mLanguageKeyCode)) {
           // return mLanguageKeyCode;
        }
        if (mLanguageKeyCode.contains("_")) {
            //return mLanguageKeyCode;
        }
        final String countryCode = mResources.getConfiguration().locale
                .getCountry();
        if (countryCode.endsWith("CN")) {
            return DEFAULT_LANGUAGE;
        } else {
            return DEFAULT_LANGUAGE;
        }
        //return getKeyCode(mLanguageKeyCode, countryCode);
    }

    /**
     * 获取当前的语言码,不包含地区码<br>
     * 比如zh, en<br>
     *
     * @return
     */
    public String getCurrentLanguageNoLocale() {
        if (TextUtils.isEmpty(mLanguageKeyCode)) {
            return mLanguageKeyCode;
        }
        if (!mLanguageKeyCode.contains("_")) {
            return mLanguageKeyCode;
        }
        return mLanguageKeyCode.split("_")[0];
    }

    /**
     * 获取格式化的语言唯一的代码, 由语言码及地区码组合而成(地区码为可选).<br>
     * 格式形如en_US
     *
     * @return
     */
    public static String getKeyCode(String languageCode, String countryCode) {
        if (TextUtils.isEmpty(languageCode)) {
            throw new IllegalArgumentException(
                    "languageCode can't not be EMPTY!");
        }
        StringBuffer code = new StringBuffer();
        code.append(languageCode.toLowerCase(Locale.US));
        if (!TextUtils.isEmpty(countryCode)) {
            code.append("_");
            code.append(countryCode.toUpperCase(Locale.US));
        }
        return code.toString();
    }
}

package com.jiubang.commerce.ad.http;

import android.content.Context;
import android.os.Build;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.url.AdRedirectUrlUtils;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.GoogleMarketUtils;
import com.jiubang.commerce.utils.NetworkUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class AdHttpPostHandlerForNet {
    protected static final String KEY_PARAM_DATA = "data";
    protected static final String KEY_PARAM_HANDLE = "handle";
    protected static final String KEY_PARAM_PFUNID = "pfunid";
    protected static final String KEY_PARAM_PKEY = "pkey";
    protected static final String KEY_PARAM_SHANDLE = "shandle";
    protected static final String KEY_PARAM_SIGN = "sign";
    public static final int RESULT_STATUS_ERR_BUSSINESS = -2;
    public static final int RESULT_STATUS_ERR_SERVICE = -1;
    public static final int RESULT_STATUS_OK = 1;
    protected static final String TAG_ERRORCODE = "errorcode";
    protected static final String TAG_MSG = "msg";
    protected static final String TAG_RESULT = "result";
    protected static final String TAG_STATUS = "status";
    @Deprecated
    public static final int VALUE_HANDLE_BASE64 = 2;
    public static final int VALUE_HANDLE_GZIP = 1;
    public static final int VALUE_HANDLE_NONE = 0;
    protected Context mContext;

    public static class GoHttpPostResult {
        public int mErrorCode;
        public String mErrorMsg;
        public int mStatus = Integer.MIN_VALUE;
    }

    public AdHttpPostHandlerForNet(Context context) {
        this.mContext = context;
    }

    public String createUrl(String domainName, int funid) {
        return domainName + funid + "&rd=" + System.currentTimeMillis();
    }

    public HashMap<String, String> createPostParams(Map<String, Object> bussinessParams, String funid) {
        return createPostParams(bussinessParams, 0, 0, " ", " ", funid);
    }

    public String createPostData(Map<String, Object> bussinessParams, String funid) {
        return createPostData(bussinessParams, 0, 0, " ", " ", funid);
    }

    public HashMap<String, String> createPostParams(Map<String, Object> bussinessParams, int handle, int shandle, String funid) {
        return createPostParams(bussinessParams, handle, shandle, " ", " ", funid);
    }

    public HashMap<String, String> createPostParams(Map<String, Object> bussinessParams, int handle, int shandle, String pkey, String sign, String pFunid) {
        HashMap paramMap = new HashMap();
        paramMap.put(KEY_PARAM_DATA, handleDataParam(createDataParam(bussinessParams), handle));
        paramMap.put(KEY_PARAM_HANDLE, "" + handle);
        paramMap.put(KEY_PARAM_SHANDLE, "" + shandle);
        paramMap.put(KEY_PARAM_PKEY, pkey);
        paramMap.put(KEY_PARAM_SIGN, sign);
        paramMap.put(KEY_PARAM_PFUNID, pFunid);
        return paramMap;
    }

    public String createPostData(Map<String, Object> bussinessParams, int handle, int shandle, String pkey, String sign, String pFunid) {
        String dataParam = handleDataParam(createDataParam(bussinessParams), handle);
        try {
            dataParam = URLEncoder.encode(dataParam, "UTF-8");
            pkey = URLEncoder.encode(pkey, "UTF-8");
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException var9) {
            var9.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(KEY_PARAM_DATA).append("=").append(dataParam).append("&").append(KEY_PARAM_HANDLE).append("=").append(handle).append("&").append(KEY_PARAM_SHANDLE).append("=").append(shandle).append("&").append(KEY_PARAM_PKEY).append("=").append(pkey).append("&").append(KEY_PARAM_SIGN).append("=").append(sign).append("&").append(KEY_PARAM_PFUNID).append("=").append(pFunid);
        return sb.toString();
    }

    private static String handleDataParam(JSONObject dataJson, int handle) {
        if (dataJson == null) {
            return null;
        }
        switch (handle) {
            case 1:
                return HttpUtil.gzip(dataJson);
            default:
                return dataJson.toString();
        }
    }

    /* access modifiers changed from: protected */
    public JSONObject createDataParam(Map<String, Object> bussinessParams) {
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("phead", createHead());
            HttpUtil.putInJSONObject(bussinessParams, dataJson);
        } catch (JSONException var4) {
            var4.printStackTrace();
        }
        return dataJson;
    }

    /* access modifiers changed from: protected */
    public JSONObject createHead() {
        Context context = this.mContext;
        JSONObject pheadJson = new JSONObject();
        if (context != null) {
            try {
                AdSdkManager advertManager = AdSdkManager.getInstance();
                pheadJson.put("channel", advertManager.getChannel());
                pheadJson.put("vcode", AppUtils.getAppVersionCode(context, context.getPackageName()));
                pheadJson.put("vname", AppUtils.getAppVersionName(context, context.getPackageName()));
                pheadJson.put("country", StringUtils.toUpperCase(SystemUtils.getLocal(context)));
                pheadJson.put("lang", StringUtils.toLowerCase(SystemUtils.getLanguage(context)));
                pheadJson.put("goid", advertManager.getGoId());
                pheadJson.put(AdSdkRequestHeader.ANDROID_ID, StringUtils.toString(SystemUtils.getAndroidId(context)));
                pheadJson.put("imei", SystemUtils.getVirtualIMEI(context));
                pheadJson.put("imsi", SystemUtils.getImsi(context));
                pheadJson.put("sys", Build.VERSION.RELEASE);
                pheadJson.put("sdk", Build.VERSION.SDK_INT);
                pheadJson.put("net", NetworkUtils.buildNetworkState(context));
                pheadJson.put("hasmarket", GoogleMarketUtils.isMarketExist(context) ? 1 : 0);
                pheadJson.put("dpi", SystemUtils.getDpi(context));
                pheadJson.put("resolution", SystemUtils.getDisplay(context));
                pheadJson.put("adid", advertManager.getGoogleId());
                pheadJson.put("ua", AdRedirectUrlUtils.getUserAgent(context));
            } catch (JSONException var4) {
                var4.printStackTrace();
            }
        }
        return pheadJson;
    }

    public static JSONObject parseResponse(HttpResponse response, int shandle) {
        if (response == null) {
            return null;
        }
        JSONObject json = HttpUtil.getJsonObject(response, shandle == 1);
        if (json == null) {
            return null;
        }
        try {
            int status = json.getJSONObject("result").getInt("status");
            if (status == 1 || status == -2) {
                return json;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return null;
    }

    public static GoHttpPostResult parseResult(JSONObject responseJsonObject) {
        if (responseJsonObject == null) {
            return null;
        }
        try {
            GoHttpPostResult e = new GoHttpPostResult();
            JSONObject resultJson = responseJsonObject.getJSONObject("result");
            e.mStatus = resultJson.getInt("status");
            try {
                e.mErrorCode = resultJson.getInt(TAG_ERRORCODE);
                e.mErrorMsg = resultJson.getString(TAG_MSG);
                return e;
            } catch (JSONException e2) {
                return e;
            }
        } catch (JSONException var5) {
            var5.printStackTrace();
            return null;
        }
    }
}

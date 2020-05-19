package com.jiubang.commerce.statistics.adinfo.http;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.encrypt.Base64;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;
import com.jiubang.commerce.statistics.adinfo.http.BaseHttpConnector;
import com.jiubang.commerce.utils.CallbackUtil;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AdInfoController extends BaseHttpConnector {
    public static final String AD_INFO_URL = "/api/v3/configurations";
    public static final String HOST = "http://conf.api.hk.goforandroid.com";
    private static AdInfoController sController;
    private Context mContext;

    public static AdInfoController getInstance(Context context) {
        if (sController == null) {
            synchronized (AdInfoController.class) {
                if (sController == null) {
                    sController = new AdInfoController(context.getApplicationContext(), HOST);
                }
            }
        }
        return sController;
    }

    private AdInfoController(Context context, String host) {
        super(context, host);
        this.mContext = context.getApplicationContext();
    }

    public void getAdInfo(BaseHttpConnector.ConnectListener listener) {
        String url = AD_INFO_URL;
        HashMap<String, String> paramMap = createParams();
        if (paramMap != null) {
            if (url.lastIndexOf("?") != url.length() - 1) {
                url = url + "?";
            }
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                url = url + entry.getKey() + "=" + entry.getValue() + "&";
            }
        }
        get(url, listener);
    }

    /* access modifiers changed from: protected */
    public HashMap<String, String> createParams() {
        Context context = this.mContext;
        HashMap<String, String> params = new HashMap<>();
        if (context != null) {
            params.put("product_id", "1272");
            params.put("config_name", "ad_title_sequence_" + GoHttpHeadUtil.getCountry(context).toLowerCase());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("lang", GoHttpHeadUtil.getLanguage(context).toLowerCase());
                jsonObject.put("country", GoHttpHeadUtil.getCountry(context));
                jsonObject.put("channel", CallbackUtil.HTTP_RESPONSE_CODE_OK);
                jsonObject.put("cversion_name", GoHttpHeadUtil.getVersionName(context));
                jsonObject.put("cversion_number", GoHttpHeadUtil.getVersionCode(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String client = jsonObject.toString();
            LogUtils.i("hzw", "client:" + client);
            try {
                client = Base64.encodeString(client, "UTF-8").replaceAll("\\n", "");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            params.put("client", client);
        }
        return params;
    }
}

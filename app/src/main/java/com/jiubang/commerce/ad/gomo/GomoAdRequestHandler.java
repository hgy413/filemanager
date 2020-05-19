package com.jiubang.commerce.ad.gomo;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.http.AdHttpPostHandlerForNet;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.AdvertHttpAdapter;
import com.jiubang.commerce.ad.http.AdvertJsonOperator;
import com.jiubang.commerce.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class GomoAdRequestHandler extends AdHttpPostHandlerForNet implements IConnectListener {
    static final String URL = "http://advonline.goforandroid.com/adv_online/onlineadv";
    private int mAdPost;
    private int mAdvDataSource;
    private IGomoAdListener mIGomoAdListener;
    private String mPrefix = null;

    public interface IGomoAdListener {
        void onRetrived(JSONObject jSONObject);
    }

    public GomoAdRequestHandler(Context context, int adPos, int advDataSource, IGomoAdListener listener) {
        super(context);
        this.mAdPost = adPos;
        this.mAdvDataSource = advDataSource;
        this.mIGomoAdListener = listener;
    }

    public void startRequest(boolean isAsync) {
        THttpRequest request = createRequest();
        if (this.mIGomoAdListener != null && request != null) {
            AdvertHttpAdapter.getInstance(this.mContext).addTask(request, isAsync);
        }
    }

    private String getLogPrefix() {
        return this.mPrefix != null ? this.mPrefix : "[GomoAd:" + this.mAdPost + "]";
    }

    /* access modifiers changed from: protected */
    public JSONObject createHead() {
        JSONObject pheadJson = super.createHead();
        try {
            pheadJson.put("advposid", String.valueOf(this.mAdPost));
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", getLogPrefix() + pheadJson.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pheadJson;
    }

    private THttpRequest createRequest() {
        HashMap<String, String> params = new HashMap<>();
        params.put("phead", StringUtils.toString(createHead()));
        Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
        params.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
        params.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
        THttpRequest httpRequest = null;
        try {
            THttpRequest httpRequest2 = new THttpRequest(URL, this);
            try {
                httpRequest2.setParamMap(params);
                httpRequest2.setProtocol(1);
                httpRequest2.setTimeoutValue(15000);
                httpRequest2.setRequestPriority(10);
                httpRequest2.setOperator(new AdvertJsonOperator(false));
                return httpRequest2;
            } catch (Exception e) {
                e = e;
                httpRequest = httpRequest2;
                LogUtils.w("Ad_SDK", getLogPrefix() + "createRequest-->error", e);
                return httpRequest;
            }
        } catch (Exception e2) {
            e = e2;
            LogUtils.w("Ad_SDK", getLogPrefix() + "createRequest-->error", e);
            return httpRequest;
        }
    }

    public void onStart(THttpRequest tHttpRequest) {
    }

    public void onFinish(THttpRequest tHttpRequest, IResponse iResponse) {
        String responseStr = iResponse.getResponse().toString();
        if (LogUtils.isShowLog()) {
            LogUtils.i("Ad_SDK", getLogPrefix() + "onFinish-->" + responseStr);
        }
        try {
            JSONObject json = new JSONObject(responseStr);
            this.mIGomoAdListener.onRetrived(json);
            JSONObject jSONObject = json;
        } catch (JSONException e) {
            LogUtils.w("Ad_SDK", getLogPrefix() + "onFinish-->", e);
            this.mIGomoAdListener.onRetrived((JSONObject) null);
        } catch (Throwable th) {
            this.mIGomoAdListener.onRetrived((JSONObject) null);
            throw th;
        }
    }

    public void onException(THttpRequest tHttpRequest, int reason) {
        LogUtils.i("Ad_SDK", getLogPrefix() + "onException-->" + reason);
        this.mIGomoAdListener.onRetrived((JSONObject) null);
    }

    @Deprecated
    public void onException(THttpRequest tHttpRequest, HttpResponse httpResponse, int reason) {
        onException(tHttpRequest, reason);
    }
}

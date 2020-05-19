package com.jiubang.commerce.ad.abtest;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.AdvertHttpAdapter;
import com.jiubang.commerce.ad.http.AdvertJsonOperator;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.ClientParams;
import com.jiubang.commerce.utils.AppUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;

public class AbTestHttpHandler implements IConnectListener {
    private String mBussinessID;
    private Context mContext;
    private IABTestHttpListener mIABTestHttpListener;

    public interface IABTestHttpListener {
        void onFinish(String str, AbBean abBean);
    }

    public AbTestHttpHandler(Context context, String bid, IABTestHttpListener listener) {
        this.mContext = context.getApplicationContext();
        this.mBussinessID = bid;
        this.mIABTestHttpListener = listener;
    }

    public void startRequest() {
        if (this.mIABTestHttpListener != null) {
            THttpRequest httpRequest = null;
            try {
                String url = getUrl();
                if (LogUtils.isShowLog()) {
                    LogUtils.d(ABTestManager.TAG, "AbTestHttpHandler url=" + url);
                }
                httpRequest = new THttpRequest(url, this);
            } catch (Exception e) {
                LogUtils.e(ABTestManager.TAG, "AbTestHttpHandler Create THttpRequest Exception", e);
            }
            if (httpRequest != null) {
                httpRequest.setProtocol(0);
                httpRequest.setOperator(new AdvertJsonOperator(false));
                httpRequest.setTimeoutValue(15000);
                httpRequest.setRequestPriority(10);
                AdvertHttpAdapter.getInstance(this.mContext).addTask(httpRequest, true);
            }
        }
    }

    public void onFinish(THttpRequest request, IResponse response) {
        String responseStr = StringUtils.toString(response.getResponse());
        if (LogUtils.isShowLog()) {
            LogUtils.d(ABTestManager.TAG, "bid=" + this.mBussinessID + " responseStr=" + responseStr);
        }
        this.mIABTestHttpListener.onFinish(this.mBussinessID, new AbBean(responseStr));
    }

    public void onException(THttpRequest request, int reason) {
        LogUtils.e(ABTestManager.TAG, "AbTestHttpHandler onException reason=" + reason);
        this.mIABTestHttpListener.onFinish(this.mBussinessID, new AbBean((String) null));
    }

    public void onStart(THttpRequest request) {
    }

    public void onException(THttpRequest request, HttpResponse response, int reason) {
        onException(request, reason);
    }

    /* access modifiers changed from: protected */
    public String getUrl() {
        Map<String, String> params = getParams();
        StringBuffer sb = new StringBuffer(AdSdkRequestHeader.getABTestUrl());
        sb.append("?");
        for (String key : params.keySet()) {
            sb.append(key + "=" + params.get(key) + '&');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public Map<String, String> getParams() {
        AdSdkManager manager = AdSdkManager.getInstance();
        ClientParams clientParams = ClientParams.getFromLocal(this.mContext);
        HashMap<String, String> params = new HashMap<>();
        params.put("gzip", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
        params.put(AdSdkRequestHeader.PRODUCT_ID, manager.getCid());
        params.put("entrance", AdSdkRequestHeader.sIS_TEST_SERVER ? "999" : manager.getEntranceId());
        params.put("cversion", AppUtils.getAppVersionCode(this.mContext, this.mContext.getPackageName()) + "");
        params.put(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_IP_LOCAL, SystemUtils.getLocal(this.mContext));
        params.put("utm_source", clientParams.getBuyChannel());
        params.put("cdays", clientParams.getCDays() + "");
        params.put("isupgrade", clientParams.getIsUpgrade() ? "1" : "2");
        params.put(AdSdkRequestHeader.ANDROID_ID, SystemUtils.getAndroidId(this.mContext));
        params.put("pkgname", this.mContext.getPackageName());
        params.put("sid", this.mBussinessID);
        return params;
    }
}

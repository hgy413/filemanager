package com.jiubang.commerce.buychannel.buyChannel.utils;

import android.content.Context;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.http.AdvertJsonOperator;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.AdvertHttpAdapter;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;
import java.util.HashMap;

public class RequestDataUtils {
    public static void requestBuyChannelType(Context context, String cid, String aid, String buychannel, IConnectListener listener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.getBuyChannelTypeUrl(), listener);
        } catch (Exception e) {
            LogUtils.e("buychannelsdk", "requestBuyChannelType(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("cid", cid);
            maps.put("aid", aid);
            maps.put("buychannel", buychannel);
            maps.put("rd", BuyChannelTypeContans.TYPE_ORGANIC);
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(10000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, false);
            return;
        }
        LogUtils.d("buychannelsdk", "requestBuyChannelType(error, httpRequest is null)");
    }
}

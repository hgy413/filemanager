package com.jiubang.commerce.ad.http;

import android.content.Context;
import android.content.SharedPreferences;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.bean.AdUserTagInfoBean;
import com.jiubang.commerce.ad.http.AdSdkRequestHeader;
import com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean;
import com.jiubang.commerce.ad.manager.AdModuleShowCountManager;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.ad.params.AdSdkParamsBuilder;
import com.jiubang.commerce.ad.params.ModuleRequestParams;
import com.jiubang.commerce.ad.params.UserTagParams;
import com.jiubang.commerce.database.table.AdvertFilterTable;
import com.jiubang.commerce.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdSdkRequestDataUtils {
    private static final String ADMODULE_UNREACHABLE_SP = "sp_admodule_unreachable";
    private static final String FUN_ID_ABTEST = "16";
    private static final String FUN_ID_GET_AD_CONTROL_INFO = "13";
    public static final String RESPONSE_JOSN_TAG_DATAS = "datas";
    public static final String RESPONSE_JOSN_TAG_IP_ADDRESS = "ip";
    public static final String RESPONSE_JOSN_TAG_IP_LOCAL = "local";
    public static final String RESPONSE_JOSN_TAG_MFLAG = "mflag";
    public static final String RESPONSE_JOSN_TAG_MFLAG_MSTATUS = "mstatus";
    public static final String RESPONSE_JOSN_TAG_NOAD = "noad";
    public static final String RESPONSE_JOSN_TAG_RESULT = "result";
    public static final String RESPONSE_JOSN_TAG_STATUS = "status";
    public static final String RESPONSE_JOSN_TAG_UFLAG = "uflag";
    public static final String RESPONSE_JOSN_TAG_UFLAG_BUYTYPE = "buychanneltype";
    public static final String RESPONSE_JOSN_TAG_UFLAG_USER = "user";
    public static final int RESPONSE_STATUS_CODE_ERROR = -1;
    public static final int RESPONSE_STATUS_CODE_SUCCESS = 1;

    public static void requestAdControlInfo(Context context, int moduleId, int pageId, boolean isAddFilterPackageNames, AdSdkParamsBuilder apb, IConnectListener connectListener) {
        List<ModuleRequestParams> moduleRequestParams = new ArrayList<>();
        moduleRequestParams.add(new ModuleRequestParams(Integer.valueOf(moduleId), Integer.valueOf(pageId)));
        requestAdControlInfo(context, moduleRequestParams, "", isAddFilterPackageNames, apb, connectListener);
    }

    public static void requestAdControlInfo(final Context context, List<ModuleRequestParams> moduleRequestParams, String packageNames, boolean isAddFilterPackageNames, AdSdkParamsBuilder apb, IConnectListener connectListener) {
        final JSONObject postdata = new JSONObject();
        int moduleId = 0;
        try {
            postdata.put("phead", AdSdkRequestHeader.createPhead(context, apb));
            moduleId = moduleRequestParams.get(0).getModuleId().intValue();
            postdata.put("filterpkgnames", isAddFilterPackageNames ? AdvertFilterTable.getInstance(context).getFilterList(String.valueOf(moduleId), 30) : "");
            JSONArray reqArray = new JSONArray();
            for (ModuleRequestParams param : moduleRequestParams) {
                int mid = param.getModuleId().intValue();
                JSONObject reqJson = new JSONObject();
                reqJson.put("moduleId", mid);
                reqJson.put("pageid", param.getPageId().intValue());
                reqJson.put("showquantity", AdModuleShowCountManager.getInstance(context).getShowCount(mid));
                reqArray.put(reqJson);
            }
            postdata.put("reqs", reqArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int mId = moduleId;
        final IConnectListener iConnectListener = connectListener;
        AdSdkManager.getInstance().requestUserTags(context, new AdSdkManager.IAdvertUserTagResultListener() {
            public void onAdRequestSuccess(AdUserTagInfoBean adUserTagInfoBean) {
                try {
                    postdata.put("tags", adUserTagInfoBean.getUserTagStr());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AdSdkRequestDataUtils.requestData(context, mId, AdSdkRequestHeader.getUrl("13"), postdata, true, "13", iConnectListener);
            }

            public void onAdRequestFail(int statusCode) {
                AdSdkRequestDataUtils.requestData(context, mId, AdSdkRequestHeader.getUrl("13"), postdata, true, "13", iConnectListener);
            }
        });
    }

    public static void requestOnlineAdInfo(Context context, int adCount, int adPos, AdSdkRequestHeader.S2SParams params, IConnectListener connectListener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.getOnlineAdUrl(), connectListener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestOnlineAdInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForOnline(context, adCount, adPos, params)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "requestOnlineAdInfo param:" + maps.toString());
            }
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, false);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "requestOnlineAdInfo(error, httpRequest is null)");
        }
    }

    public static void requestIntelligentAdInfo(Context context, int adPos, IConnectListener connectListener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.INTELLIGENT_REQUEST_URL, connectListener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestIntelligentAdInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForIntelligent(context, adPos)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("shandle", "1");
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, true);
            if (LogUtils.isShowLog()) {
                LogUtils.i("Ad_SDK", "requestIntelligentAdInfo(param)=" + maps.toString());
            }
        } else if (LogUtils.isShowLog()) {
            LogUtils.e("Ad_SDK", "requestIntelligentAdInfo(error, httpRequest is null)");
        }
    }

    public static void requestSearchPresolveAdInfo(Context context, int adPos, String key, IConnectListener connectListener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.SEARCH_PRESOLVE_REQUEST_URL, connectListener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestSearchPresolveAdInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForSearchPresolve(context, adPos)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("q", key);
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("shandle", "1");
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, true);
        } else if (LogUtils.isShowLog()) {
            LogUtils.e("Ad_SDK", "requestSearchPresolveAdInfo(error, httpRequest is null)");
        }
    }

    public static void requestSIMBAdInfo(Context context, int adPos, IConnectListener connectListener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.SIMB_REQUEST_URL, connectListener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestSIMBAdInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForSIMB(context, adPos)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, true);
        } else if (LogUtils.isShowLog()) {
            LogUtils.e("Ad_SDK", "requestSIMBAdInfo(error, httpRequest is null)");
        }
    }

    public static void requestUserTagInfo(Context context, IConnectListener listener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.getUserTagUrl(), listener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestUserTagInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForOnline(context, 0, 0, (AdSdkRequestHeader.S2SParams) null)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("shandle", "1");
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, false);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "requestUserTagInfo(error, httpRequest is null)");
        }
    }

    public static void requestUserTagInfo(Context context, IConnectListener listener, UserTagParams params, boolean isNew) {
        if (params == null) {
            requestUserTagInfo(context, listener);
            return;
        }
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.getUserTagUrl(), listener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestUserTagInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForOnlineFromParams(context, params, isNew)));
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, params.mProductKey);
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, params.mAccessKey);
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("shandle", "1");
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, false);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "requestUserTagInfo(error, httpRequest is null)");
        }
    }

    public static void requestUserTagInfo(Context context, boolean isAsyncTask, IConnectListener listener) {
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(AdSdkRequestHeader.getUserTagUrl(), listener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "requestUserTagInfo(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("phead", StringUtils.toString(AdSdkRequestHeader.createPheadForOnline(context, 0, 0, (AdSdkRequestHeader.S2SParams) null)));
            Map<String, String> requestParameKeyMap = AdSdkRequestHeader.getOnlineAdRequestParameKey();
            maps.put(AdSdkRequestHeader.ONLINE_AD_PRODKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_PRODKEY));
            maps.put(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY, requestParameKeyMap.get(AdSdkRequestHeader.ONLINE_AD_ACCESSKEY));
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("shandle", "1");
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(15000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator(false));
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, isAsyncTask);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "requestUserTagInfo(error, httpRequest is null)");
        }
    }

    public static void requestABTestInfo(Context context, IConnectListener connectListener) {
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("phead", AdSdkRequestHeader.createPhead(context, AdSdkParamsBuilder.createEmptyBuilder((String) null, (Integer) null, (Integer) null).build()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestData(context, -1, AdSdkRequestHeader.getUrl("16"), postdata, true, "16", connectListener);
    }

    /* access modifiers changed from: private */
    public static void requestData(Context context, int moduleId, String url, JSONObject postdata, boolean isAsync, String funid, IConnectListener connectListener) {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "[vmId:" + moduleId + "]requestData(start, " + url + ", " + postdata + ")");
        }
        THttpRequest httpRequest = null;
        try {
            httpRequest = new THttpRequest(url, connectListener);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "[vmId:" + moduleId + "]requestData(error, " + (e != null ? e.getMessage() : "==") + ")");
        }
        if (httpRequest != null) {
            HashMap<String, String> maps = new HashMap<>();
            maps.put("handle", BaseModuleDataItemBean.AD_DATA_SOURCE_TYPE_OFFLINE);
            maps.put("data", StringUtils.toString(postdata));
            maps.put("shandle", "1");
            maps.put("pfunid", funid);
            httpRequest.setParamMap(maps);
            httpRequest.setProtocol(1);
            httpRequest.setTimeoutValue(10000);
            httpRequest.setRequestPriority(10);
            httpRequest.setOperator(new AdvertJsonOperator());
            AdvertHttpAdapter.getInstance(context).addTask(httpRequest, isAsync);
        } else if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "[vmId:" + moduleId + "]requestData(error, httpRequest is null)");
        }
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "[vmId:" + moduleId + "]requestData(end, " + url + ")");
        }
    }

    public static boolean canAdModuleReachable(Context context, int virtualModuleId, JSONObject mflagJson) {
        if (mflagJson == null) {
            return true;
        }
        try {
            String virtualModuleIdStr = String.valueOf(virtualModuleId);
            if (mflagJson.getJSONObject(virtualModuleIdStr).getInt(RESPONSE_JOSN_TAG_MFLAG_MSTATUS) != 0) {
                return true;
            }
            recordUnReachableAdModuel(context, virtualModuleIdStr);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean canAdModuleReachable(Context context, int virtualModuleId) {
        SharedPreferences sp = context.getSharedPreferences(ADMODULE_UNREACHABLE_SP, 0);
        String virtualModuleIdStr = String.valueOf(virtualModuleId);
        boolean ret = true;
        if (sp.contains(virtualModuleIdStr)) {
            if (System.currentTimeMillis() - sp.getLong(virtualModuleIdStr, 0) > 86400000) {
                ret = true;
            } else {
                ret = false;
            }
            if (ret) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(virtualModuleIdStr);
                editor.commit();
            }
        }
        return ret;
    }

    private static void recordUnReachableAdModuel(Context context, String virtualModuleIdStr) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ADMODULE_UNREACHABLE_SP, 0).edit();
        editor.putLong(virtualModuleIdStr, System.currentTimeMillis());
        editor.commit();
    }
}

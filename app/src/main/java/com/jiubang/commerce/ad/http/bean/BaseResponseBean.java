package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.avoid.AdAvoider;
import com.jiubang.commerce.ad.http.AdSdkRequestDataUtils;
import com.jiubang.commerce.utils.FileCacheUtils;
import org.json.JSONObject;

public class BaseResponseBean {
    public static final String NORMAL_FLOW_CHANNEL = "1";
    private String mBuychanneltype = "";
    private String mIPAddress;
    private String mIPLocal;
    private int mNoad = 0;
    private String mUser = "";

    public static BaseResponseBean parseBaseResponseBeanJSONObject(Context context, int virtualModuleId, JSONObject json) {
        if (json == null || json.length() <= 0) {
            return null;
        }
        BaseResponseBean baseResponseBean = new BaseResponseBean();
        JSONObject uflagJson = json.optJSONObject(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_UFLAG);
        if (uflagJson != null) {
            baseResponseBean.setUser(uflagJson.optString(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_UFLAG_USER, ""));
            baseResponseBean.setBuychanneltype(uflagJson.optString(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_UFLAG_BUYTYPE, ""));
            baseResponseBean.mIPLocal = uflagJson.optString(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_IP_LOCAL);
            baseResponseBean.mNoad = uflagJson.optInt(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_NOAD);
            baseResponseBean.mIPAddress = uflagJson.optString(AdSdkRequestDataUtils.RESPONSE_JOSN_TAG_IP_ADDRESS);
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "Your ip address is " + baseResponseBean.mIPAddress);
            }
            AdAvoider.getInstance(context).detect(baseResponseBean.mIPLocal, Integer.valueOf(baseResponseBean.mNoad));
        }
        if (!LogUtils.isShowLog()) {
            return baseResponseBean;
        }
        LogUtils.d("Ad_SDK", "[vmId:" + virtualModuleId + "]BaseResponseBean(mUser=" + baseResponseBean.getUser() + " mBuychanneltype=" + baseResponseBean.getBuychanneltype() + ")");
        return baseResponseBean;
    }

    public String getBuychanneltype() {
        return this.mBuychanneltype;
    }

    public void setBuychanneltype(String mBuychanneltype2) {
        this.mBuychanneltype = mBuychanneltype2;
    }

    public String getUser() {
        return this.mUser;
    }

    public void setUser(String mUser2) {
        this.mUser = mUser2;
    }

    public String getIPLocal() {
        return this.mIPLocal;
    }

    public int getNoad() {
        return this.mNoad;
    }

    public boolean isNormalChannel() {
        return "1".equals(this.mBuychanneltype);
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0058 A[SYNTHETIC, Splitter:B:14:0x0058] */
    /* JADX WARNING: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean saveSelfDataToSdcard(int r12) {
        /*
            r11 = this;
            r10 = 1
            r5 = 0
            java.lang.String r6 = r11.mUser
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0013
            java.lang.String r6 = r11.mBuychanneltype
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0013
        L_0x0012:
            return r5
        L_0x0013:
            r0 = 0
            org.json.JSONObject r4 = new org.json.JSONObject
            r4.<init>()
            java.lang.String r6 = "user"
            java.lang.String r7 = r11.mUser     // Catch:{ JSONException -> 0x0066 }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x0066 }
            java.lang.String r6 = "buychanneltype"
            java.lang.String r7 = r11.mBuychanneltype     // Catch:{ JSONException -> 0x0066 }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x0066 }
            java.lang.String r6 = "local"
            java.lang.String r7 = r11.mIPLocal     // Catch:{ JSONException -> 0x0066 }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x0066 }
            java.lang.String r6 = "noad"
            int r7 = r11.mNoad     // Catch:{ JSONException -> 0x0066 }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x0066 }
            java.lang.String r6 = "ip"
            java.lang.String r7 = r11.mIPAddress     // Catch:{ JSONException -> 0x0066 }
            r4.put(r6, r7)     // Catch:{ JSONException -> 0x0066 }
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0066 }
            r1.<init>()     // Catch:{ JSONException -> 0x0066 }
            java.lang.String r6 = "uflag"
            r1.put(r6, r4)     // Catch:{ JSONException -> 0x0070 }
            java.lang.String r6 = "saveDataTime"
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x0070 }
            r1.put(r6, r8)     // Catch:{ JSONException -> 0x0070 }
            r0 = r1
        L_0x0050:
            if (r0 == 0) goto L_0x0012
            int r6 = r0.length()
            if (r6 < r10) goto L_0x0012
            java.lang.String r6 = getCacheFileName(r12)     // Catch:{ Exception -> 0x006b }
            java.lang.String r7 = com.jiubang.commerce.utils.StringUtils.toString(r0)     // Catch:{ Exception -> 0x006b }
            r8 = 1
            boolean r5 = com.jiubang.commerce.utils.FileCacheUtils.saveCacheDataToSdcard(r6, r7, r8)     // Catch:{ Exception -> 0x006b }
            goto L_0x0012
        L_0x0066:
            r3 = move-exception
        L_0x0067:
            r3.printStackTrace()
            goto L_0x0050
        L_0x006b:
            r2 = move-exception
            r2.printStackTrace()
            goto L_0x0012
        L_0x0070:
            r3 = move-exception
            r0 = r1
            goto L_0x0067
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.http.bean.BaseResponseBean.saveSelfDataToSdcard(int):boolean");
    }

    public static String getCacheFileName(int virtualModuleId) {
        return "BaseResponseBean-" + virtualModuleId;
    }

    public static BaseResponseBean getBaseResponseBeanFromCacheData(Context context, int virtualModuleId) {
        String adControlCacheData = FileCacheUtils.readCacheDataToString(getCacheFileName(virtualModuleId), true);
        if (!TextUtils.isEmpty(adControlCacheData)) {
            try {
                return parseBaseResponseBeanJSONObject(context, virtualModuleId, new JSONObject(adControlCacheData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

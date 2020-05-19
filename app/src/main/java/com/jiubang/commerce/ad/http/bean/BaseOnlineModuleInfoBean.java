package com.jiubang.commerce.ad.http.bean;

import android.content.Context;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.utils.FileCacheUtils;
import com.jiubang.commerce.utils.StringUtils;
import java.util.List;
import org.json.JSONObject;

public class BaseOnlineModuleInfoBean {
    private static final int DEFAULT_VALUE_INT = 0;
    private static final String DEFAULT_VALUE_STRING = "";
    private int mAdPos;
    private String mHasShowAdUrlList;
    private List<BaseOnlineAdInfoBean> mOnlineAdInfoList;
    private long mSaveDataTime;

    public int getAdPos() {
        return this.mAdPos;
    }

    public void setAdPos(int adPos) {
        this.mAdPos = adPos;
    }

    public long getSaveDataTime() {
        return this.mSaveDataTime;
    }

    public void setSaveDataTime(long saveDataTime) {
        this.mSaveDataTime = saveDataTime;
    }

    public void setHasShowAdUrlList(String hasShowAdUrlList) {
        this.mHasShowAdUrlList = hasShowAdUrlList;
    }

    public String getHasShowAdUrlList() {
        return this.mHasShowAdUrlList;
    }

    public List<BaseOnlineAdInfoBean> getOnlineAdInfoList() {
        return this.mOnlineAdInfoList;
    }

    public void setOnlineAdInfoList(List<BaseOnlineAdInfoBean> onlineAdInfoList) {
        this.mOnlineAdInfoList = onlineAdInfoList;
    }

    public static BaseOnlineModuleInfoBean parseJsonObject(Context context, int adPos, int advDataSource, int virtualModuleId, int moduleId, JSONObject datasJsonObj) {
        if (datasJsonObj == null || datasJsonObj.length() < 1 || !datasJsonObj.has(String.valueOf(adPos))) {
            return null;
        }
        BaseOnlineModuleInfoBean onlineModuleInfoBean = new BaseOnlineModuleInfoBean();
        onlineModuleInfoBean.mAdPos = adPos;
        try {
            onlineModuleInfoBean.mOnlineAdInfoList = BaseOnlineAdInfoBean.parseJsonArray(context, datasJsonObj.getJSONArray(String.valueOf(adPos)), virtualModuleId, moduleId, adPos, advDataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (datasJsonObj.has(AdSdkContants.SAVE_DATA_TIME)) {
            onlineModuleInfoBean.mSaveDataTime = datasJsonObj.optLong(AdSdkContants.SAVE_DATA_TIME, 0);
        }
        if (!datasJsonObj.has(AdSdkContants.HAS_SHOW_AD_URL_LIST)) {
            return onlineModuleInfoBean;
        }
        onlineModuleInfoBean.mHasShowAdUrlList = datasJsonObj.optString(AdSdkContants.HAS_SHOW_AD_URL_LIST, "");
        return onlineModuleInfoBean;
    }

    public static boolean saveAdDataToSdcard(int adPos, JSONObject cacheDataJsonObject) {
        if (cacheDataJsonObject == null || cacheDataJsonObject.length() < 1) {
            return false;
        }
        if (!cacheDataJsonObject.has(AdSdkContants.SAVE_DATA_TIME)) {
            try {
                cacheDataJsonObject.put(AdSdkContants.SAVE_DATA_TIME, System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return FileCacheUtils.saveCacheDataToSdcard(getCacheFileName(adPos), StringUtils.toString(cacheDataJsonObject), true);
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b2, code lost:
        if (r21.contains(r14.getPackageName()) == false) goto L_0x00b4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.jiubang.commerce.ad.bean.AdModuleInfoBean getOnlineAdInfoList(android.content.Context r16, com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean r17, int r18, int r19, boolean r20, java.util.List<java.lang.String> r21, org.json.JSONObject r22) {
        /*
            r4 = -1
            r5 = -1
            if (r17 == 0) goto L_0x000c
            int r4 = r17.getVirtualModuleId()
            int r5 = r17.getModuleId()
        L_0x000c:
            if (r17 == 0) goto L_0x0186
            int r3 = r17.getAdvDataSource()
        L_0x0012:
            r1 = r16
            r2 = r18
            r6 = r22
            com.jiubang.commerce.ad.http.bean.BaseOnlineModuleInfoBean r9 = parseJsonObject(r1, r2, r3, r4, r5, r6)
            if (r9 == 0) goto L_0x0189
            java.util.List r15 = r9.getOnlineAdInfoList()
        L_0x0022:
            r10 = 0
            if (r15 == 0) goto L_0x00d4
            boolean r1 = r15.isEmpty()
            if (r1 != 0) goto L_0x00d4
            java.lang.String r12 = r9.getHasShowAdUrlList()
            boolean r1 = com.jb.ga0.commerce.util.LogUtils.isShowLog()
            if (r1 == 0) goto L_0x0069
            java.lang.String r1 = "Ad_SDK"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "[vmId:"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r3 = "]getOnlineAdInfoList(adPos::->"
            java.lang.StringBuilder r2 = r2.append(r3)
            r0 = r18
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r3 = ", hasShowAdUrls::->"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r12)
            java.lang.String r3 = ")"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.jb.ga0.commerce.util.LogUtils.d(r1, r2)
        L_0x0069:
            java.util.Iterator r13 = r15.iterator()
        L_0x006d:
            boolean r1 = r13.hasNext()
            if (r1 == 0) goto L_0x00d4
            java.lang.Object r14 = r13.next()
            com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean r14 = (com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean) r14
            if (r14 == 0) goto L_0x006d
            if (r20 == 0) goto L_0x00a6
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 != 0) goto L_0x00a6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "||"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r14.getTargetUrl()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "||"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r1 = r12.indexOf(r1)
            if (r1 >= 0) goto L_0x006d
        L_0x00a6:
            if (r21 == 0) goto L_0x00b4
            java.lang.String r1 = r14.getPackageName()
            r0 = r21
            boolean r1 = r0.contains(r1)
            if (r1 != 0) goto L_0x00c0
        L_0x00b4:
            java.lang.String r1 = r14.getPackageName()
            r0 = r16
            boolean r1 = com.jiubang.commerce.utils.AppUtils.isAppExist(r0, r1)
            if (r1 != 0) goto L_0x006d
        L_0x00c0:
            if (r10 != 0) goto L_0x00c7
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
        L_0x00c7:
            r10.add(r14)
            if (r19 <= 0) goto L_0x006d
            int r1 = r10.size()
            r0 = r19
            if (r1 < r0) goto L_0x006d
        L_0x00d4:
            if (r10 == 0) goto L_0x018c
            boolean r1 = r10.isEmpty()
            if (r1 != 0) goto L_0x018c
            com.jiubang.commerce.ad.bean.AdModuleInfoBean r6 = new com.jiubang.commerce.ad.bean.AdModuleInfoBean
            r6.<init>()
            r7 = r16
            r8 = r17
            r11 = r21
            r6.setOnlineAdInfoList(r7, r8, r9, r10, r11)
            boolean r1 = com.jb.ga0.commerce.util.LogUtils.isShowLog()
            if (r1 == 0) goto L_0x018d
            java.util.Iterator r13 = r10.iterator()
        L_0x00f4:
            boolean r1 = r13.hasNext()
            if (r1 == 0) goto L_0x018d
            java.lang.Object r14 = r13.next()
            com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean r14 = (com.jiubang.commerce.ad.http.bean.BaseOnlineAdInfoBean) r14
            if (r14 == 0) goto L_0x00f4
            java.lang.String r1 = "Ad_SDK"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "return online ad info::>(count:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r10.size()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = "--"
            java.lang.StringBuilder r2 = r2.append(r3)
            r0 = r19
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r3 = ", VirtualModuleId:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r14.getVirtualModuleId()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ", ModuleId:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r14.getModuleId()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ", MapId:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r14.getMapId()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ", packageName:"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = r14.getPackageName()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ", Name:"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = r14.getAppName()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ", AdPos:"
            java.lang.StringBuilder r2 = r2.append(r3)
            int r3 = r14.getAdPos()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ")"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.jb.ga0.commerce.util.LogUtils.d(r1, r2)
            goto L_0x00f4
        L_0x0186:
            r3 = 0
            goto L_0x0012
        L_0x0189:
            r15 = 0
            goto L_0x0022
        L_0x018c:
            r6 = 0
        L_0x018d:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.ad.http.bean.BaseOnlineModuleInfoBean.getOnlineAdInfoList(android.content.Context, com.jiubang.commerce.ad.http.bean.BaseModuleDataItemBean, int, int, boolean, java.util.List, org.json.JSONObject):com.jiubang.commerce.ad.bean.AdModuleInfoBean");
    }

    public static boolean checkOnlineAdInfoValid(long loadDataTime) {
        if (loadDataTime <= 0 || loadDataTime > System.currentTimeMillis() - 3600000) {
            return true;
        }
        return false;
    }

    public static String getCacheFileName(int adPos) {
        return AdSdkContants.ONLINE_AD_CACHE_FILE_NAME_PREFIX + adPos;
    }
}

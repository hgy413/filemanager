package com.jiubang.commerce.statistics;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.thread.AdSdkThread;
import com.jiubang.commerce.utils.StringUtils;

public class BaseSeq105OperationStatistic extends AbsBaseStatistic {
    protected static final String ASSOCIATED_OBJ_SEPARATE = ";";
    public static final int OPERATION_LOG_SEQ = 105;
    public static final String SDK_AD_ACTIVE = "k000";
    public static final String SDK_AD_CLICK = "a000";
    public static final String SDK_AD_DOWNLOADED = "downloaded";
    public static final String SDK_AD_INSTALL = "b000";
    public static final String SDK_AD_REQUEST = "ad_requst";
    public static final String SDK_AD_REQUEST_DURATION = "re_duration";
    public static final String SDK_AD_REQUEST_RESULT = "ad_requst_re";
    public static final String SDK_AD_SHOW = "f000";
    public static final String SDK_CLIENT_AD_REQUEST = "cli_requst";
    public static int sPRODUCT_ID = -1;

    public static boolean uploadInstallAppStatistic(Context context, String packageName) {
        long downloadTime;
        String str;
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        if (!isPreInstallState(context, packageName, String.valueOf(OPERATION_LOG_SEQ)) || sMPreInstallMap == null || TextUtils.isEmpty((CharSequence) sMPreInstallMap.get(1))) {
            return false;
        }
        if (TextUtils.isEmpty((CharSequence) sMPreInstallMap.get(10))) {
            return true;
        }
        try {
            downloadTime = Long.parseLong((String) sMPreInstallMap.get(10));
        } catch (Exception e) {
            downloadTime = -1;
        }
        if (System.currentTimeMillis() - downloadTime <= AdSdkContants.GOMO_AD_VALID_CACHE_DURATION) {
            String str2 = (String) sMPreInstallMap.get(0);
            String str3 = (String) sMPreInstallMap.get(1);
            String str4 = (String) sMPreInstallMap.get(3);
            String str5 = (String) sMPreInstallMap.get(4);
            String str6 = (String) sMPreInstallMap.get(5);
            String str7 = (String) sMPreInstallMap.get(6);
            if (TextUtils.isEmpty((CharSequence) sMPreInstallMap.get(7))) {
                str = packageName;
            } else {
                str = (String) sMPreInstallMap.get(7);
            }
            uploadSqe105InstallAppStatistic(context, str2, str3, str4, 1, str5, str6, str7, str, (String) sMPreInstallMap.get(8), (String) sMPreInstallMap.get(9));
            if (!TextUtils.isEmpty((String) sMPreInstallMap.get(11))) {
                uploadRequestUrl(context, (String) sMPreInstallMap.get(11));
            }
        }
        return true;
    }

    public static void uploadActivateAppStatistic(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName) && isPreActivateState(context, packageName, String.valueOf(OPERATION_LOG_SEQ)) && sMPreActiveMap != null && !TextUtils.isEmpty((CharSequence) sMPreActiveMap.get(1))) {
            uploadSqe105StatisticData(context, StringUtils.toInteger(sMPreActiveMap.get(0), -1).intValue(), (String) sMPreActiveMap.get(1), (String) sMPreActiveMap.get(3), 1, (String) sMPreActiveMap.get(4), (String) sMPreActiveMap.get(5), (String) sMPreActiveMap.get(6), TextUtils.isEmpty((CharSequence) sMPreActiveMap.get(7)) ? packageName : (String) sMPreActiveMap.get(7), (String) sMPreActiveMap.get(8), (String) sMPreActiveMap.get(9));
            sMPreActiveMap.clear();
        }
    }

    public static void uploadRequestUrl(final Context context, final String callUrl) {
        if (context != null && !TextUtils.isEmpty(callUrl)) {
            new AdSdkThread("upload_request_url", (Runnable) new Runnable() {
                public void run() {
                    Process.setThreadPriority(10);
                    if (LogUtils.isShowLog()) {
                        LogUtils.d("Ad_SDK", "Statistic.uploadRequestUrl(" + callUrl + ")");
                    }
                    StatisticsManager.getInstance(context).uploadRequestUrl(callUrl);
                }
            }).start();
        }
    }

    public static void uploadSqe105StatisticData(Context context, String sender, String optionCode, int optionResults, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark) {
        uploadSqe105StatisticData(context, getFunctionId(optionCode), sender, optionCode, optionResults, entrance, tabCategory, position, associatedObj, aId, remark);
    }

    public static void uploadSqe105InstallAppStatistic(Context context, String fId, String sender, String optionCode, int optionResults, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark) {
        int funId = StringUtils.toInteger(fId, -1).intValue();
        if (funId != -1) {
            uploadSqe105StatisticData(context, funId, sender, optionCode, optionResults, entrance, tabCategory, position, associatedObj, aId, remark);
        }
    }

    public static void uploadSqe105StatisticData(Context context, int funId, String sender, String optionCode, int optionResults, String entrance, String tabCategory, String position, String associatedObj, String aId, String remark) {
        if (!TextUtils.isEmpty(optionCode)) {
            final boolean isGoKeyboard = AdSdkManager.isGoKeyboard();
            final int i = funId;
            final String str = optionCode;
            final String str2 = sender;
            final int i2 = optionResults;
            final String str3 = entrance;
            final String str4 = tabCategory;
            final String str5 = position;
            final String str6 = associatedObj;
            final String str7 = aId;
            final String str8 = remark;
            final Context context2 = context;
            new AdSdkThread(new Runnable() {
                public void run() {
                    Process.setThreadPriority(10);
                    StringBuffer buffer = new StringBuffer();
                    if (isGoKeyboard) {
                        buffer.append(System.currentTimeMillis());
                        buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    }
                    int newFunId = i > 0 ? i : BaseSeq105OperationStatistic.getFunctionId(str);
                    buffer.append(newFunId);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str2);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(i2);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str3);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str4);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str5);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str6);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str7);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str8);
                    AbsBaseStatistic.uploadStatisticData(context2, BaseSeq105OperationStatistic.OPERATION_LOG_SEQ, newFunId, buffer, new Object[0]);
                }
            }).start();
        }
    }

    protected static synchronized int getFunctionId(String optionCode) {
        int i;
        synchronized (BaseSeq105OperationStatistic.class) {
            i = sPRODUCT_ID;
        }
        return i;
    }
}

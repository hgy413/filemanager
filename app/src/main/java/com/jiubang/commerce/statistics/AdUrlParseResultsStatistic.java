package com.jiubang.commerce.statistics;

import android.content.Context;
import android.os.Build;

public class AdUrlParseResultsStatistic extends BaseSeq101OperationStatistic {
    private static final String ADSDK_OPERATION_ABTEST = "abtest";
    private static final String ADSDK_OPERATION_ADV_STATUS = "adv_status";
    private static final String ADSDK_OPERATION_AD_REQUEST = "ad_requst";
    private static final String ADSDK_OPERATION_AD_REQUEST_RESULT = "ad_requst_result";
    private static final String ADSDK_OPERATION_DOWNLOAD = "dow_on";
    private static final String ADSDK_OPERATION_OPEN_GP = "gp_on";
    private static final String ADSDK_OPERATION_SATISFY = "open_gp";
    public static final int OPTION_RESULTS_ABNORMAL = 2;
    public static final int OPTION_RESULTS_FAILURE = 1;
    public static final int OPTION_RESULTS_NORMAL = 3;
    public static final String REMARK_GOKEYBOARD = "GoKeyboard";
    public static final String REMARK_REQUEST_FAKE_GP = "re_fake_gp";
    public static final String REMARK_REQUEST_FAKE_MOB_TIMER = "re_fake_mob_timer";
    public static final String REMARK_REQUEST_INTELL_NORMAL = "re_intelligent_normal";
    public static final String REMARK_REQUEST_INTELL_NUM = "re_intelligent_num";
    public static final String REMARK_REQUEST_MOB_NORMAL = "re_mobvista_normal";
    public static final String REMARK_REQUEST_MOB_TIMER = "re_mobvista_timer";

    public static void uploadAdStatusStatistic(Context context, String sender, int optionResults, String entrance, String tabCategory, String position, String associatedObj, String remark) {
        Context context2 = context;
        uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_ADV_STATUS), sender, ADSDK_OPERATION_ADV_STATUS, optionResults, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, tabCategory, position, associatedObj, remark);
    }

    public static void uploadGPOpen(Context context, String position, String remark) {
        if (Build.VERSION.SDK_INT != 23) {
            Context context2 = context;
            uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_OPEN_GP), (String) null, ADSDK_OPERATION_OPEN_GP, 0, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, (String) null, position, (String) null, remark);
        }
    }

    public static void uploadDownload(Context context, String position, String remark) {
        Context context2 = context;
        uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_DOWNLOAD), (String) null, ADSDK_OPERATION_DOWNLOAD, 0, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, (String) null, position, (String) null, remark);
    }

    public static void uploadSatisfyCondition(Context context, String sender, String position, String remark) {
        if (Build.VERSION.SDK_INT != 23) {
            Context context2 = context;
            uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_SATISFY), sender, ADSDK_OPERATION_SATISFY, 0, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, (String) null, position, (String) null, remark);
        }
    }

    public static void uploadAdRequest(Context context, String entrance, String tabCategory, String position, String remark) {
        Context context2 = context;
        uploadOperationStatisticData(context2, getFunctionId("ad_requst"), (String) null, "ad_requst", 0, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, tabCategory, position, (String) null, remark);
    }

    public static void uploadAdRequestResult(Context context, String tabCategory, String position, String remark) {
        Context context2 = context;
        uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_AD_REQUEST_RESULT), (String) null, ADSDK_OPERATION_AD_REQUEST_RESULT, 0, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, tabCategory, position, (String) null, remark);
    }

    public static void uploadABTestStatistic(Context context, String sender, int optionResults) {
        Context context2 = context;
        uploadOperationStatisticData(context2, getFunctionId(ADSDK_OPERATION_ABTEST), sender, ADSDK_OPERATION_ABTEST, optionResults, "" + BaseSeq105OperationStatistic.sPRODUCT_ID, (String) null, (String) null, (String) null, (String) null);
    }

    private static int getFunctionId(String optionCode) {
        return BaseSeq101OperationStatistic.FUNCTION_ID_AD_URL_PARSE;
    }
}

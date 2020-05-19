package com.jiubang.commerce.statistics;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.thread.AdSdkThread;

public class BaseSeq101OperationStatistic extends AbsBaseStatistic {
    protected static final String ASSOCIATED_OBJ_SEPARATE = ";";
    public static final int FUNCTION_ID_AD_URL_PARSE = 400;
    public static final int OPERATION_LOG_SEQ = 101;

    protected static void uploadOperationStatisticData(Context context, int funId, String sender, String optionCode, int optionResults, String entrance, String tabCategory, String position, String associatedObj, String remark) {
        if (!TextUtils.isEmpty(optionCode)) {
            final boolean isGoKeyboard = AdSdkManager.isGoKeyboard();
            final int i = funId;
            final String str = sender;
            final String str2 = optionCode;
            final int i2 = optionResults;
            final String str3 = entrance;
            final String str4 = tabCategory;
            final String str5 = position;
            final String str6 = associatedObj;
            final String str7 = remark;
            final Context context2 = context;
            new AdSdkThread(new Runnable() {
                public void run() {
                    Process.setThreadPriority(10);
                    StringBuffer buffer = new StringBuffer();
                    if (isGoKeyboard) {
                        buffer.append(System.currentTimeMillis());
                        buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    }
                    buffer.append(i);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str2);
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
                    AbsBaseStatistic.uploadStatisticData(context2, BaseSeq101OperationStatistic.OPERATION_LOG_SEQ, i, buffer, new Object[0]);
                }
            }).start();
        }
    }
}

package com.jiubang.commerce.statistics;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import com.jiubang.commerce.ad.AdSdkContants;
import com.jiubang.commerce.ad.manager.AdSdkManager;
import com.jiubang.commerce.thread.AdSdkThread;

public class BaseSeq102OperationStatistic extends AbsBaseStatistic {
    protected static final String ASSOCIATED_OBJ_SEPARATE = ";";
    public static final int OPERATION_LOG_SEQ = 102;
    public static int sPRODUCT_ID = -1;

    public static void uploadAdInfo(Context context, String layoutInfo, int type) {
        uploadSqe102StatisticData(context, 646, layoutInfo, type, getProductId(), "adv_info");
    }

    public static void uploadAdUrl(Context context, String url, int type) {
        uploadSqe102StatisticData(context, 646, url, type, getProductId(), "adv_url");
    }

    public static void uploadSqe102StatisticData(Context context, int funId, String layoutInfo, int type, int position, String mask) {
        if (!TextUtils.isEmpty(layoutInfo)) {
            final boolean isGoKeyboard = AdSdkManager.isGoKeyboard();
            final int i = funId;
            final String str = layoutInfo;
            final int i2 = type;
            final int i3 = position;
            final String str2 = mask;
            final Context context2 = context;
            new AdSdkThread(new Runnable() {
                public void run() {
                    Process.setThreadPriority(10);
                    StringBuffer buffer = new StringBuffer();
                    if (isGoKeyboard) {
                        buffer.append(System.currentTimeMillis());
                        buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    }
                    int newFunId = i;
                    buffer.append(newFunId);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(i2);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(i3);
                    buffer.append(AdSdkContants.SYMBOL_DOUBLE_LINE);
                    buffer.append(str2);
                    AbsBaseStatistic.uploadStatisticData(context2, BaseSeq102OperationStatistic.OPERATION_LOG_SEQ, newFunId, buffer, new Object[0]);
                }
            }).start();
        }
    }

    public static int getProductId() {
        return BaseSeq105OperationStatistic.sPRODUCT_ID;
    }
}

package com.jiubang.commerce.utils;

import com.gau.utils.net.util.HeartSetting;
import com.jb.ga0.commerce.util.LogUtils;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallbackUtil {
    public static final int HTTP_RESPONSE_CODE_OK = 200;
    public static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    public static void sendCallbackOnThread(final String callbackUrl) {
        if (callbackUrl != null && !callbackUrl.equals("")) {
            sExecutorService.execute(new Runnable() {
                public void run() {
                    CallbackUtil.sendCallback(callbackUrl);
                }
            });
        }
    }

    public static void sendCallback(String callbackUrl) {
        if (callbackUrl != null && !"".equals(callbackUrl)) {
            for (int count = 5; count > 0 && !requestOneCallback(callbackUrl); count--) {
                try {
                    Thread.sleep(HeartSetting.DEFAULT_HEART_TIME_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean requestOneCallback(String callbackUrl) {
        if (callbackUrl == null || callbackUrl.equals("")) {
            return false;
        }
        try {
            if (HttpRequestUtils.executeHttpRequest(callbackUrl).getStatusLine().getStatusCode() != 200) {
                return false;
            }
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", callbackUrl + " 回调成功");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (!LogUtils.isShowLog()) {
                return false;
            }
            LogUtils.d("Ad_SDK", callbackUrl + " 回调失败");
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            if (!LogUtils.isShowLog()) {
                return false;
            }
            LogUtils.d("Ad_SDK", callbackUrl + " 回调失败");
            return false;
        }
    }
}

package com.jiubang.commerce.ad.bean;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.manager.AdSdkSetting;

public class AdOldUserTagInfoBean extends AdUserTagInfoBean {
    public boolean isValid(Context context) {
        long lastUpdateTime = AdSdkSetting.getInstance(context).getLastOldUserTagUpdateTime();
        long intervalUpdateTime = System.currentTimeMillis() - lastUpdateTime;
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "lastUpdateTime: " + lastUpdateTime + "intervalUpdateTime: " + intervalUpdateTime);
        }
        if (intervalUpdateTime <= 0 || intervalUpdateTime > AdSdkSetting.ADSDK_OLD_USER_TAG_VALIAD_TIME) {
            return false;
        }
        return true;
    }
}

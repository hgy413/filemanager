package com.jiubang.commerce.ad.bean;

import android.content.Context;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.manager.UserTypeManager;
import java.util.HashMap;

public class UserTypeBean {
    private HashMap<String, String> mHashMap = new HashMap<>();

    public boolean isValid(Context context, String buychannel) {
        long lastUpdateTime = UserTypeManager.getInstance(context).getLastUserTypeUpdateTime(buychannel);
        long intervalUpdateTime = System.currentTimeMillis() - lastUpdateTime;
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "lastUpdateTime: " + lastUpdateTime + "intervalUpdateTime: " + intervalUpdateTime);
        }
        if (intervalUpdateTime <= 0 || intervalUpdateTime > 86400000) {
            return false;
        }
        return true;
    }

    public void setBuyChannel(String buychannel, String buychanneltype) {
        if (buychannel != null && buychanneltype != null) {
            this.mHashMap.put(buychannel, buychanneltype);
        }
    }
}

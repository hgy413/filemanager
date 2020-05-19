package com.jiubang.commerce.ad.bean;

import android.content.Context;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.manager.AdSdkSetting;
import java.util.ArrayList;
import java.util.List;

public class AdUserTagInfoBean {
    private List<String> mUserTags = new ArrayList();

    public boolean isValid(Context context) {
        long lastUpdateTime = AdSdkSetting.getInstance(context).getLastUserTagUpdateTime();
        long intervalUpdateTime = System.currentTimeMillis() - lastUpdateTime;
        if (LogUtils.isShowLog()) {
            LogUtils.i("maple", "lastUpdateTime: " + lastUpdateTime + "intervalUpdateTime: " + intervalUpdateTime);
        }
        if (intervalUpdateTime <= 0 || intervalUpdateTime > 86400000) {
            return false;
        }
        return true;
    }

    public boolean isTag(String tag) {
        if (this.mUserTags == null || !this.mUserTags.contains(tag)) {
            return false;
        }
        return true;
    }

    public void setUserTags(String tags) {
        if (tags != null) {
            this.mUserTags.clear();
            String[] tagArray = tags.split(",");
            for (int i = tagArray.length; i > 0; i--) {
                this.mUserTags.add(tagArray[i - 1]);
            }
        }
    }

    public List<String> getUserTags() {
        return this.mUserTags;
    }

    public String getUserTagStr() {
        if (this.mUserTags == null) {
            return null;
        }
        String str = "";
        for (String tag : this.mUserTags) {
            str = str + tag + ",";
        }
        if (!TextUtils.isEmpty(str)) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    public boolean isEmpty() {
        if (this.mUserTags == null || this.mUserTags.size() == 0) {
            return true;
        }
        return false;
    }
}

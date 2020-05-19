package com.jiubang.commerce.buychannel.buyChannel.bean;

import android.text.TextUtils;
import com.jiubang.commerce.buychannel.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class Statistics19Bean {
    public int mChannel = 200;
    public boolean mIsOldUser;
    public boolean mIsPay = false;
    public boolean mNeedRootInfo = false;
    public String mP19Key = BuildConfig.FLAVOR;

    public Statistics19Bean(int channel, boolean isOldUser, String p19Key, boolean needRootInfo, boolean pay) {
        this.mChannel = channel;
        this.mIsOldUser = isOldUser;
        this.mP19Key = p19Key;
        this.mNeedRootInfo = needRootInfo;
        this.mIsPay = pay;
    }

    public String toJsonStr() {
        JSONObject object = new JSONObject();
        try {
            object.put("mChannel", this.mChannel);
            object.put("mIsPay", this.mIsPay);
            object.put("mP19Key", this.mP19Key);
            object.put("mNeedRootInfo", this.mNeedRootInfo);
            object.put("mIsOldUser", this.mIsOldUser);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Statistics19Bean jsonStr2Bean(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(jsonString);
            return new Statistics19Bean(object.optInt("mChannel"), object.optBoolean("mIsOldUser"), object.optString("mP19Key"), object.optBoolean("mNeedRootInfo"), object.optBoolean("mIsPay"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

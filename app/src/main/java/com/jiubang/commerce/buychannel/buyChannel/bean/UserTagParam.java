package com.jiubang.commerce.buychannel.buyChannel.bean;

import android.text.TextUtils;
import com.jiubang.commerce.buychannel.BuildConfig;
import org.json.JSONObject;

public class UserTagParam {
    public final String mAccessKey;
    public final String mChannel;
    public final String mGoId;
    public final String mGoogleId;
    public final String mProductKey;

    public UserTagParam(String goId, String googleId, String channel, String productKey, String accessKey) {
        this.mGoId = goId;
        this.mGoogleId = googleId;
        this.mChannel = channel;
        this.mProductKey = productKey;
        this.mAccessKey = accessKey;
    }

    public String toJsonStr() {
        JSONObject object = new JSONObject();
        try {
            object.put("goId", this.mGoId == null ? BuildConfig.FLAVOR : this.mGoId);
            object.put("googleId", this.mGoogleId == null ? BuildConfig.FLAVOR : this.mGoogleId);
            object.put("channel", this.mChannel == null ? BuildConfig.FLAVOR : this.mChannel);
            object.put("productKey", this.mProductKey == null ? BuildConfig.FLAVOR : this.mProductKey);
            object.put("accessKey", this.mAccessKey == null ? BuildConfig.FLAVOR : this.mAccessKey);
            return object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UserTagParam jsonStr2Bean(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(jsonString);
            return new UserTagParam(object.optString("goId"), object.optString("googleId"), object.optString("channel"), object.optString("productKey"), object.optString("accessKey"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

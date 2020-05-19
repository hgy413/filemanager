package com.jiubang.commerce.buychannel.buyChannel.bean;

import android.text.TextUtils;
import com.jiubang.commerce.buychannel.BuildConfig;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class BuyChannelBean {
    private String campaign = "null";
    private boolean isSuccessCheck = false;
    private String mBuyChannel = BuySdkConstants.UNKNOWN_BUYCHANNEL;
    private String mCampaignId = "null";
    private String mChannelFrom = "un_known";
    private String mFirstUserType = "organic";
    private int mSecondUserType = -1;

    public boolean isSuccessCheck() {
        return this.isSuccessCheck;
    }

    public void setSuccessCheck(boolean successCheck) {
        this.isSuccessCheck = successCheck;
    }

    public boolean isUserBuy() {
        if (this.mFirstUserType.equals("userbuy") || this.mFirstUserType.equals("apkbuy")) {
            return true;
        }
        return false;
    }

    public boolean isWithCount() {
        if (this.mFirstUserType.equals("withCount")) {
            return true;
        }
        return false;
    }

    public boolean isOrganic() {
        if (this.mFirstUserType.equals("organic")) {
            return true;
        }
        return false;
    }

    public boolean isApkBuy() {
        if (this.mFirstUserType.equals("apkbuy")) {
            return true;
        }
        return false;
    }

    public int getSecondUserType() {
        return this.mSecondUserType;
    }

    public void setSecondUserType(int sencondType) {
        this.mSecondUserType = sencondType;
    }

    public String getFirstUserType() {
        return this.mFirstUserType;
    }

    public void setFirstUserType(String firstUserType) {
        this.mFirstUserType = firstUserType;
    }

    public String getChannelFrom() {
        return this.mChannelFrom;
    }

    public void setChannelFrom(String channelFrom) {
        this.mChannelFrom = channelFrom;
    }

    public String getBuyChannel() {
        return this.mBuyChannel;
    }

    public void setBuyChannel(String buyChannel) {
        this.mBuyChannel = buyChannel;
    }

    public String getCampaign() {
        return this.campaign;
    }

    public void setCampaign(String campaign2) {
        this.campaign = campaign2;
    }

    public String getCampaignId() {
        return this.mCampaignId;
    }

    public void setCampaignId(String campaignId) {
        this.mCampaignId = campaignId;
    }

    public String toString() {
        return "buyChannel:[" + this.mBuyChannel + "]channelFrom:[" + this.mChannelFrom + "]UserType:[" + this.mFirstUserType + "]JuniorUserType:[" + this.mSecondUserType + "]，是否成功获取用户身份 :" + this.isSuccessCheck;
    }

    public String toJsonStr() {
        JSONObject object = new JSONObject();
        try {
            object.put("buyChannel", this.mBuyChannel == null ? BuildConfig.FLAVOR : this.mBuyChannel);
            object.put("channelFrom", this.mChannelFrom == null ? BuildConfig.FLAVOR : this.mChannelFrom);
            object.put("firstUserType", this.mFirstUserType == null ? BuildConfig.FLAVOR : this.mFirstUserType);
            object.put(BuySdkConstants.OLD_USERTUPE, this.mSecondUserType);
            object.put("isSuccessCheck", this.isSuccessCheck);
            object.put(BuySdkConstants.CAMPAIGN, this.campaign);
            object.put(BuySdkConstants.CAMPAIGN_ID, this.mCampaignId);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BuyChannelBean jsonStr2Bean(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(jsonString);
            BuyChannelBean buyChannelBean = new BuyChannelBean();
            buyChannelBean.setChannelFrom(object.optString("channelFrom"));
            buyChannelBean.setBuyChannel(object.optString("buyChannel"));
            buyChannelBean.setFirstUserType(object.optString("firstUserType"));
            buyChannelBean.setSecondUserType(Integer.parseInt(object.optString(BuySdkConstants.OLD_USERTUPE)));
            buyChannelBean.setSuccessCheck(Boolean.parseBoolean(object.optString("isSuccessCheck")));
            buyChannelBean.setCampaign(object.optString(BuySdkConstants.CAMPAIGN));
            buyChannelBean.setCampaignId(object.optString(BuySdkConstants.CAMPAIGN_ID));
            return buyChannelBean;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

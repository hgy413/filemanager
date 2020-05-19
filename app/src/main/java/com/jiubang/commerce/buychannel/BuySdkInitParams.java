package com.jiubang.commerce.buychannel;

import com.jiubang.commerce.utils.StringUtils;
import java.util.List;

public class BuySdkInitParams {
    public final boolean isSetImei;
    public final String mAccessKey;
    public final List<String> mAdwordsGdnCampaignids;
    public final int mChannel;
    public final boolean mIsApkUpLoad45;
    public final boolean mIsGoKeyboard;
    public final boolean mIsOldUserWithoutSdk;
    public final String mOldBuyChannel;
    public final int mP45FunId;
    public final String mProcessName;
    public final String mProductKey;
    public final IProtocal19Handler mProtocal19Handler;
    public final String mUsertypeProtocalCId;

    public interface IProtocal19Handler {
        void uploadProtocal19();
    }

    private BuySdkInitParams(Builder builder) {
        this.mChannel = builder.mChannel;
        this.mP45FunId = builder.mP45FunId;
        this.mUsertypeProtocalCId = builder.mUsertypeProtocalCId;
        this.mProtocal19Handler = builder.mProtocal19Handler;
        this.mIsGoKeyboard = builder.mIsGoKeyboard;
        this.mProductKey = builder.mProductKey;
        this.mAccessKey = builder.mAccessKey;
        this.mIsOldUserWithoutSdk = builder.mIsOldUserWithoutSdk;
        this.mOldBuyChannel = builder.mOldBuyChannel;
        this.mProcessName = builder.mProcessName;
        this.mAdwordsGdnCampaignids = builder.mAdwordsGdnCampaignids;
        this.isSetImei = builder.mIsSetImei;
        this.mIsApkUpLoad45 = builder.mIsApkUpLoad45;
    }

    public static class Builder {
        public String mAccessKey;
        public List<String> mAdwordsGdnCampaignids;
        public int mChannel;
        public boolean mIsApkUpLoad45 = false;
        public boolean mIsGoKeyboard;
        public boolean mIsOldUserWithoutSdk;
        public boolean mIsSetImei = true;
        public String mOldBuyChannel;
        public int mP45FunId;
        public String mProcessName;
        public String mProductKey;
        public IProtocal19Handler mProtocal19Handler;
        public String mUsertypeProtocalCId;

        public Builder(String channel, int p45FunId, String usertypeProtocalCId, IProtocal19Handler p19Handler, boolean isGoKeyboard, String productKey, String accessKey) {
            int c = StringUtils.toInteger(channel, 0).intValue();
            this.mChannel = Integer.parseInt(c <= 0 ? "200" : String.valueOf(c));
            this.mP45FunId = p45FunId;
            this.mUsertypeProtocalCId = usertypeProtocalCId;
            this.mProtocal19Handler = p19Handler;
            this.mIsGoKeyboard = isGoKeyboard;
            this.mProductKey = productKey;
            this.mAccessKey = accessKey;
        }

        public Builder isSetImei(boolean v) {
            this.mIsSetImei = v;
            return this;
        }

        public Builder isApkUpLoad45(boolean v) {
            this.mIsApkUpLoad45 = v;
            return this;
        }

        public Builder isOldUserWithoutSdk(boolean v) {
            this.mIsOldUserWithoutSdk = v;
            return this;
        }

        public Builder oldBuyChannel(String v) {
            this.mOldBuyChannel = v;
            return this;
        }

        public Builder processName(String v) {
            this.mProcessName = v;
            return this;
        }

        public Builder adwordsGdnCampaignids(List<String> v) {
            this.mAdwordsGdnCampaignids = v;
            return this;
        }

        public BuySdkInitParams build() {
            return new BuySdkInitParams(this);
        }
    }
}

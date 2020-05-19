package com.jiubang.commerce.ad.params;

public class PresolveParams {
    public final long mCustomCacheDuration;
    public final boolean mIsControlled;
    public final boolean mRepeatClickEnable;
    public final ReferSendType mSendReferBroadcast;
    public final int mUAType;
    public final boolean mUploadGA;
    public final boolean mUseCache;

    public enum ReferSendType {
        NO,
        ALWAYS,
        DEPENDS
    }

    public PresolveParams(Builder builder) {
        this.mRepeatClickEnable = builder.mRepeatClickEnable;
        this.mIsControlled = builder.mIsControlled;
        this.mUseCache = builder.mUseCache;
        this.mCustomCacheDuration = builder.mCustomCacheDuration;
        this.mUAType = builder.mUAType;
        this.mUploadGA = builder.mUploadGA;
        this.mSendReferBroadcast = builder.mSendReferBroadcast;
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public long mCustomCacheDuration = -1;
        /* access modifiers changed from: private */
        public boolean mIsControlled = true;
        /* access modifiers changed from: private */
        public boolean mRepeatClickEnable = true;
        /* access modifiers changed from: private */
        public ReferSendType mSendReferBroadcast = ReferSendType.NO;
        /* access modifiers changed from: private */
        public int mUAType = -1;
        /* access modifiers changed from: private */
        public boolean mUploadGA = false;
        /* access modifiers changed from: private */
        public boolean mUseCache = true;

        public Builder repeatClickEnable(boolean repeatClickEnable) {
            this.mRepeatClickEnable = repeatClickEnable;
            return this;
        }

        public Builder isControlled(boolean isControlled) {
            this.mIsControlled = isControlled;
            return this;
        }

        public Builder useCache(boolean useCache) {
            this.mUseCache = useCache;
            return this;
        }

        public Builder customCacheDuration(long duration) {
            this.mCustomCacheDuration = duration;
            return this;
        }

        public Builder uaType(int uaType) {
            this.mUAType = uaType;
            return this;
        }

        public Builder uploadGA(boolean uploadGA) {
            this.mUploadGA = uploadGA;
            return this;
        }

        public Builder sendReferBroadcast(ReferSendType type) {
            this.mSendReferBroadcast = type;
            return this;
        }

        public PresolveParams build() {
            return new PresolveParams(this);
        }
    }
}

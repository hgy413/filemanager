package com.jiubang.commerce.ad.http.bean.flash;

import org.json.JSONObject;

public class FlashBean {
    public static final int TYPE_FLASH_SCREEN = 5;
    private int mAdType;
    private String mBanner;
    private int mButtonIntent;
    private String mButtonIntentParam;
    private String mButtonIntentSource;
    private String mButtonName;
    private String mCParams;
    private String mDescription;
    private String mIcon;
    private String mIconDesc;
    private String mIconName;
    private int mIconPraise;
    private int mLayout;
    private int mModulecontentId;
    private int mMoreIntent;
    private String mMoreIntentParam;
    private String mMoreIntentSource;
    private String mMoreName;
    private String mName;
    private int mPraise;
    private int mRId;
    private long mShowDate;
    private long mShowDateEnd;
    private String mSuperscriptUrl;
    private String mTitle;

    public static FlashBean parseJson(JSONObject json) {
        if (json == null || json.length() < 1) {
            return null;
        }
        FlashBean bean = new FlashBean();
        bean.mModulecontentId = json.optInt("modulecontent_id");
        bean.mRId = json.optInt("rid");
        bean.mSuperscriptUrl = json.optString("superscriptUrl");
        bean.mTitle = json.optString("title");
        bean.mName = json.optString("name");
        bean.mDescription = json.optString("description");
        bean.mBanner = json.optString("banner");
        bean.mMoreName = json.optString("more_name");
        bean.mMoreIntent = json.optInt("more_intent");
        bean.mMoreIntentParam = json.optString("more_intent_param");
        bean.mMoreIntentSource = json.optString("more_intent_source");
        bean.mIcon = json.optString("icon");
        bean.mIconName = json.optString("icon_name");
        bean.mIconDesc = json.optString("icon_desc");
        bean.mIconPraise = json.optInt("icon_praise");
        bean.mButtonName = json.optString("button_name");
        bean.mButtonIntent = json.optInt("button_intent");
        bean.mButtonIntentParam = json.optString("button_intent_param");
        bean.mButtonIntentSource = json.optString("button_intent_source");
        bean.mLayout = json.optInt("layout");
        bean.mAdType = json.optInt("adtype");
        bean.mShowDate = json.optLong("showdate");
        bean.mShowDateEnd = json.optLong("showdate_end");
        bean.mPraise = json.optInt("praise");
        bean.mCParams = json.optString("cparams");
        return bean;
    }

    public int getModulecontentId() {
        return this.mModulecontentId;
    }

    public int getRId() {
        return this.mRId;
    }

    public String getSuperscriptUrl() {
        return this.mSuperscriptUrl;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getBanner() {
        return this.mBanner;
    }

    public String getMoreName() {
        return this.mMoreName;
    }

    public int getMoreIntent() {
        return this.mMoreIntent;
    }

    public String getMoreIntentParam() {
        return this.mMoreIntentParam;
    }

    public String getMoreIntentSource() {
        return this.mMoreIntentSource;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public String getIconName() {
        return this.mIconName;
    }

    public String getIconDesc() {
        return this.mIconDesc;
    }

    public int getIconPraise() {
        return this.mIconPraise;
    }

    public String getButtonName() {
        return this.mButtonName;
    }

    public int getButtonIntent() {
        return this.mButtonIntent;
    }

    public String getButtonIntentParam() {
        return this.mButtonIntentParam;
    }

    public String getButtonIntentSource() {
        return this.mButtonIntentSource;
    }

    public int getLayout() {
        return this.mLayout;
    }

    public int getAdType() {
        return this.mAdType;
    }

    public long getShowDate() {
        return this.mShowDate;
    }

    public long getShowDateEnd() {
        return this.mShowDateEnd;
    }

    public int getPraise() {
        return this.mPraise;
    }

    public String getCParams() {
        return this.mCParams;
    }
}

package com.jiubang.commerce.product;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.ad.ResourcesProvider;

public class Product implements Parcelable {
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    public static String DEFUAL = "-1";
    public static final String XML_NAME_AD_REQUEST_ACCESS_KEY = "cfg_commerce_ad_request_access_key";
    public static final String XML_NAME_AD_REQUEST_PRODUCT_KEY = "cfg_commerce_ad_request_product_key";
    public static final String XML_NAME_DATA_CHANNEL = "cfg_commerce_data_channel";
    public static final String XML_NAME_ENTRANCE_ID = "cfg_commerce_entrance_id";
    public static final String XML_NAME_INTELLIGENT_ADPOS = "cfg_commerce_intelligent_adpos";
    public static final String XML_NAME_INTELLIGENT_ADPOS_INSTALL_PREPARSE = "cfg_commerce_intelligent_adpos_install_preparse";
    public static final String XML_NAME_INTELLIGENT_ADPOS_MOB = "cfg_commerce_intelligent_adpos_mob";
    public static final String XML_NAME_INTELLIGENT_ADPOS_MOB_NEW = "cfg_commerce_intelligent_adpos_mob_new";
    public static final String XML_NAME_KEYBOARD_NEW_STATISTIC = "cfg_commerce_keyboard_new_statistic";
    public static final String XML_NAME_NATIVE_PRESOLVE_REQUEST_ID = "cfg_commerce_native_presolve_request_id";
    public static final String XML_NAME_PRODUCT_ID = "cfg_commerce_cid";
    public static final String XML_NAME_STATISTIC_ID_105 = "cfg_commerce_statistic_id_105";
    public static final String XML_NAME_SYSTEM_INSTALL_ADPOS = "cfg_commerce_system_install_adpos";
    private boolean isNewInit = false;
    private String mAdRequestAccessKey;
    private String mAdRequestProductKey;
    private int[] mAdposes;
    private String mChannel = "200";
    private String mDataChannel = DEFUAL;
    private String mEntranceId = DEFUAL;
    private String mGoId = "1";
    private String mGoogleId = "123456789";
    private int mIntelligentAdPos = -1;
    private int mIntelligentAdPosInstallPreparse = -1;
    private int mIntelligentAdPosMobNew = -1;
    private int mIntelligentAdposMob = -1;
    private boolean mIsKeyBoardNewStatistic = false;
    private int mNativePresolveRequestId = -1;
    private String mProcessName = "";
    private String mProductId = DEFUAL;
    private int mStatisticId105 = -1;
    private int mSystemInstallAdPos = -1;

    public Product(String cid, String dataChannel, String entranceId) {
        this.mProductId = cid;
        this.mDataChannel = dataChannel;
        this.mEntranceId = entranceId;
        this.mEntranceId = ("1".equals(this.mEntranceId) || "2".equals(this.mEntranceId)) ? this.mEntranceId : "1";
        LogUtils.i("Ad_SDK", "旧初始化[产品ID:" + this.mProductId + ",数据渠道:" + this.mDataChannel + ",入口:" + this.mEntranceId + "]");
    }

    public Product(Context context) {
        ResourcesProvider resourcesProvider = ResourcesProvider.getInstance(context);
        this.isNewInit = true;
        this.mProductId = resourcesProvider.getString(XML_NAME_PRODUCT_ID);
        this.mDataChannel = resourcesProvider.getString(XML_NAME_DATA_CHANNEL);
        this.mEntranceId = resourcesProvider.getString(XML_NAME_ENTRANCE_ID);
        this.mEntranceId = ("1".equals(this.mEntranceId) || "2".equals(this.mEntranceId)) ? this.mEntranceId : "1";
        this.mAdRequestProductKey = resourcesProvider.getString(XML_NAME_AD_REQUEST_PRODUCT_KEY);
        this.mAdRequestAccessKey = resourcesProvider.getString(XML_NAME_AD_REQUEST_ACCESS_KEY);
        this.mStatisticId105 = resourcesProvider.getInteger(XML_NAME_STATISTIC_ID_105);
        try {
            this.mIntelligentAdPos = resourcesProvider.getInteger(XML_NAME_INTELLIGENT_ADPOS);
        } catch (Exception e) {
        }
        try {
            this.mIntelligentAdposMob = resourcesProvider.getInteger(XML_NAME_INTELLIGENT_ADPOS_MOB);
        } catch (Exception e2) {
        }
        try {
            this.mIntelligentAdPosMobNew = resourcesProvider.getInteger(XML_NAME_INTELLIGENT_ADPOS_MOB_NEW);
        } catch (Exception e3) {
        }
        try {
            this.mIntelligentAdPosInstallPreparse = resourcesProvider.getInteger(XML_NAME_INTELLIGENT_ADPOS_INSTALL_PREPARSE);
        } catch (Exception e4) {
        }
        this.mAdposes = new int[]{this.mIntelligentAdPos, this.mIntelligentAdposMob, this.mIntelligentAdPosMobNew, this.mIntelligentAdPosInstallPreparse};
        this.mNativePresolveRequestId = resourcesProvider.getInteger(XML_NAME_NATIVE_PRESOLVE_REQUEST_ID);
        this.mSystemInstallAdPos = resourcesProvider.getInteger(XML_NAME_SYSTEM_INSTALL_ADPOS);
        if (context.getResources().getIdentifier(XML_NAME_KEYBOARD_NEW_STATISTIC, "integer", context.getPackageName()) != 0) {
            this.mIsKeyBoardNewStatistic = true;
        } else {
            this.mIsKeyBoardNewStatistic = false;
        }
        LogUtils.i("Ad_SDK", "新初始化[产品ID:" + this.mProductId + ",数据渠道:" + this.mDataChannel + ",入口:" + this.mEntranceId + ",ProductKey:" + this.mAdRequestProductKey + ",AccessKey:" + this.mAdRequestAccessKey + ",105统计:" + this.mStatisticId105 + ",智预-AdPos:" + this.mIntelligentAdPos + ",智预-AdposMob:" + this.mIntelligentAdposMob + ",智预-AdPosMobNew:" + this.mIntelligentAdPosMobNew + ",智预-AdPosInstallPreparse:" + this.mIntelligentAdPosInstallPreparse + ",native抓取:" + this.mNativePresolveRequestId + ",系统安装抓取:" + this.mSystemInstallAdPos + "]");
    }

    public void setCid(String cid) {
        this.mProductId = cid;
    }

    public Product setGoId(String goId) {
        this.mGoId = goId;
        return this;
    }

    public Product setGoogleId(String googleId) {
        this.mGoogleId = googleId;
        return this;
    }

    public Product setChannel(String channel) {
        this.mChannel = channel;
        return this;
    }

    public Product setProcessName(String processName) {
        this.mProcessName = processName;
        return this;
    }

    public boolean isNewInit() {
        return this.isNewInit;
    }

    public String getCid() {
        return this.mProductId;
    }

    public String getDataChannel() {
        return this.mDataChannel;
    }

    public String getAdRequestProductKey() {
        check("ProductKey");
        return this.mAdRequestProductKey;
    }

    public String getAdRequestAccessKey() {
        check("AccessKey");
        return this.mAdRequestAccessKey;
    }

    public int getStatisticId105() {
        check("StatisticId105");
        return this.mStatisticId105;
    }

    public int getNativePresolveRequestId() {
        check("");
        return this.mNativePresolveRequestId;
    }

    public int getSystemInstallAdPos() {
        check("");
        return this.mSystemInstallAdPos;
    }

    public String getEntranceId() {
        return this.mEntranceId;
    }

    public int[] getAdposes() {
        check("");
        return this.mAdposes;
    }

    public String getGoId() {
        return this.mGoId;
    }

    public String getGoogleId() {
        return this.mGoogleId;
    }

    public String getChannel() {
        return this.mChannel;
    }

    public void setIsKeyBoardNewStatistic(boolean IsKeyBoardNewStatistic) {
        this.mIsKeyBoardNewStatistic = IsKeyBoardNewStatistic;
    }

    public boolean isKeyBoardNewStatistic() {
        return this.mIsKeyBoardNewStatistic;
    }

    public String getProcessName() {
        return this.mProcessName;
    }

    private void check(String msg) {
        if (!this.isNewInit) {
            throw new IllegalAccessError("旧流程不能从Product中获取特有参数" + msg);
        }
    }

    public boolean isGoKeyboard() {
        String cid = getCid();
        if (isKeyBoardNewStatistic() || TextUtils.isEmpty(cid) || (!"4".equals(cid) && !"9".equals(cid) && !"31".equals(cid) && !"39".equals(cid) && !"90".equals(cid) && !"53".equals(cid))) {
            return false;
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }

    protected Product(Parcel in) {
        boolean z = false;
        this.isNewInit = in.readByte() != 0 ? true : z;
        this.mProductId = in.readString();
        this.mDataChannel = in.readString();
        this.mEntranceId = in.readString();
        this.mAdRequestProductKey = in.readString();
        this.mAdRequestAccessKey = in.readString();
        this.mStatisticId105 = in.readInt();
        this.mIntelligentAdPos = in.readInt();
        this.mIntelligentAdposMob = in.readInt();
        this.mIntelligentAdPosMobNew = in.readInt();
        this.mIntelligentAdPosInstallPreparse = in.readInt();
        this.mAdposes = in.createIntArray();
        this.mNativePresolveRequestId = in.readInt();
        this.mSystemInstallAdPos = in.readInt();
        this.mGoId = in.readString();
        this.mGoogleId = in.readString();
        this.mChannel = in.readString();
        this.mProcessName = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.isNewInit ? 1 : 0));
        dest.writeString(this.mProductId);
        dest.writeString(this.mDataChannel);
        dest.writeString(this.mEntranceId);
        dest.writeString(this.mAdRequestProductKey);
        dest.writeString(this.mAdRequestAccessKey);
        dest.writeInt(this.mStatisticId105);
        dest.writeInt(this.mIntelligentAdPos);
        dest.writeInt(this.mIntelligentAdposMob);
        dest.writeInt(this.mIntelligentAdPosMobNew);
        dest.writeInt(this.mIntelligentAdPosInstallPreparse);
        dest.writeIntArray(this.mAdposes);
        dest.writeInt(this.mNativePresolveRequestId);
        dest.writeInt(this.mSystemInstallAdPos);
        dest.writeString(this.mGoId);
        dest.writeString(this.mGoogleId);
        dest.writeString(this.mChannel);
        dest.writeString(this.mProcessName);
    }

    public String toString() {
        String str = "[mProductId:" + this.mProductId + ",mDataChannel:" + this.mDataChannel + ",mEntranceId:" + this.mEntranceId + ",mGoId:" + this.mGoId + ",mGoogleId:" + this.mGoogleId + ",mChannel:" + this.mChannel + ",mProcessName" + this.mProcessName;
        if (isNewInit()) {
            str = ("new" + str) + ",mAdRequestProductKey:" + this.mAdRequestProductKey + ",mAdRequestAccessKey:" + this.mAdRequestAccessKey + ",mStatisticId105:" + this.mStatisticId105 + ",mIntelligentAdPos:" + this.mIntelligentAdPos + ",mIntelligentAdposMob:" + this.mIntelligentAdposMob + ",mIntelligentAdPosMobNew:" + this.mIntelligentAdPosMobNew + ",mIntelligentAdPosInstallPreparse:" + this.mIntelligentAdPosInstallPreparse + ",mIntelligentAdPosMobNew:" + this.mNativePresolveRequestId + ",mNativePresolveRequestId:" + this.mNativePresolveRequestId;
        }
        return str + "]";
    }
}

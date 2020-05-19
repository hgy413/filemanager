package com.jiubang.commerce.buychannel;

import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;

public class BuySdkConstants {
    public static final int ALARM_ID = 1999;
    public static final int ALARM_ID_VERSIONCODE = 18542;
    public static final String ALARM_NAME = "buychannelsdk";
    public static final String ALREADY_UPLOAD_19 = "already_upload_19";
    public static final int APK_USERBUY = 5;
    public static final String APPFLY_UPLOARD = "appfly_upload";
    public static final String ASSOCIATE_103 = "associatedObj";
    public static final String BUYCHANNEL_TAG_USERBUY = "buychannel_from_tag_userbuy";
    public static final String BUYCHANNEL_TAG_WITHCOUNT = "buychannel_from_tag_userbuy_withcount";
    public static final String BUY_SDK_VERSIONCODE = "buy_sdk_versioncode";
    public static final String CAMPAIGN = "campaign";
    public static final String CAMPAIGN_ID = "campaignId";
    public static final long CHECK_OLD_DELAY = 3000;
    public static final String CHECK_SERVER_TYPE = "check_server_type";
    public static final String CHECK_USERTABLE_OLDUSER = "check_usertable_olduser";
    public static final String CHECK_USERTAG_NEWUSER = "check_usertag_newuser";
    public static final String CHECK_USERTAG_OLDUSER = "check_usertag_olduser";
    public static final String CHECK_USERTAG_USERTABLE_OLDUSER = "check_usertag_usertable_olduser";
    public static final String CID_45 = "cid_45";
    public static final String COMMERCESDK_SUCCESS = "commercesdk_success";
    public static final String CONVERSIONDATA = "conversionData";
    public static final String DEBUG_CODE1 = "repair";
    public static final String DEBUG_CODE2 = "ga_not_send45";
    public static final String DEBUG_CODE3 = "ga_receive";
    public static final String DEBUG_CODE4 = "af_receive";
    public static final String FILE_NAME = "commerce_buychannel";
    public static final String FIRST_ORGNIC = "isOrgnic";
    public static final String FROM_3G_CHANNEL = "from_3g_channel";
    public static final String FUN_ID_45 = "funid_45";
    public static final String GOOGLE_AD_ID = "google_ad_id";
    public static final long INTERVAL_SAVE_VERSION = 3600000;
    public static final long INTERVAL_SERVER_CHECKTIME = 28800000;
    public static final String IS_FIRST_RECEIVER = "isfirst";
    public static final String IS_GOKEYBORAD = "is_goKeyBoard";
    public static final String KEY_HAS_UPLOAD_19 = "key_has_upload_19";
    public static final String KEY_HAS_UPLOAD_45 = "key_has_upload_45";
    public static final String KEY_STATISTICS_45_DATA = "key_statistics_45_data";
    public static final long LIMIT_DAY = 432000000;
    public static final String LOG_TAG = "buychannelsdk";
    public static final String NEW_USER_BEFORE = "new_user_beford";
    public static final String OLD_45PARMS = "45Params";
    public static final String OLD_SENDER = "sender";
    public static final String OLD_USERTUPE = "userType";
    public static final String OLD_USER_MSG = "old_user_msg";
    public static final String PRODUCT_ID = "product_id";
    public static final int PROTOCOL_ID_19 = 19;
    public static final String REFERRER = "referrer";
    public static final String REPEAT_NETWORK = "repeat_network";
    public static final String SAVE_VERSIONCODE = "is_save_versioncode";
    public static final int SDK_VERSION_CODE = 18;
    public static final String SDK_VERSION_NAME = "1.4.2";
    public static final String SEPARATOR = "%26";
    public static final String SEPERATOR_45_SP = "\\$";
    public static final String STATISTICS19 = "statistics19";
    public static final long TIME_APPFLY_UPLOARD = 15000;
    public static final long TIME_WAITFOR_APPSFLYER_RESULE = 15000;
    public static final long TIME_WAITFOR_REFEERER_UPLOARD = 60000;
    public static final long TIME_WAITFOR_REQUEST_USERTAG = 15000;
    public static final String UNKNOWN_BUYCHANNEL = "unknown_buychannel";
    public static final int USERTAG_ALARM_ID = 26768;
    public static final String USERTAG_ALARM_NAME = "usertag_alarm";
    public static final long USERTAG_LIMIT_DAY = 172800000;
    public static final String USERTAG_PARAMS = "usertag_params";
    public static final int WITHCOUNT_ORGNIC = 0;

    public enum Position_103 {
        unknown(BuyChannelTypeContans.TYPE_WITHCOUNT),
        POSITION_1(BuyChannelTypeContans.TYPE_ORGANIC),
        POSITION_2(BuyChannelTypeContans.TYPE_NOT_GP),
        POSITION_3(BuyChannelTypeContans.TYPE_GP),
        POSITION_4(BuyChannelTypeContans.TYPE_FB),
        POSITION_5(BuyChannelTypeContans.TYPE_ADWORDS),
        POSITION_6("6"),
        POSITION_7("7"),
        POSITION_8("8"),
        POSITION_9("9");
        
        private String mValue;

        private Position_103(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }

        public static Position_103 fromValue(int value) {
            return values()[value];
        }
    }
}

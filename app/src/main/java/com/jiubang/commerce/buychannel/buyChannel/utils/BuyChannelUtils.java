package com.jiubang.commerce.buychannel.buyChannel.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class BuyChannelUtils {
    public static String getCountrySuccess(Context context) {
        try {
            TelephonyManager e = (TelephonyManager) context.getSystemService("phone");
            if (e != null) {
                String ret = e.getSimCountryIso().toUpperCase();
                return BuyChannelTypeContans.TYPE_WITHCOUNT;
            }
        } catch (Throwable th) {
        }
        if (!TextUtils.isEmpty((CharSequence) null)) {
            return "-1";
        }
        String ret2 = Locale.getDefault().getCountry().toUpperCase();
        return "-1";
    }

    public static String getOldSender(Context context) {
        return BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getString(BuySdkConstants.OLD_SENDER, (String) null);
    }

    public static void setOldSender(Context context, String sender) {
        BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).edit().putString(BuySdkConstants.OLD_SENDER, sender).commit();
    }

    public static String getOldUserType(Context context) {
        return BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getString(BuySdkConstants.OLD_USERTUPE, (String) null);
    }

    public static void setOldUserType(Context context, String userType) {
        BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).edit().putString(BuySdkConstants.OLD_USERTUPE, userType).commit();
    }

    public static BuyChannelBean jsonStr2BuyChannelBean(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(jsonString);
            BuyChannelBean buyChannelBean = new BuyChannelBean();
            buyChannelBean.setChannelFrom(object.getString("channelFrom"));
            buyChannelBean.setBuyChannel(object.getString("buyChannel"));
            buyChannelBean.setFirstUserType(object.getString("firstUserType"));
            buyChannelBean.setSecondUserType(Integer.parseInt(object.getString(BuySdkConstants.OLD_USERTUPE)));
            buyChannelBean.setSuccessCheck(Boolean.parseBoolean(object.optString("isSuccessCheck")));
            buyChannelBean.setCampaign(object.optString(BuySdkConstants.CAMPAIGN));
            buyChannelBean.setCampaignId(object.optString(BuySdkConstants.CAMPAIGN_ID));
            return buyChannelBean;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isOldUser(Context context) {
        String oldUserMsg = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getString(BuySdkConstants.OLD_USER_MSG, (String) null);
        boolean isOldUser = false;
        if (!TextUtils.isEmpty(oldUserMsg)) {
            String[] oldMsg = oldUserMsg.split(BuySdkConstants.SEPARATOR);
            if (oldMsg.length < 3) {
                isOldUser = Boolean.parseBoolean(oldMsg[1]);
            }
            LogUtils.i("buychannelsdk", "获取老用户,[BuyChannelUtils::isOldUser]   isOldUser:" + isOldUser);
        }
        return isOldUser;
    }

    public static boolean isOldApkBuy(Context context) {
        BuyChannelBean oldBuyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (oldBuyChannelBean == null || !oldBuyChannelBean.getFirstUserType().equals(UserTypeInfo.FirstUserType.apkbuy.toString())) {
            return false;
        }
        LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldApkBuy] 缓存中，已经保存了apk买量:" + oldBuyChannelBean.toString());
        return true;
    }

    public static boolean isOldWithCount(Context context) {
        BuyChannelBean oldBuyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (oldBuyChannelBean == null || !oldBuyChannelBean.getFirstUserType().equals(UserTypeInfo.FirstUserType.withCount.toString())) {
            return false;
        }
        LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldWithCount] 缓存中，已经保存了带量:" + oldBuyChannelBean.toString());
        return true;
    }

    public static boolean isOldOrgnic(Context context) {
        BuyChannelBean oldBuyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (oldBuyChannelBean == null || !oldBuyChannelBean.getFirstUserType().equals(UserTypeInfo.FirstUserType.organic.toString())) {
            return false;
        }
        LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldOrgnic] 缓存中，已经保存了自然:" + oldBuyChannelBean.toString());
        return true;
    }

    public static boolean isOldFbAdTw(Context context) {
        BuyChannelBean oldBuyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (oldBuyChannelBean != null && oldBuyChannelBean.getSecondUserType() == 2) {
            LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldFbAdTw] 缓存中，已经保存了fb自投:" + oldBuyChannelBean.toString());
            return true;
        } else if (oldBuyChannelBean != null && oldBuyChannelBean.getSecondUserType() == 3) {
            LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldFbAdTw] 缓存中，已经保存了fb非自投:" + oldBuyChannelBean.toString());
            return true;
        } else if (oldBuyChannelBean != null && oldBuyChannelBean.getSecondUserType() == 4) {
            LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldFbAdTw] 缓存中，已经保存了adwords自投:" + oldBuyChannelBean.toString());
            return true;
        } else if (oldBuyChannelBean == null || oldBuyChannelBean.getSecondUserType() != 6) {
            return false;
        } else {
            LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldFbAdTw] 缓存中，已经保存了adwords非自投:" + oldBuyChannelBean.toString());
            return true;
        }
    }

    public static boolean isOldEmpty(Context context) {
        if (BuyChannelDataMgr.getInstance(context).getBuyChannelBean() == null) {
            return true;
        }
        return false;
    }

    public static boolean isOldUserBuy(Context context) {
        BuyChannelBean oldBuyChannelBean = BuyChannelDataMgr.getInstance(context).getBuyChannelBean();
        if (oldBuyChannelBean == null || !oldBuyChannelBean.getFirstUserType().equals(UserTypeInfo.FirstUserType.userbuy.toString())) {
            return false;
        }
        LogUtils.i("buychannelsdk", "[BuyChannelUtils::isOldUserBuy] 缓存中数据为一般买量数据");
        return true;
    }

    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }
}

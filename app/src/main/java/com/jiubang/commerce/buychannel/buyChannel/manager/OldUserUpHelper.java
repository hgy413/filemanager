package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.AppsFlyerProxy;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.BuySdkInitParams;
import com.jiubang.commerce.buychannel.buyChannel.Interface.OldUserTableListenner;
import com.jiubang.commerce.buychannel.buyChannel.Interface.OldUserTagListenner;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.UserStatistics;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.database.BuychannelDbHelpler;
import com.jiubang.commerce.buychannel.buyChannel.utils.RequestDataUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;
import com.jiubang.commerce.utils.StringUtils;
import com.jiubang.commerce.utils.SystemUtils;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class OldUserUpHelper {
    private static OldUserUpHelper sInstance;
    /* access modifiers changed from: private */
    public BuyChannelDataMgr mBuyChannelDataMgr;
    /* access modifiers changed from: private */
    public String mCheckType = null;
    private final ConnectionChangeReceiver mConnectionChangeReceiver;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public Runnable mOldUserTask;
    private boolean mReceiverTag = false;
    /* access modifiers changed from: private */
    public SharedPreferences mSp;

    public static OldUserUpHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (OldUserUpHelper.class) {
                if (sInstance == null) {
                    sInstance = new OldUserUpHelper(context);
                }
            }
        }
        return sInstance;
    }

    private OldUserUpHelper(Context context) {
        Context context2;
        if (context != null) {
            context2 = context.getApplicationContext();
        } else {
            context2 = null;
        }
        this.mContext = context2;
        this.mConnectionChangeReceiver = new ConnectionChangeReceiver();
        this.mSp = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context);
    }

    public void updateOldUser(String checkType) {
        this.mCheckType = checkType;
        this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, checkType).commit();
        checkOldUser();
    }

    /* access modifiers changed from: private */
    public void checkUserTable(String checkType) {
        this.mCheckType = checkType;
        this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, checkType).commit();
        startConnectionReceiver();
    }

    /* access modifiers changed from: private */
    public void startConnectionReceiver() {
        if (!this.mReceiverTag) {
            IntentFilter filter = new IntentFilter();
            this.mReceiverTag = true;
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            this.mContext.registerReceiver(this.mConnectionChangeReceiver, filter);
        }
    }

    public void refreshNewUserTag(String checkType) {
        this.mCheckType = checkType;
        this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, checkType).commit();
        startConnectionReceiver();
    }

    public void checkUserTagAfterUserTable(String checkType) {
        this.mCheckType = checkType;
        this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, checkType).commit();
        startConnectionReceiver();
    }

    private void checkOldUser() {
        if (this.mOldUserTask != null) {
            CustomThreadExecutorProxy.getInstance().cancel(this.mOldUserTask);
            this.mOldUserTask = null;
        }
        this.mOldUserTask = new Runnable() {
            public void run() {
                OldUserUpHelper.this.startConnectionReceiver();
                Runnable unused = OldUserUpHelper.this.mOldUserTask = null;
            }
        };
        LogUtils.i("buychannelsdk", "[OldUserUpHelper::checkOldUser] 3s后，去核实老用户身份 ");
        CustomThreadExecutorProxy.getInstance().runOnMainThread(this.mOldUserTask, BuySdkConstants.CHECK_OLD_DELAY);
    }

    private class ConnectionChangeReceiver extends BroadcastReceiver {
        private String mOldBuyChannel;

        private ConnectionChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(0);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(1);
                if (System.currentTimeMillis() - Long.valueOf(OldUserUpHelper.this.mSp.getLong(BuySdkConstants.REPEAT_NETWORK, 0)).longValue() < 5000) {
                    OldUserUpHelper.this.mSp.edit().putLong(BuySdkConstants.REPEAT_NETWORK, System.currentTimeMillis()).commit();
                    return;
                }
                OldUserUpHelper.this.mSp.edit().putLong(BuySdkConstants.REPEAT_NETWORK, System.currentTimeMillis()).commit();
                LogUtils.i("buychannelsdk", "网络状态变化--->");
                if ((mobNetInfo != null && mobNetInfo.isConnected()) || (wifiNetInfo != null && wifiNetInfo.isConnected())) {
                    LogUtils.i("buychannelsdk", "网络状态变化--->成功联网");
                    String checkType = OldUserUpHelper.this.mSp.getString(BuySdkConstants.CHECK_SERVER_TYPE, (String) null);
                    OldUserUpHelper oldUserUpHelper = OldUserUpHelper.this;
                    if (checkType == null) {
                        checkType = OldUserUpHelper.this.mCheckType;
                    }
                    String unused = oldUserUpHelper.mCheckType = checkType;
                    if (TextUtils.isEmpty(OldUserUpHelper.this.mCheckType)) {
                        return;
                    }
                    if (OldUserUpHelper.this.mCheckType.equals(BuySdkConstants.CHECK_USERTAG_OLDUSER)) {
                        String oldUserMsg = OldUserUpHelper.this.mSp.getString(BuySdkConstants.OLD_USER_MSG, (String) null);
                        this.mOldBuyChannel = null;
                        if (!TextUtils.isEmpty(oldUserMsg)) {
                            String[] oldMsg = oldUserMsg.split(BuySdkConstants.SEPARATOR);
                            if (oldMsg.length > 1) {
                                this.mOldBuyChannel = oldMsg[0];
                                LogUtils.i("buychannelsdk", "[OldUserUpHelper::ConnectionChangeReceiver] buyChannel:" + oldMsg[0]);
                            }
                            OldUserUpHelper.this.checkOldUser(context, this.mOldBuyChannel);
                        }
                    } else if (OldUserUpHelper.this.mCheckType.equals(BuySdkConstants.CHECK_USERTAG_NEWUSER)) {
                        OldUserUpHelper.this.checkNewUserTag();
                    } else if (OldUserUpHelper.this.mCheckType.equals(BuySdkConstants.CHECK_USERTABLE_OLDUSER)) {
                        OldUserUpHelper.this.checkUnkownOldUser(OldUserUpHelper.this.mContext, this.mOldBuyChannel, new OldUserTableListenner() {
                            public void requestUserTableSuccess() {
                                OldUserUpHelper.this.stopConnectionStateUpdate();
                                OldUserUpHelper.this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, (String) null).commit();
                            }
                        });
                    } else if (OldUserUpHelper.this.mCheckType.equals(BuySdkConstants.CHECK_USERTAG_USERTABLE_OLDUSER)) {
                        OldUserUpHelper.this.checkUserTagAfterUserTable(OldUserUpHelper.this.mContext, this.mOldBuyChannel);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkNewUserTag() {
        new Thread(new Runnable() {
            private String mAssociatedObj;

            public void run() {
                UserTypeInfo.SecondUserType secondUserType = UserTagHelper.getInstance(OldUserUpHelper.this.mContext).requestUserTag(new OldUserTagListenner() {
                    public void requestUserTagSuccess() {
                        OldUserUpHelper.this.stopConnectionStateUpdate();
                        OldUserUpHelper.this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, (String) null).commit();
                    }
                }, true);
                BuyChannelDataMgr.getInstance(OldUserUpHelper.this.mContext).setLastCheckTime(System.currentTimeMillis());
                LogUtils.i("buychannelsdk", "记录最近一次server-check为当前时间：" + System.currentTimeMillis());
                if (BuyChannelDataMgr.getInstance(OldUserUpHelper.this.mContext).getFirstCheckTime() == 0) {
                    BuyChannelDataMgr.getInstance(OldUserUpHelper.this.mContext).setFirstCheckTime(System.currentTimeMillis());
                    LogUtils.i("buychannelsdk", "如果第一次时间为空，设置当前时间为第一次时间：" + System.currentTimeMillis());
                }
                UserTypeInfo.FirstUserType firstUserType = UserTypeInfo.FirstUserType.userbuy;
                String buyChannel = BuySdkConstants.BUYCHANNEL_TAG_USERBUY;
                if (secondUserType == null) {
                    LogUtils.i("buychannelsdk", "新用户，通过标签找回为非买量或带量用户，不覆盖数据");
                    return;
                }
                if (secondUserType.equals(UserTypeInfo.SecondUserType.WITHCOUNT_ORGNIC)) {
                    firstUserType = UserTypeInfo.FirstUserType.withCount;
                    buyChannel = BuySdkConstants.BUYCHANNEL_TAG_WITHCOUNT;
                    this.mAssociatedObj = UserStatistics.get45AssociatedEmptyOther(buyChannel);
                }
                if (secondUserType.equals(UserTypeInfo.SecondUserType.APK_USERBUY)) {
                    firstUserType = UserTypeInfo.FirstUserType.apkbuy;
                }
                String conversionDataJsonStr = OldUserUpHelper.this.mSp.getString(BuySdkConstants.CONVERSIONDATA, (String) null);
                String referrer = OldUserUpHelper.this.mSp.getString(BuySdkConstants.REFERRER, (String) null);
                this.mAssociatedObj = UserStatistics.get45AssociatedObjOther(buyChannel, (String) null, (String) null, (String) null);
                BuyChannelSetting.getInstance(OldUserUpHelper.this.mContext).setBuyChannel(buyChannel, BuyChannelSetting.ChannelFrom.from_usertag, firstUserType, secondUserType, OldUserUpHelper.this.mSp.getString(BuySdkConstants.CAMPAIGN, (String) null), OldUserUpHelper.this.mSp.getString(BuySdkConstants.CAMPAIGN_ID, (String) null), this.mAssociatedObj, (String) null, conversionDataJsonStr, referrer, OldUserUpHelper.this.mSp.getString(BuySdkConstants.NEW_USER_BEFORE, (String) null) + "_usertag_newuser", (SetBuyChannelListener) null);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void checkUserTagAfterUserTable(final Context context, final String buyChannel) {
        new Thread(new Runnable() {
            private String mAssociatedObj;

            public void run() {
                UserTypeInfo.FirstUserType firstUserType;
                UserTypeInfo.SecondUserType secondUserType = UserTagHelper.getInstance(context).requestUserTag(new OldUserTagListenner() {
                    public void requestUserTagSuccess() {
                        OldUserUpHelper.this.stopConnectionStateUpdate();
                        OldUserUpHelper.this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, (String) null).commit();
                    }
                }, false);
                BuyChannelDataMgr unused = OldUserUpHelper.this.mBuyChannelDataMgr = BuyChannelDataMgr.getInstance(context);
                if (secondUserType == null) {
                    LogUtils.i("buychannelsdk", "[OldUserUpHelper::checkUserTagAfterUserTable] 通过标签无法识别老用户身份");
                    OldUserUpHelper.this.mSp.edit().putLong(BuySdkConstants.REPEAT_NETWORK, 0).commit();
                } else if (!secondUserType.equals(UserTypeInfo.SecondUserType.WITHCOUNT_ORGNIC)) {
                    if (secondUserType.equals(UserTypeInfo.SecondUserType.APK_USERBUY)) {
                        firstUserType = UserTypeInfo.FirstUserType.apkbuy;
                        ServerCheckHelper.getInstance(OldUserUpHelper.this.mContext).cancelCheckUserTag();
                    } else {
                        firstUserType = UserTypeInfo.FirstUserType.userbuy;
                        ServerCheckHelper.getInstance(OldUserUpHelper.this.mContext).cancelCheckUserTag();
                    }
                    this.mAssociatedObj = UserStatistics.get45AssociatedObjOther(buyChannel, (String) null, (String) null, (String) null);
                    OldUserUpHelper.this.mBuyChannelDataMgr.setBuyChannelBean(buyChannel, BuyChannelSetting.ChannelFrom.from_oldUser_usertag, firstUserType, secondUserType, OldUserUpHelper.this.mSp.getString(BuySdkConstants.CONVERSIONDATA, (String) null), (SetBuyChannelListener) null, OldUserUpHelper.this.mSp.getString(BuySdkConstants.CAMPAIGN, (String) null), OldUserUpHelper.this.mSp.getString(BuySdkConstants.CAMPAIGN_ID, (String) null));
                    BuySdkInitParams mInitParams = AppsFlyerProxy.getInstance().getInitParams();
                    UserStatistics.upload45(OldUserUpHelper.this.mContext, mInitParams.mIsGoKeyboard, mInitParams.mP45FunId, this.mAssociatedObj, (String) null, (String) null, String.valueOf(secondUserType.getValue()), "usertable_olduser_usertag", (String) null, false);
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void checkOldUser(final Context context, final String buyChannel) {
        new Thread(new Runnable() {
            public void run() {
                UserTypeInfo.FirstUserType firstUserType;
                UserTypeInfo.SecondUserType secondUserType = UserTagHelper.getInstance(context).requestUserTag(new OldUserTagListenner() {
                    public void requestUserTagSuccess() {
                        OldUserUpHelper.this.stopConnectionStateUpdate();
                        OldUserUpHelper.this.mSp.edit().putString(BuySdkConstants.CHECK_SERVER_TYPE, (String) null).commit();
                    }
                }, false);
                BuyChannelDataMgr unused = OldUserUpHelper.this.mBuyChannelDataMgr = BuyChannelDataMgr.getInstance(context);
                if (secondUserType == null) {
                    LogUtils.i("buychannelsdk", "[OldUserUpHelper::checkOldUser] 通过标签无法识别老用户身份,判断为未知老用户类型,需要去查买量表");
                    OldUserUpHelper.this.mSp.edit().putLong(BuySdkConstants.REPEAT_NETWORK, 0).commit();
                    OldUserUpHelper.this.checkUserTable(BuySdkConstants.CHECK_USERTABLE_OLDUSER);
                    return;
                }
                if (secondUserType.equals(UserTypeInfo.SecondUserType.WITHCOUNT_ORGNIC)) {
                    firstUserType = UserTypeInfo.FirstUserType.withCount;
                } else if (secondUserType.equals(UserTypeInfo.SecondUserType.APK_USERBUY)) {
                    firstUserType = UserTypeInfo.FirstUserType.apkbuy;
                } else {
                    firstUserType = UserTypeInfo.FirstUserType.userbuy;
                }
                OldUserUpHelper.this.mBuyChannelDataMgr.setBuyChannelBean(buyChannel, BuyChannelSetting.ChannelFrom.from_oldUser, firstUserType, secondUserType, (String) null, (SetBuyChannelListener) null, (String) null, (String) null);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void checkUnkownOldUser(Context context, String buyChannel, OldUserTableListenner listenner) {
        final String aid = StringUtils.toString(SystemUtils.getAndroidId(context));
        final String cid = this.mSp.getString(BuySdkConstants.PRODUCT_ID, (String) null);
        final Context context2 = context;
        final String str = buyChannel;
        final OldUserTableListenner oldUserTableListenner = listenner;
        new Thread(new Runnable() {
            public void run() {
                RequestDataUtils.requestBuyChannelType(context2, cid, aid, str, new IConnectListener() {
                    public void onFinish(THttpRequest tHttpRequest, IResponse iResponse) {
                        UserTypeInfo.SecondUserType secondUserType;
                        UserTypeInfo.FirstUserType firstUserType;
                        JSONObject datasJson = null;
                        try {
                            datasJson = new JSONObject(StringUtils.toString(iResponse.getResponse()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String buyChannelType = datasJson != null ? datasJson.optString("buychanneltype") : null;
                        if (TextUtils.isEmpty(buyChannelType)) {
                            LogUtils.i("buychannelsdk", "服务器返回的数据为空");
                            return;
                        }
                        LogUtils.d("buychannelsdk", "服务器返回的数据成功，buyChannelType=" + buyChannelType);
                        oldUserTableListenner.requestUserTableSuccess();
                        UserTypeInfo.FirstUserType firstUserType2 = null;
                        UserTypeInfo.SecondUserType secondUserType2 = null;
                        if (Integer.parseInt(buyChannelType) > 1) {
                            UserTypeInfo.FirstUserType firstUserType3 = UserTypeInfo.FirstUserType.userbuy;
                            char c = 65535;
                            switch (buyChannelType.hashCode()) {
                                case 50:
                                    if (buyChannelType.equals(BuyChannelTypeContans.TYPE_NOT_GP)) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 51:
                                    if (buyChannelType.equals(BuyChannelTypeContans.TYPE_GP)) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                                case 52:
                                    if (buyChannelType.equals(BuyChannelTypeContans.TYPE_FB)) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 53:
                                    if (buyChannelType.equals(BuyChannelTypeContans.TYPE_ADWORDS)) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                            }
                            switch (c) {
                                case 0:
                                    UserTypeInfo.FirstUserType firstUserType4 = UserTypeInfo.FirstUserType.apkbuy;
                                    UserTypeInfo.SecondUserType secondUserType3 = UserTypeInfo.SecondUserType.APK_USERBUY;
                                    LogUtils.d("buychannelsdk", "买量表判断为非GP渠道，判定为APK买量，buyChannelType=" + buyChannelType);
                                    secondUserType = secondUserType3;
                                    firstUserType = firstUserType4;
                                    break;
                                case BuychannelDbHelpler.DB_VERSION_MAX:
                                    UserTypeInfo.SecondUserType secondUserType4 = UserTypeInfo.SecondUserType.GA_USERBUY;
                                    LogUtils.d("buychannelsdk", "买量表判断为GP渠道，判定为GA买量，buyChannelType=" + buyChannelType);
                                    secondUserType = secondUserType4;
                                    firstUserType = firstUserType3;
                                    break;
                                case 2:
                                    UserTypeInfo.SecondUserType secondUserType5 = UserTypeInfo.SecondUserType.FB_AUTO;
                                    LogUtils.d("buychannelsdk", "买量表判断为FB买量，判定为FB自投，buyChannelType=" + buyChannelType);
                                    secondUserType = secondUserType5;
                                    firstUserType = firstUserType3;
                                    break;
                                case 3:
                                    UserTypeInfo.SecondUserType secondUserType6 = UserTypeInfo.SecondUserType.ADWORDS_AUTO;
                                    LogUtils.d("buychannelsdk", "买量表判断为Adwords买量，判定为Adwords自投，buyChannelType=" + buyChannelType);
                                    secondUserType = secondUserType6;
                                    firstUserType = firstUserType3;
                                    break;
                                default:
                                    secondUserType = null;
                                    firstUserType = firstUserType3;
                                    break;
                            }
                            String associatedObj = UserStatistics.get45AssociatedObjOther(str, (String) null, (String) null, (String) null);
                            BuySdkInitParams mInitParams = AppsFlyerProxy.getInstance().getInitParams();
                            UserStatistics.upload45(OldUserUpHelper.this.mContext, mInitParams.mIsGoKeyboard, mInitParams.mP45FunId, associatedObj, (String) null, (String) null, String.valueOf(secondUserType.getValue()), "organic_usertable_olduser", (String) null, false);
                            secondUserType2 = secondUserType;
                            firstUserType2 = firstUserType;
                        } else if (Integer.parseInt(buyChannelType) <= 1) {
                            firstUserType2 = UserTypeInfo.FirstUserType.organic;
                            secondUserType2 = UserTypeInfo.SecondUserType.GP_ORGNIC;
                            LogUtils.d("buychannelsdk", "买量表判断为自然，判定为自然，buyChannelType=" + buyChannelType);
                        }
                        OldUserUpHelper.this.mBuyChannelDataMgr.setBuyChannelBean(str, BuyChannelSetting.ChannelFrom.from_oldUser, firstUserType2, secondUserType2, (String) null, (SetBuyChannelListener) null, (String) null, (String) null);
                        ServerCheckHelper.getInstance(OldUserUpHelper.this.mContext).startCheckUserTag(BuySdkConstants.INTERVAL_SERVER_CHECKTIME);
                    }

                    public void onException(THttpRequest tHttpRequest, int i) {
                    }

                    public void onStart(THttpRequest tHttpRequest) {
                    }

                    public void onException(THttpRequest tHttpRequest, HttpResponse httpResponse, int i) {
                    }
                });
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void stopConnectionStateUpdate() {
        if (this.mConnectionChangeReceiver != null && this.mReceiverTag) {
            this.mReceiverTag = false;
            this.mContext.unregisterReceiver(this.mConnectionChangeReceiver);
        }
    }
}

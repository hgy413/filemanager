package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import com.gau.go.gostaticsdk.StatisticStateListener;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jb.ga0.commerce.util.CustomAlarmManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.Interface.SetBuyChannelListener;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.bean.UserTypeInfo;
import com.jiubang.commerce.buychannel.buyChannel.database.BuychannelDbHelpler;
import com.jiubang.commerce.buychannel.buyChannel.database.StaticsTable;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;
import java.util.List;

public class InitManager {
    private static InitManager sInstance;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public SharedPreferences mSp;
    private StatisticStateListener sStatisticsListener = new StatisticStateListener() {
        public void onCtrlInfoInsertToDB(int i, ContentValues contentValues) {
        }

        public void onStatisticDataInsertToDB(String s, int i, int i1, String s1) {
        }

        public void onUploadStatisticDataStart(String s, int i, int i1, String s1) {
        }

        public void onUploadStatisticDataSuccess(String s, int i, int i1, String s1) {
            LogUtils.d("buychannelsdk", "[BuyChannelApi::onUploadStatisticDataSuccess]upload listener onUploadStatisticDataSuccess s = " + s + " i = " + i + " i1 = " + i1 + " s1 = " + s1);
            if (i1 == 19) {
                SharedPreferences sp = BuyChannelDataMgr.getInstance(InitManager.this.mContext).getSharedPreferences(InitManager.this.mContext);
                if (!sp.getBoolean(BuySdkConstants.KEY_HAS_UPLOAD_19, false)) {
                    LogUtils.d("buychannelsdk", "[BuyChannelApi::onUploadStatisticDataSuccess]first time upload 19 success");
                    try {
                        sp.edit().putBoolean(BuySdkConstants.KEY_HAS_UPLOAD_19, true).commit();
                        InitManager.this.uploadSp45(sp, 0);
                    } catch (Exception e) {
                        LogUtils.d("buychannelsdk", " error：" + e);
                    }
                }
            }
        }

        public void onUploadStatisticDataFailed(String s, int i, int i1, String s1) {
            LogUtils.d("buychannelsdk", "[BuyChannelApi::onUploadStatisticDataFailed]upload listener 统计协议上传失败 s = " + s + " i = " + i + " i1 = " + i1 + " s1 = " + s1);
        }
    };

    public static InitManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (InitManager.class) {
                if (sInstance == null) {
                    sInstance = new InitManager(context);
                }
            }
        }
        return sInstance;
    }

    private InitManager(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mSp = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
    }

    public void setStatisticStateListener() {
        try {
            StatisticsManager.getInstance(this.mContext).setStatisticStateListener(this.sStatisticsListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void uploadSp45(SharedPreferences sp, final int position) {
        final int funId = sp.getInt(BuySdkConstants.FUN_ID_45, 0);
        CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
            public void run() {
                BuychannelDbHelpler dbHelpler = BuychannelDbHelpler.getInstance(InitManager.this.mContext);
                List statics45List = StaticsTable.queryAll(dbHelpler);
                if (statics45List.size() != 0 && position == 2) {
                    StatisticsDebug.Statistic103Params statistic103ParamsP2 = new StatisticsDebug.Statistic103Params();
                    statistic103ParamsP2.code(BuySdkConstants.DEBUG_CODE1).position(2);
                    StatisticsDebug.upload(InitManager.this.mContext, statistic103ParamsP2);
                }
                for (int i = 0; i < statics45List.size(); i++) {
                    StatisticsManager.getInstance(InitManager.this.mContext).upLoadStaticData(45, funId, statics45List.get(i).toString());
                    LogUtils.d("buychannelsdk", "[BuyChannelApi::uploadSp45]upload 45 after 19 upload success");
                }
                StaticsTable.deleteAll(dbHelpler);
            }
        });
    }

    public void check45(int position) {
        this.mSp = BuyChannelDataMgr.getInstance(this.mContext).getSharedPreferences(this.mContext);
        uploadSp45(this.mSp, position);
    }

    public boolean isUpdateBuyChannelSdk() {
        if (this.mSp.getInt(BuySdkConstants.BUY_SDK_VERSIONCODE, 0) <= 0) {
            return false;
        }
        LogUtils.i("buychannelsdk", "[BuyChannelApi::isUpdateBuyChannelSdk] 之前已经接过买量SDK");
        return true;
    }

    public void saveSdkVersion() {
        CustomAlarmManager.getInstance(this.mContext).getAlarm("saveVersionCode").alarmRepeat(BuySdkConstants.ALARM_ID_VERSIONCODE, BuySdkConstants.INTERVAL_SAVE_VERSION, BuySdkConstants.INTERVAL_SERVER_CHECKTIME, true, new CustomAlarm.OnAlarmListener() {
            public void onAlarm(int i) {
                if (InitManager.this.mSp.getBoolean(BuySdkConstants.SAVE_VERSIONCODE, false)) {
                    CustomAlarmManager.getInstance(InitManager.this.mContext).getAlarm("saveVersionCode").cancelAarm(BuySdkConstants.ALARM_ID_VERSIONCODE);
                } else if (18 > InitManager.this.mSp.getInt(BuySdkConstants.BUY_SDK_VERSIONCODE, 0)) {
                    InitManager.this.mSp.edit().putInt(BuySdkConstants.BUY_SDK_VERSIONCODE, 18).commit();
                    InitManager.this.mSp.edit().putBoolean(BuySdkConstants.SAVE_VERSIONCODE, true).commit();
                }
            }
        });
    }

    public void updateOldUser(Context context, String buyChannel, boolean isOldUser, String cId) {
        this.mSp.edit().putString(BuySdkConstants.PRODUCT_ID, cId).commit();
        if (!TextUtils.isEmpty(this.mSp.getString(BuySdkConstants.OLD_USER_MSG, (String) null))) {
            LogUtils.i("buychannelsdk", "[BuyChannelApi::updateOldUser] 缓存中已有旧用户的信息,说明之前已经老用户升级过，不再查询老用户标签");
            return;
        }
        if (TextUtils.isEmpty(buyChannel) && isOldUser) {
            buyChannel = "null";
        }
        saveOldUserMsg(buyChannel, isOldUser);
        if (BuyChannelUtils.isOldApkBuy(context)) {
            LogUtils.i("buychannelsdk", "[InitManager::updateOldUser] 已缓存APK买量，不去查找老用户身份");
        } else {
            OldUserUpHelper.getInstance(context).updateOldUser(BuySdkConstants.CHECK_USERTAG_OLDUSER);
        }
    }

    private void savaOrganicOldUser(Context context, String buyChannel) {
        LogUtils.i("buychannelsdk", "[InitManager::savaOrganicOldUser] 老用户buyChannel：" + buyChannel + "默认设置自然老用户");
        String str = buyChannel;
        BuyChannelDataMgr.getInstance(context).setBuyChannelBean(str, BuyChannelSetting.ChannelFrom.from_oldUser, UserTypeInfo.FirstUserType.organic, UserTypeInfo.SecondUserType.GP_ORGNIC, (String) null, (SetBuyChannelListener) null, (String) null, (String) null);
    }

    private void saveOldUserMsg(String buyChannel, boolean isOldUser) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(buyChannel);
        buffer.append(BuySdkConstants.SEPARATOR);
        buffer.append(isOldUser);
        LogUtils.i("buychannelsdk", "[BuyChannelApi::updateOldUser] 是否老用户" + isOldUser + ",老用户buyChannel:" + buyChannel);
        this.mSp.edit().putString(BuySdkConstants.OLD_USER_MSG, buffer.toString()).commit();
    }
}

package com.jiubang.commerce.buychannel.buyChannel.manager;

import android.content.Context;
import com.jb.ga0.commerce.util.CustomAlarm;
import com.jb.ga0.commerce.util.CustomAlarmManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.buychannel.BuyChannelApi;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuyChannelSetting;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;

public class ServerCheckHelper {
    private static ServerCheckHelper sInstance;
    /* access modifiers changed from: private */
    public Context mContext;

    public static ServerCheckHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ServerCheckHelper.class) {
                if (sInstance == null) {
                    sInstance = new ServerCheckHelper(context);
                }
            }
        }
        return sInstance;
    }

    private ServerCheckHelper(Context context) {
        this.mContext = context != null ? context.getApplicationContext() : null;
    }

    public void startCheckUserTag(long triggerTime) {
        BuyChannelDataMgr.getInstance(this.mContext).saveFirstCheckUserTagTime(System.currentTimeMillis());
        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckUserTag]2天内，每间隔８小时，再去查询一次买量标签表");
        CustomAlarmManager.getInstance(this.mContext).getAlarm(BuySdkConstants.USERTAG_ALARM_NAME).alarmRepeat(BuySdkConstants.USERTAG_ALARM_ID, triggerTime, BuySdkConstants.INTERVAL_SERVER_CHECKTIME, true, new CustomAlarm.OnAlarmListener() {
            public void onAlarm(int i) {
                long firstCheckTime = BuyChannelDataMgr.getInstance(ServerCheckHelper.this.mContext).getFirstCheckUserTagTime();
                if (firstCheckTime != 0) {
                    long intervalTime = System.currentTimeMillis() - firstCheckTime;
                    LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] intervalTime:" + intervalTime);
                    if (intervalTime - BuySdkConstants.USERTAG_LIMIT_DAY > 0) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckUserTag] 已经超过2天时间，不再检测用户标签接口]");
                        CustomAlarmManager.getInstance(ServerCheckHelper.this.mContext).getAlarm(BuySdkConstants.USERTAG_ALARM_NAME).cancelAarm(BuySdkConstants.USERTAG_ALARM_ID);
                        return;
                    }
                }
                OldUserUpHelper.getInstance(ServerCheckHelper.this.mContext).checkUserTagAfterUserTable(BuySdkConstants.CHECK_USERTAG_USERTABLE_OLDUSER);
            }
        });
    }

    public void startCheckServer(long triggerTime) {
        CustomAlarmManager.getInstance(this.mContext).getAlarm("buychannelsdk").alarmRepeat(BuySdkConstants.ALARM_ID, triggerTime, BuySdkConstants.INTERVAL_SERVER_CHECKTIME, true, new CustomAlarm.OnAlarmListener() {
            public void onAlarm(int i) {
                BuyChannelBean oldBuyChannelBean = BuyChannelApi.getBuyChannelBean(ServerCheckHelper.this.mContext);
                if (oldBuyChannelBean != null) {
                    if (BuyChannelUtils.isOldUserBuy(ServerCheckHelper.this.mContext)) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] 旧数据已经是买量，无需再server-check");
                        ServerCheckHelper.this.cancelCheckServer();
                        return;
                    } else if (BuyChannelUtils.isOldApkBuy(ServerCheckHelper.this.mContext)) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] 旧数据已经是apk买量，无需再server-check");
                        ServerCheckHelper.this.cancelCheckServer();
                        return;
                    } else if (oldBuyChannelBean.getSecondUserType() == 0) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] 旧数据已经是自然带量，无需再server-check");
                        ServerCheckHelper.this.cancelCheckServer();
                        return;
                    } else if (oldBuyChannelBean.getChannelFrom().equals(BuyChannelSetting.ChannelFrom.from_oldUser.toString())) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] 旧数据已经老用户并确定身份，无需再server-check");
                        ServerCheckHelper.this.cancelCheckServer();
                        return;
                    }
                }
                long firstCheckTime = BuyChannelDataMgr.getInstance(ServerCheckHelper.this.mContext).getFirstCheckTime();
                if (firstCheckTime != 0) {
                    long intervalTime = System.currentTimeMillis() - firstCheckTime;
                    LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] intervalTime:" + intervalTime);
                    if (intervalTime - BuySdkConstants.LIMIT_DAY > 0) {
                        LogUtils.i("buychannelsdk", "[ServerCheckHelper::startCheckServer] 已经超过5天时间，不再检测]");
                        CustomAlarmManager.getInstance(ServerCheckHelper.this.mContext).getAlarm("buychannelsdk").cancelAarm(BuySdkConstants.ALARM_ID);
                        return;
                    }
                }
                OldUserUpHelper.getInstance(ServerCheckHelper.this.mContext).refreshNewUserTag(BuySdkConstants.CHECK_USERTAG_NEWUSER);
            }
        });
    }

    public void cancelCheckUserTag() {
        CustomAlarmManager.getInstance(this.mContext).getAlarm(BuySdkConstants.USERTAG_ALARM_NAME).cancelAarm(BuySdkConstants.USERTAG_ALARM_ID);
    }

    public void cancelCheckServer() {
        CustomAlarmManager.getInstance(this.mContext).getAlarm("buychannelsdk").cancelAarm(BuySdkConstants.ALARM_ID);
    }
}

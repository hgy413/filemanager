package com.jiubang.commerce.buychannel.buyChannel.Statistics;

import android.content.Context;
import android.content.SharedPreferences;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.jb.ga0.commerce.util.LogUtils;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;
import com.jb.ga0.commerce.util.statistics.BaseStatistic;
import com.jb.ga0.commerce.util.thread.CustomThreadExecutorProxy;
import com.jiubang.commerce.buychannel.AppsFlyerProxy;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.BuySdkInitParams;
import com.jiubang.commerce.buychannel.buyChannel.Statistics.StatisticsDebug;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;
import com.jiubang.commerce.buychannel.buyChannel.database.BuychannelDbHelpler;
import com.jiubang.commerce.buychannel.buyChannel.database.StaticsTable;
import com.jiubang.commerce.buychannel.buyChannel.manager.InitManager;
import com.jiubang.commerce.buychannel.buyChannel.utils.BuyChannelUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Statistics45 extends BaseStatistic {
    private static final int LOG_ID = 45;
    private Runnable mUpload45Task;

    public static void upload(final Context context, boolean isGoKeyboard, Statistic45Params params, boolean isAfter19) {
        StringBuffer buffer = new StringBuffer();
        if (isGoKeyboard) {
            buffer.append(System.currentTimeMillis());
            buffer.append("||");
        }
        buffer.append(LOG_ID);
        buffer.append("||");
        appendStatisticField(buffer, GoHttpHeadUtil.getAndroidId(context));
        buffer.append("||");
        appendStatisticField(buffer, getCurrentBeiJingTime());
        buffer.append("||");
        buffer.append(params.mFunId);
        buffer.append("||");
        appendStatisticField(buffer, params.mSender);
        buffer.append("||");
        appendStatisticField(buffer, params.mOperationCode);
        buffer.append("||");
        appendStatisticField(buffer, params.mOperationResult);
        buffer.append("||");
        appendStatisticField(buffer, GoHttpHeadUtil.getCountry(context));
        buffer.append("||");
        appendStatisticField(buffer, params.mChannel);
        buffer.append("||");
        buffer.append(GoHttpHeadUtil.getVersionCode(context));
        buffer.append("||");
        appendStatisticField(buffer, GoHttpHeadUtil.getVersionName(context));
        buffer.append("||");
        appendStatisticField(buffer, params.mEntrance);
        buffer.append("||");
        appendStatisticField(buffer, params.mAfDetail);
        buffer.append("||");
        appendStatisticField(buffer, params.mReferrer);
        buffer.append("||");
        appendStatisticField(buffer, StatisticsManager.sIMEI);
        buffer.append("||");
        appendStatisticField(buffer, StatisticsManager.getGOID(context));
        buffer.append("||");
        appendStatisticField(buffer, params.mAssociatedObj);
        buffer.append("||");
        appendStatisticField(buffer, params.mAfAgency);
        buffer.append("||");
        appendStatisticField(buffer, params.mAdvertId);
        buffer.append("||");
        appendStatisticField(buffer, params.mGpVersionName);
        buffer.append("||");
        appendStatisticField(buffer, params.mUserType);
        buffer.append("||");
        appendStatisticField(buffer, params.mTag);
        buffer.append("||");
        appendStatisticField(buffer, params.mSdkVersionCode);
        buffer.append("||");
        appendStatisticField(buffer, BuyChannelUtils.getCountrySuccess(context));
        if (isAfter19) {
            SharedPreferences sp = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context);
            if (sp.getBoolean(BuySdkConstants.KEY_HAS_UPLOAD_19, false)) {
                StatisticsManager.getInstance(context).upLoadStaticData(LOG_ID, params.mFunId, buffer.toString());
                LogUtils.i("buychannelsdk", "[Statistics45::upload] 之前已经有19协议上传成功，直接上传45协议");
                LogUtils.v("buychannelsdk", "/功能点ID : " + params.mFunId + "   /统计对象 : " + params.mSender + "   /操作代码 : " + params.mOperationCode + "   /操作结果 : " + params.mOperationResult + "   /入口 : " + params.mEntrance + "   /AF明细 : " + params.mAfDetail + "   /Referrer : " + params.mReferrer + "   /关联对象 : " + params.mAssociatedObj + "   /广告ID : " + params.mAdvertId + "   /AF Agency : " + params.mAfAgency + "   gp版本名 : " + params.mGpVersionName + "   用户类型 : " + params.mUserType + "   原用户类型标志 : " + params.mTag + "   推广SDK版本 : " + params.mSdkVersionCode + "   是否可以获取SIM卡国家 : " + BuyChannelUtils.getCountrySuccess(context));
                return;
            }
            LogUtils.i("buychannelsdk", "[Statistics45::upload] 之前未上传19协议，新数据为买量，要先传19协议再上传45协议");
            BuySdkInitParams buySdkInitParams = AppsFlyerProxy.getInstance().getInitParams();
            if (buySdkInitParams.mProtocal19Handler != null) {
                buySdkInitParams.mProtocal19Handler.uploadProtocal19();
            }
            sp.edit().putInt(BuySdkConstants.FUN_ID_45, params.mFunId).commit();
            final StringBuffer bufferStr = buffer;
            CustomThreadExecutorProxy.getInstance().runOnAsyncThread(new Runnable() {
                public void run() {
                    StaticsTable.insert(BuychannelDbHelpler.getInstance(context), bufferStr.toString());
                }
            });
            upload45NotWait(context, params);
            return;
        }
        LogUtils.i("buychannelsdk", "[Statistics45::upload] 新数据不是买量，直接上传45协议");
        StatisticsManager.getInstance(context).upLoadStaticData(LOG_ID, params.mFunId, buffer.toString());
        LogUtils.v("buychannelsdk", "/功能点ID : " + params.mFunId + "   /统计对象 : " + params.mSender + "   /操作代码 : " + params.mOperationCode + "   /操作结果 : " + params.mOperationResult + "   /入口 : " + params.mEntrance + "   /AF明细 : " + params.mAfDetail + "   /Referrer : " + params.mReferrer + "   /关联对象 : " + params.mAssociatedObj + "   /广告ID : " + params.mAdvertId + "   /AF Agency : " + params.mAfAgency + "   gp版本名 : " + params.mGpVersionName + "   用户类型 : " + params.mUserType + "   原用户类型标志 : " + params.mTag + "   推广SDK版本 : " + params.mSdkVersionCode + "   是否可以获取SIM卡国家 : " + BuyChannelUtils.getCountrySuccess(context));
    }

    private static void upload45NotWait(final Context context, final Statistic45Params params) {
        CustomThreadExecutorProxy.getInstance().runOnMainThread(new Runnable() {
            public void run() {
                if (!BuyChannelDataMgr.getInstance(context).getSharedPreferences(context).getBoolean(BuySdkConstants.KEY_HAS_UPLOAD_19, false)) {
                    InitManager.getInstance(context).check45(1);
                    StatisticsDebug.Statistic103Params statistic103ParamsP1 = new StatisticsDebug.Statistic103Params();
                    statistic103ParamsP1.code(BuySdkConstants.DEBUG_CODE1).referrer(params.mReferrer).position(1).buychannel(params.mSender);
                    StatisticsDebug.upload(context, statistic103ParamsP1);
                }
            }
        }, 15000);
    }

    public static String getCurrentBeiJingTime() {
        try {
            Date now = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            return dateFormat.format(now);
        } catch (Exception e) {
            return null;
        }
    }

    public static class Statistic45Params {
        protected String mAdvertId;
        protected String mAfAgency;
        protected String mAfDetail;
        protected String mAssociatedObj;
        protected String mChannel;
        protected String mEntrance;
        protected int mFunId;
        protected String mGpVersionName;
        protected String mOperationCode;
        protected String mOperationResult = BuyChannelTypeContans.TYPE_ORGANIC;
        protected String mReferrer;
        protected String mRemark;
        protected String mSdkVersionCode;
        protected String mSender;
        protected String mTag;
        protected String mUserType;

        public String getSender() {
            return this.mSender;
        }

        public String getUserType() {
            return this.mUserType;
        }

        public Statistic45Params funId(int funId) {
            this.mFunId = funId;
            return this;
        }

        public Statistic45Params sender(String sender) {
            this.mSender = sender;
            return this;
        }

        public Statistic45Params operationCode(String operationCode) {
            this.mOperationCode = operationCode;
            return this;
        }

        public Statistic45Params operationResult(String operationResult) {
            this.mOperationResult = operationResult;
            return this;
        }

        public Statistic45Params channel(String channel) {
            this.mChannel = channel;
            return this;
        }

        public Statistic45Params entrance(String entrance) {
            this.mEntrance = entrance;
            return this;
        }

        public Statistic45Params afDetail(String afDetail) {
            this.mAfDetail = afDetail;
            return this;
        }

        public Statistic45Params referrer(String referrer) {
            this.mReferrer = referrer;
            return this;
        }

        public Statistic45Params associatedObj(String associatedObj) {
            this.mAssociatedObj = associatedObj;
            return this;
        }

        public Statistic45Params remark(String remark) {
            this.mRemark = remark;
            return this;
        }

        public Statistic45Params advertId(String advertId) {
            this.mAdvertId = advertId;
            return this;
        }

        public Statistic45Params userType(String userType) {
            this.mUserType = userType;
            return this;
        }

        public Statistic45Params gpVersionName(String gpVersionName) {
            this.mGpVersionName = gpVersionName;
            return this;
        }

        public Statistic45Params tag(String tag) {
            this.mTag = tag;
            return this;
        }

        public Statistic45Params sdkVersionCode(String sdkVersionCode) {
            this.mSdkVersionCode = sdkVersionCode;
            return this;
        }

        public Statistic45Params afAgency(String afAgency) {
            this.mAfAgency = afAgency;
            return this;
        }
    }
}

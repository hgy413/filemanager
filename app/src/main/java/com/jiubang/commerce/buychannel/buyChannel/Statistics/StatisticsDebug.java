package com.jiubang.commerce.buychannel.buyChannel.Statistics;

import android.content.Context;
import android.content.SharedPreferences;
import com.jb.ga0.commerce.util.statistics.BaseStatistic;
import com.jiubang.commerce.buychannel.BuyChannelApi;
import com.jiubang.commerce.buychannel.BuyChannelDataMgr;
import com.jiubang.commerce.buychannel.BuySdkConstants;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelBean;
import com.jiubang.commerce.buychannel.buyChannel.bean.BuyChannelTypeContans;
import com.jiubang.commerce.buychannel.buyChannel.utils.TextUtils;

public class StatisticsDebug extends BaseStatistic {
    private static final int LOG_ID = 103;
    private static String sConversionData;
    private static String sReferrer;

    public static void upload(Context context, Statistic103Params params) {
        SharedPreferences sp = BuyChannelDataMgr.getInstance(context).getSharedPreferences(context);
        BuyChannelBean buyChannelBean = BuyChannelApi.getBuyChannelBean(context);
        String cid = sp.getString(BuySdkConstants.CID_45, (String) null);
        StringBuffer buffer = new StringBuffer();
        appendStatisticField(buffer, "670");
        buffer.append("||");
        appendStatisticField(buffer, buyChannelBean.getBuyChannel());
        buffer.append("||");
        appendStatisticField(buffer, params.mCode);
        buffer.append("||");
        appendStatisticField(buffer, BuyChannelTypeContans.TYPE_ORGANIC);
        buffer.append("||");
        if (params.mReferrer != null) {
            sReferrer = params.mReferrer;
        } else {
            sReferrer = sp.getString(BuySdkConstants.REFERRER, (String) null);
        }
        appendStatisticField(buffer, sReferrer);
        buffer.append("||");
        if (!TextUtils.isEmpty(params.mAppflyer)) {
            sConversionData = params.mAppflyer;
        } else {
            sConversionData = sp.getString(BuySdkConstants.CONVERSIONDATA, (String) null);
        }
        appendStatisticField(buffer, sConversionData);
        buffer.append("||");
        appendStatisticField(buffer, String.valueOf(params.mPosition));
        buffer.append("||");
        appendStatisticField(buffer, cid);
        uploadStatisticData(context, LOG_ID, 670, buffer, new BaseStatistic.SatisticsUploadPolicy[0]);
    }

    public static class Statistic103Params {
        protected String mAdvertId;
        protected String mAppflyer;
        protected String mBuyChannel;
        protected String mChannel;
        protected String mCode;
        protected int mPosition;
        protected String mReferrer;

        public Statistic103Params channel(String channel) {
            this.mChannel = channel;
            return this;
        }

        public Statistic103Params advertId(String advertId) {
            this.mAdvertId = advertId;
            return this;
        }

        public Statistic103Params buychannel(String buychannel) {
            this.mBuyChannel = buychannel;
            return this;
        }

        public Statistic103Params code(String code) {
            this.mCode = code;
            return this;
        }

        public Statistic103Params referrer(String referrer) {
            this.mReferrer = referrer;
            return this;
        }

        public Statistic103Params position(int position) {
            this.mPosition = position;
            return this;
        }

        public Statistic103Params appflyer(String conversationData) {
            this.mAppflyer = conversationData;
            return this;
        }
    }
}

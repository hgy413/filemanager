package com.jb.filemanager.statistics.bean;

import android.content.Context;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.jb.filemanager.BuildConfig;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.util.AppUtils;
import com.jb.ga0.commerce.util.http.GoHttpHeadUtil;

/**
 * 异常信息统计协议(41-558)<br>
 * 数据格式为: 日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口
 * ||tab分类||位置||imei||goid||关联对象||备注1||备注2||备注3||GADID<br>
 * 参看: http://wiki.3g.net.cn/pages/viewpage.action?pageId=18779100<br>
 *
 * @author Alex
 *
 */
public class Statistics41Bean extends BaseStatisticsBean {

    /** &1.日志序列,统一日志序列号；不能为空；不能和其他协议重复；本协议对应值为41 **/
    public final String mLogSeq = "41";

    /** &2.Android ID, 用户唯一标识（Google为每个android系统生成的唯一码）；此标识可以关联GO系列产品信息； **/
    public String mAndroidId = "0";

    /** &3.日志打印时间,客户端日志的打印时间；格式如：2013-02-26 12:00:02；默认转成中国时区 **/
    public String mLogTime = "0";

    /** &4.功能点ID, 558 **/
    public final String mFuntionId = String.valueOf(StatisticsConstants.PROTOCOL_41_FUN_ID);

    /**
     * 5.统计对象,
     * GA连接（例如：utm_source=glispa&utm_medium=banner&utm_term=Test&utm_content
     * =Test
     * &utm_campaign=Test&gokey_click_id=4B20B6EA9127924DFE83&gokey_channel=200）
     **/
    public String mAppId = "0";

    /** &6.操作代码, 目前只有k001 **/
    public String mOperateCode = "0";

    /** &7.操作结果, 0：未成功，1：成功 （默认成功） **/
    public String mOperateResult = "1";

    /** &8.国家, 取SIM卡里面的国家代码；无SIM卡时，取手动系统语言里面的国家代码，报表显示 cn **/
    public String mCountry = "0";

    /** &9.渠道, 产品所属渠道；用数字表示；例：200 **/
    public String mUid = "0";

    /** &10.版本号, 产品的发行版本号：Version Code **/
    public String mVersionCode = "0";

    /** &11.版本名, 产品的发行版本名：Version Name **/
    public String mVersionName = "0";

    /** 12.入口, 本次需求为空 **/
    public String mEntrance = "0";

    /** 13.tab分类, 本次需求为空 **/
    public String mTab = "0";

    /** 14.位置, 本次需求为空 **/
    public String mPosition = "0";

    /** 15.imei, 随机生成,引用go桌面的同一套编码；只有桌面主题才上传该字段，其他产品填0 **/
    public String mImei = "0";

    /** &16.goid, 用户在GO系列产品中的唯一id，用于产品间的用户关联。用户在不同GO系列产品间使用同一goid **/
    public String mGoId = "0";

    /**
     * 17.关联对象，本次需求为空
     */
    public String mRelatedAppId = "0";

    /**
     * 18.备注, Crash log
     */
    public String mRemark1 = "0";

    /**
     * 19.备注, 本次需求为空
     */
    public String mRemark2 = "0";

    /**
     * 20.备注, 本次需求为空
     */
    public String mRemark3 = "0";

    /**
     * 21.GADID, 谷歌广告ID
     */
    public String mGAdId = "0";

    public Statistics41Bean(Context context) {
        mAndroidId = GoHttpHeadUtil.getAndroidId(context);
        mOperateCode = "k001";
        mCountry = GoHttpHeadUtil.getLocal(context);
        mUid = AppUtils.getChannel(context);
        mVersionCode = String.valueOf(BuildConfig.VERSION_CODE);
        mVersionName = BuildConfig.VERSION_NAME;
        mImei = GoHttpHeadUtil.getVirtualIMEI(context);
        mGoId = StatisticsManager.getGOID(context);
    }

    @Override
    public void reset() {
        mAppId = "0";
        mOperateResult = "1";
        mRelatedAppId = "0";
        mRemark1 = "0";
    }

    @Override
    public String toFormatStatisticsData() {
        final StringBuilder data = new StringBuilder();
        data.append(mLogSeq);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mAndroidId);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        mLogTime = getNowTimeInEast8("%Y-%m-%d %H:%M:%S");
        data.append(mLogTime);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mFuntionId);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mAppId);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mOperateCode);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mOperateResult);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mCountry);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mUid);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mVersionCode);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mVersionName);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mEntrance);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mTab);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mPosition);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mImei);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mGoId);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mRelatedAppId);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mRemark1);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mRemark2);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mRemark3);
        data.append(StatisticsTools.PROTOCOL_DIVIDER);
        data.append(mGAdId);
        return data.toString();
    }
}
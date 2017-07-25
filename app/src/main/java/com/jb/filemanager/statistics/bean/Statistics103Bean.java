package com.jb.filemanager.statistics.bean;

import com.jb.filemanager.buyuser.BuyUserManager;
import com.jb.filemanager.statistics.StatisticsConstants;

/**
 * Created by nieyh on 2017/3/1.<br>
 * 协议：103协议 <br>
 * 功能ID：584 <br>
 * 上传说明：
 * <ol>
 *     <li>日志必须<b><u>实时上传</u></b>，上传成功后（标准：服务器成功接收日志后会返回OK）必须删除客户端本地日志，避免重复上传；</li>
 *     <li>发包前必须通过测试组同学的验证；</li>
 *     <li>标*字段为必填字段，非标*字段则按操作要求填</li>
 *     <li>带（SDK）字段为客户端统计SDK负责采集的信息，一般为静态信息</li>
 * </ol>
 * 简述需要上传的数据：<br>
 * 1、下面表格中的数据指定数据上传 <br>
 * <ol>
 *     <li>功能点ID = 584</li>
 *     <li>操作代码 = force_install</li>
 *     <li>操作结果 =  0：未成功，1：成功 （默认成功）</li>
 *     <li>入口 = 125 (产品id)</li>
 * </ol>
 * 2、其他的数据传入空值<br>
 *     <br>
 * 格式：
 * 日志序列||Android ID||GOID|IMIE||日志打印时间||国家||渠道||版本号||版本名||GADID||日志采集批次||ABTest标识||静态信息||动态信息||功能点ID||统计对象||操作代码||操作结果||入口||Tab分类||位置||关联对象||广告ID||备注
 * <br><br>
 * 备注：<a href = "http://wiki.3g.net.cn/pages/viewpage.action?pageId=18779778">103协议WiKi链接地址</a>
 */

public class Statistics103Bean {
    /**
     * 统计对象
     */
    public String mStatisticsObject;

    /**
     * 操作代码
     * */
    public String mOperateCode;

    /**
     * 操作结果
     * */
    public String mOperateResult;

    /**
     * 入口
     * */
    public String mEntrance;

    /**
     * Tab分类
     */
    public String mTab;

    /**
     * 位置
     * */
    public String mLocation;
    /**
     * 关联对象
     */
    public String mRelativeObject;
    /**
     * 广告Id
     * */
    public String mAdId;
    /**
     * 备注
     */
    public String mRemark;

    private Statistics103Bean() {}

    /**
     * 创建103统计协议
     * */
    public static Statistics103Bean build() {
        Statistics103Bean statistics103Bean = new Statistics103Bean();
        statistics103Bean.mOperateCode = "force_install";
        statistics103Bean.mEntrance = StatisticsConstants.PRODUCT_ID;
        statistics103Bean.mOperateResult = "1";
        statistics103Bean.mStatisticsObject = "";
        statistics103Bean.mTab = "";
        statistics103Bean.mLocation = "";
        statistics103Bean.mRelativeObject = "";
        statistics103Bean.mAdId = BuyUserManager.getInstance().getBuyUserChannel();
        statistics103Bean.mRemark = "";
        return statistics103Bean;
    }

    /**
     * 将103协议转换成统计需要的数据字符串
     * @param separator 分隔符
     * */
    public String transformStatisticString(String separator) {
        return String.valueOf(StatisticsConstants.LOG_ID_584) + separator +
                mStatisticsObject + separator +
                mOperateCode + separator +
                mOperateResult + separator +
                mEntrance + separator +
                mTab + separator +
                mLocation + separator +
                mRelativeObject + separator +
                mAdId + separator +
                mRemark + separator;
    }
}

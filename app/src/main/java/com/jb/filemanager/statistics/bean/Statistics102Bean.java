package com.jb.filemanager.statistics.bean;

/**
 * 102 协议bean
 * http://wiki.3g.net.cn/pages/viewpage.action?pageId=17239322
 */
public class Statistics102Bean {

    // 设置信息
    public String mSettingInfo;
    // 类型
    public String mType;
    // 位置
    public String mLocation;
    // 备注
    public String mRemark;

    private Statistics102Bean() {
        mSettingInfo = "";
        mType = "";
        mLocation = "";
        mRemark = "";
    }

    /**
     * 获取 Statistics102Bean 对象后，必须对mSettingInfo进行赋值，否则不会统计
     */
    public static Statistics102Bean builder() {
        return new Statistics102Bean();
    }

}
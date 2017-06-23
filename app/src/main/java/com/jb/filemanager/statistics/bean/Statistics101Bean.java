package com.jb.filemanager.statistics.bean;

/**
 * 101 统计bean
 *
 */

public class Statistics101Bean {
    /**
     * 操作代码
     */
    public String mOperateId;
    /**
     * 统计对象
     */
    public String mStatisticsObject;
    /**
     * 入口
     */
    public String mEntrance;
    /**
     * tab分类
     */
    public String mTab;
    /**
     * 位置
     */
    public String mLocation;
    /**
     * 关联对象
     */
    public String mRelativeObject;
    /**
     * 备注
     */
    public String mRemark;

    public Statistics101Bean() {
        mOperateId = "";
        mStatisticsObject = "";
        mEntrance = "";
        mTab = "";
        mLocation = "";
        mRelativeObject = "";
        mRemark = "";
    }

    public Statistics101Bean(String operateId) {
        mOperateId = operateId;
    }

    /**
     * 外部调用接口 获得对象后，必须对操作码　mOpeateId进行赋值
     *
     * @return bean
     */
    public static Statistics101Bean builder() {
        return new Statistics101Bean();
    }

    @Override
    public String toString() {
        return "Statistics101Bean{" +
                "mOperateId='" + mOperateId + '\'' +
                ", mStatisticsObject='" + mStatisticsObject + '\'' +
                ", mEntrance='" + mEntrance + '\'' +
                ", mTab='" + mTab + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mRelativeObject='" + mRelativeObject + '\'' +
                ", mRemark='" + mRemark + '\'' +
                '}';
    }
}

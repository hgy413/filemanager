package com.jb.filemanager.statistics.bean;

import android.text.format.Time;

/**
 * Created by bill wang on 2017/6/20.
 *
 */

public class BaseStatisticsBean {

    private final static Time TIME = new Time();

    /**
     * 获取当间的时间,东8区,即中国时间, 使用默认格式%Y/%m/%d %H:%M:%S<br>
     * @see #getNowTimeInEast8(String)
     * @return result
     */
    protected String getNowTimeInEast8() {
        return getNowTimeInEast8("%Y-%m-%d %H:%M:%S");
    }

    /**
     * 获取当间的时间,东8区,即中国时间
     *
     * @param format
     *            如 %Y-%m-%d %H:%M:%S
     * @return result
     */
    protected String getNowTimeInEast8(String format) {
        TIME.setToNow();
        // 东8区
        TIME.set(TIME.toMillis(true) - ((TIME.gmtoff - 8 * 60 * 60) * 1000));
        return TIME.format(format);
    }

    /**
     * 重置数据<br>
     */
    public void reset() {

    }

    /**
     * 获取格式化的统计数据<br>
     *
     * @return result
     */
    public String toFormatStatisticsData() {
        return "";
    }
}

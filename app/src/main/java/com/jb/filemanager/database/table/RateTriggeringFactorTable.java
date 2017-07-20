package com.jb.filemanager.database.table;

/**
 * Created by nieyh on 17-7-19.
 * 评分触发因素表
 */

public class RateTriggeringFactorTable {
    //表名
    public static final String TABLE_NAME = "rate_triggeringfactor_table";
    //构造表的语句
    public static final String CREATE_TABLE;
    //id
    public static final String ID = "_id";
    //因素类型
    public static final String FACTOR_TYPE = "_factor_type";
    //触发次数
    public static final String TRIGGER_COUNTER = "_trigger_counter";
    //上次触发时间
    public static final String LAST_TRIGGER_TIME = "_last_trigger_time";

    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" ("+ ID +" INTEGER PRIMARY KEY, ");
        b.append(FACTOR_TYPE + " INTEGER, ");
        b.append(TRIGGER_COUNTER + " INTEGER default 1, ");
        b.append(LAST_TRIGGER_TIME + " INTEGER default 0)");
        CREATE_TABLE = b.toString();
    }
}

package com.jb.filemanager.function.search.database;

/**
 * Created by nieyh on 17-7-5.
 * 外部存储信息表
 */

public class ExternalStorageInfoTable {
    //表名
    public static final String TABLE_NAME = "external_storage_info_table";
    //构造表的语句
    public static final String CREATE_TABLE;
    //id
    public static final String ID = "_id";
    //绝对路径
    public static final String ABSOLUTE_PATH = "_path";
    //文件名字
    public static final String FILE_NAME = "_file_name";
    //文件类型
    public static final String FILE_TYPE = "_file_type";
    //文件修改时间 linux中不存在创建时间
    public static final String FILE_MODIFY_TIME = "_file_modify_time";
    //删除列表
    public static final String DELETE_TABLE = "delete from " + TABLE_NAME;

    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" ("+ ID +" INTEGER PRIMARY KEY, ");
        b.append(FILE_NAME + " TEXT, ");
        b.append(FILE_TYPE + " INGEGER, ");
        b.append(FILE_MODIFY_TIME + " INGEGER, ");
        b.append(ABSOLUTE_PATH + " TEXT)");
        CREATE_TABLE = b.toString();
    }
}

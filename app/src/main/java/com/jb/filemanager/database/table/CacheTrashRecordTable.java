package com.jb.filemanager.database.table;

/**
 * Created by nieyh on 2017/3/17.
 * 缓存垃圾清理记录 用于每日报告时 显示
 */

public class CacheTrashRecordTable {
    //缓存垃圾表
    public static final String TABLE_NAME = "cache_trash_record_table";
    //缓存垃圾应用名称
    public static final String CACHE_TRASH_APP_NAME = "cache_trash_app_name";
    //缓存垃圾包名
    public static final String CACHE_TRASH_PKG_NAME = "cache_trash_pkg_name";
    //缓存垃圾的大小
    public static final String CACHE_TRASH_SIZE = "cache_trash_size";
    //缓存垃圾记录时间
    public static final String CACHE_TRASH_RECORD_TIME = "cache_trash_record_time";
    //id
    public static final String ID = "cache_trash_id";

    public static final String CREATE_TABLE;

    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" (" + ID + " INTEGER PRIMARY KEY, ");
        b.append(CACHE_TRASH_PKG_NAME + " TEXT, ");
        b.append(CACHE_TRASH_SIZE + " INTEGER, ");
        b.append(CACHE_TRASH_RECORD_TIME + " INTEGER, ");
        b.append(CACHE_TRASH_APP_NAME + " TEXT)");
        CREATE_TABLE = b.toString();
    }
}

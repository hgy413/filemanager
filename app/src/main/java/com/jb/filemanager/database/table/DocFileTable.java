package com.jb.filemanager.database.table;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/21 18:14
 */

public class DocFileTable {
    /**
     * ============== 表名 ==============
     */
    public static final String TABLE_NAME = "doc_file_table";
    /**
     * ============== 构造表的语句 ==============
     */
    public static final String CREATE_TABLE;

    public static final String DOC_ID = "doc_id";
    public static final String DOC_NAME = "doc_name";
    public static final String DOC_PATH = "doc_path";
    public static final String DOC_SIZE = "doc_size";
    public static final String DOC_MODIFY_DATE = "doc_modify_date";
    public static final String DOC_ADDED_DATE = "doc_added_date";



    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" (_id INTEGER PRIMARY KEY, ");
        b.append(DOC_ID + " INTEGER, ");
        b.append(DOC_NAME + " TEXT, ");
        b.append(DOC_PATH + " TEXT)");
        b.append(DOC_SIZE + " INTEGER)");
        b.append(DOC_MODIFY_DATE + " TEXT)");
        b.append(DOC_ADDED_DATE + " TEXT)");
        CREATE_TABLE = b.toString();
    }
}

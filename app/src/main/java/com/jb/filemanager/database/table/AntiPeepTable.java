package com.jb.filemanager.database.table;

import android.database.Cursor;

import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;

/**
 * 防偷窥数据表
 *
 * @author chenbenbin
 */
public class AntiPeepTable implements ITable {
    /**
     * 数据库ID<br>
     * 值类型: int
     */
    public static final String ID = "_id";

    /**
     * 文件名<br>
     * 值类型: string
     */
    public static final String FILE_NAME = "file_name";
    /**
     * 应用包名
     */
    public static final String PKG_NAME = "pkg_name";
    /**
     * 创建时间
     */
    public static final String CREATE_TIME = "create_time";

    // ============================================================================//
    /**
     * 表名
     */
    public static final String TABLE_NAME = "anti_peep_table";

    /**
     * 创建表
     */
    public static final String CREATE_TABLE;

    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" (" + ID + " INTEGER PRIMARY KEY, ");
        b.append(PKG_NAME + " TEXT, ");
        b.append(CREATE_TIME + " LONG, ");
        b.append(FILE_NAME + " TEXT)");
        CREATE_TABLE = b.toString();
    }

    public static AntiPeepBean parseFromCursor(Cursor cursor) {
        AntiPeepBean bean = new AntiPeepBean();
        bean.setPath(cursor.getString(cursor.getColumnIndex(FILE_NAME)));
        bean.setPackageName(cursor.getString(cursor.getColumnIndex(PKG_NAME)));
        bean.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
        return bean;
    }
}

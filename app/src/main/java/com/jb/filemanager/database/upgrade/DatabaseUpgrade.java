package com.jb.filemanager.database.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.BaseDatabaseHelper;

/**
 * 数据库升级
 * <p/>
 * 类名称：DatabaseUpgrade 类描述： 创建人：makai 修改人：makai 修改时间：2014年11月10日 上午10:56:00
 * 修改备注：
 *
 * @version 1.0.0
 */
public class DatabaseUpgrade extends BaseDatabaseHelper.AbstractDatabaseUpgrade {

    private static final String TAG = "DatabaseUpgrade";

    public DatabaseUpgrade(Context context) {
        super(context);
    }

    @Override
    public boolean upgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Method[] methods = getClass().getDeclaredMethods();
        while (oldVersion < newVersion) {
            if (oldVersion == 1) {
                upgrade1To2(db);
            }
            oldVersion++;
        }
        return false;
    }

    public void upgrade1To2(SQLiteDatabase db) {
//        db.execSQL();
    }

}
package com.jb.filemanager.function.search.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.BaseDatabaseHelper;

/**
 * Created by nieyh on 17-7-5.
 */

public class ExternalStorageDataBaseHelper extends BaseDatabaseHelper {

    public static final String LOG_TAG = ">>> ExternalStorageDataBaseHelper";

    /**
     * 如果搜索数据库的表结构有变化 请在这里 + 1
     * 并在此添加注释说明
     * Created by nieyh {@link #DB_VERSION} = 1 <b> 初始版本： 只有一个文件树信息表<b/>
     */
    private final static int DB_VERSION = 1;

    //数据库名
    private static final String DATABASE_NAME = "exter_storage.db";

    public ExternalStorageDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //开启事务
        db.beginTransaction();
        try {
            // TODO: 17-7-5 创建新表格
            db.execSQL(ExternalStorageInfoTable.CREATE_TABLE);
            mIsNewDB = true;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_MIN_VERSION
                || oldVersion > newVersion
                || newVersion > DB_VERSION) {
            return;
        }
        if (oldVersion != newVersion) {
            //开启事务
            db.beginTransaction();
            try {
                mIsNewDB = true;
                // TODO: 17-7-5 执行删除所有老的表格 然后创建现在需要的表格
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }
}

package com.jb.filemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.table.AppPermissionsTable;
import com.jb.filemanager.database.table.CleanIgnoreTable;
import com.jb.filemanager.database.table.CleanScanOvertimeTable;
import com.jb.filemanager.database.table.DocFileTable;
import com.jb.filemanager.database.table.IgnoreListTable;
import com.jb.filemanager.database.table.LockerSceneItemTable;
import com.jb.filemanager.database.table.LockerSceneTable;
import com.jb.filemanager.database.table.LockerTable;
import com.jb.filemanager.database.table.RateTriggeringFactorTable;
import com.jb.filemanager.database.upgrade.DatabaseUpgrade;
/**
 * @version 1.0.0
 */
public class DatabaseHelper extends BaseDatabaseHelper {

    public static final String LOG_TAG = ">>> DatabaseHelper";

    private Context mContext;
    private final static int DB_VERSION = 1;

    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "filemanager.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, DB_VERSION);
        mContext = context;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            if (!mIsUpgrade) { // 更新失败，则删除数据库，再行创建。
                if (db != null) {
                    db.close();
                }
                context.deleteDatabase(DATABASE_NAME);
                getWritableDatabase();
            }
        } catch (Exception e) {
            context.deleteDatabase(DATABASE_NAME);
        }
//        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
//        while(cursor.moveToNext()){
//            //遍历出表名
//            String name = cursor.getString(0);
//            Log.i("System.out", name);
//        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // 白名单
            db.execSQL(CleanIgnoreTable.CREATE_TABLE);
            db.execSQL(CleanScanOvertimeTable.CREATE_TABLE);
            db.execSQL(IgnoreListTable.CREATE_TABLE);

            // app locker
            db.execSQL(LockerTable.CREATE_TABLE);
            db.execSQL(LockerSceneTable.CREATE_TABLE);
            db.execSQL(LockerSceneItemTable.CREATE_TABLE);

            // 权限检测
            db.execSQL(AppPermissionsTable.CREATE_TABLE);
            //评分引导触发因素表
            db.execSQL(RateTriggeringFactorTable.CREATE_TABLE);
            db.execSQL(DocFileTable.CREATE_TABLE);
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
        new DatabaseUpgrade(mContext).upgradeDb(db, oldVersion, newVersion);
    }

    public static String getDbName() {
        return DATABASE_NAME;
    }

    public static int getDbVersion() {
        return DB_VERSION;
    }

}
package com.jb.filemanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jb.filemanager.database.provider.DuplicatePhotoTable;
import com.jb.filemanager.database.table.CacheTrashRecordTable;
import com.jb.filemanager.database.table.CleanIgnoreTable;
import com.jb.filemanager.database.table.CleanScanOvertimeTable;
import com.jb.filemanager.database.table.IgnoreListTable;
import com.jb.filemanager.database.upgrade.DatabaseUpgrade;


/**
 * @version 1.0.0
 */
public class DatabaseHelper extends BaseDatabaseHelper {

    public static final String LOG_TAG = ">>> DatabaseHelper";

    private Context mContext;
    /**
     * 数据库当前版本号(数据库每次升级该字段加1)
     * modify by nieyh {@link #DB_VERSION} = 1 <b> 原始版本：applock <b/>
     * modify by xiaoyu {@link #DB_VERSION} = 2 <b> 增加DuplicatedPhoto <b/>
     * modify by nieyh {@link #DB_VERSION} = 3 <b> 增加权限警报 <b/>
     * modify by xiaoyu {@link #DB_VERSION} = 4 <b> 增加扫描白名单 & 频率</b>
     * modify by nieyh {@link #DB_VERSION} = 5 <b> 增加apk文件路径</b>
     * modify by nieyh {@link #DB_VERSION} = 6 <b> 增加缓存清理的记录</b>
     * modify by xiaoyu {@link #DB_VERSION} = 7 <b> 增加通知栏拦截白名单</b>2017-3-22 18:00:53
     * modify by xiaoyu {@link #DB_VERSION} = 8 <b> 增加省电加速白名单</b>2017-4-1 15:21:05
     */
    private final static int DB_VERSION = 8;

    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "boost.db";

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
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table';", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            Log.i("System.out", name);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // TODO: 2016/10/17 在此创建表
            // app locker
            /*db.execSQL(LockerTable.CREATE_TABLE);
            db.execSQL(LockerSceneTable.CREATE_TABLE);
            db.execSQL(LockerSceneItemTable.CREATE_TABLE);
            db.execSQL(AntiPeepTable.CREATE_TABLE);*/
            // 重复照片
            db.execSQL(DuplicatePhotoTable.CREATE_TABLE);
            //应用权限
//            db.execSQL(AppPermissionsTable.CREATE_TABLE);
            // 白名单
            db.execSQL(CleanIgnoreTable.CREATE_TABLE);
            db.execSQL(CleanScanOvertimeTable.CREATE_TABLE);
            db.execSQL(IgnoreListTable.CREATE_TABLE);
            // 2017年3月6日19:55:30
//            db.execSQL(AppLaunchStatisticsTable.CREATE_TABLE);

            //apk路径
//            db.execSQL(ApkPathTable.CREATE_TABLE);
            //缓存垃圾清理记录
            db.execSQL(CacheTrashRecordTable.CREATE_TABLE);

            // 通知栏拦截白名单2017年3月22日18:10:18
//            db.execSQL(NotificationWhiteListTable.CREATE_TABLE);

            // 省电加速白名单
//            db.execSQL(BatteryBoostIgnoreListTable.CREATE_TABLE);

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
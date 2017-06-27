package com.jb.filemanager.database.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.swift.boost.database.BaseDatabaseHelper;
import com.swift.boost.database.table.ApkPathTable;
import com.swift.boost.database.table.AppLaunchStatisticsTable;
import com.swift.boost.database.table.AppPermissionsTable;
import com.swift.boost.database.table.BatteryBoostIgnoreListTable;
import com.swift.boost.database.table.CacheTrashRecordTable;
import com.swift.boost.database.table.CleanIgnoreTable;
import com.swift.boost.database.table.CleanScanOvertimeTable;
import com.swift.boost.database.table.IgnoreListTable;
import com.swift.boost.database.table.NotificationWhiteListTable;
import com.swift.boost.function.duplicatedphoto.database.DuplicatePhotoTable;

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
            if (oldVersion == 2) {
                upgrade2To3(db);
            }
            if (oldVersion == 3) {
                upgrade3To4(db);
            }
            if (oldVersion == 4) {
                upgrade4To5(db);
            }
            if (oldVersion == 5) {
                upgrade5To6(db);
            }
            if (oldVersion == 6) {
                upgrade6To7(db);
            }
            if (oldVersion == 7) {
                upgrade7To8(db);
            }
            oldVersion++;
        }
        return false;
    }

    public void upgrade1To2(SQLiteDatabase db) {
        // 重复照片
        db.execSQL(DuplicatePhotoTable.CREATE_TABLE);
    }

    public void upgrade2To3(SQLiteDatabase db) {
        // 应用权限
        db.execSQL(AppPermissionsTable.CREATE_TABLE);
    }

    public void upgrade3To4(SQLiteDatabase db) {
        // 白名单 2017年2月27日14:22:10 xiaoyu
        db.execSQL(CleanIgnoreTable.CREATE_TABLE);
        db.execSQL(CleanScanOvertimeTable.CREATE_TABLE);
        db.execSQL(IgnoreListTable.CREATE_TABLE);
        // 2017年3月6日19:55:30
        db.execSQL(AppLaunchStatisticsTable.CREATE_TABLE);
    }

    public void upgrade4To5(SQLiteDatabase db) {
        //apk路径
        db.execSQL(ApkPathTable.CREATE_TABLE);
    }

    public void upgrade5To6(SQLiteDatabase db) {
        //缓存垃圾清理记录
        db.execSQL(CacheTrashRecordTable.CREATE_TABLE);
    }

    public void upgrade6To7(SQLiteDatabase db) {
        // 通知栏拦截白名单2017年3月22日18:10:18
        db.execSQL(NotificationWhiteListTable.CREATE_TABLE);
    }
    public void upgrade7To8(SQLiteDatabase db) {
        db.execSQL(BatteryBoostIgnoreListTable.CREATE_TABLE);
    }
}
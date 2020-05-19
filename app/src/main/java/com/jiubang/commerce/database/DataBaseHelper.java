package com.jiubang.commerce.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.table.AdConfigInfoTable;
import com.jiubang.commerce.database.table.AdShowClickTable;
import com.jiubang.commerce.database.table.AdUrlTable;
import com.jiubang.commerce.database.table.AdvertFilterTable;
import com.jiubang.commerce.database.table.InstalledPkgTable;
import com.jiubang.commerce.database.table.PkgRecordTable;
import com.jiubang.commerce.database.table.WaitActivationAppTable;
import com.jiubang.commerce.statistics.adinfo.AppInstallMonitorTable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String ASC = " ASC";
    public static final String AUTOINCREMENT = "AUTOINCREMENT";
    public static final String DATABASE_NAME = "ad_sdk.db";
    private static final int DB_VERSION_CODE = 8;
    private static final int DB_VERSION_ONE = 1;
    public static final String DEFAULT = "DEFAULT";
    public static final String DESC = " DESC";
    public static final String FOR_EACH_ROW = "FOR EACH ROW";
    public static final String IF_NOT_EXISTS = "IF NOT EXISTS";
    public static final String INNER_JOIN = "INNER JOIN";
    public static final String INSERT_INTO = "INSERT INTO";
    public static final String INSERT_OR_REPLACE_INTO = "INSERT OR REPLACE INTO";
    public static final String NOT_NULL = "NOT NULL";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String TYPE_INTEGER = "INTEGER";
    public static final String TYPE_NUMERIC = "NUMERIC";
    public static final String TYPE_TEXT = "TEXT";
    public static final String UNIQUE = "UNIQUE";
    public static final String VALUES = "VALUES";
    private static DataBaseHelper sInstance = null;
    private Context mContext;
    private SQLiteQueryBuilder mSqlQB = null;
    private boolean mUpdateResult = true;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 8);
        SQLiteDatabase db;
        this.mContext = context;
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "DataBaseHelper(" + context + ")");
        }
        try {
            this.mSqlQB = new SQLiteQueryBuilder();
            try {
                db = getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
                db = getReadableDatabase();
            }
            if (!this.mUpdateResult) {
                if (db != null) {
                    db.close();
                }
                this.mContext.deleteDatabase(DATABASE_NAME);
                getWritableDatabase();
            }
        } catch (Exception e2) {
            LogUtils.e("Ad_SDK", "DataBaseHelper(Exception:" + (e2 != null ? e2.getMessage() : ""), e2);
        }
    }

    public static DataBaseHelper getInstance(Context context) {
        init(context);
        return sInstance;
    }

    private static void init(Context context) {
        synchronized (DataBaseHelper.class) {
            if (sInstance == null) {
                sInstance = new DataBaseHelper(context != null ? context.getApplicationContext() : null);
            }
        }
    }

    public void onCreate(SQLiteDatabase db) {
        LogUtils.d("Ad_SDK", "DatabaseHelper onCreate");
        db.beginTransaction();
        try {
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "创建数据库");
                LogUtils.d("Ad_SDK", "CREATE TABLE AD_URL:CREATE TABLE IF NOT EXISTS AD_URL (packageName TEXT, redirectUrl TEXT, adUrl TEXT, updateTime NUMERIC)");
            }
            db.execSQL(AdUrlTable.CREATE_TABLE_SQL);
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "CREATE TABLE InstalledPkg:CREATE TABLE IF NOT EXISTS InstalledPkg (packageName TEXT NOT NULL UNIQUE DEFAULT(-1), updateTime NUMERIC)");
            }
            db.execSQL(InstalledPkgTable.CREATE_TABLE_SQL);
            doUpgrade(db, 1, 8);
            if (LogUtils.isShowLog()) {
                LogUtils.d("Ad_SDK", "创建数据库完毕");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "DatabaseHelper onCreate Error::->" + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (LogUtils.isShowLog()) {
            Log.i("Ad_SDK", "onDowngrade(oldVersion=" + oldVersion + ", newVersion=" + newVersion + ")");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doUpgrade(db, oldVersion, newVersion);
    }

    private void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (LogUtils.isShowLog()) {
            LogUtils.d("Ad_SDK", "doUpgrade(" + oldVersion + "," + newVersion + ")");
        }
        if (oldVersion >= 1 && oldVersion <= newVersion && newVersion <= 8) {
            RuntimeException exception = null;
            int i = oldVersion;
            while (i < newVersion) {
                String methodName = "onUpgradeDB" + i + "To" + (i + 1);
                try {
                    this.mUpdateResult = ((Boolean) getClass().getMethod(methodName, new Class[]{SQLiteDatabase.class}).invoke(this, new Object[]{db})).booleanValue();
                } catch (Throwable t) {
                    exception = new RuntimeException(t);
                }
                if (this.mUpdateResult && exception == null) {
                    i++;
                } else if (exception != null) {
                    throw exception;
                } else {
                    throw new RuntimeException("update database has exception in " + methodName);
                }
            }
        }
    }

    public boolean onUpgradeDB1To2(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, WaitActivationAppTable.TABLE_NAME)) {
                db.execSQL(WaitActivationAppTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB1To2(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB2To3(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, AdvertFilterTable.TABLE_NAME)) {
                db.execSQL(AdvertFilterTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB2To3(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB3To4(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, AdConfigInfoTable.TABLE_NAME)) {
                db.execSQL(AdConfigInfoTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB3To4(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB4To5(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, InstalledPkgTable.TABLE_NAME)) {
                db.execSQL(InstalledPkgTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB4To5(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB5To6(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, AdShowClickTable.TABLE_NAME)) {
                db.execSQL(AdShowClickTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB5To6(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB6To7(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, PkgRecordTable.TABLE_NAME)) {
                db.execSQL(PkgRecordTable.CREATE_TABLE_SQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB6To7(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean onUpgradeDB7To8(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (!isExistTable(db, AppInstallMonitorTable.TABLENAME)) {
                db.execSQL(AppInstallMonitorTable.CREATETABLESQL);
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "onUpgradeDB7To8(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    private boolean isExistTable(SQLiteDatabase db, String tableName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            Cursor cursor2 = db.query("sqlite_master", (String[]) null, "type='table' and name='" + tableName + "'", (String[]) null, (String) null, (String) null, (String) null);
            if (cursor2 != null && cursor2.getCount() > 0) {
                result = true;
            }
            if (cursor2 != null) {
                cursor2.close();
            }
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "isExistTable(Exception:" + (e != null ? e.getMessage() : "") + ")", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return result;
    }

    public long insert(String tableName, ContentValues values) {
        try {
            return getWritableDatabase().insert(tableName, (String) null, values);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "Exception when insert in " + tableName + "," + (e != null ? e.getMessage() : "error"));
            return 0;
        }
    }

    public long replace(String tableName, ContentValues values) {
        try {
            return getWritableDatabase().replace(tableName, (String) null, values);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "Exception when replace in " + tableName + "," + (e != null ? e.getMessage() : "error"));
            return 0;
        }
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        try {
            return getWritableDatabase().delete(table, whereClause, whereArgs);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "Exception when delete in " + table + ", " + whereClause + "," + (e != null ? e.getMessage() : "error"));
            return 0;
        }
    }

    public int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        try {
            return getWritableDatabase().update(tableName, values, selection, selectionArgs);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "Exception when update in " + tableName + ", " + selection + "," + (e != null ? e.getMessage() : "error"));
            return 0;
        }
    }

    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        try {
            return getReadableDatabase().query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder);
        } catch (SQLException e) {
            LogUtils.e("Ad_SDK", "SQLException when query in " + tableName + ", " + selection + "," + (e != null ? e.getMessage() : "error"));
            return null;
        } catch (IllegalStateException e2) {
            LogUtils.e("Ad_SDK", "IllegalStateException when query in " + tableName + ", " + selection + "," + (e2 != null ? e2.getMessage() : "error"));
            return null;
        } catch (Exception e3) {
            LogUtils.e("Ad_SDK", "IllegalStateException when query in " + tableName + ", " + selection + "," + (e3 != null ? e3.getMessage() : "error"));
            return null;
        }
    }

    public Cursor queryCrossTables(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        synchronized (this.mSqlQB) {
            this.mSqlQB.setTables(tableName);
            try {
                result = this.mSqlQB.query(getReadableDatabase(), projection, selection, selectionArgs, (String) null, (String) null, sortOrder);
            } catch (SQLException e) {
                LogUtils.e("Ad_SDK", "SQLException when query in " + tableName + ", " + selection + "," + (e != null ? e.getMessage() : "error"));
            } catch (IllegalStateException e2) {
                LogUtils.e("Ad_SDK", "IllegalStateException when query in " + tableName + ", " + selection + "," + (e2 != null ? e2.getMessage() : "error"));
            }
        }
        return result;
    }

    public void insertOrReplace(String tableName, String values, String selection) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            StringBuffer sb = new StringBuffer();
            sb.append("INSERT OR REPLACE INTO " + tableName + " ");
            sb.append("VALUES " + values + " ");
            if (selection != null) {
                sb.append("where " + selection + " ");
            }
            db.execSQL(sb.toString());
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "IllegalStateException when insertOrReplace in " + tableName + ", " + selection + "," + (e != null ? e.getMessage() : "error"));
        }
    }
}

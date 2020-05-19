package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;

public class PkgRecordTable {
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS PkgRecordTable (packageName TEXT NOT NULL UNIQUE DEFAULT(-1), hash INTEGER, updateTime NUMERIC)";
    public static final String HASH = "hash";
    private static final int MAX_ROWS = 500;
    public static final String PACKAGE_NAME = "packageName";
    public static final String TABLE_NAME = "PkgRecordTable";
    public static final String UPDATE_TIME = "updateTime";
    private static PkgRecordTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    private PkgRecordTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static synchronized PkgRecordTable getInstance(Context context) {
        PkgRecordTable pkgRecordTable;
        synchronized (PkgRecordTable.class) {
            if (sInstance == null) {
                sInstance = new PkgRecordTable(context);
            }
            pkgRecordTable = sInstance;
        }
        return pkgRecordTable;
    }

    public boolean insertData(String packageName) {
        boolean z = false;
        if (!TextUtils.isEmpty(packageName)) {
            SQLiteDatabase sqlDatabase = null;
            try {
                sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
                sqlDatabase.beginTransaction();
                ContentValues values = new ContentValues();
                values.put("packageName", packageName);
                values.put(HASH, Integer.valueOf(packageName.hashCode()));
                values.put("updateTime", Long.valueOf(System.currentTimeMillis()));
                sqlDatabase.replace(TABLE_NAME, (String) null, values);
                sqlDatabase.setTransactionSuccessful();
                z = true;
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    sqlDatabase.endTransaction();
                }
            } catch (Exception e) {
                LogUtils.e("Ad_SDK", "PkgRecordTable--insertData Exception!", e);
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    sqlDatabase.endTransaction();
                }
            } catch (Throwable th) {
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    sqlDatabase.endTransaction();
                }
                throw th;
            }
        }
        return z;
    }

    public boolean deleteDataBefore(long timePoint) {
        boolean z = true;
        if (timePoint <= 0) {
            return false;
        }
        if (this.mDatabaseHelper.delete(TABLE_NAME, " updateTime < ? ", new String[]{String.valueOf(timePoint)}) <= 0) {
            z = false;
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x006b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.SparseArray<com.jiubang.commerce.database.model.InstalledPkgBean> getAllData() {
        /*
            r20 = this;
            android.util.SparseArray r16 = new android.util.SparseArray
            r16.<init>()
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            r11 = 0
            java.lang.String r9 = "updateTime DESC"
            r0 = r20
            com.jiubang.commerce.database.DataBaseHelper r2 = r0.mDatabaseHelper     // Catch:{ Exception -> 0x007d }
            java.lang.String r3 = "PkgRecordTable"
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r11 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x007d }
            if (r11 == 0) goto L_0x005e
            boolean r2 = r11.moveToFirst()     // Catch:{ Exception -> 0x007d }
            if (r2 == 0) goto L_0x005e
            r10 = 0
        L_0x0025:
            com.jiubang.commerce.database.model.InstalledPkgBean r10 = new com.jiubang.commerce.database.model.InstalledPkgBean     // Catch:{ Exception -> 0x007d }
            r10.<init>()     // Catch:{ Exception -> 0x007d }
            java.lang.String r2 = "packageName"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007d }
            java.lang.String r15 = r11.getString(r2)     // Catch:{ Exception -> 0x007d }
            java.lang.String r2 = "hash"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007d }
            int r13 = r11.getInt(r2)     // Catch:{ Exception -> 0x007d }
            java.lang.String r2 = "updateTime"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007d }
            long r18 = r11.getLong(r2)     // Catch:{ Exception -> 0x007d }
            r10.setPackageName(r15)     // Catch:{ Exception -> 0x007d }
            r0 = r18
            r10.setUpdateTime(r0)     // Catch:{ Exception -> 0x007d }
            r14.add(r10)     // Catch:{ Exception -> 0x007d }
            r0 = r16
            r0.put(r13, r10)     // Catch:{ Exception -> 0x007d }
            boolean r2 = r11.moveToNext()     // Catch:{ Exception -> 0x007d }
            if (r2 != 0) goto L_0x0025
        L_0x005e:
            if (r11 == 0) goto L_0x0063
            r11.close()
        L_0x0063:
            int r2 = r14.size()
            r3 = 500(0x1f4, float:7.0E-43)
            if (r2 <= r3) goto L_0x007c
            r2 = 499(0x1f3, float:6.99E-43)
            java.lang.Object r2 = r14.get(r2)
            com.jiubang.commerce.database.model.InstalledPkgBean r2 = (com.jiubang.commerce.database.model.InstalledPkgBean) r2
            long r2 = r2.getUpdateTime()
            r0 = r20
            r0.deleteDataBefore(r2)
        L_0x007c:
            return r16
        L_0x007d:
            r12 = move-exception
            r12.printStackTrace()     // Catch:{ all -> 0x0087 }
            if (r11 == 0) goto L_0x0063
            r11.close()
            goto L_0x0063
        L_0x0087:
            r2 = move-exception
            if (r11 == 0) goto L_0x008d
            r11.close()
        L_0x008d:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.PkgRecordTable.getAllData():android.util.SparseArray");
    }
}

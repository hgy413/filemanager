package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.AdShowClickBean;
import com.jiubang.commerce.utils.AdTimer;

public class AdShowClickTable {
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS AdShowClick (vmid INTEGER DEFAULT(-1), opt TEXT , updateTime NUMERIC)";
    public static final long DURATION = 1209600000;
    public static final String OPT = "opt";
    public static final String TABLE_NAME = "AdShowClick";
    public static final String UPDATE_TIME = "updateTime";
    public static final String VMID = "vmid";
    private static AdShowClickTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    private AdShowClickTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static AdShowClickTable getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AdShowClickTable.class) {
                if (sInstance == null) {
                    sInstance = new AdShowClickTable(context);
                }
            }
        }
        return sInstance;
    }

    public boolean insertData(AdShowClickBean bean) {
        boolean z = false;
        if (bean != null) {
            SQLiteDatabase sqlDatabase = null;
            try {
                sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
                sqlDatabase.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(VMID, Integer.valueOf(bean.getVMID()));
                values.put(OPT, bean.getOpt());
                values.put("updateTime", Long.valueOf(bean.getUpdateTime()));
                sqlDatabase.replace(TABLE_NAME, (String) null, values);
                sqlDatabase.setTransactionSuccessful();
                z = true;
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Exception e) {
                        LogUtils.e("Ad_SDK", "AdShowClickTable--sqlDatabase.endTransaction Exception!", e);
                    }
                }
            } catch (Exception e2) {
                LogUtils.e("Ad_SDK", "AdShowClickTable--insertData Exception!", e2);
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Exception e3) {
                        LogUtils.e("Ad_SDK", "AdShowClickTable--sqlDatabase.endTransaction Exception!", e3);
                    }
                }
            } catch (Throwable th) {
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Exception e4) {
                        LogUtils.e("Ad_SDK", "AdShowClickTable--sqlDatabase.endTransaction Exception!", e4);
                    }
                }
                throw th;
            }
        }
        return z;
    }

    public boolean deleteExpiredData() {
        long time = AdTimer.getTodayZeroMills() - DURATION;
        if (this.mDatabaseHelper.delete(TABLE_NAME, " updateTime < ? ", new String[]{String.valueOf(time)}) > 0) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0078  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.AdShowClickBean> getValidData(int r19) {
        /*
            r18 = this;
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r11 = 0
            long r14 = com.jiubang.commerce.utils.AdTimer.getTodayZeroMills()     // Catch:{ Exception -> 0x007c }
            r2 = 1209600000(0x48190800, double:5.97621805E-315)
            long r16 = r14 - r2
            java.lang.String r5 = "vmid = ? AND updateTime >= ? AND updateTime < ?"
            r2 = 3
            java.lang.String[] r6 = new java.lang.String[r2]     // Catch:{ Exception -> 0x007c }
            r2 = 0
            java.lang.String r3 = java.lang.String.valueOf(r19)     // Catch:{ Exception -> 0x007c }
            r6[r2] = r3     // Catch:{ Exception -> 0x007c }
            r2 = 1
            java.lang.String r3 = java.lang.String.valueOf(r16)     // Catch:{ Exception -> 0x007c }
            r6[r2] = r3     // Catch:{ Exception -> 0x007c }
            r2 = 2
            java.lang.String r3 = java.lang.String.valueOf(r14)     // Catch:{ Exception -> 0x007c }
            r6[r2] = r3     // Catch:{ Exception -> 0x007c }
            java.lang.String r9 = "updateTime ASC"
            r0 = r18
            com.jiubang.commerce.database.DataBaseHelper r2 = r0.mDatabaseHelper     // Catch:{ Exception -> 0x007c }
            java.lang.String r3 = "AdShowClick"
            r4 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r11 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x007c }
            if (r11 == 0) goto L_0x0076
            boolean r2 = r11.moveToFirst()     // Catch:{ Exception -> 0x007c }
            if (r2 == 0) goto L_0x0076
            r10 = 0
        L_0x0041:
            com.jiubang.commerce.database.model.AdShowClickBean r10 = new com.jiubang.commerce.database.model.AdShowClickBean     // Catch:{ Exception -> 0x007c }
            r10.<init>()     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = "vmid"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007c }
            int r2 = r11.getInt(r2)     // Catch:{ Exception -> 0x007c }
            r10.setVMID(r2)     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = "opt"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = r11.getString(r2)     // Catch:{ Exception -> 0x007c }
            r10.setOpt(r2)     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = "updateTime"
            int r2 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x007c }
            long r2 = r11.getLong(r2)     // Catch:{ Exception -> 0x007c }
            r10.setUpdateTime(r2)     // Catch:{ Exception -> 0x007c }
            r13.add(r10)     // Catch:{ Exception -> 0x007c }
            boolean r2 = r11.moveToNext()     // Catch:{ Exception -> 0x007c }
            if (r2 != 0) goto L_0x0041
        L_0x0076:
            if (r11 == 0) goto L_0x007b
            r11.close()
        L_0x007b:
            return r13
        L_0x007c:
            r12 = move-exception
            java.lang.String r2 = "Ad_SDK"
            java.lang.String r3 = "AdShowClickTable--getValidData Exception!"
            com.jb.ga0.commerce.util.LogUtils.e(r2, r3, r12)     // Catch:{ all -> 0x008a }
            if (r11 == 0) goto L_0x007b
            r11.close()
            goto L_0x007b
        L_0x008a:
            r2 = move-exception
            if (r11 == 0) goto L_0x0090
            r11.close()
        L_0x0090:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.AdShowClickTable.getValidData(int):java.util.List");
    }
}

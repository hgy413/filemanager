package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.InstalledPkgBean;
import com.jiubang.commerce.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;

public class InstalledPkgTable {
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS InstalledPkg (packageName TEXT NOT NULL UNIQUE DEFAULT(-1), updateTime NUMERIC)";
    private static final long ONE_DAY_MILLS = 86400000;
    public static final String PACKAGE_NAME = "packageName";
    public static final String TABLE_NAME = "InstalledPkg";
    public static final String UPDATE_TIME = "updateTime";
    private static final long VALID_TIME_MILLS = 5184000000L;
    private static InstalledPkgTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    private InstalledPkgTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
        try {
            insertData(getNeed2InsertData(context));
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "InstalledPkgTable--constructor Exception!", e);
        }
    }

    private List<InstalledPkgBean> getNeed2InsertData(Context context) {
        List<InstalledPkgBean> beanList = getAllData();
        List<PackageInfo> pkgInfos = AppUtils.getAllInstalledApps(context);
        if (pkgInfos == null || pkgInfos.isEmpty()) {
            return null;
        }
        List<InstalledPkgBean> filteredBeans = new ArrayList<>();
        long time = System.currentTimeMillis();
        for (PackageInfo info : pkgInfos) {
            if (!needFilter(info.packageName, beanList)) {
                filteredBeans.add(new InstalledPkgBean(info.packageName, time));
            }
        }
        return filteredBeans;
    }

    private boolean needFilter(String pkgName, List<InstalledPkgBean> beanList) {
        if (beanList == null || beanList.isEmpty() || TextUtils.isEmpty(pkgName)) {
            return false;
        }
        for (InstalledPkgBean bean : beanList) {
            if (bean != null && pkgName.equals(bean.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static synchronized InstalledPkgTable getInstance(Context context) {
        InstalledPkgTable installedPkgTable;
        synchronized (InstalledPkgTable.class) {
            if (sInstance == null) {
                sInstance = new InstalledPkgTable(context);
            }
            installedPkgTable = sInstance;
        }
        return installedPkgTable;
    }

    public boolean insertData(List<InstalledPkgBean> beanList) {
        boolean z = false;
        if (beanList != null && !beanList.isEmpty()) {
            SQLiteDatabase sqlDatabase = null;
            try {
                SQLiteDatabase sqlDatabase2 = this.mDatabaseHelper.getWritableDatabase();
                sqlDatabase2.beginTransaction();
                for (int index = 0; index < beanList.size(); index++) {
                    InstalledPkgBean bean = beanList.get(index);
                    ContentValues values = new ContentValues();
                    values.put("packageName", bean.getPackageName());
                    values.put("updateTime", Long.valueOf(bean.getUpdateTime()));
                    sqlDatabase2.replace(TABLE_NAME, (String) null, values);
                }
                sqlDatabase2.setTransactionSuccessful();
                z = true;
                if (sqlDatabase2 != null && sqlDatabase2.inTransaction()) {
                    try {
                        sqlDatabase2.endTransaction();
                    } catch (Exception e) {
                        LogUtils.e("Ad_SDK", "InstalledPkgTable--sqlDatabase.endTransaction Exception!", e);
                    }
                }
            } catch (Exception e2) {
                LogUtils.e("Ad_SDK", "InstalledPkgTable--insertData Exception!", e2);
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Exception e3) {
                        LogUtils.e("Ad_SDK", "InstalledPkgTable--sqlDatabase.endTransaction Exception!", e3);
                    }
                }
            } catch (Throwable th) {
                if (sqlDatabase != null && sqlDatabase.inTransaction()) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Exception e4) {
                        LogUtils.e("Ad_SDK", "InstalledPkgTable--sqlDatabase.endTransaction Exception!", e4);
                    }
                }
                throw th;
            }
        }
        return z;
    }

    public boolean deleteExpiredData() {
        return deleteDataOutOf(VALID_TIME_MILLS);
    }

    public boolean deleteDataOutOf(long duration) {
        boolean z = true;
        if (duration <= 0) {
            return false;
        }
        if (this.mDatabaseHelper.delete(TABLE_NAME, " updateTime <= ? ", new String[]{String.valueOf(System.currentTimeMillis() - duration)}) <= 0) {
            z = false;
        }
        return z;
    }

    public List<InstalledPkgBean> getValidData() {
        return getDataWithin(VALID_TIME_MILLS);
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0057  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.InstalledPkgBean> getDataWithin(long r16) {
        /*
            r15 = this;
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r9 = 0
            java.lang.String r3 = "updateTime > ?"
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x005b }
            long r12 = r0 - r16
            r0 = 1
            java.lang.String[] r4 = new java.lang.String[r0]     // Catch:{ Exception -> 0x005b }
            r0 = 0
            java.lang.String r1 = java.lang.String.valueOf(r12)     // Catch:{ Exception -> 0x005b }
            r4[r0] = r1     // Catch:{ Exception -> 0x005b }
            com.jiubang.commerce.database.DataBaseHelper r0 = r15.mDatabaseHelper     // Catch:{ Exception -> 0x005b }
            java.lang.String r1 = "InstalledPkg"
            r2 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r9 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x005b }
            if (r9 == 0) goto L_0x0055
            boolean r0 = r9.moveToFirst()     // Catch:{ Exception -> 0x005b }
            if (r0 == 0) goto L_0x0055
            r8 = 0
        L_0x002d:
            com.jiubang.commerce.database.model.InstalledPkgBean r8 = new com.jiubang.commerce.database.model.InstalledPkgBean     // Catch:{ Exception -> 0x005b }
            r8.<init>()     // Catch:{ Exception -> 0x005b }
            java.lang.String r0 = "packageName"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x005b }
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x005b }
            r8.setPackageName(r0)     // Catch:{ Exception -> 0x005b }
            java.lang.String r0 = "updateTime"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x005b }
            long r0 = r9.getLong(r0)     // Catch:{ Exception -> 0x005b }
            r8.setUpdateTime(r0)     // Catch:{ Exception -> 0x005b }
            r11.add(r8)     // Catch:{ Exception -> 0x005b }
            boolean r0 = r9.moveToNext()     // Catch:{ Exception -> 0x005b }
            if (r0 != 0) goto L_0x002d
        L_0x0055:
            if (r9 == 0) goto L_0x005a
            r9.close()
        L_0x005a:
            return r11
        L_0x005b:
            r10 = move-exception
            r10.printStackTrace()     // Catch:{ all -> 0x0065 }
            if (r9 == 0) goto L_0x005a
            r9.close()
            goto L_0x005a
        L_0x0065:
            r0 = move-exception
            if (r9 == 0) goto L_0x006b
            r9.close()
        L_0x006b:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.InstalledPkgTable.getDataWithin(long):java.util.List");
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0047  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.InstalledPkgBean> getAllData() {
        /*
            r12 = this;
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r9 = 0
            com.jiubang.commerce.database.DataBaseHelper r0 = r12.mDatabaseHelper     // Catch:{ Exception -> 0x004b }
            java.lang.String r1 = "InstalledPkg"
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r9 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x004b }
            if (r9 == 0) goto L_0x0045
            boolean r0 = r9.moveToFirst()     // Catch:{ Exception -> 0x004b }
            if (r0 == 0) goto L_0x0045
            r8 = 0
        L_0x001d:
            com.jiubang.commerce.database.model.InstalledPkgBean r8 = new com.jiubang.commerce.database.model.InstalledPkgBean     // Catch:{ Exception -> 0x004b }
            r8.<init>()     // Catch:{ Exception -> 0x004b }
            java.lang.String r0 = "packageName"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x004b }
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x004b }
            r8.setPackageName(r0)     // Catch:{ Exception -> 0x004b }
            java.lang.String r0 = "updateTime"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x004b }
            long r0 = r9.getLong(r0)     // Catch:{ Exception -> 0x004b }
            r8.setUpdateTime(r0)     // Catch:{ Exception -> 0x004b }
            r11.add(r8)     // Catch:{ Exception -> 0x004b }
            boolean r0 = r9.moveToNext()     // Catch:{ Exception -> 0x004b }
            if (r0 != 0) goto L_0x001d
        L_0x0045:
            if (r9 == 0) goto L_0x004a
            r9.close()
        L_0x004a:
            return r11
        L_0x004b:
            r10 = move-exception
            r10.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r9 == 0) goto L_0x004a
            r9.close()
            goto L_0x004a
        L_0x0055:
            r0 = move-exception
            if (r9 == 0) goto L_0x005b
            r9.close()
        L_0x005b:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.InstalledPkgTable.getAllData():java.util.List");
    }
}

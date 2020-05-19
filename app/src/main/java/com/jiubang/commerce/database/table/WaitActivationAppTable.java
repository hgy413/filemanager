package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.WaitActivationAppInfoBean;
import java.util.List;

public class WaitActivationAppTable {
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS WAIT_ACTIVATION_APP (packageName TEXT, installTime TEXT)";
    public static final String INSTALL_TIME = "installTime";
    public static final String PACKAGE_NAME = "packageName";
    public static final String TABLE_NAME = "WAIT_ACTIVATION_APP";
    private static WaitActivationAppTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    public WaitActivationAppTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static synchronized WaitActivationAppTable getInstance(Context context) {
        WaitActivationAppTable waitActivationAppTable;
        synchronized (WaitActivationAppTable.class) {
            if (sInstance == null) {
                sInstance = new WaitActivationAppTable(context);
            }
            waitActivationAppTable = sInstance;
        }
        return waitActivationAppTable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x008d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.WaitActivationAppInfoBean> getWaitActivationAppData(com.jiubang.commerce.database.model.WaitActivationAppInfoBean r15) {
        /*
            r14 = this;
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r8 = 0
            java.lang.StringBuffer r13 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0091 }
            java.lang.String r0 = " 1=1"
            r13.<init>(r0)     // Catch:{ Exception -> 0x0091 }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x0091 }
            r10.<init>()     // Catch:{ Exception -> 0x0091 }
            if (r15 == 0) goto L_0x0040
            java.lang.String r0 = r15.getPackageName()     // Catch:{ Exception -> 0x0091 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0091 }
            if (r0 != 0) goto L_0x002a
            java.lang.String r0 = " AND packageName = ?"
            r13.append(r0)     // Catch:{ Exception -> 0x0091 }
            java.lang.String r0 = r15.getPackageName()     // Catch:{ Exception -> 0x0091 }
            r10.add(r0)     // Catch:{ Exception -> 0x0091 }
        L_0x002a:
            java.lang.Long r0 = r15.getInstallTime()     // Catch:{ Exception -> 0x0091 }
            if (r0 == 0) goto L_0x0040
            java.lang.String r0 = " AND installTime <= ?"
            r13.append(r0)     // Catch:{ Exception -> 0x0091 }
            java.lang.Long r0 = r15.getInstallTime()     // Catch:{ Exception -> 0x0091 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ Exception -> 0x0091 }
            r10.add(r0)     // Catch:{ Exception -> 0x0091 }
        L_0x0040:
            com.jiubang.commerce.database.DataBaseHelper r0 = r14.mDatabaseHelper     // Catch:{ Exception -> 0x0091 }
            java.lang.String r1 = "WAIT_ACTIVATION_APP"
            r2 = 2
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x0091 }
            r3 = 0
            java.lang.String r4 = "packageName"
            r2[r3] = r4     // Catch:{ Exception -> 0x0091 }
            r3 = 1
            java.lang.String r4 = "installTime"
            r2[r3] = r4     // Catch:{ Exception -> 0x0091 }
            java.lang.String r3 = r13.toString()     // Catch:{ Exception -> 0x0091 }
            java.lang.String[] r4 = r14.listConvertStringArray(r10)     // Catch:{ Exception -> 0x0091 }
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r8 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0091 }
            if (r8 == 0) goto L_0x008b
            boolean r0 = r8.moveToFirst()     // Catch:{ Exception -> 0x0091 }
            if (r0 == 0) goto L_0x008b
            r11 = 0
        L_0x0069:
            com.jiubang.commerce.database.model.WaitActivationAppInfoBean r11 = new com.jiubang.commerce.database.model.WaitActivationAppInfoBean     // Catch:{ Exception -> 0x0091 }
            java.lang.String r0 = "packageName"
            int r0 = r8.getColumnIndex(r0)     // Catch:{ Exception -> 0x0091 }
            java.lang.String r0 = r8.getString(r0)     // Catch:{ Exception -> 0x0091 }
            java.lang.String r1 = "installTime"
            int r1 = r8.getColumnIndex(r1)     // Catch:{ Exception -> 0x0091 }
            long r2 = r8.getLong(r1)     // Catch:{ Exception -> 0x0091 }
            r11.<init>(r0, r2)     // Catch:{ Exception -> 0x0091 }
            r12.add(r11)     // Catch:{ Exception -> 0x0091 }
            boolean r0 = r8.moveToNext()     // Catch:{ Exception -> 0x0091 }
            if (r0 != 0) goto L_0x0069
        L_0x008b:
            if (r8 == 0) goto L_0x0090
            r8.close()
        L_0x0090:
            return r12
        L_0x0091:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x009b }
            if (r8 == 0) goto L_0x0090
            r8.close()
            goto L_0x0090
        L_0x009b:
            r0 = move-exception
            if (r8 == 0) goto L_0x00a1
            r8.close()
        L_0x00a1:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.WaitActivationAppTable.getWaitActivationAppData(com.jiubang.commerce.database.model.WaitActivationAppInfoBean):java.util.List");
    }

    public boolean insertWaitActivationAppData(List<WaitActivationAppInfoBean> waitActivationAppInfoList) {
        if (waitActivationAppInfoList == null || waitActivationAppInfoList.isEmpty()) {
            return false;
        }
        SQLiteDatabase sqlDatabase = null;
        try {
            sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
            sqlDatabase.beginTransaction();
            for (int index = 0; index < waitActivationAppInfoList.size(); index++) {
                WaitActivationAppInfoBean waitActivationAppInfoBean = waitActivationAppInfoList.get(index);
                if (!TextUtils.isEmpty(waitActivationAppInfoBean.getPackageName())) {
                    ContentValues values = new ContentValues();
                    values.put("packageName", waitActivationAppInfoBean.getPackageName());
                    values.put(INSTALL_TIME, Long.valueOf(waitActivationAppInfoBean.getInstallTime() == null ? System.currentTimeMillis() : waitActivationAppInfoBean.getInstallTime().longValue()));
                    sqlDatabase.delete(TABLE_NAME, " packageName = ?", new String[]{waitActivationAppInfoBean.getPackageName()});
                    sqlDatabase.insert(TABLE_NAME, (String) null, values);
                }
            }
            sqlDatabase.setTransactionSuccessful();
            if (sqlDatabase == null) {
                return true;
            }
            try {
                sqlDatabase.endTransaction();
                return true;
            } catch (Throwable th) {
                th.printStackTrace();
                return true;
            }
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "insert wait activation app data Exception!", e);
            if (sqlDatabase != null) {
                sqlDatabase.endTransaction();
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return false;
    }

    public boolean deleteInvalidWaitActivationAppData(String packageName) {
        int count;
        if (TextUtils.isEmpty(packageName)) {
            count = this.mDatabaseHelper.delete(TABLE_NAME, (String) null, (String[]) null);
        } else {
            count = this.mDatabaseHelper.delete(TABLE_NAME, " packageName = ?", new String[]{packageName});
        }
        if (count > 0) {
            return true;
        }
        return false;
    }

    public String[] listConvertStringArray(List<String> paramList) {
        if (paramList == null || paramList.size() < 0) {
            return null;
        }
        String[] paramArray = new String[paramList.size()];
        for (int index = 0; index < paramList.size(); index++) {
            paramArray[index] = paramList.get(index);
        }
        return paramArray;
    }
}

package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.AdConfigInfoBean;

public class AdConfigInfoTable {
    public static final String CONFIG_KEY = "configKey";
    public static final String CONFIG_KEY_AD_SOURCE_CONTROL = "AD_SOURCE_CONTROL";
    public static final String CONFIG_VALUE = "configValue";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS AD_CONFIG_INFO (configKey TEXT, configValue TEXT, updateTime NUMERIC)";
    public static final String TABLE_NAME = "AD_CONFIG_INFO";
    public static final String UPDATE_TIME = "updateTime";
    private static AdConfigInfoTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    public AdConfigInfoTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static synchronized AdConfigInfoTable getInstance(Context context) {
        AdConfigInfoTable adConfigInfoTable;
        synchronized (AdConfigInfoTable.class) {
            if (sInstance == null) {
                sInstance = new AdConfigInfoTable(context);
            }
            adConfigInfoTable = sInstance;
        }
        return adConfigInfoTable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.jiubang.commerce.database.model.AdConfigInfoBean getAdConfigInfoData(java.lang.String r13) {
        /*
            r12 = this;
            r8 = 0
            r10 = 0
            com.jiubang.commerce.database.DataBaseHelper r0 = r12.mDatabaseHelper     // Catch:{ Exception -> 0x0062 }
            java.lang.String r1 = "AD_CONFIG_INFO"
            r2 = 3
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x0062 }
            r3 = 0
            java.lang.String r4 = "configKey"
            r2[r3] = r4     // Catch:{ Exception -> 0x0062 }
            r3 = 1
            java.lang.String r4 = "configValue"
            r2[r3] = r4     // Catch:{ Exception -> 0x0062 }
            r3 = 2
            java.lang.String r4 = "updateTime"
            r2[r3] = r4     // Catch:{ Exception -> 0x0062 }
            java.lang.String r3 = " configKey = ?"
            r4 = 1
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ Exception -> 0x0062 }
            r5 = 0
            r4[r5] = r13     // Catch:{ Exception -> 0x0062 }
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r10 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0062 }
            if (r10 == 0) goto L_0x005c
            boolean r0 = r10.moveToFirst()     // Catch:{ Exception -> 0x0062 }
            if (r0 == 0) goto L_0x005c
            com.jiubang.commerce.database.model.AdConfigInfoBean r9 = new com.jiubang.commerce.database.model.AdConfigInfoBean     // Catch:{ Exception -> 0x0062 }
            r9.<init>()     // Catch:{ Exception -> 0x0062 }
            java.lang.String r0 = "configKey"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            r9.setConfigKey(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            java.lang.String r0 = "configValue"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            r9.setConfigValue(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            java.lang.String r0 = "updateTime"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            long r0 = r10.getLong(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            r9.setUpdateTime(r0)     // Catch:{ Exception -> 0x0076, all -> 0x0073 }
            r8 = r9
        L_0x005c:
            if (r10 == 0) goto L_0x0061
            r10.close()
        L_0x0061:
            return r8
        L_0x0062:
            r11 = move-exception
        L_0x0063:
            r11.printStackTrace()     // Catch:{ all -> 0x006c }
            if (r10 == 0) goto L_0x0061
            r10.close()
            goto L_0x0061
        L_0x006c:
            r0 = move-exception
        L_0x006d:
            if (r10 == 0) goto L_0x0072
            r10.close()
        L_0x0072:
            throw r0
        L_0x0073:
            r0 = move-exception
            r8 = r9
            goto L_0x006d
        L_0x0076:
            r11 = move-exception
            r8 = r9
            goto L_0x0063
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.AdConfigInfoTable.getAdConfigInfoData(java.lang.String):com.jiubang.commerce.database.model.AdConfigInfoBean");
    }

    public boolean insertAdConfigInfoData(AdConfigInfoBean adConfigInfoBean) {
        if (adConfigInfoBean == null || TextUtils.isEmpty(adConfigInfoBean.getConfigKey()) || TextUtils.isEmpty(adConfigInfoBean.getConfigValue())) {
            return false;
        }
        SQLiteDatabase sqlDatabase = null;
        try {
            sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
            sqlDatabase.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(CONFIG_KEY, adConfigInfoBean.getConfigKey());
            values.put(CONFIG_VALUE, adConfigInfoBean.getConfigValue());
            values.put("updateTime", Long.valueOf(System.currentTimeMillis()));
            sqlDatabase.delete(TABLE_NAME, " configKey = ?", new String[]{adConfigInfoBean.getConfigKey()});
            sqlDatabase.insert(TABLE_NAME, (String) null, values);
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
            LogUtils.e("Ad_SDK", "insert ad url data Exception!", e);
            if (sqlDatabase != null) {
                sqlDatabase.endTransaction();
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return false;
    }

    public boolean deleteAdConfigInfoData(String adConfigKey) {
        boolean z = true;
        if (TextUtils.isEmpty(adConfigKey)) {
            return false;
        }
        if (this.mDatabaseHelper.delete(TABLE_NAME, " configKey = ?", new String[]{adConfigKey}) <= 0) {
            z = false;
        }
        return z;
    }
}

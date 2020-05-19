package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.AdUrlInfoBean;
import java.util.ArrayList;
import java.util.List;

public class AdUrlTable {
    public static final String AD_URL = "adUrl";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS AD_URL (packageName TEXT, redirectUrl TEXT, adUrl TEXT, updateTime NUMERIC)";
    public static final String PACKAGE_NAME = "packageName";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String TABLE_NAME = "AD_URL";
    public static final String UPDATE_TIME = "updateTime";
    private static AdUrlTable sInstance;
    private DataBaseHelper mDatabaseHelper;

    public AdUrlTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static synchronized AdUrlTable getInstance(Context context) {
        AdUrlTable adUrlTable;
        synchronized (AdUrlTable.class) {
            if (sInstance == null) {
                sInstance = new AdUrlTable(context);
            }
            adUrlTable = sInstance;
        }
        return adUrlTable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.AdUrlInfoBean> getAdUrlData(com.jiubang.commerce.database.model.AdUrlInfoBean r15) {
        /*
            r14 = this;
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 0
            java.lang.StringBuffer r13 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = " 1=1"
            r13.<init>(r0)     // Catch:{ Exception -> 0x00a5 }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ Exception -> 0x00a5 }
            r12.<init>()     // Catch:{ Exception -> 0x00a5 }
            if (r15 == 0) goto L_0x002a
            java.lang.String r0 = r15.getRedirectUrl()     // Catch:{ Exception -> 0x00a5 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x00a5 }
            if (r0 != 0) goto L_0x002a
            java.lang.String r0 = " AND redirectUrl = ?"
            r13.append(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = r15.getRedirectUrl()     // Catch:{ Exception -> 0x00a5 }
            r12.add(r0)     // Catch:{ Exception -> 0x00a5 }
        L_0x002a:
            com.jiubang.commerce.database.DataBaseHelper r0 = r14.mDatabaseHelper     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r1 = "AD_URL"
            r2 = 4
            java.lang.String[] r2 = new java.lang.String[r2]     // Catch:{ Exception -> 0x00a5 }
            r3 = 0
            java.lang.String r4 = "packageName"
            r2[r3] = r4     // Catch:{ Exception -> 0x00a5 }
            r3 = 1
            java.lang.String r4 = "redirectUrl"
            r2[r3] = r4     // Catch:{ Exception -> 0x00a5 }
            r3 = 2
            java.lang.String r4 = "adUrl"
            r2[r3] = r4     // Catch:{ Exception -> 0x00a5 }
            r3 = 3
            java.lang.String r4 = "updateTime"
            r2[r3] = r4     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r3 = r13.toString()     // Catch:{ Exception -> 0x00a5 }
            java.lang.String[] r4 = r14.listConvertStringArray(r12)     // Catch:{ Exception -> 0x00a5 }
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r10 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x00a5 }
            if (r10 == 0) goto L_0x009f
            boolean r0 = r10.moveToFirst()     // Catch:{ Exception -> 0x00a5 }
            if (r0 == 0) goto L_0x009f
            r8 = 0
        L_0x005d:
            com.jiubang.commerce.database.model.AdUrlInfoBean r8 = new com.jiubang.commerce.database.model.AdUrlInfoBean     // Catch:{ Exception -> 0x00a5 }
            r8.<init>()     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = "packageName"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x00a5 }
            r8.setPackageName(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = "redirectUrl"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x00a5 }
            r8.setRedirectUrl(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = "adUrl"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x00a5 }
            r8.setAdUrl(r0)     // Catch:{ Exception -> 0x00a5 }
            java.lang.String r0 = "updateTime"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x00a5 }
            long r0 = r10.getLong(r0)     // Catch:{ Exception -> 0x00a5 }
            r8.setUpdateTime(r0)     // Catch:{ Exception -> 0x00a5 }
            r9.add(r8)     // Catch:{ Exception -> 0x00a5 }
            boolean r0 = r10.moveToNext()     // Catch:{ Exception -> 0x00a5 }
            if (r0 != 0) goto L_0x005d
        L_0x009f:
            if (r10 == 0) goto L_0x00a4
            r10.close()
        L_0x00a4:
            return r9
        L_0x00a5:
            r11 = move-exception
            r11.printStackTrace()     // Catch:{ all -> 0x00af }
            if (r10 == 0) goto L_0x00a4
            r10.close()
            goto L_0x00a4
        L_0x00af:
            r0 = move-exception
            if (r10 == 0) goto L_0x00b5
            r10.close()
        L_0x00b5:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.AdUrlTable.getAdUrlData(com.jiubang.commerce.database.model.AdUrlInfoBean):java.util.List");
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0063  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.jiubang.commerce.database.model.AdUrlInfoBean> getDataLatest() {
        /*
            r12 = this;
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 0
            java.lang.String r6 = "updateTime = MAX(updateTime)"
            com.jiubang.commerce.database.DataBaseHelper r0 = r12.mDatabaseHelper     // Catch:{ Exception -> 0x0067 }
            java.lang.String r1 = "AD_URL"
            r2 = 0
            r3 = 0
            r4 = 0
            java.lang.String r5 = "packageName"
            r7 = 0
            android.database.Cursor r10 = r0.query(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0067 }
            if (r10 == 0) goto L_0x0061
            boolean r0 = r10.moveToFirst()     // Catch:{ Exception -> 0x0067 }
            if (r0 == 0) goto L_0x0061
            r8 = 0
        L_0x001f:
            com.jiubang.commerce.database.model.AdUrlInfoBean r8 = new com.jiubang.commerce.database.model.AdUrlInfoBean     // Catch:{ Exception -> 0x0067 }
            r8.<init>()     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = "packageName"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x0067 }
            r8.setPackageName(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = "redirectUrl"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x0067 }
            r8.setRedirectUrl(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = "adUrl"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x0067 }
            r8.setAdUrl(r0)     // Catch:{ Exception -> 0x0067 }
            java.lang.String r0 = "updateTime"
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x0067 }
            long r0 = r10.getLong(r0)     // Catch:{ Exception -> 0x0067 }
            r8.setUpdateTime(r0)     // Catch:{ Exception -> 0x0067 }
            r9.add(r8)     // Catch:{ Exception -> 0x0067 }
            boolean r0 = r10.moveToNext()     // Catch:{ Exception -> 0x0067 }
            if (r0 != 0) goto L_0x001f
        L_0x0061:
            if (r10 == 0) goto L_0x0066
            r10.close()
        L_0x0066:
            return r9
        L_0x0067:
            r11 = move-exception
            r11.printStackTrace()     // Catch:{ all -> 0x0071 }
            if (r10 == 0) goto L_0x0066
            r10.close()
            goto L_0x0066
        L_0x0071:
            r0 = move-exception
            if (r10 == 0) goto L_0x0077
            r10.close()
        L_0x0077:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jiubang.commerce.database.table.AdUrlTable.getDataLatest():java.util.List");
    }

    public boolean insertAdUrlData(List<AdUrlInfoBean> adUrlInfoList) {
        if (adUrlInfoList == null || adUrlInfoList.isEmpty()) {
            return false;
        }
        SQLiteDatabase sqlDatabase = null;
        try {
            sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
            sqlDatabase.beginTransaction();
            for (int index = 0; index < adUrlInfoList.size(); index++) {
                AdUrlInfoBean adUrlInfoBean = adUrlInfoList.get(index);
                if (!TextUtils.isEmpty(adUrlInfoBean.getAdUrl()) && !TextUtils.isEmpty(adUrlInfoBean.getRedirectUrl())) {
                    ContentValues values = new ContentValues();
                    values.put("packageName", adUrlInfoBean.getPackageName());
                    values.put("redirectUrl", adUrlInfoBean.getRedirectUrl());
                    values.put(AD_URL, adUrlInfoBean.getAdUrl());
                    values.put("updateTime", Long.valueOf(adUrlInfoBean.getUpdateTime()));
                    sqlDatabase.delete(TABLE_NAME, " redirectUrl = ?", new String[]{adUrlInfoBean.getRedirectUrl()});
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
            LogUtils.e("Ad_SDK", "insert ad url data Exception!", e);
            if (sqlDatabase != null) {
                sqlDatabase.endTransaction();
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return false;
    }

    public boolean deleteInvalidAdUrlData(long duration) {
        boolean z = true;
        if (duration <= 0) {
            return false;
        }
        if (this.mDatabaseHelper.delete(TABLE_NAME, " updateTime <= ? OR redirectUrl IS NULL OR adUrl IS NULL", new String[]{String.valueOf(System.currentTimeMillis() - duration)}) <= 0) {
            z = false;
        }
        return z;
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

    public static List<AdUrlInfoBean> getAdUrlInfoList(String packageName, String redirectUrl, String adUrl, long updateTime) {
        List<AdUrlInfoBean> adUrlInfoList = new ArrayList<>();
        AdUrlInfoBean adUrlInfo = new AdUrlInfoBean();
        adUrlInfo.setPackageName(packageName);
        adUrlInfo.setRedirectUrl(redirectUrl);
        adUrlInfo.setAdUrl(adUrl);
        adUrlInfo.setUpdateTime(updateTime);
        adUrlInfoList.add(adUrlInfo);
        return adUrlInfoList;
    }
}

package com.jiubang.commerce.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import com.jiubang.commerce.database.model.AdvertFilterBean;
import java.util.HashMap;
import java.util.Map;

public class AdvertFilterTable {
    public static final String ADVERT_POS = "advertPos";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS AdvertFilter (packageName TEXT, moduleId TEXT, advertPos TEXT, showCount INTEGER, saveTime NUMERIC)";
    public static final long DETELE_INVAILD_TIME = 14400000;
    public static final long INVAILD_TIME = 345600000;
    public static final String MODULE_ID = "moduleId";
    public static final String PACKAGE_NAME = "packageName";
    public static final String SAVE_TIME = "saveTime";
    public static final String SHOW_COUNT = "showCount";
    public static final String TABLE_NAME = "AdvertFilter";
    private static AdvertFilterTable sInstance;
    private DataBaseHelper mDatabaseHelper;
    private Map<String, Long> mLastDeteleInvaildDataTime = null;

    public AdvertFilterTable(Context context) {
        this.mDatabaseHelper = DataBaseHelper.getInstance(context);
    }

    public static synchronized AdvertFilterTable getInstance(Context context) {
        AdvertFilterTable advertFilterTable;
        synchronized (AdvertFilterTable.class) {
            if (sInstance == null) {
                sInstance = new AdvertFilterTable(context);
            }
            advertFilterTable = sInstance;
        }
        return advertFilterTable;
    }

    public boolean insert(AdvertFilterBean bean) {
        boolean z = false;
        if (bean != null) {
            SQLiteDatabase sqlDatabase = null;
            try {
                sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
                sqlDatabase.beginTransaction();
                if (!TextUtils.isEmpty(bean.getmPackageName())) {
                    ContentValues values = new ContentValues();
                    values.put("packageName", bean.getmPackageName());
                    values.put("moduleId", bean.getmMoudleId());
                    values.put(ADVERT_POS, bean.getmAdvertPos());
                    values.put(SHOW_COUNT, Integer.valueOf(bean.getmShowCount()));
                    values.put(SAVE_TIME, Long.valueOf(bean.getmSaveTime()));
                    sqlDatabase.insert(TABLE_NAME, (String) null, values);
                    sqlDatabase.setTransactionSuccessful();
                    z = true;
                    if (sqlDatabase != null) {
                        try {
                            sqlDatabase.endTransaction();
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                } else if (sqlDatabase != null) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Throwable th2) {
                        th2.printStackTrace();
                    }
                }
            } catch (Exception e) {
                LogUtils.e("Ad_SDK", "AdvertFilterTable.insert Exception!", e);
                if (sqlDatabase != null) {
                    sqlDatabase.endTransaction();
                }
            } catch (Throwable th3) {
                th3.printStackTrace();
            }
        }
        return z;
    }

    public boolean update(AdvertFilterBean bean) {
        if (bean == null) {
            return false;
        }
        SQLiteDatabase sqlDatabase = null;
        try {
            sqlDatabase = this.mDatabaseHelper.getWritableDatabase();
            sqlDatabase.beginTransaction();
            if (!TextUtils.isEmpty(bean.getmPackageName())) {
                ContentValues values = new ContentValues();
                values.put("packageName", bean.getmPackageName());
                values.put("moduleId", bean.getmMoudleId());
                values.put(ADVERT_POS, bean.getmAdvertPos());
                values.put(SHOW_COUNT, Integer.valueOf(bean.getmShowCount()));
                values.put(SAVE_TIME, Long.valueOf(bean.getmSaveTime()));
                sqlDatabase.update(TABLE_NAME, values, " packageName =? and moduleId =? ", new String[]{bean.getmPackageName(), bean.getmMoudleId()});
                sqlDatabase.setTransactionSuccessful();
                if (sqlDatabase != null) {
                    try {
                        sqlDatabase.endTransaction();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
                return true;
            } else if (sqlDatabase == null) {
                return false;
            } else {
                try {
                    sqlDatabase.endTransaction();
                    return false;
                } catch (Throwable th2) {
                    th2.printStackTrace();
                    return false;
                }
            }
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "AdvertFilterTable.update Exception!", e);
            if (sqlDatabase == null) {
                return false;
            }
            sqlDatabase.endTransaction();
            return false;
        } catch (Throwable th3) {
            th3.printStackTrace();
            return false;
        }
    }

    public AdvertFilterBean isDataExist(String packageName, String moudleId) {
        if (packageName == null || moudleId == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            Cursor cursor2 = this.mDatabaseHelper.getWritableDatabase().query(TABLE_NAME, (String[]) null, " packageName =? and moduleId =? ", new String[]{packageName, moudleId}, (String) null, (String) null, (String) null);
            if (cursor2 == null || cursor2.getCount() <= 0 || !cursor2.moveToFirst()) {
                if (cursor2 != null) {
                    cursor2.close();
                }
                return null;
            }
            AdvertFilterBean advertFilterBean = new AdvertFilterBean();
            advertFilterBean.setmPackageName(cursor2.getString(cursor2.getColumnIndex("packageName")));
            advertFilterBean.setmMoudleId(cursor2.getString(cursor2.getColumnIndex("moduleId")));
            advertFilterBean.setmAdvertPos(cursor2.getString(cursor2.getColumnIndex(ADVERT_POS)));
            advertFilterBean.setmShowCount(cursor2.getInt(cursor2.getColumnIndex(SHOW_COUNT)));
            advertFilterBean.setmSaveTime(cursor2.getLong(cursor2.getColumnIndex(SAVE_TIME)));
            if (cursor2 == null) {
                return advertFilterBean;
            }
            cursor2.close();
            return advertFilterBean;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "AdvertFilterTable.isDataExist Exception!", e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public String getFilterList(String moudleId, int returnMaxCount) {
        String filterPackages;
        Cursor cursor = null;
        try {
            deleteInvaildData(moudleId);
            cursor = this.mDatabaseHelper.getWritableDatabase().query(TABLE_NAME, (String[]) null, " moduleId =? ", new String[]{moudleId}, (String) null, (String) null, " showCount DESC");
            if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                filterPackages = "";
                if (cursor != null) {
                    cursor.close();
                }
                return filterPackages;
            }
            filterPackages = "";
            int count = 0;
            while (true) {
                filterPackages = filterPackages + (cursor.getString(cursor.getColumnIndex("packageName")) + "|" + String.valueOf(cursor.getInt(cursor.getColumnIndex(SHOW_COUNT))) + ",");
                if (returnMaxCount <= 0 || (count = count + 1) < returnMaxCount) {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return filterPackages;
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "AdvertFilterTable.getFilterList Exception!", e);
            filterPackages = "";
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public void deleteInvaildData(String moudleId) {
        String key = moudleId;
        long now = System.currentTimeMillis();
        if (this.mLastDeteleInvaildDataTime == null || !this.mLastDeteleInvaildDataTime.containsKey(key) || now - this.mLastDeteleInvaildDataTime.get(key).longValue() >= 14400000) {
            try {
                this.mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME, " moduleId =? and saveTime <=?", new String[]{moudleId, String.valueOf(System.currentTimeMillis() - INVAILD_TIME)});
                if (this.mLastDeteleInvaildDataTime == null) {
                    this.mLastDeteleInvaildDataTime = new HashMap();
                }
                this.mLastDeteleInvaildDataTime.put(key, Long.valueOf(now));
            } catch (Exception e) {
                LogUtils.e("Ad_SDK", "AdvertFilterTable.deleteInvaildData Exception!", e);
            }
        }
    }

    public void deleteAllData() {
        try {
            this.mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME, (String) null, (String[]) null);
        } catch (Exception e) {
            LogUtils.e("Ad_SDK", "AdvertFilterTable.deleteAllData Exception!", e);
        }
    }
}

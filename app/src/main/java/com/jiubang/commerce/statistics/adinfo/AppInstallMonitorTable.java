package com.jiubang.commerce.statistics.adinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.jb.ga0.commerce.util.LogUtils;
import com.jiubang.commerce.database.DataBaseHelper;
import java.util.ArrayList;

public class AppInstallMonitorTable {
    public static final String CREATETABLESQL = "create table if not exists app_install_monitor (pkg_name text PRIMARY KEY, info text )";
    private static final String KEY = "pkg_name";
    public static final String TABLENAME = "app_install_monitor";
    private static final String VALUE = "info";
    DataBaseHelper mDbHelper;

    public AppInstallMonitorTable(DataBaseHelper dbHelper) {
        this.mDbHelper = dbHelper;
    }

    public AppInstallMonitorTable(Context context) {
        this.mDbHelper = DataBaseHelper.getInstance(context.getApplicationContext());
    }

    public boolean put(String key, String value) {
        LogUtils.i("hzw", "put " + key + ":" + value);
        Cursor cursor = null;
        try {
            cursor = this.mDbHelper.query(TABLENAME, (String[]) null, "pkg_name=?", new String[]{key}, (String) null, (String) null, (String) null);
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY, key);
            contentValues.put(VALUE, value);
            if (cursor == null || cursor.getCount() <= 0) {
                this.mDbHelper.insert(TABLENAME, contentValues);
            } else {
                this.mDbHelper.update(TABLENAME, contentValues, "pkg_name=?", new String[]{key});
            }
            if (cursor != null) {
                cursor.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public String get(String key) {
        Cursor cursor = this.mDbHelper.query(TABLENAME, (String[]) null, "pkg_name=?", new String[]{key}, (String) null, (String) null, (String) null);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String value = cursor.getString(cursor.getColumnIndex(VALUE));
                    cursor.close();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void remove(String key) {
        try {
            this.mDbHelper.delete(TABLENAME, "pkg_name=?", new String[]{key});
            LogUtils.i("hzw", "remove " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getAll() {
        Cursor cursor = this.mDbHelper.query(TABLENAME, (String[]) null, "pkg_name!='1'", (String[]) null, (String) null, (String) null, (String) null);
        ArrayList<String[]> list = new ArrayList<>();
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        list.add(new String[]{cursor.getString(cursor.getColumnIndex(KEY)), cursor.getString(cursor.getColumnIndex(VALUE))});
                    } while (cursor.moveToNext());
                    if (cursor == null) {
                        return list;
                    }
                    cursor.close();
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (cursor == null) {
                    return list;
                }
                cursor.close();
                return list;
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}

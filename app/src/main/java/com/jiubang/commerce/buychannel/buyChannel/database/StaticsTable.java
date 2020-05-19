package com.jiubang.commerce.buychannel.buyChannel.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.jb.ga0.commerce.util.io.DataBaseHelper;
import com.jb.ga0.commerce.util.io.DatabaseException;
import java.util.ArrayList;
import java.util.List;

public class StaticsTable {
    public static final String CREATE_STATICS_TABLE = "CREATE TABLE IF NOT EXISTS buychannel_45_table (statics45 TEXT NOT NULL)";
    public static final String STATICS45 = "statics45";
    public static final String TABLE_NAME = "buychannel_45_table";

    public static void insert(DataBaseHelper dataBaseHelper, String buffer45) {
        if (dataBaseHelper != null && buffer45 != null) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(STATICS45, buffer45);
                dataBaseHelper.insert(TABLE_NAME, contentValues);
                Log.i("buychannelsdk", "[StaticsTable::insert]:" + buffer45);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> queryAll(DataBaseHelper dataBaseHelper) {
        if (dataBaseHelper == null) {
            return null;
        }
        List<String> statics45List = new ArrayList<>();
        StringBuffer whereBuffer = new StringBuffer(" 1=1");
        DataBaseHelper dataBaseHelper2 = dataBaseHelper;
        Cursor cursor = dataBaseHelper2.query(TABLE_NAME, new String[]{STATICS45}, whereBuffer.toString(), (String[]) null, (String) null, (String) null, (String) null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        statics45List.add(cursor.getString(cursor.getColumnIndex(STATICS45)));
                        Log.i("buychannelsdk", "[StaticsTable::queryAll] ");
                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        if (cursor != null) {
            cursor.close();
        }
        return statics45List;
    }

    public static void deleteAll(DataBaseHelper dataBaseHelper) {
        if (dataBaseHelper != null) {
            try {
                int count = dataBaseHelper.delete(TABLE_NAME, STATICS45, (String[]) null);
                Log.i("buychannelsdk", "[StaticsTable::deleteAll] ");
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.table.RateTriggeringFactorTable;
import com.jb.filemanager.util.Logger;

/**
 * Created by nieyh on 17-7-19.
 */

public class RateTriggeringFactorProvider extends BaseDataProvider {

    public final static String TAG = "RateTriggeringFactorProvider";

    public RateTriggeringFactorProvider(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    /**
     * 是否触发次数有大于某次的
     * @param times 次数
     * @param type 类型数组
     * */
    public boolean isTriggerCounterOverSomeTimes(int times, Integer... type) {
        synchronized (TAG) {
            boolean isReach = false;
            if (type == null) {
                return false;
            }
            String sql;
            if (type.length == 1) {
                sql = "SELECT " + RateTriggeringFactorTable.FACTOR_TYPE + " FROM " + RateTriggeringFactorTable.TABLE_NAME + " WHERE " + RateTriggeringFactorTable.TRIGGER_COUNTER + " >= " + times
                        + " AND " + RateTriggeringFactorTable.FACTOR_TYPE + " = " + type[0];
            } else {
                //当时多个类型的时候
                sql = "SELECT " + RateTriggeringFactorTable.FACTOR_TYPE + " FROM " + RateTriggeringFactorTable.TABLE_NAME + " WHERE " + RateTriggeringFactorTable.TRIGGER_COUNTER + " >= " + times
                        + " AND " + RateTriggeringFactorTable.FACTOR_TYPE + " IN (" + TextUtils.join(",", type) + ")";
            }
            Logger.w(TAG, sql);
            Cursor cursor = mDBHelper.rawQuery(sql, null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        isReach = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
            return isReach;
        }
    }

    /**
     * 更新触发因素数据
     * @param type 类型 和这个有关{@link com.jb.filemanager.function.rate.RateManager.FactorType}
     * */
    public void updateTrigger(int type) {
        synchronized (TAG) {
            long date = System.currentTimeMillis();
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(RateTriggeringFactorTable.LAST_TRIGGER_TIME, date);
                contentValues.put(RateTriggeringFactorTable.TRIGGER_COUNTER, RateTriggeringFactorTable.TRIGGER_COUNTER + " + 1");
                if (db.update(RateTriggeringFactorTable.TABLE_NAME,
                        contentValues,
                        RateTriggeringFactorTable.FACTOR_TYPE + " =? ",
                        new String[]{String.valueOf(type)}) == 0) {
                    Logger.w(TAG, "update failure");
                    //当更新失败时 则直接插入数据
                    contentValues.remove(RateTriggeringFactorTable.TRIGGER_COUNTER);
                    contentValues.put(RateTriggeringFactorTable.FACTOR_TYPE, type);
                    db.insert(RateTriggeringFactorTable.TABLE_NAME, null, contentValues);
                } else {
                    Logger.w(TAG, "update success");
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 获取上一次因素触发时间
     * @param type 类型 和这个有关{@link com.jb.filemanager.function.rate.RateManager.FactorType}
     * */
    public long getLastTriggerDate(int type) {
        synchronized (TAG) {
            long lastTriggerDate = 0;
            Cursor cursor = mDBHelper.rawQuery("SELECT " + RateTriggeringFactorTable.LAST_TRIGGER_TIME + " FROM " + RateTriggeringFactorTable.TABLE_NAME
                    + " WHERE " + RateTriggeringFactorTable.FACTOR_TYPE + " = " + type, null);
            if (cursor != null) {
                try {
                    Logger.w(TAG, "cursor != null");
                    while (cursor.moveToNext()) {
                        Logger.w(TAG, "cursor.moveToNext");
                        lastTriggerDate = cursor.getLong(0);

                        Logger.w(TAG, "lastTriggerDate >> " + lastTriggerDate);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
            return lastTriggerDate;
        }
    }
}

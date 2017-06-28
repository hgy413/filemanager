package com.jb.filemanager.function.applock.database;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.params.DeleteParams;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.provider.BaseDataProvider;
import com.jb.filemanager.database.table.LockerTable;
import com.jb.filemanager.function.applock.model.bean.LockerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 帮助获取数据库信息
 *
 * @author zhanghuijun
 */
public class LockerDatabaseHelper extends BaseDataProvider {

    public static final String TAG = "LockerDatabaseHelper";

    private static LockerDatabaseHelper sInstance;

    private LockerDatabaseHelper(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    public static LockerDatabaseHelper getInstance() {
        if (sInstance == null) {
            sInstance = new LockerDatabaseHelper(TheApplication.getAppContext());
        }
        return sInstance;
    }

    /**
     * 查询当前加锁应用信息
     *
     * @return
     */
    public List<ComponentName> queryLockerInfo() {
        List<ComponentName> list = new ArrayList<ComponentName>();
        Cursor cursor = mDBHelper.query(LockerTable.TABLE_NAME, null, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    String component = cursor.getString(cursor.getColumnIndex(LockerTable.COMPONENTNAME));
                    ComponentName componentName;
                    try {
                        componentName = ComponentName.unflattenFromString(component);
                    } catch (NoSuchMethodError e) {     //诡异的问题
                        componentName = unflattenFromString(component);
                    }
                    if (null != componentName) {
                        list.add(componentName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return list.isEmpty() ? null : list;
    }

    /**
     * 是否存在锁定的App
     * */
    public boolean isHaveLockerApp() {
        String sql = "select count(*) from " + LockerTable.TABLE_NAME;
        int count = 0;
        Cursor cursor = mDBHelper.rawQuery(sql, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return count > 0;
    }

    private static ComponentName unflattenFromString(String str) {
        int sep = str.indexOf('/');
        if (sep < 0 || (sep + 1) >= str.length()) {
            return null;
        }
        String pkg = str.substring(0, sep);
        String cls = str.substring(sep + 1);
        if (cls.length() > 0 && cls.charAt(0) == '.') {
            cls = pkg + cls;
        }
        return new ComponentName(pkg, cls);
    }

    /**
     * 检查是否存在该内容
     */
    private boolean checkExist(String sql) {
        Cursor cursor = mDBHelper.rawQuery(sql, null);
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public void lockItem(LockerItem... lockerItems) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        for (LockerItem lockerItem : lockerItems) {
            if (!checkExist("select * from " + LockerTable.TABLE_NAME + " where " + LockerTable.COMPONENTNAME + "='" + lockerItem.componentName.flattenToString() + "'")) {
                ContentValues values = new ContentValues();
                lockerItem.writeObject(values, LockerTable.TABLE_NAME);
                InsertParams insert = new InsertParams(LockerTable.TABLE_NAME, values);
                list.add(insert);
            }
        }

        if (!list.isEmpty()) {
            try {
                mDBHelper.insert(list);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }


    public void unlockItem(LockerItem... lockerItems) {
        ArrayList<DeleteParams> list = new ArrayList<DeleteParams>();
        for (LockerItem lockerItem : lockerItems) {
            DeleteParams delete = new DeleteParams(LockerTable.TABLE_NAME, LockerTable.COMPONENTNAME + "=?",
                    new String[]{lockerItem.componentName.flattenToString()});
            list.add(delete);
        }
        if (!list.isEmpty()) {
            try {
                mDBHelper.delete(list);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }


    public void unlockItem(String pkgName) {
        try {
            mDBHelper.exec("delete from " + LockerTable.TABLE_NAME + " where " + LockerTable.COMPONENTNAME + " like '%" + pkgName + "/%'");
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }
}

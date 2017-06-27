package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.database.BaseDatabaseHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.params.DeleteParams;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.params.UpdateParams;

import java.util.List;

/**
 * 基础数据库操作类(所有需要操作数据库的接口需要实现该类)
 *
 * 类名称：BaseDataProvider
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年10月14日 下午4:10:36
 * 修改备注：
 * @version 1.0.0
 *
 */
public class BaseDataProvider {

    public static final String SQL_MATH_MAX = "max(%s) as %s ";

    public static final String SQL_MIN_MAX = "min(%s) as %s ";

    protected final Object mLock;

    protected BaseDatabaseHelper mDBHelper;

    public BaseDataProvider(Context context) {
        mLock = new Object();
    }

    public boolean isNewDB() {
        // 装载默认配置信息
        return mDBHelper.isNewDB();
    }

    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (mLock) {
            return mDBHelper.query(table, projection, selection, selectionArgs, sortOrder);
        }
    }

    public int delete(String tableName, String selection, String[] selectionArgs) {
        synchronized (mLock) {
            try {
                return mDBHelper.delete(tableName, selection, selectionArgs);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public boolean delete(List<DeleteParams> list) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                isSuccess = mDBHelper.delete(list);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    /**
     * 插入一条数据
     * insert(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @param tableName
     *          table name
     * @param initialValues
     *          initial values
     * @return
     *          boolean
     * @since  1.0.0
     */
    public boolean insert(String tableName, ContentValues initialValues) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                mDBHelper.insert(tableName, initialValues);
                isSuccess = true;
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    /**
     * 插入多条数据
     * insert(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @return
     *          boolean
     * @since  1.0.0
     */
    public boolean insert(InsertParams... instertParams) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                mDBHelper.insert(instertParams);
                isSuccess = true;
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    public boolean insert(List<InsertParams> list) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                mDBHelper.insert(list);
                isSuccess = true;
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    /**
     * 更新
     * update(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @param tableName
     *          table name
     * @param values
     *          values
     * @param selection
     *          selection
     * @return
     *          boolean
     * @since  1.0.0
     */
    public boolean update(String tableName, ContentValues values, String selection) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                int count = mDBHelper.update(tableName, values, selection, null);
                if (count > 0) {
                    isSuccess = true;
                }
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    /**
     * 更新
     * update(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @param tableName
     *          table name
     * @param values
     *          values
     * @param selection
     *          selection
     * @return
     *          boolean
     * @since  1.0.0
     */
    public boolean update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                int count = mDBHelper.update(tableName, values, selection, selectionArgs);
                if (count > 0) {
                    isSuccess = true;
                }
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    public boolean update(List<UpdateParams> list) {
        synchronized (mLock) {
            boolean isSuccess = false;
            try {
                isSuccess = mDBHelper.update(list);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }

    public boolean exec(Object... sqls) {
        synchronized (mLock) {
            try {
                return mDBHelper.exec(sqls);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean exec(List<Object> sqls) {
        synchronized (mLock) {
            try {
                return mDBHelper.exec(sqls);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        synchronized (mLock) {
            return mDBHelper.rawQuery(sql, selectionArgs);
        }

    }

    public Cursor rawQuery(String sql) {
        return rawQuery(sql, null);
    }

    /**
     * 通过表名查询所有信息
     * queryAllByTableName(这里用一句话描述这个方法的作用)
     * (这里描述这个方法适用条件 – 可选)
     * @param tableName
     *          table name
     * @return
     *          Cursor
     * @since  1.0.0
     */
    public Cursor queryAllByTableName(String tableName) {
        synchronized (mLock) {
            return mDBHelper.query(tableName, null, null, null, null);
        }
    }

}
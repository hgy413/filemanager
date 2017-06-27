package com.jb.filemanager.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jb.filemanager.database.params.DeleteParams;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.params.UpdateParams;

import java.util.List;

/**
 * 数据库工具类(封装共有方法及操作)
 * <p>
 * 类名称：DatabaseUtils
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年8月25日 上午10:51:17
 * 修改备注：
 *
 * @version 1.0.0
 */
public class DatabaseUtils {

    public final static String TYPE_BLOB = "blob";

    public final static String TYPE_TEXT = "text";

    public final static String TYPE_NUMERIC = "numeric";

    /**
     * 检查指定的表是否存在
     *
     * @param db        db
     * @param tableName 表名
     * @return result
     */
    public static boolean isExistTable(final SQLiteDatabase db, String tableName) {
        boolean result = false;
        Cursor cursor = null;
        String where = "type='table' and name='" + tableName + "'";
        try {
            cursor = db.query("sqlite_master", null, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 检查表中是否存在该字段
     *
     * @param db         db
     * @param tableName  表名
     * @param columnName 字段名
     * @return result
     */
    public static boolean isExistColumnInTable(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            // 查询列数
            String columns[] = {columnName};
            cursor = db.query(tableName, columns, null, null, null, null, null);
            if (cursor != null && cursor.getColumnIndex(columnName) >= 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 新添加字段到表中
     *
     * @param db           db
     * @param tableName    修改表名
     * @param columnName   新增字段名
     * @param columnType   新增字段类型(INTEGER / REAL / TEXT / BLOB)
     * @param defaultValue 新增字段默认值。为null，则不提供默认值
     */
    public static void addColumnToTable(SQLiteDatabase db, String tableName, String columnName, String columnType, String defaultValue) {
        if (!isExistColumnInTable(db, tableName, columnName)) {
            db.beginTransaction();
            try {
                // 增加字段
                String updateSql = "ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType;
                db.execSQL(updateSql);
                // 提供默认值
                if (defaultValue != null) {
                    if (columnType.equals(TYPE_TEXT)) {
                        // 如果是字符串类型，则需加单引号
                        defaultValue = "'" + defaultValue + "'";
                    }
                    updateSql = "update " + tableName + " set " + columnName + " = " + defaultValue;
                    db.execSQL(updateSql);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    db.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static long insert(SQLiteOpenHelper sqLiteOpenHelper, String tableName, ContentValues initialValues) throws DatabaseException {
        long rowId;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            rowId = db.insert(tableName, null, initialValues);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return rowId;
    }

    public static boolean insert(SQLiteOpenHelper sqLiteOpenHelper, InsertParams... instertParams) throws DatabaseException {
        boolean isSuccess = false;
        if (null != instertParams && instertParams.length > 0) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (InsertParams instertParam : instertParams) {
                    db.insert(instertParam.getTableName(), null, instertParam.getContentValues());
                }
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    public static boolean insert(SQLiteOpenHelper sqLiteOpenHelper, List<InsertParams> list) throws DatabaseException {
        boolean isSuccess = false;
        if (null != list && !list.isEmpty()) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (InsertParams instertParam : list) {
                    db.insert(instertParam.getTableName(), null, instertParam.getContentValues());
                }
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    public static int delete(SQLiteOpenHelper sqLiteOpenHelper, String tableName, String selection, String[] selectionArgs) throws DatabaseException {
        int count;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            count = db.delete(tableName, selection, selectionArgs);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return count;
    }

    /**
     * 批量删除
     *
     * @param sqLiteOpenHelper helper
     * @param list             list
     * @return result
     * @throws DatabaseException
     */
    public static boolean delete(SQLiteOpenHelper sqLiteOpenHelper, List<DeleteParams> list) throws DatabaseException {
        boolean isSucces = false;
        if (null != list && !list.isEmpty()) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (DeleteParams deleteParams : list) {
                    db.delete(deleteParams.getTableName(), deleteParams.getSelection(), deleteParams.getWhereArgs());
                }
                db.setTransactionSuccessful();
                isSucces = true;
            } catch (Exception e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSucces;
    }

    public static int update(SQLiteOpenHelper sqLiteOpenHelper, String tableName, ContentValues values, String selection, String[] selectionArgs) throws DatabaseException {
        int count;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            count = db.update(tableName, values, selection, selectionArgs);
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return count;
    }

    public static boolean update(SQLiteOpenHelper sqLiteOpenHelper, UpdateParams updatePamas) throws DatabaseException {
        int count = 0;
        if (updatePamas != null) {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            count = db.update(updatePamas.getTableName(), updatePamas.getContentValues(), updatePamas.getSelection(), updatePamas.getWhereArgs());
        }
        return count != 0;
    }

    public static boolean update(SQLiteOpenHelper sqLiteOpenHelper, List<UpdateParams> list) throws DatabaseException {
        boolean isSucces = false;
        if (null != list && !list.isEmpty()) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (UpdateParams updatePamas : list) {
                    db.update(updatePamas.getTableName(), updatePamas.getContentValues(), updatePamas.getSelection(), updatePamas.getWhereArgs());
                }
                db.setTransactionSuccessful();
                isSucces = true;
            } catch (Exception e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSucces;
    }

    public static void exec(SQLiteOpenHelper sqLiteOpenHelper, String sql) throws DatabaseException {
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            db.execSQL(sql);
        } catch (SQLException e) {
            //Log.i("data", "Exception when exec " + sql);
            throw new DatabaseException(e);
        }
    }

    public static boolean exec(SQLiteOpenHelper sqLiteOpenHelper, Object... sqls) throws DatabaseException {
        boolean isSuccess = false;
        if (null != sqls && sqls.length > 0) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (Object sql : sqls) {
                    if (sql instanceof String) {
                        db.execSQL(sql.toString());
                    } else if (sql instanceof InsertParams) {
                        InsertParams insertParams = (InsertParams) sql;
                        db.insert(insertParams.getTableName(), null, insertParams.getContentValues());
                    }
                }
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    public static boolean exec(SQLiteOpenHelper sqLiteOpenHelper, List<Object> sqls) throws DatabaseException {
        boolean isSuccess = false;
        if (null != sqls && !sqls.isEmpty()) {
            SQLiteDatabase db = null;
            try {
                db = sqLiteOpenHelper.getWritableDatabase();
                db.beginTransaction();
                for (Object sql : sqls) {
                    if (sql instanceof InsertParams) {
                        InsertParams insertParams = (InsertParams) sql;
                        db.insert(insertParams.getTableName(), null, insertParams.getContentValues());
                    } else if (sql instanceof String) {
                        db.execSQL(sql.toString());
                    }
                }
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (SQLException e) {
                throw new DatabaseException(e);
            } finally {
                if (null != db) {
                    try {
                        db.endTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    public static Cursor rawQuery(SQLiteOpenHelper sqLiteOpenHelper, String sql, String[] selectionArgs) {
        Cursor result = null;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
            result = db.rawQuery(sql, selectionArgs);
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 用于单表查询
     */
    public static Cursor query(SQLiteOpenHelper sqLiteOpenHelper, String tableName, String[] projection, String selection, String[] selectionArgs,
                               String groupBy, String having, String sortOrder) {
        Cursor result = null;
        try {
            SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
            result = db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder);
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void change(byte[] src, byte[] scr) {
        if (src == null) {
            return;
        }
        int l = src.length - 1;
        byte t;
        for (int i = 0; i < l / 2; i++) {
            t = src[i];
            src[i] = src[l - i];
            src[l - i] = t;
        }
        change(scr, null);
    }

}
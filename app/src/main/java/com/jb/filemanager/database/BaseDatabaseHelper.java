package com.jb.filemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jb.filemanager.database.params.DeleteParams;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.params.UpdateParams;

import java.util.List;

/**
 * 基础数据库实现类(所有数据库都需要继承该类)
 *
 * 类名称：BaseDatabaseHelper 类描述： 创建人：makai 修改人：makai 修改时间：2014年10月14日 下午4:06:45
 * 修改备注：
 *
 * @version 1.0.0
 *
 */
public abstract class BaseDatabaseHelper extends SQLiteOpenHelper {

    /**
     * 数据库最小版本号(第一个发布的版本号)
     */
    public final static int DB_MIN_VERSION = 1;

    /**
     * 是否升级成功
     */
    protected boolean mIsUpgrade = true;

    protected boolean mIsNewDB = false;

    public BaseDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public boolean isNewDB() {
        return mIsNewDB;
    }

    public boolean insert(InsertParams... insertParams)
            throws DatabaseException {
        return DatabaseUtils.insert(this, insertParams);
    }

    public boolean insert(List<InsertParams> list) throws DatabaseException {
        return DatabaseUtils.insert(this, list);
    }

    /**
     * 插入 insert(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param tableName         table name
     * @param initialValues     initial values
     * @return                  result
     * @throws DatabaseException
     *             long
     * @since 1.0.0
     */
    public long insert(String tableName, ContentValues initialValues)
            throws DatabaseException {
        return DatabaseUtils.insert(this, tableName, initialValues);
    }

    /**
     * 删除 delete(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param tableName         table name
     * @param selection         selection
     * @param selectionArgs     args
     * @return result
     * @throws DatabaseException
     *             int
     * @since 1.0.0
     */
    public int delete(String tableName, String selection, String[] selectionArgs)
            throws DatabaseException {
        return DatabaseUtils.delete(this, tableName, selection, selectionArgs);
    }

    public boolean delete(List<DeleteParams> list)
            throws DatabaseException {
        return DatabaseUtils.delete(this, list);
    }

    /**
     * 更新 update(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param tableName             table name
     * @param values                values
     * @param selection             selection
     * @param selectionArgs         args
     * @return result
     * @throws DatabaseException
     *             int
     * @since 1.0.0
     */
    public int update(String tableName, ContentValues values, String selection,
                      String[] selectionArgs) throws DatabaseException {
        return DatabaseUtils.update(this, tableName, values, selection,
                selectionArgs);
    }

    /**
     * 更新 update(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param list      list
     * @return result
     * @throws DatabaseException
     *             boolean
     * @since 1.0.0
     */
    public boolean update(List<UpdateParams> list) throws DatabaseException {
        return DatabaseUtils.update(this, list);
    }

    /**
     * 更新 update(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param updateParams
     * @return result
     * @throws DatabaseException
     *             boolean
     * @since 1.0.0
     */
    public boolean update(UpdateParams updateParams) throws DatabaseException {
        return DatabaseUtils.update(this, updateParams);
    }

    /**
     *
     * exec(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param sql       sql
     * @throws DatabaseException
     *             void
     * @since 1.0.0
     */
    public void exec(String sql) throws DatabaseException {
        DatabaseUtils.exec(this, sql);
    }

    public boolean exec(Object... sqls) throws DatabaseException {
        return DatabaseUtils.exec(this, sqls);
    }

    public boolean exec(List<Object> sqls) throws DatabaseException {
        return DatabaseUtils.exec(this, sqls);
    }

    /**
     * 通过SQL查询 rawQuery(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param sql               sql
     * @param selectionArgs     args
     * @return Cursor
     * @since 1.0.0
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return DatabaseUtils.rawQuery(this, sql, selectionArgs);
    }

    /**
     * 查询 query(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param tableName         table name
     * @param projection        projection
     * @param selection         selection
     * @param selectionArgs     args
     * @param sortOrder         order
     * @return Cursor
     * @since 1.0.0
     */
    public Cursor query(String tableName, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        return query(tableName, projection, selection, selectionArgs, null,
                null, sortOrder);
    }

    /**
     * 查询 query(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param tableName         table name
     * @param projection        projection
     * @param selection         selection
     * @param selectionArgs     args
     * @param groupBy           group by
     * @param having            having
     * @param sortOrder         order
     * @return Cursor
     * @since 1.0.0
     */
    public Cursor query(String tableName, String[] projection,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String sortOrder) {
        return DatabaseUtils.query(this, tableName, projection, selection,
                selectionArgs, groupBy, having, sortOrder);
    }

    /**
     * 数据升级基础类(所有的数据库升级必须继承该类)
     *
     * 类名称：DatabaseUpgrade 类描述：数据库升级抽象类 创建人：makai 修改人：makai 修改时间：2014年8月20日
     * 上午11:35:40 修改备注：
     *
     * @version 1.0.0
     *
     */
    static public abstract class AbstractDatabaseUpgrade {

        protected Context mContext;

        public AbstractDatabaseUpgrade(Context context) {
            mContext = context;
        }

        /**
         * 升级
         *
         * @param db        db
         */
        public abstract boolean upgradeDb(SQLiteDatabase db, int oldVersion,
                                          int newVersion);

        /**
         * 检查指定的表是否存在
         *
         * @param db        db
         * @param tableName
         *            表名
         * @return result
         */
        public boolean isExistTable(final SQLiteDatabase db, String tableName) {
            return DatabaseUtils.isExistTable(db, tableName);
        }

        /**
         * 检查表中是否存在该字段
         *
         * @param db
         *            db
         * @param tableName
         *            表名
         * @param columnName
         *            字段名
         * @return
         *            result
         */
        public boolean isExistColumnInTable(SQLiteDatabase db,
                                            String tableName, String columnName) {
            return DatabaseUtils
                    .isExistColumnInTable(db, tableName, columnName);
        }

        /**
         * 新添加字段到表中
         *
         * @param db
         *            db
         * @param tableName
         *            修改表名
         * @param columnName
         *            新增字段名
         * @param columnType
         *            新增字段类型
         * @param defaultValue
         *            新增字段默认值。为null，则不提供默认值
         */
        public void addColumnToTable(SQLiteDatabase db, String tableName,
                                     String columnName, String columnType, String defaultValue) {
            DatabaseUtils.addColumnToTable(db, tableName, columnName,
                    columnType, defaultValue);
        }
    }

}
package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.params.UpdateParams;
import com.jb.filemanager.database.table.AppPermissionsTable;

import java.util.List;

/**
 * Created by nieyh on 2017/2/8. 获取应用安装的权限
 */

public class AppPermissionsProvider extends BaseDataProvider {

    private final int TRUE = 1;
    private final int FALSE = -1;

    public AppPermissionsProvider(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    /**
     * 是否数据为空
     */
    public synchronized boolean isTableEmpty() {
        boolean result = true;
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(AppPermissionsTable.TABLE_NAME, null, null, null, null);
            result = !(cursor != null && cursor.moveToFirst());
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
     * 是否已经更新过 （代表是否第一次安装）
     *
     * @param pkgName 应用包名
     */
    public synchronized boolean hasUpdateBefore(String pkgName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(AppPermissionsTable.TABLE_NAME,
                    new String[]{AppPermissionsTable.IS_FIRST_UPDATE}, AppPermissionsTable.PKG_NAME + "=?",
                    new String[]{pkgName}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int flag = cursor.getInt(cursor.getColumnIndex(AppPermissionsTable.IS_FIRST_UPDATE));
                result = (flag == TRUE);
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
     * 是否已经显示过 （代表此应用退出2秒后显示权限对话框是否展示了）
     *
     * @param pkgName 应用包名
     */
    public synchronized boolean hasExitShowBefore(String pkgName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(AppPermissionsTable.TABLE_NAME,
                    new String[]{AppPermissionsTable.HAS_APP_EXIT_SHOWED}, AppPermissionsTable.PKG_NAME + "=?",
                    new String[]{pkgName}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int flag = cursor.getInt(cursor.getColumnIndex(AppPermissionsTable.HAS_APP_EXIT_SHOWED));
                result = (flag == TRUE);
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
     * 获取权限列表
     *
     * @param pkgName 应用包名
     */
    public synchronized String[] getPermissions(String pkgName) {
        String[] result = null;
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(AppPermissionsTable.TABLE_NAME, null, AppPermissionsTable.PKG_NAME + "=?",
                    new String[]{pkgName}, null);
            if (cursor != null && cursor.moveToFirst()) {
                String permission = cursor.getString(cursor.getColumnIndex(AppPermissionsTable.PERMISSIONS));
                result = permission.split(";");
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
     * 是否之前保存过记录
     *
     * @param pkgName 应用包名
     */
    public synchronized boolean isRecordExisted(String pkgName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(AppPermissionsTable.TABLE_NAME, null, AppPermissionsTable.PKG_NAME + "=?",
                    new String[]{pkgName}, null);
            if (cursor != null && cursor.moveToFirst()) {
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
     * 更新权限列表
     *
     * @param pkgName     包名
     * @param permissions 权限列表
     */
    public synchronized int update(String pkgName, String[] permissions) {
        if (!isRecordExisted(pkgName)) {
            return (int) insert(pkgName, permissions, true);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(AppPermissionsTable.PKG_NAME, pkgName);
            cv.put(AppPermissionsTable.PERMISSIONS, getPermissionString(permissions));
            cv.put(AppPermissionsTable.IS_FIRST_UPDATE, TRUE);

            int count = 0;
            try {
                count = mDBHelper.update(AppPermissionsTable.TABLE_NAME, cv, AppPermissionsTable.PKG_NAME + "=?",
                        new String[]{pkgName});
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return count;
        }
    }

    /**
     * 插入数据
     *
     * @param permissions    权限
     * @param pkgName        包名
     * @param hasFirstUpdate 是否已经第一次更新
     */
    public synchronized long insert(String pkgName, String[] permissions, boolean hasFirstUpdate) {
        if (permissions == null || permissions.length == 0) {
            return -1;
        }

        ContentValues cv = getInsertCV(pkgName, permissions, hasFirstUpdate, false);
        long count = 0;
        try {
            count = mDBHelper.insert(AppPermissionsTable.TABLE_NAME, cv);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 更新应用退出时弹框 是否展示
     *
     * @param pkgName          包名
     * @param hasAppExitShowed 是否展示过
     */
    public synchronized int update(String pkgName, boolean hasAppExitShowed) {
        if (!isRecordExisted(pkgName)) {
            return -1;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(AppPermissionsTable.HAS_APP_EXIT_SHOWED, hasAppExitShowed ? TRUE : FALSE);
            int count = 0;
            try {
                count = mDBHelper.update(AppPermissionsTable.TABLE_NAME, cv, AppPermissionsTable.PKG_NAME + "=?",
                        new String[]{pkgName});
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return count;
        }
    }

    /**
     * 更新权限信息
     */
    public boolean updatePermission(UpdateParams updateParams) {
        try {
            return mDBHelper.update(updateParams);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定包名的数据
     *
     * @param pkgName 包名
     */
    public synchronized int delete(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return -1;
        }
        int count = -1;
        try {
            count = mDBHelper.delete(AppPermissionsTable.TABLE_NAME, AppPermissionsTable.PKG_NAME + "=?",
                    new String[]{pkgName});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除所有数据
     */
    public synchronized void deleteAllData() {
        try {
            mDBHelper.delete(AppPermissionsTable.TABLE_NAME, null, null);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建插入参数
     */
    public synchronized InsertParams buildInsertParams(String pkgName, String[] permissions) {
        if (permissions == null || permissions.length <= 0) {
            return null;
        }
        ContentValues cv = getInsertCV(pkgName, permissions, true, false);
        return new InsertParams(AppPermissionsTable.TABLE_NAME, cv);
    }

    /**
     * 构建更新参数
     * */
    public synchronized UpdateParams buildUpdateParams(String pkgName, String[] permissions) {
        InsertParams insertParams = buildInsertParams(pkgName, permissions);
        UpdateParams updateParams = new UpdateParams(AppPermissionsTable.TABLE_NAME, insertParams.getContentValues(), AppPermissionsTable.PKG_NAME + "=?", new String[]{pkgName});
        return updateParams;
    }

    /**
     * 批量提交
     */
    public synchronized boolean commitBatch(List<InsertParams> insertParamsList) {
        if (insertParamsList != null) {
            try {
                return mDBHelper.insert(insertParamsList);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

   /* public boolean updatePermission(UpdateParams updateParams) {
        try {
            List<UpdateParams> updateParamseList = new ArrayList<>(1);
            updateParamseList.add(updateParams);
            return mDBHelper.update(updateParamseList);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public boolean insertPermission(InsertParams insertParams) {

    }*/

    /**
     * 将权限数组转换成对应的长字符串
     *
     * @param permissions 权限数组
     */
    private String getPermissionString(String[] permissions) {
        StringBuilder sb = new StringBuilder();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                sb.append(permission);
                sb.append(";");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 通过参数创建ContentValues
     *
     * @param pkgName          包名
     * @param permissions      权限
     * @param hasFirstUpdate   是否已经更新过
     * @param hasAppExitShowed 是否展示过
     */
    private ContentValues getInsertCV(String pkgName, String[] permissions, boolean hasFirstUpdate,
                                      boolean hasAppExitShowed) {
        ContentValues cv = new ContentValues();
        cv.put(AppPermissionsTable.PKG_NAME, pkgName);
        cv.put(AppPermissionsTable.PERMISSIONS, getPermissionString(permissions));
        cv.put(AppPermissionsTable.IS_FIRST_UPDATE, hasFirstUpdate ? TRUE : FALSE);
        cv.put(AppPermissionsTable.HAS_APP_EXIT_SHOWED, hasAppExitShowed ? TRUE : FALSE);
        return cv;
    }
}

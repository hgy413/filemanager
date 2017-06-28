package com.jb.filemanager.function.applock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.params.UpdateParams;
import com.jb.filemanager.database.provider.BaseDataProvider;
import com.jb.filemanager.database.table.AntiPeepTable;
import com.jb.filemanager.function.applock.model.bean.AntiPeepBean;
import com.jb.filemanager.util.FileUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nieyh on 2016/12/28.
 */

public class AntiPeepDatabaseHelper extends BaseDataProvider {

    private static AntiPeepDatabaseHelper sInstance;

    public static AntiPeepDatabaseHelper getInstance() {
        if (sInstance == null) {
            sInstance = new AntiPeepDatabaseHelper(TheApplication.getAppContext());
        }
        return sInstance;
    }

    private AntiPeepDatabaseHelper(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    /**
     * 插入偷窥者信息
     */
    public void insertPeep(AntiPeepBean bean) {
        ContentValues value = new ContentValues();
        value.put(AntiPeepTable.PKG_NAME, bean.getPackageName());
        value.put(AntiPeepTable.CREATE_TIME, bean.getCreateTime());
        value.put(AntiPeepTable.FILE_NAME, FileUtil.getNameFromFilepath(bean.getPath()));
        try {
            mDBHelper.insert(AntiPeepTable.TABLE_NAME, value);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有偷窥者信息
     */
    public void deleteAllPeep() {
        try {
            mDBHelper.delete(AntiPeepTable.TABLE_NAME, null, null);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新所有偷窥者路径信息
     */
    public void updatePeepPath(List<AntiPeepBean> list) {
        List<UpdateParams> upList = new ArrayList<UpdateParams>();
        for (AntiPeepBean bean : list) {
            ContentValues value = new ContentValues();
            value.put(AntiPeepTable.CREATE_TIME, bean.getCreateTime());
            UpdateParams up = new UpdateParams(AntiPeepTable.TABLE_NAME, value,
                    AntiPeepTable.FILE_NAME + "='" + FileUtil.getNameFromFilepath(bean.getPath()) + "'");
            upList.add(up);
        }
        try {
            mDBHelper.update(upList);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除偷窥者路径信息
     */
    public void deletePeep(AntiPeepBean bean) {
        try {
            mDBHelper.delete(AntiPeepTable.TABLE_NAME, AntiPeepTable.CREATE_TIME + "=?",
                    new String[]{bean.getCreateTime() + ""});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据创建时间获取应用包名
     */
    public void initAntiPeepBean(List<AntiPeepBean> srcList) {
        Cursor cursor = null;
        List<AntiPeepBean> dbList = new ArrayList<AntiPeepBean>();
        try {
            cursor = mDBHelper.query(AntiPeepTable.TABLE_NAME, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                do {
                    dbList.add(AntiPeepTable.parseFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Iterator<AntiPeepBean> iterator = srcList.iterator();
        while (iterator.hasNext()) {
            AntiPeepBean srcBean = iterator.next();
            String srcName = FileUtil.getName(srcBean.getPath());
            if (TextUtils.isEmpty(srcName)) {
                iterator.remove();
                continue;
            }
            boolean mHasDataFound = false;
            for (AntiPeepBean dbBean : dbList) {
                if ((srcBean.getCreateTime() == dbBean.getCreateTime()) &&
                        (srcName.equals(dbBean.getPath()))) {
                    // 注意:DB存的是文件名而非路径
                    srcBean.setPackageName(dbBean.getPackageName());
                    mHasDataFound = true;
                    break;
                }
            }
            // 若文件下的数据不存在于数据库中，则不展示
            if (!mHasDataFound) {
                iterator.remove();
            }
        }
    }


}

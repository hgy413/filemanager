package com.jb.filemanager.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.database.BaseDatabaseHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.table.CleanIgnoreTable;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreAdBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreResidueBean;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 垃圾清理白名单数据库操作类
 *
 * @author chenbenbin
 */
public class CleanIgnoreDao {
    private BaseDatabaseHelper mDBHelper;

    public CleanIgnoreDao(Context context, BaseDatabaseHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    // 添加
    public void addCacheApp(CleanIgnoreCacheAppBean bean) {
        Logger.e("IgnoreDao", "add Cache App");
        ContentValues value = new ContentValues();
        value.put(CleanIgnoreTable.TYPE, bean.getType());
        value.put(CleanIgnoreTable.TITLE, bean.getTitle());
        value.put(CleanIgnoreTable.KEY_1, bean.getPackageName());
        try {
            mDBHelper.insert(CleanIgnoreTable.TABLE_NAME, value);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void addCachePath(CleanIgnoreCachePathBean bean) {
        Logger.e("IgnoreDao", "add Cache Path");
        ContentValues value = new ContentValues();
        value.put(CleanIgnoreTable.TYPE, bean.getType());
        value.put(CleanIgnoreTable.TITLE, bean.getTitle());
        value.put(CleanIgnoreTable.SUB_TITLE, bean.getSubTitle());
        value.put(CleanIgnoreTable.KEY_1, bean.getPackageName());
        value.put(CleanIgnoreTable.KEY_2, bean.getPath());
        try {
            mDBHelper.insert(CleanIgnoreTable.TABLE_NAME, value);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void addResidue(CleanIgnoreResidueBean bean) {
        ContentValues value = new ContentValues();
        value.put(CleanIgnoreTable.TYPE, bean.getType());
        value.put(CleanIgnoreTable.TITLE, bean.getTitle());
        value.put(CleanIgnoreTable.KEY_1, bean.getPkgNameString());
        try {
            mDBHelper.insert(CleanIgnoreTable.TABLE_NAME, value);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void addAd(CleanIgnoreAdBean bean) {
        ContentValues value = new ContentValues();
        value.put(CleanIgnoreTable.TYPE, bean.getType());
        value.put(CleanIgnoreTable.TITLE, bean.getTitle());
        value.put(CleanIgnoreTable.KEY_1, bean.getPath());
        try {
            mDBHelper.insert(CleanIgnoreTable.TABLE_NAME, value);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    // 查询
    public List<CleanIgnoreBean> queryCacheApp() {
        Logger.e("IgnoreDao", "queryCacheApp");
        return queryCommon(CleanIgnoreBean.TYPE_CACHE_APP);
    }

    public List<CleanIgnoreBean> queryCachePath() {
        Logger.e("IgnoreDao", "queryCachePath");
        return queryCommon(CleanIgnoreBean.TYPE_CACHE_PATH);
    }

    public List<CleanIgnoreBean> queryResidue() {
        return queryCommon(CleanIgnoreBean.TYPE_RESIDUE);
    }

    public List<CleanIgnoreBean> queryAd() {
        return queryCommon(CleanIgnoreBean.TYPE_AD);
    }

    private ArrayList<CleanIgnoreBean> queryCommon(int type) {
        ArrayList<CleanIgnoreBean> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDBHelper.query(CleanIgnoreTable.TABLE_NAME, null,
                    CleanIgnoreTable.TYPE + "=?", new String[]{type + ""}, CleanIgnoreTable.ID + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    list.add(CleanIgnoreTable.parseFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    // 删除
    public void removeCacheApp(CleanIgnoreCacheAppBean bean) {
        try {
            mDBHelper.delete(CleanIgnoreTable.TABLE_NAME, CleanIgnoreTable.KEY_1
                    + "=?", new String[]{bean.getPackageName()});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void removeCachePath(CleanIgnoreCachePathBean bean) {
        try {
            mDBHelper.delete(CleanIgnoreTable.TABLE_NAME,
                    CleanIgnoreTable.KEY_1 + "=? and " + CleanIgnoreTable.KEY_2 + "=?",
                    new String[]{bean.getPackageName(), bean.getPath()});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void removeCachePathByPackageName(String packageName) {
        try {
            mDBHelper.delete(CleanIgnoreTable.TABLE_NAME,
                    CleanIgnoreTable.KEY_1 + "=? and " + CleanIgnoreTable.TYPE + "=?",
                    new String[]{packageName, CleanIgnoreBean.TYPE_CACHE_PATH + ""});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void removeResidue(CleanIgnoreResidueBean bean) {
        try {
            mDBHelper.delete(CleanIgnoreTable.TABLE_NAME,
                    CleanIgnoreTable.KEY_1 + "=?",
                    new String[]{bean.getPkgNameString()});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void removeAd(CleanIgnoreAdBean bean) {
        try {
            mDBHelper.delete(CleanIgnoreTable.TABLE_NAME,
                    CleanIgnoreTable.KEY_1 + "=?",
                    new String[]{bean.getPath()});
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

}

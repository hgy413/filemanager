package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.dao.CleanIgnoreDao;
import com.jb.filemanager.database.dao.CleanScanOvertimeDAO;
import com.jb.filemanager.database.params.InsertParams;
import com.jb.filemanager.database.table.AccessibilityIgnoreListTable;
import com.jb.filemanager.database.table.IgnoreListTable;
import com.jb.filemanager.database.table.SettingTable;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreAdBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreResidueBean;
import com.jb.filemanager.util.Logger;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by xiaoyu on 2017/1/4 21:28.
 */

public class DataProvider extends BaseDataProvider {
    private CleanScanOvertimeDAO mCleanScanOvertimeDAO;
    private CleanIgnoreDao mCleanIgnoreDao;
    //private MsgDao mMsgDao;
    //private AntiPeepDao mAntiPeepDao;

    /**
     * 不要随意new实例，通过
     * {link com.gto.zero.zboost.framwork.LauncherModel#getDataProvider()}
     * 获取实例即可<br>
     */
    public DataProvider(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
        mCleanScanOvertimeDAO = new CleanScanOvertimeDAO(context, mDBHelper);
        mCleanIgnoreDao = new CleanIgnoreDao(context, mDBHelper);
        //mMsgDao = new MsgDao(mDBHelper);
        //mAntiPeepDao = new AntiPeepDao(mDBHelper);
    }
    /**
     * 查询设置项的数据
     */
    public Cursor querySettingDB() {
        synchronized (mLock) {
            String sql = "select * from " + SettingTable.TABLE_NAME;
            return mDBHelper.rawQuery(sql, null);
        }
    }

    public void updateSettingDB(final String key, final String value) {
        TheApplication.postRunOnShortTaskThread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(SettingTable.KEY, key);
                values.put(SettingTable.VALUE, value);
                String where = SettingTable.KEY + " = '" + key + "'";
                try {
                    boolean isSuccess = update(SettingTable.TABLE_NAME, values,
                            where, null);
                    if (!isSuccess) {
                        insert(SettingTable.TABLE_NAME, values);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询单个设置项<br>
     * @param key
     * @return
     */
    public String querySetting(final String key) {
        String value = null;
        Cursor query = query(SettingTable.TABLE_NAME,
                new String[] { SettingTable.VALUE }, SettingTable.KEY + "=?",
                new String[] { key }, "_id");
        if (query != null) {
            try {
                final int columnIndex = query
                        .getColumnIndex(SettingTable.VALUE);
                value = query.getString(columnIndex);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                value = null;
                query.close();
            }
        }
        return value;
    }

    /**
     * 获取白名单列表
     *
     * @return 返回一个hashset，方便匹配
     */
    public LinkedHashSet<String> getIgnoreList() {
        LinkedHashSet<String> pkgNames = new LinkedHashSet<String>();
        final String[] projection = new String[] { IgnoreListTable.ID,
                IgnoreListTable.COL_PACKAGE_NAME };
        Cursor cursor = null;
        try {
            cursor = query(IgnoreListTable.TABLE_NAME, projection, null, null,
                    "'_id' DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String p = cursor.getString(1);
                    if (!TextUtils.isEmpty(p)) {
                        pkgNames.add(p);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (Logger.DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return pkgNames;
    }

    /**
     * 将包名加入白名单列表中
     */
    public boolean insertIntoIgnoreList(String packageName) {
        boolean success = false;
        if (!TextUtils.isEmpty(packageName)) {
            ContentValues cv = new ContentValues();
            cv.put(IgnoreListTable.COL_PACKAGE_NAME, packageName);
            InsertParams params = new InsertParams(IgnoreListTable.TABLE_NAME,
                    cv);
            success = insert(params);
        }
        return success;
    }

    /**
     * 将包名从白名单列表中移除
     */
    public boolean deleteFromIgnoreList(String packageName) {
        boolean success = false;
        if (!TextUtils.isEmpty(packageName)) {
            success = delete(IgnoreListTable.TABLE_NAME,
                    IgnoreListTable.COL_PACKAGE_NAME + "=?",
                    new String[] { packageName }) >= 1;
        }
        return success;
    }

    /**
     * 获取辅助杀白名单列表<br>
     * @return 返回一个hashset，方便匹配
     */
    public LinkedHashSet<String> getAccessibilityIgnoreList() {
        LinkedHashSet<String> pkgNames = new LinkedHashSet<String>();
        final String[] projection = new String[] {
                AccessibilityIgnoreListTable.ID,
                AccessibilityIgnoreListTable.PACKAGE_NAME };
        Cursor cursor = null;
        try {
            cursor = query(AccessibilityIgnoreListTable.TABLE_NAME, projection,
                    null, null, "'_id' DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int columnIndex = cursor
                            .getColumnIndex(AccessibilityIgnoreListTable.PACKAGE_NAME);
                    String packageName = cursor.getString(columnIndex);
                    if (!TextUtils.isEmpty(packageName)) {
                        pkgNames.add(packageName);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (Logger.DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return pkgNames;
    }

    /**
     * 将包名加入辅助查杀白名单列表中<br>
     */
    public boolean insertIntoAccessibilityIgnoreList(String packageName) {
        boolean success = false;
        if (!TextUtils.isEmpty(packageName)) {
            ContentValues cv = new ContentValues();
            cv.put(AccessibilityIgnoreListTable.PACKAGE_NAME, packageName);
            InsertParams params = new InsertParams(
                    AccessibilityIgnoreListTable.TABLE_NAME, cv);
            success = insert(params);
        }
        return success;
    }

    /**
     * 垃圾扫描超时：数据库是否存在该路径
     */
    public boolean isCleanScanOvertimePathExist(String path) {
        return mCleanScanOvertimeDAO.isPathExist(path);
    }

    /**
     * 垃圾扫描超时：插入新路径
     */
    public void insertCleanScanOvertimePath(String path) {
        mCleanScanOvertimeDAO.insertPath(path);
    }

    // 添加
    public void addCacheApp(CleanIgnoreCacheAppBean bean) {
        mCleanIgnoreDao.addCacheApp(bean);
    }

    public void addCachePath(CleanIgnoreCachePathBean bean) {
        mCleanIgnoreDao.addCachePath(bean);
    }

    public void addResidue(CleanIgnoreResidueBean bean) {
        mCleanIgnoreDao.addResidue(bean);
    }

    public void addAd(CleanIgnoreAdBean bean) {
        mCleanIgnoreDao.addAd(bean);
    }

    // 查询
    public List<CleanIgnoreBean> queryCacheApp() {
        return mCleanIgnoreDao.queryCacheApp();
    }

    public List<CleanIgnoreBean> queryCachePath() {
        return mCleanIgnoreDao.queryCachePath();
    }

    public List<CleanIgnoreBean> queryResidue() {
        return mCleanIgnoreDao.queryResidue();
    }

    public List<CleanIgnoreBean> queryAd() {
        return mCleanIgnoreDao.queryAd();
    }

    // 删除
    public void removeCacheApp(CleanIgnoreCacheAppBean bean) {
        mCleanIgnoreDao.removeCacheApp(bean);
    }

    public void removeCachePath(CleanIgnoreCachePathBean bean) {
        mCleanIgnoreDao.removeCachePath(bean);
    }

    public void removeCachePathByPackageName(String packageName) {
        mCleanIgnoreDao.removeCachePathByPackageName(packageName);
    }

    public void removeResidue(CleanIgnoreResidueBean bean) {
        mCleanIgnoreDao.removeResidue(bean);
    }

    public void removeAd(CleanIgnoreAdBean bean) {
        mCleanIgnoreDao.removeAd(bean);
    }

//    public MsgDao getMsgDao() {
//        return mMsgDao;
//    }

//    public AntiPeepDao getAntiPeepDao() {
//        return mAntiPeepDao;
//    }
}

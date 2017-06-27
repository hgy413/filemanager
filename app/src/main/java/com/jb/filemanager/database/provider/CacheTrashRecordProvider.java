package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.table.CacheTrashRecordTable;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.SysCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;

import java.util.Calendar;
import java.util.List;

/**
 * Created by nieyh on 2017/3/17.
 * 缓存垃圾清理的记录提供者
 */

public class CacheTrashRecordProvider extends BaseDataProvider {

    //使用OBJECT静态锁 来锁定多个线程操作 而不用创建Provider 的单例来实现
    public static final Object SYNCHRO_LOCK = new Object();

    public CacheTrashRecordProvider(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    /**
     * 插入缓存垃圾记录
     *
     * @param appName 应用名称
     * @param pkgName 包名
     * @param size    缓存大小
     *                先更新后插入数据
     */
    private void insertCacheTrashRecord(String appName, String pkgName, long size) {
        synchronized (SYNCHRO_LOCK) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CacheTrashRecordTable.CACHE_TRASH_APP_NAME, appName);
            contentValues.put(CacheTrashRecordTable.CACHE_TRASH_PKG_NAME, pkgName);
            contentValues.put(CacheTrashRecordTable.CACHE_TRASH_SIZE, size);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int now = year * 10000 + month * 100 + day;

            contentValues.put(CacheTrashRecordTable.CACHE_TRASH_RECORD_TIME, now);
            boolean isSuccess = update(CacheTrashRecordTable.TABLE_NAME, contentValues, CacheTrashRecordTable.CACHE_TRASH_RECORD_TIME + "=? AND "
                            + CacheTrashRecordTable.CACHE_TRASH_PKG_NAME + "=? AND "
                            + CacheTrashRecordTable.CACHE_TRASH_APP_NAME + "=?",
                    new String[]{String.valueOf(now), pkgName, appName});
            if (!isSuccess) {
                insert(CacheTrashRecordTable.TABLE_NAME, contentValues);
            }
        }
    }

    /**
     * 插入所有缓存垃圾记录 没有三级目录
     *
     * @param appCacheBeanList 应用缓存列表
     */
    public void insertAllCacheTrashRecord(List<ItemBean> appCacheBeanList) {
        synchronized (SYNCHRO_LOCK) {
            if (appCacheBeanList != null) {
                for (ItemBean appCacheBean : appCacheBeanList) {
                    if (appCacheBean instanceof AppCacheBean) {
                        insertAppCacheTrashRecord((AppCacheBean) appCacheBean);
                    } else if (appCacheBean instanceof SysCacheBean) {
                        //假设选中了 才保存
                        if (appCacheBean.getState() == GroupSelectBox.SelectState.ALL_SELECTED) {
                            insertSysCacheTrashRecord(appCacheBean.getSubItemList());
                        }
                    }
                }
            }
        }
    }

    /**
     * 插入所有缓存垃圾记录 没有三级目录
     *
     * @param itemBean 应用缓存列表
     */
    private void insertAppCacheTrashRecord(AppCacheBean itemBean) {
        synchronized (SYNCHRO_LOCK) {
            if (itemBean != null) {
                List<SubItemBean> subItemBeanList = itemBean.getSubItemList();
                if (subItemBeanList == null || subItemBeanList.size() == 0) {
                    return;
                }
                String appName = itemBean.getTitle();
                String pkgName = itemBean.getPackageName();
                long size = 0;
                for (SubItemBean appCacheBean : subItemBeanList) {
                    Log.d("CacheTrashRecordProvide", "appSubCacheBean.appname: " + appCacheBean.getTitle() + " isChecked " + appCacheBean.isChecked());
                    if (appCacheBean.isChecked()) {
                        //选上则存到数据库中
                        size += appCacheBean.getSize();
                    }

                }

                if (size == 0) {
                    return;
                }
                Log.d("CacheTrashRecordProvide", "appCacheBean.appname:" + appName);
                Log.d("CacheTrashRecordProvide", "appCacheBean.pkgName:" + pkgName);
                Log.d("CacheTrashRecordProvide", "appCacheBean.size:" + size);
                insertCacheTrashRecord(appName, pkgName, size);
            }
        }
    }

    /**
     * 插入系统缓存垃圾记录
     *
     * @param sysCacheBeanList 系统缓存列表
     */
    private void insertSysCacheTrashRecord(List<SubItemBean> sysCacheBeanList) {
        synchronized (SYNCHRO_LOCK) {
            if (sysCacheBeanList != null) {
                if (sysCacheBeanList.size() == 0) {
                    return;
                }
                String appName = TheApplication.getAppContext().getString(R.string.clean_item_sys_cache);
                long size = 0;
                for (SubItemBean sysCacheBean : sysCacheBeanList) {
                    if (sysCacheBean.isSysCache()) {
                        size += sysCacheBean.getSize();
                    }
                }
                if (size != 0) {
                    Log.d("CacheTrashRecordProvide", "sysCacheBean.appname:" + appName);
                    Log.d("CacheTrashRecordProvide", "sysCacheBean.size:" + size);
                    insertCacheTrashRecord(appName, "", size);
                }
            }
        }
    }
}

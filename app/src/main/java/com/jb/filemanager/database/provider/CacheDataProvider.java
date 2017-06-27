package com.jb.filemanager.database.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.CacheDBHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.table.CacheLangTable;
import com.jb.filemanager.database.table.CacheTable;
import com.jb.filemanager.database.tablebean.CacheLangBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.AppCacheBean;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.util.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 缓存文件数据库操作助手类 应用更新升级时不能直接覆盖数据库 由于需要考虑多语言切换，把缓存垃圾的描述独立出来为另一数据库
 * 这样在切换语言后就不会整个数据库的描述都被其他语言覆盖掉
 *
 * @author chenbenbin & kvan
 */
public class CacheDataProvider extends BaseDataProvider {
    private static CacheDataProvider sInstance;

    private static byte[] sKeySeed = new byte[]{0x70, 0x70, 0x51, 0x5a, 0x4b,
            0x26, 0x24, 0x4f, 0x67, 0x51, 0x72, 0x71, 0x3b, 0x48, 0x6a, 0x78};
    KeyGenerator mKgen = null;
    SecureRandom mSr = null;
    private static byte[] sIvBytesV2 = {0x07, 0x1f, 0x12, 0x50, 0x48, 0x7a,
            0x3c, 0x77, 0x26, 0x3c, 0x70, 0x54, 0x51, 0x51, 0x67, 0x1a};
    private byte[] mKey;

    private CacheDataProvider(Context context) {
        super(context);
        mDBHelper = new CacheDBHelper(context);

        try {
            mKgen = KeyGenerator.getInstance("AES");
            mSr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        change(sKeySeed, sIvBytesV2);
        mSr.setSeed(sKeySeed);
        mKgen.init(128, mSr); // 192 and 256 bits may not be available
        SecretKey skey = mKgen.generateKey();
        // setKey(skey.getEncoded());
        setKey(sKeySeed);
    }

    public synchronized static CacheDataProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CacheDataProvider(context);
        }
        return sInstance;
    }

    /**
     * 查询所有缓存垃圾数据
     *
     * @param context
     * @return
     */
    public Map<String, AppCacheBean> queryAllAppCache(Context context) {
        Cursor cursor = mDBHelper.query(CacheTable.TABLE_NAME, null, null,
                null, null);
        Map<String, AppCacheBean> appCacheBeanMap = new HashMap<>();
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String pkgName = cursor.getString(cursor.getColumnIndex(CacheTable.APP_PKG_NAME));
                        if (appCacheBeanMap.containsKey(pkgName)) {
                            continue;
                        } else {
                            AppCacheBean appCacheBean = new AppCacheBean();
                            appCacheBean.setPackageName(pkgName);
                            appCacheBeanMap.put(pkgName, appCacheBean);
                        }
                    } while (cursor.moveToNext());
                }
                if (cursor.moveToFirst()) {
                    do {
                        // 创建三级存储单元
                        String pkgName = cursor.getString(cursor.getColumnIndex(CacheTable.APP_PKG_NAME));
                        SubAppCacheBean subBean = CacheTable.parseFromCursor(context, cursor);
                        AppCacheBean appCacheBean = appCacheBeanMap.get(pkgName);
                        if (null != appCacheBean) {
                            appCacheBean.addSubItem(subBean);
                        }
                    } while (cursor.moveToNext());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return appCacheBeanMap;
    }

    /**
     * 查询单个应用的本地垃圾版本 当数据库中没有记录时返回默认0
     *
     * @param pkgName pkgName
     * @return int
     */
    public int queryAppCacheVersion(String pkgName) {
        int result = 0;
        Cursor cursor = mDBHelper.query(CacheTable.TABLE_NAME,
                new String[]{CacheTable.CACHE_VERSION},
                CacheTable.APP_PKG_NAME + "=?", new String[]{pkgName}, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    result = cursor.getInt(cursor
                            .getColumnIndex(CacheTable.CACHE_VERSION));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 查询所有缓存垃的多语言描述，其中Map的第二个参数中String[0]存放title，String[1]存放description
     *
     * @return
     */
    public Map<String, String[]> queryAllCacheDes() {
        Map<String, String[]> cacheDesMap = new HashMap<String, String[]>();
        Cursor cursor = mDBHelper.query(CacheLangTable.TABLE_NAME, null, null,
                null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        StringBuilder keySb = new StringBuilder();
                        keySb.append(cursor.getString(cursor
                                .getColumnIndex(CacheLangTable.LANG_TEXT_ID)));
                        keySb.append(cursor.getString(cursor
                                .getColumnIndex(CacheLangTable.LANG_LANG)));

                        String[] desc = new String[]{"cache", ""};
                        desc[0] = cursor.getString(cursor
                                .getColumnIndex(CacheLangTable.LANG_TITLE));
                        byte[] b = cursor
                                .getBlob(cursor
                                        .getColumnIndex(CacheLangTable.LANG_DESCRIPTION));
                        try {
                            byte[] decryptedData = decrypt(getKey(), b);
                            desc[1] = new String(decryptedData, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        cacheDesMap.put(keySb.toString(), desc);
                    } while (cursor.moveToNext());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                // CursorWindowAllocationException暂时只catch不处理
                Logger.e("kvan", "CacheDataProvider CursorWindowAllocationException");
            } finally {
                cursor.close();
            }
        }
        return cacheDesMap;
    }

    /**
     * 更新缓存数据后将数据存入数据库
     *
     * @param newCacheData
     * @return boolean
     */
    public boolean updateAppsCache(List<AppCacheBean> newCacheData) {
        Iterator<AppCacheBean> it2 = newCacheData.iterator();
        while (it2.hasNext()) {
            AppCacheBean tempBean2 = it2.next();
            // 先清除旧数据
            try {
                mDBHelper.delete(CacheTable.TABLE_NAME, CacheTable.APP_PKG_NAME
                        + "=?", new String[]{tempBean2.getPackageName()});
            } catch (DatabaseException e) {
                e.printStackTrace();
                return false;
            }
            // 再插入新数据
            ArrayList<SubItemBean> tempSubList = tempBean2
                    .getSubItemList();
            Iterator<SubItemBean> it3 = tempSubList.iterator();
            while (it3.hasNext()) {
                SubAppCacheBean tempBean3 = (SubAppCacheBean) it3
                        .next();
                ContentValues value = new ContentValues();
                // value.put(CacheTable.CACHE_PATH, tempBean3.getPath());
                try {
                    byte[] encryptedData = encrypt(getKey(), tempBean3
                            .getPath().getBytes());
                    value.put(CacheTable.CACHE_PATH, encryptedData);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                value.put(CacheTable.CACHE_TEXT_ID, tempBean3.getTextId());
                value.put(CacheTable.CACHE_WARN_LEVEL, tempBean3.getWarnLv());
                value.put(CacheTable.CACHE_DAYS_BEFORE,
                        tempBean3.getDayBefore());
                value.put(CacheTable.CACHE_TYPE, tempBean3.getContentType());
                // 版本号和包名需要在二级单元中获取
                value.put(CacheTable.CACHE_VERSION, tempBean2.getVersion());
                value.put(CacheTable.APP_PKG_NAME, tempBean2.getPackageName());
                try {
                    mDBHelper.insert(CacheTable.TABLE_NAME, value);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 将新的多语言描述保存到数据库
     *
     * @param langList <CacheLangBean>
     * @return boolean
     */
    public boolean updateLangData(List<CacheLangBean> langList) {
        if (langList.isEmpty()) {
            return true;
        }
        Iterator<CacheLangBean> it = langList.iterator();

		/*----- 恢复旧方法只需要把以下去除然后把紧接下面被注释的反注释 -------*/
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();
        try {
            //批量处理操作
            while (it.hasNext()) {
                CacheLangBean tempBean = it.next();
                // Loger.d("kvan", tempBean.toString());
                ContentValues value = new ContentValues();
                value.put(CacheLangTable.LANG_ID, tempBean.getId());
                value.put(CacheLangTable.LANG_TEXT_ID, tempBean.getTextId());
                value.put(CacheLangTable.LANG_LANG, tempBean.getLang());
                value.put(CacheLangTable.LANG_TITLE, tempBean.getTitle());
                // value.put(CacheLangTable.LANG_DESCRIPTION,
                // tempBean.getDescription());
                try {
                    byte[] encryptedData = encrypt(getKey(), tempBean
                            .getDescription().getBytes());
                    value.put(CacheLangTable.LANG_DESCRIPTION, encryptedData);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                int status = db.update(CacheLangTable.TABLE_NAME, value,
                        CacheLangTable.LANG_ID + "=?",
                        new String[]{String.valueOf(tempBean.getId())});
                if (status == 0) {
                    db.insert(CacheLangTable.TABLE_NAME, null, value);
                }
            }

            //设置事务标志为成功，当结束事务时就会提交事务
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //结束事务
            db.endTransaction();
        }
        /*----- 恢复旧方法只需要把以上去除然后把紧接下面被注释的反注释 -------*/


//		while (it.hasNext()) {
//			CacheLangBean tempBean = it.next();
//			// Loger.d("kvan", tempBean.toString());
//			ContentValues value = new ContentValues();
//			value.put(CacheLangTable.LANG_ID, tempBean.getId());
//			value.put(CacheLangTable.LANG_TEXT_ID, tempBean.getTextId());
//			value.put(CacheLangTable.LANG_LANG, tempBean.getLang());
//			value.put(CacheLangTable.LANG_TITLE, tempBean.getTitle());
//			// value.put(CacheLangTable.LANG_DESCRIPTION,
//			// tempBean.getDescription());
//			try {
//				byte[] encryptedData = encrypt(getKey(), tempBean
//						.getDescription().getBytes());
//				value.put(CacheLangTable.LANG_DESCRIPTION, encryptedData);
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//			try {
//				int status = mDBHelper.update(CacheLangTable.TABLE_NAME, value,
//						CacheLangTable.LANG_ID + "=?",
//						new String[] { String.valueOf(tempBean.getId()) });
//				if (status == 0) {
//					mDBHelper.insert(CacheLangTable.TABLE_NAME, value);
//				}
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//				return false;
//			}
//		}

        return true;
    }


    /**
     * 查询上次请求时返回的langCode
     *
     * @return String
     */
    public String getLangCode() {
        String result = "1";
        Cursor cursor = mDBHelper.query("store_table",
                new String[]{"value"}, "key = 'langCode'", null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 更新langCode
     *
     * @param langCode
     */
    public void updateLangCode(String langCode) {
        ContentValues value = new ContentValues();
        value.put("value", langCode);
        try {
            mDBHelper.update("store_table", value, "key = 'langCode'", null);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询上次请求时返回的cacheCode
     *
     * @return String
     */
    public String getCacheCode() {
        String result = "1";
        Cursor cursor = mDBHelper.query("store_table",
                new String[]{"value"}, "key = 'cacheCode'", null, null);
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 更新cacheCode
     *
     * @param cacheCode
     */
    public void updateCacheCode(String cacheCode) {
        ContentValues value = new ContentValues();
        value.put("value", cacheCode);
        try {
            mDBHelper.update("store_table", value, "key = 'cacheCode'", null);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        IvParameterSpec iv = new IvParameterSpec(sIvBytesV2);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(sIvBytesV2);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public byte[] getKey() {
        return mKey;
    }

    public void setKey(byte[] key) {
        this.mKey = key;
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
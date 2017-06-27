package com.jb.filemanager.database.provider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.CacheDBHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.table.AdLangTable;
import com.jb.filemanager.database.table.AdPathTable;
import com.jb.filemanager.database.tablebean.AdLangBean;
import com.jb.filemanager.database.tablebean.AdPathBean;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 广告垃圾数据库的逻辑助手类
 *
 * @author chenbenbin & kvan
 */
public class AdDataProvider extends BaseDataProvider {
    private static AdDataProvider sInstance;

    private static byte[] sKeySeed = new byte[]{0x70, 0x70, 0x51, 0x5a, 0x4b,
            0x26, 0x24, 0x4f, 0x67, 0x51, 0x72, 0x71, 0x3b, 0x48, 0x6a, 0x78};
    KeyGenerator mKgen = null;
    SecureRandom mSr = null;
    private static byte[] sIvBytesV2 = {0x07, 0x1f, 0x12, 0x50, 0x48, 0x7a,
            0x3c, 0x77, 0x26, 0x3c, 0x70, 0x54, 0x51, 0x51, 0x67, 0x1a};
    private byte[] mKey;

    private AdDataProvider(Context context) {
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

    public static AdDataProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdDataProvider(context);
        }
        return sInstance;
    }

    /**
     * 查询所有残留垃圾的路径
     *
     * @return
     */
    public ArrayList<AdPathBean> queryAllAdPath() {
        Cursor cursor = mDBHelper.query(AdPathTable.TABLE_NAME, null,
                null, null, null);
        if (null == cursor) {
            return null;
        }
        ArrayList<AdPathBean> adBeanList = new ArrayList<AdPathBean>();
        try {
            while (cursor.moveToNext()) {
                AdPathBean bean = new AdPathBean();
                bean.setAdId(cursor.getString(cursor
                        .getColumnIndex(AdPathTable.AD_ID)));
                byte[] b = cursor.getBlob(cursor
                        .getColumnIndex(AdPathTable.PATH));
                try {
                    byte[] decryptedData = decrypt(getKey(), b);
                    bean.setPath(new String(decryptedData, "UTF-8"));
                    adBeanList.add(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return adBeanList;
    }

    /**
     * 查询所有广告垃圾多语言描述
     *
     * @return
     */
    public Map<String, String> queryAllAdLang() {
        Cursor cursor = mDBHelper.query(AdLangTable.TABLE_NAME, null,
                null, null, null);
        if (null == cursor) {
            return null;
        }
        Map<String, String> langMap = new HashMap<String, String>();
        try {
            while (cursor.moveToNext()) {
                StringBuilder key = new StringBuilder();
                key.append(cursor.getString(cursor
                        .getColumnIndex(AdLangTable.AD_ID)));
                key.append("#");
                key.append(cursor.getString(cursor
                        .getColumnIndex(AdLangTable.LANG_CODE)));

                langMap.put(key.toString(), cursor.getString(cursor
                        .getColumnIndex(AdLangTable.AD_TITLE)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return langMap;
    }

    /**
     * 将新广告垃圾路径保存到数据库
     *
     * @param adPathBeanList <AdBean>
     * @return boolean
     */
    public boolean updateAdPathData(List<AdPathBean> adPathBeanList) {
        if (adPathBeanList.isEmpty()) {
            return false;
        }
        // 插入前删除旧数据
        Iterator<AdPathBean> it2 = adPathBeanList.iterator();
        Set<String> idSet = new HashSet<String>();
        while (it2.hasNext()) {
            idSet.add(it2.next().getAdId());
        }
        Iterator<String> it3 = idSet.iterator();
        while (it3.hasNext()) {
            String id = it3.next();
            try {
                mDBHelper.delete(AdPathTable.TABLE_NAME,
                        AdPathTable.AD_ID + "=?", new String[]{id});
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

        Iterator<AdPathBean> it = adPathBeanList.iterator();

        while (it.hasNext()) {
            AdPathBean tempBean = it.next();
            // Loger.d("kvan", tempBean.toString());
            ContentValues value = new ContentValues();
            value.put(AdPathTable.AD_ID, tempBean.getAdId());
            try {
                byte[] encryptedData = encrypt(getKey(), tempBean.getPath()
                        .getBytes());
                value.put(AdPathTable.PATH, encryptedData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mDBHelper.insert(AdPathTable.TABLE_NAME, value);
            } catch (DatabaseException e) {
                e.printStackTrace();
                continue;
            }
        }
        return true;
    }

    /**
     * 将新广告垃圾多语言应用名保存到数据库
     *
     * @param adLangList <AdLangBean>
     * @return boolean
     */
    public boolean updateAdLangData(List<AdLangBean> adLangList) {
        if (adLangList.isEmpty()) {
            return false;
        }
        Iterator<AdLangBean> it = adLangList.iterator();

		/*----- 恢复旧方法只需要把以下去除然后把紧接下面被注释的反注释 -------*/
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();
        try {
            //批量处理操作
            //do something
            while (it.hasNext()) {
                AdLangBean tempBean = it.next();
                // Loger.d("kvan", tempBean.toString());
                ContentValues value = new ContentValues();
                value.put(AdLangTable.AD_ID, tempBean.getAdId());
                value.put(AdLangTable.LANG_CODE, tempBean.getLangCode());
                value.put(AdLangTable.AD_TITLE, tempBean.getTitle());
                int status = db.update(
                        AdLangTable.TABLE_NAME,
                        value,
                        AdLangTable.AD_ID + "=? AND "
                                + AdLangTable.LANG_CODE + "=?",
                        new String[]{tempBean.getAdId(),
                                tempBean.getLangCode()});
                if (status == 0) {
                    db.insert(AdLangTable.TABLE_NAME, null, value);
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
//			AdLangBean tempBean = it.next();
//			// Loger.d("kvan", tempBean.toString());
//			ContentValues value = new ContentValues();
//			value.put(AdLangTable.AD_ID, tempBean.getAdId());
//			value.put(AdLangTable.LANG_CODE, tempBean.getLangCode());
//			value.put(AdLangTable.AD_TITLE, tempBean.getTitle());
//			try {
//				int status = mDBHelper.update(
//						AdLangTable.TABLE_NAME,
//						value,
//						AdLangTable.AD_ID + "=? AND "
//								+ AdLangTable.LANG_CODE + "=?",
//						new String[] { tempBean.getAdId(),
//								tempBean.getLangCode() });
//				if (status == 0) {
//					mDBHelper.insert(AdLangTable.TABLE_NAME, value);
//				}
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
        return true;
    }


    /**
     * 查询上次请求时返回的adCode
     *
     * @return String
     */
    public String getAdCode() {
        String result = "1";
        Cursor cursor = mDBHelper.query("store_table",
                new String[]{"value"}, "key = 'adCode'", null, null);
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
     * 更新adCode
     *
     * @param langCode adCode
     */
    public void updateAdCode(String langCode) {
        ContentValues value = new ContentValues();
        value.put("value", langCode);
        try {
            mDBHelper.update("store_table", value, "key = 'adCode'", null);
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
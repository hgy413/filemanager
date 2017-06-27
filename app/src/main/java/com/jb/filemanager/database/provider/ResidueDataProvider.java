package com.jb.filemanager.database.provider;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.jb.filemanager.database.CacheDBHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.table.ResidueLangTable;
import com.jb.filemanager.database.table.ResiduePathTable;
import com.jb.filemanager.database.tablebean.ResidueLangBean;
import com.jb.filemanager.database.tablebean.ResiduePathBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.ResidueBean;
import com.jb.filemanager.util.Logger;

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
public class ResidueDataProvider extends BaseDataProvider {

    private static byte[] sKeySeed = new byte[]{0x70, 0x70, 0x51, 0x5a, 0x4b,
            0x26, 0x24, 0x4f, 0x67, 0x51, 0x72, 0x71, 0x3b, 0x48, 0x6a, 0x78};
    private static byte[] sIvBytesV2 = {0x07, 0x1f, 0x12, 0x50, 0x48, 0x7a,
            0x3c, 0x77, 0x26, 0x3c, 0x70, 0x54, 0x51, 0x51, 0x67, 0x1a};
    private byte[] mKey;
    private KeyGenerator mKgen = null;
    private SecureRandom mSr = null;

    private static ResidueDataProvider sInstance;
    private ResidueDataProvider(Context context) {
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

    public static ResidueDataProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ResidueDataProvider(context);
        }
        return sInstance;
    }

    /**
     * 查询所有残留垃圾的路径
     */
    public ArrayList<ResidueBean> queryAllResiduePath() {
        Cursor cursor = mDBHelper.query(ResiduePathTable.TABLE_NAME, null,
                null, null, null);
        if (null == cursor) {
            return null;
        }
        ArrayList<ResidueBean> residueBeanList = new ArrayList<ResidueBean>();
        try {
            while (cursor.moveToNext()) {
                ResidueBean bean = new ResidueBean();
                bean.setPathId(cursor.getString(cursor
                        .getColumnIndex(ResiduePathTable.PATH_ID)));
                bean.setPackageName(cursor.getString(cursor
                        .getColumnIndex(ResiduePathTable.PACKAGE_NAME)));
                byte[] b = cursor.getBlob(cursor
                        .getColumnIndex(ResiduePathTable.PATH));
                try {
                    byte[] decryptedData = decrypt(getKey(), b);
                    bean.setPath(new String(decryptedData, "UTF-8"));
                    bean.setDBKey(bean.getPath());
                    residueBeanList.add(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return residueBeanList;
    }

    /**
     * 查询所有残留垃圾多语言应用名
     */
    public Map<String, String> queryAllResidueLang() {
        Cursor cursor = mDBHelper.query(ResidueLangTable.TABLE_NAME, null,
                null, null, null);
        if (null == cursor) {
            return null;
        }
        Map<String, String> langMap = new HashMap<String, String>();
        try {
            while (cursor.moveToNext()) {
                StringBuilder key = new StringBuilder();
                key.append(cursor.getString(cursor
                        .getColumnIndex(ResidueLangTable.PACKAGE_NAME)));
                key.append("#");
                key.append(cursor.getString(cursor
                        .getColumnIndex(ResidueLangTable.LANG_CODE)));

                // langMap.put(key.toString(), cursor.getString(cursor
                // .getColumnIndex(ResidueLangTable.APP_NAME)));
                byte[] b = cursor.getBlob(cursor
                        .getColumnIndex(ResidueLangTable.APP_NAME));
                try {
                    byte[] decryptedData = decrypt(getKey(), b);
                    langMap.put(key.toString(), new String(decryptedData,
                            "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError error) {
                    // 暂时只catch不处理
                    Logger.e("kvan", "ResidueDataProvider oom");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return langMap;
    }

    /**
     * 将新残留垃圾路径保存到数据库
     */
    public boolean updateResiduePathData(List<ResiduePathBean> residueList) {
        if (residueList.isEmpty()) {
            return true;
        }
        Iterator<ResiduePathBean> it = residueList.iterator();

        /*----- 恢复旧方法只需要把以下去除然后把紧接下面被注释的反注释 -------*/
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();
        try {
            //批量处理操作
            while (it.hasNext()) {
                ResiduePathBean tempBean = it.next();
                // Loger.d("kvan", tempBean.toString());
                ContentValues value = new ContentValues();
                value.put(ResiduePathTable.PATH_ID, tempBean.getPathId());
                // value.put(ResiduePathTable.PATH, tempBean.getPath());
                value.put(ResiduePathTable.PACKAGE_NAME, tempBean.getPkgName());
                try {
                    byte[] encryptedData = encrypt(getKey(), tempBean.getPath()
                            .getBytes());
                    value.put(ResiduePathTable.PATH, encryptedData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int status = db.update(
                        ResiduePathTable.TABLE_NAME,
                        value,
                        ResiduePathTable.PATH_ID + "=? AND "
                                + ResiduePathTable.PACKAGE_NAME + "=?",
                        new String[]{tempBean.getPathId(),
                                tempBean.getPkgName()});
                if (status == 0) {
                    db.insert(ResiduePathTable.TABLE_NAME, null, value);
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
//        while (it.hasNext()) {
//            ResiduePathBean tempBean = it.next();
//            // Loger.d("kvan", tempBean.toString());
//            ContentValues value = new ContentValues();
//            value.put(ResiduePathTable.PATH_ID, tempBean.getPathId());
//            // value.put(ResiduePathTable.PATH, tempBean.getPath());
//            value.put(ResiduePathTable.PACKAGE_NAME, tempBean.getPkgName());
//            try {
//                byte[] encryptedData = encrypt(getKey(), tempBean.getPath()
//                        .getBytes());
//                value.put(ResiduePathTable.PATH, encryptedData);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                int status = mDBHelper.update(
//                        ResiduePathTable.TABLE_NAME,
//                        value,
//                        ResiduePathTable.PATH_ID + "=? AND "
//                                + ResiduePathTable.PACKAGE_NAME + "=?",
//                        new String[]{tempBean.getPathId(),
//                                tempBean.getPkgName()});
//                if (status == 0) {
//                    mDBHelper.insert(ResiduePathTable.TABLE_NAME, value);
//                }
//            } catch (DatabaseException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        return true;
    }

    /**
     * 将新残留垃圾多语言应用名保存到数据库
     */
    public boolean updateResidueLangData(
            List<ResidueLangBean> residueLangList) {
        if (residueLangList.isEmpty()) {
            return true;
        }
        Iterator<ResidueLangBean> it = residueLangList.iterator();
        Logger.d("kvan", "start residue update");

        /*----- 恢复旧方法只需要把以下去除然后把紧接下面被注释的反注释 -------*/
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();
        try {
            //批量处理操作
            //do something
            while (it.hasNext()) {
                ResidueLangBean tempBean = it.next();
                // Loger.d("kvan", tempBean.toString());
                ContentValues value = new ContentValues();
                value.put(ResidueLangTable.PATH_ID, tempBean.getmPathId());
                value.put(ResidueLangTable.LANG_CODE, tempBean.getLangCode());
                value.put(ResidueLangTable.PACKAGE_NAME, tempBean.getPkgName());
                // value.put(ResidueLangTable.APP_NAME, tempBean.getAppName());
                try {
                    byte[] encryptedData = encrypt(getKey(), tempBean.getAppName()
                            .getBytes());
                    value.put(ResidueLangTable.APP_NAME, encryptedData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int status = db.update(
                        ResidueLangTable.TABLE_NAME,
                        value,
                        ResidueLangTable.PACKAGE_NAME + "=? AND "
                                + ResidueLangTable.LANG_CODE + "=?",
                        new String[]{tempBean.getPkgName(),
                                tempBean.getLangCode()});
                if (status == 0) {
                    db.insert(ResidueLangTable.TABLE_NAME, null, value);
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

//        while (it.hasNext()) {
//            ResidueLangBean tempBean = it.next();
//            // Loger.d("kvan", tempBean.toString());
//            ContentValues value = new ContentValues();
//            value.put(ResidueLangTable.PATH_ID, tempBean.getmPathId());
//            value.put(ResidueLangTable.LANG_CODE, tempBean.getLangCode());
//            value.put(ResidueLangTable.PACKAGE_NAME, tempBean.getPkgName());
//            // value.put(ResidueLangTable.APP_NAME, tempBean.getAppName());
//            try {
//                byte[] encryptedData = encrypt(getKey(), tempBean.getAppName()
//                        .getBytes());
//                value.put(ResidueLangTable.APP_NAME, encryptedData);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                int status = mDBHelper.update(
//                        ResidueLangTable.TABLE_NAME,
//                        value,
//                        ResidueLangTable.PACKAGE_NAME + "=? AND "
//                                + ResidueLangTable.LANG_CODE + "=?",
//                        new String[]{tempBean.getPkgName(),
//                                tempBean.getLangCode()});
//                if (status == 0) {
//                    mDBHelper.insert(ResidueLangTable.TABLE_NAME, value);
//                }
//            } catch (DatabaseException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        Logger.d("kvan", "end residue update");
        return true;
    }

    /**
     * 查询上次请求时返回的residueCode
     */
    public String getResidueCode() {
        String result = "1";
        Cursor cursor = mDBHelper.query("store_table",
                new String[]{"value"}, "key = 'residueCode'", null, null);
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
     * 更新residueCode
     */
    public void updateResidueCode(String langCode) {
        ContentValues value = new ContentValues();
        value.put("value", langCode);
        try {
            mDBHelper.update("store_table", value, "key = 'residueCode'", null);
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
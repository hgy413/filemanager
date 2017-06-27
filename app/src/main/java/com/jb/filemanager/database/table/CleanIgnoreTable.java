package com.jb.filemanager.database.table;

import android.database.Cursor;

import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreAdBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCacheAppBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreCachePathBean;
import com.jb.filemanager.function.scanframe.clean.ignore.CleanIgnoreResidueBean;


/**
 * 清理白名单数据库表
 *
 * @author chenbenbin
 */
public class CleanIgnoreTable implements ITable {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "clean_ignore_table";

    /**
     * 数据库ID<br>
     * 值类型: int
     */
    public static final String ID = "_id";

    /**
     * 数据类型<br>
     * 值类型: int
     */
    public static final String TYPE = "type";
    /**
     * 显示的标题<br>
     * 值类型: string
     */
    public static final String TITLE = "title";
    /**
     * 显示的子标题<br>
     * 值类型: string
     */
    public static final String SUB_TITLE = "sub_title";
    /**
     * 数据key1<br>
     * 值类型: string
     */
    public static final String KEY_1 = "key_1";
    /**
     * 数据key2<br>
     * 值类型: string
     */
    public static final String KEY_2 = "key_2";

    /**
     * 创建表
     */
    public static final String CREATE_TABLE;

    static {
        CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_NAME)
                + " (" + ID + " INTEGER PRIMARY KEY, " + TYPE + " INTEGER, "
                + TITLE + " TEXT, " + SUB_TITLE + " TEXT, " + KEY_1 + " TEXT, " + KEY_2 + " TEXT)";
    }

    public static CleanIgnoreBean parseFromCursor(Cursor cursor) {
        int type = cursor.getInt(cursor.getColumnIndex(TYPE));
        switch (type) {
            case CleanIgnoreBean.TYPE_CACHE_APP:
                CleanIgnoreCacheAppBean cacheApp = new CleanIgnoreCacheAppBean();
                cacheApp.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                cacheApp.setPackageName(cursor.getString(cursor.getColumnIndex(KEY_1)));
                return cacheApp;
            case CleanIgnoreBean.TYPE_CACHE_PATH:
                CleanIgnoreCachePathBean cachePath = new CleanIgnoreCachePathBean();
                cachePath.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                cachePath.setSubTitle(cursor.getString(cursor.getColumnIndex(SUB_TITLE)));
                cachePath.setPackageName(cursor.getString(cursor.getColumnIndex(KEY_1)));
                cachePath.setPath(cursor.getString(cursor.getColumnIndex(KEY_2)));
                return cachePath;
            case CleanIgnoreBean.TYPE_RESIDUE:
                CleanIgnoreResidueBean residue = new CleanIgnoreResidueBean();
                residue.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                residue.setPkgNameString(cursor.getString(cursor.getColumnIndex(KEY_1)));
                return residue;
            case CleanIgnoreBean.TYPE_AD:
                CleanIgnoreAdBean ad = new CleanIgnoreAdBean();
                ad.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                ad.setPath(cursor.getString(cursor.getColumnIndex(KEY_1)));
                return ad;
            default:
                return null;
        }
    }

}

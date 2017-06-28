package com.jb.filemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.file.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 缓存数据数据库构建帮助类
 * 内置数据库版本升级后只需要修改 DATABASE_NAME和DB_VERSION 两个字段
 *
 * @author chenbenbin & kvan
 */
public class CacheDBHelper extends BaseDatabaseHelper {
    /**
     * 数据库名
     */
    public static final String DATABASE_NAME = "cache_6.db";
    /**
     * 数据库当前版本号(数据库每次升级该字段加1)
     */
    private final static int DB_VERSION = 1;

    private static final String DB_PATH = Environment.getDataDirectory()
            .getAbsolutePath()
            + "/data/"
            + Const.PACKAGE_NAME
            + "/databases/";
    private static final int BUFFER_CACHE = 1024;

    public CacheDBHelper(Context context) {
        super(context, DATABASE_NAME, DB_VERSION);

        copyDB(context);
        deleteOldDB();
    }

    /**
     * 删除旧库
     */
    private void deleteOldDB() {
        String dbPath = DB_PATH + "cache.db";
        String dbJournalPath = DB_PATH + "cache.db-journal";
        // 第一版因命名需特殊处理
        if (FileUtil.isFileExist(dbPath)) {
            FileUtil.deleteFile(dbPath);
        }
        if (FileUtil.isFileExist(dbJournalPath)) {
            FileUtil.deleteFile(dbJournalPath);
        }

        for (int i = 2; i < DB_VERSION; i++) {
            dbPath = DB_PATH + "cache_" + i + ".db";
            dbJournalPath = DB_PATH + "cache_" + i + ".db-journal";
            if (FileUtil.isFileExist(dbPath)) {
                FileUtil.deleteFile(dbPath);
            }
            if (FileUtil.isFileExist(dbJournalPath)) {
                FileUtil.deleteFile(dbJournalPath);
            }
        }
    }

    /**
     * 拷贝数据库
     */
    private void copyDB(Context context) {
        if (FileUtil.isFileExist(DB_PATH + CacheDBHelper.DATABASE_NAME)) {
           return;
        }
        Logger.d("kvan", "init database--copy database");
        // 若SQLite 数据库文件不存在，再检查一下 database 目录是否存在
        File dbPath = new File(DB_PATH);
        if (!dbPath.exists()) {
            dbPath.mkdir();
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            // 得到 raw 目录下我们实现准备好的 SQLite 数据库作为输入流
            is = context.getResources().openRawResource(R.raw.cache_6);
            os = new FileOutputStream(DB_PATH + CacheDBHelper.DATABASE_NAME);
            byte[] buffer = new byte[BUFFER_CACHE];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            // 关闭文件流
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            // 拷贝数据库失败
//			BatchIdNullReporter bp = new BatchIdNullReporter();
//			bp.setErrorMessage(e.getMessage());
//			bp.sendCopyDataBaseFailed();
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 因为有执行拷贝数据库，因此不启用创建数据库
        // db.beginTransaction();
        // try {
        // db.execSQL(ResidueTable.CREATE_TABLE);
        // db.execSQL(ResidueVersionTable.CREATE_TABLE);
        // mIsNewDB = true;
        // db.setTransactionSuccessful();
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // db.endTransaction();
        // }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static int getDbVersion() {
        return DB_VERSION;
    }

}
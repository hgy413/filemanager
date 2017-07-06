package com.jb.filemanager.function.search.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.jb.filemanager.database.provider.BaseDataProvider;
import com.jb.filemanager.function.search.modle.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-5.
 */

public class ExternalStorageProvider extends BaseDataProvider {

    private final static String TAG = "ExternalStorageProvider";

    public ExternalStorageProvider(Context context) {
        super(context);
        mDBHelper = new ExternalStorageDataBaseHelper(context);
    }

    /**
     * 插入数据
     */
    public void insertData(List<FileInfo> fileInfoList) {
        //线程同步
        synchronized (TAG) {
            if (fileInfoList == null || fileInfoList.isEmpty()) {
                return;
            }
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                //删除所有数据
                db.execSQL(ExternalStorageInfoTable.DELETE_TABLE);
                //将自增降到零
//                db.execSQL(ExternalStorageInfoTable.UPDATE_SEQUENCE);
                //批量插入数据 增加插入速度
                for (int i = 0; i < fileInfoList.size(); i++) {
                    FileInfo fileinfo = fileInfoList.get(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ExternalStorageInfoTable.FILE_NAME, fileinfo.mFileName);
                    contentValues.put(ExternalStorageInfoTable.FILE_MODIFY_TIME, fileinfo.mModificateTime);
                    contentValues.put(ExternalStorageInfoTable.FILE_TYPE, fileinfo.mFileType);
                    contentValues.put(ExternalStorageInfoTable.ABSOLUTE_PATH, fileinfo.mFileAbsolutePath);
                    db.insert(ExternalStorageInfoTable.TABLE_NAME, null, contentValues);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * 搜索数据
     * 此处参数有待扩展 先只支持一个参数
     */
    public ArrayList<FileInfo> searchData(String parameter) {
        //线程同步
        synchronized (TAG) {
            if (TextUtils.isEmpty(parameter)) {
                return null;
            }
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            //模糊搜索
            String likeSearch = "SELECT * FROM " + ExternalStorageInfoTable.TABLE_NAME + " WHERE " +
                    ExternalStorageInfoTable.FILE_NAME + " like '%" + parameter + "%'";
            ArrayList<FileInfo> mFileInfoList = new ArrayList<>();
            Cursor cursor = db.rawQuery(likeSearch, null);
            try {
                if (cursor != null) {
                    int indexName = cursor.getColumnIndex(ExternalStorageInfoTable.FILE_NAME);
                    int indexTime = cursor.getColumnIndex(ExternalStorageInfoTable.FILE_MODIFY_TIME);
                    int indexType = cursor.getColumnIndex(ExternalStorageInfoTable.FILE_TYPE);
                    int indexPath = cursor.getColumnIndex(ExternalStorageInfoTable.ABSOLUTE_PATH);
                    while (cursor.moveToNext()) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.mFileName = cursor.getString(indexName);
                        fileInfo.mModificateTime = cursor.getLong(indexTime);
                        fileInfo.mFileType = cursor.getInt(indexType);
                        fileInfo.mFileAbsolutePath = cursor.getString(indexPath);
                        //插入数据中
                        mFileInfoList.add(fileInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            //返回数组
            return mFileInfoList;
        }
    }

    /**
     * 查询是否存在数据
     * @return 返回是否存在数据 <b>true</b>代表存在数据, 反之不存在
     * */
    public boolean checkHasData() {
        synchronized (TAG) {
            int number = 0;
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            String CHECK_DATA = "SELECT COUNT (" + ExternalStorageInfoTable.ID + ") AS 'NUM' FROM" + ExternalStorageInfoTable.TABLE_NAME;
            Cursor cursor = db.rawQuery(CHECK_DATA, null);
            try {
                if (cursor != null) {
                    int indexNumber = cursor.getColumnIndex("NUM");
                    while (cursor.moveToNext()) {
                        number = cursor.getInt(indexNumber);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            return number != 0;
        }
    }
}

package com.jb.filemanager.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.database.BaseDatabaseHelper;
import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.table.CleanScanOvertimeTable;


/**
 * 垃圾扫描超时上传路径的数据库辅助类
 * @author chenbenbin
 */
public class CleanScanOvertimeDAO {
	private BaseDatabaseHelper mDBHelper;

	public CleanScanOvertimeDAO(Context context, BaseDatabaseHelper dbHelper) {
		mDBHelper = dbHelper;
	}

	/**
	 * 数据库是否存在该路径
	 */
	public boolean isPathExist(String path) {
		Cursor cursor = null;
		boolean isContain = false;
		try {
			cursor = mDBHelper.query(CleanScanOvertimeTable.TABLE_NAME, null,
					CleanScanOvertimeTable.PATH + "=?", new String[] { path },
					null);
			isContain = cursor != null && cursor.getCount() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return isContain;
	}

	/**
	 * 插入新路径
	 */
	public void insertPath(String path) {
		ContentValues value = new ContentValues();
		value.put(CleanScanOvertimeTable.PATH, path);
		try {
			mDBHelper.insert(CleanScanOvertimeTable.TABLE_NAME, value);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}

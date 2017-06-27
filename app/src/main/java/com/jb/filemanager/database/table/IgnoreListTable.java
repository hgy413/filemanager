package com.jb.filemanager.database.table;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.jb.filemanager.R;
import com.jb.filemanager.util.Logger;

import java.util.List;

/**
 * 白名单列表
 */
public class IgnoreListTable implements ITable {

	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "ignore_list_table";

	public static final String ID = "_id";

	public static final String COL_PACKAGE_NAME = "package_name";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS [" + TABLE_NAME + "] ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_PACKAGE_NAME + " TEXT, "
			+ "UNIQUE (" + COL_PACKAGE_NAME + ") ON CONFLICT REPLACE)";

	/**
	 * 默认的白名单列表
	 * @param ctx
	 */
	private static final String[] loadDefaultIgnoreList(Context ctx) {
		final Resources r = ctx.getResources();
		return r.getStringArray(R.array.default_ignore_list);
	}

	/**
	 * 加载所有的桌面app
	 * 
	 * @param context
	 * @return
	 */
	private static List<ResolveInfo> loadLauncherApps(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		PackageManager pkgMgr = context.getPackageManager();
		final int flag = PackageManager.GET_RESOLVED_FILTER | PackageManager.GET_INTENT_FILTERS
				| PackageManager.MATCH_DEFAULT_ONLY;
		return pkgMgr.queryIntentActivities(intent, flag);
	}

	/**
	 * 加载所有的输入法app
	 * @return
	 */
	private static List<InputMethodInfo> loadIMEApps(Context context) {
		InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return inputMgr.getEnabledInputMethodList();
	}

	/**
	 * 插入默认白名单列表
	 * @param ctx
	 * @param db
	 */
	public static void insertDefaultValue(Context ctx, SQLiteDatabase db) {
		String[] packageNames = loadDefaultIgnoreList(ctx);
		List<ResolveInfo> launcherApps = loadLauncherApps(ctx);
		List<InputMethodInfo> imeApps = loadIMEApps(ctx);
		ContentValues cv = new ContentValues();
		db.beginTransaction();
		try {
			for (String packageName : packageNames) {
				cv.put(COL_PACKAGE_NAME, packageName);
				db.insert(TABLE_NAME, null, cv);
			}
			if (launcherApps != null) {
				for (ResolveInfo ri : launcherApps) {
					cv.put(COL_PACKAGE_NAME, ri.activityInfo.packageName);
					db.insert(TABLE_NAME, null, cv);
				}
			}
			if (imeApps != null) {
				for (InputMethodInfo ime : imeApps) {
					cv.put(COL_PACKAGE_NAME, ime.getPackageName());
					db.insert(TABLE_NAME, null, cv);
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			if (Logger.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			db.endTransaction();
		}
	}
	
}

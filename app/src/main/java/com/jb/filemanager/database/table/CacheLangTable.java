package com.jb.filemanager.database.table;

import android.database.Cursor;

import com.jb.filemanager.database.tablebean.CacheLangBean;


/**
 * 缓存垃圾文件多语言数据表 / 暂定
 * 
 * @author chenbenbin & kvan
 * 
 */
public class CacheLangTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "lang_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 语言ID
	 */
	public static final String LANG_ID = "id";
	/**
	 * 缓存垃圾描述id
	 */
	public static final String LANG_TEXT_ID = "text_id";
	/**
	 * 语言代码
	 */
	public static final String LANG_LANG = "lang";
	/**
	 * 语言标题
	 */
	public static final String LANG_TITLE = "title";
	/**
	 * 缓存垃圾的描述
	 */
	public static final String LANG_DESCRIPTION = "description";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(LANG_ID + " INTEGER, ");
		b.append(LANG_TEXT_ID + " INTEGER, ");
		b.append(LANG_LANG + " TEXT, ");
		b.append(LANG_TITLE + " TEXT, ");
		b.append(LANG_DESCRIPTION + " TEXT)");
		CREATE_TABLE = b.toString();
	}

	public static CacheLangBean parseFromCursor(Cursor cursor) {
		CacheLangBean bean = new CacheLangBean();
		bean.setId(cursor.getString(cursor.getColumnIndex(LANG_ID)));
		bean.setTextId(cursor.getInt(cursor.getColumnIndex(LANG_TEXT_ID)));
		bean.setLang(cursor.getString(cursor.getColumnIndex(LANG_LANG)));
		bean.setTitle(cursor.getString(cursor.getColumnIndex(LANG_TITLE)));
		bean.setDescription(cursor.getString(cursor
				.getColumnIndex(LANG_DESCRIPTION)));

		// bean.setPath(decrypt(cursor.getString(cursor.getColumnIndex(PATH))));
		return bean;
	}

	// /**
	// * 数据库解密
	// */
	// private static String decrypt(String data) {
	// String result = CryptTool.decrypt(data, ZBoostApplication.PACKAGE_NAME);
	// return result != null ? result : data;
	// }

	// public static String parsePackageName(Cursor cursor) {
	// return cursor.getString(cursor.getColumnIndex(PACKAGE_NAME));
	// }
}
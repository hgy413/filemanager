package com.jb.filemanager.database.table;


import android.content.Context;
import android.database.Cursor;

import com.jb.filemanager.database.provider.CacheDataProvider;
import com.jb.filemanager.function.scanframe.bean.cachebean.subitem.SubAppCacheBean;

import java.io.UnsupportedEncodingException;

/**
 * 缓存文件数据库表格
 * 
 * @author chenbenbin & kvan
 * 
 */
public class CacheTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "cache_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 缓存垃圾路径
	 */
	public static final String CACHE_PATH = "path";
	/**
	 * 缓存垃圾描述id
	 */
	public static final String CACHE_TEXT_ID = "text_id";
	/**
	 * 缓存垃圾警告级别
	 */
	public static final String CACHE_WARN_LEVEL = "warn";
	/**
	 * 缓存垃圾时间筛选限制
	 */
	public static final String CACHE_DAYS_BEFORE = "days_before";
	/**
	 * 缓存垃圾类型
	 */
	public static final String CACHE_TYPE = "type";
	/**
	 * 缓存垃圾当前版本
	 */
	public static final String CACHE_VERSION = "version";
	/**
	 * 此垃圾隶属的应用包名
	 */
	public static final String APP_PKG_NAME = "pkg_name";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(CACHE_PATH + " TEXT, ");
		b.append(CACHE_TEXT_ID + " INTEGER, ");
		b.append(CACHE_WARN_LEVEL + " INTEGER, ");
		b.append(CACHE_DAYS_BEFORE + " INTEGER, ");
		b.append(CACHE_TYPE + " INTEGER, ");
		b.append(CACHE_VERSION + " INTEGER, ");
		b.append(APP_PKG_NAME + " TEXT)");
		CREATE_TABLE = b.toString();
	}

	public static SubAppCacheBean parseFromCursor(Context context, Cursor cursor) {
		SubAppCacheBean bean = new SubAppCacheBean();
		// bean.setPath(cursor.getString(cursor.getColumnIndex(CACHE_PATH)));
		byte[] b = cursor.getBlob(cursor.getColumnIndex(CACHE_PATH));
		try {
			byte[] decryptedData = null;
			decryptedData = CacheDataProvider.decrypt(CacheDataProvider.getInstance(context).getKey(), b);
			bean.setPath(new String(decryptedData, "UTF-8"));
			bean.setDBKey(bean.getPath());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		bean.setTextId(cursor.getInt(cursor.getColumnIndex(CACHE_TEXT_ID)));
		bean.setWarnLv(cursor.getInt(cursor.getColumnIndex(CACHE_WARN_LEVEL)));
		bean.setDayBefore(cursor.getInt(cursor.getColumnIndex(CACHE_DAYS_BEFORE)));
		bean.setContentType(cursor.getInt(cursor.getColumnIndex(CACHE_TYPE)));
		bean.setVersion(cursor.getInt(cursor.getColumnIndex(CACHE_VERSION)));
		bean.setPackageName(cursor.getString(cursor.getColumnIndex(APP_PKG_NAME)));

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
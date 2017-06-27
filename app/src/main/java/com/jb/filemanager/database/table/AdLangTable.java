package com.jb.filemanager.database.table;

/**
 * 广告垃圾多语言数据库表格
 * 
 * @author chenbenbin & kvan
 * 
 */
public class AdLangTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "ad_lang_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 广告id
	 */
	public static final String AD_ID = "ad_id";
	/**
	 * 语言代码
	 */
	public static final String LANG_CODE = "lang_code";
	/**
	 * 广告标题
	 */
	public static final String AD_TITLE = "title";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(AD_ID + " INTEGER, ");
		b.append(LANG_CODE + " TEXT, ");
		b.append(AD_TITLE + " TEXT)");
		CREATE_TABLE = b.toString();
	}
}
package com.jb.filemanager.database.table;

/**
 * 广告垃圾路径数据库表格
 * 
 * @author chenbenbin & kvan
 * 
 */
public class AdPathTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "ad_path_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 广告id
	 */
	public static final String AD_ID = "ad_id";
	/**
	 * 路径
	 */
	public static final String PATH = "path";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(AD_ID + " INGEGER, ");
		b.append(PATH + " TEXT)");
		CREATE_TABLE = b.toString();
	}
}
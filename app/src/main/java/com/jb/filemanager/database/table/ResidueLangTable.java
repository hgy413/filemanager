package com.jb.filemanager.database.table;

/**
 * 残留文件多语言数据库表格
 * 
 * @author chenbenbin & kvan
 * 
 */
public class ResidueLangTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "residue_lang_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 路径id
	 */
	public static final String PATH_ID = "path_id";
	/**
	 * 语言代码
	 */
	public static final String LANG_CODE = "lang_code";
	/**
	 * 应用包名
	 */
	public static final String PACKAGE_NAME = "pkg_name";
	/**
	 * 应用名
	 */
	public static final String APP_NAME = "app_name";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(PATH_ID + " TEXT, ");
		b.append(PACKAGE_NAME + " TEXT, ");
		b.append(LANG_CODE + " TEXT, ");
		b.append(APP_NAME + " TEXT)");
		CREATE_TABLE = b.toString();
	}
}
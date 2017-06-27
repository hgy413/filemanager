package com.jb.filemanager.database.table;

/**
 * 辅助加速白名单列表, 对于白名单的应用不采用辅助加速处理<br>
 */
public class AccessibilityIgnoreListTable implements ITable {

	public static final String ID = "_id";

	public static final String PACKAGE_NAME = "package_name";

	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "accessibility_ignore_list_table";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ["
			+ TABLE_NAME + "] (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PACKAGE_NAME + " TEXT, " + "UNIQUE (" + PACKAGE_NAME
			+ ") ON CONFLICT REPLACE)";

}

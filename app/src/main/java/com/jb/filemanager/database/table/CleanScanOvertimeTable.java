package com.jb.filemanager.database.table;

/**
 * 垃圾扫描超时上传路径的数据库表
 * @author chenbenbin
 */
public class CleanScanOvertimeTable implements ITable {
	/**
	 * 数据库ID<br>
	 * 值类型: int
	 */
	public static final String ID = "_id";

	/**
	 * 路径<br>
	 * 值类型: string
	 */
	public static final String PATH = "path";

	// ============================================================================//
	/**
	 * 表名
	 */
	public static final String TABLE_NAME = "clean_scan_overtime_table";

	/**
	 * 创建表
	 */
	public static final String CREATE_TABLE;

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (" + ID + " INTEGER PRIMARY KEY, ");
		b.append(PATH + " TEXT)");
		CREATE_TABLE = b.toString();
	}

}
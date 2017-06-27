package com.jb.filemanager.database.table;

/**
 * 残留文件路径数据库表格
 * 
 * @author chenbenbin & kvan
 * 
 */
public class ResiduePathTable implements ITable {
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "residue_path_table";
	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE;
	/**
	 * 路径id
	 */
	public static final String PATH_ID = "path_id";
	/**
	 * 路径
	 */
	public static final String PATH = "path";
	/**
	 * 应用包名
	 */
	public static final String PACKAGE_NAME = "pkg_name";

	static {
		StringBuilder b = new StringBuilder();
		b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
		b.append(" (_id INTEGER PRIMARY KEY, ");
		b.append(PATH_ID + " TEXT, ");
		b.append(PACKAGE_NAME + " TEXT, ");
		b.append(PATH + " TEXT)");
		CREATE_TABLE = b.toString();
	}
	// public static CleanResiduePathBean parseFromCursor(Cursor cursor) {
	// CleanResiduePathBean bean = new CleanResiduePathBean();
	// bean.setPathId(cursor.getString(cursor.getColumnIndex(PATH_ID)));
	// bean.setPkgName(cursor.getString(cursor.getColumnIndex(PACKAGE_NAME)));
	// // bean.setPath(decrypt(cursor.getString(cursor.getColumnIndex(PATH))));
	// return bean;
	// }
}
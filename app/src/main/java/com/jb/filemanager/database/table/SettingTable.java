package com.jb.filemanager.database.table;

/**
 * 存储所有元素的详细信息(快捷方式,应用程序,文件夹)
 * 
 * 类名称：SettingTable
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年8月28日 下午3:08:29
 * 修改备注：
 * @version 1.0.0
 *
 */
public interface SettingTable extends ITable {
	
	public static final int INDEX_KEY = 1;
	public static final int INDEX_VALUE = 2;
	
	/**
	 * 表名
	 */
	String TABLE_NAME = "settings";
	/**
	 * 键
	 */
	String KEY = "key";
	/*
	 * 值
	 */
	String VALUE = "value";
	
	String SQL = CREATE_PRIMARY_KEY_AUTO_INCREMENT
			 		+ KEY + DATA_TYPE_TEXT_TEXT
			 		+ VALUE + DATA_TYPE_TEXT;

	String TABLE_SQL = String.format(CREATE_TABLE_SQL, TABLE_NAME, SQL);
	
}

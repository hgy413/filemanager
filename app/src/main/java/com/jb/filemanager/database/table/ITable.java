package com.jb.filemanager.database.table;


import com.jb.filemanager.database.DatabaseUtils;

/**
 * 创建表所需的常量字段
 * 
 * 类名称：TableConsf
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年8月25日 上午11:16:05
 * 修改备注：
 * @version 1.0.0
 *
 */
public interface ITable {
	
	String SQL_SYMBOL_COMMA = ", ";
	
	String SQL_SYMBOL_SPACE = " ";
	
	/***************************建表常用语句*************************************/
	
	String PRIMARY_KEY_SQL = "PRIMARY KEY (%s)";
	
	String CREATE_TABLE_SQL = "create table %s(%s)";
	
	/**
	 * 自曾主键
	 */
	String CREATE_PRIMARY_KEY_AUTO_INCREMENT = "_id integer PRIMARY KEY autoincrement" + SQL_SYMBOL_COMMA;
	
	
	
	/****************************数据类型**************************************/
	
	String DATA_TYPE_BLOB = SQL_SYMBOL_SPACE + DatabaseUtils.TYPE_BLOB;
	
	String DATA_TYPE_TEXT = SQL_SYMBOL_SPACE + DatabaseUtils.TYPE_TEXT;
	
	String DATA_TYPE_NUMERIC = SQL_SYMBOL_SPACE + DatabaseUtils.TYPE_NUMERIC;
	
	String DATA_TYPE_TEXT_TEXT = DATA_TYPE_TEXT + SQL_SYMBOL_COMMA;
	
	String DATA_TYPE_BLOB_TEXT = DATA_TYPE_BLOB + SQL_SYMBOL_COMMA;
	
	String DATA_TYPE_NUMERIC_TEXT = DATA_TYPE_NUMERIC + SQL_SYMBOL_COMMA;
	
}

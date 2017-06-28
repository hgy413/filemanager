package com.jb.filemanager.database.table;
/**
 * 场景项表
 *
 */
public class LockerSceneItemTable {
	
	public static final String SCENE_ID = "scene_id";
	
	public static final String COMPONENTNAME = "componentname";
	
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "applock_sense_item";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
			+ SCENE_ID + " numeric,"
			+ COMPONENTNAME + " text"
			+ ")";
}

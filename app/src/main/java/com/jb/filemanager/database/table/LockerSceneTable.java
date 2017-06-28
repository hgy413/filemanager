package com.jb.filemanager.database.table;

/**
 * 场景表
 */
public class LockerSceneTable {

	public static final String COMPONENTNAME = "componentname";
	
	public static final String ID = "id";
	
	public static final String TITLE = "title";

	public static final String ACTION = "action";

	public static final String HAS_DELETE = "has_delete";

	public static final String HAS_EDITER = "has_editer";

	public static final String POSITION = "position";

	public static final String ISALLLOCKED = "isAllLocked";

	public static final String ISALLUNLOCKED = "isAllUnlocked";

	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "applock_sense";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
			+ ID + " numeric, "
			+ TITLE + " text, "
			+ HAS_DELETE + " numeric, "
			+ HAS_EDITER + " numeric, "
			+ ISALLLOCKED + " numeric, "
			+ ISALLUNLOCKED + " numeric, "
			+ ACTION + " text, "
			+ POSITION + " numeric"
			+ ")";
}

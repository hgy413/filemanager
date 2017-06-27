package com.jb.filemanager.database.provider;
/**
 * 重复照片信息表
 * @author zhanghuijun
 *
 */
public class DuplicatePhotoTable {
	public static final String ID = "_id";
	/**
	 * 路径
	 */
	public static final String COL_PATH = "path";
	/**
	 * 文件创建时间
	 */
//	public static final String COL_CREATE_TIME = "create_time";
	/**
	 * 拍摄时间
	 */
	public static final String COL_PHOTO_TIME = "photo_time";
	/**
	 * 拍摄时间戳
	 */
	public static final String COL_PHOTO_TIMESTAMP = "photo_timestamp";
	/**
	 * 图片宽
	 */
	public static final String COL_PHOTO_WIDTH = "photo_width";
	/**
	 * 图片高
	 */
	public static final String COL_PHOTO_HEIGHT = "photo_height";
	/**
	 * 图片大小
	 */
	public static final String COL_PHOTO_SIZE = "photo_size";
	/**
	 * 闪光灯信息
	 */
	public static final String COL_FLASH = "flash";
	/**
	 * 照片方向
	 */
	public static final String COL_ORIENTATION = "orientation";
	/**
	 * 白平衡信息
	 */
	public static final String COL_WHITE_BALANCE = "white_balance";
	/**
	 * 第几组
	 */
	public static final String COL_ROW_INDEX = "row_index";
	/**
	 * 是否可以展示
	 */
	public static final String COL_CAN_SHOW = "can_show";
	/**
	 * PHash
	 */
	public static final String COL_PHOTO_PHASH = "photo_phash";
	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "duplicate_photo";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS [" + TABLE_NAME + "] ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
			COL_PATH + " TEXT," + 
			COL_PHOTO_TIME + " TEXT," +
			COL_PHOTO_TIMESTAMP + " NUMERIC DEFAULT 0," +
			COL_PHOTO_WIDTH + " NUMERIC DEFAULT 0," +
			COL_PHOTO_HEIGHT + " NUMERIC DEFAULT 0," +
			COL_PHOTO_SIZE + " NUMERIC DEFAULT 0," +
			COL_FLASH + " TEXT," +
			COL_ORIENTATION + " TEXT," +
			COL_WHITE_BALANCE + " TEXT," +
			COL_ROW_INDEX + " NUMERIC DEFAULT 0," +
			COL_CAN_SHOW + " NUMERIC DEFAULT 1," +
			COL_PHOTO_PHASH + " TEXT," +
			"UNIQUE (" + COL_PATH + ") ON CONFLICT REPLACE)";
}

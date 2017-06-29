package com.jb.filemanager.database.table;

import com.jb.filemanager.manager.spm.IPreferencesIds;

/**
 * @description 用于保存手机中所有App的权限
 * @author: nieyh
 * @date: 2017-2-8 15:16
 */
public class AppPermissionsTable {
    /**
     * 数据库ID<br>
     * 值类型: int
     */
    public static final String ID = "_id";

    /**
     * 是否第一第更新
     */
    public static final String IS_FIRST_UPDATE = "is_first_update";

    /**
     * 应用包名
     */
    public static final String PKG_NAME = "pkg_name";
    /**
     * 权限列表，多个用逗号隔开
     */
    public static final String PERMISSIONS = "permissions";
    /**
     * 这个App有没有在退出时显示过弹窗
     */
    public static final String HAS_APP_EXIT_SHOWED = "has_app_exit_showed";

    // ============================================================================//
    /**
     * 表名
     */
    public static final String TABLE_NAME = IPreferencesIds.APP_NAME + "app_permissions";

    /**
     * 创建表
     */
    public static final String CREATE_TABLE;

    static {
        StringBuilder b = new StringBuilder();
        b.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME);
        b.append(" (" + ID + " INTEGER PRIMARY KEY, ");
        b.append(PKG_NAME + " TEXT, ");
        b.append(PERMISSIONS + " TEXT, ");
        b.append(HAS_APP_EXIT_SHOWED + " INTEGER default -1, ");
        b.append(IS_FIRST_UPDATE + " INTEGER default -1)");
        CREATE_TABLE = b.toString();
    }
}

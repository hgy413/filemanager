package com.jb.filemanager.database;

/**
 * 数据库异常类(处理数据库操作异常)
 *
 * 类名称：DatabaseException
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年8月25日 上午10:50:22
 * 修改备注：
 * @version 1.0.0
 *
 */
public class DatabaseException extends Exception {

    /**
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）
     *
     * @since 1.0.0
     */

    private static final long serialVersionUID = 1L;

    public DatabaseException(Exception e) {
        super(e);
    }

    public DatabaseException(String msg) {
        super(msg);
    }

}

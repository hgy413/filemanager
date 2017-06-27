package com.jb.filemanager.database.params;

import android.content.ContentValues;

/**
 * SQL更新参数
 *
 * 类名称：UpdateParams
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2015年1月6日 下午3:22:59
 * 修改备注：
 * @version 1.0.0
 *
 */
public class UpdateParams extends InsertParams {

    private String mSelection;
    private String[] mWhereArgs;

    public UpdateParams(String mTableName, ContentValues mContentValues, String mSelection, String[] whereArgs) {
        super(mTableName, mContentValues);
        this.mSelection = mSelection;
        this.mWhereArgs = whereArgs;
    }

    public UpdateParams(String mTableName, ContentValues mContentValues, String mSelection) {
        this(mTableName, mContentValues, mSelection, null);
    }

    public String getSelection() {
        return mSelection;
    }

    public String[] getWhereArgs() {
        return mWhereArgs;
    }

    @Override
    public String toString() {
        return super.toString() + " , mSelection : " + mSelection + " , mWhereArgs : " + mWhereArgs;
    }
}

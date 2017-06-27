package com.jb.filemanager.database.params;

import android.content.ContentValues;

/**
 * SQL删除参数
 *
 * 类描述：
 *
 */
public class DeleteParams extends InsertParams {

    private String mSelection;
    private String[] mWhereArgs;

    public DeleteParams(String mTableName, ContentValues mContentValues, String mSelection, String[] whereArgs) {
        super(mTableName, mContentValues);
        this.mSelection = mSelection;
        this.mWhereArgs = whereArgs;
    }

    public DeleteParams(String mTableName, ContentValues mContentValues, String mSelection) {
        this(mTableName, mContentValues, mSelection, null);
    }

    public DeleteParams(String mTableName, String mSelection, String[] whereArgs) {
        this(mTableName, null, mSelection, whereArgs);
    }

    public String getSelection() {
        return mSelection;
    }

    public String[] getWhereArgs() {
        return mWhereArgs;
    }

    @Override
    public String toString() {
        return super.toString() + " , mSelection : " + mSelection;
    }
}

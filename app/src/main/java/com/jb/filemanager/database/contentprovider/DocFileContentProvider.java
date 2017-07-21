package com.jb.filemanager.database.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jb.filemanager.database.DatabaseException;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.table.DocFileTable;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/4/7 16:47
 */

public class DocFileContentProvider extends ContentProvider {
    private static final String AUTHROITY = "com.jb.filemanager.database.contentprovider.DocFileContentProvider";
    private static final String TABLE_NAME = DocFileTable.TABLE_NAME;

    private static final int CODE_NO_PARAM = 1;
    private static final int CODE_PARAM = 2;

    private DatabaseHelper mDatabaseHelper;
    private static UriMatcher sMatcher;

    static{
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sMatcher.addURI(AUTHROITY, TABLE_NAME, CODE_NO_PARAM);
        sMatcher.addURI(AUTHROITY, TABLE_NAME + "/#", CODE_PARAM);
    }


    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                return "vnd.android.cursor.dir/" + TABLE_NAME;
            case CODE_PARAM:
                return "vnd.android.cursor.item/" + TABLE_NAME;
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                return mDatabaseHelper.query(TABLE_NAME, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                return mDatabaseHelper.query(TABLE_NAME, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                long id = 0;
                try {
                    id = mDatabaseHelper.insert(TABLE_NAME, values);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                int count = 0;
                try {
                    count = mDatabaseHelper.delete(TABLE_NAME, selection,selectionArgs);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return count;
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sMatcher.match(uri)) {
            case CODE_NO_PARAM:
                int id = -1;
                try {
                    id = mDatabaseHelper.update(TABLE_NAME, values, selection, selectionArgs);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return id;
            default:
                throw new IllegalArgumentException("this is unknown uri:" + uri);
        }
    }
}

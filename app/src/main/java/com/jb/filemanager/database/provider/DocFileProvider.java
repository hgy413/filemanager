package com.jb.filemanager.database.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.table.DocFileTable;
import com.jb.filemanager.function.docmanager.DocChildBean;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/21 18:22
 */

public class DocFileProvider extends BaseDataProvider {
    private static final String TAG = "DocFileContentProvider";
    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String TXT = "txt";
    public static final String PDF = "pdf";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";

    private static DocFileProvider sInstance;
    private Uri mUri = Uri.parse("content://com.jb.filemanager.database.contentprovider.DocFileContentProvider/" + DocFileTable.TABLE_NAME);
    private static ContentResolver sContentResolver = TheApplication.getAppContext().getContentResolver();

    private DocFileProvider(Context context) {
        super(context);
        mDBHelper = new DatabaseHelper(context);
    }

    public static synchronized DocFileProvider getInstance() {
        if (sInstance == null) {
            sInstance = new DocFileProvider(TheApplication.getAppContext());
        }
        return sInstance;
    }

    public ArrayList<DocChildBean> queryDocList(String type) {
        ArrayList<DocChildBean> childList = new ArrayList<>();
        String[] projection = new String[]{DocFileTable.DOC_ID,
                DocFileTable.DOC_NAME,
                DocFileTable.DOC_PATH,
                DocFileTable.DOC_SIZE,
                DocFileTable.DOC_TYPE,
                DocFileTable.DOC_ADDED_DATE,
                DocFileTable.DOC_MODIFY_DATE
        };
        Cursor cursor = sContentResolver.query(
                mUri,
                projection,
                DocFileTable.DOC_TYPE + " like ?",
                new String[]{type},
                null);

        int fileType = 0;
        if (DOC.equals(type) || DOCX.equals(type)) {
            fileType = DocChildBean.TYPE_DOC;
        } else if (TXT.equals(type)) {
            fileType = DocChildBean.TYPE_TXT;
        } else if (PDF.equals(type)) {
            fileType = DocChildBean.TYPE_PDF;
        } else if (XLS.equals(type) || XLSX.equals(type)) {
            fileType = DocChildBean.TYPE_XLS;
        } else if (PPT.equals(type) || PPTX.equals(type)) {
            fileType = DocChildBean.TYPE_PPT;
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_ID);
                int nameIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_NAME);
                int dataIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_PATH);
                int sizeIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_SIZE);
                int typeIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_TYPE);
                int dataAddedIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_ADDED_DATE);
                int dataModifyIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_MODIFY_DATE);
                do {
                    String id = cursor.getString(idIndex);
                    String name = cursor.getString(nameIndex);
                    String path = cursor.getString(dataIndex);
//                    String type = cursor.getString(typeIndex);
                    String size = cursor.getString(sizeIndex);
                    long dataAdded = cursor.getLong(dataAddedIndex);
                    long dateModify = cursor.getLong(dataModifyIndex);
                    DocChildBean childBean = new DocChildBean();
                    childBean.mDocId = id;
                    childBean.mDocPath = path;
                    childBean.mDocSize = size;
                    childBean.mDocName = name;
                    childBean.mFileType = fileType;
                    childBean.mAddDate = dataAdded;
                    childBean.mModifyDate = dateModify;
                    childList.add(childBean);
                    Logger.d(TAG, childBean.mDocPath + "   " + childBean.mDocSize + "   " + childBean.mDocName + "   " +
                            childBean.mAddDate + "   " + childBean.mModifyDate + "   " + id);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return childList;
    }

    public DocChildBean queryItem(String filePath) {
        DocChildBean childBean = null;
        String[] projection = new String[]{DocFileTable.DOC_ID,
                DocFileTable.DOC_NAME,
                DocFileTable.DOC_PATH,
                DocFileTable.DOC_SIZE,
                DocFileTable.DOC_TYPE,
                DocFileTable.DOC_ADDED_DATE,
                DocFileTable.DOC_MODIFY_DATE
        };
        Cursor cursor = sContentResolver.query(
                mUri,
                projection,
                DocFileTable.DOC_PATH + " like ?",
                new String[]{filePath},
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_ID);
                int nameIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_NAME);
                int dataIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_PATH);
                int sizeIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_SIZE);
                int typeIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_TYPE);
                int dataAddedIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_ADDED_DATE);
                int dataModifyIndex = cursor
                        .getColumnIndex(DocFileTable.DOC_MODIFY_DATE);
                do {
                    String id = cursor.getString(idIndex);
                    String name = cursor.getString(nameIndex);
                    String path = cursor.getString(dataIndex);
//                    String type = cursor.getString(typeIndex);
                    String size = cursor.getString(sizeIndex);
                    long dataAdded = cursor.getLong(dataAddedIndex);
                    long dateModify = cursor.getLong(dataModifyIndex);
                    childBean = new DocChildBean();
                    childBean.mDocId = id;
                    childBean.mDocPath = path;
                    childBean.mDocSize = size;
                    childBean.mDocName = name;
                    childBean.mAddDate = dataAdded;
                    childBean.mModifyDate = dateModify;

                    Logger.d(TAG, childBean.mDocPath + "   " + childBean.mDocSize + "   " + childBean.mDocName + "   " +
                            childBean.mAddDate + "   " + childBean.mModifyDate + "   " + id);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void insertDocItem(DocChildBean childBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DocFileTable.DOC_NAME, childBean.mDocName);
        contentValues.put(DocFileTable.DOC_PATH, childBean.mDocPath);
        contentValues.put(DocFileTable.DOC_SIZE, childBean.mDocSize);
        int dot = childBean.mDocName.lastIndexOf(".");
        String type = childBean.mDocName.substring(dot + 1);
        contentValues.put(DocFileTable.DOC_TYPE, type);
        contentValues.put(DocFileTable.DOC_MODIFY_DATE, childBean.mModifyDate);
        contentValues.put(DocFileTable.DOC_ADDED_DATE, childBean.mAddDate);
        sContentResolver.insert(mUri, contentValues);
    }
    public void insertDocList(ArrayList<DocChildBean> itemList) {
        if (itemList == null || itemList.size() == 0) {
            return;
        }
        for (DocChildBean childBean:itemList) {
            insertDocItem(childBean);
        }
    }

    public void updateDocFile(String oldFile, String newFile) {
        DocChildBean childBean = queryItem(oldFile);
        if (childBean == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns._ID, childBean.mDocId);
        contentValues.put(MediaStore.Files.FileColumns.DATA, newFile);
        contentValues.put(MediaStore.Files.FileColumns.SIZE, childBean.mDocSize);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, childBean.mAddDate);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, childBean.mModifyDate);

        sContentResolver.update(mUri, contentValues,
                DocFileTable.DOC_ID + " like ?", new String[]{childBean.mDocId});
    }

    public void deleteDocFile(String oldFile) {
        int delete = sContentResolver.delete(
                mUri,
                DocFileTable.DOC_PATH + " like ?",
                new String[]{oldFile});
        Logger.d(TAG, "delete number" + delete + "   " + oldFile);
    }
}

package com.jb.filemanager.database.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.DatabaseHelper;
import com.jb.filemanager.database.table.DocFileTable;
import com.jb.filemanager.function.docmanager.DocChildBean;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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

        Iterator<DocChildBean> iterator = childList.iterator();
        while (iterator.hasNext()) {
            DocChildBean next = iterator.next();
            File file = new File(next.mDocPath);
            if (!file.exists()) {
                deleteDocFile(next.mDocPath);
                iterator.remove();
            }
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
                    String type = cursor.getString(typeIndex);
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
                            childBean.mAddDate + "   " + type + "   " + id);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return childBean;
    }

    public void insertDocItem(DocChildBean childBean) {
        if (childBean == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DocFileTable.DOC_NAME, childBean.mDocName);
        contentValues.put(DocFileTable.DOC_PATH, childBean.mDocPath);
        contentValues.put(DocFileTable.DOC_SIZE, childBean.mDocSize);
        int dot = childBean.mDocName.lastIndexOf(".");
        String type = childBean.mDocName.substring(dot + 1);
        contentValues.put(DocFileTable.DOC_TYPE, type.toLowerCase());
        contentValues.put(DocFileTable.DOC_MODIFY_DATE, childBean.mModifyDate);
        contentValues.put(DocFileTable.DOC_ADDED_DATE, childBean.mAddDate);
        DocChildBean queryItem = queryItem(childBean.mDocPath);
        if (queryItem == null) {//插入新的数据
            sContentResolver.insert(mUri, contentValues);
        } else {//之前就有的数据  那么更新数据
            updateDocFile(queryItem, childBean);
        }
    }
    public void insertDocList(ArrayList<DocChildBean> itemList) {
        if (itemList == null || itemList.size() == 0) {
            return;
        }
        for (DocChildBean childBean:itemList) {
            insertDocItem(childBean);
        }
    }

    public void updateDocFilePath(String oldFile, String newFile) {
        DocChildBean childBean = queryItem(oldFile);
        if (childBean == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DocFileTable.DOC_ID, childBean.mDocId);
        contentValues.put(DocFileTable.DOC_PATH, newFile);
        contentValues.put(DocFileTable.DOC_SIZE, childBean.mDocSize);
        contentValues.put(DocFileTable.DOC_NAME, childBean.mDocName);
        int dot = childBean.mDocName.lastIndexOf(".");
        String type = childBean.mDocName.substring(dot + 1);
        contentValues.put(DocFileTable.DOC_TYPE, type.toLowerCase());
        contentValues.put(DocFileTable.DOC_ADDED_DATE, childBean.mAddDate);
        contentValues.put(DocFileTable.DOC_MODIFY_DATE, childBean.mModifyDate);

        sContentResolver.update(mUri, contentValues,
                DocFileTable.DOC_ID + " like ?", new String[]{childBean.mDocId});

        Logger.d(TAG, "update path" + newFile + "   " + childBean.mDocSize + "   " + childBean.mDocName + "   " +
                childBean.mAddDate + "   " + type + "   " + childBean.mDocId);
    }

    public void updateDocFileName(String oldFile, String newFile) {
        DocChildBean childBean = queryItem(oldFile);
        if (childBean == null) {
            return;
        }

        int dot = newFile.lastIndexOf("/");
        String name = newFile.substring(dot + 1);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DocFileTable.DOC_ID, childBean.mDocId);
        contentValues.put(DocFileTable.DOC_PATH, newFile);
        contentValues.put(DocFileTable.DOC_SIZE, childBean.mDocSize);
        contentValues.put(DocFileTable.DOC_NAME, name);
        int dot2 = name.lastIndexOf(".");
        String type = name.substring(dot2 + 1);
        contentValues.put(DocFileTable.DOC_TYPE, type.toLowerCase());
        contentValues.put(DocFileTable.DOC_ADDED_DATE, childBean.mAddDate);
        contentValues.put(DocFileTable.DOC_MODIFY_DATE, childBean.mModifyDate);

        sContentResolver.update(mUri, contentValues,
                DocFileTable.DOC_ID + " like ?", new String[]{childBean.mDocId});

        Logger.d(TAG, "update name" + newFile + "   " + childBean.mDocSize + "   " + name + "   " +
                childBean.mAddDate + "   " + type + "   " + childBean.mDocId);
    }

    public void updateDocFile(DocChildBean oldFile, DocChildBean newFile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DocFileTable.DOC_ID, oldFile.mDocId);
        contentValues.put(DocFileTable.DOC_PATH, newFile.mDocPath);
        contentValues.put(DocFileTable.DOC_SIZE, newFile.mDocSize);
        contentValues.put(DocFileTable.DOC_NAME, newFile.mDocName);
        int dot = newFile.mDocName.lastIndexOf(".");
        String type = newFile.mDocName.substring(dot + 1);
        contentValues.put(DocFileTable.DOC_TYPE, type.toLowerCase());
        contentValues.put(DocFileTable.DOC_ADDED_DATE, newFile.mAddDate);
        contentValues.put(DocFileTable.DOC_MODIFY_DATE, newFile.mModifyDate);

        sContentResolver.update(mUri, contentValues,
                DocFileTable.DOC_ID + " like ?", new String[]{newFile.mDocId});

        Logger.d(TAG, "update File" + newFile.mDocPath + "   " + newFile.mDocSize + "   " + newFile.mDocName + "   " +
                newFile.mAddDate + "   " + type + "   " + oldFile.mDocId);
    }

    public void deleteDocFile(String oldFile) {
        int delete = sContentResolver.delete(
                mUri,
                DocFileTable.DOC_PATH + " like ?",
                new String[]{oldFile});
        Logger.d(TAG, "delete number" + delete + "   " + oldFile);
    }
}

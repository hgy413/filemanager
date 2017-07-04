package com.jb.filemanager.function.docmanager;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 10:50
 */

public class DocManagerSupport implements DocManagerContract.Support {

    private static final String TAG = "DocManagerSupport";

    @Override
    public List<DocChildBean> getDocFileInfo() {
//        return queryFiles("doc");
        List<DocChildBean> doc = queryFiles("doc");
        List<DocChildBean> docX = queryFiles("docx");
        doc.addAll(docX);
        return doc;
    }

    @Override
    public List<DocChildBean> getTextFileInfo() {
        return queryFiles("txt");
    }

    @Override
    public List<DocChildBean> getPdfFileInfo() {
        return queryFiles("pdf");
    }

    private List<DocChildBean> queryFiles(String type) {
        List<DocChildBean> childList = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED
        };
        Cursor cursor = TheApplication.getAppContext().getContentResolver().query(
                Uri.parse("content://media/external/file"),
                projection,
                MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%." + type},
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns._ID);
                int dataIndex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int sizeIndex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                int dataAddedIndex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
                int dataModifyIndex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
                do {
                    String id = cursor.getString(idIndex);
                    String path = cursor.getString(dataIndex);
                    String size = cursor.getString(sizeIndex);
                    long dataAdded = cursor.getLong(dataAddedIndex);
                    long dateModify = cursor.getLong(dataModifyIndex);
                    int dot = path.lastIndexOf("/");
                    String name = path.substring(dot + 1);
                    DocChildBean childBean = new DocChildBean();
                    childBean.mDocPath = path;
                    childBean.mDocSize = size;
                    childBean.mDocName = name;
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
}

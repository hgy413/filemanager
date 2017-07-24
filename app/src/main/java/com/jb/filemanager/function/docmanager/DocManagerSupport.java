package com.jb.filemanager.function.docmanager;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.DocFileProvider;
import com.jb.filemanager.util.Logger;

import java.util.ArrayList;

import static com.jb.filemanager.R.string.delete;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 10:50
 */

public class DocManagerSupport implements DocManagerContract.Support {

    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String TXT = "txt";
    public static final String PDF = "pdf";
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";

    public static final String DOC_MIME_TYPE = "application/msword";
    public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS_MIME_TYPE = "application/vnd.ms-excel";
    public static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPT_MIME_TYPE = "application/vnd.ms-powerpoint";
    public static final String PPTX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String TXT_MIME_TYPE = "text/plain";
    public static final String PDF_MIME_TYPE = "application/pdf";
    private static final String TAG = "DocManagerSupport";

    @Override
    public ArrayList<DocChildBean> getDocFileInfo() {
//        return queryFiles("doc");
        ArrayList<DocChildBean> doc = queryFiles(DOC);
        ArrayList<DocChildBean> docX = queryFiles(DOCX);
        doc.addAll(docX);
        return doc;
    }

    @Override
    public ArrayList<DocChildBean> getTextFileInfo() {
        return queryFiles(TXT);
    }

    @Override
    public ArrayList<DocChildBean> getPdfFileInfo() {
        return queryFiles(PDF);
    }

    private ArrayList<DocChildBean> queryFiles(String type) {
        ArrayList<DocChildBean> childList = new ArrayList<>();
        childList.addAll(DocFileProvider.getInstance().queryDocList(type));
        return childList;
    }

    @Override
    public void handleFileDelete(String docPath) {
        DocFileProvider.getInstance().deleteDocFile(docPath);
        Logger.d(TAG, "delete number" + delete + "   " + docPath);
    }

    @Override
    public void handleFileCopy(String oldFile, String newFile) {
        DocChildBean childBean = DocFileProvider.getInstance().queryItem(oldFile);
        childBean.mDocPath = newFile;
        DocFileProvider.getInstance().insertDocItem(childBean);
    }

    @Override
    public void handleFileCut(String oldFile, String newFile) {
        DocFileProvider.getInstance().updateDocFilePath(oldFile, newFile);
    }

    @Override
    public void handleFileRename(String oldFile, String newFile) {
        DocFileProvider.getInstance().updateDocFileName(oldFile,newFile);
    }

    public DocChildBean handleQueryFile(String filePath) {
        DocChildBean childBean = null;
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
                new String[]{filePath},
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
        return childBean;
    }
}

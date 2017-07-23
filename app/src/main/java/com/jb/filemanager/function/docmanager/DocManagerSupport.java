package com.jb.filemanager.function.docmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.database.provider.DocFileProvider;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.device.Machine;

import java.io.File;
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
        /*String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
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
                    childBean.mDocId = id;
                    childBean.mDocPath = path;
                    childBean.mDocSize = size;
                    childBean.mDocName = name;
                    childBean.mAddDate = dataAdded;
                    childBean.mModifyDate = dateModify;
                    childBean.mFileType = fileType;
                    childList.add(childBean);

                    Logger.d(TAG, childBean.mDocPath + "   " + childBean.mDocSize + "   " + childBean.mDocName + "   " +
                            childBean.mAddDate + "   " + childBean.mModifyDate + "   " + id);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }*/
        return childList;
    }

    @Override
    public void handleFileDelete(String docPath) {
            /*int delete = TheApplication.getAppContext().getContentResolver().delete(
                    Uri.parse("content://media/external/file"),
                    MediaStore.Files.FileColumns.DATA + " like ?",
                    new String[]{docPath});*/
        DocFileProvider.getInstance().deleteDocFile(docPath);
        Logger.d(TAG, "delete number" + delete + "   " + docPath);
    }

    @Override
    public void handleFileCopy(String oldFile, String newFile) {
        /*DocChildBean childBeen = handleQueryFile(oldFile);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DATA, newFile);
        contentValues.put(MediaStore.Files.FileColumns.SIZE, childBeen.mDocSize);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, childBeen.mAddDate);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, childBeen.mModifyDate);
        TheApplication.getAppContext().getContentResolver().insert(
                Uri.parse("content://media/external/file"), contentValues);*/
        DocChildBean childBean = DocFileProvider.getInstance().queryItem(oldFile);
        childBean.mDocPath = newFile;
        DocFileProvider.getInstance().insertDocItem(childBean);
    }

    @Override
    public void handleFileCut(String oldFile, String newFile) {
        DocFileProvider.getInstance().updateDocFilePath(oldFile, newFile);
        /*DocChildBean childBean = handleQueryFile(oldFile);
        if (childBean == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns._ID, childBean.mDocId);
        contentValues.put(MediaStore.Files.FileColumns.DATA, newFile);
        contentValues.put(MediaStore.Files.FileColumns.SIZE, childBean.mDocSize);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, childBean.mAddDate);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, childBean.mModifyDate);
        handleFileDelete(oldFile);
        TheApplication.getAppContext().getContentResolver().insert(
                Uri.parse("content://media/external/file"), contentValues);*/
    }

    @Override
    public void handleFileRename(String oldFile, String newFile) {
        DocFileProvider.getInstance().updateDocFileName(oldFile,newFile);
       /* DocChildBean childBean = handleQueryFile(oldFile);
        if (childBean == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns._ID, childBean.mDocId);
        contentValues.put(MediaStore.Files.FileColumns.DATA, newFile);
        contentValues.put(MediaStore.Files.FileColumns.SIZE, childBean.mDocSize);
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, childBean.mAddDate);
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, childBean.mModifyDate);

        TheApplication.getAppContext().getContentResolver().update(
                Uri.parse("content://media/external/file"), contentValues,
                MediaStore.Files.FileColumns._ID + " like ?", new String[]{childBean.mDocId});*/
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

    @Override
    public void scanBroadcastReceiver(File file) {

        if (Machine.HAS_SDK_KITKAT) {
            exportToGallery(file.getAbsolutePath());
            return;
        }
        //扫描sd的广播在19以后只有系统才能发出   之后只能扫描制定的文件或者文件夹
        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        final Uri contentUri = Uri.fromFile(file);
        scanIntent.setData(contentUri);
        TheApplication.getAppContext().sendBroadcast(scanIntent);
    }

    private Uri exportToGallery(String filename) {
        // Save the name and description of a video in a ContentValues map.
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        values.put(MediaStore.Files.FileColumns.DATA, filename);
        // Add a new record (identified by uri)
        final Uri uri = TheApplication.getAppContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        TheApplication.getAppContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filename)));
        return uri;
    }



}

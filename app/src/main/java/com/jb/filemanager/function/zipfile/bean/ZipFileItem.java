package com.jb.filemanager.function.zipfile.bean;

import java.io.File;

/**
 * Created by xiaoyu on 2017/6/30 9:57.
 */

public class ZipFileItem {
    private String mFileName;
    private long mFileSize;
    private boolean mSelected;
    private long mLastModifiedTime;
    private File mFile;

    public ZipFileItem(File file) {
        if (file.exists() && file.isFile()) {
            mFile = file;
            mFileName = file.getName();
            mFileSize = file.length();
            mSelected = false;
            mLastModifiedTime = file.lastModified();
        }
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long fileSize) {
        mFileSize = fileSize;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public long getLastModifiedTime() {
        return mLastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        mLastModifiedTime = lastModifiedTime;
    }
}

package com.jb.filemanager.function.recent.bean;

import com.jb.filemanager.function.recent.util.RecentFileUtil;

import java.io.File;

/**
 * Created by xiaoyu on 2017/7/14 11:20.
 */

public class BlockItemFileBean {
    private String mFileName;
    private String mFilePath;
    private boolean mFilePictureType;
    private boolean mSelected;

    public BlockItemFileBean(File file) {
        mFileName = file.getName();
        mFilePath = file.getPath();
        mFilePictureType = RecentFileUtil.isPictureType(mFileName);
        mSelected = false;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public boolean isFilePictureType() {
        return mFilePictureType;
    }

    public void setFilePictureType(boolean filePictureType) {
        mFilePictureType = filePictureType;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}

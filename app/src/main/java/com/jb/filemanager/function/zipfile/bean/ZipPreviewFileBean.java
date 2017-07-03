package com.jb.filemanager.function.zipfile.bean;

import com.jb.filemanager.function.zipfile.util.FileUtils;

import net.lingala.zip4j.model.FileHeader;

import java.util.zip.ZipEntry;

/**
 * Created by xiaoyu on 2017/6/30 15:09.
 * <p>
 * 压缩包内容预览Bean类
 * <ol>
 * <li>无加密zip压缩包</li>
 * <li>加密zip压缩包</li>
 * <li>无加密rar压缩包</li>
 * </ol>
 * </p>
 */

public class ZipPreviewFileBean {
    private boolean mIsDirectory;
    private String mFullPath;
    private String mFileName;
    private long mLastModifyTime;
    private long mSize;
    private long mCompressedSize;
    // 预留字段
    private int mFileType;
    private boolean mSelected = false;

    /**
     * zip压缩包, 原生方式初始化
     *
     * @param entry e
     */
    public ZipPreviewFileBean(ZipEntry entry) {
        mIsDirectory = entry.isDirectory();
        mFullPath = FileUtils.removeEdgeSeparatorIfExist(entry.getName());
        mFileName = FileUtils.getFileName(entry.getName());
        mLastModifyTime = entry.getTime();
        mSize = mIsDirectory ? -1 : entry.getSize();
        mCompressedSize = mIsDirectory ? -1 : entry.getCompressedSize();
    }

    /**
     * zip加密压缩包初始化
     *
     * @param fileHeader f
     */
    public ZipPreviewFileBean(FileHeader fileHeader) {
        mIsDirectory = fileHeader.isDirectory();
        mFullPath = FileUtils.removeEdgeSeparatorIfExist(fileHeader.getFileName());
        mFileName = FileUtils.getFileName(fileHeader.getFileName());
        mLastModifyTime = fileHeader.getLastModFileTime();
        mSize = fileHeader.getUncompressedSize();
        mCompressedSize = fileHeader.getCompressedSize();
    }

    /**
     * rar压缩包初始化
     *
     * @param fileHeader f
     */
    public ZipPreviewFileBean(de.innosystec.unrar.rarfile.FileHeader fileHeader) {
        mIsDirectory = fileHeader.isDirectory();
        mFullPath = FileUtils.formatterRarFileNameCode(fileHeader);
        mFullPath = FileUtils.removeEdgeSeparatorIfExist(mFullPath);
        mFileName = FileUtils.getFileName(mFullPath);
        mLastModifyTime = fileHeader.getMTime().getTime();
        mSize = fileHeader.getFullUnpackSize();
        mCompressedSize = fileHeader.getFullPackSize();
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setDirectory(boolean directory) {
        mIsDirectory = directory;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public void setFullPath(String fullPath) {
        mFullPath = fullPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public long getLastModifyTime() {
        return mLastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        mLastModifyTime = lastModifyTime;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getCompressedSize() {
        return mCompressedSize;
    }

    public void setCompressedSize(long compressedSize) {
        mCompressedSize = compressedSize;
    }

    public int getFileType() {
        return mFileType;
    }

    public void setFileType(int fileType) {
        mFileType = fileType;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public String toString() {
        return "ZipPreviewFileBean{" +
                "mIsDirectory=" + mIsDirectory +
                ", mFullPath='" + mFullPath + '\'' +
                ", mFileName='" + mFileName + '\'' +
                ", mLastModifyTime=" + mLastModifyTime +
                ", mSize=" + mSize +
                ", mCompressedSize=" + mCompressedSize +
                ", mFileType=" + mFileType +
                '}';
    }
}

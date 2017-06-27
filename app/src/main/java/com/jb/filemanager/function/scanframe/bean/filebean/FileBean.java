package com.jb.filemanager.function.scanframe.bean.filebean;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.util.ConvertUtils;

import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/24.
 */

public class FileBean extends ItemBean {
    public FileBean(GroupType groupType) {
        super(groupType);
    }

    /**
     * 文件路径
     */
    private String mPath;
    /**
     * 文件路径集合
     */
    private HashSet<String> mPathSet = new HashSet<String>();
    /**
     * 文件大小
     */
    private long mSize;
    /**
     * 文件名
     */
    private String mName;
    /**
     * 版本名
     */
    private String mVersionName;
    /**
     * 版本号
     */
    private int mVersionCode;
    /**
     * 是否安装
     */
    private boolean mIsInstall;
    /**
     * 是否选中
     */
    private boolean mIsCheck;
    /**
     * 是否备份
     */
    private boolean mIsBackup;
    /**
     * 文件类型
     */
    private FileFlag mFileFlag = FileFlag.NORMAL;

    public void setFileName(String name) {
        this.mName = name;
    }

    public String getFileName() {
        return mName;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        mVersionName = versionName;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        mVersionCode = versionCode;
    }

    public boolean isIsInstall() {
        return mIsInstall;
    }

    public void setIsInstall(boolean isInstall) {
        mIsInstall = isInstall;
    }

    public boolean isCheck() {
        return mIsCheck;
    }

    public void setCheck(boolean isCheck) {
        super.setCheck(isCheck);
        mIsCheck = isCheck;
    }

    @Override
    public String getTitle() {
        return getFileName();
    }

    @Override
    public void setTitle(String title) {
        setFileName(title);
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public void setSize(long size) {
        mSize = size;
    }

    @Override
    public void setPath(String path) {
        mPath = path;
        mPathSet.clear();
        mPathSet.add(path);
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public HashSet<String> getPaths() {
        return mPathSet;
    }

    public boolean isBackup() {
        return mIsBackup;
    }

    public void setIsBackup(boolean isBackup) {
        mIsBackup = isBackup;
    }

    public FileFlag getFileFlag() {
        return mFileFlag;
    }

    public void setFileFlag(FileFlag mFileFlag) {
        this.mFileFlag = mFileFlag;
    }

    @Override
    public String toString() {
        return "FileInfo [mPath=" + mPath + ", mSize=" + mSize
                + ", mName=" + mName + ", mVersionName=" + mVersionName
                + ", mVersionCode=" + mVersionCode + ", mIsInstall="
                + mIsInstall + ", mIsCheck=" + mIsCheck + ", mIsBackup="
                + mIsBackup + ", mFileFlag=" + mFileFlag + "]";
    }

    public String toSimpleString() {
        return "FileInfo [mPath=" + mPath + ", mSize=" + ConvertUtils.formatFileSize(mSize)
                + ", mName=" + mName + "]";
    }
}

package com.jb.filemanager.function.scanframe.bean.adbean;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.residuebean.FolderBean;

import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class AdBean extends ItemBean implements Cloneable, FolderBean {
    /**
     * 数据库Key
     */
    private String mDBKey;
    /**
     * 路径
     */
    private String mPath;
    private HashSet<String> mPathSet = new HashSet<>();
    /**
     * 大小
     */
    private long mSize;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 文件夹个数
     */
    private int mFolderCount;
    /**
     * 文件个数
     */
    private int mFileCount;

    public AdBean() {
        super(GroupType.AD);
    }

    public String getDBKey() {
        return mDBKey;
    }

    public void setDBKey(String key) {
        mDBKey = key;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public HashSet<String> getPaths() {
        return mPathSet;
    }

    @Override
    public void setPath(String path) {
        mPathSet.clear();
        mPathSet.add(path);
        mPath = path;
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
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public int getFolderCount() {
        return mFolderCount;
    }

    @Override
    public void setFolderCount(int folderCount) {
        mFolderCount = folderCount;
    }

    @Override
    public int getFileCount() {
        return mFileCount;
    }

    @Override
    public void setFileCount(int fileCount) {
        mFileCount = fileCount;
    }

    @Override
    public AdBean clone() {
        AdBean bean = null;
        try {
            bean = (AdBean) super.clone();
            HashSet<String> pathSet = new HashSet<String>();
            pathSet.addAll(mPathSet);
            bean.mPathSet = pathSet;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "CleanAdBean{" +
                "mDBKey='" + mDBKey + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mPathSet=" + mPathSet +
                ", mSize=" + mSize +
                ", mTitle='" + mTitle + '\'' +
                ", mFolderCount=" + mFolderCount +
                ", mFileCount=" + mFileCount +
                '}';
    }
}
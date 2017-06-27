package com.jb.filemanager.function.scanframe.bean.bigfolder;


import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;

import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class BigFolderBean extends ItemBean implements Cloneable {
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
     * 标题
     */
    private String mTitle;
    /**
     * 包名
     */
    private String mPackageName;
    /**
     * 是否为文件夹
     */
    private boolean mIsFolder;


    public BigFolderBean() {
        super(GroupType.BIG_FOLDER);
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

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    @Override
    public BigFolderBean clone() {
        BigFolderBean bean = null;
        try {
            bean = (BigFolderBean) super.clone();
            HashSet<String> pathSet = new HashSet<String>();
            pathSet.addAll(mPathSet);
            bean.mPathSet = pathSet;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public void setIsFolder(boolean isFolder) {
        mIsFolder = isFolder;
    }

    @Override
    public String toString() {
        return "BigFolderBean{" +
                "mPath='" + mPath + '\'' +
                ", mSize=" + mSize +
                ", mTitle='" + mTitle + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                ", mIsFolder=" + mIsFolder +
                '}';
    }
}

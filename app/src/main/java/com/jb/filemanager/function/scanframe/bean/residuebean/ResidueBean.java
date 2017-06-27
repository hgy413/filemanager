package com.jb.filemanager.function.scanframe.bean.residuebean;

import android.text.TextUtils;

import com.jb.filemanager.function.scanframe.bean.common.FileType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/25.
 */

public class ResidueBean extends ItemBean implements Cloneable, FolderBean {
    /**
     * 路径分割符
     */
    private static final String PATH_DIVIDER = ";\n";

    public ResidueBean() {
        super(GroupType.RESIDUE);
    }

    /**
     * 多语言文本id
     */
    private String mPathId;
    /**
     * 数据库Key
     */
    private String mDBKey;

    /**
     * 包名
     */
    private String mPackageName;
    private HashSet<String> mPkgNameSet = new HashSet<String>();
    /**
     * 应用名
     */
    private String mAppName;
    /**
     * 残留路径
     */
    private String mPath;
    private HashSet<String> mPathSet = new HashSet<String>();
    /**
     * 文件大小
     */
    private long mSize;
    /**
     * 包含的文件类型
     */
    private HashSet<FileType> mFileTypeSet = new HashSet<FileType>();

    /**
     * 文件夹个数
     */
    private int mFolderCount;
    /**
     * 文件个数
     */
    private int mFileCount;

    /**
     * 包含的Video集合
     */
    private HashSet<String> mVideoSet = new HashSet<String>();
    /**
     * 包含的Image集合
     */
    private HashSet<String> mImageSet = new HashSet<String>();

    public String getPathId() {
        return mPathId;
    }

    public void setPathId(String pathId) {
        this.mPathId = pathId;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        this.mAppName = appName;
    }

    public HashSet<String> getPkgNameSet() {
        return mPkgNameSet;
    }

    public void setPkgNameSet(HashSet<String> set) {
        if (set != null) {
            mPkgNameSet = set;
        }
    }

    public String getDBKey() {
        return mDBKey;
    }

    public void setDBKey(String DBKey) {
        mDBKey = DBKey;
    }

    @Override
    public void setPath(String path) {
        mPathSet.clear();
        mPathSet.add(path);
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void addPath(String path) {
        if (TextUtils.isEmpty(mPath)) {
            mPath = path;
        } else {
            mPath = mPath + PATH_DIVIDER + path;
        }
        mPathSet.add(path);
    }

    public void setPaths(Collection<String> paths) {
        mPathSet.clear();
        mPath = "";
        for (String path : paths) {
            addPath(path);
        }
    }

    public HashSet<String> getPaths() {
        return mPathSet;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public void setSize(long size) {
        mSize = size;
    }

    public HashSet<FileType> getFileType() {
        return mFileTypeSet;
    }

    public void addFileType(FileType fileType) {
        mFileTypeSet.add(fileType);
    }

    @Override
    public String getTitle() {
        return getAppName();
    }

    /**
     * 设置无效
     */
    @Override
    public void setTitle(String title) {
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

    public HashSet<String> getVideoSet() {
        return mVideoSet;
    }

    public void addVideo(String path) {
        mVideoSet.add(path);
    }

    public HashSet<String> getImageSet() {
        return mImageSet;
    }

    public void addImage(String path) {
        mImageSet.add(path);
    }

    @Override
    public ResidueBean clone() {
        ResidueBean bean = null;
        try {
            bean = (ResidueBean) super.clone();
            HashSet<String> pathSet = new HashSet<String>();
            pathSet.addAll(mPathSet);
            bean.mPathSet = pathSet;
            ArrayList<SubItemBean> subBeans = new ArrayList<SubItemBean>();
            subBeans.addAll(mSubItemList);
            bean.mSubItemList = subBeans;
            HashSet<FileType> fileTypeSet = new HashSet<FileType>();
            fileTypeSet.addAll(mFileTypeSet);
            bean.mFileTypeSet = fileTypeSet;
            HashSet<String> pkgNameSet = new HashSet<String>();
            pkgNameSet.addAll(mPkgNameSet);
            bean.mPkgNameSet = pkgNameSet;
            HashSet<String> videoSet = new HashSet<String>();
            videoSet.addAll(mVideoSet);
            bean.mVideoSet = videoSet;
            HashSet<String> imageSet = new HashSet<String>();
            videoSet.addAll(mImageSet);
            bean.mImageSet = imageSet;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "ResidueBean{" +
                "mPathId='" + mPathId + '\'' +
                ", mDBKey='" + mDBKey + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                ", mPkgNameSet=" + mPkgNameSet +
                ", mAppName='" + mAppName + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mPathSet=" + mPathSet +
                ", mSize=" + mSize +
                ", mFileTypeSet=" + mFileTypeSet +
                ", mFolderCount=" + mFolderCount +
                ", mFileCount=" + mFileCount +
                ", mVideoSet=" + mVideoSet +
                ", mImageSet=" + mImageSet +
                '}';
    }
}

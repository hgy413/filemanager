package com.jb.filemanager.function.scanframe.bean.cachebean.subitem;

import android.app.AlarmManager;
import android.content.Context;

import com.jb.filemanager.R;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemType;

import java.io.File;
import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 系统缓存下每个app项下的缓存项<br>
 */

public class SubAppCacheBean extends SubItemBean {
    /**
     * 数据库Key
     */
    private String mDBKey;
    /**
     * 包名
     */
    private String mPackageName;
    /**
     * 路径
     */
    private String mPath;
    /**
     * 提示文案的Id
     */
    private int mTextId;
    /**
     * 提示文案 - 标题
     */
    private String mTitle;
    /**
     * 提示文案 - 描述内容
     */
    private String mDesc;
    /**
     * 警告级别:ID:警告:备注 <br>
     * 11:建议保留
     * 10:清理后无法恢复:因为系统不能填负值,因此修改为10.:ing will remove these forever <br>
     * 0:清理后不影响使用:Your files will not be affected after cleaning <br>
     * 1:建议清理:ing is completely safe <br>
     * 2:清理后需要联网重新加载:They will need to be re download
     */
    private int mWarnLv;
    /**
     * 几天前
     */
    private int mDayBefore;
    /**
     * 类型
     */
    private int mContentType;
    /**
     * 版本
     */
    private int mVersion;
    /**
     * 是否被勾选
     */
    private boolean mIsChecked;
    /**
     * 文件大小
     */
    private long mSize;
    /**
     * 文件夹个数
     */
    private int mFolderCount;
    /**
     * 文件个数
     */
    private int mFileCount;

    /**
     * 子路径
     */
    private HashSet<String> mChildList = new HashSet<String>();

    public SubAppCacheBean() {
        super(SubItemType.APP);
    }

    public String getDBKey() {
        return mDBKey;
    }

    public void setDBKey(String DBKey) {
        mDBKey = DBKey;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public void setPath(String path) {
        mPath = path;
    }

    public void addChildFile(String path) {
        mChildList.add(path);
    }

    public HashSet<String> getChildList() {
        return mChildList;
    }

    public boolean isCacheFileParent(File file) {
        String path = file.getPath();
        boolean isMatchPath = getPathWithoutEndSeparator().toLowerCase()
                .startsWith(path.toLowerCase());
        return isMatchPath;
    }

    /**
     * 是否符合X天之前的条件
     */
    public boolean isMatchDayBefore(File file) {
        if (file != null) {
            return (System.currentTimeMillis() - file.lastModified()) > getDayBefore()
                    * AlarmManager.INTERVAL_DAY;
        } else {
            return false;
        }
    }

    /**
     * 去除路径末尾的文件夹分隔符(把DB数据中的文件夹后缀/去除(因为文件扫描出的文件夹路径没有后缀/))
     */
    public String getPathWithoutEndSeparator() {
        if (mPath.endsWith(File.separator)) {
            return mPath.substring(0, mPath.length() - 1);
        }
        return mPath;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public String getTitle(Context context) {
        if (mDayBefore > 0) {
            return getTitle() + " " + getDayBefore() + " "
                    + context.getString(R.string.clean_days_ago);
        } else {
            return getTitle();
        }
    }

    @Override
    public void setTitle(String title) {
        mTitle = title;
    }

    public int getTextId() {
        return mTextId;
    }

    public void setTextId(int textId) {
        mTextId = textId;
    }

    public int getWarnLv() {
        return mWarnLv;
    }

    public void setWarnLv(int warnLv) {
        mWarnLv = warnLv;
    }

    @Override
    public boolean isDefaultCheck() {
        return mWarnLv < 10;
    }

    public int getDayBefore() {
        return mDayBefore;
    }

    public void setDayBefore(int dayBefore) {
        mDayBefore = dayBefore;
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    @Override
    public long getSize() {
        return mSize;
    }

    @Override
    public void setSize(long size) {
        mSize = size;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public int getContentType() {
        return mContentType;
    }

    public void setContentType(int type) {
        mContentType = type;
    }

    @Override
    public String getKey() {
        return mPackageName;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public int getFolderCount() {
        return mFolderCount;
    }

    public void setFolderCount(int folderCount) {
        mFolderCount = folderCount;
    }

    public int getFileCount() {
        return mFileCount;
    }

    public void setFileCount(int fileCount) {
        mFileCount = fileCount;
    }

    /**
     * 是否包含子路径(通配符或者过期日期)
     */
    public boolean isContainChildPath() {
        return !mChildList.isEmpty();
    }

    @Override
    public SubAppCacheBean clone() {
        SubAppCacheBean bean = null;
        try {
            bean = (SubAppCacheBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "SubAppCacheBean{" +
                "mDBKey='" + mDBKey + '\'' +
                ", mPackageName='" + mPackageName + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mTextId=" + mTextId +
                ", mTitle='" + mTitle + '\'' +
                ", mDesc='" + mDesc + '\'' +
                ", mWarnLv=" + mWarnLv +
                ", mDayBefore=" + mDayBefore +
                ", mContentType=" + mContentType +
                ", mVersion=" + mVersion +
                ", mIsChecked=" + mIsChecked +
                ", mSize=" + mSize +
                ", mFolderCount=" + mFolderCount +
                ", mFileCount=" + mFileCount +
                ", mChildList=" + mChildList +
                '}';
    }
}

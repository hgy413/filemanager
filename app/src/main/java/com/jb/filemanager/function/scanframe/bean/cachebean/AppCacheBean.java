package com.jb.filemanager.function.scanframe.bean.cachebean;


import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/20.<br>
 * app缓存 -- 缓存文件项列表的第二项至最后一项<br>
 */

public class AppCacheBean extends CacheBean implements Cloneable {
    /**
     * 数据库Key
     */
    private String mDBKey;
    /**
     * 版本号
     */
    private int mVersion;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 文件大小
     */
    private long mSize;
    /**
     * 包名
     */
    private String mPackageName;
    /**
     * 文件路径集合(缓存文件)
     */
    private HashSet<String> mPathSet = new HashSet<>();

    public AppCacheBean() {
    }

    public String getDBKey() {
        return mDBKey;
    }

    public void setDBKey(String DBKey) {
        mDBKey = DBKey;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
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
    public long getSize() {
        return mSize;
    }

    @Override
    public void setSize(long size) {
        mSize = size;
    }

    @Override
    public String getPath() {
        return mPackageName;
    }

    @Override
    public HashSet<String> getPaths() {
        mPathSet.clear();
        mPathSet.add(mPackageName);
        return mPathSet;
    }

    @Override
    public void setPath(String path) {

    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    @Override
    public String getKey() {
        return mPackageName;
    }

    @Override
    public AppCacheBean clone() {
        AppCacheBean bean = null;
        try {
            bean = (AppCacheBean) super.clone();
            HashSet<String> pathSet = new HashSet<String>();
            pathSet.addAll(mPathSet);
            bean.mPathSet = pathSet;
            ArrayList<SubItemBean> subBeans = new ArrayList<SubItemBean>();
            subBeans.addAll(mSubItemList);
            bean.mSubItemList = subBeans;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "AppCacheBean{" +
                "mDBKey='" + mDBKey + '\'' +
                ", mVersion=" + mVersion +
                ", mTitle='" + mTitle + '\'' +
                ", mSize=" + mSize +
                ", mPackageName='" + mPackageName + '\'' +
                //", mPathSet=" + mPathSet +
                '}';
    }
}

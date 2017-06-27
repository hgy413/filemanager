package com.jb.filemanager.function.scanframe.bean.cachebean;

import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 系统缓存 -- 缓存文件列表项中的第一项<br>
 */

public class SysCacheBean extends CacheBean {
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 文件大小
     */
    private long mSize;
    /**
     * 文件路径集合
     */
    private HashSet<String> mPathSet = new HashSet<>();

    public SysCacheBean() {
        super();
    }

    @Override
    public String getPath() {
        return mTitle;
    }

    @Override
    public HashSet<String> getPaths() {
        mPathSet.clear();
        mPathSet.add(mTitle);
        return mPathSet;
    }

    @Override
    public void setPath(String path) {
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
    public String getKey() {
        return "SystemCache";
    }

    @Override
    public String toString() {
        return "CleanSysCacheBean [mTitle=" + mTitle + ", mSize=" + mSize
                + ", mPathSet=" + mPathSet + "]";
    }

}

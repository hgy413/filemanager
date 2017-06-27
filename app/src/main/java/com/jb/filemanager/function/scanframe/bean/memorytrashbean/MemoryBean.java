package com.jb.filemanager.function.scanframe.bean.memorytrashbean;


import com.jb.filemanager.function.scanframe.bean.appBean.RunningAppBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.util.ConvertUtils;

import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/21.<br>
 * 内存垃圾项<br>
 */

public class MemoryBean extends ItemBean {
    /**
     * 内存信息
     */
    private RunningAppBean mRunningAppBean = null;
    /**
     * 文件路径集合
     */
    private HashSet<String> mPathSet = new HashSet<>();

    public MemoryBean(RunningAppBean runningAppBean) {
        super(GroupType.MEMORY);
        mRunningAppBean = runningAppBean;
    }

    @Override
    public String getPath() {
        return mRunningAppBean.mPackageName;
    }

    @Override
    public HashSet<String> getPaths() {
        mPathSet.clear();
        mPathSet.add(getPath());
        return mPathSet;
    }

    @Override
    public void setPath(String path) {
    }

    @Override
    public long getSize() {
        if (mRunningAppBean != null) {
            return mRunningAppBean.mMemory * 1024;
        }
        return 0;
    }

    @Override
    public void setSize(long size) {
    }

    @Override
    public String getTitle() {
        if (mRunningAppBean != null) {
            return mRunningAppBean.mAppName;
        }
        return "";
    }

    @Override
    public void setTitle(String title) {
    }

    public RunningAppBean getRunningAppBean() {
        return mRunningAppBean;
    }

    public void setRunningAppBean(RunningAppBean mRunningAppBean) {
        this.mRunningAppBean = mRunningAppBean;
    }

    public boolean isIgnore() {
        if (mRunningAppBean != null) {
            return mRunningAppBean.mIsIgnore;
        }
        return false;
    }

    @Override
    public String toString() {
        return "MemoryBean{" +
                "名字=" + getTitle() +
                ", 大小=" + ConvertUtils.formatFileSize(mRunningAppBean.mMemory) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof MemoryBean) {
            if (this.getRunningAppBean().mPackageName.equals(((MemoryBean) o).getRunningAppBean().mPackageName)) {
                result = true;
            }
        }
        return result;
    }
}

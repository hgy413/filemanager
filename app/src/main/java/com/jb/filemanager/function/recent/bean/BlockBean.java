package com.jb.filemanager.function.recent.bean;

import com.jb.filemanager.function.recent.util.RecentFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/13 20:09.
 */

public class BlockBean {

    private String mBlockDirName;
    // 修改的时间 单位:分钟
    private int mWithinTime;
    // Block类型只有两种,所以用布尔值表示, 决定ListView的样式
    private boolean mIsPictureType;
    // Block内的文件列表
    private List<BlockItemFileBean> mItemFiles;
    // ItemFile数量
    private int mChildCount;
    // 是否显示More按钮 文件:数量大于3 图片:数量大于6
    private boolean mHaveMore;

    public BlockBean(File file) {
        mBlockDirName = file.getParentFile().getName();
        mWithinTime = RecentFileUtil.calculateWithinMinute(System.currentTimeMillis() - file.lastModified());
        mIsPictureType = RecentFileUtil.isPictureType(file.getName());
        mItemFiles = new ArrayList<>();
        addBlockItemFile(file);
    }

    public void addBlockItemFile(File file) {
        if (mItemFiles != null) {
            mItemFiles.add(new BlockItemFileBean(file));
        }
    }

    public String getBlockDirName() {
        return mBlockDirName;
    }

    public int getWithinTime() {
        return mWithinTime;
    }

    public boolean isPictureType() {
        return mIsPictureType;
    }

    public List<BlockItemFileBean> getItemFiles() {
        return mItemFiles;
    }

    public boolean isHaveMore() {
        if (mItemFiles == null) return false;
        if (mIsPictureType) {
            return mItemFiles.size() > 6;
        } else {
            return mItemFiles.size() > 3;
        }
    }

    public int getChildCount() {
        if (mItemFiles != null) {
            mChildCount = mItemFiles.size();
        } else {
            mChildCount = 0;
        }
        return mChildCount;
    }
}

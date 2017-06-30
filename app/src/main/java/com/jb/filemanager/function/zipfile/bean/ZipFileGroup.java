package com.jb.filemanager.function.zipfile.bean;

import com.jb.filemanager.util.TimeUtil;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/29 17:45.
 */

public class ZipFileGroup {
    private long mGroupTime;
    private String mGroupTimeStr;
    private List<ZipFileItem> mZipFileList;
    private int mChildCount;
    private boolean mChecked;

    public ZipFileGroup(List<ZipFileItem> list) {
        if (list != null && list.size() > 0) {
            mZipFileList = list;
            mGroupTime = TimeUtil.getYMDTime(list.get(0).getLastModifiedTime());
            mChildCount = list.size();
            mGroupTimeStr = TimeUtil.getTime(mGroupTime, TimeUtil.DATE_FORMATTER_DATE);
        }
    }

    public ZipFileItem getChild(int position) {
        return mZipFileList.get(position);
    }

    public long getGroupTime() {
        return mGroupTime;
    }

    public void setGroupTime(long groupTime) {
        mGroupTime = groupTime;
    }

    public List<ZipFileItem> getZipFileList() {
        return mZipFileList;
    }

    public void setZipFileList(List<ZipFileItem> zipFileList) {
        mZipFileList = zipFileList;
    }

    public int getChildCount() {
        mChildCount = mZipFileList.size();
        return mChildCount;
    }

    public void setChildCount(int childCount) {
        mChildCount = childCount;
    }

    public String getGroupTimeStr() {
        return mGroupTimeStr;
    }

    public boolean isChecked() {

        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public void setGroupTimeStr(String groupTimeStr) {

        mGroupTimeStr = groupTimeStr;
    }
}

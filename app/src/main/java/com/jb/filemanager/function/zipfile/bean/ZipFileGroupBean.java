package com.jb.filemanager.function.zipfile.bean;

import com.jb.filemanager.util.TimeUtil;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/29 17:45.
 */

public class ZipFileGroupBean {

    public static final int STATE_SELECTED_NONE = 0x01;
    public static final int STATE_SELECTED_MULTI = 0x02;
    public static final int STATE_SELECTED_ALL = 0x03;

    private long mGroupTime;
    private String mGroupTimeStr;
    private List<ZipFileItemBean> mZipFileList;
    private int mChildCount;
    private boolean mChecked;

    private int mSelectedState = STATE_SELECTED_NONE;

    public ZipFileGroupBean(List<ZipFileItemBean> list) {
        if (list != null && list.size() > 0) {
            mZipFileList = list;
            mGroupTime = TimeUtil.getYMDTime(list.get(0).getLastModifiedTime());
            mChildCount = list.size();
            mGroupTimeStr = TimeUtil.getTime(mGroupTime, TimeUtil.DATE_FORMATTER_DATE);
        }
    }

    public ZipFileItemBean getChild(int position) {
        return mZipFileList.get(position);
    }

    public long getGroupTime() {
        return mGroupTime;
    }

    public void setGroupTime(long groupTime) {
        mGroupTime = groupTime;
    }

    public List<ZipFileItemBean> getZipFileList() {
        return mZipFileList;
    }

    public void setZipFileList(List<ZipFileItemBean> zipFileList) {
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

    public int getSelectedState() {
        int count = 0;
        for (ZipFileItemBean bean : mZipFileList) {
            count += bean.isSelected() ? 1 : 0;
        }
        if (count == 0) {
            mSelectedState = STATE_SELECTED_NONE;
        } else if (count == mZipFileList.size()) {
            mSelectedState = STATE_SELECTED_ALL;
        } else {
            mSelectedState = STATE_SELECTED_MULTI;
        }
        return mSelectedState;
    }

    public void switchGroupState() {
        if (mSelectedState == STATE_SELECTED_ALL) {
            for (ZipFileItemBean bean : mZipFileList) {
                bean.setSelected(false);
            }
        } else {
            for (ZipFileItemBean bean : mZipFileList) {
                bean.setSelected(true);
            }
        }
    }
}

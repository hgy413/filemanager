package com.jb.filemanager.function.applock.model.bean;


import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;

import java.util.List;

/**
 * Created by wangying on 15/12/7.
 */
public class AppLockGroupData extends BaseGroupsDataBean<LockerItem> {

    private String mLockerGroupTitle;

    private boolean isAllChecked;

    public AppLockGroupData(List<LockerItem> children, String groupTitle) {
        super(children);
        mLockerGroupTitle = groupTitle;
    }

    /**
     * 获取AppLocker的Group的Title
     *
     * @return
     */
    public String getLockerGroupTitle() {
        return mLockerGroupTitle;
    }

    public boolean isAllChecked() {
        return isAllChecked;
    }

    public void setAllChecked(boolean allChecked) {
        isAllChecked = allChecked;
    }

}

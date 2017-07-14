package com.jb.filemanager.function.apkmanager;


import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 15:02
 */

public class AppGroupBean extends BaseGroupsDataBean<AppItemInfo> {

    public static final int USER_APP = 0;
    public static final int SYSTEM_APP = 1;
    public int mAppType = USER_APP;
    String mGroupTitle;
    GroupSelectBox.SelectState mSelectState;
    boolean mIsExpand;

    protected AppGroupBean(List<AppItemInfo> children, String title, GroupSelectBox.SelectState selectState, boolean isExpand,int appType) {
        super(children);
        mGroupTitle = title;
        mSelectState = selectState;
        mIsExpand = isExpand;
        mAppType = appType;
    }

    public void setSelectState(GroupSelectBox.SelectState selectState) {
        mSelectState = selectState;
    }
}

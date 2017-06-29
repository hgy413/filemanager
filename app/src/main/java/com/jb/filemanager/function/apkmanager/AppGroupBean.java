package com.jb.filemanager.function.apkmanager;


import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.applock.adapter.BaseGroupsDataBean;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 15:02
 */

public class AppGroupBean extends BaseGroupsDataBean<AppChildBean> {

    String mGroupTitle;
    GroupSelectBox.SelectState mSelectState;
    boolean mIsExpand;

    protected AppGroupBean(List<AppChildBean> children, String title, GroupSelectBox.SelectState selectState, boolean isExpaned) {
        super(children);
        mGroupTitle = title;
        mSelectState = selectState;
        mIsExpand = isExpaned;
    }

    public void setSelectState(GroupSelectBox.SelectState selectState) {
        mSelectState = selectState;
    }
}

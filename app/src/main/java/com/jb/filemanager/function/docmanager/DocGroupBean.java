package com.jb.filemanager.function.docmanager;


import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 10:43
 */

public class DocGroupBean extends BaseGroupsDataBean<DocChildBean> {

    String mGroupTitle;
    GroupSelectBox.SelectState mSelectState;
    boolean mIsExpand;

    protected DocGroupBean(List<DocChildBean> children,String title, GroupSelectBox.SelectState selectState, boolean isExpand) {
        super(children);
        mGroupTitle = title;
        mSelectState = selectState;
        mIsExpand = isExpand;
    }
}

package com.jb.filemanager.function.scanframe.clean.ignore;


import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;

import java.util.List;

/**
 * 清理白名单组Bean
 *
 * @author chenbenbin
 */
public class CleanIgnoreGroupBean extends BaseGroupsDataBean {
    /** 类别 */
    private GroupType mGroupType;
    public boolean mIsGroupExpanded;//组是否展开

    public CleanIgnoreGroupBean(List<? extends CleanIgnoreBean> children, GroupType type) {
        super(children);
        mGroupType = type;
    }

    public GroupType getGroupType() {
        return mGroupType;
    }
}

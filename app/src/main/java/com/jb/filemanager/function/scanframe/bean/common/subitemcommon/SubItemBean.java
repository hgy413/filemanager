package com.jb.filemanager.function.scanframe.bean.common.subitemcommon;


import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.god.ChildType;

/**
 * Created by xiaoyu on 2016/10/20.
 * 三级子项模型<br>
 */

public abstract class SubItemBean extends BaseChildBean {

    private SubItemType mSubItemType;

    private SubItemBean() {
        super(ChildType.SUB_ITEM);
    }

    public SubItemBean(SubItemType type) {
        this();
        mSubItemType = type;
    }

    public SubItemType getSubItemType() {
        return mSubItemType;
    }

    public void setSubItemType(SubItemType subItemType) {
        mSubItemType = subItemType;
    }

    public boolean isAppCache() {
        return mSubItemType.equals(SubItemType.APP);
    }

    public boolean isSysCache() {
        return mSubItemType.equals(SubItemType.SYS);
    }

    public abstract String getPath();

    public abstract void setPath(String path);

    public abstract boolean isChecked();

    public abstract void setChecked(boolean isChecked);

    public abstract boolean isDefaultCheck();
}

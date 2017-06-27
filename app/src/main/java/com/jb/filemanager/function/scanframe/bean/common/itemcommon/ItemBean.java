package com.jb.filemanager.function.scanframe.bean.common.itemcommon;


import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.god.ChildType;
import com.jb.filemanager.function.scanframe.bean.common.subitemcommon.SubItemBean;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by xiaoyu on 2016/10/20.<br>
 * 二级子项模型<br>
 * 大的子项, 类型详情见{@link GroupType}
 */

public abstract class ItemBean extends BaseChildBean {

    /**
     * 类别
     */
    private GroupType mGroupType;
    /**
     * 勾选状态
     */
    private GroupSelectBox.SelectState mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
    /**
     * 默认规则的选中状态
     */
    private boolean mDefaultCheck;
    /**
     * 是否展开
     */
    private boolean mIsExpand = false;
    /**
     * 子项队列
     */
    protected ArrayList<SubItemBean> mSubItemList = new ArrayList<>();

    public ItemBean(GroupType groupType) {
        super(ChildType.ITEM);
        mGroupType = groupType;
    }

    public abstract String getPath();

    public abstract HashSet<String> getPaths();

    public abstract void setPath(String path);

    public void setCheck(boolean check) {
        mSelectState = check ? GroupSelectBox.SelectState.ALL_SELECTED
                : GroupSelectBox.SelectState.NONE_SELECTED;
    }

    public GroupSelectBox.SelectState getState() {
        return mSelectState;
    }

    public void setState(GroupSelectBox.SelectState state) {
        mSelectState = state;
    }

    /**
     * 根据当前的状态进行切换下一个状态
     *
     * @param state 当前状态
     */
    public void switchState(GroupSelectBox.SelectState state) {
        setState(state == GroupSelectBox.SelectState.ALL_SELECTED ? GroupSelectBox.SelectState.NONE_SELECTED
                : GroupSelectBox.SelectState.ALL_SELECTED);
    }

    public boolean isAllSelected() {
        return mSelectState.equals(GroupSelectBox.SelectState.ALL_SELECTED);
    }

    public boolean isNoneSelected() {
        return mSelectState.equals(GroupSelectBox.SelectState.NONE_SELECTED);
    }

    public boolean isMultSelected() {
        return mSelectState.equals(GroupSelectBox.SelectState.MULT_SELECTED);
    }

    /**
     * 根据三级项的选中状态更新组的选中状态
     */
    public void updateState() {
        if (mSubItemList.isEmpty()) {
            // 若没有三级列表，则不处理，保持和之前外部设置的一致
            return;
        }
        boolean isAllSelect = true;
        boolean isMultSelect = false;
        for (SubItemBean subItem : mSubItemList) {
            isAllSelect = isAllSelect && subItem.isChecked();
            isMultSelect = isMultSelect || subItem.isChecked();
        }
        if (isAllSelect) {
            setState(GroupSelectBox.SelectState.ALL_SELECTED);
        } else if (isMultSelect) {
            setState(GroupSelectBox.SelectState.MULT_SELECTED);
        } else {
            setState(GroupSelectBox.SelectState.NONE_SELECTED);
        }
    }

    public boolean isDefaultCheck() {
        return mDefaultCheck;
    }

    public void setDefaultCheck(boolean mDefaultCheck) {
        this.mDefaultCheck = mDefaultCheck;
    }

    public GroupType getGroupType() {
        return mGroupType;
    }

    public boolean isExpand() {
        return mIsExpand;
    }

    public void setIsExpand(boolean isExpand) {
        mIsExpand = isExpand;
    }

    public ArrayList<SubItemBean> getSubItemList() {
        return mSubItemList;
    }

    public void setSubItemList(ArrayList<SubItemBean> subItemList) {
        mSubItemList = subItemList;
    }

    public void addSubItem(SubItemBean item) {
        mSubItemList.add(item);
    }

    public void removeSubItem(SubItemBean item) {
        mSubItemList.remove(item);
    }

}

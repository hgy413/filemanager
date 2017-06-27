package com.jb.filemanager.function.scanframe.bean;

import android.content.Context;

import com.jb.filemanager.commomview.GroupSelectBox.SelectState;
import com.jb.filemanager.function.scanframe.bean.common.god.BaseChildBean;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.GroupType;
import com.jb.filemanager.function.scanframe.bean.common.itemcommon.ItemBean;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanDoneEvent;
import com.jb.filemanager.function.scanframe.clean.event.CleanScanFileSizeEvent;

import java.util.List;

/**
 * 清理列表组的数据类型
 * 
 * @author chenbenbin
 * 
 */
public class CleanGroupsBean extends BaseGroupsDataBean {
	/** 标题 */
	private String mTitle;
	/** 勾选状态 */
	private SelectState mSelectState;
	/** 是否展开显示 */
	private boolean mIsExpaned;
	/** 类别 */
	private GroupType mGroupType;
	/** 是否扫描结束 */
	private boolean mIsScanFinish;
	/** 扫描结束后的进度条动画是否结束 */
	private boolean mIsProgressFinish;

	@SuppressWarnings("unchecked")
	public CleanGroupsBean(List<? extends BaseChildBean> children,
						   GroupType type, String title, SelectState state, long size) {
		super(children);
		mGroupType = type;
		mTitle = title;
		mSelectState = state;
	}

	public CleanGroupsBean(List<? extends BaseChildBean> children,
						   GroupType type, String title) {
		this(children, type, title, SelectState.NONE_SELECTED, 0);
	}

	public CleanGroupsBean(Context context,
						   List<? extends BaseChildBean> children, GroupType type) {
		this(children, type, context.getString(type.getNameId()),
				SelectState.NONE_SELECTED, 0);
	}

	public String getTitle() {
		return mTitle;
	}

	public SelectState getState() {
		return mSelectState;
	}

	public void setState(SelectState state) {
		mSelectState = state;
	}

	/**
	 * 根据当前的状态进行切换下一个状态
	 * @param state 当前状态
	 */
	public void switchState(SelectState state) {
		setState(state == SelectState.ALL_SELECTED ? SelectState.NONE_SELECTED
				: SelectState.ALL_SELECTED);
	}

	/**
	 * 根据<i><b>三级项</b></i>更新<i><b>二级&一级</b></i>的状态
	 */
	public void updateStateBySubItem() {
		updateState(true);
	}

	/**
	 * 根据<i><b>二级项</b></i>更新<i><b>一级</b></i>的状态
	 */
	public void updateStateByItem() {
		updateState(false);
	}

	/**
	 * 根据子项更新状态
	 * @param isUpdateItemBean 是否更新二级项
	 */
	private void updateState(boolean isUpdateItemBean) {
		boolean isAllSelect = true;
		boolean isMultiSelect = false;
		for (Object childObject : getChildren()) {
			BaseChildBean childBean = (BaseChildBean) childObject;
			if (childBean.isTypeItem()) {
				ItemBean itemBean = (ItemBean) childBean;
				if (isUpdateItemBean) {
					itemBean.updateState();
				}
				isAllSelect = isAllSelect && itemBean.isAllSelected();
				isMultiSelect = isMultiSelect || !itemBean.isNoneSelected();
			}
		}
		if (isAllSelect) {
			setState(SelectState.ALL_SELECTED);
		} else if (isMultiSelect) {
			setState(SelectState.MULT_SELECTED);
		} else {
			setState(SelectState.NONE_SELECTED);
		}
	}

	public boolean isAllSelected() {
		return mSelectState == SelectState.ALL_SELECTED;
	}

	public long getSize() {
		return CleanScanFileSizeEvent.get(mGroupType).getSize();
	}

	public GroupType getGroupType() {
		return mGroupType;
	}

	public boolean isScanFinish() {
		return mIsScanFinish;
	}

	public void updateScanFinish() {
		mIsScanFinish = CleanScanDoneEvent.isDone(mGroupType);
	}

	public boolean isProgressFinish() {
		return mIsProgressFinish;
	}

	public void setProgressFinish(boolean isProgressFinish) {
		mIsProgressFinish = isProgressFinish;
	}

	public boolean isExpaned() {
		return mIsExpaned;
	}

	public void setExpaned(boolean isExpaned) {
		mIsExpaned = isExpaned;
	}

	public static CleanGroupsBean getGroup(GroupType type,
			List<CleanGroupsBean> groupList) {
		for (CleanGroupsBean group : groupList) {
			if (group.getGroupType().equals(type)) {
				return group;
			}
		}
		return null;
	}

}
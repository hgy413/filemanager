package com.jb.filemanager.function.trashfiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.jb.filemanager.function.scanframe.bean.BaseGroupsDataBean;

import java.util.List;

/**
 * 
 * @author chaoziliang
 *
 * @param <T>
 */
public abstract class AbsAdapter<T extends BaseGroupsDataBean> extends
		BaseExpandableListAdapter {

	protected List<T> mGroups;
	protected LayoutInflater mLayoutInflater;
	protected Context mContext;

	public AbsAdapter(List<T> groups, Context context) {
		mGroups = groups;
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 获取父类的组件
	 */
	public abstract View onGetGroupView(int groupPosition, boolean isExpanded,
										View convertView, ViewGroup parent);

	/**
	 * 子类需实现此接口，数据加载完毕
	 */
	public abstract View onGetChildView(int groupPosition, int childPosition,
										boolean isLastChild, View convertView, ViewGroup parent);

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroups.get(groupPosition).getchildrenSize();
	}

	@Override
	public T getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).getChild(childPosition);
	}

	public void removeGroup(T t) {
		mGroups.remove(t);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		return onGetGroupView(groupPosition, isExpanded, convertView, parent);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		return onGetChildView(groupPosition, childPosition, isLastChild,
				convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * 设置每一项的背景，黑白间隔
	 * 
	 * @param position
	 * @param view
	 */
	public void setBackground(int position, View view) {
		// if (position % 2 == 0) {
		// view.setBackgroundResource(R.drawable.common_list_item_white_selector);
		// } else {
		//view.setBackgroundResource(R.drawable.common_list_item_gray_white_selector);
		// }
	}
}

package com.jb.filemanager.function.scanframe.bean;

import java.util.List;

/**
 * 
 * @author chaoziliang
 * @param <T>
 * 
 */
public class BaseGroupsDataBean<T> {

	private List<T> mChildren;

	protected BaseGroupsDataBean(List<T> children) {
		mChildren = children;
	}

	public T getChild(int postiton) {
		return mChildren.get(postiton);
	}

	public int getchildrenSize() {
		return mChildren.size();
	}

	public List<T> getChildren() {
		return mChildren;
	}

	public void setChild(List<T> children) {
		mChildren = children;
	}

}

package com.jb.filemanager.function.zipfile.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.util.LinkedList;
import java.util.List;

/**
 * 文件管理器面包屑样式导航
 * @author lishen
 */
public class BreadcrumbNavigation extends HorizontalScrollView implements View.OnClickListener {

	private Context mContext;
	private LinearLayout mBaseLayout;
	private LinearLayout.LayoutParams mLp;
	private LinkedList<BreadcrumbItem> mItems = new LinkedList<BreadcrumbItem>();
	private OnBreadcrumbClickListener mListener;

	public BreadcrumbNavigation(Context context) {
		super(context);
		init(context);
	}

	public BreadcrumbNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		setEnabled(false);
		mLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBaseLayout = (LinearLayout) findViewById(R.id.content);
	}

	public void setOnBreadcrumbClickListener(OnBreadcrumbClickListener listener) {
		mListener = listener;
	}

	/**
	 * 根标签
	 * @param path
	 */
	public void addRootItem(String path) {
		String tag = getTagFromPath(path);
		BreadcrumbItem item = new BreadcrumbItem(mContext, tag, path);
		item.setArrowGone();
		mItems.add(item);
		mBaseLayout.addView(item, mLp);
		updateSelectedState();
	}

	/**
	 * 进入文件夹，添加标签
	 * @param path
	 */
	public void addItem(String path) {
		BreadcrumbItem item = makeItem(path);
		if (item != null) {
			mItems.add(item);
			mBaseLayout.addView(item, mLp);
			updateSelectedState();
			getHandler().post(new Runnable() {
				@Override
				public void run() {
					fullScroll(ScrollView.FOCUS_RIGHT);
				}
			});
		}
	}

	/**
	 * 制造一个面包屑标签
	 * @param path
	 * @return
	 */
	private BreadcrumbItem makeItem(String path) {
		BreadcrumbItem item = null;
		if (!TextUtils.isEmpty(path)) {
			String tag = getTagFromPath(path);
			item = new BreadcrumbItem(mContext, tag, path);
		}
		return item;
	}

	/**
	 * 拿到当前文件夹名作为标签
	 * @param path
	 * @return
	 */
	private String getTagFromPath(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		int index = path.lastIndexOf("/");
		if (index == path.length() - 1) {
			path = path.substring(0, index);
			index = path.lastIndexOf("/");
		}
		return path.substring(index + 1, path.length());
	}

	/**
	 * 返回上一层级
	 */
	public void back() {
		if (mItems.size() > 1) { // 确保根item不被移除
			BreadcrumbItem lastItem = mItems.pollLast();
			if (lastItem != null) {
				mBaseLayout.removeView(lastItem);
			}
		}
		updateSelectedState();
	}

	/**
	 * 最后一个子view为选中状态
	 */
	private void updateSelectedState() {
		final int count = mBaseLayout.getChildCount();
		if (count != 0) {
			for (int i = 0; i < count; i++) {
				View child = mBaseLayout.getChildAt(i);
				if (i == count - 1) {
					child.setSelected(true);
				} else {
					child.setSelected(false);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		BreadcrumbItem item = (BreadcrumbItem) v;
		// 回调点击
		if (mListener != null) {
			mListener.onBreadcrumbClick(item, item.mPath);
		}
		// 将点击项之后的标签移除
		final int size = mItems.size();
		int index = mItems.indexOf(item);
		List<BreadcrumbItem> list = mItems.subList(index + 1, size);
		if (!list.isEmpty()) {
			for (BreadcrumbItem b : list) {
				mBaseLayout.removeView(b);
			}
			list.clear();
		}
		updateSelectedState();
	}

	/**
	 * 面包屑导航item
	 * @author lishen
	 */
	public class BreadcrumbItem extends LinearLayout {
		/** 路径 */
		public String mPath;
		/** 标签 */
		public String mTag;

		private TextView mItem;
		private View mArrow;

		public BreadcrumbItem(Context context, String tag, String path) {
			super(context);
			LayoutInflater.from(context).inflate(R.layout.breadcrumb_item, this);
			mPath = path;
			mTag = tag;
			init();
		}

		/**
		 * 设置属性
		 */
		private void init() {
			mItem = (TextView) findViewById(R.id.item);
			mArrow = findViewById(R.id.arrow);
			mItem.setText(mTag);
			setOnClickListener(BreadcrumbNavigation.this);
		}

		/**
		 * 不显示上级箭头
		 */
		public void setArrowGone() {
			mArrow.setVisibility(GONE);
		}
	}

	/**
	 * 点击标签的回调接口
	 * @author lishen
	 */
	public interface OnBreadcrumbClickListener {
		/**
		 * @param item
		 * @param path
		 */
		public void onBreadcrumbClick(BreadcrumbItem item, String path);
	}

}

package com.jb.filemanager.function.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;


/**
 * 选项列表的弹出对话框
 * 
 * @author lishen
 */
public class ListItemDialog extends BaseDialog implements OnItemClickListener {

	private Context mCtx;
	private TextView mTitle;
	private ListView mList;
	private ArrayAdapter<String> mAdapter;
	private SparseArray<String> mItems = new SparseArray<String>();
	private OnItemClickListener mClickListener;

	public ListItemDialog(Activity act) {
		super(act);
		init();
	}

	public ListItemDialog(Activity act, int style) {
		super(act, style);
		init();
	}

	public ListItemDialog(Activity act, boolean cancelOutside) {
		super(act, cancelOutside);
		init();
	}

	public ListItemDialog(Activity act, int style, boolean cancelOutside) {
		super(act, style, cancelOutside);
		init();
	}

	private void init() {
		mCtx = getContext();
		setContentView(R.layout.dialog_list_layout);
		mTitle = (TextView) findViewById(R.id.title);
		mList = (ListView) findViewById(R.id.list);
		mList.setOnItemClickListener(this);
		mAdapter = new ArrayAdapter<String>(mCtx, R.layout.dialog_list_item);
		mList.setAdapter(mAdapter);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	/**
	 * 设置标题
	 * 
	 * @param id
	 */
	public void setTitle(int id) {
		mTitle.setText(id);
	}

	/**
	 * 添加选项
	 * 
	 * @param id
	 *            选项id，用于点击回调识别
	 * @param title
	 *            选项title
	 */
	public void addItem(int id, String title) {
		mItems.put(id, title);
	}

	public void build() {
		final int size = mItems.size();
		for (int i = 0; i < size; i++) {
			mAdapter.add(mItems.valueAt(i));
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 显示窗口
	 */
	public void showDialog() {
		setSize(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		show();
	}

	/**
	 * 点击回调
	 * 
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		if (mClickListener != null) {
			mClickListener.onItemClick(mItems.keyAt(position), position);
		}
		dismiss();
	}

	/**
	 * 点击回调
	 */
	public interface OnItemClickListener {
		public void onItemClick(int id, int position);
	}
}

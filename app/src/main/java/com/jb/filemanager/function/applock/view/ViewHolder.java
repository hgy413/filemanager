package com.jb.filemanager.function.applock.view;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * View Holder<br>
 * 
 * @author laojiale
 * 
 */
public abstract class ViewHolder {

	private View mContentView;

	/**
	 * 在构造完成后应当调用方法{@link #setContentView(View)} 设置内容视图.<br>
	 */
	public ViewHolder() {

	}

	public ViewHolder(View contentView) {
		setContentView(contentView);
	}

	/**
	 * 设置内容视图<br>
	 * 
	 * @param contentView
	 */
	public final void setContentView(View contentView) {
		mContentView = contentView;
	}

	/**
	 * 获取内容视图<br>
	 * 
	 * @return
	 */
	public final View getContentView() {
		return mContentView;
	}

	/**
	 * 通过id查找内容视图中View.<br>
	 * 在调用前必须已调用{@link #setContentView(View)}
	 * 
	 * @see View.findViewById(int)
	 * @param id
	 * @return
	 */
	public final View findViewById(int id) {
		return mContentView.findViewById(id);
	}

	/**
	 * set the visibility of contentView.<br>
	 * 
	 * @param visibility
	 */
	public void setVisibility(int visibility) {
		mContentView.setVisibility(visibility);
	}
	
	/**
	 * get the visibility of contentView.<br>
	 * 
	 * @return
	 */
	public int getVisibility() {
		return mContentView.getVisibility();
	}

	/**
	 * set the enabled of contentView.<br>
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		mContentView.setEnabled(enabled);
	}

	public TextView getTextView(int id) {
		return (TextView) findViewById(id);
	}

	public ImageView getImageView(int id) {
		return (ImageView) findViewById(id);
	}

	public Button getButton(int id) {
		return (Button) findViewById(id);
	}

	public CheckBox getCheckBox(int id) {
		return (CheckBox) findViewById(id);
	}

	public EditText getEditText(int id) {
		return (EditText) findViewById(id);
	}

}

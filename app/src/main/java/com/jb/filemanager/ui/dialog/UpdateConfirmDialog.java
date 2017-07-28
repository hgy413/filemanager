package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;

/**
 * 
 * @author wangying
 *
 */
public class UpdateConfirmDialog extends BaseDialog {

	private TextView mContentTextView;
	private TextView mNoBtu;
	private TextView mYesBtu;
	private TextView mTitle;
	private ConfirmCommonDialog.OnConfirmDetailListener mOnConfirmDetailListener;

	public UpdateConfirmDialog(Activity act, boolean cancelOutside) {
		super(act, cancelOutside);
		initView();
	}

	void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(TheApplication.getAppContext());
		View contentView = layoutInflater.inflate(R.layout.dialog_update_layout, null);
		mTitle = (TextView) contentView.findViewById(R.id.dialog_update_layout_title);
		mContentTextView = (TextView) contentView.findViewById(R.id.dialog_update_layout_update_content);
		mNoBtu = (TextView) contentView.findViewById(R.id.dialog_update_layout_no_btn);
		mYesBtu = (TextView) contentView.findViewById(R.id.dialog_update_layout_yes_btn);
		setContentView(contentView);
		setOnListener();
	}

	void setOnListener(){
		mYesBtu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnConfirmDetailListener.onConfirm();
				dismiss();
			}
		});

		mNoBtu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnConfirmDetailListener.onCancel();
				dismiss();
			}
		});
	}

	public void setContentText(String str) {
		mContentTextView.setText(str);
	}

	public void setCancelText(int cancelText) {
		mNoBtu.setText(cancelText);
	}

	public void setOkText(int okText) {
		mYesBtu.setText(okText);
	}

	public void setOnConfirmDetailListener(ConfirmCommonDialog.OnConfirmDetailListener onConfirmDetailListener) {
		this.mOnConfirmDetailListener = onConfirmDetailListener;
	}

	public void setTitleText(int titleText) {
		mTitle.setText(titleText);
	}

	public void setCancelGone() {
		mNoBtu.setVisibility(View.GONE);
	}
}
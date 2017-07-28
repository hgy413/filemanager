package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
	private ImageView mNoBtu;
	private TextView mYesBtu;
	private TextView mTitle;
	private ConfirmCommonDialog.OnConfirmDetailListener mOnConfirmDetailListener;

	public UpdateConfirmDialog(Activity act, boolean cancelOutside) {
		super(act, cancelOutside);
		initView();
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getContext().getResources().getDisplayMetrics());
		setSize(getContext().getResources().getDisplayMetrics().widthPixels - 2 * margin, WindowManager.LayoutParams.WRAP_CONTENT);
	}

	void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(TheApplication.getAppContext());
		View contentView = layoutInflater.inflate(R.layout.dialog_update_layout, null);
		mTitle = (TextView) contentView.findViewById(R.id.dialog_update_layout_title);
		mContentTextView = (TextView) contentView.findViewById(R.id.dialog_update_layout_update_content);
		mNoBtu = (ImageView) contentView.findViewById(R.id.dialog_update_layout_no_btn);
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
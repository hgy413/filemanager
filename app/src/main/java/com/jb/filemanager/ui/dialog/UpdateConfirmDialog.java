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
public class UpdateConfirmDialog extends CommonVerifyDialog {

	private TextView mContentTextView;

	public UpdateConfirmDialog(Activity act) {
		super(act);
	}

	@Override
	public View onCreateMiddleView(ViewGroup parent) {
		View contentView = LayoutInflater.from(TheApplication.getAppContext()).inflate(R.layout.layout_update_dialog_contentview, parent, false);
		mContentTextView = (TextView) contentView.findViewById(R.id.layout_update_dialog_update_content);
		return contentView;
	}

	public void setUpdateTipText(String str) {
		mContentTextView.setText(str);
	}

}

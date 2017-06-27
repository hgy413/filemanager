package com.jb.filemanager.function.trashfiles.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 自定义CheckBox
 * 
 * @author chenbenbin
 * 
 */
public class ItemCheckBox extends ImageView {
	private boolean mIsCheck;
	private int mUnCheckRes;
	private int mCheckRes;

	public ItemCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImageRes(int unCheckRes, int checkRes) {
		mUnCheckRes = unCheckRes;
		mCheckRes = checkRes;
	}

	public boolean isIsCheck() {
		return mIsCheck;
	}

	public void setChecked(boolean isCheck) {
		mIsCheck = isCheck;
		setImageResource(isCheck ? mCheckRes : mUnCheckRes);
	}

}

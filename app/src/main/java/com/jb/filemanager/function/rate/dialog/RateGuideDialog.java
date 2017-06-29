package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.statistics.StatisticsConstants;

/**
 * Created by nieyh on 2016/7/12.
 */
public class RateGuideDialog extends RateDialog {

	private final String TAG = "RateGuideDialog";
	private TextView mNoBtu;
	private TextView mYesBtu;

	public RateGuideDialog(Activity act) {
		super(act);
	}

	@Override
	void initView() {
		LayoutInflater layoutInflater = LayoutInflater.from(TheApplication.getAppContext());
		View contentView = layoutInflater.inflate(R.layout.dialog_gospeed_cheers, null);
		mNoBtu = (TextView) contentView.findViewById(R.id.googleplay_cheers_rate_no_btn);
		mYesBtu = (TextView) contentView.findViewById(R.id.googleplay_cheers_rate_yes_btn);
		setContentView(contentView);
		setOnListener();
		setSize(WindowManager.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, getContext().getResources().getDisplayMetrics()));
	}

	/**
	 * 设置所有控件监听器
	 */
	private void setOnListener() {
		mNoBtu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mQuickClickGuard.isQuickClick(view.getId())) {
					return;
				}
				if (mOnPressListener != null) {
					mOnPressListener.pressNo();
				}
				statisticsClick(1);
			}
		});
		mYesBtu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mQuickClickGuard.isQuickClick(view.getId())) {
					return;
				}
				if (mOnPressListener != null) {
					mOnPressListener.pressYes();
				}
				statisticsClick(2);
			}
		});
	}

	@Override
	public void show() {
		super.show();
		SharedPreferencesManager mSharedPreferencesManager = SharedPreferencesManager.getInstance(mActivity);
		boolean isSecondShow = mSharedPreferencesManager.getBoolean(IPreferencesIds.KEY_IS_SHOW_DIALOG_ONE, false);
		int tag = isSecondShow ? 2 : 1;
//		statisticsShow(StatisticsConstants.RATE_ONE_DIALOG_SHOW, tag);
		mSharedPreferencesManager.commitBoolean(IPreferencesIds.KEY_IS_SHOW_DIALOG_ONE, true);
	}

}

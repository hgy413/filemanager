package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;

import com.jb.filemanager.ui.dialog.BaseDialog;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Created by nieyh on 2016/9/2.
 */
public abstract class RateDialog extends BaseDialog {
	protected final int FIRST_SHOW = 1;
	protected final int SECOND_SHOW = 2;
	protected QuickClickGuard mQuickClickGuard = new QuickClickGuard();
	protected OnPressListener mOnPressListener;

	public RateDialog(Activity act) {
		super(act);
		initView();
	}

	abstract void initView();


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mOnPressListener != null) {
			mOnPressListener.pressBack();
		}
	}

	/**
	 * 上传101显示统计协议
	 */
	protected void statisticsShow(String operateId, int tag) {
//		Statistics101Bean statistics101Bean = Statistics101Bean.builder();
//		statistics101Bean.mOpeateId = operateId;
//		statistics101Bean.mTab = String.valueOf(tag);
//		StatisticsTools.upload101InfoNew(statistics101Bean);
	}

	/**
	 * 上传101点击统计协议
	 */
	protected void statisticsClick(int tag) {
//		Statistics101Bean statistics101Bean = Statistics101Bean.builder();
//		statistics101Bean.mOpeateId = StatisticsConstants.RATE_DIALOG_BTU_CLICK;
//		statistics101Bean.mTab = String.valueOf(tag);
//		StatisticsTools.upload101InfoNew(statistics101Bean);
	}

	public void setOnPressListener(OnPressListener mOnPressListener) {
		this.mOnPressListener = mOnPressListener;
	}

	public OnPressListener getOnPressListener() {
		return mOnPressListener;
	}

	/**
	 * 点击事件监听器
	 */
	public interface OnPressListener {
		void pressBack();

		void pressYes();

		void pressNo();
	}
}

package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.function.rate.dialog.RateDialog;

/**
 * Created by nieyh on 2016/9/7.
 */
public interface RateContract {

	interface View {
		void showCheerDialog(RateDialog.OnPressListener onPressListener);

		void showFeedBackDialog(RateDialog.OnPressListener onPressListener);

		void showLoveDialog(RateDialog.OnPressListener onPressListener);

		void dismissCheerDialog();

		void dismissFeedBackDialog();

		void dismissLoveDialog();

		void gotoFeedBack();

		boolean gotoGp();
	}

	interface Support {
		boolean isRateSuccess();

		boolean isShortTimeExtGp();

		void commitShortTimeExtGp(boolean is);

		void commitRateSuccess();

		long getFirstInstallTime();

		long getExtGpTime();

		void setExtGpTime(long time);

		boolean isClickDialogNo();

		void commitClickDialogNo();

		int getRateAppearTimes();

		void commitRateAppearTimes(int times);

		void release();
	}

	interface Presenter {
		boolean rateShow();

		void clickRateCheerNo();

		void clickRateCheerYes();

		void clickRateFeedbackNo();

		void clickRateFeedbackYes();

		void clickRateLoveNo();

		void clickRateLoveYes();

		void pressToBack();

		void onResume(long time);

		void release();
	}
}

package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.function.rate.dialog.AbsRateDialog;

/**
 * Created by nieyh on 2016/9/7.
 */
public interface RateContract {

    interface View {
        void showCheerDialog(AbsRateDialog.OnPressListener onPressListener);

        void showFeedBackDialog(AbsRateDialog.OnPressListener onPressListener);

        void showLoveDialog(AbsRateDialog.OnPressListener onPressListener);

        void dismissCheerDialog();

        void dismissFeedBackDialog();

        void dismissLoveDialog();

        void gotoFeedBack();

        boolean gotoGp();
    }

    interface Support {
        void setRateSuccess();

        boolean isCanShow();
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

        void release();
    }
}

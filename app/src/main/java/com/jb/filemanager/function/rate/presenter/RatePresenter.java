package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.function.rate.dialog.AbsRateDialog;

/**
 * Created by nieyh on 2016/9/7.
 */
public class RatePresenter implements RateContract.Presenter {

    private final String TAG = "RatePresenter";

    private RateContract.View mView;

    private RateContract.Support mSupport;

    public RatePresenter(RateContract.View mView, RateContract.Support mSupport) {
        this.mView = mView;
        this.mSupport = mSupport;
    }

    @Override
    public boolean rateShow() {
        boolean isShow = isCanShow();
        if (isShow) {
            mView.showCheerDialog(new AbsRateDialog.OnPressListener() {
                @Override
                public void pressBack() {
                    pressToBack();
                }

                @Override
                public void pressYes() {
                    clickRateCheerYes();
                }

                @Override
                public void pressNo() {
                    clickRateCheerNo();
                }
            });
        }
        return isShow;
    }

    /**
     * 是否弹出
     */
    private boolean isCanShow() {
        return mSupport.isCanShow();
    }

    @Override
    public void clickRateCheerNo() {
        mSupport.setRateSuccess();
        mView.dismissCheerDialog();
        mView.showFeedBackDialog(new AbsRateDialog.OnPressListener() {
            @Override
            public void pressBack() {
                pressToBack();
            }

            @Override
            public void pressYes() {
                clickRateFeedbackYes();
            }

            @Override
            public void pressNo() {
                clickRateFeedbackNo();
            }
        });
        setRateSuccess();
    }

    @Override
    public void clickRateCheerYes() {
        mSupport.setRateSuccess();
        mView.dismissCheerDialog();
        mView.showLoveDialog(new AbsRateDialog.OnPressListener() {
            @Override
            public void pressBack() {
                pressToBack();
            }

            @Override
            public void pressYes() {
                clickRateLoveYes();
            }

            @Override
            public void pressNo() {
                clickRateLoveNo();
            }
        });
    }

    @Override
    public void clickRateFeedbackNo() {
        setRateSuccess();
        mView.dismissFeedBackDialog();
    }

    @Override
    public void clickRateFeedbackYes() {
        mView.dismissFeedBackDialog();
        mView.gotoFeedBack();
    }

    @Override
    public void clickRateLoveNo() {
        setRateSuccess();
        mView.dismissLoveDialog();
    }

    @Override
    public void clickRateLoveYes() {
        mView.dismissLoveDialog();
        mView.gotoGp();
    }

    @Override
    public void pressToBack() {
        setRateSuccess();
    }

    @Override
    public void release() {
        mSupport = null;
        mView = null;
    }

    /**
     * 保存状态
     */
    private void setRateSuccess() {
        //设置点击No 设置为满足一定条件可再次弹出
        mSupport.setRateSuccess();
    }
}

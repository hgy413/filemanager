package com.jb.filemanager.function.rate.presenter;

import com.jb.filemanager.function.rate.dialog.RateDialog;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;

/**
 * Created by nieyh on 2016/9/7.
 */
public class RatePresenter implements RateContract.Presenter {

	private final String TAG = "RatePresenter";

	enum RateType {
		GuideDlg,
		FeedbackDlg,
		ToGpDlg,
		Unvalid
	}

	private RateContract.View mView;

	private RateContract.Support mSupport;

	private static long mGpStartTime;

	private RateType mRateType = RateType.Unvalid;
	//是否符合弹出条件
	private static boolean sAchieveRateCondition = false;
	//离开gp的阈值
	public static final long EXIT_GP_THRESHOLD_TIME = 5 * 1000;

	public RatePresenter(RateContract.View mView, RateContract.Support mSupport) {
		this.mView = mView;
		this.mSupport = mSupport;
	}

	@Override
	public boolean rateShow() {
		boolean isShow = false;
		if (sAchieveRateCondition) {
			isShow = isCanShow();
			if (isShow) {
				mView.showCheerDialog(new RateDialog.OnPressListener() {
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
				int appearCount = mSupport.getRateAppearTimes();
				mSupport.commitRateAppearTimes(++appearCount);
			}
			sAchieveRateCondition = false;
		}
		return isShow;
	}

	/**
	 * 是否弹出
	 */
	private boolean isCanShow() {
		//评分引导最多出现2次
		int apearTimes = mSupport.getRateAppearTimes();
		if (apearTimes >= 2) {
			return false;
		}
		if (mSupport.isClickDialogNo()) {
			//返回是否安装时间达到三天或者超过三天
			if (!isInstallTimeOver3Days()) {
				return false;
			}
		}
		//当评论没有成功 则再判断 离开时间
		if (!mSupport.isRateSuccess()) {
			// Gp未评价 之后的第三天或者之后时间
			long lastDismissTime = mSupport.getExtGpTime();
			int days = 0;
			try {
				days = TimeUtil.calcDifferenceDays(lastDismissTime, System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
			}
			//如果短时间内离开
			if (mSupport.isShortTimeExtGp()) {
				if (days < 2) {
					return false;
				} else {
					//重新初始化 改为默认没有离开
					mSupport.commitShortTimeExtGp(false);
				}
			}
		} else
			//当评论成功后 不再弹出对话框
			return false;
		//默认弹出
		return true;
	}

	@Override
	public void clickRateCheerNo() {
		mView.dismissCheerDialog();
		mView.showFeedBackDialog(new RateDialog.OnPressListener() {
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
		savePressNoState();
		mRateType = RateType.Unvalid;
	}

	@Override
	public void clickRateCheerYes() {
		mView.dismissCheerDialog();
		mView.showLoveDialog(new RateDialog.OnPressListener() {
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
		mRateType = RateType.GuideDlg;
	}

	@Override
	public void clickRateFeedbackNo() {
		savePressNoState();
		mView.dismissFeedBackDialog();
		mRateType = RateType.Unvalid;
	}

	@Override
	public void clickRateFeedbackYes() {
		mView.dismissFeedBackDialog();
		mView.gotoFeedBack();
		mRateType = RateType.FeedbackDlg;
	}

	@Override
	public void clickRateLoveNo() {
		savePressNoState();
		mView.dismissLoveDialog();
		mRateType = RateType.Unvalid;
	}

	@Override
	public void clickRateLoveYes() {
		mView.dismissLoveDialog();
		boolean isGoto = mView.gotoGp();
		mGpStartTime = System.currentTimeMillis();
		mRateType = RateType.ToGpDlg;
		Logger.w(TAG, String.valueOf(isGoto));
		if (!isGoto) {
			//没跳转成功 与五秒内一样的处理
			onResume(mGpStartTime);
		}
	}

	@Override
	public void pressToBack() {
		savePressNoState();
		mRateType = RateType.Unvalid;
	}

	@Override
	public void onResume(long time) {
		if (mRateType == RateType.ToGpDlg) {
			//mGpStartTime可以过滤不是点击Ok返回来的
			if (mGpStartTime > 0) { // 从GP跳转回来
				if ((time - mGpStartTime) < EXIT_GP_THRESHOLD_TIME) { // 5秒没有评分，等同点击了消失
					saveDismissTime();
					//设置为短时间内离开
					mSupport.commitShortTimeExtGp(true);
				} else {
					//超过5秒 则为评论成功
					mSupport.commitRateSuccess();
				}
				mGpStartTime = 0;
			}
			mRateType = RateType.Unvalid;
		}
	}

	@Override
	public void release() {
		if (mSupport != null) {
			mSupport.release();
			mSupport = null;
		}
		mView = null;
	}

	/**
	 * 保存上次 对话框消失时间
	 */
	private void saveDismissTime() {
		// 保存下时间
		mSupport.setExtGpTime(System.currentTimeMillis());
	}

	/**
	 * 安装时间第3天或者之后
	 */
	private boolean isInstallTimeOver3Days() {
		long installTime = mSupport.getFirstInstallTime();
		//自安装之后的天数
		int days = 0;
		try {
			days = TimeUtil.calcDifferenceDays(installTime, System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//天数在第三天或者之后
		return days >= 2;
	}

	/**
	 * 保存状态
	 */
	private void savePressNoState() {
		//设置点击No 设置为满足一定条件可再次弹出
		mSupport.commitClickDialogNo();
	}

	public static boolean isAchieveRateCondition() {
		return sAchieveRateCondition;
	}

	public static void setAchieveRateCondition(boolean achieveRateCondition) {
		sAchieveRateCondition = achieveRateCondition;
	}
}

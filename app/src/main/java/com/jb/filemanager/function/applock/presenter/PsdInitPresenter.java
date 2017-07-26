package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.function.applock.view.PatternView;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;

import java.util.ArrayList;
import java.util.List;

import static com.jb.filemanager.function.applock.activity.PsdSettingActivity.PSD_INIT;
import static com.jb.filemanager.function.applock.activity.PsdSettingActivity.PSD_REST;
import static com.jb.filemanager.function.applock.activity.PsdSettingActivity.QUESTION_REST;

/**
 * Created by nieyh on 2017/1/1.
 */

public class PsdInitPresenter implements PsdInitContract.Presenter {

    /**
     * 此处模式代表是 初始化 密码 还是 重置密码
     */
    private int mMode = PSD_INIT;

    private final int MIN_PATTERN_CELL_NUM = 4;

    private PsdInitContract.View mView;

    private PsdInitContract.Support mSupport;
    //当前第几步
    private int mCurrentStep = 0;
    //第一次输入的密码
    private List<PatternView.Cell> mInitPatternPsdCells;

    public PsdInitPresenter(PsdInitContract.View view, PsdInitContract.Support support) {
        this.mView = view;
        this.mSupport = support;
    }

    @Override
    public void start() {
        if (mView != null) {
            if (mMode != QUESTION_REST) {
                mView.showPsdViewDismissQuestion();
                mView.showStepTopPatternTip(1);
            } else {
                mView.showStepTopPatternTip(3);
                mView.showProblemViewDismissPsd();
            }
        }
    }

    @Override
    public void cachePattern(List<PatternView.Cell> currentCells) {
        if (mView != null) {
            switch (mCurrentStep) {
                case 0:
                    if (currentCells.size() < MIN_PATTERN_CELL_NUM) {
                        mView.showPatternError();
                        delayClearErrorPattern();
                        mView.showPatternShort();
                    } else {
                        mInitPatternPsdCells = new ArrayList<>(currentCells.size());
                        for (PatternView.Cell cell : currentCells) {
                            mInitPatternPsdCells.add(cell);
                        }
                        mView.clearPsd();
                        dealPatternStart();
                        mView.showStepTopPatternTip(2);
                        mCurrentStep++;
                    }
                    break;
                case 1:
                    boolean isSame = true;
                    if (mInitPatternPsdCells != null && currentCells.size() == mInitPatternPsdCells.size()) {
                        for (int i = 0; i < currentCells.size(); i++) {
                            if (!currentCells.get(i).equals(mInitPatternPsdCells.get(i))) {
                                isSame = false;
                                break;
                            }
                        }
                    } else {
                        isSame = false;
                    }
                    if (!isSame) {
                        mView.showPatternError();
                        delayClearErrorPattern();
                        mView.showPatternDiffTip();
                        return;
                    } else {
                        mView.clearPsd();
                        dealPatternStart();
                        if (mMode == PSD_INIT) {
                            mView.showStepTopPatternTip(3);
                            mView.showProblemViewDismissPsd();
                        } else {
                            //重置密码
                            if (mSupport != null) {
                                mSupport.updatePasscode(mInitPatternPsdCells.toString(), true);
                            }
                            mView.toBack(/*false*/);
                        }
                        mCurrentStep++;
                    }
                    break;
            }
        }
    }

    @Override
    public void dealSaveSecureProblem() {
        if (mView != null) {
            String question = mView.getProblem();
            String answer = mView.getAnswer();
            int currentPos = mView.getLockOptions();
            if (TextUtils.isEmpty(answer)) {
                mView.showAnswerShortTip();
                return;
            }
            if (mMode == PSD_INIT) {
                mView.setResult(mInitPatternPsdCells.toString(), question, answer, currentPos == 0);
            } else if (mMode == QUESTION_REST) {
                mSupport.updateIssureQuestion(question, answer);
                mView.toBack(/*false*/);
            }
        }
    }

    @Override
    public void dealBackPress(boolean isSystemBack) {
        if (mView != null) {
            if (mMode != QUESTION_REST) {
                String entrace = isSystemBack ? String.valueOf("1") : String.valueOf("2");
                switch (mCurrentStep) {
                    case 0:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_INIT_PSD_1_EXIT, entrace);
                        break;
                    case 1:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_INIT_PSD_2_EXIT, entrace);
                        break;
                    case 2:
                        StatisticsTools.upload(StatisticsConstants.APPLOCK_INIT_PSD_3_EXIT, entrace);
                        break;
                }
                if (mCurrentStep != 0) {
                    mCurrentStep = 0;
                    mView.showPsdViewDismissQuestion();
                    mView.showStepTopPatternTip(1);
                    mView.clearPsd();
                    dealPatternStart();
                } else {
                    mView.toBack(/*isSystemBack*/);
                }
            } else {
                mView.toBack(/*isSystemBack*/);
            }
        }
    }

    @Override
    public void setMode(int mode) {
        if (mView != null) {
            mMode = mode;
            if (mode == PSD_REST) {
                mView.invisiableStep3();
            }
        }
    }

    /**
     * 延时清理图案密码
     */
    private Runnable mDelayClearPattern = new Runnable() {
        @Override
        public void run() {
            if (mView != null) {
                mView.clearPsd();
            }
        }
    };

    /**
     * 延长消失的时长 800毫秒
     */
    private final long DELAY_CLEAR_ERROR_TIME_LONG = 1000;

    /**
     * 延时执行清除任务
     */
    private void delayClearErrorPattern() {
        if (mSupport != null) {
            mSupport.toUiWork(mDelayClearPattern, DELAY_CLEAR_ERROR_TIME_LONG);
        }
    }

    @Override
    public void dealPatternStart() {
        if (mSupport != null) {
            mSupport.removeUiWork(mDelayClearPattern);
        }
    }

    @Override
    public void release() {
        mSupport = null;
        mView = null;
    }
}

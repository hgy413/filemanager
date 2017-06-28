package com.jb.filemanager.function.applock.presenter;

import android.text.TextUtils;

import com.jb.filemanager.function.applock.view.PatternView;

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
    private int mMode;

    private final int MIN_PATTERN_CELL_NUM = 4;
    /**
     * 当前是否是图案密码
     */
    private boolean isPatternPsd;

    private PsdInitContract.View mView;

    private PsdInitContract.Support mSupport;
    //当前第几步
    private int mCurrentStep = 0;
    //第一次输入的密码
    private List<PatternView.Cell> mInitPatternPsdCells;

    private String[] mInitNumberPsd;

    public PsdInitPresenter(PsdInitContract.View view, PsdInitContract.Support support) {
        this.mView = view;
        this.mSupport = support;
    }

    @Override
    public void start() {
        if (mView != null) {
            if (mMode != QUESTION_REST) {
                mView.showPsdViewDismissQuestion(isPatternPsd);
                if (isPatternPsd) {
                    mView.showStepTopPatternTip(1);
                    mView.showNumberSwitch();
                } else {
                    mView.showStepTopNumberTip(1);
                    mView.showPatternSwitch();
                }
                mView.showStep(1, true);
                mView.showStep(2, false);
                if (mMode == PSD_INIT) {
                    mView.showStep(3, false);
                }
            } else {
                mView.showStepTopPatternTip(3);
                mView.dismissStepView();
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
                        mView.showLockerSwitch();
                    } else {
                        mInitPatternPsdCells = new ArrayList<>(currentCells.size());
                        for (PatternView.Cell cell : currentCells) {
                            mInitPatternPsdCells.add(cell);
                        }
                        mView.dismissLockerSwitch();
                        mView.clearPsd(isPatternPsd);
                        dealPatternStart();
                        mView.showStep(1, false);
                        mView.showStepTopPatternTip(2);
                        mView.showStep(2, true);
                        if (mMode == PSD_INIT) {
                            mView.showStep(3, false);
                        }
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
                        mView.clearPsd(isPatternPsd);
                        dealPatternStart();
                        mView.showStep(1, false);
                        mView.showStep(2, false);
                        if (mMode == PSD_INIT) {
                            mView.showStepTopPatternTip(3);
                            mView.showStep(3, true);
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
    public void cacheNumber(String[] number) {
        // TODO: 2017/2/27 数字密码
        if (mView != null) {
            switch (mCurrentStep) {
                case 0:
                    mView.dismissLockerSwitch();
                    mInitNumberPsd = number;
                    mView.showStep(1, false);
                    mView.showStepTopNumberTip(2);
                    mView.showStep(2, true);
                    if (mMode == PSD_INIT) {
                        mView.showStep(3, false);
                    }
                    mCurrentStep++;
                    break;
                case 1:
                    boolean isSame = true;
                    if (mInitNumberPsd != null) {
                        for (int i = 0; i < mInitNumberPsd.length; i++) {
                            if (!mInitNumberPsd[i].equals(number[i])) {
                                isSame = false;
                                break;
                            }
                        }
                    } else {
                        isSame = false;
                    }
                    if (!isSame) {
                        mView.showPatternDiffTip();
                        mView.showNumberErrorAnim();
                        return;
                    } else {
                        mView.showStep(1, false);
                        mView.showStep(2, false);
                        if (mMode == PSD_INIT) {
                            mView.showStepTopNumberTip(3);
                            mView.showStep(3, true);
                            mView.showProblemViewDismissPsd();
                        } else {
                            //重置密码
                            if (mSupport != null) {
                                StringBuilder result = new StringBuilder();
                                for (String s : mInitNumberPsd) {
                                    result.append(s);
                                }
                                mSupport.updatePasscode(result.toString(), false);
                            }
                            mView.toBack();
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
            if (TextUtils.isEmpty(answer)) {
                mView.showAnswerShortTip();
                return;
            }
            if (mMode == PSD_INIT) {
                if (isPatternPsd) {
                    mView.setResult(isPatternPsd, mInitPatternPsdCells.toString(), question, answer);
                } else {
                    if (mInitNumberPsd != null) {
                        StringBuilder result = new StringBuilder();
                        for (String s : mInitNumberPsd) {
                            result.append(s);
                        }
                        mView.setResult(isPatternPsd, result.toString(), question, answer);
                    }
                }
            } else if (mMode == QUESTION_REST) {
                mSupport.updateIssureQuestion(question, answer);
                mView.toBack(/*false*/);
            }
        }
    }

    @Override
    public void dealBackPress(/*boolean isSystemBack*/) {
        if (mView != null) {
            if (mMode != QUESTION_REST) {
                if (mCurrentStep != 0) {
                    mCurrentStep = 0;
                    mView.showPsdViewDismissQuestion(isPatternPsd);
                    if (isPatternPsd) {
                        mView.showStepTopPatternTip(1);
                    } else {
                        mView.showStepTopNumberTip(1);
                    }
                    mView.showStep(1, true);
                    mView.showStep(2, false);
                    if (mMode == PSD_INIT) {
                        mView.showStep(3, false);
                    }
                    mView.clearPsd(isPatternPsd);
                    dealPatternStart();
                    mView.showLockerSwitch();
                    mView.cleanQuestionCache();
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

    @Override
    public void chgLockerType() {
        isPatternPsd = !isPatternPsd;
        if (isPatternPsd) {
            mView.showPatternPsdView();
            mView.showNumberSwitch();
            mView.showStepTopPatternTip(1);
        } else {
            mView.showPatternSwitch();
            mView.showNumberPsdView();
            mView.showStepTopNumberTip(1);
        }
        mView.clearPsd(isPatternPsd);
    }

    @Override
    public void setPsdType(boolean isPatternPsd) {
        this.isPatternPsd = isPatternPsd;
        if (mMode == QUESTION_REST) {
            mView.dismissLockerSwitch();
        } else {
            if (isPatternPsd) {
                mView.showPatternPsdView();
            } else {
                mView.showNumberPsdView();
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
                mView.clearPsd(isPatternPsd);
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
    public void dealPasscodeInput() {
        if (mCurrentStep == 0) {
            mView.dismissLockerSwitch();
        }
    }

    @Override
    public void dealPasscodeAllDeleted() {
        if (mCurrentStep == 0) {
            mView.showLockerSwitch();
        }
    }

    @Override
    public String getBackStatisticConst(boolean isSystemBack) {
        if (mMode == PSD_INIT) {
//            switch (mCurrentStep) {
//                case 0:
//                    if (isSystemBack) {
//                        return StatisticsConst.APP_LOCK_SET_PSD_1_BACK2;
//                    } else {
//                        return StatisticsConst.APP_LOCK_SET_PSD_1_BACK1;
//                    }
//                case 1:
//                    if (isSystemBack) {
//                        return StatisticsConst.APP_LOCK_SET_PSD_2_BACK2;
//                    } else {
//                        return StatisticsConst.APP_LOCK_SET_PSD_2_BACK1;
//                    }
//                case 2:
//                    if (isSystemBack) {
//                        return StatisticsConst.APP_LOCK_SET_PSD_3_BACK2;
//                    } else {
//                        return StatisticsConst.APP_LOCK_SET_PSD_3_BACK1;
//                    }
//            }
        }
        return null;
    }

    @Override
    public String getHomeStatisticConst() {
        if (mMode == PSD_INIT) {
//            switch (mCurrentStep) {
//                case 0:
//                    return StatisticsConst.APP_LOCK_SET_PSD_1_HOME;
//                case 1:
//                    return StatisticsConst.APP_LOCK_SET_PSD_2_HOME;
//                case 2:
//                    return StatisticsConst.APP_LOCK_SET_PSD_3_HOME;
//            }
        }
        return null;
    }

    @Override
    public void release() {
        if (mSupport != null) {
            mSupport.release();
        }
        mView = null;
    }
}

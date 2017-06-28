package com.jb.filemanager.function.applock.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.dialog.QuestionListDialog;
import com.jb.filemanager.function.applock.presenter.PsdInitContract;
import com.jb.filemanager.function.applock.presenter.PsdInitPresenter;
import com.jb.filemanager.function.applock.presenter.PsdInitSupport;
import com.jb.filemanager.function.applock.view.NumberLockerView;
import com.jb.filemanager.function.applock.view.PatternView;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.Logger;

import java.util.List;

/**
 * 1、输入密码 <br/>
 * 2、确认密码 <br/>
 * 3、输入找回密码的问题 <br/>
 *
 * @author nieyh
 * @version 1.0.1
 * @describe 应用锁的第一次打开时候的密码初始化界面
 * @date 2016/12/27
 */
public class PsdSettingActivity extends BaseHomeWatcherActivity implements PsdInitContract.View {

    public static final int PSD_REST = 1;

    public static final int PSD_INIT = 2;

    public static final int QUESTION_REST = 3;

    public static final String PSD_SETTING_MODE = "psd_setting_mode";

    public static final String PSD_GRAPHICAL_PASSCODE = "psd_graphical_passcode";

    public static final String PSD_QUESTION_WORD = "psd_question_word";

    public static final String PSD_ANSWER_WORD = "psd_answer_word";

    public static final String PSD_DEFAULT_TYPE_IS_PATTERN = "psd_default_type_is_pattern";

    private final String TAG = "PsdSettingActivity";

    private PatternView mPatternView;

    private ImageView mLockTypeImg;

    private NumberLockerView mNumberLockerView;

    private View mLockTypeSwitch;

    private View mRootGradientBg;

    private TextView mStepTxt1;

    private TextView mStepTxt2;

    private View mStepLine2;

    private TextView mStepTxt3;

    private View mStepLayout;

    private PsdInitContract.Presenter mPresenter;

    private View mBack;

    private View mTitle;

    private TextView mTopTip;

    private View mSetProblemLayout;

    private View mQuestionBotLine;

    private TextView mQuestionTip;

    private EditText mAnswer;

    private View mQuestionLayout;

    private QuestionListDialog mQuestionListDialog;

    private View mSave;

    private ImageView mArrow;

    private float mDefaultMargin;
    private float mDefaultSSize;
    private float mDefaultLSize;

    private ValueAnimator mRotateAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psd_initialize);
        initView();
        initListener();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mPresenter = new PsdInitPresenter(this, new PsdInitSupport());
        mPatternView = (PatternView) findViewById(R.id.activity_psd_initialize_patternview);
        mLockTypeSwitch = findViewById(R.id.activity_psd_initialize_lock_type_layout);
        mLockTypeImg = (ImageView) findViewById(R.id.activity_psd_initialize_lock_type);
        mNumberLockerView = (NumberLockerView) findViewById(R.id.activity_psd_initialize_numbserlockerview);
        mNumberLockerView.setColorStyle(NumberLockerView.DARK_STYLE);
        mRootGradientBg = findViewById(R.id.activity_psd_initialize_root_bg);
        mStepTxt1 = (TextView) findViewById(R.id.activity_psd_initialize_step_1);
        mStepTxt2 = (TextView) findViewById(R.id.activity_psd_initialize_step_2);
        mStepTxt3 = (TextView) findViewById(R.id.activity_psd_initialize_step_3);
        mStepLayout = findViewById(R.id.activity_psd_initialize_step_layout);
        mStepLine2 = findViewById(R.id.activity_psd_initialize_step_2_3_line);
        mTopTip = (TextView) findViewById(R.id.activity_psd_initialize_top_tip);
        mSetProblemLayout = findViewById(R.id.activity_psd_initialize_set_problem);
        mQuestionTip = (TextView) findViewById(R.id.activity_psd_initialize_show);
        mQuestionBotLine = findViewById(R.id.activity_psd_initialize_bottom_line);
        mQuestionLayout = findViewById(R.id.activity_psd_initialize_question_layout);
        mSave = findViewById(R.id.activity_psd_initialize_question_save);
        mAnswer = (EditText) findViewById(R.id.activity_psd_initialize_question_input);
        mDefaultMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        mDefaultSSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 21, getResources().getDisplayMetrics());
        mDefaultLSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics());
        mQuestionTip.setText(getResources().getStringArray(R.array.pupup_window_reset_psd_questions)[QuestionListDialog.WEN_BIRTHDAY]);
        ((TextView) findViewById(R.id.activity_title_word)).setText(R.string.activity_applock_title);
        mBack = findViewById(R.id.activity_title_icon);
        mTitle = findViewById(R.id.activity_title_word);
        initGradient();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mode = bundle.getInt(PSD_SETTING_MODE);
            mPresenter.setMode(mode);
            boolean isPatternPsd = bundle.getBoolean(PSD_DEFAULT_TYPE_IS_PATTERN, true);
            mPresenter.setPsdType(isPatternPsd);
        }
        mPresenter.start();
    }

    private void initGradient() {
        int startColor = 0xff0084ff;
        int endColor = 0xff3bd6f2;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(mRootGradientBg, gradientDrawableLR);
        mArrow = (ImageView) findViewById(R.id.activity_psd_initialize_arrow);
        //执行变色
        mArrow.setColorFilter(0xffcccccc, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 初始化所有监听器
     */
    private void initListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId()) && mPresenter != null) {
                    mPresenter.dealBackPress();
                }

            }
        });

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId()) && mPresenter != null) {
                    mPresenter.dealBackPress();
                }
            }
        });

        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                if (mPresenter != null) {
                    mPresenter.dealPatternStart();
                    mPresenter.dealPasscodeInput();
                }
            }

            @Override
            public void onPatternCleared() {
            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> pattern) {
                Logger.w(TAG, pattern.toString());
                if (mPresenter != null) {
                    mPresenter.cachePattern(pattern);
                }
            }
        });

        mNumberLockerView.setOnNumberListener(new NumberLockerView.OnNumberListener() {
            @Override
            public void onNumberFinish(String[] numbers) {
                if (mPresenter != null) {
                    mPresenter.cacheNumber(numbers);
                }
            }

            @Override
            public void onNumberInput(String number) {
                if (mPresenter != null) {
                    mPresenter.dealPasscodeInput();
                }
            }

            @Override
            public void onNumberAllDeleted() {
                if (mPresenter != null) {
                    mPresenter.dealPasscodeAllDeleted();
                }
            }
        });

        mQuestionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionListDialog == null) {
                    mQuestionListDialog = new QuestionListDialog(mQuestionTip, QuestionListDialog.WEN_BIRTHDAY);
                    mQuestionListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //消失 执行旋转动画
                            arrowRotateAnim(false);
                            //分割线 变灰色
                            mQuestionBotLine.setBackgroundColor(0xffcccccc);
                        }
                    });
                }
                mQuestionListDialog.showUnderView(mQuestionBotLine);
                //显示 执行旋转动画
                arrowRotateAnim(true);
                //分割线 变蓝色
                mQuestionBotLine.setBackgroundColor(0xff219eff);
            }
        });

        mLockTypeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    mPresenter.chgLockerType();
                }
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPresenter != null) {
                    //保存答案
                    mPresenter.dealSaveSecureProblem();
                }
            }
        });
    }

    /**
     * 箭头旋转动画
     */
    private void arrowRotateAnim(final boolean isPopUpWindowShow) {
        if (mRotateAnimator == null) {
            mRotateAnimator = ValueAnimator.ofFloat(0 , 1);
        }
        mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isPopUpWindowShow) {
                    float now = (float) animation.getAnimatedValue();
                    float rotate = -90 + now * 180;
                    mArrow.setRotation(rotate);
                } else {
                    float now = (float) animation.getAnimatedValue();
                    now = 1 - now;
                    float rotate = -90 + now * 180;
                    mArrow.setRotation(rotate);
                }
            }
        });
        mRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isPopUpWindowShow) {
                    //执行变色
                    mArrow.setColorFilter(0xff219eff, PorterDuff.Mode.SRC_ATOP);
                } else {
                    //执行变色
                    mArrow.setColorFilter(0xffcccccc, PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
        mRotateAnimator.start();
    }

    @Override
    public void showStepTopPatternTip(int step) {
        switch (step) {
            case 1:
                mTopTip.setText(R.string.set_graphic_password_message_draw);
                break;
            case 2:
                mTopTip.setText(R.string.set_graphic_password_message_redraw);
                break;
            case 3:
                mTopTip.setText(R.string.set_graphic_password_message_set_question);
                break;
        }
    }

    @Override
    public void showStepTopNumberTip(int step) {
        switch (step) {
            case 1:
                mTopTip.setText(R.string.set_number_password_message_draw);
                break;
            case 2:
                mTopTip.setText(R.string.set_number_password_message_redraw);
                break;
            case 3:
                mTopTip.setText(R.string.set_graphic_password_message_set_question);
                break;
        }
    }

    @Override
    public void showPsdViewDismissQuestion(boolean isPatternPsd) {
        if (isPatternPsd) {
            mPatternView.setVisibility(View.VISIBLE);
        } else {
            mNumberLockerView.setVisibility(View.VISIBLE);
        }
        mSetProblemLayout.setVisibility(View.GONE);
    }

    @Override
    public void showProblemViewDismissPsd() {
        mPatternView.setVisibility(View.GONE);
        mNumberLockerView.setVisibility(View.GONE);
        mSetProblemLayout.setVisibility(View.VISIBLE);
    }

    /**
     * @param step    具体第几步
     * @param isLarge 是否显示为大圆
     */
    @Override
    public void showStep(int step, boolean isLarge) {
        TextView temp = null;
        switch (step) {
            case 1:
                temp = mStepTxt1;
                break;
            case 2:
                temp = mStepTxt2;
                break;
            case 3:
                temp = mStepTxt3;
                break;
        }
        if (temp == null) {
            return;
        }
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) temp.getLayoutParams();
        if (isLarge) {
            layoutParams1.leftMargin = 0;
            layoutParams1.rightMargin = 0;
            layoutParams1.width = (int) mDefaultLSize;
            layoutParams1.height = (int) mDefaultLSize;
            temp.setBackgroundResource(R.drawable.ic_about_logo);
            temp.setTextColor(0xff22a3fe);
        } else {
            layoutParams1.rightMargin = (int) mDefaultMargin;
            layoutParams1.leftMargin = (int) mDefaultMargin;
            layoutParams1.width = (int) mDefaultSSize;
            layoutParams1.height = (int) mDefaultSSize;
            temp.setBackgroundResource(R.drawable.ic_about_logo);
            temp.setTextColor(0xffcee5ff);
        }
        temp.requestLayout();
    }

    @Override
    public void showPatternDiffTip() {
        Toast.makeText(this, R.string.set_graphic_password_message_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNumberErrorAnim() {
        if (mNumberLockerView != null) {
            mNumberLockerView.shakeNumberLayout();
        }
    }

    @Override
    public void showPatternShort() {
        Toast.makeText(this, R.string.set_graphic_password_message_too_short, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void invisiableStep3() {
        mStepLine2.setVisibility(View.GONE);
        mStepTxt3.setVisibility(View.GONE);
    }

    @Override
    public void showAnswerShortTip() {
        Toast.makeText(this, R.string.set_graphic_password_answer_edit_short_tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismissStepView() {
        mStepLayout.setVisibility(View.GONE);
    }

    @Override
    public void setResult(boolean isPatternPsd, String passcode, String question, String answer) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(answer)) {
            intent.putExtra(PSD_ANSWER_WORD, answer);
        }
        if (!TextUtils.isEmpty(question)) {
            intent.putExtra(PSD_QUESTION_WORD, question);
        }
        intent.putExtra(PSD_DEFAULT_TYPE_IS_PATTERN, isPatternPsd);
        intent.putExtra(PSD_GRAPHICAL_PASSCODE, passcode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getProblem() {
        if (mQuestionTip != null) {
            return mQuestionTip.getText().toString().trim();
        }
        return null;
    }

    @Override
    public String getAnswer() {
        if (mAnswer != null) {
            return mAnswer.getText().toString().trim();
        }
        return null;
    }

    @Override
    public void clearPsd(boolean isPatternPsd) {
        if (isPatternPsd) {
            mPatternView.clearPattern();
            mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
        } else {
            mNumberLockerView.clean();
        }
    }

    @Override
    public void showPatternError() {
        mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
    }

    @Override
    public void showLockerSwitch() {
        mLockTypeSwitch.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNumberSwitch() {
        mLockTypeImg.setImageResource(R.drawable.ic_about_logo);
    }

    @Override
    public void showPatternSwitch() {
        mLockTypeImg.setImageResource(R.drawable.ic_about_logo);
    }

    @Override
    public void showNumberPsdView() {
        mNumberLockerView.setVisibility(View.VISIBLE);
        mPatternView.setVisibility(View.GONE);
    }

    @Override
    public void showPatternPsdView() {
        mNumberLockerView.setVisibility(View.GONE);
        mPatternView.setVisibility(View.VISIBLE);
    }

    @Override
    public void cleanQuestionCache() {
        if (mAnswer != null) {
            mAnswer.setText("");
        }
        if (mQuestionTip != null) {
            mQuestionTip.setText(getResources().getStringArray(R.array.pupup_window_reset_psd_questions)[QuestionListDialog.WEN_BIRTHDAY]);
        }
    }

    @Override
    public void dismissLockerSwitch() {
        mLockTypeSwitch.setVisibility(View.GONE);
    }

    @Override
    public void toBack() {
        super.onBackPressed();
    }

    @Override
    protected void onHomePressed() {
        if (mPresenter != null) {
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
        }
        if (mPresenter != null) {
            mPresenter.dealBackPress();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.release();
            mPresenter = null;
        }
        if (mRotateAnimator != null) {
            mRotateAnimator.cancel();
        }
        super.onDestroy();
    }
}

package com.jb.filemanager.function.applock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.dialog.ListDialog;
import com.jb.filemanager.function.applock.presenter.PsdInitContract;
import com.jb.filemanager.function.applock.presenter.PsdInitPresenter;
import com.jb.filemanager.function.applock.presenter.PsdInitSupport;
import com.jb.filemanager.function.applock.view.PatternView;
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

    public static final String PSD_LOCK_OPTIONS = "psd_lock_options";

    private final String TAG = "PsdSettingActivity";
    //步数层布局
    private View[] mStepFl;
    //步数图片视图
    private ImageView[] mStepIv;
    //提示
    private TextView mApplockTip;
    //图案密码
    private PatternView mPatternView;
    //保存
    private View mSave;
    //返回按钮
    private ImageView mBack;
    //标题
    private TextView mTitle;
    //问题部分布局
    private View mQuestionView;
    private TextView mQuestionTextView;
    private View mQuestionBottomLine1;
    private EditText mQuestionInput;
    private View mQuestionBottomLine2;
    private TextView mLockOptionsTip;
    private View mLockOptionsView;
    private TextView mLockOptionsTextView;
    private View mQuestionBottomLine3;
    //问题列表对话框
    private ListDialog mQuestionListDialog;
    //选项列表对话框
    private ListDialog mOptionsListDialog;

    private PsdInitContract.Presenter mPresenter;

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
        //步数
        mStepFl = new View[3];
        mStepIv = new ImageView[3];
        //步数布局
        mStepFl[0] = findViewById(R.id.fragment_app_lock_step_1_fl);
        mStepFl[1] = findViewById(R.id.fragment_app_lock_step_2_fl);
        mStepFl[2] = findViewById(R.id.fragment_app_lock_step_3_fl);
        //步数图片视图
        mStepIv[0] = (ImageView) findViewById(R.id.fragment_app_lock_step_1_iv);
        mStepIv[1] = (ImageView) findViewById(R.id.fragment_app_lock_step_2_iv);
        mStepIv[2] = (ImageView) findViewById(R.id.fragment_app_lock_step_3_iv);
        //提示
        mApplockTip = (TextView) findViewById(R.id.fragment_app_lock_step_tip);
        //图案密码
        mPatternView = (PatternView) findViewById(R.id.fragment_app_lock_set_psd_patternview);
        mQuestionView = findViewById(R.id.fragment_app_lock_set_psd_question_layout);
        mQuestionTextView = (TextView) findViewById(R.id.fragment_app_lock_set_psd_show);
        mQuestionBottomLine1 = findViewById(R.id.fragment_app_lock_set_psd_bottom_line);
        mQuestionInput = (EditText) findViewById(R.id.fragment_app_lock_set_psd_question_input);
        mQuestionBottomLine2 = findViewById(R.id.fragment_app_lock_set_psd_bottom_line2);
        mLockOptionsView = findViewById(R.id.fragment_app_lock_lock_options_layout);
        mLockOptionsTip = (TextView) findViewById(R.id.fragment_app_lock_lock_options);
        mLockOptionsTextView = (TextView) findViewById(R.id.fragment_app_lock_lock_options_show);
        mQuestionBottomLine3 = findViewById(R.id.fragment_app_lock_set_psd_bottom_line3);
        mSave = findViewById(R.id.fragment_app_lock_set_psd_bottom_save);
        findViewById(R.id.fragment_app_lock_head).setBackgroundColor(0xFF00BBA0);
        //标题
        mBack = (ImageView) findViewById(R.id.common_applock_bar_layout_back);
        mTitle = (TextView) findViewById(R.id.common_applock_bar_layout_title);
        mTitle.setText(R.string.activity_applock_title);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int mode = bundle.getInt(PSD_SETTING_MODE);
            mPresenter.setMode(mode);
        }
        mPresenter.start();
        initListener();
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

        mQuestionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    mQuestionListDialog.showUnderView(mQuestionBottomLine1);
                }
            }
        });

        mLockOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    mOptionsListDialog.showUnderView(mQuestionBottomLine3);
                }
            }
        });

        mPatternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                if (mPresenter != null) {
                    mPresenter.dealPatternStart();
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

    @Override
    public void showStepTopPatternTip(int step) {
//        for (int i = 0; i < mStepIv.length; i++) {
//            mStepIv[i].clearColorFilter();
//        }
        switch (step) {
            case 1:
//                mStepIv[0].setColorFilter(0xff00BBA0);
//                mStepIv[1].setColorFilter(0xffCBF2ED);
//                mStepIv[2].setColorFilter(0xffCBF2ED);
                mStepIv[0].setAlpha(1.0f);
                mStepIv[1].setAlpha(0.3f);
                mStepIv[2].setAlpha(0.3f);
                setQuestionLayerVisiable(false);
                mApplockTip.setText(R.string.applock_psd_set_step_1);
                break;
            case 2:
//                mStepIv[0].setColorFilter(0xff00BBA0);
//                mStepIv[1].setColorFilter(0xff00BBA0);
//                mStepIv[2].setColorFilter(0xffCBF2ED);
                mStepIv[0].setAlpha(1.0f);
                mStepIv[1].setAlpha(1.0f);
                mStepIv[2].setAlpha(0.3f);
                setQuestionLayerVisiable(false);
                mApplockTip.setText(R.string.applock_psd_set_step_2);
                break;
            case 3:
//                mStepIv[0].setColorFilter(0xff00BBA0);
//                mStepIv[1].setColorFilter(0xff00BBA0);
//                mStepIv[2].setColorFilter(0xff00BBA0);
                mStepIv[0].setAlpha(1.0f);
                mStepIv[1].setAlpha(1.0f);
                mStepIv[2].setAlpha(1.0f);
                setQuestionLayerVisiable(true);
                mApplockTip.setText(R.string.applock_psd_set_step_3);
                break;
        }
    }

    @Override
    public void showPsdViewDismissQuestion() {
        mPatternView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProblemViewDismissPsd() {
        mPatternView.setVisibility(View.GONE);
    }

    /**
     * 设置问题层的展示
     *
     * @param show 是否展示
     */
    private void setQuestionLayerVisiable(boolean show) {
        int visiable = show ? View.VISIBLE : View.GONE;
        int unVisiable = show ? View.GONE : View.VISIBLE;
        mQuestionView.setVisibility(visiable);
        mQuestionTextView.setVisibility(visiable);
        mQuestionBottomLine1.setVisibility(visiable);
        mQuestionInput.setVisibility(visiable);
        mQuestionBottomLine2.setVisibility(visiable);
        mLockOptionsView.setVisibility(visiable);
        mLockOptionsTip.setVisibility(visiable);
        mLockOptionsTextView.setVisibility(visiable);
        mQuestionBottomLine3.setVisibility(visiable);
        mSave.setVisibility(visiable);
        mPatternView.setVisibility(unVisiable);
        if (show) {
            String[] questions = this.getResources().getStringArray(R.array.pupup_window_reset_psd_questions);
            String[] options = this.getResources().getStringArray(R.array.pupup_window_lock_options);
            if (questions != null) {
                mQuestionTextView.setText(questions[0]);
            }
            if (options != null) {
                mLockOptionsTextView.setText(options[0]);
            }
            mQuestionInput.setText("");
            if (mQuestionListDialog == null) {
                mQuestionListDialog = new ListDialog(mQuestionTextView, R.array.pupup_window_reset_psd_questions);
            }
            if (mOptionsListDialog == null) {
                mOptionsListDialog = new ListDialog(mLockOptionsTextView, R.array.pupup_window_lock_options);
            }
        }
    }

    @Override
    public void showPatternDiffTip() {
        Toast.makeText(this, R.string.set_graphic_password_message_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPatternShort() {
        Toast.makeText(this, R.string.set_graphic_password_message_too_short, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void invisiableStep3() {
        mStepFl[2].setVisibility(View.GONE);
        setQuestionLayerVisiable(false);
    }

    @Override
    public void showAnswerShortTip() {
        Toast.makeText(this, R.string.set_graphic_password_answer_edit_short_tip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setResult(String passcode, String question, String answer, boolean isLockForLeave) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(answer)) {
            intent.putExtra(PSD_ANSWER_WORD, answer);
        }
        if (!TextUtils.isEmpty(question)) {
            intent.putExtra(PSD_QUESTION_WORD, question);
        }
        intent.putExtra(PSD_GRAPHICAL_PASSCODE, passcode);
        intent.putExtra(PSD_LOCK_OPTIONS, isLockForLeave);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getProblem() {
        if (mQuestionTextView != null) {
            return mQuestionTextView.getText().toString().trim();
        }
        return null;
    }

    @Override
    public String getAnswer() {
        if (mQuestionInput != null) {
            return mQuestionInput.getText().toString().trim();
        }
        return null;
    }

    @Override
    public int getLockOptions() {
        if (mOptionsListDialog != null) {
            return mOptionsListDialog.getCurrentPos();
        }
        return 0;
    }

    @Override
    public void clearPsd() {
        mPatternView.clearPattern();
        mPatternView.setDisplayMode(PatternView.DisplayMode.Correct);
    }

    @Override
    public void showPatternError() {
        mPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
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
            mPresenter.dealBackPress();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.release();
            mPresenter = null;
        }
        super.onDestroy();
    }
}

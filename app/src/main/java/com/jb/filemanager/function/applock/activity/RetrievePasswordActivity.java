package com.jb.filemanager.function.applock.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.applock.model.AppLockerDataManager;
import com.jb.filemanager.function.applock.presenter.RetrievePasswordContract;
import com.jb.filemanager.function.applock.presenter.RetrievePasswordPresenter;
import com.jb.filemanager.function.applock.presenter.RetrievePasswordSupport;
import com.jb.filemanager.util.APIUtil;

/**
 * Created by nieyh on 2017/1/9.
 */

public class RetrievePasswordActivity extends BaseActivity implements RetrievePasswordContract.View {
    //问题
    private TextView mQuestion;
    //回答
    private EditText mAnswer;
    //确定
    private TextView mSure;

    private View mBack;

    private TextView mTitle;

    private RetrievePasswordContract.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findback_psd);
        initView();
        initListener();
    }

    /**
     * 初始化View
     * */
    private void initView() {
        mTitle = (TextView)findViewById(R.id.activity_title_word);
        mTitle.setText(R.string.applock_forget_psd_title);
        mBack = findViewById(R.id.activity_title_icon);
        mQuestion = (TextView) findViewById(R.id.activity_findback_psd_show);
        mAnswer = (EditText) findViewById(R.id.activity_findback_psd_question_input);
        mSure = (TextView) findViewById(R.id.activity_findback_psd_question_save);
        initGradient();
        mPresenter = new RetrievePasswordPresenter(this, new RetrievePasswordSupport());
        mPresenter.start();
    }

    /**
     * 初始化渐变色
     * */
    private void initGradient() {
        View rootGradientBg = findViewById(R.id.activity_findback_psd_root_bg);
        int startColor = 0xff0084ff;
        int endColor = 0x3bd6f2;
        GradientDrawable gradientDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gradientDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawableLR.setShape(GradientDrawable.RECTANGLE);
        APIUtil.setBackground(rootGradientBg, gradientDrawableLR);
    }

    private void initListener() {
        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    if (mPresenter != null) {
                        mPresenter.dealSure();
                    }
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    finish();
                }
            }
        });

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    finish();
                }
            }
        });
    }

    @Override
    public void gotoResetPsd() {
        Intent i = new Intent(RetrievePasswordActivity.this, PsdSettingActivity.class);
        i.putExtra(PsdSettingActivity.PSD_SETTING_MODE, PsdSettingActivity.PSD_REST);
//        i.putExtra(PsdSettingActivity.PSD_DEFAULT_TYPE_IS_PATTERN, AppLockerDataManager.getInstance().isPatternPsd());
        startActivity(i);
        finish();
    }

    @Override
    public String getAnswer() {
        return mAnswer.getText().toString().trim();
    }

    @Override
    public void showQuestion(String question) {
        if (mQuestion != null && !TextUtils.isEmpty(question)) {
            mQuestion.setText(question);
        }
    }

    @Override
    public void showAnswerErrorTip() {
        Toast.makeText(this, R.string.applock_retrieve_psd_error_tip, Toast.LENGTH_SHORT).show();
    }
}

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
        findViewById(android.R.id.content).setBackgroundColor(0xFF44D6C3);
        mTitle = (TextView)findViewById(R.id.common_applock_bar_layout_title);
        mTitle.setText(R.string.applock_forget_psd_title);
        mBack = findViewById(R.id.common_applock_bar_layout_back);
        mQuestion = (TextView) findViewById(R.id.activity_findback_psd_question_show);
        mAnswer = (EditText) findViewById(R.id.activity_findback_psd_question_input);
        mSure = (TextView) findViewById(R.id.activity_findback_psd_bottom_save);
        mPresenter = new RetrievePasswordPresenter(this, new RetrievePasswordSupport());
        mPresenter.start();
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

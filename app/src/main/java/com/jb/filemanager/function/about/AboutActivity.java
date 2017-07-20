package com.jb.filemanager.function.about;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.privacy.PrivacyHelper;
import com.jb.filemanager.util.AppUtils;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class AboutActivity extends BaseActivity {

    private TextView mContactUs;
    private TextView mTvPolicy;
    private TextView mTvUserExperience;
    private TextView mTvCommonActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        setListener();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mTvCommonActionBarTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mTvCommonActionBarTitle.setText(R.string.about_about_us);

        mTvPolicy = (TextView) findViewById(R.id.tv_about_policy);
        if (mTvPolicy != null) {
            mTvPolicy.getPaint().setAntiAlias(true);
            mTvPolicy.setPaintFlags(mTvPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        mTvUserExperience = (TextView) findViewById(R.id.tv_about_user_experience);
        if (mTvUserExperience != null) {
            mTvUserExperience.getPaint().setAntiAlias(true);
            mTvUserExperience.setPaintFlags(mTvUserExperience.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        mContactUs = (TextView) findViewById(R.id.tv_about_contact_us);
        if (mContactUs != null) {
            mContactUs.getPaint().setAntiAlias(true);
            mContactUs.setPaintFlags(mTvUserExperience.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        TextView appName = (TextView) findViewById(R.id.tv_about_app_name);
        if (appName != null) {
            appName.getPaint().setAntiAlias(true);
            appName.setText(getString(R.string.app_name));
        }

        TextView version = (TextView) findViewById(R.id.tv_about_app_version);
        if (version != null) {
            version.getPaint().setAntiAlias(true);
            version.setText(String.format(getString(R.string.about_app_version), AppUtils.getVersionName(this)));
        }
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        mTvCommonActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuickClickGuard.isQuickClick(v.getId())) {
                    return;
                }
                PrivacyHelper.gotoPrivacyInfoPage(AboutActivity.this);
            }
        });

        mTvUserExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuickClickGuard.isQuickClick(v.getId())) {
                    return;
                }
                PrivacyHelper.gotoUepInfoPage(AboutActivity.this);
            }
        });

        mContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuickClickGuard.isQuickClick(v.getId())) {
                    return;
                }
                AppUtils.goToContactUs();
            }
        });
    }

    //处理对话框
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

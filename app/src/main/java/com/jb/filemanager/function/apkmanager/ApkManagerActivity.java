package com.jb.filemanager.function.apkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

public class ApkManagerActivity extends BaseActivity implements ApkManagerContract.View {

    private ApkPresenter mPresenter;
    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarWithSearchTitle;
    private EditText mEtCommonActionBarWithSearchSearch;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private ExpandableListView mElvApk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_manager);

        initView();
        initData();
        initClick();
        mPresenter = new ApkPresenter(this, new ApkSupport());
        mPresenter.onCreate(getIntent());
    }

    @Override
    public void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarWithSearchTitle = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        mEtCommonActionBarWithSearchSearch = (EditText) findViewById(R.id.et_common_action_bar_with_search_search);
        mIvCommonActionBarWithSearchSearch = (ImageView) findViewById(R.id.iv_common_action_bar_with_search_search);
        mElvApk = (ExpandableListView) findViewById(R.id.elv_apk);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initClick() {

    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.onPressHomeKey();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }
}

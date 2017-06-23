package com.jb.filemanager.function.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;


public class SettingActivity extends BaseActivity implements SettingContract.View, View.OnClickListener {

    private SettingPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initOnClickListener();
        mPresenter = new SettingPresenter(this, new SettingSupport());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
            mPresenter = null;
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
            mPresenter = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    //对View进行初始化
    private void initView() {
        TextView mTvBack = (TextView) findViewById(R.id.tv_common_action_bar_title);
        if (mTvBack != null) {
            mTvBack.getPaint().setAntiAlias(true);
            mTvBack.setOnClickListener(this);
        }
    }

    //初始化本页面的点击事件
    private void initOnClickListener() {

    }

    @Override
    public void onClick(View v) {
        if (mQuickClickGuard.isQuickClick(v.getId())) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_common_action_bar_title:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void finishActivity() {
        super.onBackPressed();
    }
}

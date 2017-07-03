package com.jb.filemanager.function.apkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.imageloader.IconLoader;

public class AppManagerActivity extends BaseActivity implements AppManagerContract.View, View.OnClickListener {

    private AppManagerPresenter mPresenter;
    private LinearLayout mLlTitle;
    private TextView mTvCommonActionBarWithSearchTitle;
    private EditText mEtCommonActionBarWithSearchSearch;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private ExpandableListView mElvApk;
    private AppManagerAdapter mAdapter;
    private TextView mTvBottomDelete;
    private int mChosenCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_manager);
        IconLoader.ensureInitSingleton(this);
        IconLoader.getInstance().bindServicer(this);
        mPresenter = new AppManagerPresenter(this, new AppManagerSupport());
        mPresenter.onCreate(getIntent());

        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        mTvCommonActionBarWithSearchTitle = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        mEtCommonActionBarWithSearchSearch = (EditText) findViewById(R.id.et_common_action_bar_with_search_search);
        mIvCommonActionBarWithSearchSearch = (ImageView) findViewById(R.id.iv_common_action_bar_with_search_search);
        mElvApk = (ExpandableListView) findViewById(R.id.elv_apk);
        mTvBottomDelete = (TextView) findViewById(R.id.tv_bottom_delete);

    }

    @Override
    public void initData() {
        mAdapter = new AppManagerAdapter(mPresenter.getAppInfo());
        mAdapter.setOnItemChosenListener(new AppManagerAdapter.OnItemChosenListener() {
            @Override
            public void onItemChosen(int chosenCount) {
                handleBottomDeleteShow(chosenCount);
            }
        });
        mElvApk.setAdapter(mAdapter);
        mAdapter.handleCheckedCount();
    }

    @Override
    public void initClick() {
        mTvBottomDelete.setOnClickListener(this);
    }

    private void handleBottomDeleteShow(int chosenCount) {
        mChosenCount = chosenCount;
        if (chosenCount == 0) {
            mTvBottomDelete.setVisibility(View.GONE);
        } else {
            mTvBottomDelete.setVisibility(View.VISIBLE);
        }
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
        IconLoader.getInstance().unbindServicer(this);
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

    @Override
    public void onClick(View view) {
        if (mQuickClickGuard.isQuickClick(view.getId())) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_bottom_delete:
                Toast.makeText(AppManagerActivity.this, mChosenCount + "个app被选中了呢   欧尼酱", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

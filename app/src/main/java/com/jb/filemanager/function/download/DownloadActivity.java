package com.jb.filemanager.function.download;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.ui.widget.BottomOperateBar;

/**
 * Created by bool on 17-7-1.
 * 下载内容管理页面
 */

public class DownloadActivity extends BaseActivity implements DownloadContract.View,
        View.OnClickListener{
    DownloadContract.Presenter mPresenter;
    private BottomOperateBar mBottomOperateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        DownloadSupport support = new DownloadSupport();
        mPresenter = new DownloadPresenter(this, new DownloadSupport(),
                new FileLoader(this, support), getSupportLoaderManager());
        initView();
    }
    private void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.download_title);
            back.setOnClickListener(this);
        }
        mBottomOperateBar = (BottomOperateBar)findViewById(R.id.bottom_operate_bar_container);
        mBottomOperateBar.setClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onPressedHomeKey() {
        super.onPressedHomeKey();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPresenter.onClickBackButton(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_with_search_title:
                onBackPressed();
                break;
            case R.id.iv_common_action_bar_with_search_search:
                // TODO
                break;
            case R.id.tv_common_operate_bar_cut:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCutButton();
                }
                break;
            case R.id.tv_common_operate_bar_copy:
                if (mPresenter != null) {
                    mPresenter.onClickOperateCopyButton();
                }
                break;
            case R.id.tv_common_operate_bar_delete:
                if (mPresenter != null) {
                    mPresenter.onClickOperateDeleteButton();
                }
                break;
            case R.id.tv_common_operate_bar_more:
                if (mPresenter != null) {
                    mPresenter.onClickOperateMoreButton();
                }
                break;
            default:
                break;
        }
    }
}

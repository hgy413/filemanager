package com.jb.filemanager.function.download;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;

/**
 * Created by bool on 17-7-1.
 * 下载内容管理页面
 */

public class DownloadActivity extends BaseActivity implements DownloadContract.View,
        View.OnClickListener{
    DownloadContract.Presenter mPresenter;

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

    }
}

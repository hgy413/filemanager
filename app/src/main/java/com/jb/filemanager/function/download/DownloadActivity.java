package com.jb.filemanager.function.download;

import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.jb.filemanager.BaseActivity;

/**
 * Created by bool on 17-7-1.
 * 下载内容管理页面
 */

public class DownloadActivity extends BaseActivity implements DownloadContract.View {
    DownloadContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new DownLoadPresenter(this, new DownLoadSupport(), new AsyncTaskLoader(this) {
            @Override
            public Object loadInBackground() {
                return null;
            }
        }, getSupportLoaderManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

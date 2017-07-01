package com.jb.filemanager.function.download;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-7-1.
 */

public class DownLoadPresenter implements DownloadContract.Presenter, LoaderManager.LoaderCallbacks{
    private DownloadContract.View mView;
    private DownloadContract.Support support;
    private Loader mLoader;
    private LoaderManager mLoaderManager;
    public DownLoadPresenter(@NonNull DownloadContract.View mView, @NonNull DownloadContract.Support support,
                             @NonNull Loader loader, @NonNull LoaderManager manager) {
        this.mView = checkNotNull(mView);
        this.support = checkNotNull(support);
        this.mLoader = checkNotNull(loader);
        mLoaderManager = checkNotNull(manager);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }
}

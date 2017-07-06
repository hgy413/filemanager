package com.jb.filemanager.function.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;

import com.jb.filemanager.home.MainActivity;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-7-4.
 */

public class VideoPresenter implements VideoContract.Presenter, LoaderManager.LoaderCallbacks {
    private VideoActivity mView;
    private VideoContract.Support mSupport;
    private Loader mLoader;
    private LoaderManager mLoadrManager;

    public VideoPresenter(@NonNull VideoActivity mView, @NonNull VideoContract.Support mSupport,
                          @NonNull Loader loader, @NonNull LoaderManager manager) {
        this.mView = checkNotNull(mView);
        this.mSupport = checkNotNull(mSupport);
        mLoader = checkNotNull(loader);
        mLoadrManager = checkNotNull(manager);
    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public void onClickBackButton(boolean b) {

    }

    @Override
    public void start() {

    }

    @Override
    public void onClickOperateCutButton() {
        mView.startActivity(new Intent(mView, MainActivity.class));

    }

    @Override
    public void onClickOperateCopyButton() {
        mView.startActivity(new Intent(mView, MainActivity.class));
    }

    @Override
    public void onClickOperateDeleteButton() {

    }

    @Override
    public void onClickOperateMoreButton() {

    }


}
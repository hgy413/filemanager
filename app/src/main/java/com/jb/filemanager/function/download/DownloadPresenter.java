package com.jb.filemanager.function.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.home.MainActivity;
import java.io.File;
import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-7-1.
 */

public class DownloadPresenter implements DownloadContract.Presenter,
        LoaderManager.LoaderCallbacks<GroupList<String, File>> {
    private static final short LOADER_ID = 2;
    private DownloadActivity mView;
    private DownloadContract.Support support;
    private Loader mLoader;
    private LoaderManager mLoaderManager;
    public DownloadPresenter(@NonNull DownloadActivity mView, @NonNull DownloadContract.Support support,
                             @NonNull Loader loader, @NonNull LoaderManager manager) {
        this.mView = checkNotNull(mView);
        this.support = checkNotNull(support);
        this.mLoader = checkNotNull(loader);
        mLoaderManager = checkNotNull(manager);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<GroupList<String, File>> loader, GroupList<String, File> data) {

    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onClickBackButton(boolean finishActivity) {
        if (mView != null) {
            if (finishActivity) {
                ((AppCompatActivity)mView).finish();
            } else {
                ((AppCompatActivity)mView).finish();
            }
        }
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onClickOperateCutButton() {
        mView.startActivity(new Intent(mView, MainActivity.class));
    }

    @Override
    public void onClickOperateDeleteButton() {

    }

    @Override
    public void onClickOperateMoreButton() {

    }

    @Override
    public void onClickOperateCopyButton() {
        mView.startActivity(new Intent(mView, MainActivity.class));
    }
}

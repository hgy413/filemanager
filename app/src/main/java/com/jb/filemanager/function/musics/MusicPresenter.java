package com.jb.filemanager.function.musics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.jb.filemanager.Const;
import com.jb.filemanager.home.MainActivity;

import java.io.File;
import java.util.ArrayList;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-6-30.
 *
 */

public class MusicPresenter implements MusicContract.Presenter,
        LoaderManager.LoaderCallbacks<GroupList<String, MusicInfo>> {

    private static final int LOADER_ID = 1;
    private final MusicActivity mView;
    private final MusicContract.Support mSupport;
    private final LoaderManager mLoaderManager;
    private final MusicsLoader mMusicLoader;
    private GroupList<String, MusicInfo> mMusicGroupList;
    public MusicPresenter(@NonNull MusicActivity view, @NonNull MusicContract.Support support,
                          @NonNull MusicsLoader loader, @NonNull LoaderManager manager){
        mView = checkNotNull(view);
        mSupport = checkNotNull(support);
        mLoaderManager = checkNotNull(manager);
        mMusicLoader = checkNotNull(loader);

    }
    @Override
    public Loader<GroupList<String,MusicInfo>> onCreateLoader(int id, Bundle args) {
        return mMusicLoader;
    }

    @Override
    public void onLoadFinished(Loader<GroupList<String, MusicInfo>> loader, GroupList<String, MusicInfo> data) {
        mMusicGroupList = data;
        if (mMusicGroupList == null) {
            //Todo 显示没有音乐提示
        } else {
            // 显示列表
            mView.showMusicList(mMusicGroupList);
        }
    }

    @Override
    public void onLoaderReset(Loader<GroupList<String, MusicInfo>> loader) {

    }

    @Override
    public void onCreate(Intent intent) {

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
<<<<<<< HEAD
=======

    @Override
    public void onClickOperateCutButton(boolean[] selectedPosition) {
        if (selectedPosition != null && selectedPosition.length >0 ) {
            Intent intent = new Intent(mView, MainActivity.class);
            intent.putExtra(Const.BOTTOM_OPERATE, Const.BOTTOM_OPERATE_BAR_CUT);
            intent.putExtra(Const.BOTTOM_OPERATE_DATA, selectedPositon2PathList(selectedPosition));
            mView.startActivity(intent);
        } else {
            Toast.makeText(mView, "No Item is selected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClickOperateCopyButton(boolean[] selectedPosition) {
        if (selectedPosition != null && selectedPosition.length >0 ) {
            Intent intent = new Intent(mView, MainActivity.class);
            intent.putExtra(Const.BOTTOM_OPERATE, Const.BOTTOM_OPERATE_BAR_COPY);
            intent.putExtra(Const.BOTTOM_OPERATE_DATA, selectedPositon2PathList(selectedPosition));
            mView.startActivity(intent);
        } else {
            Toast.makeText(mView, "No Item is selected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClickOperateDeleteButton() {
        mView.showDeleteConfirmDialog();
    }

    @Override
    public void onClickOperateMoreButton(boolean[] selectedPosition) {
        int selectCount = 0;
        for (boolean isSelected : selectedPosition) {
            if (isSelected) {
                selectCount++;
            }
        }
        // 这里size 只可能是大于等于1，没有选中文件的时候应该不会出现底部操作栏
        switch (selectCount) {
            case 1:
                mView.showBottomMoreOperatePopWindow(false);
                break;
            default:
                mView.showBottomMoreOperatePopWindow(true);
                break;
        }
    }

    @Override
    public void onClickConfirmDeleteButton(boolean[] selectedPosition) {
        if (selectedPosition != null && selectedPosition.length >0 ) {
            mSupport.delete(selectedPositon2PathList(selectedPosition));
        } else {
            Toast.makeText(mView, "No Item is selected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClickOperateDetailButton() {

    }

    @Override
    public void onClickOperateRenameButton() {

    }

    protected ArrayList<String> selectedPositon2PathList(boolean[] position) {
        ArrayList<String> selectedFile = new ArrayList<>();
        for (int i = 0; i < position.length; i++) {
            selectedFile.add(mMusicGroupList.getItem(i).mFullPath);
        }
        return  selectedFile;
    }
>>>>>>> 条
}

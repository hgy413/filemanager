package com.jb.filemanager.function.samefile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.jb.filemanager.Const;
import com.jb.filemanager.home.MainActivity;
import java.util.ArrayList;


/**
 * Created by bool on 17-6-30.
 *
 */

public class SameFilePresenter implements SameFileContract.Presenter,
        LoaderManager.LoaderCallbacks<GroupList<String, FileInfo>> {
    private static final String TAG = "SameFilePresenter.class";
    private final SameFileActivity mView;
    private final SameFileContract.Support mSupport;
    private final LoaderManager mLoaderManager;
    private AsyncTaskLoader mFileLoader;
    private GroupList<String, FileInfo> mMusicGroupList;
    public SameFilePresenter(@NonNull SameFileActivity view, @NonNull SameFileContract.Support support,
                             @NonNull LoaderManager manager){
        mView = view;
        mSupport = support;
        mLoaderManager = manager;
    }
    @Override
    public Loader<GroupList<String,FileInfo>> onCreateLoader(int id, Bundle args) {
        return mFileLoader;
    }

    @Override
    public void onLoadFinished(Loader<GroupList<String, FileInfo>> loader, GroupList<String, FileInfo> data) {
        mMusicGroupList = data;
        if (mMusicGroupList == null) {
            //Todo 显示没有音乐提示
        } else {
            // 显示列表
            mView.showMusicList(mMusicGroupList);
        }
    }

    @Override
    public void onLoaderReset(Loader<GroupList<String, FileInfo>> loader) {

    }

    @Override
    public void onCreate(Intent intent) {
        if (intent != null) {
            int fileType = intent.getIntExtra(Const.FILE_TYPE, -1);// 默认给出一个错误值，以免混乱，避免获取不到时加载错误的选项造成疑惑
            mView.initView(fileType);
            this.start(fileType);
        }
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
    public void start(final int fileType) {
        switch (fileType) {
            case Const.FILE_TYPE_MUSIC:
                mFileLoader = new MusicsLoader(mView, (SameFileSupport)mSupport);
                break;
            case Const.FILE_TYPE_VIDEO:
                mFileLoader = new VideosLoader(mView, (SameFileSupport)mSupport);
                break;
            case Const.FILE_TYPE_DOWNLOAD:
                mFileLoader = new DownloadLoader(mView, (SameFileSupport)mSupport);
                break;
            case Const.FILE_TYPE_IMAGE:// Image file

            case Const.FILE_TYPE_APPLICATION: // Application file

            case Const.FILE_TYPE_DOCUMENT:

            default:
                Log.d(TAG, "No sutch filt type: " + fileType);
        }
        if (Const.FILE_TYPE_MUSIC <= fileType && fileType <= Const.FILE_TYPE_DOCUMENT) {
            mLoaderManager.initLoader(fileType, null, this).forceLoad();
        }
    }

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
}

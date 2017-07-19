package com.jb.filemanager.function.samefile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.jb.filemanager.Const;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.function.search.view.SearchFragment;
import com.jb.filemanager.home.MainActivity;

import java.io.File;
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
    private GroupList<String, FileInfo> mFileGroupList;

    private int mCategoryType = Const.CategoryType.CATEGORY_TYPE_ALL;

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
        mFileGroupList = data;
        if (mFileGroupList == null) {
            mView.onNoFileFindShow();
        } else {
            // 显示列表
            mView.showFileList(mFileGroupList);
        }
    }

    @Override
    public void onLoaderReset(Loader<GroupList<String, FileInfo>> loader) {

    }

    @Override
    public void onCreate(Intent intent) {
        if (intent != null) {
            int fileType = intent.getIntExtra(Const.CLASSIFY_TYPE, -1);// 默认给出一个错误值，以免混乱，避免获取不到时加载错误的选项造成疑惑

            switch (fileType) {
                case Const.FILE_TYPE_IMAGE:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_PHOTO;
                    break;
                case Const.FILE_TYPE_VIDEO:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_VIDEO;
                    break;
                case Const.FILE_TYPE_APPLICATION:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_APP;
                    break;
                case Const.FILE_TYPE_MUSIC:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_MUSIC;
                    break;
                case Const.FILE_TYPE_DOCUMENT:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_DOC;
                    break;
                case Const.FILE_TYPE_ZIP:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_ZIP;
                    break;
                case Const.FILE_TYPE_DOWNLOAD:
                    mCategoryType = Const.CategoryType.CATEGORY_TYPE_DOWNLOAD;
                    break;
            }
            mCategoryType = fileType;
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
    public void onClickSearchButton() {
        if (mView != null) {
            SearchActivity.showSearchResult(mSupport.getContext(), mCategoryType);
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
    public ArrayList<File> getSelectFile() {
        ArrayList<File> selectedFile = new ArrayList<>();
        for (int i = 0; i < mFileGroupList.size(); i++) {
            for (FileInfo info : mFileGroupList.valueAt(i)) {
                if (info.isSelected) {
                    selectedFile.add(new File(info.mFullPath));
                }
            }
        }
        return  selectedFile;

    }

    protected ArrayList<String> selectedPositon2PathList(boolean[] position) {
        ArrayList<String> selectedFile = new ArrayList<>();
        for (int i = 0; i < position.length; i++) {
            selectedFile.add(mFileGroupList.getItem(i).mFullPath);
        }
        return  selectedFile;
    }

    @Override
    public void jumpToStoragePage(){
        Intent intent = new Intent(mView, MainActivity.class);
        intent.putExtra("HAVE_PAST_DATE", true);
        mView.startActivity(intent);
    }
}

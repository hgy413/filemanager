package com.jb.filemanager.function.music;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicPresenter implements MusicContract.Presenter
        , LoaderManager.LoaderCallbacks<Cursor> {
    public static final int MUSIC_STATUS_NORMAL = 0;
    public static final int MUSIC_STATUS_SELECT = 1;
    private static final int LOADER_ID = 1;
    private static final String GROUP_ID = "_id";
    private static final String GROUP_NAME = "name";
    private static final String GROUP_START = "start";
    private static final String GROUP_END = "end";

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private int mStatus = MUSIC_STATUS_NORMAL;

    private MusicContract.View mView;
    private MusicContract.Support mSupport;
    private LoaderManager mLoaderManager;
    MusicPresenter(@NonNull MusicContract.View view, @NonNull MusicContract.Support support, LoaderManager loaderManager) {
        mView = view;
        mSupport = support;
        mLoaderManager = loaderManager;
        mLoaderManager.initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
        mLoaderManager.destroyLoader(LOADER_ID);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            if (systemBack) {
                mView.finishActivity();
            } else {
                mView.finishActivity();
            }
        }
    }

    @Override
    public void onPressHomeKey() {
        // nothing to do
    }

    @Override
    public boolean isSelected(File file) {
        boolean result = false;
        if (file != null) {
            try {
                result = mSelectedFiles.contains(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void addOrRemoveSelected(File file) {
        if (file != null) {
            try {
                if (mSelectedFiles.contains(file)) {
                    mSelectedFiles.remove(file);
                } else {
                    mSelectedFiles.add(file);
                }

                if (mSelectedFiles.size() > 0) {
                    mStatus = MUSIC_STATUS_SELECT;
                } else {
                    mStatus = MUSIC_STATUS_NORMAL;
                }

                mView.updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void restartLoader() {
        mLoaderManager.restartLoader(LOADER_ID, null, this);;
    }

    @Override
    public Cursor queryDate(long start, long end) {
        return mSupport.queryData(start, end);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mSupport.getAllMusicCursor();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            try {
                int group = 0;
                long lastModify = 0L;
                String[] columnNames = { GROUP_ID, GROUP_NAME, GROUP_START, GROUP_END };
                MatrixCursor matrixCursor = new MatrixCursor(columnNames, columnNames.length);

                while (data.moveToNext()) {
                    String path = data.getString(0);
                    long modify = data.getLong(1);
                    Logger.e("wangzq", path + " " + String.valueOf(modify));

                    boolean isSameDay = TimeUtil.isSameDayOfMillis(lastModify, modify);
                    if (!isSameDay) {
                        // modify 的单位是秒
                        lastModify = modify;
                        String timeString = TimeUtil.getTime(modify * 1000);
                        long startMills = TimeUtil.getStartMillsInDay(modify);
                        long endMills = startMills + TimeUtil.MILLIS_IN_DAY;

                        String[] row = new String[] { String.valueOf(group), timeString, String.valueOf(startMills), String.valueOf(endMills) };
                        matrixCursor.addRow(row);
                    }
                }

                mView.changeCursor(matrixCursor);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                data.close();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoaderManager.restartLoader(LOADER_ID, null, this);;
    }
}

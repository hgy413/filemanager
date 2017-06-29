package com.jb.filemanager.manager.file;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.manager.spm.IPreferencesIds;
import com.jb.filemanager.manager.spm.SharedPreferencesManager;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bill wang on 2017/6/23.
 *
 */

public class FileLoader extends AsyncTaskLoader<List<File>> {

    private static final int FILE_OBSERVER_MASK = FileObserver.CREATE
            | FileObserver.DELETE | FileObserver.DELETE_SELF
            | FileObserver.MOVED_FROM | FileObserver.MOVED_TO
            | FileObserver.MODIFY | FileObserver.MOVE_SELF;

    private FileObserver mFileObserver;

    private List<File> mData;
    private String mPath;

    private Comparator<File> mSort;

    public FileLoader(Context context, String path) {
        super(context);
        this.mPath = path;
        mSort = FileUtil.sComparator;
    }

    public FileLoader(Context context, String path, Comparator<File> sort) {
        super(context);
        this.mPath = path;
        this.mSort = sort;
    }

    @Override
    public List<File> loadInBackground() {
        if (TextUtils.isEmpty(mPath)) {
            return new ArrayList<File>();
        }

        ArrayList<File> list = new ArrayList<File>();

        // Current directory File instance
        final File pathDir = new File(mPath);

        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(TheApplication.getInstance());
        boolean showHiddenFile = spm.getBoolean(IPreferencesIds.KEY_SETTING_SHOW_HIDDEN_FILES, false);

        // List file in this directory with the directory filter
        final File[] dirs = pathDir.listFiles(showHiddenFile ? FileUtil.sDirFilterWithHidden : FileUtil.sDirFilter);
        if (dirs != null) {
            // Sort the folders
            Arrays.sort(dirs, mSort);
            // Add each folder to the File list for the list adapter
            for (File dir : dirs)
                list.add(dir);
        }

        // List file in this directory with the file filter
        final File[] files = pathDir.listFiles(showHiddenFile ? FileUtil.sFileFilterWithHidden : FileUtil.sFileFilter);
        if (files != null) {
            // Sort the files
            Arrays.sort(files, mSort);
            // Add each file to the File list for the list adapter
            for (File file : files)
                list.add(file);
        }

        return list;
    }

    @Override
    public void deliverResult(List<File> data) {
        if (isReset()) {
            onReleaseResources(data);
            return;
        }

        List<File> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (mFileObserver == null) {
            mFileObserver = new FileObserver(mPath, FILE_OBSERVER_MASK) {
                @Override
                public void onEvent(int event, String path) {
                    onContentChanged();
                }
            };
        }
        mFileObserver.startWatching();
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(List<File> data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    protected void onReleaseResources(List<File> data) {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }
}

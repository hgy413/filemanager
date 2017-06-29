package com.jb.filemanager.function.music;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.jb.filemanager.TheApplication;

import java.io.File;
import java.util.List;

import static com.mopub.common.Preconditions.NoThrow.checkNotNull;

/**
 * Created by bill wang on 2017/6/28.
 */

public class MusicSupport extends AsyncTaskLoader<List<MusicInfo>> implements MusicContract.Support {

    private static final String[] PROJECTION_MUSIC = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
    };
    private Context mContext;
    public MusicSupport(@NonNull Context context) {
        super(context);
        checkNotNull(context);
        mContext = context;

    }

    @Override
    public Context getContext() {
        return TheApplication.getInstance();
    }

    @Override
    public Application getApplication() {
        return TheApplication.getInstance();
    }

    @Override
    public List<MusicInfo> getAllMusic() {
        Cursor cursor = queryData(mContext, 0L, 0L);
        if (cursor != null) {
            int clickedCount = cursor.getCount();
            Integer childCheckedCount = mChildCheckedCount.get(groupName);
            if (childCheckedCount == null) {
                childCheckedCount = 0;
            }
            boolean isChecked = childCheckedCount == clickedCount
                    && clickedCount > 0;
            try {
                if (!isChecked) {
                    mChildCheckedCount.put(groupName, clickedCount);
                } else {
                    mChildCheckedCount.put(groupName, 0);
                }

                while (cursor.moveToNext()) {
                    String path = cursor.getString(INDEX_CHILD_PATH);
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (file.exists() && file.isFile()) {
                            if (mPresenterRef != null && mPresenterRef.get() != null) {
                                if (isChecked) {
                                    if (mPresenterRef.get().isSelected(file)) {
                                        mPresenterRef.get().addOrRemoveSelected(file);
                                    }
                                } else {
                                    if (!mPresenterRef.get().isSelected(file)) {
                                        mPresenterRef.get().addOrRemoveSelected(file);
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                cursor.close();
            }
            notifyDataSetChanged();
        }
        return null;
    }

    @Override
    public List<MusicInfo> loadInBackground() {
        return getAllMusic();
    }

    private Cursor queryData(Context context, long start, long end) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_MUSIC,
                MediaStore.Audio.Media.SIZE + " > 0" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + " > ?" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + "<= ?",
                new String[]{String.valueOf(start), String.valueOf(end)},
                MediaStore.Audio.Media.DISPLAY_NAME);
    }

    @Override
    public void deliverResult(List<MusicInfo> data) {
        if (isReset()) {
            return;
        }
        if (isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        // Deliver any previously loaded data immediately if available.
//        if (cachedTasksAvailable()) {
//            deliverResult(getCachedTasks());
//        }

        // Begin monitoring the underlying data source.
//        addContentObserver(this);

//        if (takeContentChanged() || !cachedTasksAvailable()) {
            // When a change has  been delivered or the repository cache isn't available, we force
            // a load.
//            forceLoad();
//        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
//        removeContentObserver(this);
    }

    public void onTasksChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }
}

package com.jb.filemanager.function.music;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import com.jb.filemanager.util.images.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MusicContract.View,
        View.OnClickListener {

    private static final int LOADER_ID = 1;
    private static final String GROUP_ID = "_id";
    private static final String GROUP_NAME = "name";
    private static final String GROUP_START = "start";
    private static final String GROUP_END = "end";

    private static final int INDEX_GROUP_NAME = 1;
    private static final int INDEX_GROUP_START = 2;
    private static final int INDEX_GROUP_END = 3;

    public static final int INDEX_CHILD_ID = 0;
    public static final int INDEX_CHILD_DISPLAY_NAME = 1;
    public static final int INDEX_CHILD_SIZE = 2;
    public static final int INDEX_CHILD_PATH = 3;
    public static final int INDEX_CHILD_DATE_MODIFIED = 4;
    public static final int INDEX_CHILD_MIME_TYPE = 5;
    public static final int INDEX_CHILD_DURATION = 6;
    public static final int INDEX_CHILD_ARTIST = 7;

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


    private MusicContract.Presenter mPresenter;

    private ExpandableListView mElvMusic;
    private MusicAdapter mAdapter;

    private ImageFetcher mImageFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mImageFetcher = ImageUtils.createImageFetcher(this, imageWidth, R.drawable.ic_default_music);

        mPresenter = new MusicPresenter(this, new MusicSupport());
        mPresenter.onCreate(getIntent());

        initView();

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }

        if (mImageFetcher != null) {
            mImageFetcher.setExitTasksEarly(false);
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }

        if (mImageFetcher != null) {
            mImageFetcher.setPauseWork(false);
            mImageFetcher.setExitTasksEarly(true);
            mImageFetcher.flushCache();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        getLoaderManager().destroyLoader(LOADER_ID);

        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }

        super.onDestroy();
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            mPresenter.onPressHomeKey();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }


    // private start

    private void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.image_title);
            back.setOnClickListener(this);
        }

        mElvMusic = (ExpandableListView) findViewById(R.id.elv_music);
        if (mElvMusic != null) {
            mAdapter = new MusicAdapter(this);
            mAdapter.setPresenter(mPresenter);
            mElvMusic.setAdapter(mAdapter);

            mElvMusic.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                    // Pause fetcher to ensure smoother scrolling when flinging
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                        // Before Honeycomb pause image loading on scroll to help
                        // with performance
                        if (!Utils.hasHoneycomb()) {
                            mImageFetcher.setPauseWork(true);
                        }
                    } else {
                        mImageFetcher.setPauseWork(false);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView,
                                     int firstVisibleItem,
                                     int visibleItemCount,
                                     int totalItemCount) {
                }
            });
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    // private end

    // implements View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_with_search_title:
                if (mPresenter != null) {
                    mPresenter.onClickBackButton(false);
                }
                break;
            case R.id.iv_common_action_bar_with_search_search:
                // TODO
                break;
            default:
                break;
        }
    }

    // implements ImageContract.View
    @Override
    public void finishActivity() {
        super.onBackPressed();
    }

    @Override
    public void updateView() {
        // TODO
    }

    // implements LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_MODIFIED },
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MediaStore.Audio.Media.DATE_MODIFIED + " desc");
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

                mAdapter.changeCursor(matrixCursor);
                int groupCount = matrixCursor.getCount();
                for (int i = 0; i < groupCount; i++) {
                    mElvMusic.expandGroup(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                data.close();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
        restartLoader();
    }




    private static class MusicAdapter extends CursorTreeAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private WeakReference<MusicContract.Presenter> mPresenterRef;

        private Map<String, Integer> mChildCheckedCount;

        public MusicAdapter(Context context) {
            super(null, context, true);
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mChildCheckedCount = new HashMap<>();
        }

        public void setPresenter(MusicContract.Presenter presenter) {
            mPresenterRef = new WeakReference<>(presenter);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            String groupStart = groupCursor.getString(INDEX_GROUP_START);
            String groupEnd = groupCursor.getString(INDEX_GROUP_END);
            long start = 0L;
            long end = 0L;
            try {
                start = Long.valueOf(groupStart);
                end = Long.valueOf(groupEnd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return queryData(mContext, start, end);
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            View convertView = mInflater.inflate(R.layout.item_music_group, parent, false);
            GroupViewHolder holder = new GroupViewHolder();
            holder.mTvGroupName = (TextView) convertView.findViewById(R.id.tv_music_group_item_title);
            holder.mIvSelect = (ImageView) convertView.findViewById(R.id.iv_music_group_item_select);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            final GroupViewHolder holder = (GroupViewHolder) view.getTag();
            final String groupName = cursor.getString(INDEX_GROUP_NAME);
            String groupStart = cursor.getString(INDEX_GROUP_START);
            String groupEnd = cursor.getString(INDEX_GROUP_END);
            long start = 0L;
            long end = 0L;
            try {
                start = Long.valueOf(groupStart);
                end = Long.valueOf(groupEnd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cursor childCursor = queryData(mContext, start, end);
            int childCount = childCursor.getCount();
            if (mChildCheckedCount.containsKey(groupName)) {
                int selectCount = mChildCheckedCount.get(groupName);
                if (selectCount == childCount && childCount > 0) {
                    holder.mIvSelect.setImageResource(R.drawable.ic_main_storage_list_item_checked);
                } else if (selectCount > 0) {
                    holder.mIvSelect.setImageResource(R.drawable.ic_main_storage_list_item_unchecked);
                } else {
                    holder.mIvSelect.setImageResource(R.drawable.ic_main_storage_style_grid);
                }
            } else {
                holder.mIvSelect.setImageResource(R.drawable.ic_main_storage_style_grid);
            }

            final long startFinal = start;
            final long endFinal = end;
            holder.mIvSelect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Cursor clickedCursor = queryData(mContext, startFinal, endFinal);
                    if (clickedCursor != null) {
                        int clickedCount = clickedCursor.getCount();
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

                            while (clickedCursor.moveToNext()) {
                                String path = clickedCursor.getString(INDEX_CHILD_PATH);
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
                            clickedCursor.close();
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            holder.mTvGroupName.setText(groupName);
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            View convertView = mInflater.inflate(R.layout.item_music_child, parent, false);
            ChildViewHolder holder = new ChildViewHolder();
            holder.mIvCover = (ImageView) convertView.findViewById(R.id.iv_music_child_item_cover);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_music_child_item_name);
            holder.mTvInfo = (TextView) convertView.findViewById(R.id.tv_music_child_item_info);
            holder.mIvSelect = (ImageView) convertView.findViewById(R.id.iv_music_child_item_select);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            ChildViewHolder holder = (ChildViewHolder) view.getTag();
            String path = cursor.getString(INDEX_CHILD_PATH);
            String name = cursor.getString(INDEX_CHILD_DISPLAY_NAME);
            long size = cursor.getLong(INDEX_CHILD_SIZE);
            long duration = cursor.getLong(INDEX_CHILD_DURATION);
            String artist = cursor.getString(INDEX_CHILD_ARTIST);
            // 当获得的fileName为空时，从filePath中获得文件名
            if (TextUtils.isEmpty(name)) {
                name = "";
                if (!TextUtils.isEmpty(path)) {
                    name = path.substring(path.lastIndexOf(File.separator) + 1);
                }
            }

            holder.mTvName.setText(name);
            holder.mIvCover.setImageResource(R.drawable.ic_default_music);
            holder.mTvInfo.setText(artist + " " + ConvertUtil.getReadableSize(size) + " " + TimeUtil.getMSTime(duration));

            if (mPresenterRef != null && mPresenterRef.get() != null) {
                boolean isSelected = mPresenterRef.get().isSelected(new File(path));
                holder.mIvSelect.setSelected(isSelected);
                // TODO
            }
        }

        private Cursor queryData(Context context, long start, long end) {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION_MUSIC,
                    MediaStore.Audio.Media.SIZE + " > 0" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + " > ?" + " AND " + MediaStore.Audio.Media.DATE_MODIFIED + "<= ?",
                    new String[]{String.valueOf(start), String.valueOf(end)},
                    MediaStore.Audio.Media.DISPLAY_NAME);
        }

        private static class ChildViewHolder {
            ImageView mIvCover;
            TextView mTvName;
            TextView mTvInfo;
            ImageView mIvSelect;
        }

        private static class GroupViewHolder {
            TextView mTvGroupName;
            ImageView mIvSelect;
        }
    }

}

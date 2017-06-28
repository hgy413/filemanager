package com.jb.filemanager.function.music;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import com.jb.filemanager.util.images.Utils;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MusicContract.View,
        View.OnClickListener {

    private static final int LOADER_ID = 1;

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

        initView();

        mPresenter = new MusicPresenter(this, new MusicSupport());
        mPresenter.onCreate(getIntent());

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

    // implements LoaderManager.LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this,
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_MODIFIED },
                MediaStore.Audio.Media.SIZE + " > 0 ",
                null,
                MediaStore.Audio.Media.DISPLAY_NAME);;
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            try {
                while (data.moveToNext()) {
                    String path = data.getString(0);
                    long modify = data.getLong(1);

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

        private LayoutInflater mInflater;

        public MusicAdapter(Context context) {
            super(null, context, true);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            return null;
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            return null;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            return null;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

        }
    }

}

package com.jb.filemanager.function.musics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtil;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import com.jb.filemanager.util.images.Utils;

import java.util.ArrayList;
import java.util.Map;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-6-30.
 */

public class MusicActivity extends BaseActivity implements MusicContract.View,
        View.OnClickListener {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private MusicContract.Presenter mPresenter;
    private ImageFetcher mImageFetcher;
    private RecyclerView mRvMuscicList;
    private Map<String, ArrayList<MusicInfo>> mMusicDateArrayMap;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        MusicSupport support = new MusicSupport();
        mPresenter = new MusicPresenter(this, support, new MusicsLoader(this, support),
                getSupportLoaderManager());

        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mImageFetcher = ImageUtils.createImageFetcher(this, imageWidth, R.drawable.ic_default_music);
        mPresenter.onCreate(getIntent());
        initView();

    }
    // private start

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.image_title);
            back.setOnClickListener(this);
        }

        mRvMuscicList = (RecyclerView) findViewById(R.id.elv_music);
        if (mRvMuscicList != null) {
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mRvMuscicList.setLayoutManager(manager);
            // mAdapter = new MusicAdapter(this);
           // mRvMuscicList.setAdapter(mAdapter);

            mRvMuscicList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });
//                    setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                    // Pause fetcher to ensure smoother scrolling when flinging
//                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                        // Before Honeycomb pause image loading on scroll to help
//                        // with performance
//                        if (!Utils.hasHoneycomb()) {
//                            mImageFetcher.setPauseWork(true);
//                        }
//                    } else {
//                        mImageFetcher.setPauseWork(false);
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView absListView,
//                                     int firstVisibleItem,
//                                     int visibleItemCount,
//                                     int totalItemCount) {
//                }
//            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mPresenter != null) {
//            mPresenter.onResume();
//        }
        if (mImageFetcher != null) {
            mImageFetcher.setExitTasksEarly(false);
        }
    }

    @Override
    protected void onPressedHomeKey() {
        if (mPresenter != null) {
            //mPresenter.onPressHomeKey();
        }
    }


    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onClickBackButton(true);
        }
    }
    @Override
    protected void onPause() {
        if (mPresenter != null) {
           // mPresenter.onPause();
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
           // mPresenter.onDestroy();
        }

        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
           // mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

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

    @Override
    public void showMusicList(Map<String, ArrayList<MusicInfo>> mMusicMaps) {
        mMusicDateArrayMap = mMusicMaps;
        mRvMuscicList.invalidate();
    }


    public class RecyclerListAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private Map<String, ArrayList<MusicInfo>> mMusicDataMap;
        private LayoutInflater mInflater;

        public RecyclerListAdapter(@NonNull Context context, Map<String, ArrayList<MusicInfo>> map) {
            mContext = checkNotNull(context);
            mMusicDataMap = map;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = mInflater.inflate(R.layout.item_music_child, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            holder.mIvCover = (ImageView) convertView.findViewById(R.id.iv_music_child_item_cover);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_music_child_item_name);
            holder.mTvInfo = (TextView) convertView.findViewById(R.id.tv_music_child_item_info);
            holder.mIvSelect = (ImageView) convertView.findViewById(R.id.iv_music_child_item_select);
            convertView.setTag(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder)holder;
            ArrayList<MusicInfo> musicArray;
            MusicInfo info = null;
            int last = position;
            for (int i = 0; mMusicDataMap != null && i < mMusicDataMap.size(); i++) {
                musicArray = mMusicDataMap.get(i);
                if (last < musicArray.size()) {
                    info = musicArray.get(i);
                    break;
                } else {
                    last -= musicArray.size();
                }
            }
            if (info != null) {
                viewHolder.mTvName.setText(info.mName);
                viewHolder.mTvInfo.setText(info.mArtist + " " +
                        ConvertUtil.getReadableSize(info.mSize) + " " +
                        TimeUtil.getMSTime(info.mDuration));
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (int i = 0; mMusicDataMap != null && i < mMusicDataMap.size(); i++) {
                count += mMusicDataMap.get(i).size();
            }
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mIvCover;
            TextView mTvName;
            TextView mTvInfo;
            ImageView mIvSelect;
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}

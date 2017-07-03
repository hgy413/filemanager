package com.jb.filemanager.function.musics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtil;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
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
    private GroupList<String, MusicInfo> mMusicDataArrayMap;
    private RecyclerListAdapter mMusicListAdapter;
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


    private void initView() {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        if (back != null) {
            back.getPaint().setAntiAlias(true);
            back.setText(R.string.music_title);
            back.setOnClickListener(this);
        }

        mRvMuscicList = (RecyclerView) findViewById(R.id.elv_music);
        int height = 40;
        // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
        float scale = this.getResources().getDisplayMetrics().density;
        height =  (int) (height * scale + 0.5f);
        StickyDecoration decoration = StickyDecoration.Builder
                .init(new GroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //获取组名，用于判断是否是同一组
                        return mMusicDataArrayMap.getGroupKey(position);
                    }

                    @Override
                    public View getGroupView(int position) {
                        //获取自定定义的组View
                        if (position < mMusicDataArrayMap.size()) {
                            View view = getLayoutInflater().inflate(R.layout.item_music_group, null, false);
                            ((TextView) view.findViewById(R.id.tv_music_group_item_title))
                                    .setText(mMusicDataArrayMap.getGroupKey(position));
                            view.findViewById(R.id.iv_music_group_item_select);
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                .setGroupHeight(height)   //设置高度
                .build();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvMuscicList.setLayoutManager(manager);
//        if (mRvMuscicList != null) {
//            // mAdapter = new MusicAdapter(this);
//           // mRvMuscicList.setAdapter(mAdapter);
//
//            mRvMuscicList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                }
//            });
//        }
        mRvMuscicList.addItemDecoration(decoration);
        mMusicListAdapter = new RecyclerListAdapter(this, mMusicDataArrayMap);
        mRvMuscicList.setAdapter(mMusicListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.start();
        }
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
    public void showMusicList(GroupList<String, MusicInfo> mMusicMaps) {
        mMusicDataArrayMap = mMusicMaps;
        int i = mMusicDataArrayMap.itemSize();
        mMusicListAdapter.notifyDataSetChanged();
    }


    public class RecyclerListAdapter extends RecyclerView.Adapter {
        private Context mContext;
        //private GroupList<String, MusicInfo> mMusicDataArrayMap;
        private LayoutInflater mInflater;

        public RecyclerListAdapter(@NonNull Context context, GroupList<String, MusicInfo> mapList) {
            mInflater = MusicActivity.this.getLayoutInflater();
            mContext = checkNotNull(context);
            mMusicDataArrayMap = mapList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = mInflater.inflate(R.layout.item_music_child, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            holder.mIvCover = (ImageView) convertView.findViewById(R.id.iv_music_child_item_cover);
            holder.mIvCover.setImageResource(R.drawable.ic_default_music);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_music_child_item_name);
            holder.mTvInfo = (TextView) convertView.findViewById(R.id.tv_music_child_item_info);
            holder.mIvSelect = (ImageView) convertView.findViewById(R.id.iv_music_child_item_select);
            convertView.setTag(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder)holder;
            MusicInfo info = mMusicDataArrayMap.getItem(position);

            if (info != null) {
                viewHolder.mTvName.setText(info.mName);
                viewHolder.mTvInfo.setText(info.mArtist + " " +
                        ConvertUtil.getReadableSize(info.mSize) + " " +
                        TimeUtil.getMSTime(info.mDuration));
            }
        }

        @Override
        public int getItemCount() {
            return mMusicDataArrayMap != null ? mMusicDataArrayMap.itemSize() : 0;
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

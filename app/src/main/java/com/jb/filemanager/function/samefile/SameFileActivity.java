package com.jb.filemanager.function.samefile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.DeleteFileDialog;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.DensityUtil;
import com.jb.filemanager.util.TimeUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;

/**
 * Created by bool on 17-6-30.
 * 显示音乐列表
 */

public class SameFileActivity extends BaseActivity implements SameFileContract.View,
        View.OnClickListener {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private SameFileContract.Presenter mPresenter;
    private ImageFetcher mImageFetcher;
    private RecyclerView mRvMuscicList;
    private GroupList<String, FileInfo> mMusicDataArrayMap;
    private RecyclerListAdapter mMusicListAdapter;
    private BottomOperateBar mBottomOperateContainer;

    private boolean[] mItemSelected;
    private int mSelecedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_file);
        mItemSelected = new boolean[0];
        mPresenter = new SameFilePresenter(this, new SameFileSupport(), getSupportLoaderManager());
        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mImageFetcher = ImageUtils.createImageFetcher(this, imageWidth, R.drawable.img_music);
        mPresenter.onCreate(getIntent());
    }

    @Override
    public void initView(final int fileType) {
        TextView back = (TextView) findViewById(R.id.tv_common_action_bar_with_search_title);
        back.getPaint().setAntiAlias(true);
        switch (fileType) {
            case Const.FILE_TYPE_MUSIC:
                back.setText(R.string.music_title);
                break;
            case Const.FILE_TYPE_VIDEO:
                back.setText(R.string.video_title);
                break;
            case Const.FILE_TYPE_DOWNLOAD:
                back.setText(R.string.download_title);
                break;
            default:
                back.setText("Transfer unknow type");
        }
        back.setOnClickListener(this);

        mRvMuscicList = (RecyclerView) findViewById(R.id.elv_music);
        int height = 40;
        // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
        float scale = this.getResources().getDisplayMetrics().density;
        height =  (int) (height * scale + 0.5f);
        StickyDecoration decoration = StickyDecoration.Builder
                .init(new StickyDecoration.GroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //获取组名，用于判断是否是同一组
                        return mMusicDataArrayMap.getGroupKey(position);
                    }

                    @Override
                    public View getGroupView(int position) {
                        //获取自定定义的组View
                        if (position < mMusicDataArrayMap.itemSize()) {
                            View view = getLayoutInflater().inflate(R.layout.item_samefile_group,
                                    null, false);
                            ((TextView) view.findViewById(R.id.tv_music_group_item_title))
                                    .setText(mMusicDataArrayMap.getGroupKey(position));
                            ((ImageView)view.findViewById(R.id.iv_music_group_item_select))
                                    .setOnClickListener(SameFileActivity.this);
                            return view;
                        } else {
                            return null;
                        }
                    }
                })
                .setGroupHeight(DensityUtil.dip2px(SameFileActivity.this, 40))   //设置高度40dp
                .setGroupDevideHeight(DensityUtil.dip2px(SameFileActivity.this, 10))// Group devide line
                .build();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvMuscicList.setLayoutManager(manager);
        mRvMuscicList.addItemDecoration(decoration);
        mMusicListAdapter = new RecyclerListAdapter(this, mMusicDataArrayMap);
        mRvMuscicList.setAdapter(mMusicListAdapter);

        mBottomOperateContainer = (BottomOperateBar) findViewById(R.id.bottom_operate_bar_container);
        mBottomOperateContainer.onClickedAction(new BottomOperateBar.OnBottomClicked() {
            @Override
            public void onCutClicked() {
                mPresenter.onClickOperateCutButton(mItemSelected);
            }

            @Override
            public void onCopyClicked() {
                mPresenter.onClickOperateCopyButton(mItemSelected);
            }

            @Override
            public void onDeleteClicked() {
                mPresenter.onClickOperateDeleteButton();

            }

            @Override
            public void onMoreClicked() {
                mPresenter.onClickOperateMoreButton(mItemSelected);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.iv_music_group_item_select:
                Toast.makeText(this, "Clicked", Toast.LENGTH_LONG);
            default:
                break;
        }
    }

    @Override
    public void showMusicList(GroupList<String, FileInfo> mMusicMaps) {
        mMusicDataArrayMap = mMusicMaps;
        mItemSelected = new boolean[mMusicMaps.itemSize()];
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = false;
        }
        mSelecedCount = 0;
        mMusicListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeleteConfirmDialog() {
        DeleteFileDialog dialog = new DeleteFileDialog(this, new DeleteFileDialog.Listener() {
            @Override
            public void onConfirm(DeleteFileDialog dialog) {
                dialog.dismiss();
                if (mPresenter != null) {
                    mPresenter.onClickConfirmDeleteButton(mItemSelected);
                }
            }

            @Override
            public void onCancel(DeleteFileDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showBottomMoreOperatePopWindow(boolean multiSelected) {
        int resId;
        if (multiSelected) {
            resId = R.layout.pop_mutli_file_operate_more;
        } else {
            resId = R.layout.pop_single_file_operate_more;
        }

        // 一个自定义的布局，作为显示的内容
        View contentView = getLayoutInflater().inflate(resId, null);
        TextView details = (TextView) contentView.findViewById(R.id.tv_main_operate_more_detail);
        TextView rename = (TextView) contentView.findViewById(R.id.tv_main_operate_more_rename);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(APIUtil.getDrawable(this, R.color.white));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(mBottomOperateContainer,
                mBottomOperateContainer.getWidth() - contentView.getWidth(), 5);

        if (details != null) {
            details.getPaint().setAntiAlias(true);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter != null) {
                        mPresenter.onClickOperateDetailButton();
                    }
                    popupWindow.dismiss();
                }
            });
        }

        if (rename != null) {
            rename.getPaint().setAntiAlias(true);
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter != null) {
                        mPresenter.onClickOperateRenameButton();
                    }
                    popupWindow.dismiss();
                }
            });
        }
    }

    public class RecyclerListAdapter extends RecyclerView.Adapter {
        private Context mContext;
        //private GroupList<String, FileInfo> mMusicDataArrayMap;
        private LayoutInflater mInflater;

        public RecyclerListAdapter(@NonNull Context context, GroupList<String, FileInfo> mapList) {
            mInflater = SameFileActivity.this.getLayoutInflater();
            mContext = context;
            mMusicDataArrayMap = mapList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = mInflater.inflate(R.layout.item_samefile_child, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            holder.mIvCover = (ImageView) convertView.findViewById(R.id.iv_music_child_item_cover);
            holder.mIvCover.setImageResource(R.drawable.img_music);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_music_child_item_name);
            holder.mTvInfo = (TextView) convertView.findViewById(R.id.tv_music_child_item_info);
            holder.mIvSelect = (ImageView) convertView.findViewById(R.id.iv_music_child_item_select);
            holder.setCliceListener();
            convertView.setTag(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder)holder;
            FileInfo info = mMusicDataArrayMap.getItem(position);
            if (mItemSelected[position]) {
                viewHolder.mIvSelect.setImageResource(R.drawable.choose_all);
            } else {
                viewHolder.mIvSelect.setImageResource(R.drawable.choose_none);
            }
            if (info != null) {
                viewHolder.mTvName.setText(info.mName);
                viewHolder.mTvInfo.setText(info.mArtist + "  " +
                        ConvertUtils.getReadableSize(info.mSize) + "  " +
                        TimeUtil.getMSTime(info.mDuration));
            }
        }

        @Override
        public int getItemCount() {
            return mMusicDataArrayMap != null ? mMusicDataArrayMap.itemSize() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            ImageView mIvCover;
            TextView mTvName;
            TextView mTvInfo;
            ImageView mIvSelect;
            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            public void setCliceListener(){
                mIvSelect.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int posion = getAdapterPosition();
                switch (v.getId()) {
                    case R.id.iv_music_child_item_select:
                        if (mItemSelected[posion]) {
                            mItemSelected[posion] = false;
                            mIvSelect.setImageResource(R.drawable.choose_none);
                            SameFileActivity.this.mSelecedCount--;
                        } else {
                            mItemSelected[posion] = true;
                            mIvSelect.setImageResource(R.drawable.ic_common_item_selected);
                            SameFileActivity.this.mSelecedCount++;
                        }
                        break;
                    default:
                        break;
                }

                if (SameFileActivity.this.mSelecedCount > 0) {
                    mBottomOperateContainer.setVisibility(View.VISIBLE);
                } else {
                    mBottomOperateContainer.setVisibility(View.GONE);
                }
            }
        }
    }
}

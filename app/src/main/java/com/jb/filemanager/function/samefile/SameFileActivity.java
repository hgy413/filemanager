package com.jb.filemanager.function.samefile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.DeleteFileDialog;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by bool on 17-6-30.
 * 显示音乐列表
 */

public class SameFileActivity extends BaseActivity implements SameFileContract.View,
        View.OnClickListener {

    private SameFileContract.Presenter mPresenter;
    private ImageFetcher mImageFetcher;
    private ExpandableListView mElvFilelist;
    private GroupList<String, FileInfo> mMusicDataArrayMap;
    private FileExpandableListAdapter mFileExpandableListAdapter;
    private BottomOperateBar mBottomOperateContainer;
    private LinearLayout mLlNoFileView;
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
        mElvFilelist = (ExpandableListView) findViewById(R.id.elv_same_file_list);
        mFileExpandableListAdapter = new FileExpandableListAdapter(
                new FileExpandableListAdapter.ItemChooseChangeListener(){
                    @Override
                    public void onChooseNumChanged(int num) {
                       if (num > 0) {
                           mBottomOperateContainer.setVisibility(View.VISIBLE);
                       } else {
                           mBottomOperateContainer.setVisibility(View.GONE);
                       }
                    }
                });
        mElvFilelist.setAdapter(mFileExpandableListAdapter);

        mBottomOperateContainer = (BottomOperateBar) findViewById(R.id.bottom_operate_bar_container);
        mBottomOperateContainer.setListener(new BottomOperateBar.Listener() {
            @Override
            public ArrayList<File> getCurrentSelectedFiles() {
                return mPresenter.getSelectFile();
            }

            @Override
            public Activity getActivity() {
                return SameFileActivity.this;
            }

            @Override
            public void afterCopy() {
                mPresenter.jumpToStoragePage();
            }

            @Override
            public void afterCut() {
                mPresenter.jumpToStoragePage();
            }

            @Override
            public void afterRename() {
                mPresenter.onCreate(getIntent());
            }

            @Override
            public void afterDelete() {
                mPresenter.onCreate(getIntent());
            }
        });

        // TODO @bool
//        mBottomOperateContainer.onClickedAction(new BottomOperateBar.OnBottomClicked() {
//            @Override
//            public void onCutClicked() {
//                mPresenter.onClickOperateCutButton(mItemSelected);
//            }
//
//            @Override
//            public void onCopyClicked() {
//                mPresenter.onClickOperateCopyButton(mItemSelected);
//            }
//
//            @Override
//            public void onDeleteClicked() {
//                mPresenter.onClickOperateDeleteButton();
//
//            }
//
//            @Override
//            public void onMoreClicked() {
//                mPresenter.onClickOperateMoreButton(mItemSelected);
//            }
//        });
        mLlNoFileView = (LinearLayout)findViewById(R.id.ll_no_file);
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
            default:
                break;
        }
    }

    @Override
    public void showFileList(GroupList<String, FileInfo> mMusicMaps) {
        mMusicDataArrayMap = mMusicMaps;
        mItemSelected = new boolean[mMusicMaps.itemSize()];
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = false;
        }
        mSelecedCount = 0;
        //mMusicListAdapter.notifyDataSetChanged();
        mFileExpandableListAdapter.reflaceDate(mMusicMaps);
        int groupCount = mMusicDataArrayMap.size();
        for (int i = 0; i < groupCount; i++) {
            mElvFilelist.expandGroup(i);
        }
    }

//    @Override
//    public void showDeleteConfirmDialog() {
//        DeleteFileDialog dialog = new DeleteFileDialog(this, new DeleteFileDialog.Listener() {
//            @Override
//            public void onConfirm(DeleteFileDialog dialog) {
//                dialog.dismiss();
//                if (mPresenter != null) {
//                    mPresenter.onClickConfirmDeleteButton(mItemSelected);
//                }
//            }
//
//            @Override
//            public void onCancel(DeleteFileDialog dialog) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

//    @Override
//    public void showBottomMoreOperatePopWindow(boolean multiSelected) {
//        int resId;
//        if (multiSelected) {
//            resId = R.layout.pop_mutli_file_operate_more;
//        } else {
//            resId = R.layout.pop_single_file_operate_more;
//        }

//        // 一个自定义的布局，作为显示的内容
//        View contentView = getLayoutInflater().inflate(resId, null);
//        TextView details = (TextView) contentView.findViewById(R.id.tv_main_operate_more_detail);
//        TextView rename = (TextView) contentView.findViewById(R.id.tv_main_operate_more_rename);
//
//        final PopupWindow popupWindow = new PopupWindow(contentView,
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popupWindow.setTouchable(true);
//
//        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            }
//        });
//
//        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
//        // 我觉得这里是API的一个bug
//        popupWindow.setBackgroundDrawable(APIUtil.getDrawable(this, R.color.white));
//
//        // 设置好参数之后再show
//        popupWindow.showAsDropDown(mBottomOperateContainer,
//                mBottomOperateContainer.getWidth() - contentView.getWidth(), 5);
//
//        if (details != null) {
//            details.getPaint().setAntiAlias(true);
//            details.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mPresenter != null) {
//                        mPresenter.onClickOperateDetailButton();
//                    }
//                    popupWindow.dismiss();
//                }
//            });
//        }

//        if (rename != null) {
//            rename.getPaint().setAntiAlias(true);
//            rename.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mPresenter != null) {
//                        mPresenter.onClickOperateRenameButton();
//                    }
//                    popupWindow.dismiss();
//                }
//            });
//        }
//    }
    @Override
    public void onNoFileFindShow () {
        mLlNoFileView.setVisibility(View.VISIBLE);
    }
}

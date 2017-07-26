package com.jb.filemanager.function.samefile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.ui.view.SearchTitleView;
import com.jb.filemanager.ui.view.SearchTitleViewCallback;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import java.io.File;
import java.util.ArrayList;

import static com.jb.filemanager.R.id.search_title;

/**
 * Created by bool on 17-6-30.
 * 显示音乐列表
 */

public class SameFileActivity extends BaseActivity implements SameFileContract.View {

    public static final String PARAM_CATEGORY_TYPE = "param_category_type";

    private SameFileContract.Presenter mPresenter;
    private ImageFetcher mImageFetcher;
    private FloatingGroupExpandableListView mElvFilelist;
    private GroupList<String, FileInfo> mFileGroupList;
    private FileExpandableListAdapter mFileExpandableListAdapter;
    private SearchTitleView mSearchTitle;
    private BottomOperateBar mBottomOperateContainer;
    private LinearLayout mLlNoFileView;
    private boolean[] mItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_file);
        mItemSelected = new boolean[0];
        mPresenter = new SameFilePresenter(this, new SameFileSupport(), getSupportLoaderManager());
        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mImageFetcher = ImageUtils.createImageFetcher(this, imageWidth, R.drawable.music_icon);
        mPresenter.onCreate(getIntent());
    }

    @Override
    public void initView(final int fileType) {
        mSearchTitle = (SearchTitleView) findViewById(search_title);
        switch (fileType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                mSearchTitle.setTitleName("Music");// .setText(R.string.music_title);
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                mSearchTitle.setTitleName("Video"); // .setText(R.string.video_title);
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                mSearchTitle.setTitleName("Download"); // back.setText(R.string.download_title);
                break;
            default:
                mSearchTitle.setTitleName("Transfer unknow type"); // back.setText("Transfer unknow type");
        }
        mSearchTitle.setClickCallBack(new SearchTitleViewCallback(){
            @Override
            public void onSearchClick() {
                SearchActivity.showSearchResult(getApplicationContext(), Const.CategoryType.CATEGORY_TYPE_ZIP);
            }

            @Override
            public void onIvCancelSelectClick() {
                mPresenter.cleanSelect();
            }

            @Override
            public void onIvBackClick() {
                finish();
            }

            @Override
            public void onSelectBtnClick() {
                if (mFileExpandableListAdapter.getSelectCount() == mFileGroupList.itemSize()) {
                    mPresenter.cleanSelect();
                } else {
                    mPresenter.selectAllFile();
                }
            }
        });

        mElvFilelist = (FloatingGroupExpandableListView) findViewById(R.id.elv_same_file_list);
        mFileExpandableListAdapter = new FileExpandableListAdapter(SameFileActivity.this,
                new FileExpandableListAdapter.ItemChooseChangeListener() {
                    @Override
                    public void onChooseNumChanged(int num) {
                        fileSelectShow(num);
                    }
                });
        mElvFilelist.setAdapter(new WrapperExpandableListAdapter(mFileExpandableListAdapter));
        mElvFilelist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                        int childPosition, long id) {
                FileInfo fileInfo = mFileGroupList.valueAt(groupPosition).get(childPosition);
                Intent mFileIntent = FileUtil.getOpenFileIntent(fileInfo.mFullPath);
                if (mFileIntent != null) {
                    startActivity(mFileIntent);
                }
                return false;
            }
        });

        mBottomOperateContainer = (BottomOperateBar) findViewById(R.id.bottom_operate_bar_container);
        mBottomOperateContainer.setListener(new BottomOperateBar.Listener() {
            ArrayList<File> selectFileList;
            @Override
            public ArrayList<File> getCurrentSelectedFiles() {
                selectFileList = mPresenter.getSelectFile();
                return selectFileList;
            }

            @Override
            public Activity getActivity() {
                return SameFileActivity.this;
            }

            @Override
            public void afterCopy() {
                mPresenter.jumpToFileBrowserPage();
            }

            @Override
            public void afterCut() {
                mPresenter.jumpToFileBrowserPage();
            }

            @Override
            public void afterRename() {
                //mPresenter.reloadData();
                fileSelectShow(0);
                mBottomOperateContainer.setVisibility(View.GONE);
            }

            @Override
            public void afterDelete() {
                mPresenter.onCreate(getIntent());
                fileSelectShow(0);
                mBottomOperateContainer.setVisibility(View.GONE);
            }

            @Override
            public void statisticsClickCopy() {
                // TODO 统计
            }

            @Override
            public void statisticsClickCut() {
                // TODO 统计
            }

            @Override
            public void statisticsClickDelete() {
                // TODO 统计
            }

            @Override
            public void statisticsClickMore() {
                // TODO 统计
            }

            @Override
            public void statisticsClickRename() {
                // TODO 统计
            }

            @Override
            public void statisticsClickDetail() {
                // TODO 统计
            }
        });

        mLlNoFileView = (LinearLayout) findViewById(R.id.ll_no_file);
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
        if (mFileExpandableListAdapter.getSelectCount() > 0) {
            mPresenter.cleanSelect();
        } else if (mPresenter != null) {
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
            mPresenter.onDestroy();
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
    public void showFileList(GroupList<String, FileInfo> mMusicMaps) {
        mFileGroupList = mMusicMaps;
        mItemSelected = new boolean[mMusicMaps.itemSize()];
        for (int i = 0; i < mItemSelected.length; i++) {
            mItemSelected[i] = false;
        }
        mFileExpandableListAdapter.reflaceDate(mMusicMaps);
        int groupCount = mFileGroupList.size();
        for (int i = 0; i < groupCount; i++) {
            mElvFilelist.expandGroup(i);
        }
    }

    @Override
    public void onNoFileFindShow() {
        mLlNoFileView.setVisibility(View.VISIBLE);
    }

    @Override
    public void fileSelectShow(int num) {
        boolean isSlected = false;
        if (num > 0) {
            mSearchTitle.setSelectedCount(num);
            if (num == mFileGroupList.getAllSize()) {
                mSearchTitle.setSelectBtnResId(2); // Select all
            } else {
                mSearchTitle.setSelectBtnResId(1); // Select part
            }
            isSlected = true;
        }
        mSearchTitle.switchTitleMode(isSlected);
        mBottomOperateContainer.setVisibility(isSlected ? View.VISIBLE : View.GONE);
    }
}

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
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.docmanager.DocManagerActivity;
import com.jb.filemanager.function.image.ImageActivity;
import com.jb.filemanager.function.image.ImageDetailFragment;
import com.jb.filemanager.function.image.adapter.ImageExpandableAdapter;
import com.jb.filemanager.function.image.modle.ImageGroupModle;
import com.jb.filemanager.function.image.modle.ImageModle;
import com.jb.filemanager.function.recent.RecentImageActivity;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.function.txtpreview.TxtPreviewActivity;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.ui.view.SearchTitleView;
import com.jb.filemanager.ui.view.SearchTitleViewCallback;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.CommonLoadingView;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.images.ImageFetcher;
import com.jb.filemanager.util.images.ImageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jb.filemanager.R.id.cancel;
import static com.jb.filemanager.R.id.list;
import static com.jb.filemanager.R.id.search_title;

/**
 * Created by bool on 17-6-30.
 * 分类显示列表
 */

public class SameFileActivity extends BaseActivity implements SameFileContract.View {

    public static final String PARAM_CATEGORY_TYPE = "param_category_type";
    private static int sCategoryType = -1;
    private static String sTitleShow = "";
    private SameFileContract.Presenter mPresenter;
    private ImageFetcher mImageFetcher;
    private FloatingGroupExpandableListView mElvFilelist;
    private GroupList<String, FileInfo> mFileGroupList;
    private FileExpandableListAdapter mFileExpandableListAdapter;
    private SearchTitleView mSearchTitle;
    private BottomOperateBar mBottomOperateContainer;
    private LinearLayout mLlNoFileView;
    private CommonLoadingView mLoadingView;
    private boolean[] mItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_file);
        mItemSelected = new boolean[0];
        mPresenter = new SameFilePresenter(this, new SameFileSupport(), getSupportLoaderManager());
        int imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mImageFetcher = ImageUtils.createImageFetcher(this, imageWidth, R.drawable.file_type_music);
        mPresenter.onCreate(getIntent());
    }

    @Override
    public void initView(final int fileType) {
        sCategoryType = fileType;
        mSearchTitle = (SearchTitleView) findViewById(search_title);
        switch (fileType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                sTitleShow = "Music";
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                sTitleShow = "Video";
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                sTitleShow = "Download";
                break;
            default:
                sTitleShow = "Unknow";
        }
        mSearchTitle.setTitleName(sTitleShow);

        mElvFilelist = (FloatingGroupExpandableListView) findViewById(R.id.elv_same_file_list);
        mFileExpandableListAdapter = new FileExpandableListAdapter(SameFileActivity.this,
                new FileExpandableListAdapter.ItemChooseChangeListener() {
                    @Override
                    public void onChooseNumChanged(int num) {
                        fileSelectShow(num);
                    }
                });
        mElvFilelist.setAdapter(new WrapperExpandableListAdapter(mFileExpandableListAdapter));
        mBottomOperateContainer = (BottomOperateBar) findViewById(R.id.bottom_operate_bar_container);
        mLlNoFileView = (LinearLayout) findViewById(R.id.ll_no_file);
        mLoadingView = (CommonLoadingView) findViewById(R.id.mlv_image_loading);
        initClicklistener();
    }



    private void initClicklistener() {
        mSearchTitle.setClickCallBack(new SearchTitleViewCallback(){
            @Override
            public void onSearchClick() {
                statisticsClickSearch();
                SearchActivity.showSearchResult(getApplicationContext(), Const.CategoryType.CATEGORY_TYPE_ZIP);
            }

            @Override
            public void onIvCancelSelectClick() {
                mPresenter.cleanSelect();
            }

            @Override
            public void onIvBackClick() {
                statisticsClickExit("2");
                //SameFileActivity.this.onDestroy();
                mPresenter.onClickBackButton(true);
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
        mElvFilelist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                        int childPosition, long id) {
                statisticsClickItem();
                statisticsCalledSystemPaly();
                FileInfo fileInfo = mFileGroupList.valueAt(groupPosition).get(childPosition);
                Intent fileOpenIntent = null;
                switch (fileInfo.mFileType) {
                    case FileManager.TXT:
                        long fileSize = new File(fileInfo.mFullPath).length();
                        if (fileSize <= 1024 * 1024) {//只提供1M以下的小文件的预览
                            fileOpenIntent = new Intent(SameFileActivity.this, TxtPreviewActivity.class);
                            fileOpenIntent.putExtra(TxtPreviewActivity.TARGET_DOC_PATH, fileInfo.mFullPath);
                        } else {
                            fileOpenIntent = FileUtil.getOpenFileIntent(fileInfo.mFullPath);
                        }
                        startActivity(fileOpenIntent);
                        break;
                    case FileManager.IMAGE:
                        ArrayList<FileInfo> arrayList =  mFileGroupList.get("Picture");
                        ArrayList<ImageModle> imagelist = getAllImagesModule(arrayList);
                        if (imagelist.size() > 0) {
                            RecentImageActivity.startView(SameFileActivity.this, imagelist, arrayList.indexOf(fileInfo));
                        }
                        break;
                    case FileManager.ZIP:
                    default:
                        fileOpenIntent = FileUtil.getOpenFileIntent(fileInfo.mFullPath);
                        startActivity(fileOpenIntent);
                }
                return false;
            }
        });

        mElvFilelist.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                statisticsClickGroup();
                return false;
            }
        });

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
                SameFileActivity.statisticsClickCopy();
            }

            @Override
            public void statisticsClickCut() {
                SameFileActivity.statisticsClickCut();
            }

            @Override
            public void statisticsClickDelete() {
                SameFileActivity.statisticsClickDelete();
            }

            @Override
            public void statisticsClickMore() {
                SameFileActivity.statisticsClickMore();
            }

            @Override
            public void statisticsClickRename() {
                SameFileActivity.statisticsClickRename();
            }

            @Override
            public void statisticsClickDetail() {
                SameFileActivity.statisticsClickDetail();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mImageFetcher != null) {
            mImageFetcher.setExitTasksEarly(false);
        }
    }
    ArrayList<ImageModle> getAllImagesModule(ArrayList<FileInfo> arrayList) {
        ArrayList<ImageModle> moduleList = new ArrayList();
        ImageModle image;
        for (FileInfo info : arrayList ) {
            image = new ImageModle(info.mFullPath, 0, info.isSelected, 0);
            moduleList.add(image);
        }
        return moduleList;
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
            statisticsClickExit("1");
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
        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void showFileList(GroupList<String, FileInfo> mMusicMaps) {
        mLoadingView.setVisibility(View.GONE);
        mLlNoFileView.setVisibility(View.GONE);
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
        mLoadingView.setVisibility(View.GONE);
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


    //====================统计代码  Start =====================
    protected void statisticsClickGroup() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_GROUP_TITLE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_GROUP_TITLE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_GROUP_TITLE;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击折叠分类---" + bean.mOperateId);
    }
    protected void statisticsSelectGroup() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_SELECT_GROUP;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_SELECT_GROUP;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_SELECT_GROUP;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击分组复选框---" + bean.mOperateId);
    }
    protected void statisticsSelectItem() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_SELECT_ITEN;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_SELECT_ITEN;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_SELECT_ITEN;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击单项选择框---" + bean.mOperateId);
    }
    static private void statisticsClickCopy() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_COPY;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_COPY;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_COPY;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sCategoryType + " 点击复制---" + bean.mOperateId);
    }

    static private void statisticsClickCut() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_CUT;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_CUT;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_CUT;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sCategoryType + " 点击剪切---" + bean.mOperateId);
    }

    static protected void statisticsClickPast() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_PAST;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_PAST;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_PAST;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击粘贴---" + bean.mOperateId);
    }

    static private void statisticsClickDelete() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_DELETE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_DELETE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_DELETE;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击删除---" + bean.mOperateId);
    }

    static private void statisticsClickRename() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_RENAME;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_RENAME;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_RENAME;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击重命名---" + bean.mOperateId);
    }

    static private void statisticsClickDetail() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_DETAIL;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_DETAIL;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_DETAIL;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击详情---" + bean.mOperateId);
    }

    static private void statisticsClickMore() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_MORE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_MORE;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_MORE;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击more---" + bean.mOperateId);
    }

    private void statisticsClickItem() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_PLAY;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_ITEM;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_ITEM;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sCategoryType + " 点击单个条目---" + bean.mOperateId);
    }

    private void statisticsClickSearch() {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_SEARCH;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_SEARCH;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_SEARCH;
                break;
            default:

        }
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 点击搜索---" + bean.mOperateId);
    }

    private void statisticsCalledSystemPaly() {
        if (sCategoryType == Const.CategoryType.CATEGORY_TYPE_MUSIC) {
            Statistics101Bean bean = Statistics101Bean.builder();
            bean.mOperateId = StatisticsConstants.MUSIC_CALLED_SYSTEM_PLAY;

            StatisticsTools.upload101InfoNew(bean);
            Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 音乐播放调用成功---" + bean.mOperateId);
        }
    }

    private void statisticsClickExit(String entrance) {
        Statistics101Bean bean = Statistics101Bean.builder();
        switch (sCategoryType) {
            case Const.CategoryType.CATEGORY_TYPE_MUSIC:
                bean.mOperateId = StatisticsConstants.MUSIC_CLICK_EXIT;
                break;
            case Const.CategoryType.CATEGORY_TYPE_VIDEO:
                bean.mOperateId = StatisticsConstants.VIDEO_CLICK_EXIT;
                break;
            case Const.CategoryType.CATEGORY_TYPE_DOWNLOAD:
                bean.mOperateId = StatisticsConstants.DOWNLOAD_CLICK_EXIT;
                break;
            default:

        }
        bean.mEntrance = entrance;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, sTitleShow + " 退出---" + bean.mOperateId);
    }

    //====================统计代码  end =====================
}

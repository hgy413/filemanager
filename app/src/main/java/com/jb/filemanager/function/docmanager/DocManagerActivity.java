package com.jb.filemanager.function.docmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.function.txtpreview.TxtPreviewActivity;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.ui.widget.BottomOperateBar;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocManagerActivity extends BaseActivity implements DocManagerContract.View, View.OnClickListener {
    //    public static final int UNINSTALL_APP_REQUEST_CODE = 101;
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
    public static final String TAG = "DocManagerActivity";
    public static final String SEARCH_RESULT = "search_result";
    public static final int SEARCH_RESULT_REQUEST_CODE = 102;
    private static final int TXT_PREVIEW_REQUEST_CODE = 103;
    private static final String TXT_FILE_DATA = "txt_file_data";
    private DocManagerPresenter mPresenter;
    private TextView mTvCommonActionBarWithSearchTitle;
    private ImageView mIvCommonActionBarBack;
    private ImageView mIvCommonActionBarWithSearchSearch;
    private FloatingGroupExpandableListView mElvApk;

    private BottomOperateBar mBobBottomOperator;

    private DocManagerAdapter mAdapter;
    private List<DocGroupBean> mAppInfo;
    private boolean mIsSelectMode;
    private boolean mIsAllSelect;
    ArrayList<File> mChosenFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_manager);

        mChosenFiles = new ArrayList<>();
        mPresenter = new DocManagerPresenter(this, new DocManagerSupport());
        mPresenter.onCreate(getIntent());
        initView();
        initData();
        initClick();
        initList();
        initBroadcastReceiver();
    }

    @Override
    public void initView() {
        mTvCommonActionBarWithSearchTitle = (TextView) findViewById(R.id.tv_common_action_bar_title);
        mIvCommonActionBarBack = (ImageView) findViewById(R.id.iv_common_action_bar_back);
        mIvCommonActionBarWithSearchSearch = (ImageView) findViewById(R.id.iv_common_action_bar_search);
        mElvApk = (FloatingGroupExpandableListView) findViewById(R.id.elv_apk);
        mBobBottomOperator = (BottomOperateBar) findViewById(R.id.bob_bottom_operator);

        mTvCommonActionBarWithSearchTitle.setText(R.string.doc_manager_title);
    }

    @Override
    public void initData() {
        mPresenter.setDocScanListener(new DocScanListener() {
            @Override
            public void onScanStart() {
//                Toast.makeText(DocManagerActivity.this, "加载开始了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanFinish(final ArrayList<DocGroupBean> arrayList, final boolean keepCheck) {
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(DocManagerActivity.this, "加载完成了", Toast.LENGTH_SHORT).show();
                        if (arrayList == null) {
                            return;
                        }
                        mAppInfo = arrayList;
                        if (mAdapter == null) {
                            mAdapter = new DocManagerAdapter(mAppInfo);
                            mAdapter.setOnItemChosenListener(new DocManagerAdapter.OnItemChosenListener() {
                                @Override
                                public void onItemChosen(int chosenCount) {
                                    handleBottomDeleteShow(chosenCount);
                                    handleTitleSelectChange(chosenCount);
                                }
                            });
                            mElvApk.setAdapter(new WrapperExpandableListAdapter(mAdapter));
                            mAdapter.handleCheckedCount();
                            for (int i = 0; i < mAppInfo.size(); i++) {
                                mElvApk.expandGroup(i);
                            }
                        } else {
                            mAdapter.setListData(mAppInfo, keepCheck);
                        }
                    }
                });

            }

            @Override
            public void onLoadError() {

            }

            @Override
            public void onLoadProgress(int progress) {

            }
        });
        mPresenter.getDocInfo(true, true);
    }

    /**
     * 处理标题栏的变化
     *
     * @param chosenCount 选中的数量
     */
    private void handleTitleSelectChange(int chosenCount) {
        if (chosenCount == 0) {
            mIsSelectMode = false;
            refreshTile();
        } else {
            handleTitleSelectMode(chosenCount);
        }
    }

    private void handleTitleSelectMode(int chosenCount) {
        if (!mIsSelectMode) {//只有之前不是选择模式  才需要改变图片
            mIvCommonActionBarBack.setImageResource(R.drawable.ic_cancel_blue);
        }
        int totalCount = 0;
        for (DocGroupBean groupBean : mAppInfo) {
            totalCount += groupBean.getchildrenSize();
        }
        if (chosenCount == totalCount) {
            mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.choose_all);
        } else {
            mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.title_select_all);
        }
        mTvCommonActionBarWithSearchTitle.setText(getString(R.string.common_title_bar_choose_tip, chosenCount));
        mIsSelectMode = true;
    }

    private void handleBottomDeleteShow(int chosenCount) {
        if (chosenCount == 0) {
            mBobBottomOperator.setVisibility(View.GONE);
//            mRlCommonOperateBarContainer.setVisibility(View.GONE);
        } else {
//            mRlCommonOperateBarContainer.setVisibility(View.VISIBLE);
            mBobBottomOperator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initClick() {
//        mRlCommonOperateBarContainer.setOnClickListener(this);
//        mBobBottomOperator.setOnClickListener(this);
        mBobBottomOperator.setListener(new BottomOperateBar.Listener() {
            @Override
            public ArrayList<File> getCurrentSelectedFiles() {
                List<DocChildBean> checkedDoc = getCheckedDoc();
                mChosenFiles.clear();
                for (int i = 0; i < checkedDoc.size(); i++) {
                    DocChildBean childBean = checkedDoc.get(i);
                    File file = new File(childBean.mDocPath);
                    if (!file.exists()) {
                        ArrayList<File> arrayList = new ArrayList<>();
                        arrayList.add(file);
                        mPresenter.handleFileDelete(arrayList);
                        continue;
                    }
                    mChosenFiles.add(file);
                }
                if (mChosenFiles.size() == 0) {
                    return null;
                }
                return mChosenFiles;
            }

            @Override
            public Activity getActivity() {
                return DocManagerActivity.this;
            }

            @Override
            public void afterCopy() {
                FileBrowserActivity.startBrowser(DocManagerActivity.this, "");
                handleDataCopy();
            }

            @Override
            public void afterCut() {
                FileBrowserActivity.startBrowser(DocManagerActivity.this, "");
                handleDataCut();
            }

            @Override
            public void afterRename() {
                handleRename();
            }

            @Override
            public void afterDelete() {
                handleDataDelete();
            }

            @Override
            public void statisticsClickCopy() {
                statisticsClickBottomCopy();
            }

            @Override
            public void statisticsClickCut() {
                statisticsClickBottomCut();
            }

            @Override
            public void statisticsClickDelete() {
                statisticsClickBottomDelete();
            }

            @Override
            public void statisticsClickMore() {
                statisticsClickBottomMore();
            }

            @Override
            public void statisticsClickRename() {
                statisticsClickBottomRename();
            }

            @Override
            public void statisticsClickDetail() {
                statisticsClickBottomDetail();
            }
        });
        mTvCommonActionBarWithSearchTitle.setOnClickListener(this);
        mIvCommonActionBarBack.setOnClickListener(this);
        mIvCommonActionBarWithSearchSearch.setOnClickListener(this);
        mElvApk.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                        int childPosition, long id) {
                Intent mFileIntent;
                DocChildBean child = mAppInfo.get(groupPosition).getChild(childPosition);
                if (child.mFileType == DocChildBean.TYPE_TXT) {//只针对txt提供预览
                    long fileSize = Long.parseLong(child.mDocSize);
                    if (fileSize > 1024 * 1024) {
                        statisticsClickOtherFile();
                        mFileIntent = FileUtil.getTextFileIntent(child.mDocPath, false);
                    } else {//只提供1M以下的小文件的预览
                        statisticsClickTxt();
                        mFileIntent = new Intent(DocManagerActivity.this, TxtPreviewActivity.class);
                        mFileIntent.putExtra(TxtPreviewActivity.TARGET_DOC_PATH, child.mDocPath);
                    }
                } else if (child.mFileType == DocChildBean.TYPE_DOC) {
                    statisticsClickOtherFile();
                    mFileIntent = FileUtil.getWordFileIntent(child.mDocPath);
                } else if (child.mFileType == DocChildBean.TYPE_XLS) {
                    statisticsClickOtherFile();
                    mFileIntent = FileUtil.getExcelFileIntent(child.mDocPath);
                } else if (child.mFileType == DocChildBean.TYPE_PPT) {
                    statisticsClickOtherFile();
                    mFileIntent = FileUtil.getPptFileIntent(child.mDocPath);
                } else if (child.mFileType == DocChildBean.TYPE_PDF) {
                    statisticsClickOtherFile();
                    mFileIntent = FileUtil.getPdfFileIntent(child.mDocPath);
                } else {
                    statisticsClickOtherFile();
                    mFileIntent = FileUtil.getTextFileIntent(child.mDocPath, false);
                }
                startActivity(mFileIntent);
                return false;
            }
        });
    }

    @Override
    public void initList() {

    }

    @Override
    public void refreshList(boolean keepUserCheck, boolean shouldScanAgain) {
        mPresenter.getDocInfo(keepUserCheck, shouldScanAgain);//新获取到的数据
    }

    @Override
    public void initBroadcastReceiver() {

    }

    @Override
    public void releaseBroadcastReceiver() {
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void updateDeleteProgress(int done, int total) {
        if (done == total) {
            //说明删完了
//            Toast.makeText(DocManagerActivity.this, total + "delete has done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void refreshTile() {
        mTvCommonActionBarWithSearchTitle.setText(R.string.doc_manager_title);
        mIvCommonActionBarBack.setImageResource(R.drawable.action_bar_back_drawable_selector_blue);
        mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.ic_action_bar_search);
        mIsSelectMode = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        releaseBroadcastReceiver();
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
        if (mIsSelectMode){
            refreshTile();
            unSelectAllItem();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        if (mQuickClickGuard.isQuickClick(view.getId())){
            return;
        }
        switch (view.getId()){
            case R.id.tv_common_action_bar_title:
                if (!mIsSelectMode) {
                    finishActivity();
                }
                break;
            case R.id.iv_common_action_bar_back:
                if (mIsSelectMode) {
                    refreshTile();
                    unSelectAllItem();
                } else {
                    finishActivity();
                }
                break;
            case R.id.iv_common_action_bar_search:
                if (mIsSelectMode) {
                    handleTitleSelect(mIsAllSelect);
                } else {
                    statisticsClickSearch();
                    SearchActivity.showSearchResult(this, Const.CategoryType.CATEGORY_TYPE_DOC);
                }
                break;
            default:
                break;
        }
    }

    private void unSelectAllItem() {
        for (DocGroupBean groupBean : mAppInfo) {
            if (groupBean.getchildrenSize() == 0) {
                continue;
            }
            groupBean.mSelectState = GroupSelectBox.SelectState.NONE_SELECTED;
            List<DocChildBean> children = groupBean.getChildren();
            for (DocChildBean childBean : children) {
                childBean.mIsChecked = false;
            }
        }
        mAdapter.handleCheckedCount();
        mAdapter.notifyDataSetChanged();
    }

    protected void selectAllItem() {
        for (DocGroupBean groupBean : mAppInfo) {
            if (groupBean.getchildrenSize() == 0) {
                continue;
            }
            groupBean.mSelectState = GroupSelectBox.SelectState.ALL_SELECTED;
            List<DocChildBean> children = groupBean.getChildren();
            for (DocChildBean childBean : children) {
                childBean.mIsChecked = true;
            }
        }
        mAdapter.handleCheckedCount();
        mAdapter.notifyDataSetChanged();
    }

    private void handleTitleSelect(boolean isAllSelect) {
        if (isAllSelect) {
            unSelectAllItem();
        } else {
            selectAllItem();
        }
        mIsAllSelect = !isAllSelect;
    }

    private List<DocChildBean> getCheckedDoc() {
        List<DocChildBean> mResultPackage = new ArrayList<>();
        for (DocGroupBean groupBean : mAppInfo) {
            List<DocChildBean> children = groupBean.getChildren();
            if (children == null || children.isEmpty()) {
                continue;
            }
            for (DocChildBean childBean : children) {
                if (childBean.mIsChecked) {
                    mResultPackage.add(childBean);
                }
            }
        }
        return mResultPackage;
    }

    //处理选中的数据
    private void handleDataCopy() {
        refreshTile();
        //隐藏底部栏
        mBobBottomOperator.setVisibility(View.GONE);
    }

    private void handleDataDelete() {
        mPresenter.handleFileDelete(mChosenFiles);//处理数据库的删除
        mPresenter.refreshData(false, false);
        refreshTile();
        //隐藏底部栏
        mBobBottomOperator.setVisibility(View.GONE);
    }

    private void handleDataCut() {
        refreshTile();
        //隐藏底部栏
        mBobBottomOperator.setVisibility(View.GONE);
    }

    private void handleRename() {
        refreshTile();
        //隐藏底部栏
        mBobBottomOperator.setVisibility(View.GONE);
    }

    //====================统计代码  Start =====================
    private void statisticsClickOtherFile() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_OTHER_FILE;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击其他文件---" + bean.mOperateId);
    }

    private void statisticsClickBottomCopy() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_COPY;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击复制---" + bean.mOperateId);
    }

    private void statisticsClickBottomCut() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_CUT;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击剪切---" + bean.mOperateId);
    }

    private void statisticsClickBottomDelete() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_DELETE;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击删除---" + bean.mOperateId);
    }

    private void statisticsClickBottomRename() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_RENAME;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击重命名---" + bean.mOperateId);
    }

    private void statisticsClickBottomDetail() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_DETAIL;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击详情---" + bean.mOperateId);
    }

    private void statisticsClickBottomMore() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_MORE;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击more---" + bean.mOperateId);
    }

    private void statisticsClickTxt() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_TXT;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击小的txt---" + bean.mOperateId);
    }

    private void statisticsClickSearch() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.DOC_CLICK_SEARCH_BUTTON;
        StatisticsTools.upload101InfoNew(bean);
        Logger.d(StatisticsConstants.LOGGER_SHOW, "doc 点击搜索---" + bean.mOperateId);
    }
    //====================统计代码  end =====================
}

package com.jb.filemanager.function.docmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.ui.dialog.DocRenameDialog;
import com.jb.filemanager.ui.dialog.FileDeleteConfirmDialog;
import com.jb.filemanager.ui.dialog.MultiFileDetailDialog;
import com.jb.filemanager.ui.dialog.SingleFileDetailDialog;
import com.jb.filemanager.ui.widget.FloatingGroupExpandableListView;
import com.jb.filemanager.ui.widget.WrapperExpandableListAdapter;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocManagerActivity extends BaseActivity implements DocManagerContract.View, View.OnClickListener {
    //    public static final int UNINSTALL_APP_REQUEST_CODE = 101;
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
    private RelativeLayout mRlCommonOperateBarContainer;
    private LinearLayout mLlOperateBar;
    private TextView mTvCommonOperateBarCut;
    private TextView mTvCommonOperateBarCopy;
    private TextView mTvCommonOperateBarDelete;
    private TextView mTvCommonOperateBarMore;
    private LinearLayout mLlMoreOperateContainer;
    private TextView mTvBottomDetail;
    private TextView mTvBottomOpen;
    private TextView mTvBottomFileRename;

    private DocManagerAdapter mAdapter;
    private int mChosenCount;
    private List<DocGroupBean> mAppInfo;
    private boolean mIsMoreOperatorShown;
    private BroadcastReceiver mScanSdReceiver;
    private SingleFileDetailDialog mSingleFileDetailDialog;
    private MultiFileDetailDialog mMultiFileDetailDialog;
    private boolean mIsSelectMode;
    private boolean mIsAllSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_manager);

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
        mRlCommonOperateBarContainer = (RelativeLayout) findViewById(R.id.rl_common_operate_bar_container);
        mLlOperateBar = (LinearLayout) findViewById(R.id.ll_operate_bar);
        mTvCommonOperateBarCut = (TextView) findViewById(R.id.tv_common_operate_bar_cut);
        mTvCommonOperateBarCopy = (TextView) findViewById(R.id.tv_common_operate_bar_copy);
        mTvCommonOperateBarDelete = (TextView) findViewById(R.id.tv_common_operate_bar_delete);
        mTvCommonOperateBarMore = (TextView) findViewById(R.id.tv_common_operate_bar_more);
        mLlMoreOperateContainer = (LinearLayout) findViewById(R.id.ll_more_operate_container);
        mTvBottomDetail = (TextView) findViewById(R.id.tv_bottom_detail);
        mTvBottomOpen = (TextView) findViewById(R.id.tv_bottom_open);
        mTvBottomFileRename = (TextView) findViewById(R.id.tv_bottom_rename);
    }

    @Override
    public void initData() {
        mAppInfo = mPresenter.getDocInfo();
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
            mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.select_all);
        } else {
            mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.title_select_all);
        }
        mTvCommonActionBarWithSearchTitle.setText(getString(R.string.doc_manager_title_select_mode, chosenCount));
        mIsSelectMode = true;
    }

    private void handleBottomDeleteShow(int chosenCount) {
        mChosenCount = chosenCount;
        hideMoreOperator();
        if (chosenCount == 0) {
            mRlCommonOperateBarContainer.setVisibility(View.GONE);
        } else {
            mRlCommonOperateBarContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initClick() {
        mRlCommonOperateBarContainer.setOnClickListener(this);
        mTvCommonActionBarWithSearchTitle.setOnClickListener(this);
        mIvCommonActionBarBack.setOnClickListener(this);
        mIvCommonActionBarWithSearchSearch.setOnClickListener(this);
        mElvApk.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition,
                                        int childPosition, long id) {
                Intent mFileIntent;
                DocChildBean child = mAppInfo.get(groupPosition).getChild(childPosition);
                if (groupPosition == 0) {//只针对txt提供预览
                    mFileIntent = FileUtil.getTextFileIntent(child.mDocPath, false);
                } else if (groupPosition == 1) {
                    mFileIntent = FileUtil.getWordFileIntent(child.mDocPath);
                } else {
                    mFileIntent = FileUtil.getPdfFileIntent(child.mDocPath);
                }
                startActivity(mFileIntent);
                return false;
            }
        });

        mTvCommonOperateBarCopy.setOnClickListener(this);
        mTvCommonOperateBarCut.setOnClickListener(this);
        mTvCommonOperateBarDelete.setOnClickListener(this);
        mTvCommonOperateBarMore.setOnClickListener(this);
        mTvBottomDetail.setOnClickListener(this);
        mTvBottomOpen.setOnClickListener(this);
        mTvBottomFileRename.setOnClickListener(this);
    }

    @Override
    public void initList() {

    }

    @Override
    public void refreshList() {
        mAdapter.setListData(mPresenter.getDocInfo());
    }

    @Override
    public void initBroadcastReceiver() {
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentfilter.addDataScheme("file");
        mScanSdReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(intent.getAction())) {
                    mPresenter.scanStart();
                } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(intent.getAction())) {
                    mPresenter.scanFinished();
                }
            }
        };
        registerReceiver(mScanSdReceiver, intentfilter);

        //扫描sd的广播在19以后只有系统才能发出   之后只能扫描制定的文件或者文件夹
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(Environment.getExternalStorageDirectory());
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }
    }

    @Override
    public void releaseBroadcastReceiver() {
        unregisterReceiver(mScanSdReceiver);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showDocDetail(List<DocChildBean> docList) {
        Toast.makeText(DocManagerActivity.this, docList.size() + "个 文件的detail", Toast.LENGTH_SHORT).show();
        if (docList.size() == 1) {//选中的是单个文件
            DocChildBean childBean = docList.get(0);
            File file = new File(childBean.mDocPath);
            mSingleFileDetailDialog = new SingleFileDetailDialog(this, file, new SingleFileDetailDialog.Listener() {
                @Override
                public void onConfirm(SingleFileDetailDialog dialog) {
                    mSingleFileDetailDialog.dismiss();//确认关闭弹窗
                    // TODO: 2017/7/6 add by --miwo 是否要取消选择?
                }
            });
            mSingleFileDetailDialog.show();
        } else {//多个文件
            ArrayList<File> fileList = new ArrayList<>();
            for (DocChildBean childBean : docList) {
                fileList.add(new File(childBean.mDocPath));
            }
            mMultiFileDetailDialog = new MultiFileDetailDialog(this, fileList, new MultiFileDetailDialog.Listener() {
                @Override
                public void onConfirm(MultiFileDetailDialog dialog) {
                    mMultiFileDetailDialog.dismiss();
                    // TODO: 2017/7/6 add by --miwo 是否要处理取消选择?
                }
            });
            mMultiFileDetailDialog.show();
        }
    }

    @Override
    public void fileRename(List<DocChildBean> docList) {
        Toast.makeText(DocManagerActivity.this, docList.get(0).mDocName + " will be rename", Toast.LENGTH_SHORT).show();
        final File file = new File(docList.get(0).mDocPath);
        if (file.exists()) {
            DocRenameDialog docRenameDialog = new DocRenameDialog(this, file.isDirectory(), new DocRenameDialog.Listener() {
                @SuppressWarnings("ResultOfMethodCallIgnored")
                @Override
                public void onConfirm(DocRenameDialog dialog, String newName) {
                    if (file.exists()) {
                        file.renameTo(new File(file.getParentFile().getAbsolutePath() + File.separator + newName));
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancel(DocRenameDialog dialog) {
                    dialog.dismiss();
                }
            });
            docRenameDialog.show();
        }
    }

    @Override
    public void openWith(List<DocChildBean> docList) {
        DocChildBean childBean = docList.get(0);
        Toast.makeText(DocManagerActivity.this, childBean.mDocName + "will open", Toast.LENGTH_SHORT).show();
        Intent fileIntent;
        //打开文件
        switch (childBean.mFileType) {
            case DocChildBean.TYPE_DOC:
                fileIntent = FileUtil.getWordFileIntent(childBean.mDocPath);
                break;
            case DocChildBean.TYPE_TXT:
                fileIntent = FileUtil.getTextFileIntent(childBean.mDocPath, false);
                break;
            case DocChildBean.TYPE_PDF:
                fileIntent = FileUtil.getPdfFileIntent(childBean.mDocPath);
                break;
            default:
                fileIntent = FileUtil.getTextFileIntent(childBean.mDocPath, false);
                break;
        }
        startActivity(fileIntent);
    }

    @Override
    public void updateDeleteProgress(int done, int total) {
        if (done == total) {
            //说明删完了
            Toast.makeText(DocManagerActivity.this, total + "delete has done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void refreshTile() {
        mTvCommonActionBarWithSearchTitle.setText(R.string.app_name);
        mIvCommonActionBarBack.setImageResource(R.drawable.action_bar_back_drawable_selector_blue);
        mIvCommonActionBarWithSearchSearch.setImageResource(R.drawable.search_icon);
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
        super.onBackPressed();
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
            case R.id.rl_common_operate_bar_container:
                Toast.makeText(DocManagerActivity.this, "我是占位的bottom啦", Toast.LENGTH_SHORT).show();
                break;
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
                    startActivity(new Intent(this, SearchActivity.class));
                }
//                handleSearchButtonClick(mIsSearchInput);
                break;
            case R.id.tv_common_operate_bar_copy:
                handleDataCopy();
                break;
            case R.id.tv_common_operate_bar_cut:
                handleDataCut();
                break;
            case R.id.tv_common_operate_bar_delete:
                handleDataDelete();
                break;
            case R.id.tv_common_operate_bar_more:
//                Toast.makeText(DocManagerActivity.this, "more", Toast.LENGTH_SHORT).show();
                if (mIsMoreOperatorShown) {
                    hideMoreOperator();
                } else {
                    showMoreOperator(mChosenCount);
                }
                break;
            case R.id.tv_bottom_detail:
                showDocDetail(getCheckedDoc());
                hideMoreOperator();
                break;
            case R.id.tv_bottom_rename:
                fileRename(getCheckedDoc());
                hideMoreOperator();
                break;
            case R.id.tv_bottom_open:
                openWith(getCheckedDoc());
                hideMoreOperator();
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

    //显示more的内容
    private void showMoreOperator(int chosenCount) {
        mIsMoreOperatorShown = true;
        if (chosenCount == 1) {
            mLlMoreOperateContainer.setVisibility(View.VISIBLE);
            mTvBottomOpen.setVisibility(View.VISIBLE);
            mTvBottomFileRename.setVisibility(View.VISIBLE);
        } else {
            mLlMoreOperateContainer.setVisibility(View.VISIBLE);
            mTvBottomOpen.setVisibility(View.GONE);
            mTvBottomFileRename.setVisibility(View.GONE);
        }
    }

    //隐藏more的内容
    private void hideMoreOperator() {
        mIsMoreOperatorShown = false;
        mLlMoreOperateContainer.setVisibility(View.GONE);
    }

    //处理选中的数据
    private void handleDataCopy() {
        List<DocChildBean> checkedDoc = getCheckedDoc();
        Toast.makeText(DocManagerActivity.this, checkedDoc.size() + "will copy", Toast.LENGTH_SHORT).show();
        //判断空间是否充足
        long totalSize = 0;
        for (DocChildBean childBean : checkedDoc) {
            totalSize += Long.parseLong(childBean.mDocSize);
        }
        if (!StorageUtil.isSDCardAvailable()) {
            Toast.makeText(DocManagerActivity.this, "sd card is not available plz try again later", Toast.LENGTH_SHORT).show();
        }
        long freeSize = StorageUtil.getSDCardInfo(this.getApplicationContext()).mFree;
        if (freeSize < totalSize) {
            Toast.makeText(DocManagerActivity.this, "there is not enough space, plz try again later", Toast.LENGTH_SHORT).show();
        }
        // TODO: 2017/7/4 add by --miwo 传递复制的参数
    }

    private void handleDataDelete() {
        final List<DocChildBean> checkedDoc = getCheckedDoc();
        Logger.d(TAG, "之前选中的数量" + checkedDoc.size());
        Toast.makeText(DocManagerActivity.this, checkedDoc.size() + "will delete", Toast.LENGTH_SHORT).show();
        // TODO: 2017/7/4 add by --miwo 此处应有删除的逻辑
        ArrayList<File> fileList = new ArrayList<>();
        for (DocChildBean childBean : checkedDoc) {
            fileList.add(new File(childBean.mDocPath));
        }
        FileDeleteConfirmDialog confirmDialog = new FileDeleteConfirmDialog(this, fileList);
        confirmDialog.setOnDialogClickListener(new FileDeleteConfirmDialog.OnClickListener() {
            @Override
            public void clickConfirm() {
                //刷新页面数据
                Iterator<DocGroupBean> groupIterator = mAppInfo.iterator();
                while (groupIterator.hasNext()) {
                    DocGroupBean group = groupIterator.next();
                    /*if (group.mSelectState == GroupSelectBox.SelectState.ALL_SELECTED) {//如果全选  那么就全部删除
                        groupIterator.remove();
                        continue;
                    }*/
                    Iterator<DocChildBean> childIterator = group.getChildren().iterator();
                    while (childIterator.hasNext()) {
                        DocChildBean child = childIterator.next();
                        if (child.mIsChecked) {//删除选中的文件
                            childIterator.remove();
                        }
                    }
                }
                mPresenter.handleFileDelete(checkedDoc);
                //隐藏底部栏
                mRlCommonOperateBarContainer.setVisibility(View.GONE);
                mPresenter.refreshData();
                Logger.d(TAG, "剩余选中的数量" + getCheckedDoc().size());
            }

            @Override
            public void clickCancel() {

            }
        });
        confirmDialog.show();
    }

    private void handleDataCut() {
        List<DocChildBean> checkedDoc = getCheckedDoc();
        Toast.makeText(DocManagerActivity.this, checkedDoc.size() + "will cut", Toast.LENGTH_SHORT).show();
        // TODO: 2017/7/4 add by --miwo 此处应有剪切的逻辑
    }
}

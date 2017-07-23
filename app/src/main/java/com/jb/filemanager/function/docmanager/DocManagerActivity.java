package com.jb.filemanager.function.docmanager;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.function.txtpreview.TxtPreviewActivity;
import com.jb.filemanager.home.MainActivity;
import com.jb.filemanager.ui.dialog.DocRenameDialog;
import com.jb.filemanager.ui.dialog.MultiFileDetailDialog;
import com.jb.filemanager.ui.dialog.SingleFileDetailDialog;
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
    private BroadcastReceiver mScanSdReceiver;
    private SingleFileDetailDialog mSingleFileDetailDialog;
    private MultiFileDetailDialog mMultiFileDetailDialog;
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
    }

    @Override
    public void initData() {
        mPresenter.setDocScanListener(new DocScanListener() {
            @Override
            public void onScanStart() {
                Toast.makeText(DocManagerActivity.this, "加载开始了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanFinish(final ArrayList<DocGroupBean> arrayList, final boolean keepCheck) {
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DocManagerActivity.this, "加载完成了", Toast.LENGTH_SHORT).show();
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
        mPresenter.getDocInfo(false);
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
                    mChosenFiles.add(new File(childBean.mDocPath));
                }
                return mChosenFiles;
            }

            @Override
            public Activity getActivity() {
                return DocManagerActivity.this;
            }

            @Override
            public void afterCopy() {
                startActivity(new Intent(DocManagerActivity.this, MainActivity.class));
                handleDataCopy();
            }

            @Override
            public void afterCut() {
                startActivity(new Intent(DocManagerActivity.this, MainActivity.class));
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
                        mFileIntent = FileUtil.getTextFileIntent(child.mDocPath, false);
                    } else {//只提供1M以下的小文件的预览
                        mFileIntent = new Intent(DocManagerActivity.this, TxtPreviewActivity.class);
                        mFileIntent.putExtra(TxtPreviewActivity.TARGET_DOC_PATH, child.mDocPath);
                    }
                } else if (child.mFileType == DocChildBean.TYPE_DOC) {
                    mFileIntent = FileUtil.getWordFileIntent(child.mDocPath);
                } else {
                    mFileIntent = FileUtil.getPdfFileIntent(child.mDocPath);
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
    public void refreshList(boolean keepUserCheck) {
        mPresenter.getDocInfo(keepUserCheck);//新获取到的数据
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
            Logger.d(TAG,contentUri.toString());
            scanIntent.setData(contentUri);
            sendBroadcast(scanIntent);

            /*String[] paths = {Environment.getExternalStorageDirectory().getAbsolutePath()};
            String[] mimeTypes = {DocManagerSupport.DOC_MIME_TYPE,DocManagerSupport.DOCX_MIME_TYPE,DocManagerSupport.XLS_MIME_TYPE,DocManagerSupport.XLSX_MIME_TYPE,
                    DocManagerSupport.PPT_MIME_TYPE,DocManagerSupport.PPTX_MIME_TYPE,DocManagerSupport.TXT_MIME_TYPE,DocManagerSupport.PDF_MIME_TYPE};
            MediaScannerConnection.scanFile(TheApplication.getAppContext(), paths, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    mPresenter.scanStart();
                }

                @Override
                public void onScanCompleted(String s, Uri uri) {
                    mPresenter.scanFinished();
                }
            });*/
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

//    @Override
//    public void openWith(List<DocChildBean> docList) {
//        DocChildBean childBean = docList.get(0);
//        Toast.makeText(DocManagerActivity.this, childBean.mDocName + "will open", Toast.LENGTH_SHORT).show();
//        Intent fileIntent;
//        //打开文件
//        switch (childBean.mFileType) {
//            case DocChildBean.TYPE_DOC:
//                fileIntent = FileUtil.getWordFileIntent(childBean.mDocPath);
//                break;
//            case DocChildBean.TYPE_TXT:
//                fileIntent = FileUtil.getTextFileIntent(childBean.mDocPath, false);
//                break;
//            case DocChildBean.TYPE_PDF:
//                fileIntent = FileUtil.getPdfFileIntent(childBean.mDocPath);
//                break;
//            default:
//                fileIntent = FileUtil.getTextFileIntent(childBean.mDocPath, false);
//                break;
//        }
//        startActivity(fileIntent);
//    }

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
        mPresenter.refreshData(false);
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
}

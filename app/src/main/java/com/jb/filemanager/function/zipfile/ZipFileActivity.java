package com.jb.filemanager.function.zipfile;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.commomview.ProgressWheel;
import com.jb.filemanager.function.zipfile.adapter.ZipListAdapter;
import com.jb.filemanager.function.zipfile.bean.ZipFileGroupBean;
import com.jb.filemanager.function.zipfile.bean.ZipFileItemBean;
import com.jb.filemanager.function.zipfile.dialog.ZipFileOperationDialog;
import com.jb.filemanager.function.zipfile.presenter.ZipActivityContract;
import com.jb.filemanager.function.zipfile.presenter.ZipFileActivityPresenter;

import java.util.List;

/**
 * Created by xiaoyu on 2017/6/29 17:01.
 */

public class ZipFileActivity extends BaseActivity implements ZipActivityContract.View, View.OnClickListener {

    private ZipFileActivityPresenter mPresenter = new ZipFileActivityPresenter(this);
    private ProgressWheel mProgress;
    private ExpandableListView mListView;
    private ZipListAdapter mAdapter;
    private ZipFileOperationDialog mOperationDialog;
    private RelativeLayout mRlCommonOperateBarContainer;
    private LinearLayout mLlOperateBar;
    private TextView mTvCommonOperateBarCut;
    private TextView mTvCommonOperateBarCopy;
    private TextView mTvCommonOperateBarDelete;
    private TextView mTvCommonOperateBarMore;
    private LinearLayout mLlMoreOperateContainer;
    private TextView mTvBottomDetail;
    private TextView mTvBottomOpen;
    private TextView mTvBottomShowInFolder;
    private boolean mIsMoreOperatorShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_file);

        mProgress = (ProgressWheel) findViewById(R.id.zip_progress);
        mListView = (ExpandableListView) findViewById(R.id.zip_expand_lv);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if (mPresenter != null) {
                    mPresenter.onItemClick(groupPosition, childPosition);
                }
                return true;
            }
        });
        mRlCommonOperateBarContainer = (RelativeLayout) findViewById(R.id.rl_common_operate_bar_container);
        mLlOperateBar = (LinearLayout) findViewById(R.id.ll_operate_bar);
        mTvCommonOperateBarCut = (TextView) findViewById(R.id.tv_common_operate_bar_cut);
        mTvCommonOperateBarCopy = (TextView) findViewById(R.id.tv_common_operate_bar_copy);
        mTvCommonOperateBarDelete = (TextView) findViewById(R.id.tv_common_operate_bar_delete);
        mTvCommonOperateBarMore = (TextView) findViewById(R.id.tv_common_operate_bar_more);
        mLlMoreOperateContainer = (LinearLayout) findViewById(R.id.ll_more_operate_container);
        mTvBottomDetail = (TextView) findViewById(R.id.tv_bottom_detail);
        mTvBottomOpen = (TextView) findViewById(R.id.tv_bottom_open);
        mTvBottomShowInFolder = (TextView) findViewById(R.id.tv_bottom_rename);

        mTvCommonOperateBarCut.setOnClickListener(this);
        mTvCommonOperateBarCopy.setOnClickListener(this);
        mTvCommonOperateBarDelete.setOnClickListener(this);
        mTvCommonOperateBarMore.setOnClickListener(this);
        mTvBottomDetail.setOnClickListener(this);
        mTvBottomOpen.setOnClickListener(this);
        mTvBottomShowInFolder.setOnClickListener(this);

        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setWidgetsState(boolean isLoading) {
        mProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setListData(List<ZipFileGroupBean> data) {
        mAdapter = new ZipListAdapter(data);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showOperationDialog(ZipFileItemBean fileItem) {
        if (mOperationDialog != null && mOperationDialog.isShowing()) {
            mOperationDialog.dismiss();
            mOperationDialog = null;
        }
        mOperationDialog = new ZipFileOperationDialog(this, fileItem);
        mOperationDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (ExtractManager.getInstance().isProgressDialogAttached()) {
            ExtractManager.getInstance().hideProgressDialogFromWindow();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_common_operate_bar_container:
                break;
            case R.id.iv_common_action_bar_with_search_search:
                break;
            case R.id.tv_common_operate_bar_copy:
//                handleDataCopy();
                break;
            case R.id.tv_common_operate_bar_cut:
//                handleDataCut();
                break;
            case R.id.tv_common_operate_bar_delete:
//                handleDataDelete();
                break;
            case R.id.tv_common_operate_bar_more:
//                Toast.makeText(DocManagerActivity.this, "more", Toast.LENGTH_SHORT).show();
                if (mIsMoreOperatorShown) {
                    hideMoreOperator();
                } else {
                    showMoreOperator(1);
                }
                break;
            case R.id.tv_bottom_detail:
//                showDocDetail(getCheckedDoc());
                hideMoreOperator();
                break;
            case R.id.tv_bottom_rename:
//                showInFolder(getCheckedDoc());
                hideMoreOperator();
                break;
            case R.id.tv_bottom_open:
//                openWith(getCheckedDoc());
                hideMoreOperator();
                break;
            default:
                break;
        }
    }

    //显示more的内容
    private void showMoreOperator(int chosenCount) {
        mIsMoreOperatorShown = true;
        if (chosenCount == 1) {
            mLlMoreOperateContainer.setVisibility(View.VISIBLE);
            mTvBottomOpen.setVisibility(View.VISIBLE);
            mTvBottomShowInFolder.setVisibility(View.VISIBLE);
        } else {
            mLlMoreOperateContainer.setVisibility(View.VISIBLE);
            mTvBottomOpen.setVisibility(View.GONE);
            mTvBottomShowInFolder.setVisibility(View.GONE);
        }
    }

    //隐藏more的内容
    private void hideMoreOperator() {
        mIsMoreOperatorShown = false;
        mLlMoreOperateContainer.setVisibility(View.GONE);
    }

}

package com.jb.filemanager.function.zipfile;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

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

public class ZipFileActivity extends BaseActivity implements ZipActivityContract.View {

    private ZipFileActivityPresenter mPresenter = new ZipFileActivityPresenter(this);
    private ProgressWheel mProgress;
    private ExpandableListView mListView;
    private ZipListAdapter mAdapter;
    private ZipFileOperationDialog mOperationDialog;

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
}

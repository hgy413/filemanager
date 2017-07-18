package com.jb.filemanager.function.recent;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.recent.adapter.RecentFileAdapter;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.presenter.RecentFileContract;
import com.jb.filemanager.function.recent.presenter.RecentFilePresenter;
import com.jb.filemanager.ui.view.SearchTitleView;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:14.
 */

public class RecentFileActivity extends BaseActivity implements RecentFileContract.View {

    private RecentFilePresenter mPresenter = new RecentFilePresenter(this);
    private ListView mListView;
    private RecentFileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_file);

        initViews();
        mPresenter.onCreate();
    }

    private void initViews() {
        SearchTitleView searchTitleView = (SearchTitleView) findViewById(R.id.search_title);
        searchTitleView.setSearchIconVisibility(false);
        searchTitleView.setTitleName(getString(R.string.recent));
        searchTitleView.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView = (ListView) findViewById(R.id.recent_expand_lv);
    }

    @Override
    public void setListViewData(List<BlockBean> data) {
        if (mAdapter == null) {
            mAdapter = new RecentFileAdapter(data);
        }
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void notifyListDataChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}

package com.jb.filemanager.function.recent;

import android.os.Bundle;
import android.widget.ListView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.R;
import com.jb.filemanager.function.recent.adapter.RecentFileAdapter;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentItemCheckChangedListener;
import com.jb.filemanager.function.recent.presenter.RecentFileContract;
import com.jb.filemanager.function.recent.presenter.RecentFilePresenter;
import com.jb.filemanager.ui.view.SearchTitleView;
import com.jb.filemanager.ui.view.SearchTitleViewCallback;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:14.
 */

public class RecentFileActivity extends BaseActivity implements RecentFileContract.View {

    private RecentFilePresenter mPresenter = new RecentFilePresenter(this);
    private ListView mListView;
    private RecentFileAdapter mAdapter;
    private SearchTitleView mSearchTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_file);

        initViews();
        mPresenter.onCreate();
    }

    private void initViews() {
        mSearchTitle = (SearchTitleView) findViewById(R.id.search_title);
        mSearchTitle.setSearchIconVisibility(false);
        mSearchTitle.setTitleName(getString(R.string.recent));
        mSearchTitle.setClickCallBack(new SearchTitleViewCallback() {
            @Override
            public void onIvBackClick() {
                finish();
            }

            @Override
            public void onIvCancelSelectClick() {
                mPresenter.onTitleCancelBtnClick();
            }

            @Override
            public void onSelectBtnClick() {
                mPresenter.onTitleSelectBtnClick();
            }
        });
        mListView = (ListView) findViewById(R.id.recent_expand_lv);
    }

    @Override
    public void setListViewData(List<BlockBean> data) {
        if (mAdapter == null) {
            mAdapter = new RecentFileAdapter(this, data);
            mAdapter.setCheckChangedListener(new RecentItemCheckChangedListener() {
                @Override
                public void onItemCheckChanged() {
                    mPresenter.onItemCheckChanged();
                }
            });
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
    public void switchSelectMode(boolean isToSelectMode) {
        mSearchTitle.switchTitleMode(isToSelectMode);
    }

    @Override
    public void setSearchTitleSelectBtnState(int state) {
        mSearchTitle.setSelectBtnResId(state);
    }

    @Override
    public void setSearchTitleSelectCount(int count) {
        mSearchTitle.setSelectedCount(count);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}

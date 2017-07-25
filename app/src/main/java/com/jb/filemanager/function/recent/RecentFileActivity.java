package com.jb.filemanager.function.recent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.jb.filemanager.BaseActivity;
import com.jb.filemanager.Const;
import com.jb.filemanager.R;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.recent.adapter.RecentFileAdapter;
import com.jb.filemanager.function.recent.bean.BlockBean;
import com.jb.filemanager.function.recent.listener.RecentItemCheckChangedListener;
import com.jb.filemanager.function.recent.presenter.RecentFileContract;
import com.jb.filemanager.function.recent.presenter.RecentFilePresenter;
import com.jb.filemanager.function.search.view.SearchActivity;
import com.jb.filemanager.ui.view.SearchTitleView;
import com.jb.filemanager.ui.view.SearchTitleViewCallback;
import com.jb.filemanager.ui.widget.BottomOperateBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/17 14:14.
 */

public class RecentFileActivity extends BaseActivity implements RecentFileContract.View {

    private RecentFilePresenter mPresenter = new RecentFilePresenter(this);
    private ListView mListView;
    private RecentFileAdapter mAdapter;
    private SearchTitleView mSearchTitle;
    private BottomOperateBar mOperateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_file);

        initViews();
        mPresenter.onCreate();
    }

    private void initViews() {
        mSearchTitle = (SearchTitleView) findViewById(R.id.search_title);
        mSearchTitle.setSearchIconVisibility(true);
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

            @Override
            public void onSearchClick() {
                SearchActivity.showSearchResult(getApplicationContext(), Const.CategoryType.CATEGORY_TYPE_RECENT);
            }
        });
        mListView = (ListView) findViewById(R.id.recent_expand_lv);
        mOperateBar = (BottomOperateBar) findViewById(R.id.bob_bottom_operator);
        mOperateBar.setListener(new BottomOperateBar.Listener() {
            @Override
            public ArrayList<File> getCurrentSelectedFiles() {
                return mPresenter.getCurrentSelectFile();
            }

            @Override
            public Activity getActivity() {
                return RecentFileActivity.this;
            }

            @Override
            public void afterCopy() {
                FileBrowserActivity.startBrowser(RecentFileActivity.this, "");
                mPresenter.afterCopy();
            }

            @Override
            public void afterCut() {
                FileBrowserActivity.startBrowser(RecentFileActivity.this, "");
                mPresenter.afterCut();
            }

            @Override
            public void afterRename() {
                mPresenter.afterRename();
            }

            @Override
            public void afterDelete() {
                mPresenter.afterDelete();
            }
        });
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
        mOperateBar.setVisibility(isToSelectMode ? View.VISIBLE : View.GONE);
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
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mSearchTitle.isSelectMode()) {
            mPresenter.onTitleCancelBtnClick();
        } else {
            super.onBackPressed();
        }
    }
}

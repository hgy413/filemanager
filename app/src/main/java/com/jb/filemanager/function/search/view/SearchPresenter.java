package com.jb.filemanager.function.search.view;

import android.content.Intent;
import android.text.TextUtils;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.search.SearchManager;
import com.jb.filemanager.function.search.event.SearchFinishEvent;
import com.jb.filemanager.function.search.modle.FileInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/20.
 *
 */

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;
    private SearchContract.Support mSupport;

    private int mCategoryType;
    private String mKeyword;
    private boolean mAnimPlayOnce;
    private boolean mSearchFinished;
    private ArrayList<FileInfo> mSearchResult;

    /**
     * 搜索监听结果
     * */
    private IOnEventMainThreadSubscriber<SearchFinishEvent> mSearchFinishMainThreadSubscriber = new IOnEventMainThreadSubscriber<SearchFinishEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SearchFinishEvent event) {
            if (mView != null) {
                if (mAnimPlayOnce) {
                    mView.stopSearchAnim();
                    mView.showSearchResult(event.mFileInfoList);
                } else {
                    mSearchResult = event.mFileInfoList;
                    mSearchFinished = true;
                }
            }
        }
    };

    SearchPresenter(SearchContract.View view, SearchContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {
        if (intent != null) {
            mCategoryType = intent.getIntExtra(SearchActivity.PARAM_CATEGORY_TYPE, Const.CategoryType.CATEGORY_TYPE_ALL);
        } else {
            mCategoryType = Const.CategoryType.CATEGORY_TYPE_ALL;
        }

        if (mView != null) {
            mView.showKeyboard();
        }
    }

    @Override
    public void onResume() {
        mAnimPlayOnce = false;
        // TODO research

        doSearch(mKeyword);
    }

    @Override
    public void onPause() {
        if (mView != null) {
            mView.stopSearchAnim();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            mView.finishActivity();
        }
    }

    @Override
    public void onPressHomeKey() {

    }


    @Override
    public void onClickMask() {
        if (mView != null) {
            mView.finishActivity();
        }
    }

    @Override
    public void onClickClearInputButton() {
        if (mView != null) {
            mView.clearInput();
        }
    }

    @Override
    public void onClickSearch(String keyword) {
        doSearch(keyword);
    }

    @Override
    public void onClickSearchOnKeyboard(String keyword) {
        doSearch(keyword);
    }



    // private
    private void doSearch(String keyword) {
        mKeyword = keyword;
        if (TextUtils.isEmpty(mKeyword)) {
            if (mView != null) {
                mView.showInputEmptyTips();
                return;
            }
        }

        if (mView != null) {
            mView.hideKeyboard();
            mView.showSearchAnim();
        }

        if (!TheApplication.getGlobalEventBus().isRegistered(mSearchFinishMainThreadSubscriber)) {
            TheApplication.getGlobalEventBus().register(mSearchFinishMainThreadSubscriber);
        }
        SearchManager.getInstance().requestSearch(mKeyword);
    }

    @Override
    public void onAnimRepeat() {
        mAnimPlayOnce = true;
        if (mSearchFinished && mView != null) {
            mView.stopSearchAnim();
            mView.showSearchResult(mSearchResult);
        }
    }
}

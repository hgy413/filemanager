package com.jb.filemanager.function.search.view;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.jb.filemanager.Const;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.filebrowser.FileBrowserActivity;
import com.jb.filemanager.function.search.SearchManager;
import com.jb.filemanager.function.search.event.SearchFinishEvent;
import com.jb.filemanager.function.search.modle.FileInfo;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.FileUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
                    mView.showSearchResult(mKeyword, event.mFileInfoList);

                    statisticsShowSearchResult();
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
        if (!TextUtils.isEmpty(mKeyword)) {
            doSearch(mKeyword);
        }
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

        statisticsSearchExit(systemBack ? "1" : "2");
    }

    @Override
    public void onPressHomeKey() {

    }


    @Override
    public void onClickMask() {
        if (mView != null) {
            mView.finishActivity();
        }

        statisticsSearchExit("3");
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

    @Override
    public void onClickSearchResult(Activity activity, String clickedFilePath) {
        File clickedFile = new File(clickedFilePath);
        if (clickedFile.exists()) {
            if (clickedFile.isDirectory()) {
                FileBrowserActivity.startBrowserForPaste(activity, clickedFilePath);
            } else {
                FileUtil.openFile(activity, clickedFile);
            }
        }
        statisticsClickResult();
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

            statisticsShowSearchAnim();
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
            mView.showSearchResult(mKeyword, mSearchResult);

            statisticsShowSearchResult();
        }
    }

    private void statisticsShowSearchAnim() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.SEARCH_SHOW_ANIM;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsShowSearchResult() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.SEARCH_SHOW_RESULT;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsSearchExit(String entrance) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.SEARCH_EXIT_SEARCH;
        bean.mEntrance = entrance;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickResult() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.SEARCH_CLICK_RESULT;
        StatisticsTools.upload101InfoNew(bean);
    }
}

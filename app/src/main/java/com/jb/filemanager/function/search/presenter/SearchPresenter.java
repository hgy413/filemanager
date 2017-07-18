package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.function.search.SearchManager;
import com.jb.filemanager.function.search.event.SearchFinishEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by nieyh on 17-7-6.
 */

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;
    private SearchContract.Support mSupport;
    /**
     * 搜索监听结果
     * */
    private IOnEventMainThreadSubscriber<SearchFinishEvent> mSearchFinishMainThreadSubscriber = new IOnEventMainThreadSubscriber<SearchFinishEvent>() {
        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SearchFinishEvent event) {
            if (mView != null) {
                mView.showResult(event.mFileInfoList);
            }
        }
    };

    public SearchPresenter(SearchContract.View view, SearchContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onViewCreated(EditText editText) {
        if (mSupport != null) {
            mSupport.showSoftInput(editText);
        }
    }

    @Override
    public void search(String input, Activity activity) {
        if (TextUtils.isEmpty(input)) {
            if (mView != null) {
                mView.tipInputEmpty();
                return;
            }
        }

        if (mView != null) {
            mView.showLoading();
        }
        if (mSupport != null) {
            mSupport.hideSoftInput(activity);
        }
        if (!TheApplication.getGlobalEventBus().isRegistered(mSearchFinishMainThreadSubscriber)) {
            TheApplication.getGlobalEventBus().register(mSearchFinishMainThreadSubscriber);
        }
        SearchManager.getInstance().requestSearch(input);
    }

    @Override
    public void release() {
        //取消搜索
        SearchManager.getInstance().cancelSearch();
        if (TheApplication.getGlobalEventBus().isRegistered(mSearchFinishMainThreadSubscriber)) {
            TheApplication.getGlobalEventBus().unregister(mSearchFinishMainThreadSubscriber);
        }
    }


    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            mView.finishActivity();
        }
    }

    @Override
    public void onCLickClearInputButton() {
        if (mView != null) {
            mView.clearInput();
        }
    }
}

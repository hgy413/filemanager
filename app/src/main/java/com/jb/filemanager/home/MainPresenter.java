package com.jb.filemanager.home;

import android.content.Intent;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.home.event.CurrentPathChangeEvent;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

@SuppressWarnings("StatementWithEmptyBody")
public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private MainContract.Support mSupport;

    private int mCurrentTab = 0;

    private long mExitTime;

    private String mCurrentPath;

    private IOnEventMainThreadSubscriber<CurrentPathChangeEvent> mPathChangeEvent = new IOnEventMainThreadSubscriber<CurrentPathChangeEvent>() {

        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(CurrentPathChangeEvent event) {
            mCurrentPath = event.mCurrentPath;
        }
    };

    MainPresenter(MainContract.View view, MainContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {
        EventBus.getDefault().register(mPathChangeEvent);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

        if (EventBus.getDefault().isRegistered(mPathChangeEvent)) {
            EventBus.getDefault().unregister(mPathChangeEvent);
        }

        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            if (systemBack) {
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    mExitTime = System.currentTimeMillis();
                    Toast.makeText(mSupport.getContext(), R.string.main_double_click_exit_app_tips, Toast.LENGTH_SHORT).show();
                } else {
                    mView.finishActivity();
                }
            } else {
                // 首页没有非系统返回
            }
        }
    }

    @Override
    public void onPressHomeKey() {
        // nothing to do
    }

    @Override
    public void onSwitchTab(int pos) {
        if (mView != null) {
            mCurrentTab = pos;
            mView.showNormalStatus(mCurrentTab);
        }

        statisticsSwitchTab();
    }

    @Override
    public void onClickDrawerButton() {
        if (mView != null) {
            mView.openDrawer(MainDrawer.CLI_OPEN);
        }

        statisticsClickDrawer();
    }

    @Override
    public void onClickActionSearchButton() {
        if (mView != null) {
            mView.goToSearchActivity();
        }

        statisticsClickSearch();
    }

    @Override
    public void onClickActionMoreButton() {
        if (mView != null) {
            mView.showActionMoreOperatePopWindow();
        }

        statisticsClickActionMore();
    }

    @Override
    public void onClickActionNewFolderButton() {
        if (mView != null) {
            mView.showNewFolderDialog();
        }

        statisticsClickCreateNewFolder();
    }

    @Override
    public void onClickActionSortByButton() {
        if (mView != null) {
            mView.showSortByDialog();
        }

        statisticsClickSort();
    }

    @Override
    public String getCurrentPath() {
        return mCurrentPath;
    }


    private void statisticsSwitchTab() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = mCurrentTab == 0 ? StatisticsConstants.HOME_CLICK_TAB_CATEGORY : StatisticsConstants.HOME_CLICK_TAB_STORAGE;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickDrawer() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.HOME_CLICK_DRAWER;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickSearch() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = mCurrentTab == 0 ? StatisticsConstants.HOME_CLICK_SEARCH : StatisticsConstants.STORAGE_CLICK_SEARCH;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickActionMore() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_ACTION_MORE;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickCreateNewFolder() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_CREATE_FOLDER;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickSort() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_SORT;
        StatisticsTools.upload101InfoNew(bean);
    }
}

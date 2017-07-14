package com.jb.filemanager.function.apkmanager;

import android.content.Intent;

import com.jb.filemanager.R;
import com.jb.filemanager.commomview.GroupSelectBox;
import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;
import com.jb.filemanager.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 13:37
 */

class AppManagerPresenter implements AppManagerContract.Presenter {
    private AppManagerContract.View mView;
    private AppManagerContract.Support mSupport;
    private List<AppItemInfo> mUserAppBean;
    private List<AppItemInfo> mSystemAppBean;

    AppManagerPresenter(AppManagerContract.View view, AppManagerContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {
        List<AppItemInfo> installAppInfo = mSupport.getInstallAppInfo();
        mUserAppBean = new ArrayList<>();
        mSystemAppBean = new ArrayList<>();
        for (AppItemInfo appBean : installAppInfo) {
            if (appBean.mIsSysApp) {
                mSystemAppBean.add(appBean);
            } else {
//                appBean.mIsChecked = true;
                mUserAppBean.add(appBean);
            }
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppManagerActivity.UNINSTALL_APP_REQUEST_CODE) {
            //卸载完应用  所有数据重新获取
            refreshData();
        } else if (requestCode == AppManagerActivity.SEARCH_RESULT_REQUEST_CODE) {
            refreshData();//在结果页可能操作了APP  所以刷新数据
        }
    }

    @Override
    public void refreshData() {
        onCreate(null);
        mView.refreshList();
    }

    @Override
    public void onClickBackButton(boolean isSearchBack) {
        if (mView != null) {
            if (!isSearchBack) {
                mView.finishActivity();
            }
        }
    }

    @Override
    public void onPressHomeKey() {

    }

    @Override
    public List<AppGroupBean> getAppInfo() {
        List<AppGroupBean> appGroupBeen = new ArrayList<>();
        AppGroupBean userApp = new AppGroupBean(mUserAppBean, AppUtils.getString(R.string.user_app), GroupSelectBox.SelectState.NONE_SELECTED, true);
        AppGroupBean systemApp = new AppGroupBean(mSystemAppBean, AppUtils.getString(R.string.system_app), GroupSelectBox.SelectState.NONE_SELECTED, false);
        appGroupBeen.add(userApp);
        appGroupBeen.add(systemApp);
        return appGroupBeen;
    }
}

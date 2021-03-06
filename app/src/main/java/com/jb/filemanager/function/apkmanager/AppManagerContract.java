package com.jb.filemanager.function.apkmanager;

import android.content.Intent;

import com.jb.filemanager.function.scanframe.bean.appBean.AppItemInfo;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/6/29 11:40
 */

public class AppManagerContract {
    interface View {
        void initView();
        void initData();
        void initClick();
        void initBroadcastReceiver();
        void releaseBroadcastReceiver();
        void refreshList();
        void finishActivity();
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void refreshData();
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();
        List<AppGroupBean> getAppInfo();
    }

    interface Support {
        List<AppItemInfo> getInstallAppInfo();
    }
}

package com.jb.filemanager.function.apkmanager;

import android.content.Intent;

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
        void finishActivity();
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();
        List<AppGroupBean> getAppInfo();
    }

    interface Support {
        List<AppChildBean> getInstallAppInfo();
    }
}

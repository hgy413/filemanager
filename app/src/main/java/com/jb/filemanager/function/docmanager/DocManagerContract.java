package com.jb.filemanager.function.docmanager;

import android.content.Intent;

import java.util.List;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/4 10:39
 */

public class DocManagerContract {
    interface View{
        void initView();
        void initData();
        void initList();
        void initClick();
        void refreshList();
        void initBroadcastReceiver();
        void releaseBroadcastReceiver();
        void finishActivity();
        void refreshTitle();
        void hideProgress();
        void showDocDetail(List<DocChildBean> docList);
        void showInFolder(List<DocChildBean> docList);
        void openWith(List<DocChildBean> docList);
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
        void scanStart();
        void scanFinished();
        List<DocGroupBean> getDocInfo();
    }

    interface Support {
        List<DocChildBean> getDocFileInfo();
        List<DocChildBean> getTextFileInfo();
        List<DocChildBean> getPdfFileInfo();
    }
}

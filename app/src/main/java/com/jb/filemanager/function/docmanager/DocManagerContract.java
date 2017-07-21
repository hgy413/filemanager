package com.jb.filemanager.function.docmanager;

import android.content.Intent;

import java.io.File;
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
        void refreshList(boolean keepUserCheck);
        void initBroadcastReceiver();
        void releaseBroadcastReceiver();
        void finishActivity();
        void showDocDetail(List<DocChildBean> docList);
        void fileRename(List<DocChildBean> docList);
//        void openWith(List<DocChildBean> docList);
        void updateDeleteProgress(int done,int total);
        void refreshTile();
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void refreshData(boolean keepUserCheck);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();
        void scanStart();
        void scanFinished();
        List<DocGroupBean> getDocInfo();
        void handleFileDelete(List<File> docPathList);
    }

    interface Support {
        List<DocChildBean> getDocFileInfo();
        List<DocChildBean> getTextFileInfo();
        List<DocChildBean> getPdfFileInfo();
        void handleFileDelete(String docPath);
        void handleFileCopy(String oldFile, String newFile);
        void handleFileCut(String oldFile, String newFile);
        void handleFileRename(String oldFile, String newFile);
        void scanBroadcastReceiver(File file);
    }
}

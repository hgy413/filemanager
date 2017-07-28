package com.jb.filemanager.function.docmanager;

import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
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
        void refreshList(boolean keepUserCheck, boolean shouldScanAgain);
        void initBroadcastReceiver();
        void releaseBroadcastReceiver();
        void finishActivity();
        void updateDeleteProgress(int done,int total);
        void refreshTile();
        void showBottom();
        void setLoadState(boolean isLoad);
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void refreshData(boolean keepUserCheck, boolean shouldScanAgain);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();
        void getDocInfo(boolean keepUserCheck, boolean shouldScanAgain);
        void handleFileDelete(List<File> docPathList);
    }

    interface Support {
        ArrayList<DocChildBean> getDocFileInfo();
        ArrayList<DocChildBean> getTextFileInfo();
        ArrayList<DocChildBean> getPdfFileInfo();
        void handleFileDelete(String docPath);
        void handleFileCopy(String oldFile, String newFile);
        void handleFileCut(String oldFile, String newFile);
        void handleFileRename(String oldFile, String newFile);
    }
}

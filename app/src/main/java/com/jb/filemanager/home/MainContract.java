package com.jb.filemanager.home;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class MainContract {

    interface View {
        void showNormalStatus(int tabPos);
        void showSearchStatus();
        void showActionMoreOperatePopWindow();
        void showBottomMoreOperatePopWindow(boolean multiSelected);

        void showDeleteConfirmDialog();
        void showRenameDialog();
        void showNewFolderDialog();
        void showSortByDialog();

        void updateSelectedFileChange();

        void openDrawer(int openType);
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

        void onSwitchTab(int tabPos);
        void onClickDrawerButton();
        void onClickActionSearchButton();
        void onClickActionMoreButton();
        void onClickOperateCutButton();
        void onClickOperateCopyButton();
        void onClickOperateDeleteButton();
        void onClickOperateMoreButton();
        void onClickOperateDetailButton();
        void onClickOperateRenameButton();
        void onClickActionNewFolderButton();
        void onClickActionSortByButton();

        boolean onClickConfirmCreateFolderButton(String name);
        boolean onClickConfirmDeleteButton();
        boolean onClickConfirmRenameButton(String name);
    }

    interface Support {
        Context getContext();
        Application getApplication();
    }

}

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
        void showRenameDialog(File file);
        void showDetailSingleFile(File file);
        void showDetailMultiFile(ArrayList<File> files);
        void showNewFolderDialog();
        void showSortByDialog();
        void showPasteNeedMoreSpaceDialog(long needMoreSpace);
        void showStoragePage();
        void updateView();

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

        int getStatus();

        void onSwitchTab(int tabPos);
        void onClickDrawerButton();
        void onClickActionSearchButton();
        void onClickActionMoreButton();
        void onClickActionNewFolderButton();
        void onClickActionSortByButton();

        void onClickOperateCutButton();
        void onClickOperateCopyButton();
        void onClickOperateDeleteButton();
        void onClickOperateMoreButton();
        void onClickOperateDetailButton();
        void onClickOperateRenameButton();
        void onClickOperateCancelButton();
        void onClickOperatePasteButton();

        boolean onClickConfirmCreateFolderButton(String name);
        boolean onClickConfirmDeleteButton();
        boolean onClickConfirmRenameButton(String name);

        boolean isSelected(File file);
        void addOrRemoveSelected(File file);
        ArrayList<File> getSelectedFiles();
        void updateCurrentPath(String path);
        String getCurrentPath();

        boolean isFileExplorer();

        String getTargetFilePath();
    }

    interface Support {
        Context getContext();
        Application getApplication();
    }

}

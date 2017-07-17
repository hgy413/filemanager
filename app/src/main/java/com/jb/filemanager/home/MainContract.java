package com.jb.filemanager.home;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

public class MainContract {

    interface View {
        void showNormalStatus(int tabPos);
        void showActionMoreOperatePopWindow();

        void showNewFolderDialog();
        void showSortByDialog();
        void showStoragePage();

        void openDrawer(int openType);
        void finishActivity();

        void goToSearchActivity();
    }

    public interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();

        void onSwitchTab(int tabPos);
        void onClickDrawerButton();
        void onClickActionBackButton();
        void onClickActionSearchButton();
        void onClickActionMoreButton();
        void onClickActionNewFolderButton();
        void onClickActionSortByButton();

        boolean onClickConfirmCreateFolderButton(String name);

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

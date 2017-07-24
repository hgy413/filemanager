package com.jb.filemanager.home.fragment.storage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill wang on 2017/7/13.
 *
 */

public class StorageContract {

    interface View {
        void updateListAndGrid();
        void updateBottomBar();
        void updateCurrentPath(List<File> data, File currentPath);
        void updateItemSelectStatus(Object holder);
        void showPasteNeedMoreSpaceDialog(long needMoreSpace);
    }

    interface Presenter {
        void onCreate(Bundle args);
        void onActivityCreated();
        void onResume();
        void onPause();
        void onDestroy();

        boolean onClickSystemBack();
        void onClickItem(File file, Object holder);
        void onClickPath(String word);
        void onClickOperateCancelButton();
        void onClickOperatePasteButton();

        void afterCopy();
        void afterCut();
        void afterRename();
        void afterDelete();

        int getStatus();
        boolean isSelected(File file);
        void addOrRemoveSelected(File file);
        ArrayList<File> getSelectedFiles();
        String getCurrentPath();
        ArrayList<File> getStorageList();
    }

    interface Support {
        Context getContext();
        Application getApplication();
        LoaderManager getLoaderManager();
        Activity getActivity();
    }

}

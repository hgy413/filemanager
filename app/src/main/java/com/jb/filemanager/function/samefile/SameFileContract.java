package com.jb.filemanager.function.samefile;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bool on 17-6-30.
 */

public interface SameFileContract {
    interface View {
        void initView(int fileType);

        void showFileList(GroupList<String, FileInfo> mMusicMaps);

        void onNoFileFindShow();

        void fileSelectShow(int num);
    }

    interface Presenter {
        void onCreate(Intent intent);

        void onDestroy();

        void onClickBackButton(boolean b);

        void onClickSearchButton();

        void start(final int fileType);

        ArrayList<File> getSelectFile();

        ArrayList<FileInfo> getSelectInfo();

        void jumpToFileBrowserPage();

        void reloadData();

        void selectAllFile();

        void cleanSelect();
    }

    interface Support {

        Context getContext();

        Map<String, ArrayList<FileInfo>> getAllMusicInfo();

        int getMuscisNum();

        void delete(ArrayList<String> fullPathList);

        GroupList<String,FileInfo> getAllDownloadInfo();

        GroupList<String,FileInfo> getAllVideoInfo();

        List<File> getFiles(File file);

        void updateDatabaseRename(String oldFile, String newFile);
    }
}

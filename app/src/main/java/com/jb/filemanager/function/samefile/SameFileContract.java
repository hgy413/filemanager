package com.jb.filemanager.function.samefile;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bool on 17-6-30.
 */

public interface SameFileContract {
    interface View {
        void initView(int fileType);

        void showFileList(GroupList<String, FileInfo> mMusicMaps);

        void showDeleteConfirmDialog();

        void showBottomMoreOperatePopWindow(boolean b);
    }

    interface Presenter {
        void onCreate(Intent intent);

        void onClickBackButton(boolean b);

        void start(final int fileType);

        void onClickOperateCutButton(boolean[] selectedPosition);

        void onClickOperateCopyButton(boolean[] selectedPosition);

        void onClickOperateDeleteButton();

        void onClickOperateMoreButton(boolean[] selectedPosition);

        void onClickConfirmDeleteButton(boolean[] selectedPosition);

        void onClickOperateDetailButton();

        void onClickOperateRenameButton();
    }

    interface Support {
        Map<String, ArrayList<FileInfo>> getAllMusicInfo();

        int getMuscisNum();

        void delete(ArrayList<String> fullPathList);

        GroupList<String,FileInfo> getAllDownloadInfo();

        GroupList<String,FileInfo> getAllVideoInfo();
    }
}

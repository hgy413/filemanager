package com.jb.filemanager.function.samefile;

import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bool on 17-6-30.
 */

public interface SameFileContract {
    interface View {
        void initView(int fileType);

        void showFileList(GroupList<String, FileInfo> mMusicMaps);

        //        if (rename != null) {
        //            rename.getPaint().setAntiAlias(true);
        //            rename.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    if (mPresenter != null) {
        //                        mPresenter.onClickOperateRenameButton();
        //                    }
        //                    popupWindow.dismiss();
        //                }
        //            });
        //        }
        //    }
        void onNoFileFindShow();
    }

    interface Presenter {
        void onCreate(Intent intent);

        void onClickBackButton(boolean b);

        void start(final int fileType);

        ArrayList<File> getSelectFile();

        void jumpToStoragePage();


//        void onClickConfirmDeleteButton(boolean[] selectedPosition);
//
//        void onClickOperateDetailButton();
//
//        void onClickOperateRenameButton();
    }

    interface Support {
        Map<String, ArrayList<FileInfo>> getAllMusicInfo();

        int getMuscisNum();

        void delete(ArrayList<String> fullPathList);

        GroupList<String,FileInfo> getAllDownloadInfo();

        GroupList<String,FileInfo> getAllVideoInfo();
    }
}

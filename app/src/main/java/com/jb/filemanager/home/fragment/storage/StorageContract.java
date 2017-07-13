package com.jb.filemanager.home.fragment.storage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/13.
 *
 */

public class StorageContract {

    interface View {
        void updateView();
        void showPasteNeedMoreSpaceDialog(long needMoreSpace);
    }

    interface Presenter {
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
        void updateCurrentPath(String path);
        String getCurrentPath();
    }

}

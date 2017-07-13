package com.jb.filemanager.home.fragment.storage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/13.
 *
 */

public class StorageContract {

    interface View {
        void updateView(int currentStatus);
        void showPasteNeedMoreSpaceDialog(long needMoreSpace);
    }

    interface Presenter {
        void onClickOperateCancelButton();
        void onClickOperatePasteButton();

        int getStatus();
        boolean isSelected(File file);
        void addOrRemoveSelected(File file);
        ArrayList<File> getSelectedFiles();
        void updateCurrentPath(String path);
        String getCurrentPath();
    }

}

package com.jb.filemanager.home.fragment.storage;

import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/13.
 *
 */

class StoragePresenter implements StorageContract.Presenter {

    public static final int MAIN_STATUS_NORMAL = 0;
    public static final int MAIN_STATUS_SELECT = 1;
    public static final int MAIN_STATUS_PASTE = 2;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private String mCurrentPath;

    private int mStatus = MAIN_STATUS_NORMAL;

    private StorageContract.View mView;

    StoragePresenter(StorageContract.View view) {
        mView = view;
    }

    @Override
    public void onResume() {
        ArrayList<File> copyFiles = FileManager.getInstance().getCopyFiles();
        ArrayList<File> cutFiles = FileManager.getInstance().getCutFiles();
        if ((copyFiles != null && copyFiles.size() > 0) || (cutFiles != null && cutFiles.size() > 0)) {
            mStatus = MAIN_STATUS_PASTE;
            if (mView != null) {
                mView.updateView();
            }
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (mSelectedFiles != null) {
            mSelectedFiles.clear();
            mSelectedFiles = null;
        }
    }

    @Override
    public void onClickOperateCancelButton() {
        if (mView != null) {
            FileManager.getInstance().clearCopyFiles();
            FileManager.getInstance().clearCutFiles();
            mStatus = MAIN_STATUS_NORMAL;
            mView.updateView();
        }
    }

    @Override
    public void onClickOperatePasteButton() {
        if (mView != null) {
            FileManager.getInstance().doPaste(mCurrentPath, new FileManager.Listener() {

                @Override
                public void onPasteNeedMoreSpace(long needMoreSpace) {
                    if (mView != null) {
                        mView.showPasteNeedMoreSpaceDialog(needMoreSpace);
                    }
                }

                @Override
                public void onPasteProgressUpdate(File file) {
                    Logger.e("wangzq", "on paste: " + file.getAbsolutePath());
                }
            });

            FileManager.getInstance().clearCopyFiles();
            FileManager.getInstance().clearCutFiles();

            mStatus = MAIN_STATUS_NORMAL;
            mView.updateView();
        }
    }

    @Override
    public void afterCopy() {
        mStatus = MAIN_STATUS_PASTE;
        resetSelectFile();
    }

    @Override
    public void afterCut() {
        mStatus = MAIN_STATUS_PASTE;
        resetSelectFile();
    }

    @Override
    public void afterRename() {
        mStatus = MAIN_STATUS_NORMAL;
        resetSelectFile();
    }

    @Override
    public void afterDelete() {
        mStatus = MAIN_STATUS_NORMAL;
        resetSelectFile();
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public boolean isSelected(File file) {
        boolean result = false;
        if (file != null) {
            try {
                result = mSelectedFiles.contains(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void addOrRemoveSelected(File file) {
        switch (mStatus) {
            case MAIN_STATUS_NORMAL:
            case MAIN_STATUS_SELECT:
                if (file != null) {
                    try {
                        if (mSelectedFiles.contains(file)) {
                            mSelectedFiles.remove(file);
                        } else {
                            mSelectedFiles.add(file);
                        }

                        if (mSelectedFiles.size() > 0) {
                            mStatus = MAIN_STATUS_SELECT;
                        } else {
                            mStatus = MAIN_STATUS_NORMAL;
                        }

                        mView.updateView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public ArrayList<File> getSelectedFiles() {
        return mSelectedFiles;
    }

    @Override
    public void updateCurrentPath(String path) {
        mCurrentPath = path;
    }

    @Override
    public String getCurrentPath() {
        return mCurrentPath;
    }



    private void resetSelectFile() {
        mSelectedFiles.clear();
        if (mView != null) {
            mView.updateView();
        }
    }
}

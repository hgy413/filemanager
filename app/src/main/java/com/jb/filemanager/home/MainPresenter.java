package com.jb.filemanager.home;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.manager.file.FileManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

@SuppressWarnings("StatementWithEmptyBody")
class MainPresenter implements MainContract.Presenter {

    public static final int MAIN_STATUS_NORMAL = 0;
    public static final int MAIN_STATUS_SELECT = 1;
    public static final int MAIN_STATUS_CUT = 2;
    public static final int MAIN_STATUS_COPY = 3;

    private MainContract.View mView;
    private MainContract.Support mSupport;

    private boolean mIsInSearchMode;
    private int mCurrentTab = 0;

    private long mExitTime;

    private int mStatus = MAIN_STATUS_NORMAL;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private String mCurrentPath;

    MainPresenter(MainContract.View view, MainContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Intent intent) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mSupport = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClickBackButton(boolean systemBack) {
        if (mView != null) {
            if (systemBack) {
                if (mIsInSearchMode) {
                    mIsInSearchMode = false;
                    mView.showNormalStatus(mCurrentTab);
                } else {
                    if (System.currentTimeMillis() - mExitTime > 2000) {
                        mExitTime = System.currentTimeMillis();
                        Toast.makeText(mSupport.getContext(), R.string.main_double_click_exit_app_tips, Toast.LENGTH_SHORT).show();
                    } else {
                        mView.finishActivity();
                    }
                }
            } else {
                // 首页没有非系统返回
            }
        }
    }

    @Override
    public void onPressHomeKey() {
        // nothing to do
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public void onSwitchTab(int pos) {
        if (mView != null) {
            mCurrentTab = pos;
            mView.showNormalStatus(mCurrentTab);
        }
    }

    @Override
    public void onClickDrawerButton() {
        if (mView != null) {
            mView.openDrawer(MainDrawer.CLI_OPEN);
        }
    }

    @Override
    public void onClickActionSearchButton() {
        if (mView != null) {
            mIsInSearchMode = true;
            mView.showSearchStatus();
        }
    }

    @Override
    public void onClickActionMoreButton() {
        if (mView != null) {
            mView.showActionMoreOperatePopWindow();
        }
    }

    @Override
    public void onClickOperateCutButton() {
        // TODO


        Toast.makeText(mSupport.getContext(), "Cut", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickOperateCopyButton() {
        // TODO
        Toast.makeText(mSupport.getContext(), "Copy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClickOperateDeleteButton() {
        if (mView != null) {
            mView.showDeleteConfirmDialog();
        }
    }

    @Override
    public void onClickOperateMoreButton() {
        if (mView != null) {
            if (mSelectedFiles != null) {
                int size = mSelectedFiles.size();
                // 这里size 只可能是大于等于1，没有选中文件的时候应该不会出现底部操作栏
                switch (size) {
                    case 1:
                        mView.showBottomMoreOperatePopWindow(false);
                        break;
                    default:
                        mView.showBottomMoreOperatePopWindow(true);
                        break;
                }
            }
        }
    }

    @Override
    public void onClickOperateDetailButton() {
        if (mView != null) {
            if (mSelectedFiles != null && mSelectedFiles.size() == 1) {
                mView.showDetailSingleFile(mSelectedFiles.get(0));
            } else {
                mView.showDetailMultiFile(mSelectedFiles);
            }
        }
    }

    @Override
    public void onClickOperateRenameButton() {
        if (mView != null) {
            if (mSelectedFiles != null && mSelectedFiles.size() == 1) {
                mView.showNewFolderDialog();
            }
        }
    }

    @Override
    public void onClickActionNewFolderButton() {
        if (mView != null) {
            mView.showNewFolderDialog();
        }
    }

    @Override
    public void onClickActionSortByButton() {
        if (mView != null) {
            mView.showSortByDialog();
        }
    }

    @Override
    public boolean onClickConfirmCreateFolderButton(String name) {
        // TODO 判断输入格式是否符合规定
        return FileManager.getInstance().createFolder(name);
    }

    @Override
    public boolean onClickConfirmDeleteButton() {
        ArrayList<File> deleteFailedFiles = FileManager.getInstance().deleteSelectedFiles(mSelectedFiles);
        return (deleteFailedFiles == null || deleteFailedFiles.size() == 0);
    }

    @Override
    public boolean onClickConfirmRenameButton(String name) {
        // TODO 判断输入格式是否符合规定
        boolean result = false;
        if (!TextUtils.isEmpty(mCurrentPath) && mSelectedFiles != null && mSelectedFiles.size() == 1) {
            result = FileManager.getInstance().renameSelectedFile(mSelectedFiles.get(0), mCurrentPath + File.separator + name);
        }
        return result;
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
    public void addSelected(File file) {
        if (file != null) {
            try {
                if (!mSelectedFiles.contains(file)) {
                    mSelectedFiles.add(file);
                    mView.updateSelectedFileChange();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeSelected(File file) {
        if (file != null) {
            try {
                if (mSelectedFiles.contains(file)) {
                    mSelectedFiles.remove(file);
                    mView.updateSelectedFileChange();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}

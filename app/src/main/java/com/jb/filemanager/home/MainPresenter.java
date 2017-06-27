package com.jb.filemanager.home;

import android.content.Intent;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.home.event.SelectFileEvent;
import com.jb.filemanager.manager.file.FileManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/21.
 *
 */

@SuppressWarnings("StatementWithEmptyBody")
class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private MainContract.Support mSupport;

    private boolean mIsInSearchMode;
    private int mCurrentTab = 0;

    private long mExitTime;

    private IOnEventMainThreadSubscriber<SelectFileEvent> mSelectFileEvent = new IOnEventMainThreadSubscriber<SelectFileEvent>() {
        @Subscribe(threadMode = ThreadMode.MAIN)
        @Override
        public void onEventMainThread(SelectFileEvent event) {
            File file = event.mFile;
            try {
                if (file != null && mView != null) {
                    mView.updateSelectedFileChange();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    MainPresenter(MainContract.View view, MainContract.Support support) {
        mView = view;
        mSupport = support;

        EventBus.getDefault().register(mSelectFileEvent);
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
        EventBus.getDefault().unregister(mSelectFileEvent);
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
            ArrayList<File> files = FileManager.getInstance().getSelectedFiles();
            if (files != null) {
                int size = files.size();
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
            ArrayList<File> files = FileManager.getInstance().getSelectedFiles();
            if (files != null && files.size() == 1) {
                mView.showDetailSingleFile(files.get(0));
            } else {
                mView.showDetailMultiFile(files);
            }
        }
    }

    @Override
    public void onClickOperateRenameButton() {
        if (mView != null) {
            ArrayList<File> files = FileManager.getInstance().getSelectedFiles();
            if (files != null && files.size() == 1) {
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
        ArrayList<File> deleteFailedFiles = FileManager.getInstance().deleteSelectedFiles();
        return (deleteFailedFiles == null || deleteFailedFiles.size() == 0);
    }

    @Override
    public boolean onClickConfirmRenameButton(String name) {
        // TODO 判断输入格式是否符合规定
        return FileManager.getInstance().renameSelectedFile(name);
    }
}

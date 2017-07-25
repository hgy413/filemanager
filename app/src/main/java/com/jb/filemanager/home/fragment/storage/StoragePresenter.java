package com.jb.filemanager.home.fragment.storage;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.jb.filemanager.R;
import com.jb.filemanager.eventbus.IOnEventMainThreadSubscriber;
import com.jb.filemanager.home.event.CurrentPathChangeEvent;
import com.jb.filemanager.home.event.SortByChangeEvent;
import com.jb.filemanager.manager.file.FileLoader;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bill wang on 2017/7/13.
 *
 */

class StoragePresenter implements StorageContract.Presenter,
        LoaderManager.LoaderCallbacks<List<File>> {

    public static final int MAIN_STATUS_NORMAL = 0;
    public static final int MAIN_STATUS_SELECT = 1;
    public static final int MAIN_STATUS_PASTE = 2;

    private ArrayList<File> mStorageList;
    private Stack<File> mPathStack;

    private ArrayList<File> mSelectedFiles = new ArrayList<>();
    private String mCurrentPath;

    private int mStatus = MAIN_STATUS_NORMAL;

    private StorageContract.View mView;
    private StorageContract.Support mSupport;

    private IOnEventMainThreadSubscriber<SortByChangeEvent> mSortChangeEvent = new IOnEventMainThreadSubscriber<SortByChangeEvent>() {

        @Override
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEventMainThread(SortByChangeEvent event) {
            restartLoad();
        }
    };

    StoragePresenter(StorageContract.View view, StorageContract.Support support) {
        mView = view;
        mSupport = support;
    }

    @Override
    public void onCreate(Bundle args) {
        mPathStack = new Stack<>();
        mStorageList = new ArrayList<>();

        String path = "";
        if (args != null) {
            path = args.getString(StorageFragment.PARAM_PATH);
        }

        initStoragePath(path);

        EventBus.getDefault().register(mSortChangeEvent);
    }

    @Override
    public void onActivityCreated() {
        if (mSupport != null) {
            LoaderManager loaderManager = mSupport.getLoaderManager();
            if (loaderManager != null) {
                loaderManager.initLoader(FileManager.LOADER_FILES, null, this);
            }
        }
    }

    @Override
    public void onResume() {
        ArrayList<File> copyFiles = FileManager.getInstance().getCopyFiles();
        ArrayList<File> cutFiles = FileManager.getInstance().getCutFiles();
        if ((copyFiles != null && copyFiles.size() > 0) || (cutFiles != null && cutFiles.size() > 0)) {
            mStatus = MAIN_STATUS_PASTE;
            if (mView != null) {
                mView.updateBottomBar();
            }
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(mSortChangeEvent)) {
            EventBus.getDefault().unregister(mSortChangeEvent);
        }

        if (mSupport != null) {
            LoaderManager loaderManager = mSupport.getLoaderManager();
            if (loaderManager != null) {
                loaderManager.destroyLoader(FileManager.LOADER_FILES);
            }
        }

        if (mSelectedFiles != null) {
            mSelectedFiles.clear();
            mSelectedFiles = null;
        }

        mView = null;
        mSupport = null;
    }

    @Override
    public boolean onClickSystemBack() {
        boolean result = false;
        if (mView != null && mSupport != null) {
            if (mStatus == MAIN_STATUS_SELECT) {
                result = true;
                mStatus = MAIN_STATUS_NORMAL;
                resetSelectFile();
            } else {
                if (mPathStack.size() > 1) {
                    mPathStack.pop();
                    restartLoad();
                    return true;
                } else if (mPathStack.size() == 1) {
                    if (mStorageList.size() == 1) {
                        return false;
                    } else {
                        mPathStack.pop();
                        mCurrentPath = null;
                        mView.updateCurrentPath(mStorageList, null);
                        EventBus.getDefault().post(new CurrentPathChangeEvent(mCurrentPath));
                        return true;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void onClickItem(File file, Object holder) {
        if (mView != null && mSupport != null) {
            boolean handleClick = false;
            boolean enterFolder = false;

            if (mStatus == MAIN_STATUS_SELECT) {
                handleClick = true;
                enterFolder = false;
            } else if (mStatus == MAIN_STATUS_NORMAL) {
                handleClick = true;
                enterFolder = file.isDirectory();
            } else if (mStatus == MAIN_STATUS_PASTE){
                if (file.isDirectory()) {
                    handleClick = true;
                    enterFolder = true;
                }
            }

            if (handleClick) {
                if (file.isDirectory() && enterFolder) {
                    mPathStack.push(file);
                    restartLoad();
                } else {
                    FileUtil.openFile(mSupport.getActivity(), file);
                }
            }
        }
    }

    @Override
    public void onClickPath(String word) {
        if (mView != null && mSupport != null) {
            if (mStatus == MAIN_STATUS_SELECT) {
                mStatus = MAIN_STATUS_NORMAL;
                resetSelectFile();
            }

            String clickDirectory = word;
            for (File file : mStorageList) {
                if ((FileUtil.isInternalStoragePath(mSupport.getContext(), file.getAbsolutePath()) && word.equals(mSupport.getContext().getString(R.string.main_internal_storage)))
                        || file.getName().equals(word)) {
                    clickDirectory = file.getAbsolutePath();
                    break;
                }
            }
            if (mPathStack.isEmpty()) {
                return;
            }
            String currentDir = mPathStack.lastElement().getAbsolutePath();
            if (currentDir.endsWith(clickDirectory)) {
                if (isRootDir(currentDir) && mStorageList.size() > 1) {
                    mPathStack.clear();
                    mCurrentPath = null;
                    mView.updateCurrentPath(mStorageList, null);
                    EventBus.getDefault().post(new CurrentPathChangeEvent(mCurrentPath));
                }
            } else {
                int index = currentDir.indexOf(clickDirectory);
                String dir = currentDir.substring(0,
                        index + clickDirectory.length());
                Stack<File> temp = new Stack<>();
                for (File file : mPathStack) {
                    temp.push(file);
                    if (file.getAbsolutePath().equals(dir)) {
                        break;
                    }
                }
                mPathStack.clear();
                mPathStack.addAll(temp);
                restartLoad();
            }
        }

        statisticsClickPath();
    }

    @Override
    public void onClickOperateCancelButton() {
        if (mView != null) {
            FileManager.getInstance().clearCopyFiles();
            FileManager.getInstance().clearCutFiles();
            mStatus = MAIN_STATUS_NORMAL;
            mView.updateBottomBar();
        }
    }

    @Override
    public void onClickOperatePasteButton() {
        if (mView != null) {
            if (!TextUtils.isEmpty(mCurrentPath)) {
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
                mView.updateBottomBar();
            } else {
                AppUtils.showToast(mSupport.getContext(), R.string.toast_paste_dest_disable);
            }
        }

        statisticsClickPaste();
    }

    @Override
    public void onClickStyleSwitcher(boolean currentIsGrid) {
        if (mView != null) {
            if (currentIsGrid) {
                mView.showListStyle();
            } else {
                mView.showGridStyle();
            }

            statisticsClickStyleSwitch(currentIsGrid ? "1" : "2");
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

                        mView.updateBottomBar();
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
    public String getCurrentPath() {
        return mCurrentPath;
    }

    @Override
    public ArrayList<File> getStorageList() {
        return mStorageList;
    }

    // implements LoaderManager.LoaderCallbacks<List<File>> start
    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        Loader<List<File>> result = null;
        if (mSupport != null) {
            if (mPathStack != null && !mPathStack.isEmpty()) {
                result = new FileLoader(mSupport.getContext(), mPathStack.lastElement().getAbsolutePath(), FileManager.getInstance().getFileSort());
            } else {
                result = new FileLoader(mSupport.getContext(), null, FileManager.getInstance().getFileSort());
            }
        }
        return result;
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        if (mView != null && data != null && mPathStack != null && !mPathStack.isEmpty()) {
            mCurrentPath = mPathStack.lastElement().getAbsolutePath();
            mView.updateCurrentPath(data, mPathStack.lastElement());
            EventBus.getDefault().post(new CurrentPathChangeEvent(mCurrentPath));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        if (mView != null) {
            mCurrentPath = null;
            mView.updateCurrentPath(null, null);
            EventBus.getDefault().post(new CurrentPathChangeEvent(mCurrentPath));
        }
    }

    // implements LoaderManager.LoaderCallbacks<List<File>> end


    // ************************* private start *************************
    private void initStoragePath(String targetPath) {
        if (mSupport != null) {
            String[] paths = FileUtil.getVolumePaths(mSupport.getContext());
            if (paths != null && paths.length > 0) {
                for (String path : paths) {
                    File file = new File(path);
                    mStorageList.add(file);
                }

                boolean needDefault = true;
                if (!TextUtils.isEmpty(targetPath)) {
                    File target = new File(targetPath);
                    if (target.exists()) {
                        addTargetToStack(target);
                        mCurrentPath = targetPath;
                        needDefault = false;
                    }
                }

                if (needDefault) {
                    mPathStack.push(new File(mStorageList.get(0).getAbsolutePath()));
                }
            }
        }
    }

    private void resetSelectFile() {
        mSelectedFiles.clear();
        if (mView != null) {
            mView.updateListAndGrid();
            mView.updateBottomBar();
        }
    }

    private void restartLoad() {
        if (mSupport != null) {
            LoaderManager loaderManager = mSupport.getLoaderManager();
            if (loaderManager != null) {
                loaderManager.restartLoader(FileManager.LOADER_FILES, null, this);
            }
        }
    }

    private boolean isRootDir(String path) {
        if (mStorageList.size() > 0) {
            for (File file : mStorageList) {
                if (path.equals(file.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addTargetToStack(File file) {
        if (file != null && file.exists()) {
            if (isRootDir(file.getAbsolutePath())) {
                mPathStack.push(file);
            } else {
                File parent = file.getParentFile();
                if (parent != null) {
                    if (isRootDir(parent.getAbsolutePath())) {
                        mPathStack.push(parent);
                    } else {
                        addTargetToStack(parent);
                        mPathStack.push(parent);
                    }
                }
            }
        }
    }

    private void statisticsClickPath() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_PATH;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickStyleSwitch(String style) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_STYLE_SWITCH;
        bean.mTab = style;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickPaste() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CLICK_PASTE;
        StatisticsTools.upload101InfoNew(bean);
    }
}

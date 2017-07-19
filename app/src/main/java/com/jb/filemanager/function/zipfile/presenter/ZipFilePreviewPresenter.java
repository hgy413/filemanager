package com.jb.filemanager.function.zipfile.presenter;

import android.util.Log;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.zipfile.ExtractManager;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.LoadZipInnerFilesListener;
import com.jb.filemanager.function.zipfile.task.LoadZipInnerFilesTask;
import com.jb.filemanager.function.zipfile.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by xiaoyu on 2017/7/4 16:01.
 */

public class ZipFilePreviewPresenter implements ZipFilePreviewContract.Presenter,
        LoadZipInnerFilesListener {

    private ZipFilePreviewContract.View mView;
    private LoadZipInnerFilesTask mTask;

    private String mZipFilePath;
    private String mPassword;
    private String mRootDir;
    private String mRootDirBack; // 供undo使用

    private Stack<String> mPathStack = new Stack<>();
    private Stack<String> mPathStackBack = new Stack<>(); // 供undo使用
    private List<ZipPreviewFileBean> mListData = new ArrayList<>();
    private List<ZipPreviewFileBean> mSelected = new ArrayList<>();

    public ZipFilePreviewPresenter(ZipFilePreviewContract.View view) {
        mView = view;
    }

    @Override
    public void onCreate(String filePath, String password) {
        mZipFilePath = filePath;
        mPassword = password;
        mRootDir = FileUtils.getFileName(mZipFilePath);
        mView.addBreadcrumbRoot(mRootDir);
        loadFiles();
    }

    /**
     * 加载文件内容列表
     */
    private void loadFiles() {
        mTask = new LoadZipInnerFilesTask();
        mTask.setListener(this);
        if (mRootDir.equals(FileUtils.getFileName(mZipFilePath))) {
            mTask.execute(mZipFilePath, "", mPassword);
        } else {
            mTask.execute(mZipFilePath, mRootDir, mPassword);
        }
    }

    @Override
    public void onPreLoad() {
        mSelected.clear();
        mView.setExtractBtnVisibility(false);
        Log.e("task", "开始加载");
        TheApplication.postRunOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showProgressDialog();
            }
        });
    }

    @Override
    public void onLoading(int value) {
    }

    @Override
    public void onPosLoad(List<ZipPreviewFileBean> result) {
        if (result != null) {
            mListData.clear();
            mListData.addAll(result);
            mView.updateListData(mListData);
            mView.hideProgressDialog();
        } else {
            onLoadError();
        }
        Log.e("task", "加载完成");
    }

    @Override
    public void onCanceled() {
        Log.e("task", "任务取消");
    }

    /**
     * 打开压缩包文件错误:密码错误或者文件错误
     */
    private void onLoadError() {
        mView.showToast("打开文件失败");
        mView.hideProgressDialog();
    }

    @Override
    public void onProgressDialogCancel() {
        undoLoad();
        mTask.cancel(true);
    }

    @Override
    public void onBreadcrumbClick(String path) {
        if (!mRootDir.equals(path)) {
            // 移除当前路径以及后面的路径堆栈
            int index = mPathStack.indexOf(path);
            if (index != -1) {
                // 存根
                mPathStackBack.clear();
                mPathStackBack.addAll(mPathStack);
                mRootDirBack = mRootDir;

                int size = mPathStack.size();
                for (int i = size - 1; i >= index; i--) {
                    mPathStack.remove(i);
                }
                mRootDir = path;
                loadFiles();
            }
        }
    }

    @Override
    public void onListItemClick(ZipPreviewFileBean item) {
        if (item.isDirectory()) {
            // 存根
            mPathStackBack.clear();
            mPathStackBack.addAll(mPathStack);
            mRootDirBack = mRootDir;

            mPathStack.push(mRootDir);
            mRootDir = item.getFullPath();
            mView.navigationForward(mRootDir);

            for (String s : mPathStack) {
                Log.e("stack", s);
            }
            loadFiles();
        } else {
           // mView.showToast("click file");
        }
    }

    @Override
    public void onBackPressed() {
        if (ExtractManager.getInstance().isProgressDialogAttached()) {
            ExtractManager.getInstance().hideProgressDialogFromWindow();
        } else if (!mPathStack.isEmpty()) {
            // 存根
            mPathStackBack.clear();
            mPathStackBack.addAll(mPathStack);
            mRootDirBack = mRootDir;

            mRootDir = mPathStack.pop();
            mView.navigationBackward(false);
            loadFiles();
        } else {
            mView.navigationBackward(true);
        }
    }

    @Override
    public void onItemStateClick() {
        mSelected.clear();
        for (ZipPreviewFileBean bean : mListData) {
            if (bean.isSelected()) {
                mSelected.add(bean);
            }
        }
        mView.setExtractBtnVisibility(mSelected.size() > 0);
    }

    /**
     * 加载过程中撤销操作
     */
    private void undoLoad() {
        // 3种情况
        int delta = mPathStack.size() - mPathStackBack.size();
        if (delta == 1) {
            // 1. 点击列表item后forward加载过程中取消
            mRootDir = mPathStack.pop();
            mView.navigationBackward(false);
        } else if (delta == -1) {
            // 2. 点击物理返回键backward或点解Breadcrumb(一级)加载过程中取消
            mPathStack.push(mRootDir);
            mRootDir = mRootDirBack;
            mView.navigationForward(mRootDir);
        } else if (-delta > 1) {
            // 3. 点击Breadcrumb后backward(多级)加载过程中取消
            // a b c d e f
            // mRootDir = f
            // mStack= a b c d e -- size = 5
            // mStackBack = a b c d e -- size = 5
            // after click c
            // mRootDir = c
            // mStack = a b size = 2
            // mStackBack = a b c d e f size = 5
            // GOAL mStack = a b c d e f
            delta = -delta;
            int size = mPathStack.size();
            for (int i = size; i < delta + size; i++) {
                String item = mPathStackBack.get(i);
                mPathStack.push(item);
                if (i == size) continue;
                mView.navigationForward(item);
            }
            mRootDir = mRootDirBack;
            mView.navigationForward(mRootDir);
        }
    }

    @Override
    public void onExtractFiles() {
        if (mSelected.size() > 0) {
            ExtractManager.getInstance().extractFiles(mZipFilePath, mPassword, mSelected);
        }
    }
}

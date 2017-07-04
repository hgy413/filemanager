package com.jb.filemanager.function.zipfile.presenter;

import android.util.Log;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.LoadZipInnerFilesListener;
import com.jb.filemanager.function.zipfile.task.LoadZipInnerFilesTask;

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
    private String mRootDir = "";
    private String mPassword;
    private Stack<String> mPathStack = new Stack<>();

    public ZipFilePreviewPresenter(ZipFilePreviewContract.View view) {
        mView = view;
    }

    @Override
    public void onCreate(String filePath, String password) {
        mZipFilePath = filePath;
        mPassword = password;
        loadFiles();
    }

    /**
     * 加载文件内容列表
     */
    private void loadFiles() {
        mTask = new LoadZipInnerFilesTask();
        mTask.setListener(this);
        mTask.execute(mZipFilePath, mRootDir, mPassword);
    }

    @Override
    public void onPreLoad() {
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
            mView.updateListData(result);
            mView.hideProgressDialog();
        } else {
            onLoadError();
        }
        Log.e("task", "加载完成");
    }

    @Override
    public void onCanceled() {
        Log.e("task", "任务取消");
        // 路径出栈
        mRootDir = mPathStack.pop();
        mView.navigationBackward(false);
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
        mTask.cancel(true);
    }

    @Override
    public void onBreadcrumbClick(String path) {
        if (!mRootDir.equals(path)) {
            // 移除当前路径以及后面的路径堆栈
            int index = mPathStack.indexOf(path);
            if (index != -1) {
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
            mPathStack.push(mRootDir);
            mRootDir = item.getFullPath();
            mView.navigationForward(mRootDir);
            loadFiles();
        } else {
            mView.showToast("click file");
        }
    }

    @Override
    public void onBackPressed() {
        if (!mPathStack.isEmpty()) {
            // 路径出栈
            mRootDir = mPathStack.pop();
            mView.navigationBackward(false);
            loadFiles();
        } else {
            mView.navigationBackward(true);
        }
    }

    @Override
    public void onExtractFiles() {

    }

}

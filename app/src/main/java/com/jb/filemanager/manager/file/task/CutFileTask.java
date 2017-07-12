package com.jb.filemanager.manager.file.task;


import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.Logger;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class CutFileTask {

    private static final String LOG_TAG = "CutFileTask";

    public static final int CUT_SUCCESS_START = 0;
    public static final int CUT_ERROR_UNKNOWN = -1;
    public static final int CUT_ERROR_NOT_ENOUGH_SPACE = -2;

    private Thread mWorkerThread;
    private Listener mListener;

    private ArrayList<File> mSource;
    private String mDest;
    private final Object mLocker = new Object();
    private boolean mIsSkip = false;
    private boolean mIsStop = false;
    private boolean mIsApplyToAll = false;

    public CutFileTask(ArrayList<File> source, String dest, Listener listener) {

        mListener = listener;
        mSource = new ArrayList<> (source);
        mDest = dest;
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                if (mSource != null && mSource.size() > 0 && !TextUtils.isEmpty(mDest)) {
                    result = true;
                    for (final File file : mSource) {
                        TheApplication.postRunOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) {
                                    mListener.onProgressUpdate(file);
                                }
                            }
                        });

                        if (mDest.startsWith(file.getAbsolutePath())) {
                            Logger.i(LOG_TAG, "发现子路径:" + file.getAbsolutePath() + "  " + mDest);
                            try {
                                Logger.i(LOG_TAG, "暂停询问处理方式:" + file.getAbsolutePath());
                                synchronized (mLocker) {
                                    TheApplication.postRunOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mListener != null) {
                                                mListener.onSubFolderCopy(CutFileTask.this, file, mDest);
                                            }
                                        }
                                    });
                                    mLocker.wait();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (mIsStop) {
                            Logger.i(LOG_TAG, "停止");
                            break;
                        }

                        File test = new File(mDest + File.separator + file.getName());
                        if (test.exists() && ((test.isDirectory() && file.isDirectory()) || ((test.isFile() && file.isFile())))) {
                            Logger.i(LOG_TAG, "发现重名文件:" + file.getAbsolutePath());
                            if (!mIsApplyToAll) {
                                try {
                                    Logger.i(LOG_TAG, "暂停询问处理方式:" + file.getAbsolutePath());
                                    synchronized (mLocker) {
                                        TheApplication.postRunOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mListener != null) {
                                                    mListener.onDuplicate(CutFileTask.this, file, mSource);
                                                }
                                            }
                                        });
                                        mLocker.wait();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Logger.i(LOG_TAG, "应用之前的选择:" + file.getAbsolutePath());
                            }
                        }

                        if (!mIsSkip) {
                            Logger.i(LOG_TAG, "覆盖:" + file.getAbsolutePath());
                            result = result && file.renameTo(new File(mDest + File.separator + file.getName()));
                        } else {
                            Logger.i(LOG_TAG, "跳过:" + file.getAbsolutePath());
                        }
                    }
                }

                final boolean finalResult = result;
                TheApplication.postRunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onPostExecute(finalResult);
                        }
                    }
                });
            }
        });
    }

    public int start() {
        int resultCode = CUT_SUCCESS_START;
        if (mWorkerThread != null) {
            int checkResult = FileUtil.checkCanPaste(mSource, mDest);

            switch (checkResult) {
                case FileUtil.PASTE_CHECK_SUCCESS:
                    mWorkerThread.start();
                    break;
                case FileUtil.PASTE_CHECK_ERROR_UNKNOWN:
                    resultCode = CUT_ERROR_UNKNOWN;
                    break;
                case FileUtil.PASTE_CHECK_ERROR_NOT_ENOUGH_SPACE:
                    resultCode = CUT_ERROR_NOT_ENOUGH_SPACE;
                    break;
                default:
                    break;
            }

        }
        return resultCode;
    }

    public void continueCut(boolean isSkip, Boolean isApplyToAll) {
        synchronized (mLocker) {
            mIsSkip = isSkip;

            if (isApplyToAll != null) {
                mIsApplyToAll = isApplyToAll;
            }

            mLocker.notify();
        }
    }

    public void stopCut() {
        mIsStop = true;
        mLocker.notify();
    }

    public interface Listener {
        void onSubFolderCopy(CutFileTask task, File file, String dest);
        void onDuplicate(CutFileTask task, File file, ArrayList<File> cutSource);
        void onProgressUpdate(File file);
        void onPostExecute(Boolean aBoolean);
    }
}

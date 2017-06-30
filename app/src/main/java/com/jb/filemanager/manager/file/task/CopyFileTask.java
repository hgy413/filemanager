package com.jb.filemanager.manager.file.task;

import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.images.AsyncTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class CopyFileTask {

    private Thread mWorkerThread;
    private Listener mListener;

    private ArrayList<File> mSource;
    private String mDest;

    public CopyFileTask(ArrayList<File> source, String dest, Listener listener) {

        mListener = listener;
        mSource = new ArrayList<> (source);
        mDest = dest;
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                if (mSource != null && mSource.size() > 0) {
                    if (FileUtil.checkCanPaste(mSource, mDest)) {
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
                            result = result && FileUtil.copyFileOrDirectory(file.getAbsolutePath(), mDest);

                            // TODO wait for handle duplicated
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

    public void start() {
        if (mWorkerThread != null) {
            mWorkerThread.start();
        }
    }

    public interface Listener {
        void onProgressUpdate(File file);
        void onPreExecute();
        void onPostExecute(Boolean aBoolean);
    }
}

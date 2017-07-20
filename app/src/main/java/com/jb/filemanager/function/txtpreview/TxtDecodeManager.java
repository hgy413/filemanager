package com.jb.filemanager.function.txtpreview;

import android.text.TextUtils;

import com.jb.filemanager.TheApplication;
import com.jb.filemanager.os.ZAsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/19 19:44
 */

class TxtDecodeManager {
    private final TxtLoadTask mTxtLoadTask;
    private OnTxtLoadListener mTxtLoadListener;

    public TxtDecodeManager() {
        mTxtLoadTask = new TxtLoadTask();
    }

    private ArrayList<String> readTxt(String path) {
        ArrayList<String> resultList = new ArrayList();
        if (TextUtils.isEmpty(path)) {
            return resultList;
        }
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            File urlFile = new File(path);
            isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            br = new BufferedReader(isr);

            String mimeTypeLine;

            int lineNumber = 0;
            while ((mimeTypeLine = br.readLine()) != null) {
                stringBuilder.append(mimeTypeLine);
                lineNumber++;
                if (lineNumber % 10 == 0) {//每段10行
                    resultList.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();

            TheApplication.postRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTxtLoadListener != null) {
                        mTxtLoadListener.onLoadError(e.getMessage());
                    }
                }
            });

        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }

    public void LoadTxtPath(String path) {
        mTxtLoadTask.executeOnExecutor(ZAsyncTask.THREAD_POOL_EXECUTOR, path);
    }

    public void setTxtLoadListener(OnTxtLoadListener txtLoadListener) {
        mTxtLoadListener = txtLoadListener;
    }

    public interface OnTxtLoadListener {
        void onLoadStart();

        void onLoadComplete(ArrayList<String> result);

        void onLoadError(String msg);
    }

    private class TxtLoadTask extends ZAsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TheApplication.postRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTxtLoadListener != null) {
                        mTxtLoadListener.onLoadStart();
                    }
                }
            });
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            String param = params[0];
            return readTxt(param);
        }

        @Override
        protected void onPostExecute(final ArrayList<String> s) {
            super.onPostExecute(s);
            if (mTxtLoadListener != null) {
                        mTxtLoadListener.onLoadComplete(s);
                    }

        }
    }

    public void cancelTask() {
        if (!mTxtLoadTask.isCancelled()) {
            mTxtLoadTask.cancel(true);
        }
    }
}

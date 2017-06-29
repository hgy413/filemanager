package com.jb.filemanager.manager.file.task;

import android.text.TextUtils;

import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.images.AsyncTask;

import java.io.File;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class CutFileTask extends AsyncTask<PasteFileParam, File, Boolean> {

    private Listener mListener;

    public CutFileTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(PasteFileParam... params) {
        boolean result = false;
        if (params != null && params.length == 1) {
            PasteFileParam param = params[0];
            if (param != null
                    && !TextUtils.isEmpty(param.mDestDir)
                    && param.mSourceFiles != null
                    && param.mSourceFiles.size() > 0) {
                result = true;
                if (FileUtil.checkCanPaste(param.mSourceFiles, param.mDestDir)) {
                    for (File file : param.mSourceFiles) {
                        publishProgress(file);
                        result = result && file.renameTo(new File(param.mDestDir + File.separator + file.getName()));
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (mListener != null) {
            mListener.onPostExecute(aBoolean);
        }
    }

    @Override
    protected void onProgressUpdate(File... values) {
        if (mListener != null && values != null && values.length > 0) {
            mListener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null) {
            mListener.onPreExecute();
        }
    }

    public interface Listener {
        void onProgressUpdate(File file);
        void onPreExecute();
        void onPostExecute(Boolean aBoolean);
    }
}

package com.jb.filemanager.function.zipfile.task;

import android.os.AsyncTask;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;
import com.jb.filemanager.function.zipfile.listener.LoadZipInnerFilesListener;
import com.jb.filemanager.function.zipfile.util.ZipUtils;

import java.io.File;
import java.util.List;

/**
 * Created by xiaoyu on 2017/7/3 16:21.
 */

public class LoadZipInnerFilesTask extends AsyncTask<String, Integer, List<ZipPreviewFileBean>> {

    private LoadZipInnerFilesListener mListener = null;

    public void setListener(LoadZipInnerFilesListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onPreLoad();
        }
    }

    /**
     * @param params 0为压缩文件路径, 1为获取文件的指定路径, 2为密码(无密码为null或""空字符串)
     * @return result
     */
    @Override
    protected List<ZipPreviewFileBean> doInBackground(String... params) {
        return ZipUtils.listFiles(new File(params[0]), params[1], params[2]);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null) {
            mListener.onLoading(values[0]);
        }
    }

    @Override
    protected void onPostExecute(List<ZipPreviewFileBean> result) {
        if (mListener != null) {
            mListener.onPosLoad(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mListener != null) {
            mListener.onCanceled();
        }
    }
}

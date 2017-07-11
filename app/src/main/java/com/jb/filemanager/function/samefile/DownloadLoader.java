package com.jb.filemanager.function.samefile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by bool on 17-7-4.
 * 下载文件
 */

public class DownloadLoader extends AsyncTaskLoader {
    SameFileContract.Support mSupport;

    public DownloadLoader(Context context, @NonNull SameFileContract.Support mSupport) {
        super(context);
        this.mSupport = mSupport;
    }

    @Override
    public GroupList<String,FileInfo> loadInBackground() {
        return mSupport.getAllDownloadInfo();
    }
}

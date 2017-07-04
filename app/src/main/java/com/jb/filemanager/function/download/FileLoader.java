package com.jb.filemanager.function.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-7-4.
 * 下载文件
 */

public class FileLoader extends AsyncTaskLoader {
    DownloadSupport mSupport;

    public FileLoader(Context context, @NonNull DownloadSupport mSupport) {
        super(context);
        this.mSupport = checkNotNull(mSupport);
    }

    @Override
    public Object loadInBackground() {
        return mSupport.getAllDownloadInfo();
    }
}

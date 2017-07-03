package com.jb.filemanager.function.zipfile;

import com.jb.filemanager.function.zipfile.bean.ZipPreviewFileBean;

import java.util.List;

/**
 * Created by xiaoyu on 2017/7/3 16:15.
 */

public interface LoadZipInnerFilesListener {
    void onPreLoad();

    void onLoading(int value);

    void onPosLoad(List<ZipPreviewFileBean> result);

    void onCanceled();
}

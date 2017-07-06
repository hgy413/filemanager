package com.jb.filemanager.function.search.event;

import com.jb.filemanager.function.search.modle.FileInfo;

import java.util.ArrayList;

/**
 * Created by nieyh on 17-7-5.
 * 搜索完成事件
 */

public class SearchFinishEvent {

    public ArrayList<FileInfo> mFileInfoList;

    public SearchFinishEvent(ArrayList<FileInfo> fileInfoList) {
        mFileInfoList = fileInfoList;
    }
}

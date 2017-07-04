package com.jb.filemanager.function.download;

import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.function.musics.MusicInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by bool on 17-7-1.
 * 下载内容扫描
 */

public class DownloadContract {
    interface View {
    }

    interface Presenter {
        void onClickBackButton(boolean b);

        void start();
    }

    interface Support {

        GroupList<String, MusicInfo> getAllDownloadInfo();
    }
}

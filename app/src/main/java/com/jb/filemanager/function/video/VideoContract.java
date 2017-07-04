package com.jb.filemanager.function.video;

import android.content.Intent;

import com.jb.filemanager.function.musics.GroupList;
import com.jb.filemanager.function.musics.MusicInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bool on 17-7-4.
 * 接口管理
 */

public class VideoContract {
    interface View {
        void showVideoList(GroupList<String, MusicInfo> mMusicMaps);
    }

    interface Presenter {
        void onCreate(Intent intent);

        void onClickBackButton(boolean b);

        void start();
    }

    interface Support {
        Map<String, ArrayList<MusicInfo>> getAllVideoInfo();
    }
}

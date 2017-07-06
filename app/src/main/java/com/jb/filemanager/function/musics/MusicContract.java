package com.jb.filemanager.function.musics;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bool on 17-6-30.
 */

public interface MusicContract {
    interface View {
        void showMusicList(GroupList<String, MusicInfo> mMusicMaps);
    }

    interface Presenter {
        void onCreate(Intent intent);

        void onClickBackButton(boolean b);

        void start();
    }

    interface Support {
        Map<String, ArrayList<MusicInfo>> getAllMusicInfo();

        int getMuscisNum();

        void delete(ArrayList<String> fullPathList);
    }
}

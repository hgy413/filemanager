package com.jb.filemanager.function.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public interface MusicContract {
    interface View {

        void finishActivity();
        void updateView(List<MusicInfo> list);
    }

    interface Presenter {
        void start();

        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();

        boolean isSelected(File file);
        void addOrRemoveSelected(File file);
    }

    interface Support {
        Context getContext();
        Application getApplication();

        List<MusicInfo> getAllMusic();
    }
}

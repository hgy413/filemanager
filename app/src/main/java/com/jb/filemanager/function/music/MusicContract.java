package com.jb.filemanager.function.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.io.File;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicContract {
    interface View {
        void finishActivity();

        void updateView();
    }

    interface Presenter {
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
    }
}

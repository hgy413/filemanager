package com.jb.filemanager.function.image;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/27.
 */

public class ImageContract {

    interface View {
        void finishActivity();
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();
    }

    interface Support {
        Context getContext();
        Application getApplication();
    }
}

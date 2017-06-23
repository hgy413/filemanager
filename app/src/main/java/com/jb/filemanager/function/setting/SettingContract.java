package com.jb.filemanager.function.setting;

import android.content.Context;
import android.content.Intent;

/**
 * Created by miwo on 2016/9/2.
 *
 */
public class SettingContract {
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

    }
}

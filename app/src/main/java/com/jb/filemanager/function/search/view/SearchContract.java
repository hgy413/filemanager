package com.jb.filemanager.function.search.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jb.filemanager.function.search.modle.FileInfo;

import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/20.
 *
 */

public class SearchContract {
    interface View {
        void showInputEmptyTips();
        void showSearchAnim();
        void stopSearchAnim();
        void showSearchResult(ArrayList<FileInfo> fileInfoList);

        void showKeyboard();
        void hideKeyboard();
        void clearInput();

        void finishActivity();

        void goToActivity(Intent intent);
    }

    interface Presenter {
        void onCreate(Intent intent);
        void onResume();
        void onPause();
        void onDestroy();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onClickBackButton(boolean systemBack);
        void onPressHomeKey();

        void onClickMask();
        void onClickClearInputButton();
        void onClickSearch(String keyword);
        void onClickSearchOnKeyboard(String keyword);
        void onClickSearchResult(Activity activity, String clickedFilePath);

        void onAnimRepeat();
    }

    interface Support {
        Context getContext();
        Application getApplication();
    }
}

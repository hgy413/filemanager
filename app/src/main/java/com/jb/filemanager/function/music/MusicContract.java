package com.jb.filemanager.function.music;


import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;

import java.io.File;

/**
 * Created by bill wang on 2017/6/28.
 *
 */

public class MusicContract {
    interface View {
        void finishActivity();

        void updateView();

        void changeCursor(MatrixCursor cursor);

        Cursor queryData(long start, long end);
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

        void restartLoader();

        Cursor queryDate(long start, long end);
    }

    interface Support {
        Loader<Cursor> getAllMusicCursor();

        Cursor queryData(long start, long end);
    }
}

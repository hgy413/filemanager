package com.jb.filemanager.function.recent;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by xiaoyu on 2017/7/14 18:14.
 */

public class RecentFileObserver extends FileObserver {

    public RecentFileObserver(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
        Log.e("observer", "enter all events" + path);

        switch (event) {
            case FileObserver.ALL_EVENTS:
                Log.e("observer", "all events");
                break;
        }
    }
}

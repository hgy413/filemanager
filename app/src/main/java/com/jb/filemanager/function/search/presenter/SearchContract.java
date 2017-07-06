package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.widget.EditText;

import com.jb.filemanager.function.search.modle.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyh on 17-7-6.
 */

public interface SearchContract {

    interface View {
        void showLoading();
        void dissmissLoading();
        void showResult(ArrayList<FileInfo> fileInfoList);
        void tipInputEmpty();
    }

    interface Presenter {
        void onViewCreated(EditText editText);
        void search(String input, Activity activity);
        void release();
    }

    interface Support {
        void showSoftInput(EditText editText);
        void hideSoftInput(Activity activity);
        void releaseView();
    }
}

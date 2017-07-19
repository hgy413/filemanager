package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.view.View;
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
        void showResult(ArrayList<FileInfo> fileInfoList);
        void tipInputEmpty();

        void clearInput();
        void finishActivity();
    }

    interface Presenter {
        void onViewCreated();
        void search(String input, android.view.View editor);
        void release();

        void onClickBackButton(boolean systemBack);
        void onCLickClearInputButton();
    }

    interface Support {
        void hideSoftInput(android.view.View activity);
        void releaseView();
    }
}

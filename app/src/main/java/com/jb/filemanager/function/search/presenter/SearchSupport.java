package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jb.filemanager.TheApplication;

/**
 * Created by nieyh on 17-7-6.
 *
 */

public class SearchSupport implements SearchContract.Support {

    @Override
    public void releaseView() {
        //释放视图
        // TODO: 17-7-6 防止内存泄露
    }

}

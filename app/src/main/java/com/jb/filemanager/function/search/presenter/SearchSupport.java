package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jb.filemanager.TheApplication;
import com.jb.ga0.commerce.util.topApp.TopHelper;

/**
 * Created by nieyh on 17-7-6.
 *
 */

public class SearchSupport implements SearchContract.Support {

    @Override
    public void hideSoftInput(View view) {
        //隐藏键盘
        if (view == null) {
            return;
        }
        if (null != view.getWindowToken()) {
            InputMethodManager imm = (InputMethodManager) TheApplication.getInstance().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void releaseView() {
        //释放视图
        // TODO: 17-7-6 防止内存泄露
    }

}

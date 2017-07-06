package com.jb.filemanager.function.search.presenter;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jb.filemanager.TheApplication;

/**
 * Created by nieyh on 17-7-6.
 */

public class SearchSupport implements SearchContract.Support {

    @Override
    public void showSoftInput(EditText editText) {
        if (editText == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) TheApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void hideSoftInput(Activity activity) {
        //隐藏键盘
        if (activity == null) {
            return;
        }
        if (null != activity.getCurrentFocus() && null != activity.getCurrentFocus().getWindowToken()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void releaseView() {
        //释放视图
        // TODO: 17-7-6 防止内存泄露
    }

}

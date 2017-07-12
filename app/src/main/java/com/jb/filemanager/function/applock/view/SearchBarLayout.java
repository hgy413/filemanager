package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;

import java.lang.ref.WeakReference;

/**
 * Created by nieyh on 2016/12/27. <br>
 */

public class SearchBarLayout extends RelativeLayout implements /*TextWatcher, */View.OnClickListener {

    private EditText mSearchEdit;

    private ImageView mSearchBack;

    private View mBottomLine;

    //是否处于搜索状态
    private boolean isOpenSearch;

    private OnSearchEvtLisenter mOnSearchEvtLisenter;

    private final String TAG = "SearchBarLayout";

    private boolean isRelease = false;

    private SearchBarTextWatcher mSearchBarTextWatcher;

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化视图
     * */
    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        layoutInflater.inflate(R.layout.view_search_bar, this);
        mSearchEdit = (EditText) findViewById(R.id.view_search_bar_edit);
        mSearchBack = (ImageView) findViewById(R.id.view_search_bar_back);
        mBottomLine = findViewById(R.id.view_search_bar_bottom_line);
        mSearchBack.setOnClickListener(this);
        isOpenSearch = false;
        setVisibility(GONE);
    }

    /**
     * 打开搜索框
     * */
    public void safeToSlideOpen() {
        isOpenSearch = true;
        //显示搜索框
        mSearchEdit.requestFocus();
        //打开键盘
        AppUtils.showSoftInputFromWindow(mSearchEdit.getContext(), mSearchEdit);
        // TODO: 17-7-11 增加展示动画
        setVisibility(VISIBLE);
        if (mOnSearchEvtLisenter != null) {
            mOnSearchEvtLisenter.onShow();
        }
    }

    /**
     * 关闭搜索框
     * */
    public boolean safeToSlideClose() {
        if (isOpenSearch) {
            isOpenSearch = false;
            //显示搜索框
            mSearchEdit.setText("");
            mSearchEdit.clearFocus();
            //关闭软键盘
            AppUtils.hideSoftInputFromWindow(mSearchEdit.getContext(), mSearchEdit);
            // TODO: 17-7-11 增加隐藏动画
            setVisibility(GONE);
            if (mOnSearchEvtLisenter != null) {
                mOnSearchEvtLisenter.dismiss();
            }
            return true;
        }
        return false;
    }

    /**
     * 释放内存
     * */
    public void release(Context context) {
        if (!isRelease) {
            isRelease = true;
            AppUtils.fixInputMethodManagerLeak(context);
            if (mSearchEdit != null && mSearchBarTextWatcher != null) {
                //清除掉监听器
                mSearchEdit.removeTextChangedListener(mSearchBarTextWatcher);
            }
            mOnSearchEvtLisenter = null;
        }
    }

    /**
     * 设置监听器
     * */
    public void setOnSearchActionLisenter(OnSearchEvtLisenter OnSearchEvtLisenter) {
        mOnSearchEvtLisenter = OnSearchEvtLisenter;
        mSearchBarTextWatcher = new SearchBarTextWatcher(mOnSearchEvtLisenter);
        mSearchEdit.addTextChangedListener(mSearchBarTextWatcher);
    }

    @Override
    public void onClick(View v) {
        safeToSlideClose();
    }


    public interface OnSearchEvtLisenter {
        void searchTxtChange(Editable editable);
        void dismiss();
        void onShow();
    }

    /**
     * 防止泄漏
     * */
    private static class SearchBarTextWatcher implements TextWatcher {
        WeakReference<OnSearchEvtLisenter> mOnSearchEvtLisenterWeakReference;

        public SearchBarTextWatcher(OnSearchEvtLisenter onSearchEvtLisenterWeakReference) {
            this.mOnSearchEvtLisenterWeakReference = new WeakReference<>(onSearchEvtLisenterWeakReference);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            OnSearchEvtLisenter onSearchEvtLisenter = mOnSearchEvtLisenterWeakReference.get();
            if (onSearchEvtLisenter != null) {
                onSearchEvtLisenter.searchTxtChange(s);
            }
        }
    }
}

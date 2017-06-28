package com.jb.filemanager.function.applock.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;

import java.lang.ref.WeakReference;

/**
 * Created by nieyh on 2016/12/27. <br>
 */

public class SearchBarLayout extends LinearLayout implements /*TextWatcher, */View.OnClickListener {

    private EditText mSearchEdit;

    private ImageView mSearchIcon;

//    private View mEditLayout;

    private View mBottomLine;

    private SparseIntArray mViewStateSparseArray;
    //是否处于搜索状态
    private boolean isOpenSearch;

    private OnSearchEvtLisenter mOnSearchEvtLisenter;

    private final String TAG = "SearchBarLayout";

    private boolean isRelease = false;

    private SearchBarTextWatcher mSearchBarTextWatcher;

    public SearchBarLayout(Context context) {
        super(context);
        initView();
    }

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化视图
     * */
    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        View parent = layoutInflater.inflate(R.layout.view_search_bar, this);
        mSearchEdit = (EditText) parent.findViewById(R.id.view_search_bar_edit);
        mSearchIcon = (ImageView) parent.findViewById(R.id.view_search_bar_search);
        mBottomLine = parent.findViewById(R.id.view_search_bar_bottom_line);
//        mEditLayout = parent.findViewById(R.id.view_search_bar_edit_layout);
        //默认情况下为隐藏搜索框
//        mEditLayout.setVisibility(GONE);
        mSearchEdit.setVisibility(INVISIBLE);
        mBottomLine.setVisibility(GONE);
        mSearchIcon.setOnClickListener(this);
        isOpenSearch = false;
    }

    /**
     * 打开搜索框
     * */
    private void toSlideSearch() {
        isOpenSearch = true;
        //显示搜索框
        mSearchEdit.setVisibility(VISIBLE);
        mBottomLine.setVisibility(VISIBLE);
        mSearchEdit.requestFocus();
        //打开键盘
        AppUtils.showSoftInputFromWindow(mSearchEdit.getContext(), mSearchEdit);
        //隐藏其他控件
        ViewParent viewGroup = getParent();
        if (viewGroup != null) {
            ViewGroup parent = (ViewGroup) viewGroup;
            int childNum = parent.getChildCount();
            if (mViewStateSparseArray == null) {
                mViewStateSparseArray = new SparseIntArray(childNum);
            }
            for (int i = 0 ; i < childNum ; i ++) {
                View child = parent.getChildAt(i);
                if (!child.equals(this)) {
                    //将其他的布局的显示状态使用一个队列保存起来
                    mViewStateSparseArray.put(i, child.getVisibility());
                    //隐藏之前的其他的布局
                    child.setVisibility(GONE);
                }
            }
            ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
            layoutParams.width = parent.getWidth();
            this.setLayoutParams(layoutParams);
        }
    }

    /**
     * 关闭搜索框
     * */
    public boolean safeToSlideClose() {
        if (isOpenSearch) {
            isOpenSearch = false;
            //显示搜索框
            mSearchEdit.setVisibility(INVISIBLE);
            mSearchEdit.setText("");
            mBottomLine.setVisibility(GONE);
            mSearchEdit.clearFocus();
            //关闭软键盘
            AppUtils.hideSoftInputFromWindow(mSearchEdit.getContext(), mSearchEdit);
            //隐藏其他控件
            ViewParent viewGroup = getParent();
            if (viewGroup != null) {
                ViewGroup parent = (ViewGroup) viewGroup;
                int childNum = parent.getChildCount();
                if (mViewStateSparseArray == null) {
                    mViewStateSparseArray = new SparseIntArray(childNum);
                }
                for (int i = 0; i < childNum; i++) {
                    View child = parent.getChildAt(i);
                    if (!child.equals(this)) {
                        //隐藏之前的其他的布局
                        switch (mViewStateSparseArray.get(i)) {
                            case VISIBLE:
                                child.setVisibility(VISIBLE);
                                break;
                            case INVISIBLE:
                                child.setVisibility(INVISIBLE);
                                break;
                            case GONE:
                                child.setVisibility(GONE);
                                break;
                        }

                    }
                }
                ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                this.setLayoutParams(layoutParams);
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
        }
    }

    /**
     * 设置监听器
     * */
    public void setOnSearchTxtChgLisenter(OnSearchEvtLisenter OnSearchEvtLisenter) {
        mOnSearchEvtLisenter = OnSearchEvtLisenter;
        mSearchBarTextWatcher = new SearchBarTextWatcher(mOnSearchEvtLisenter);
        mSearchEdit.addTextChangedListener(mSearchBarTextWatcher);
    }

    @Override
    public void onClick(View v) {
        if (!isOpenSearch) {
            toSlideSearch();
            if (mOnSearchEvtLisenter != null) {
                mOnSearchEvtLisenter.searchOnclick();
            }
        }
    }


    public interface OnSearchEvtLisenter {
        void searchTxtChange(Editable editable);
        void searchOnclick();
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

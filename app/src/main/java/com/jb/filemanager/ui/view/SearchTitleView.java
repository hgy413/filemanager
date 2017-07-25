package com.jb.filemanager.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * Created by xiaoyu on 2017/7/10 14:55.
 * <p>
 * 带搜索按钮的标题栏(搜索按钮也可隐藏)
 * </p>
 */

public class SearchTitleView extends RelativeLayout implements View.OnClickListener {

    private TextView mTvTitleName;
    private ImageView mIvSearchIcon;
    private TextView mTvSelectCount;
    private ImageView mIvSelectBtn;
    private SearchTitleViewCallback mCallback;
    private boolean mIsNeedSearchBtnShow = true;

    public SearchTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_search_title, this);
        initializeView();
    }

    private void initializeView() {
        setBackgroundColor(Color.WHITE);
        mTvTitleName = (TextView) findViewById(R.id.search_title_name);
        mTvTitleName.setOnClickListener(this);
        mIvSearchIcon = (ImageView) findViewById(R.id.search_title_search_icon);
        mIvSearchIcon.setOnClickListener(this);
        mTvSelectCount = (TextView) findViewById(R.id.search_title_tv_select_count);
        mTvSelectCount.setOnClickListener(this);
        mIvSelectBtn = (ImageView) findViewById(R.id.search_title_select_btn);
        mIvSelectBtn.setOnClickListener(this);
    }

    public void setTitleName(CharSequence name) {
        if (mTvTitleName != null) {
            mTvTitleName.setText(name);
        }
    }

    public void setSearchIconVisibility(boolean isNeedShow) {
        mIsNeedSearchBtnShow = isNeedShow;
        if (mIvSearchIcon != null) {
            mIvSearchIcon.setVisibility(mIsNeedSearchBtnShow ? VISIBLE : GONE);
        }
    }

    public void setSelectBtnResId(int state) {
        if (mIvSelectBtn != null) {
            switch (state) {
                case 0:
                    mIvSelectBtn.setImageResource(R.drawable.choose_none_black);
                    break;
                case 1:
                    mIvSelectBtn.setImageResource(R.drawable.choose_none_black);
                    break;
                case 2:
                    mIvSelectBtn.setImageResource(R.drawable.choose_all);
                    break;
            }
        }
    }

    public void setSelectedCount(int count) {
        if (mTvSelectCount != null) {
            mTvSelectCount.setText(getContext().getString(R.string.file_selected, count));
        }
    }

    public void setClickCallBack(SearchTitleViewCallback callBack) {
        mCallback = callBack;
    }

    public void switchTitleMode(boolean isToSelectMode) {
        if (isToSelectMode) {
            mTvTitleName.setVisibility(GONE);
            mIvSearchIcon.setVisibility(GONE);
            mTvSelectCount.setVisibility(VISIBLE);
            mIvSelectBtn.setVisibility(VISIBLE);
        } else {
            mTvTitleName.setVisibility(VISIBLE);
            mIvSearchIcon.setVisibility(mIsNeedSearchBtnShow ? VISIBLE : GONE);
            mTvSelectCount.setVisibility(GONE);
            mIvSelectBtn.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_title_name:
                if (mCallback != null) {
                    mCallback.onIvBackClick();
                }
                break;
            case R.id.search_title_search_icon:
                if (mCallback != null) {
                    mCallback.onSearchClick();
                }
                break;
            case R.id.search_title_tv_select_count:
                switchTitleMode(false);
                if (mCallback != null) {
                    mCallback.onIvCancelSelectClick();
                }
                break;
            case R.id.search_title_select_btn:
                if (mCallback != null) {
                    mCallback.onSelectBtnClick();
                }
                break;
        }
    }
    /**
     * 建议使用{@link #setClickCallBack(SearchTitleViewCallback)}
     *
     * @param listener l
     * @deprecated
     */
    public void setOnBackClickListener(View.OnClickListener listener) {
        if (mTvTitleName != null) {
            mTvTitleName.setOnClickListener(listener);
        }
    }

    /**
     * 建议使用{@link #setClickCallBack(SearchTitleViewCallback)}
     *
     * @param listener l
     * @deprecated
     */
    public void setOnSearchClickListener(View.OnClickListener listener) {
        if (mIvSearchIcon != null) {
            mIvSearchIcon.setOnClickListener(listener);
        }
    }

}

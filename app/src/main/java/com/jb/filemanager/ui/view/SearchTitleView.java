package com.jb.filemanager.ui.view;

import android.content.Context;
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
 *     带搜索按钮的标题栏(搜索按钮也可隐藏)
 * </p>
 */

public class SearchTitleView extends RelativeLayout {

    private TextView mTvTitleName;
    private ImageView mIvSearchIcon;

    public SearchTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_search_title, this);
        initializeView();
    }

    private void initializeView() {
        mTvTitleName = (TextView) findViewById(R.id.search_title_name);
        mIvSearchIcon = (ImageView) findViewById(R.id.search_title_search_icon);
    }

    public void setTitleName(CharSequence name) {
        if (mTvTitleName != null) {
            mTvTitleName.setText(name);
        }
    }

    public void setSearchIconVisibility(boolean isShow) {
        if (mIvSearchIcon != null) {
            mIvSearchIcon.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    public void setOnBackClickListener(View.OnClickListener listener) {
        if (mTvTitleName != null) {
            mTvTitleName.setOnClickListener(listener);
        }
    }

    public void setOnSearchClickListener(View.OnClickListener listener) {
        if (mIvSearchIcon != null) {
            mIvSearchIcon.setOnClickListener(listener);
        }
    }
}

package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jb.filemanager.R;
import static com.squareup.haha.guava.base.Joiner.checkNotNull;

/**
 * Created by bool on 17-7-5.
 * 底部功能栏
 */

public class BottomOperateBar extends LinearLayout {
    private Context mContext;
    private TextView mTvCut, mTvCopy, mTvDelete, mTvMore; // 四个按钮
    public BottomOperateBar(Context context) {
        this(context, null, 0);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.common_operate_bar, this, true);
        setVisibility(View.GONE);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.common_operate_bar, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTvCut = (TextView) findViewById(R.id.tv_common_operate_bar_cut);
        mTvCut.getPaint().setAntiAlias(true);

        mTvCopy = (TextView) findViewById(R.id.tv_common_operate_bar_copy);
        mTvCopy.getPaint().setAntiAlias(true);

        mTvDelete = (TextView) findViewById(R.id.tv_common_operate_bar_delete);
        mTvDelete.getPaint().setAntiAlias(true);

        mTvMore = (TextView) findViewById(R.id.tv_common_operate_bar_more);
        mTvMore.getPaint().setAntiAlias(true);
    }

    public BottomOperateBar setClickListener(@NonNull View.OnClickListener listener) {
        checkNotNull(listener);
        mTvCopy.setOnClickListener(listener);
        mTvCopy.setOnClickListener(listener);
        mTvDelete.setOnClickListener(listener);
        mTvMore.setOnClickListener(listener);
        return this;
    }
}

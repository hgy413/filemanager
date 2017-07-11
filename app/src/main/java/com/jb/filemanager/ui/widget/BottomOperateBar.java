package com.jb.filemanager.ui.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jb.filemanager.R;
import com.jb.filemanager.util.DensityUtil;


/**
 * Created by bool on 17-7-5.
 * 底部功能栏
 */

public class BottomOperateBar extends LinearLayout implements View.OnClickListener{

    private Context mContext;
    private TextView mTvCut, mTvCopy, mTvDelete, mTvMore; // 四个按钮
    private OnBottomClicked mBottomClicked;
    public BottomOperateBar(Context context) {
        this(context, null, 0);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        //mContext = context;
        //LayoutInflater.from(mContext).inflate(R.layout.common_operate_bar, this, true);
        //setVisibility(View.GONE);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.common_operate_bar, this, true);
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //此处相当于布局文件中的Android:layout_gravity属性
        layoutParams.gravity = Gravity.BOTTOM;
        setLayoutParams(layoutParams);
        setBackgroundColor(getResources().getColor(R.color.dark_gray));
        setVisibility(View.GONE);

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

        mTvCut.setOnClickListener(this);
        mTvCopy.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);
        mTvMore.setOnClickListener(this);
    }

    public BottomOperateBar onClickedAction(@NonNull OnBottomClicked clickedAction) {
        mBottomClicked = clickedAction;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (mBottomClicked == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_common_operate_bar_cut:
                mBottomClicked.onCutClicked();
                break;
            case R.id.tv_common_operate_bar_copy:
                mBottomClicked.onCopyClicked();
                break;
            case R.id.tv_common_operate_bar_delete:
                mBottomClicked.onDeleteClicked();
                break;
            case R.id.tv_common_operate_bar_more:
                mBottomClicked.onMoreClicked();
                break;
        }
    }

    public interface OnBottomClicked{
        void onCutClicked();
        void onCopyClicked();
        void onDeleteClicked();
        void onMoreClicked();
    }
}

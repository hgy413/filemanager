package com.jb.filemanager.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.jb.filemanager.R;

/**
 * Created by bool on 17-7-5.
 * 底部功能栏
 */

public class BottomOperateBar extends LinearLayout implements View.OnClickListener{

    private Context mContext;
    private LinearLayout mLlCut, mLlCopy, mLlDelete, mLlMore; // 四个按钮
    private OnBottomClicked mBottomClicked;
    public BottomOperateBar(Context context) {
        this(context, null, 0);
    }

    public BottomOperateBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
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
        mLlCut = (LinearLayout) findViewById(R.id.ll_common_operate_bar_cut);
        //mLlCut.getPaint().setAntiAlias(true);

        mLlCopy = (LinearLayout) findViewById(R.id.ll_common_operate_bar_copy);
        //mLlCopy.getPaint().setAntiAlias(true);

        mLlDelete = (LinearLayout) findViewById(R.id.ll_common_operate_bar_delete);
        //mLlDelete.getPaint().setAntiAlias(true);

        mLlMore = (LinearLayout) findViewById(R.id.ll_common_operate_bar_more);
        //mLlMore.getPaint().setAntiAlias(true);

        mLlCut.setOnClickListener(this);
        mLlCopy.setOnClickListener(this);
        mLlDelete.setOnClickListener(this);
        mLlMore.setOnClickListener(this);
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
            case R.id.ll_common_operate_bar_cut:
                mBottomClicked.onCutClicked();
                break;
            case R.id.ll_common_operate_bar_copy:
                mBottomClicked.onCopyClicked();
                break;
            case R.id.ll_common_operate_bar_delete:
                mBottomClicked.onDeleteClicked();
                break;
            case R.id.ll_common_operate_bar_more:
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

package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Created by nieyh on 2017/1/18.
 * common verify dialog
 */

public abstract class CommonVerifyDialog extends BaseDialog {

    private TextView mTitle;
    protected TextView mHalfSure;
    protected TextView mSure;
    protected TextView mHalfCancel;
    private FrameLayout mMiddleView;
    private QuickClickGuard mQuickClickGuard;

    public CommonVerifyDialog(Activity act) {
        super(act, true);
        mQuickClickGuard = new QuickClickGuard();
        initView();
        setSize(WindowManager.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, getContext().getResources().getDisplayMetrics()));
    }

    private void initView() {
        setContentView(R.layout.dialog_verify_select);
        mTitle = (TextView) findViewById(R.id.dialog_verify_select_title);
        mHalfSure = (TextView) findViewById(R.id.dialog_verify_select_operate);
        mSure = (TextView) findViewById(R.id.dialog_verify_select_verify);
        mHalfCancel = (TextView) findViewById(R.id.dialog_verify_select_cancel);
        mMiddleView = (FrameLayout) findViewById(R.id.dialog_verify_select_middle);
        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    dismiss();
                    if (onCommonDialogListener != null) {
                        onCommonDialogListener.onSure(v);
                    }
                }
            }
        });

        mHalfSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    dismiss();
                    if (onCommonDialogListener != null) {
                        onCommonDialogListener.onSure(v);
                    }
                }
            }
        });

        mHalfCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mQuickClickGuard.isQuickClick(v.getId())) {
                    dismiss();
                    if (onCommonDialogListener != null) {
                        onCommonDialogListener.onCancel(v);
                    }
                }
            }
        });
        mMiddleView.addView(onCreateMiddleView(mMiddleView));
    }

    /**
     * set dialog title
     */
    public void setDialogTitle(String title, @ColorInt int color) {
        mTitle.setText(title);
        mTitle.setTextColor(color);
    }

    public void setDialogTitle(String title) {
        mTitle.setText(title);
    }

    public void setDialogTitleIcon(Drawable left) {
        if (left != null) {
            left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        mTitle.setCompoundDrawables(left, null , null, null);
        mTitle.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getContext().getResources().getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = mTitle.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTitle.setLayoutParams(layoutParams);
    }

    public void setDialogTitleWord(String title) {
        mTitle.setText(title);
    }

    public void setDialogTitleColor(@ColorInt int color) {
        mTitle.setTextColor(color);
    }

    public void setContentText(String... contentTxt) {
        if (contentTxt != null) {
            int length = contentTxt.length;
            if (length == 2) {
                String cancelTxt = contentTxt[0];
                String sureTxt = contentTxt[1];
                mHalfCancel.setText(cancelTxt);
                mHalfSure.setText(sureTxt);
                mHalfCancel.setVisibility(View.VISIBLE);
                mHalfSure.setVisibility(View.VISIBLE);
                mSure.setVisibility(View.GONE);
            } else if (length == 1) {
                String sureText = contentTxt[0];
                mHalfCancel.setVisibility(View.GONE);
                mHalfSure.setVisibility(View.GONE);
                mSure.setVisibility(View.VISIBLE);
                mSure.setText(sureText);
            }
        }
    }

    public void setMiddleWidthMatchParent() {
        if (mMiddleView != null) {
            ViewGroup.LayoutParams layoutParams = mMiddleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mMiddleView.setLayoutParams(layoutParams);
        }
    }

    /**
     * listener
     * */
    public interface OnCommonDialogListener {
        void onCancel(View view);
        void onSure(View view);
    }

    protected OnCommonDialogListener onCommonDialogListener;

    public void setOnCommonDialogListener(OnCommonDialogListener onCommonDialogListener) {
        this.onCommonDialogListener = onCommonDialogListener;
    }

    /**
     * create middle layout
     * @param parent parent layout <br/>
     */
    public abstract View onCreateMiddleView(ViewGroup parent);

    public boolean toShow() {
        show();
        return true;
    }
}

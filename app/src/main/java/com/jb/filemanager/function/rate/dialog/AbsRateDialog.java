package com.jb.filemanager.function.rate.dialog;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.ui.dialog.BaseDialog;
import com.jb.filemanager.util.Logger;
import com.jb.filemanager.util.QuickClickGuard;

/**
 * Created by nieyh on 2016/9/2.
 */
public abstract class AbsRateDialog extends BaseDialog implements View.OnClickListener {
    protected QuickClickGuard mQuickClickGuard = new QuickClickGuard();
    private TextView mOkTv;
    private TextView mNoTv;
    private TextView mTitle;
    protected OnPressListener mOnPressListener;

    public AbsRateDialog(Activity act) {
        super(act, false);
        initView();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics());
        setSize(getContext().getResources().getDisplayMetrics().widthPixels - 2 * margin, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initView() {
        View root = View.inflate(TheApplication.getAppContext(), R.layout.rate_dialog, null);
        RelativeLayout middleView = (RelativeLayout) root.findViewById(R.id.rate_dialog_middle);
        mOkTv = (TextView) root.findViewById(R.id.rate_dialog_yes);
        mNoTv = (TextView) root.findViewById(R.id.rate_dialog_no);
        mTitle = (TextView) root.findViewById(R.id.rate_dialog_title);
        initMiddleView(middleView);
        mOkTv.setOnClickListener(this);
        mNoTv.setOnClickListener(this);
        setContentView(root);
    }

    protected abstract void initMiddleView(RelativeLayout middleView);

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mOnPressListener != null) {
            mOnPressListener.pressBack();
        }
    }

    @Override
    public void onClick(View v) {
        if (mQuickClickGuard.isQuickClick(v.getId())) {
            return;
        }
        if (mOnPressListener != null) {
            if (v == mOkTv) {
                mOnPressListener.pressYes();
            } else if (v == mNoTv) {
                mOnPressListener.pressNo();
            }
        }
    }

    //设置标题文案
    protected void setTitleTxt(@StringRes int resId) {
        if (mTitle != null) {
            mTitle.setText(resId);
        }
    }

    //设置标题文案
    protected void setOkTxt(@StringRes int resId) {
        if (mOkTv != null) {
            mOkTv.setText(resId);
        }
    }

    //设置标题文案
    protected void setNoTxt(@StringRes int resId) {
        if (mNoTv != null) {
            mNoTv.setText(resId);
        }
    }

    //设置监听器
    public void setOnPressListener(OnPressListener mOnPressListener) {
        this.mOnPressListener = mOnPressListener;
    }

    /**
     * 点击事件监听器
     */
    public interface OnPressListener {
        void pressBack();

        void pressYes();

        void pressNo();
    }
}

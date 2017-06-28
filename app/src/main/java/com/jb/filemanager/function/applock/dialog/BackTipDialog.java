package com.jb.filemanager.function.applock.dialog;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by nieyh on 2017/1/10.
 */

public class BackTipDialog extends BaseDialog {

    private View mRoot;

    private View mActivate;

    private View mExit;

    public BackTipDialog(Activity act) {
        super(act, R.style.bottom_dialog_theme, true);
        mRoot = LayoutInflater.from(mActivity).inflate(R.layout.dialog_back_tip_layout, null);
        mActivate = mRoot.findViewById(R.id.dialog_back_tip_layout_activate);
        mExit = mRoot.findViewById(R.id.dialog_back_tip_layout_exit);
        mActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOnBackTipClickListener != null) {
                    mOnBackTipClickListener.onExitClick(v);
                }
            }
        });
        setContentView(mRoot);
        setSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setXY(0, 0, Gravity.BOTTOM);
    }

    private OnBackTipClickListener mOnBackTipClickListener;

    public void setOnBackTipClickListener(@Nullable OnBackTipClickListener onBackTipClickListener) {
        this.mOnBackTipClickListener = onBackTipClickListener;
    }

    public interface OnBackTipClickListener {
        void onExitClick(View v);
    }

}

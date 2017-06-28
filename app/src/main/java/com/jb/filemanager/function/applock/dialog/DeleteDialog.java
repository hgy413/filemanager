package com.jb.filemanager.function.applock.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by nieyh on 2017/1/6.
 * 用途：<br>
 * 删除图片
 */

public class DeleteDialog extends BaseDialog {

    private View mDelete;

    private View mCancel;

    private onDeleteListener mOnDeleteListener;

    public DeleteDialog(Activity act) {
        super(act);
        initView();
    }

    public DeleteDialog(Activity act, int style) {
        super(act, style);
        initView();
    }

    public DeleteDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
        initView();
    }

    private void initView() {
        View parent = LayoutInflater.from(getContext()).inflate(R.layout.dialog_applocker_delete_img, null);
        mCancel = parent.findViewById(R.id.dialog_applocker_delete_img_cancel);
        mDelete = parent.findViewById(R.id.dialog_applocker_delete_img_delete);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onDelete(v);
                    dismiss();
                }
            }
        });
        setContentView(parent);
    }

    public void setOnDeleteListener(onDeleteListener onDeleteListener) {
        this.mOnDeleteListener = onDeleteListener;
    }

    public interface onDeleteListener {
        void onDelete(View v);
    }
}

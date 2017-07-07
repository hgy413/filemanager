package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/6 18:34
 */

public class FileDeleteConfirmDialog extends BaseDialog {
    private TextView mTvDialogTitle;
    private TextView mTvDialogContent;
    private TextView mTvDialogCancel;
    private TextView mTvDialogOk;
    private OnClickListener mDialogListener;
    private ArrayList<File> mFilesList;

    public FileDeleteConfirmDialog(Activity act, ArrayList<File> files) {
        this(act);
        mFilesList = files;
    }

    public FileDeleteConfirmDialog(Activity act) {
        super(act);
        View dialogView = View.inflate(act, R.layout.dialog_file_delete_confirem, null);

        mTvDialogTitle = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        mTvDialogContent = (TextView) dialogView.findViewById(R.id.tv_dialog_content);
        mTvDialogCancel = (TextView) dialogView.findViewById(R.id.tv_dialog_cancel);
        mTvDialogOk = (TextView) dialogView.findViewById(R.id.tv_dialog_ok);

        mTvDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogListener != null) {
                    if (mFilesList != null && !mFilesList.isEmpty()) {
                        for (File file : mFilesList) {
                            FileUtils.deleteFile(file);
                        }
                    }
                    mDialogListener.clickConfirm();
                }
                dismiss();
            }
        });

        mTvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogListener != null) {
                    mDialogListener.clickCancel();
                }
                dismiss();
            }
        });
        setContentView(dialogView);
    }

    public void setDialogTitle(@NonNull CharSequence dialogTitle) {
        mTvDialogTitle.setText(dialogTitle);
    }

    public void setDialogContent(@NonNull CharSequence dialogContent) {
        mTvDialogContent.setText(dialogContent);
    }

    public void setDialogCancel(@NonNull CharSequence dialogCancel) {
        mTvDialogCancel.setText(dialogCancel);
    }

    public void setDialogConfirm(@NonNull CharSequence dialogConfirm) {
        mTvDialogOk.setText(dialogConfirm);
    }

    public void setOnDialogClickListener(OnClickListener listener) {
        this.mDialogListener = listener;
    }

    public interface OnClickListener {
        void clickConfirm();

        void clickCancel();
    }
}

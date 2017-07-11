package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.FileUtil;

/**
 * Created by bill wang on 2017/7/11.
 *
 */

public class CreateNewFolderDialog extends FMBaseDialog {

    private TextView mTvTitle;
    private TextView mTvErrorTips;
    private EditText mEtInput;

    public CreateNewFolderDialog(Activity act, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_main_create_folder, null);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_create_folder_title);
        if (mTvTitle != null) {
            mTvTitle.getPaint().setAntiAlias(true);
        }

        mTvErrorTips = (TextView) dialogView.findViewById(R.id.tv_create_folder_error_tips);
        if (mTvErrorTips != null) {
            mTvErrorTips.getPaint().setAntiAlias(true);
        }

        mEtInput = (EditText) dialogView.findViewById(R.id.et_main_create_folder_input);

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEtInput != null) {
                        String input = mEtInput.getText().toString();
                        if (TextUtils.isEmpty(input)) {
                            if (mTvTitle != null) {
                                mTvTitle.setVisibility(View.GONE);
                            }
                            if (mTvErrorTips != null) {
                                mTvErrorTips.setVisibility(View.VISIBLE);
                                mTvErrorTips.setText(R.string.dialog_create_new_folder_empty_input);
                            }
                        } else if (!input.matches(FileUtil.FILE_PATH_REG)) {
                            if (mTvTitle != null) {
                                mTvTitle.setVisibility(View.GONE);
                            }
                            if (mTvErrorTips != null) {
                                mTvErrorTips.setVisibility(View.VISIBLE);
                                mTvErrorTips.setText(R.string.dialog_create_new_folder_error_input);
                            }
                        } else {
                            listener.onConfirm(CreateNewFolderDialog.this, mEtInput.getText().toString());
                        }
                    }
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(CreateNewFolderDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(CreateNewFolderDialog dialog, String folderName);

        void onCancel(CreateNewFolderDialog dialog);
    }
}

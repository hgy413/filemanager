package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.AppUtils;
import com.jb.filemanager.util.FileUtil;

import java.io.File;

/**
 * Created by bill wang on 2017/7/11.
 *
 */

public class CreateNewFolderDialog extends FMBaseDialog {

    private TextView mTvTitle;
    private TextView mTvErrorTips;
    private EditText mEtInput;

    public CreateNewFolderDialog(final Activity act, final String path) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_create_folder, null);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_create_folder_title);
        if (mTvTitle != null) {
            mTvTitle.getPaint().setAntiAlias(true);
        }

        mTvErrorTips = (TextView) dialogView.findViewById(R.id.tv_create_folder_error_tips);
        if (mTvErrorTips != null) {
            mTvErrorTips.getPaint().setAntiAlias(true);
        }

        mEtInput = (EditText) dialogView.findViewById(R.id.et_create_folder_input);

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_create_folder_confirm);
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
                                mTvErrorTips.setText(R.string.dialog_create_folder_empty_input);
                            }
                        } else if (!input.matches(FileUtil.FOLDER_NAME_REG)) {
                            if (mTvTitle != null) {
                                mTvTitle.setVisibility(View.GONE);
                            }
                            if (mTvErrorTips != null) {
                                mTvErrorTips.setVisibility(View.VISIBLE);
                                mTvErrorTips.setText(R.string.dialog_create_folder_error_input);
                            }
                        } else {
                            if (!TextUtils.isEmpty(path)) {
                                File target = new File(path + File.separator + input);
                                if (target.exists()) {
                                    if (mTvTitle != null) {
                                        mTvTitle.setVisibility(View.GONE);
                                    }
                                    if (mTvErrorTips != null) {
                                        mTvErrorTips.setVisibility(View.VISIBLE);
                                        mTvErrorTips.setText(R.string.dialog_create_folder_name_duplicate);
                                    }
                                } else {
                                    boolean success = FileUtil.createFolder(path + File.separator + input);
                                    if (success) {
                                        dismiss();
                                    } else {
                                        AppUtils.showToast(act, R.string.toast_create_folder_failed);
                                    }
                                }
                            }
                        }
                    }
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_create_folder_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }
}

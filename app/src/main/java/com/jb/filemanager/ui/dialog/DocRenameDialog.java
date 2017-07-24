package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.FileUtil;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/7 14:59
 */

public class DocRenameDialog extends FMBaseDialog {

    private TextView mTvTitle;
    private TextView mTvErrorTips;
    private EditText mEtInput;

    public DocRenameDialog(final Activity act, final boolean isFolder, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_file_rename, null);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_rename_title);
        if (mTvTitle != null) {
            mTvTitle.getPaint().setAntiAlias(true);
        }

        mTvErrorTips = (TextView) dialogView.findViewById(R.id.tv_rename_error_tips);
        if (mTvErrorTips != null) {
            mTvErrorTips.getPaint().setAntiAlias(true);
        }

        mEtInput = (EditText) dialogView.findViewById(R.id.et_rename_input);

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_rename_confirm);
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
                                mTvErrorTips.setText(isFolder ? R.string.dialog_rename_folder_empty_input : R.string.dialog_rename_file_empty_input);
                            }
                        } else if (!input.matches(isFolder ? FileUtil.FOLDER_NAME_REG : FileUtil.FILE_NAME_REG)) {
                            String notContain = "";
                            for (int i = 0; i < input.length(); i++) {
                                char testChar = input.charAt(i);
                                String testString = String.valueOf(testChar);
                                if (!testString.matches(isFolder ? FileUtil.FOLDER_NAME_REG : FileUtil.FILE_NAME_REG)) {
                                    if (!notContain.contains(testString)) {
                                        if (notContain.length() > 0) {
                                            notContain += ",";
                                        }
                                        notContain += testString;
                                    }
                                }
                            }

                            if (mTvTitle != null) {
                                mTvTitle.setVisibility(View.GONE);
                            }
                            if (mTvErrorTips != null) {
                                mTvErrorTips.setVisibility(View.VISIBLE);
                                mTvErrorTips.setText(isFolder ? act.getString(R.string.dialog_rename_folder_error_input, notContain) : act.getString(R.string.dialog_rename_file_error_input, notContain));
                            }
                        } else {
                            listener.onConfirm(DocRenameDialog.this, mEtInput.getText().toString());
                        }
                    }
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_rename_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(DocRenameDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(DocRenameDialog dialog, String folderName);

        void onCancel(DocRenameDialog dialog);
    }

}

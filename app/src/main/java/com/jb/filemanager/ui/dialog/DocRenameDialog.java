package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.FileUtil;

import java.io.File;

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

    public DocRenameDialog(final Activity act, final File sourceFile, final Listener listener) {
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
                    if (sourceFile != null && sourceFile.exists()) {
                        boolean isFolder = sourceFile.isDirectory();
                        if (mEtInput != null) {
                            String input = mEtInput.getText().toString();
                            if (TextUtils.isEmpty(input)) {
                                showErrorTips(act.getString(isFolder ? R.string.dialog_rename_folder_empty_input : R.string.dialog_rename_file_empty_input));
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
                                showErrorTips(isFolder ? act.getString(R.string.dialog_rename_folder_error_input, notContain) : act.getString(R.string.dialog_rename_file_error_input, notContain));
                            } else {
                                String dir = sourceFile.getParentFile().getAbsolutePath();
                                File target = new File(dir + File.separator + input);
                                if (target.exists()) {
                                    showErrorTips(getString(R.string.dialog_rename_target_exist));
                                } else {
                                    boolean success = FileUtil.renameSelectedFile(sourceFile, target.getAbsolutePath());
                                    MediaScannerConnection.scanFile(TheApplication.getInstance(), new String[] {dir}, null, null); // 修改后的文件添加到系统数据库
                                    if (listener != null) {
                                        listener.onResult(DocRenameDialog.this, success);
                                    }
                                }
                            }
                        }
                    } else {
                        showErrorTips(getString(R.string.dialog_rename_source_not_exist));
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

    private void showErrorTips(String errorDesc) {
        if (!TextUtils.isEmpty(errorDesc)) {
            if (mTvTitle != null) {
                mTvTitle.setVisibility(View.GONE);
            }
            if (mTvErrorTips != null) {
                mTvErrorTips.setVisibility(View.VISIBLE);
                mTvErrorTips.setText(errorDesc);
            }
        }
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onResult(DocRenameDialog dialog, boolean success);

        void onCancel(DocRenameDialog dialog);
    }

}

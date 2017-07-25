package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.statistics.StatisticsConstants;
import com.jb.filemanager.statistics.StatisticsTools;
import com.jb.filemanager.statistics.bean.Statistics101Bean;
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

    public CreateNewFolderDialog(final Activity act, final String path, final Listener listener) {
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
                            String notContain = "";
                            for (int i = 0; i < input.length(); i++) {
                                char testChar = input.charAt(i);
                                String testString = String.valueOf(testChar);
                                if (!testString.matches(FileUtil.FOLDER_NAME_REG)) {
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
                                mTvErrorTips.setText(act.getString(R.string.dialog_create_folder_error_input, notContain));
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
                                    if (listener != null) {
                                        listener.onResult(CreateNewFolderDialog.this, success);
                                    }
                                }
                            }
                        }
                    }
                    statisticsClickOk();
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_create_folder_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCancel(CreateNewFolderDialog.this);
                    }
                    statisticsClickCancel("2");
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        statisticsClickCancel("1");
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    private void statisticsClickOk() {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CREATE_FOLDER_OK;
        StatisticsTools.upload101InfoNew(bean);
    }

    private void statisticsClickCancel(String tab) {
        Statistics101Bean bean = Statistics101Bean.builder();
        bean.mOperateId = StatisticsConstants.STORAGE_CREATE_FOLDER_CANCEL;
        bean.mTab = tab;
        StatisticsTools.upload101InfoNew(bean);
    }

    public interface Listener {
        void onResult(CreateNewFolderDialog dialog, boolean success);
        void onCancel(CreateNewFolderDialog dialog);
    }
}

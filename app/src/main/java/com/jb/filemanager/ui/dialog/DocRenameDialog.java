package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/7 14:59
 */

public class DocRenameDialog extends BaseDialog {
    private File mFile;
    private TextView mTvDialogTitle;
    private TextView mTvDialogErrorMsg;
    private EditText mEtDialogContent;
    private TextView mTvDialogCancel;
    private TextView mTvDialogOk;
    private OnClickListener mDialogListener;

    public DocRenameDialog(Activity act, File file) {
        super(act);
        this.mFile = file;
        View dialogView = View.inflate(act, R.layout.dialog_file_rename, null);
        mTvDialogTitle = (TextView) dialogView.findViewById(R.id.tv_dialog_title);
        mTvDialogErrorMsg = (TextView) dialogView.findViewById(R.id.tv_dialog_error_msg);
        mEtDialogContent = (EditText) dialogView.findViewById(R.id.et_dialog_content);
        mTvDialogCancel = (TextView) dialogView.findViewById(R.id.tv_dialog_cancel);
        mTvDialogOk = (TextView) dialogView.findViewById(R.id.tv_dialog_ok);
        setContentView(dialogView);

        mEtDialogContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});//限制字符长度不能超过50
        FileRenameWatcher watcher = new FileRenameWatcher();
        watcher.setStateChangeListener(new FileRenameWatcher.OnTextInputStateChangeListener() {
            @Override
            public void onStateChange(FileRenameWatcher.InputState newState) {
                if (newState == FileRenameWatcher.InputState.TEXT_ILLEGAL) {
                    // TODO: 2017/7/7 add by --miwo 处理非法输入
                    mTvDialogErrorMsg.setVisibility(View.VISIBLE);
                    mTvDialogErrorMsg.setText("");
                } else if (newState == FileRenameWatcher.InputState.TEXT_NULL) {
                    // TODO: 2017/7/7 add by --miwo 处理空值输入
                } else {
                    // TODO: 2017/7/7 add by --miwo 处理正常输入
                }
            }

            @Override
            public void onInputChange(String input) {

            }
        });
        mEtDialogContent.addTextChangedListener(watcher);
        mTvDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogListener != null) {
                    String input = mEtDialogContent.getText().toString();
                    if (TextUtils.isEmpty(input)) {
                        mTvDialogErrorMsg.setVisibility(View.VISIBLE);
                        mTvDialogErrorMsg.setText("file name can't be null");
                        return;
                    }
                    File parentFile = mFile.getParentFile();
                    File newFile = new File(parentFile, input);
                    if (newFile.exists()) {//说明名字重复了
                        mTvDialogErrorMsg.setVisibility(View.VISIBLE);
                        mTvDialogErrorMsg.setText("file has exist");
                        mEtDialogContent.selectAll();
                        return;
                    }

                    if (input.length() > 100) {
                        Toast.makeText(mActivity, "文件名过长！", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(input);
                    if (m.find()) {
                        Toast.makeText(mActivity, "文件名不允许输入特殊符号！", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String s = "[^a-zA-Z0-9\\u4E00-\\u9FA5_()].";
                    boolean renameResult = FileUtil.renameSelectedFile(mFile, input);
                    mDialogListener.clickConfirm(input);
                }
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
    }

    public void setDialogTitle(@NonNull CharSequence dialogTitle) {
        mTvDialogTitle.setText(dialogTitle);
    }

    public void setDialogContent(@NonNull CharSequence dialogContent) {
        mEtDialogContent.setText(dialogContent);
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

    public EditText getEtDialogContent() {
        return mEtDialogContent;
    }

    public interface OnClickListener {
        void clickConfirm(String input);

        void clickCancel();
    }


}

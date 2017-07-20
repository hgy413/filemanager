package com.jb.filemanager.function.zipfile.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by xiaoyu on 2017/7/12 17:26.
 */

public class ExtractErrorDialog extends BaseDialog implements View.OnClickListener {

    private Context mContext;
    private Activity mActivity;
    private String mFileName;
    private TextView mTvFileName;
    private TextView mTvDialogTitle;
    private TextView mTvRetryTxt;
    private TextView mOkBtn;

    public ExtractErrorDialog(Activity act, String fileName) {
        super(act, true);
        mContext = act.getApplicationContext();
        mActivity = act;
        mFileName = fileName;
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.dialog_extract_error, null);
        setContentView(rootView, new ViewGroup.LayoutParams(
                mContext.getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        mTvRetryTxt = (TextView) findViewById(R.id.tv_retry_txt);
        mTvFileName = (TextView) rootView.findViewById(R.id.dialog_extract_error_file_path);
        mTvFileName.setText(mContext.getString(R.string.unable_to_extract, mFileName));
        mOkBtn = (TextView) rootView.findViewById(R.id.dialog_extract_error_ok_btn);
        mOkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_extract_error_ok_btn:
                dismiss();
                break;
        }
    }

    public void setTvDialogTitle(String dialogTitle) {
        mTvDialogTitle.setText(dialogTitle);
    }

    public void setTvFileName(String fileName) {
        mTvFileName.setText(fileName);
    }

    public void setTvRetryTxt(String retryTxt) {
        mTvRetryTxt.setText(retryTxt);
    }

    public void setOkBtn(String okBtn) {
        mOkBtn.setText(okBtn);
    }
}

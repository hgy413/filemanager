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

        TextView tvFileName = (TextView) rootView.findViewById(R.id.dialog_extract_error_file_path);
        tvFileName.setText(mContext.getString(R.string.unable_to_extract, mFileName));
        View okBtn = rootView.findViewById(R.id.dialog_extract_error_ok_btn);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_extract_error_ok_btn:
                dismiss();
                break;
        }
    }
}

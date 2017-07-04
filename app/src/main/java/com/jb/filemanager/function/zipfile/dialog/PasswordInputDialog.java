package com.jb.filemanager.function.zipfile.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by xiaoyu on 2017/7/3 20:31.
 */

public class PasswordInputDialog extends BaseDialog implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private EditText mEditText;
    private PasswordInputCallback mListener;

    public PasswordInputDialog(Activity act) {
        super(act, false);
        mActivity = act;
        mContext = act.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.dialog_zip_pass_input, null);
        setContentView(rootView);
        mEditText = (EditText) rootView.findViewById(R.id.dialog_zip_pass_in_et);

        TextView tvOk = (TextView) rootView.findViewById(R.id.dialog_zip_pass_in_ok);
        tvOk.setOnClickListener(this);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.dialog_zip_pass_in_cancel);
        tvCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_zip_pass_in_ok:
                String s = mEditText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(mActivity, "输入格式有误", Toast.LENGTH_SHORT).show();
                } else {
                    if (mListener != null) {
                        mListener.onInputFinish(s);
                    }
                    dismiss();
                }
                break;
            case R.id.dialog_zip_pass_in_cancel:
                dismiss();
                break;
        }
    }

    public void setListener(PasswordInputCallback listener) {
        mListener = listener;
    }

    public interface PasswordInputCallback {
        void onInputFinish(String password);
    }
}

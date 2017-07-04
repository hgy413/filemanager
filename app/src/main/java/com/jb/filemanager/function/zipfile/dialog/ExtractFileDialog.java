package com.jb.filemanager.function.zipfile.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by xiaoyu on 2017/7/4 19:37.
 */

public class ExtractFileDialog extends BaseDialog implements View.OnClickListener {

    private Activity mActivity;
    private Context mContext;
    private TextView mPath;

    public ExtractFileDialog(Activity act) {
        super(act, false);
        mActivity = act;
        mContext = act.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.dialog_extract_file, null);
        setContentView(rootView);
        mPath = (TextView) rootView.findViewById(R.id.dialog_extract_path);
        TextView btnCancel = (TextView) rootView.findViewById(R.id.dialog_extract_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_extract_cancel:
                cancel();
                break;
        }
    }

    public void updatePath(CharSequence path) {
        if (mPath != null) {
            mPath.setText(path);
        }
    }
}

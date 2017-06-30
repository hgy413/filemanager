package com.jb.filemanager.function.zipfile;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jb.filemanager.R;
import com.jb.filemanager.function.zipfile.bean.ZipFileItem;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Created by xiaoyu on 2017/6/30 13:50.
 * <p>
 *     压缩文件操作项弹窗。
 * </p>
 */

public class ZipFileOperationDialog extends BaseDialog implements View.OnClickListener {

    private Context mContext;
    private ZipFileItem mFile;

    public ZipFileOperationDialog(Activity act, ZipFileItem fileItem) {
        super(act, true);
        mContext = getContext().getApplicationContext();
        mFile = fileItem;
        initializeView();
    }

    private void initializeView() {
        View rootView = View.inflate(mContext, R.layout.dialog_zip_file_detail, null);
        setContentView(rootView);

        TextView fileName = (TextView) rootView.findViewById(R.id.file_name);
        fileName.setText(mFile.getFileName());
        TextView btnView = (TextView) rootView.findViewById(R.id.btn_view);
        btnView.setOnClickListener(this);
        TextView btnExtract = (TextView) rootView.findViewById(R.id.btn_extract);
        btnExtract.setOnClickListener(this);
        TextView btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view:
                Toast.makeText(mContext, "view", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_extract:
                Toast.makeText(mContext, "extrace", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}

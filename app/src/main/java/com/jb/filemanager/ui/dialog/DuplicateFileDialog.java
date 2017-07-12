package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.APIUtil;

import java.io.File;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class DuplicateFileDialog extends FMBaseDialog {

    private TextView mTvApplyToAll;

    public DuplicateFileDialog(Activity act, String duplicateFilePath, boolean isSingle, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_duplicate_file, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
        }

        TextView filePath = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_src);
        if (filePath != null) {
            filePath.getPaint().setAntiAlias(true);
            if (!TextUtils.isEmpty(duplicateFilePath)) {
                filePath.setText(duplicateFilePath);
            }

            File file = new File(duplicateFilePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    filePath.setCompoundDrawablesWithIntrinsicBounds(APIUtil.getDrawable(act, R.drawable.img_folder), null, null, null);
                } else {
                    filePath.setCompoundDrawablesWithIntrinsicBounds(APIUtil.getDrawable(act, R.drawable.img_file), null, null, null);
                }
            }
        }

        mTvApplyToAll = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_apply_to_all);
        if (mTvApplyToAll != null) {
            mTvApplyToAll.getPaint().setAntiAlias(true);
            if (isSingle) {
                mTvApplyToAll.setVisibility(View.GONE);
            } else {
                mTvApplyToAll.setVisibility(View.VISIBLE);
            }

            mTvApplyToAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvApplyToAll.setSelected(!mTvApplyToAll.isSelected());
                }
            });
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(DuplicateFileDialog.this, mTvApplyToAll.isSelected());
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(DuplicateFileDialog.this, mTvApplyToAll.isSelected());
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(DuplicateFileDialog dialog, boolean isApplyToAll);

        void onCancel(DuplicateFileDialog dialog, boolean isApplyToAll);
    }
}

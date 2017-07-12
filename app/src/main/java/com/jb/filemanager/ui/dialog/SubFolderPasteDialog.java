package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.io.File;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class SubFolderPasteDialog extends FMBaseDialog {

    public SubFolderPasteDialog(Activity act, String sourcePath, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_sub_folder_paste, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_sub_folder_paste_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_sub_folder_paste_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
        }

        TextView filePath = (TextView) dialogView.findViewById(R.id.tv_sub_folder_paste_src);
        if (filePath != null) {
            filePath.getPaint().setAntiAlias(true);
            if (!TextUtils.isEmpty(sourcePath)) {
                File file = new File(sourcePath);
                filePath.setText(file.getName());
            }
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_sub_folder_paste_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSkip(SubFolderPasteDialog.this);
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_sub_folder_paste_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(SubFolderPasteDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onSkip(SubFolderPasteDialog dialog);

        void onCancel(SubFolderPasteDialog dialog);
    }
}

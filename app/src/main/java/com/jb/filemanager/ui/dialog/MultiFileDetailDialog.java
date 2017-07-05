package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/1.
 *
 */

public class MultiFileDetailDialog extends ScreenWidthDialog {

    public MultiFileDetailDialog(Activity act, ArrayList<File> files, final Listener listener) {
        super(act, true);
        if (files != null && files.size() > 0) {
            View dialogView = View.inflate(act, R.layout.dialog_main_multi_files_detail, null);
            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_confirm);
            TextView size = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_size_value);
            TextView containValue = (TextView) dialogView.findViewById(R.id.tv_main_multi_files_detail_contain_value);

            long filesSize = 0L;
            Integer folderTotalCount = 0;
            Integer fileTotalCount = 0;
            for (File file : files) {
                int[] count = FileUtil.countFolderAndFile(file);
                filesSize += file.length();
                folderTotalCount += count[0];
                fileTotalCount += count[1];

                if (file.isDirectory()) {
                    folderTotalCount++;
                } else {
                    fileTotalCount++;
                }
            }
            size.setText(ConvertUtils.getReadableSize(filesSize));
            containValue.setText(act.getString(R.string.main_dialog_single_detail_contain, folderTotalCount, fileTotalCount));

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(MultiFileDetailDialog.this);
                }
            }); // 确定按钮点击事件

            setContentView(dialogView);
        }
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(MultiFileDetailDialog dialog);
    }
}

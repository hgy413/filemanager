package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.TheApplication;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/1.
 *
 */

public class MultiFileDetailDialog extends FMBaseDialog {

    public MultiFileDetailDialog(final Activity act, final ArrayList<File> files, final Listener listener) {
        super(act, true);
        if (files != null && files.size() > 0) {



            View dialogView = View.inflate(act, R.layout.dialog_multi_files_detail, null);

            TextView dialogTitle = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_title);
            if (dialogTitle != null) {
                dialogTitle.getPaint().setAntiAlias(true);
            }


            TextView sizeTitle = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_size_title);
            if (sizeTitle != null) {
                sizeTitle.getPaint().setAntiAlias(true);
            }

            final TextView sizeValue = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_size_value);
            if (sizeValue != null) {
                sizeValue.getPaint().setAntiAlias(true);
                sizeValue.setText(R.string.common_ellipsis);
            }

            TextView containTitle = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_contain_title);
            if (containTitle != null) {
                containTitle.getPaint().setAntiAlias(true);
            }

            final TextView containValue = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_contain_value);
            if (containValue != null) {
                containValue.getPaint().setAntiAlias(true);
                containValue.setText(R.string.common_ellipsis);
            }

            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_multi_files_detail_confirm);
            if (okButton != null) {
                okButton.getPaint().setAntiAlias(true);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onConfirm(MultiFileDetailDialog.this);
                    }
                }); // 确定按钮点击事件
            }

            TheApplication.postRunOnShortTaskThread(new Runnable() {
                @Override
                public void run() {
                    long filesSize = 0L;
                    Integer folderTotalCount = 0;
                    Integer fileTotalCount = 0;
                    for (File file : files) {
                        int[] count = FileUtil.countFolderAndFile(file);
                        filesSize += FileUtil.getSize(file);
                        folderTotalCount += count[0];
                        fileTotalCount += count[1];

                        if (file.isDirectory()) {
                            folderTotalCount++;
                        } else {
                            fileTotalCount++;
                        }
                    }

                    final long fileSizeFinal = filesSize;
                    final int folderTotalCountFinal = folderTotalCount;
                    final int fileTotalCountFinal = fileTotalCount;
                    TheApplication.postRunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeValue != null) {
                                sizeValue.setText(ConvertUtils.getReadableSize(fileSizeFinal));
                            }
                            if (containValue != null) {
                                containValue.setText(act.getString(R.string.main_dialog_single_detail_contain, folderTotalCountFinal, fileTotalCountFinal));
                            }
                        }
                    });
                }
            });

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

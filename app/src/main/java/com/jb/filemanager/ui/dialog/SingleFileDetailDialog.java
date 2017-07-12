package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtils;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;

/**
 * Created by bill wang on 2017/7/1.
 *
 */

public class SingleFileDetailDialog extends FMBaseDialog {

    public SingleFileDetailDialog(Activity act, File file, final Listener listener) {
        super(act, true);
        if (file != null && file.exists()) {
            View dialogView = View.inflate(act, R.layout.dialog_single_file_detail, null);

            TextView title = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_title);
            if (title != null) {
                title.getPaint().setAntiAlias(true);
                if (file.isDirectory()) {
                    title.setText(R.string.dialog_single_file_detail_title_folder);
                } else {
                    title.setText(R.string.dialog_single_file_detail_title_file);
                }
            }

            TextView name = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_name_value);
            if (name != null) {
                name.getPaint().setAntiAlias(true);
                name.setText(file.getName());
            }

            TextView location = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_location_value);
            if (location != null) {
                location.getPaint().setAntiAlias(true);
                location.setText(file.getAbsolutePath());
            }

            TextView modifyTime = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_modify_time_value);
            if (modifyTime != null) {
                modifyTime.getPaint().setAntiAlias(true);
                modifyTime.setText(TimeUtil.getTime(file.lastModified()));
            }

            TextView size = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_size_value);
            if (size != null) {
                size.getPaint().setAntiAlias(true);
                size.setText(ConvertUtils.getReadableSize(FileUtil.getSize(file)));
            }

            TextView containTitle = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_contain_title);
            if (containTitle != null) {
                containTitle.getPaint().setAntiAlias(true);
                if (!file.isDirectory()) {
                    containTitle.setVisibility(View.GONE);
                }
            }

            TextView containValue = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_contain_value);
            if (containValue != null) {
                containValue.getPaint().setAntiAlias(true);
                if (file.isDirectory()) {
                    int[] counts = FileUtil.countFolderAndFile(file);
                    containValue.setText(act.getString(R.string.main_dialog_single_detail_contain, counts[0], counts[1]));
                } else {
                    containValue.setVisibility(View.GONE);
                }
            }

            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_single_file_detail_confirm);
            if (okButton != null) {
                okButton.getPaint().setAntiAlias(true);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onConfirm(SingleFileDetailDialog.this);
                    }
                }); // 确定按钮点击事件
            }

            setContentView(dialogView);
        }
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(SingleFileDetailDialog dialog);
    }
}

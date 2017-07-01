package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.util.ConvertUtil;
import com.jb.filemanager.util.FileUtil;
import com.jb.filemanager.util.TimeUtil;

import java.io.File;

/**
 * Created by bill wang on 2017/7/1.
 *
 */

public class SingleFileDetailDialog extends ScreenWidthDialog {

    public SingleFileDetailDialog(Activity act, File file, final Listener listener) {
        super(act, true);
        if (file != null && file.exists()) {
            View dialogView = View.inflate(act, R.layout.dialog_main_single_file_detail, null);
            TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_confirm);
            TextView name = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_name_value);
            TextView location = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_location_value);
            TextView modifyTime = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_modify_time_value);
            TextView size = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_size_value);
            TextView containTitle = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_contain_title);
            TextView containValue = (TextView) dialogView.findViewById(R.id.tv_main_single_file_detail_contain_value);

            name.setText(file.getName());
            location.setText(file.getAbsolutePath());
            modifyTime.setText(TimeUtil.getTime(file.lastModified()));
            size.setText(ConvertUtil.getReadableSize(file.length()));

            if (file.isDirectory()) {
                int[] counts = FileUtil.countFolderAndFile(file);
                containValue.setText(act.getString(R.string.main_dialog_single_detail_contain, counts[0], counts[1]));
            } else {
                containTitle.setVisibility(View.GONE);
                containValue.setVisibility(View.GONE);
            }

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(SingleFileDetailDialog.this);
                }
            }); // 确定按钮点击事件

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

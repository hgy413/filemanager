package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * Created by bill wang on 2017/7/11.
 *
 */

public class CreateNewFolderDialog extends FMBaseDialog {

    public CreateNewFolderDialog(Activity act, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_main_create_folder, null);
        TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_confirm);
        TextView cancelButton = (TextView) dialogView.findViewById(R.id.tv_main_create_folder_cancel);
        final EditText editText = (EditText) dialogView.findViewById(R.id.et_main_create_folder_input);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(CreateNewFolderDialog.this);
            }
        }); // 取消按钮点击事件

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirm(CreateNewFolderDialog.this, editText.getText().toString());
            }
        }); // 确定按钮点击事件

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(CreateNewFolderDialog dialog, String folderName);

        void onCancel(CreateNewFolderDialog dialog);
    }
}

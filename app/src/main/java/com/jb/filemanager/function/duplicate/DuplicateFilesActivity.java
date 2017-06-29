package com.jb.filemanager.function.duplicate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.ScreenWidthDialog;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class DuplicateFilesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View dialogView = View.inflate(this, R.layout.dialog_main_rename, null);
        TextView okButton = (TextView) dialogView.findViewById(R.id.tv_main_rename_confirm);
        TextView cancelButton = (TextView) dialogView.findViewById(R.id.tv_main_rename_cancel);
        final EditText editText = (EditText) dialogView.findViewById(R.id.et_main_rename_input);

        final ScreenWidthDialog dialog = new ScreenWidthDialog(this, dialogView, true);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }); // 取消按钮点击事件

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); // 确定按钮点击事件

        dialog.show();
    }
}

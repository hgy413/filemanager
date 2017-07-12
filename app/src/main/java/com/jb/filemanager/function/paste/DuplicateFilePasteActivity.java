package com.jb.filemanager.function.paste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.DuplicateFileDialog;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class DuplicateFilePasteActivity extends Activity {

    public static final String DUPLICATE_FILE_PATH = "duplicate_file_path";
    public static final String DUPLICATE_FILE_IS_SINGLE = "duplicate_file_is_single";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String path = intent.getStringExtra(DUPLICATE_FILE_PATH);
        final boolean isSingle = intent.getBooleanExtra(DUPLICATE_FILE_IS_SINGLE, true);

        DuplicateFileDialog dialog = new DuplicateFileDialog(this, path, isSingle, new DuplicateFileDialog.Listener() {
            @Override
            public void onConfirm(DuplicateFileDialog dialog, boolean isApplyToAll) {
                FileManager.getInstance().continuePaste(path, false, isApplyToAll);
                dialog.dismiss();
                finish();
            }

            @Override
            public void onCancel(DuplicateFileDialog dialog, boolean isApplyToAll) {
                FileManager.getInstance().continuePaste(path, true, isApplyToAll);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}

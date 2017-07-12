package com.jb.filemanager.function.paste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.SubFolderPasteDialog;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class SubFolderPasteActivity extends Activity {

    public static final String SUB_FOLDER_PASTE_SOURCE_PATH = "sub_folder_paste_source_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String sourcePath = intent.getStringExtra(SUB_FOLDER_PASTE_SOURCE_PATH);

        SubFolderPasteDialog dialog = new SubFolderPasteDialog(this, sourcePath, new SubFolderPasteDialog.Listener() {
            @Override
            public void onSkip(SubFolderPasteDialog dialog) {
                FileManager.getInstance().continuePaste(sourcePath, true, null);
                dialog.dismiss();
                finish();
            }

            @Override
            public void onCancel(SubFolderPasteDialog dialog) {
                FileManager.getInstance().stopPast(sourcePath);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}

package com.jb.filemanager.function.paste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.ui.dialog.DuplicateFileDialog;
import com.jb.filemanager.ui.dialog.PasteFailedDialog;
import com.jb.filemanager.ui.dialog.SpaceNotEnoughDialog;
import com.jb.filemanager.ui.dialog.SubFolderPasteDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bill wang on 2017/6/29.
 *
 */

public class GlobalDialogActivity extends Activity {

    public static final int TYPE_PASTE_DUPLICATE_FILE = 1;
    public static final int TYPE_PASTE_SUB_FOLDER = 2;
    public static final int TYPE_PASTE_FAILED = 3;

    public static final String DIALOG_TYPE = "dialog_type";

    public static final String PASTE_DUPLICATE_FILE_PATH = "paste_duplicate_file_path";
    public static final String PASTE_DUPLICATE_FILE_IS_SINGLE = "paste_duplicate_file_is_single";

    public static final String PASTE_SUB_FOLDER_PASTE_SOURCE_PATH = "paste_sub_folder_source_path";

    public static final String PASTE_FAILED_SOURCE_PATH = "paste_failed_source_path";
    public static final String PASTE_FAILED_IS_COPY = "paste_failed_is_copy";
    public static final String PASTE_FAILED_DEST_PATH = "paste_failed_dest_path";

    private int mDialogCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        int type = intent.getIntExtra(DIALOG_TYPE, 0);
        switch (type) {
            case TYPE_PASTE_DUPLICATE_FILE:
                showPasteDuplicateFileDialog(intent);
                break;
            case TYPE_PASTE_SUB_FOLDER:
                showPasteSubFolderDialog(intent);
                break;
            case TYPE_PASTE_FAILED:
                showPasteFailedDialog(intent);
                break;
        }
    }

    private void showPasteDuplicateFileDialog(Intent intent) {
        final String path = intent.getStringExtra(PASTE_DUPLICATE_FILE_PATH);
        final boolean isSingle = intent.getBooleanExtra(PASTE_DUPLICATE_FILE_IS_SINGLE, true);

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
        mDialogCount++;
    }

    private void showPasteSubFolderDialog(Intent intent) {
        final String sourcePath = intent.getStringExtra(PASTE_SUB_FOLDER_PASTE_SOURCE_PATH);

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
        mDialogCount++;
    }

    private void showPasteFailedDialog(Intent intent) {
        ArrayList<String> sourceArray = intent.getStringArrayListExtra(PASTE_FAILED_SOURCE_PATH);
        final boolean isCopy = intent.getBooleanExtra(PASTE_FAILED_IS_COPY, true);
        final String dest = intent.getStringExtra(PASTE_FAILED_DEST_PATH);

        for (String path : sourceArray) {
            PasteFailedDialog dialog = new PasteFailedDialog(this, path, new PasteFailedDialog.Listener() {

                @Override
                public void onConfirm(PasteFailedDialog dialog, String failedFilePath) {
                    ArrayList<File> files = new ArrayList<>();
                    File file = new File(failedFilePath);
                    files.add(file);

                    if (isCopy) {
                        FileManager.getInstance().setCopyFiles(files);
                    } else {
                        FileManager.getInstance().setCutFiles(files);
                    }

                    boolean isStart = FileManager.getInstance().doPaste(dest, new FileManager.Listener() {
                        @Override
                        public void onPasteNeedMoreSpace(long needMoreSpace) {
                            SpaceNotEnoughDialog dialog = new SpaceNotEnoughDialog(GlobalDialogActivity.this, needMoreSpace, new SpaceNotEnoughDialog.Listener() {
                                @Override
                                public void onConfirm(SpaceNotEnoughDialog dialog) {
                                    dialog.dismiss();
                                    GlobalDialogActivity.this.finish();
                                }

                                @Override
                                public void onCancel(SpaceNotEnoughDialog dialog) {
                                    dialog.dismiss();
                                    GlobalDialogActivity.this.finish();
                                }
                            });
                            dialog.show();
                            mDialogCount++;
                        }

                        @Override
                        public void onPasteProgressUpdate(File file) {

                        }
                    });

                    if (isStart) {
                        dialog.dismiss();
                        finish();
                    }
                }

                @Override
                public void onCancel(PasteFailedDialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            });

            dialog.show();
            mDialogCount++;
        }

    }

    @Override
    public void finish() {
        mDialogCount--;
        if (mDialogCount == 0) {
            super.finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }
}

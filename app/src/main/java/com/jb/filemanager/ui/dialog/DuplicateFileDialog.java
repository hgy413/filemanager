package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.manager.file.FileManager;
import com.jb.filemanager.util.APIUtil;
import com.jb.filemanager.util.FileUtil;

import java.io.File;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class DuplicateFileDialog extends FMBaseDialog {

    private TextView mTvApplyToAll;

    public DuplicateFileDialog(Activity act, String duplicateFilePath, boolean isSingle, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_duplicate_file_paste, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
        }

        TextView filePath = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_src);
        if (filePath != null) {
            filePath.getPaint().setAntiAlias(true);
            if (!TextUtils.isEmpty(duplicateFilePath)) {
                filePath.setText(duplicateFilePath);
            }

            File file = new File(duplicateFilePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    filePath.setCompoundDrawablesWithIntrinsicBounds(APIUtil.getDrawable(act, R.drawable.file_type_folder), null, null, null);
                } else {
                    int type = FileUtil.getFileType(file.getAbsolutePath());
                    Drawable icon;
                    switch (type) {
                        case FileManager.IMAGE:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_photo);
                            break;
                        case FileManager.VIDEO:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_video);
                            break;
                        case FileManager.APP:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_app);
                            break;
                        case FileManager.AUDIO:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_music);
                            break;
                        case FileManager.OTHERS:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_default);
                            break;
                        case FileManager.TXT:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_txt);
                            break;
                        case FileManager.PDF:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_pdf);
                            break;
                        case FileManager.DOC:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_doc);
                            break;
                        case FileManager.ZIP:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_zip);
                            break;
                        default:
                            icon = APIUtil.getDrawable(act, R.drawable.file_type_default);
                            break;
                    }

                    if (icon != null) {
                        filePath.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                    }
                }
            }
        }

        mTvApplyToAll = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_apply_to_all);
        if (mTvApplyToAll != null) {
            mTvApplyToAll.getPaint().setAntiAlias(true);
            if (isSingle) {
                mTvApplyToAll.setVisibility(View.GONE);
            } else {
                mTvApplyToAll.setVisibility(View.VISIBLE);
            }

            mTvApplyToAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTvApplyToAll.setSelected(!mTvApplyToAll.isSelected());
                }
            });
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(DuplicateFileDialog.this, mTvApplyToAll.isSelected());
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_duplicate_file_paste_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(DuplicateFileDialog.this, mTvApplyToAll.isSelected());
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(DuplicateFileDialog dialog, boolean isApplyToAll);

        void onCancel(DuplicateFileDialog dialog, boolean isApplyToAll);
    }
}

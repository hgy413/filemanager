package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;

import java.util.ArrayList;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class PasteFailedDialog extends FMBaseDialog {

    public PasteFailedDialog(Activity act, final ArrayList<String> failedFilePath, final PasteFailedDialog.Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_paste_failed, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_paste_failed_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_paste_failed_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
        }

        TextView desc2 = (TextView) dialogView.findViewById(R.id.tv_paste_failed_desc2);
        if (desc2 != null) {
            desc2.getPaint().setAntiAlias(true);
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_paste_failed_confirm);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(PasteFailedDialog.this, failedFilePath);
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_paste_failed_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(PasteFailedDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(PasteFailedDialog dialog, ArrayList<String> failedFile);

        void onCancel(PasteFailedDialog dialog);
    }
    
}

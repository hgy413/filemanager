package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * Created by bill wang on 2017/7/12.
 *
 */

public class DeleteFileDialog extends FMBaseDialog {

    public DeleteFileDialog(Activity act, final Listener listener) {
        super(act, true);

        View dialogView = View.inflate(act, R.layout.dialog_delete_confirm, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_delete_confirm_title);
        if (title != null) {
            title.getPaint().setAntiAlias(true);
        }

        TextView dangerTips = (TextView) dialogView.findViewById(R.id.tv_delete_confirm_danger_tips);
        if (dangerTips != null) {
            dangerTips.getPaint().setAntiAlias(true);
        }

        TextView desc = (TextView) dialogView.findViewById(R.id.tv_delete_confirm_desc);
        if (desc != null) {
            desc.getPaint().setAntiAlias(true);
        }

        TextView ok = (TextView) dialogView.findViewById(R.id.tv_delete_confirm_ok);
        if (ok != null) {
            ok.getPaint().setAntiAlias(true);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirm(DeleteFileDialog.this);
                }
            }); // 确定按钮点击事件
        }

        TextView cancel = (TextView) dialogView.findViewById(R.id.tv_delete_confirm_cancel);
        if (cancel != null) {
            cancel.getPaint().setAntiAlias(true);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancel(DeleteFileDialog.this);
                }
            }); // 取消按钮点击事件
        }

        setContentView(dialogView);
    }

    public interface Listener {
        /**
         * 点击确定按钮
         */
        void onConfirm(DeleteFileDialog dialog);

        void onCancel(DeleteFileDialog dialog);
    }
}

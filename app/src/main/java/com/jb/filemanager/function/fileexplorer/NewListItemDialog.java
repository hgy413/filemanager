package com.jb.filemanager.function.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;

/**
 * Desc:
 * Author lqf
 * Email: liqf@m15.cn
 * Date: 2017/7/17 16:57
 */

public class NewListItemDialog extends BaseDialog {
    private Context mContext;
    private TextView mTvDialogTitle;
    public OnFileTypeClickListener mFileTypeClickListener;

    public NewListItemDialog(Activity act) {
        super(act);
        mContext = act.getApplicationContext();
        initializeView();
    }

    public NewListItemDialog(Activity act, int style) {
        super(act, style);
        mContext = act.getApplicationContext();
        initializeView();
    }

    public NewListItemDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
        mContext = act.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        View view = View.inflate(mContext, R.layout.dialog_list_item, null);
        setContentView(view);
        mTvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        TextView tvTypeText = (TextView) findViewById(R.id.tv_type_text);
        TextView tvTypeAudio = (TextView) findViewById(R.id.tv_type_audio);
        TextView tvTypeVideo = (TextView) findViewById(R.id.tv_type_video);
        TextView tvTypeImage = (TextView) findViewById(R.id.tv_type_image);

        tvTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileTypeClickListener != null) {
                    mFileTypeClickListener.onTypeTextClick();
                }
                dismiss();
            }
        });

        tvTypeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileTypeClickListener != null) {
                    mFileTypeClickListener.onTypeAudioClick();
                }
                dismiss();
            }
        });

        tvTypeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileTypeClickListener != null) {
                    mFileTypeClickListener.onTypeVideoClick();
                }
                dismiss();
            }
        });

        tvTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileTypeClickListener != null) {
                    mFileTypeClickListener.onTypeImageClick();
                }
                dismiss();
            }
        });
    }

    public void setTitleText(String titleText) {
        if (mTvDialogTitle != null) {
            if (!TextUtils.isEmpty(titleText)) {
                mTvDialogTitle.setText(titleText);
            } else {
                mTvDialogTitle.setVisibility(View.GONE);
            }
        }
    }

    public void setFileTypeClickListener(OnFileTypeClickListener fileTypeClickListener) {
        mFileTypeClickListener = fileTypeClickListener;
    }

    public interface OnFileTypeClickListener {
        void onTypeTextClick();

        void onTypeAudioClick();

        void onTypeVideoClick();

        void onTypeImageClick();
    }
}

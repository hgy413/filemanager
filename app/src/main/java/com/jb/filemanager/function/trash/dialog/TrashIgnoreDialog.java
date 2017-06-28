package com.jb.filemanager.function.trash.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;
import com.jb.filemanager.util.DrawUtils;
import com.jb.filemanager.util.imageloader.IconLoader;


/**
 * Created by xiaoyu on 2017/3/1 13:46.
 */

public class TrashIgnoreDialog extends BaseDialog {

    private Context mContext;
    private OnConfirmListener mListener;
    private ImageView mIcon;
    private TextView mTvName;
    private View mBtnCancel;
    private View mBtnConfirm;

    public TrashIgnoreDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
        mContext = act.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        // set width of the dialog
        int width = mContext.getResources().getDisplayMetrics().widthPixels - DrawUtils.dip2px(32);
        setSize(width, DrawUtils.dip2px(240));
        // widgets
        View view = View.inflate(mContext, R.layout.dialog_trash_ignore, null);
        setContentView(view, new ActionBar.LayoutParams(width, DrawUtils.dip2px(240)));
        mIcon = (ImageView) view.findViewById(R.id.dialog_trash_ignore_icon);
        mTvName = (TextView) view.findViewById(R.id.dialog_trash_ignore_name);
        mBtnCancel = view.findViewById(R.id.dialog_trash_ignore_cancel_btn);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnConfirm = view.findViewById(R.id.dialog_trash_ignore_confirm_btn);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm(true);
                }
                dismiss();
            }
        });
    }

    public void setAppIcon(String pkgname) {
        if (mIcon != null) {
            IconLoader.getInstance().displayImage(pkgname, mIcon);
        }
    }
    public void setAppIcon(int appIcon) {
        if (mIcon != null) {
            mIcon.setImageResource(appIcon);
        }
    }

    public void setName(String name) {
        if (mTvName != null) {
            mTvName.setText(Html.fromHtml(mContext.getString(R.string.will_not_be_scanned, name)));
        }
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        mListener = listener;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean isConfirm);
    }
}

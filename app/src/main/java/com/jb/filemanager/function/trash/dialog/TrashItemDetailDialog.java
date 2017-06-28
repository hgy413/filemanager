package com.jb.filemanager.function.trash.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jb.filemanager.R;
import com.jb.filemanager.ui.dialog.BaseDialog;
import com.jb.filemanager.util.DrawUtils;


/**
 * Created by xiaoyu on 2017/3/1 18:02.
 */

public class TrashItemDetailDialog extends BaseDialog {

    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvMessage1;
    private TextView mTvMessage2;
    private TextView mTvMessage3;
    private TextView mBtnCancel;
    private TextView mBtnView;

    public TrashItemDetailDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
        mContext = act.getApplicationContext();
        initializeView();
    }

    private void initializeView() {
        int width = mContext.getResources().getDisplayMetrics().widthPixels - DrawUtils.dip2px(32);
        setSize(width, DrawUtils.dip2px(240));
        View view = View.inflate(mContext, R.layout.dialog_item_detail, null);
        setContentView(view, new ViewGroup.LayoutParams(width, DrawUtils.dip2px(240)));

        mTvTitle = (TextView) view.findViewById(R.id.dialog_item_detail_title);
        mTvMessage1 = (TextView) view.findViewById(R.id.dialog_item_detail_message1);
        mTvMessage2 = (TextView) view.findViewById(R.id.dialog_item_detail_message2);
        mTvMessage3 = (TextView) view.findViewById(R.id.dialog_item_detail_message3);
        mBtnCancel = (TextView) view.findViewById(R.id.dialog_item_detail_btn_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnView = (TextView) view.findViewById(R.id.dialog_item_detail_btn_view);
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm(true);
                }
                dismiss();
            }
        });
    }

    public void setTitleText(String titleText) {
        if (mTvTitle != null) {
            if (!TextUtils.isEmpty(titleText)) {
                mTvTitle.setText(titleText);
            } else {
                mTvTitle.setVisibility(View.GONE);
            }
        }
    }

    public void setMessage1(String size) {
        if (mTvMessage1 != null) {
            if (!TextUtils.isEmpty(size)) {
                mTvMessage1.setText(size);
            } else {
                mTvMessage1.setVisibility(View.GONE);
            }
        }
    }

    public void setMessage2(String contain) {
        if (mTvMessage2 != null) {
            if (!TextUtils.isEmpty(contain)) {
                mTvMessage2.setText(contain);
            } else {
                mTvMessage2.setVisibility(View.GONE);
            }
        }
    }

    public void setMessage3(String path) {
        if (mTvMessage3 != null) {
            if (!TextUtils.isEmpty(path)) {
                mTvMessage3.setText(path);
            } else {
                mTvMessage3.setVisibility(View.GONE);
            }
        }
    }

    public void setOkText(int id) {
        if (mBtnView != null) {
            mBtnView.setText(id);
        }
    }

    private TrashIgnoreDialog.OnConfirmListener mListener;

    public void setOnConfirmListener(TrashIgnoreDialog.OnConfirmListener listener) {
        mListener = listener;
    }
}

package com.jb.filemanager.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jb.filemanager.R;

/**
 * 确定弹窗的公共类<br>
 * 封装了标题栏，确定，取消按钮，高度，监听事件
 *
 * @author chenbenbin
 */
public abstract class ConfirmCommonDialog extends BaseDialog implements
        DialogInterface.OnDismissListener, View.OnClickListener {
    /**
     * 是否确认操作
     */
    protected boolean mIsConfirm = false;

    protected OnConfirmListener mOnConfirmListener;
    protected OnConfirmDetailListener mConfirmDetailListener;

    protected TextView mTitle;
    protected TextView mOk;
    protected TextView mCancel;
    protected RelativeLayout mContentLayout;
    protected int mHeight;

    /**
     * 构造函数<br>
     */
    public ConfirmCommonDialog(Activity act) {
        super(act);
        init(act);
    }

    public ConfirmCommonDialog(Activity act, boolean cancelOutside) {
        super(act, cancelOutside);
        init(act);
    }

    protected void init(Activity act) {
        setContentView(R.layout.dialog_common_confirm_layout);

        mHeight = (int) act.getResources().getDimension(
                R.dimen.dialog_common_height);
        mTitle = (TextView) findViewById(R.id.confirm_common_dialog_title);
        mOk = (TextView) findViewById(R.id.confirm_common_dialog_confirm);
        mCancel = (TextView) findViewById(R.id.confirm_common_dialog_cancel);
        mContentLayout = (RelativeLayout) findViewById(R.id.confirm_common_dialog_contentview);

        initCustomLayout(mContentLayout);
        setOnDismissListener(this);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        setCancelText(R.string.common_cancel);
    }

    RelativeLayout getContentLayout() {
        return mContentLayout;
    }

    /**
     * 自定义对话框的内容 <br>
     * 必须使用LayoutInflater.from(mActivity).inflate( R.layout.layout1,
     * contentLayout, true);的方式来加载View
     */
    protected abstract void initCustomLayout(RelativeLayout contentLayout);

    /**
     * 设置标题文本
     */
    public void setTitleText(String text) {
        mTitle.setText(text);
    }

    /**
     * 是否只显示一个确定按钮
     */
    public void setSingleButton() {
        mCancel.setVisibility(View.GONE);
    }

    /**
     * 设置标题的文本的背景颜色
     */
    public void setTitleTextBackgroundColor(int color) {
        mTitle.setBackgroundColor(color);
    }

    /**
     * 设置标题字体颜色
     */
    public void setTitleTextColor(int color) {
        mTitle.setTextColor(color);
    }

    /**
     * 设置标题文本
     */
    public void setTitleText(int resId) {
        mTitle.setText(getString(resId));
    }

    /**
     * 设置OK按钮的文本
     */
    public void setOkText(String text) {
        mOk.setText(text);
    }

    public String getOkText() {
        return mOk.getText().toString();
    }

    /**
     * 设置OK按钮的文本
     */
    public void setOkText(int resId) {
        if(mOk != null){
            mOk.setText(getString(resId));
        }
    }

    /**
     * 设置OK按钮的文本颜色
     */
    public void setOkTextColor(int color) {
        mOk.setTextColor(color);
    }

    /**
     * 设置cancle按钮的文本
     */
    public void setCancelText(String text) {
        mCancel.setText(text);
    }

    public String getCancelText() {
        return mCancel.getText().toString();
    }

    /**
     * 设置cancle按钮的文本
     */
    public void setCancelText(int resId) {
        if(mCancel != null){
            mCancel.setText(getString(resId));
        }
    }

    /**
     * 设置OK按钮的文本颜色
     */
    public void setCancelTextColor(int color) {
        mCancel.setTextColor(color);
    }

    /**
     * 设置窗口高度
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * 获取窗口高度
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * 设置监听用户选择
     */
    public void setOnConfirmListener(OnConfirmListener l) {
        mOnConfirmListener = l;
    }

    /**
     * 设置详情监听事件：监听每个按钮的点击
     */
    public void setOnConfirmDetailListener(OnConfirmDetailListener listener) {
        mConfirmDetailListener = listener;
    }

    /**
     * 显示窗口
     */
    public void showDialog() {
        setSize(WindowManager.LayoutParams.MATCH_PARENT, mHeight);
        show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnConfirmListener != null) {
            mOnConfirmListener.onConfirm(mIsConfirm);
        }
        // 重置标志位：避免点击确认后，再次显示复用的弹窗，再按返回键，显示为确认
        mIsConfirm = false;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mOk)) {
            mIsConfirm = true;
            if (mConfirmDetailListener != null) {
                mConfirmDetailListener.onConfirm();
            }
            dismiss();
        } else if (v.equals(mCancel)) {
            mIsConfirm = false;
            if (mConfirmDetailListener != null) {
                mConfirmDetailListener.onCancel();
            }
            dismiss();
        }
    }

    /**
     * 回调接口：返回boolean值<br>
     * 返回键视为点击Cancel按钮
     *
     * @author laojiale
     */
    public interface OnConfirmListener {
        /**
         * 当用户作出选择时回调<br>
         *
         * @param isConfirm 是否确认了
         */
        void onConfirm(boolean isConfirm);
    }

    /**
     * 回调接口：监听每个按钮的点击(确定，取消，返回键)
     *
     * @author chenbenbin
     */
    public interface OnConfirmDetailListener {
        /**
         * 点击确定按钮
         */
        void onConfirm();

        /**
         * 点击取消按钮
         */
        void onCancel();

        /**
         * 点击返回键
         */
        void onBackPress();
    }

    public void setCancelGone() {
        mCancel.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mConfirmDetailListener != null) {
            mConfirmDetailListener.onBackPress();
        }
    }

}